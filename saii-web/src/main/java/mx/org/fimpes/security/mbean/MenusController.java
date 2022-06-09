package mx.org.fimpes.security.mbean;

import java.io.Serializable;
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
import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.commons.DefaultsMB.PersistAction;
import mx.org.fimpes.saii.ejb.security.MenusFacade;
import mx.org.fimpes.saii.model.security.Menus;
import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("menusController")
@SessionScoped
public class MenusController implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MenusController.class);
    private List<Menus> items = null;
    private Menus selected;
    
    @Resource
    UserTransaction utx;
    @EJB
    private MenusFacade menusFacade;
    @Inject
    LoginMB loginMB;

    public MenusController() {
    }

    public void prepareCreate() {
        selected = new Menus();
    }
    
    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    menusFacade.create(selected);
                    msg = msg.concat("Creó el Menú ");
                    break;
                case UPDATE:
                    menusFacade.edit(selected);
                    msg = msg.concat("Actualizó el Menú ");
                    break;
                case DELETE:
                    menusFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Menú ");
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getEtiquetaMenu());
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
    
    public List<Menus> getItems() {
        items = menusFacade.findAll();
        return items;
    }
    
    public List<Menus> getActiveOnes() {
        return menusFacade.findActiveOnes();
    }

    public Menus getMenus(Integer id) {
        return menusFacade.find(id);
    }
    
    public Menus getSelected() {
        return selected;
    }

    public void setSelected(Menus selected) {
        this.selected = selected;
    }

    @FacesConverter(forClass = Menus.class, value = "menuConverter")
    public static class MenusControllerConverter implements Converter<Menus> {

        @Override
        public Menus getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (MenusController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "menusController");
            return controller.getMenus(getKey(value));
        }

        Integer getKey(String value) {
            var key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            var sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Menus object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdMenu());
        }
    }
}
