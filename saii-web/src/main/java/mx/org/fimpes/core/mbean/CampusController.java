package mx.org.fimpes.core.mbean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.commons.DefaultsMB.PersistAction;
import mx.org.fimpes.saii.ejb.core.CampusFacade;
import mx.org.fimpes.saii.ejb.core.EstadosFacade;
import mx.org.fimpes.saii.ejb.core.StatusFimpesFacade;
import mx.org.fimpes.saii.model.core.Campus;
import mx.org.fimpes.saii.model.core.Estados;
import mx.org.fimpes.saii.model.core.StatusFimpes;
import mx.org.fimpes.security.Permissions;
import mx.org.fimpes.security.mbean.LoginMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("campusController")
@SessionScoped
public class CampusController implements Permissions<Campus>, Serializable {
	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CampusController.class);
    private Campus selected;
    private List<Campus> filteredItems;
    private List<Campus> activeOnes, all;

    @Resource
    UserTransaction utx;
    @EJB
    private CampusFacade campusFacade;
    // Relaciones con otras entidades:
    @EJB
    private StatusFimpesFacade statusFacade;
    @EJB
    private EstadosFacade estadosFacade;
    @Inject
    LoginMB loginMB;
    @Inject
    InstitucionesController institucionesController;

    public CampusController() {
    }

    @PostConstruct
    public void init() {
    }
    
    @Override
    public boolean isAbleToEdit(){
        return loginMB.isFimpesRol();
    }

    public void prepareCreate() {
        selected = new Campus();
    }

    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    campusFacade.create(selected);
                    msg = msg.concat("Creó el Campus ");
                    break;
                case UPDATE:
                    campusFacade.edit(selected);
                    msg = msg.concat("Actualizó el Campus ");
                    break;
                case DELETE:
                    campusFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Campus ");
                    break;
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getNombre());
            loginMB.createEventLog(msg, selected);
            utx.commit();

            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
            selected = null;
        } catch (IllegalStateException | SecurityException |  NotSupportedException | HeuristicMixedException | HeuristicRollbackException | RollbackException | SystemException  e) { 
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
    	if(institucionesController.isCampusCompleted(selected.getIdInstitucion())) {
    		PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
    		Context.sendWarn(null, "Ya registró el total de campus que especificó para la Institución", "Si desea agregar otro Campus, primero actualice el valor de dicho campo");
    	}else{
    		persist(PersistAction.CREATE);
    	}
    }
    
    public void update() {
        persist(PersistAction.UPDATE);
    }
    
    public void destroy() {
        persist(PersistAction.DELETE);
    }
    
    public List<Campus> getAll() {
    	if (loginMB.isFimpesRol()) {
            all = campusFacade.findAll();
        } else {
            all = campusFacade.findBy(loginMB.getAuthenticatedUser().getIdInstitucion());
        }
    	
        return all;
    }

    public List<Campus> getActiveOnes() {
    	if (loginMB.isFimpesRol()) {
            activeOnes = campusFacade.findActiveOnes();
        } else {
            activeOnes = campusFacade.findActiveOnesBy(loginMB.getAuthenticatedUser().getIdInstitucion());
        }
    	
        return activeOnes;
    }

    public Campus getById(Integer id) {
        return campusFacade.find(id);
    }

    public Campus getSelected() {
        return selected;
    }

    public void setSelected(Campus selected) {
        this.selected = selected;
    }

    public List<Campus> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Campus> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public List<StatusFimpes> getStatus() {
        return statusFacade.findAll();
    }

    public StatusFimpes getStatusById(Integer id) {
        return statusFacade.find(id);
    }

    public List<Estados> getEstados() {
        return estadosFacade.findAll();
    }

    public Estados getEstadoById(Integer id) {
        return estadosFacade.find(id);
    }

    @FacesConverter(forClass = Campus.class)
    public static class CampusControllerConverter implements Converter<Campus> {

        @Override
        public Campus getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (CampusController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "campusController");
            return controller.getById(getKey(value));
        }

        Integer getKey(String value) {
            var key = Integer.parseInt(value);
            return key;
        }

        String getStringKey(Integer value) {
            return value.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Campus object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdCampus());
        }
    }
}
