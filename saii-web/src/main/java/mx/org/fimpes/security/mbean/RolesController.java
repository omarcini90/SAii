package mx.org.fimpes.security.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import mx.org.fimpes.saii.ejb.security.CredencialesFacade;
import mx.org.fimpes.saii.ejb.security.MenusFacade;
import mx.org.fimpes.saii.ejb.security.RolesFacade;
import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.commons.DefaultsMB.PersistAction;
import mx.org.fimpes.saii.model.security.Roles;
import mx.org.fimpes.saii.model.security.Credenciales;
import mx.org.fimpes.saii.model.security.CredencialesHasMenu;
import mx.org.fimpes.saii.model.security.CredencialesHasMenuPK;
import mx.org.fimpes.saii.model.security.Menus;
import mx.org.fimpes.security.Permissions;
import org.apache.log4j.Logger;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("rolesController")
@SessionScoped
public class RolesController implements Permissions<Roles>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(RolesController.class);
    private static final List<String> SYSTEM_ROLES = new ArrayList<String>(Arrays.asList("ADMIN_FIMPES","SUPERVISOR_FIMPES","ADMIN_INSTITUTO","SUPERVISOR_INSTITUTO","COLABORADOR_INSTITUTO"));
    private List<Roles> items = null;
    private Roles selected;
    private DualListModel<Menus> menus;
    
    @Resource
    UserTransaction utx;
    @EJB
    private RolesFacade rolesFacade;
    @EJB
    private MenusFacade menusFacade;
    @EJB
    private CredencialesFacade credencialesFacade;
    @Inject
    LoginMB loginMB;    

    public RolesController() {
    }
    
    @Override
    public boolean isAbleToEdit(){
        return selected != null ? isAbleToEdit(selected) : true;
    }
    
    @Override
    public boolean isAbleToEdit(Roles rol){
        String r = rol.getRol();
        return !(SYSTEM_ROLES.contains(r));
    }
    
    @Override
    public boolean isAbleToRemove(){
        return selected != null ? isAbleToRemove(selected) : false;
    }
    
    @Override
    public boolean isAbleToRemove(Roles rol){
        String r = rol.getRol();
        return !(SYSTEM_ROLES.contains(r));
    }

    public void prepareCreate() {
        selected = new Roles();
    }
    
    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    rolesFacade.create(selected);
                    msg = msg.concat("Creó el Rol ");
                    break;
                case UPDATE:
                    rolesFacade.edit(selected);
                    msg = msg.concat("Actualizó el Rol ");
                    break;
                case DELETE:
                    rolesFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Rol ");
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getRol());
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
        if(isAbleToRemove()){
            persist(DefaultsMB.PersistAction.DELETE);
        }else{
            Context.sendWarn(null, DefaultsMB.FAILURE_MESSAGE, "No es posible eliminar un Rol de Sistema");
        }
        
    }

    public void showMenus(Roles rol) {
        if (rol != null) {
            selected = rol;
        }
        List<Menus> target = selected.getMenusList();
        List<Menus> source = menusFacade.findAvailableOnesForRol(selected.getIdRol());
        menus = new DualListModel<>(source, target);
    }

    private CredencialesHasMenu createCredencialesHasMenu(Menus menu, Credenciales user) {
        var pk = new CredencialesHasMenuPK(user.getIdCredencial(), menu.getIdMenu());
        var cm = new CredencialesHasMenu(pk);
        cm.setConsultar(false);
        cm.setCrear(false);
        cm.setEditar(false);
        cm.setEliminar(false);
        cm.setImprimir(false);
        cm.setCredenciales(user);
        cm.setMenus(menu);

        return cm;
    }
    
    public void onMenuTransfer(TransferEvent event) {
        var msg = "Actualizó los menus del Rol ";
        try {
            utx.begin();
            ((List<Menus>) event.getItems()).forEach((menu) -> {

                List<Credenciales> users = selected.getCredencialesList();
                if (event.isAdd()) {
                    rolesFacade.addMenu(selected.getIdRol(), menu.getIdMenu());
                    menu.getRolesList().add(selected);
                    selected.getMenusList().add(menu);
                    //We update the users' permissions as well
                    if (!users.isEmpty()) {
                        users.forEach((u) -> {
                            var cm = createCredencialesHasMenu(menu, u);
                            credencialesFacade.addMenu(cm);
                            u.getCredencialesHasMenuList().add(cm);
                        });
                    }
                } else {
                    //We update the users' permissions as well
                    if (!users.isEmpty()) {
                        users.forEach((u) -> {
                            var roles = u.getRolesList();
                            roles.remove(selected);
                            //We check if the menu is present in the rols left
                            var present = false;
                            for (Roles rolLeft : roles) {
                                var menus = rolLeft.getMenusList();
                                if (menus.contains(menu)) {
                                    present = true;
                                    break;
                                }
                            }
                            if (!present) {
                                var pk = new CredencialesHasMenuPK(u.getIdCredencial(), menu.getIdMenu());
                                var cm = credencialesFacade.removeMenu(pk);
                                u.getCredencialesHasMenuList().remove(cm);
                            }
                        });
                    }
                    rolesFacade.removeMenu(selected.getIdRol(), menu.getIdMenu());
                    menu.getRolesList().remove(selected);
                    selected.getMenusList().remove(menu);
                }
            });
            msg = msg.concat(selected.getRol());
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
    
    public List<Roles> getItems() {
        if (items == null) {
            items = rolesFacade.findAll();
        }
        return items;
    }

    public Roles getRoles(Integer id) {
        return rolesFacade.find(id);
    }
    
    public Roles getSelected() {
        return selected;
    }

    public void setSelected(Roles selected) {
        this.selected = selected;
    }

    public DualListModel<Menus> getMenus() {
        return menus;
    }

    public void setMenus(DualListModel<Menus> menus) {
        this.menus = menus;
    }

    @FacesConverter(forClass = Roles.class, value = "rolConverter")
    public static class RolesControllerConverter implements Converter<Roles> {

        @Override
        public Roles getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (RolesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rolesController");
            return controller.getRoles(getKey(value));
        }

        Integer getKey(String value) {
            var key = Integer.parseInt(value);
            return key;
        }

        String getStringKey(Integer value) {
            return value.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Roles object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdRol());
        }
    }
}
