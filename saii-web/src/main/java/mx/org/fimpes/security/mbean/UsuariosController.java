package mx.org.fimpes.security.mbean;

import java.io.Serializable;
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
import mx.org.fimpes.saii.ejb.security.UsuariosFacade;
import mx.org.fimpes.saii.model.security.Usuarios;
import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("usuariosController")
@SessionScoped
public class UsuariosController implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(UsuariosController.class);
    private List<Usuarios> items = null;
    private Usuarios selected;
    private List<Usuarios> filteredItems;
    private List<Usuarios> activeOnes;
    
    @Resource
    UserTransaction utx;
    @EJB
    private UsuariosFacade usuariosFacade;
    @Inject
    LoginMB loginMB;

    public UsuariosController() {
    }
    
    @PostConstruct
    public void init(){
    }

    public void prepareCreate() {
        selected = new Usuarios();
    }

    private void persist(PersistAction action) {
    	var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    usuariosFacade.create(selected);
                    msg = msg.concat("Creó el Usuario ");
                    break;
                case UPDATE:
                    usuariosFacade.edit(selected);
                    msg = msg.concat("Actualizó el Usuario ");
                    break;
                case DELETE:
                    usuariosFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Usuario ");
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getNombreCompleto());
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
    
    public List<Usuarios> getItems() {
    	if (loginMB.isFimpesRol()) {
    		items = usuariosFacade.findAll();
    	}else {
    		items = usuariosFacade.findByInstitucion(loginMB.getAuthenticatedUser().getIdInstitucion().getIdInstitucion());
    	}
        
        return items;
    }
    
    public List<Usuarios> getActiveOnes() {
    	if(loginMB.isFimpesRol()){
            activeOnes = usuariosFacade.findActiveOnes();
        }else{
            activeOnes = usuariosFacade.findActiveOnesBy(loginMB.getAuthenticatedUser().getIdInstitucion());
        }
    	
        return activeOnes;
    }

    public Usuarios getUsuarios(Integer id) {
        return usuariosFacade.find(id);
    }
    
    public Usuarios getSelected() {
        return selected;
    }

    public void setSelected(Usuarios selected) {
        this.selected = selected;
    }

    public List<Usuarios> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Usuarios> filteredItems) {
        this.filteredItems = filteredItems;
    }

    @FacesConverter(forClass = Usuarios.class)
    public static class UsuariosControllerConverter implements Converter<Usuarios> {

        @Override
        public Usuarios getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (UsuariosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuariosController");
            return controller.getUsuarios(getKey(value));
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
        public String getAsString(FacesContext facesContext, UIComponent component, Usuarios object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdUsuario());
        }
    }
}
