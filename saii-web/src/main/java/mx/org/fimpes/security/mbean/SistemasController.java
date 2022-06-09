package mx.org.fimpes.security.mbean;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.transaction.UserTransaction;
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
import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.commons.DefaultsMB.PersistAction;
import mx.org.fimpes.saii.ejb.security.SistemasFacade;
import mx.org.fimpes.saii.model.security.Sistemas;
import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("sistemasController")
@SessionScoped
public class SistemasController implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SistemasController.class);
    private List<Sistemas> items = null;
    private Sistemas selected;
    
    @Resource
    UserTransaction utx;
    @EJB
    private SistemasFacade sistemasFacade;
    @Inject
    LoginMB loginMB;

    public SistemasController() {
    }

    public void prepareCreate() {
        selected = new Sistemas();
    }

    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    sistemasFacade.create(selected);
                    msg = msg.concat("Creó el Sistema ");
                    break;
                case UPDATE:
                    sistemasFacade.edit(selected);
                    msg = msg.concat("Actualizó el Sistema ");
                    break;
                case DELETE:
                    sistemasFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Sistema ");
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getAcronimo());
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

    public List<Sistemas> getItems() {
        items = sistemasFacade.findAll();
        return items;
    }
    
    public Sistemas getSistemas(Integer id) {
        return sistemasFacade.find(id);
    }
    
    public Sistemas getSelected() {
        return selected;
    }

    public void setSelected(Sistemas selected) {
        this.selected = selected;
    }

    @FacesConverter(forClass = Sistemas.class)
    public static class SistemasControllerConverter implements Converter<Sistemas> {

        @Override
        public Sistemas getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (SistemasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sistemasController");
            return controller.getSistemas(getKey(value));
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
        public String getAsString(FacesContext facesContext, UIComponent component, Sistemas object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdSistema());
        }
    }
}
