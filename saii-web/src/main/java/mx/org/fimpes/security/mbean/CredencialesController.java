package mx.org.fimpes.security.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.commons.DefaultsMB.PersistAction;
import mx.org.fimpes.saii.ejb.security.CredencialesFacade;
import mx.org.fimpes.saii.ejb.security.RolesFacade;
import mx.org.fimpes.saii.model.security.Credenciales;
import mx.org.fimpes.saii.model.security.CredencialesHasMenu;
import mx.org.fimpes.saii.model.security.CredencialesHasMenuPK;
import mx.org.fimpes.saii.model.security.Menus;
import mx.org.fimpes.saii.model.security.Roles;
import org.apache.log4j.Logger;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("credencialesController")
@SessionScoped
public class CredencialesController implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CredencialesController.class);
    private List<CredencialesHasMenu> permissions;
    private Credenciales selected;
    private List<Credenciales> filteredItems;
    private DualListModel<Roles> roles;
    private List<Credenciales> items;
    
    @Resource
    UserTransaction utx;
    @EJB
    private CredencialesFacade credencialesFacade;
    @EJB
    private RolesFacade rolesFacade;
    @Inject
    LoginMB loginMB;

    public CredencialesController() {
    }

    @PostConstruct
    public void init() {

    }

    public void prepareCreate() {
        selected = new Credenciales();
    }

    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    credencialesFacade.create(selected);
                    msg = msg.concat("Creó la Credencial ");
                    break;
                case UPDATE:
                    credencialesFacade.edit(selected);
                    msg = msg.concat("Actualizó la Credencial ");
                    break;
                case DELETE:
                    credencialesFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente la Credencial ");
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getUsuario());
            loginMB.createEventLog(msg,selected);
            utx.commit();
            
            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
            selected = null;
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            } finally {
                Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
            }
        }
    }

    public void create() {
        persist(DefaultsMB.PersistAction.CREATE);
    }

    public void update() {
        persist(DefaultsMB.PersistAction.UPDATE);
    }

    public void destroy() {
        persist(DefaultsMB.PersistAction.DELETE);
    }
    
    public String showUsers() {
        selected = null;
        return "usuarios";
    }

    public String showPermissions() {
        if (selected != null) {
            permissions = selected.getCredencialesHasMenuList();
            return "permisos";
        } else {
            permissions = null;
            return null;
        }
    }

    public void onRowEdit(RowEditEvent event) {
        var menu = (CredencialesHasMenu) event.getObject();
        update();
        Context.sendInfo(null, "Permisos actualizados", "Para el menu " + menu.getMenus().getEtiquetaMenu());
    }

    public void onRowCancel(RowEditEvent event) {
        var menu = (CredencialesHasMenu) event.getObject();
        Context.sendInfo(null, "Edicion cancelada", "Para el menu " + menu.getMenus().getEtiquetaMenu());
    }

    public void onCellEdit(CellEditEvent event) {
        var oldValue = event.getOldValue();
        var newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            update();
            Context.sendInfo(null, "Permiso actualizado", "Valor: " + newValue);
        }
    }

    public void showRoles(Credenciales c) {
        if(c != null){
            selected = c;
        }
        List<Roles> target = selected.getRolesList();
        List<Roles> source = rolesFacade.findAvailableOnesForUser(selected.getIdCredencial(), loginMB.isFimpesRol());
        roles = new DualListModel<>(source, target);
    }

    public void onRolTransfer(TransferEvent event) {
        var msg = "Actualizó los roles del Usuario ";
        try {
            utx.begin();
			((List<Roles>) event.getItems()).forEach((rol) -> {
                //We get the default menus for each rol
                var rolHasMenu = rol.getMenusList();

                if (event.isAdd()) {
                    //We verify if the user has already permissions
                    if (selected.getCredencialesHasMenuList().isEmpty()) {
                        rolHasMenu.forEach((menu) -> {
                            //We add the default user's permissions for every menu(view) in the rol
                            var cm = createCredencialesHasMenu(menu);
                            credencialesFacade.addMenu(cm);
                            selected.getCredencialesHasMenuList().add(cm);
                        });
                    } else {
                        //We get the current user's menus
                        var menus = new ArrayList<Menus>();
                        selected.getCredencialesHasMenuList().forEach((cm) -> {
                            menus.add(cm.getMenus());
                        });

                        rolHasMenu.forEach((menu) -> {
                            if (!menus.contains(menu)) {
                                //We add the default user's permissions left
                                var cm = createCredencialesHasMenu(menu);
                                credencialesFacade.addMenu(cm);
                                selected.getCredencialesHasMenuList().add(cm);
                            }
                        });
                    }
                    credencialesFacade.addRol(selected.getIdCredencial(), rol.getIdRol());
                    selected.getRolesList().add(rol);
                } else {
                	selected.getRolesList().remove(rol);
                    //For each selected rol's menu
                    rolHasMenu.forEach((menu) -> {
                        //We check if the menu is present in the rols left
                        var present = false;
                        for (Roles rolLeft : selected.getRolesList()) {
                            var menus = rolLeft.getMenusList();
                            if (menus.contains(menu)) {
                                present = true;
                                break;
                            }
                        }
                        if (!present) {
                            var pk = new CredencialesHasMenuPK(selected.getIdCredencial(), menu.getIdMenu());
                            var cm = credencialesFacade.removeMenu(pk);
                            selected.getCredencialesHasMenuList().remove(cm);
                        }
                    });
                    credencialesFacade.removeRol(selected.getIdCredencial(), rol.getIdRol());
                }
            });
			msg = msg.concat(selected.getUsuario());
            loginMB.createEventLog(msg, selected);
            utx.commit();
            
            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            } finally {
                Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
            }
        }
    }
    
    public List<Credenciales> getItems() {
    	if (loginMB.isFimpesRol()) {
    		items = credencialesFacade.findAll();
    	}else {
    		items = credencialesFacade.findByInstitucion(loginMB.getAuthenticatedUser().getIdInstitucion());
    	}
        
        return items;
    }

    public List<CredencialesHasMenu> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<CredencialesHasMenu> permissions) {
        this.permissions = permissions;
    }

    public Credenciales getCredenciales(Integer id) {
        return credencialesFacade.find(id);
    }
    
    public Credenciales getSelected() {
        return selected;
    }

    public void setSelected(Credenciales selected) {
        this.selected = selected;
    }
    
    public List<Credenciales> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Credenciales> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public DualListModel<Roles> getRoles() {
        return roles;
    }

    public void setRoles(DualListModel<Roles> roles) {
        this.roles = roles;
    }    

    private CredencialesHasMenu createCredencialesHasMenu(Menus menu) {
        var pk = new CredencialesHasMenuPK(selected.getIdCredencial(), menu.getIdMenu());
        var cm = new CredencialesHasMenu(pk);
        cm.setConsultar(false);
        cm.setCrear(false);
        cm.setEditar(false);
        cm.setEliminar(false);
        cm.setImprimir(false);
        cm.setCredenciales(selected);
        cm.setMenus(menu);

        return cm;
    }

    @FacesConverter(forClass = Credenciales.class)
    public static class CredencialesControllerConverter implements Converter<Credenciales> {

        @Override
        public Credenciales getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (CredencialesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "credencialesController");
            return controller.getCredenciales(getKey(value));
        }

        Integer getKey(String value) {
            var key = Integer.parseInt(value);
            return key;
        }

        String getStringKey(Integer value) {
            return value.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Credenciales object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdUsuario());
        }
    }
}
