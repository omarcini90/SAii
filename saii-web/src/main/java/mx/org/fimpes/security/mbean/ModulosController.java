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
import mx.org.fimpes.saii.ejb.security.ModulosFacade;
import mx.org.fimpes.saii.model.security.Modulos;
import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("modulosController")
@SessionScoped
public class ModulosController implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ModulosController.class);
    private List<Modulos> items = null;
    private Modulos selected;
    
    @Resource
    UserTransaction utx;
    @EJB
    private ModulosFacade modulosFacade;
    @Inject
    LoginMB loginMB;

    public ModulosController() {
    }

    public void prepareCreate() {
        selected = new Modulos();
    }

    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    modulosFacade.create(selected);
                    msg = msg.concat("Creó el Módulo ");
                    break;
                case UPDATE:
                    modulosFacade.edit(selected);
                    msg = msg.concat("Actualizó el Módulo ");
                    break;
                case DELETE:
                    modulosFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Módulo ");
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

    public List<Modulos> getItems() {
        items = modulosFacade.findAll();
        return items;
    }

    public Modulos getModulos(Integer id) {
        return modulosFacade.find(id);
    }
    
    public Modulos getSelected() {
        return selected;
    }

    public void setSelected(Modulos selected) {
        this.selected = selected;
    }

    @FacesConverter(forClass = Modulos.class)
    public static class ModulosControllerConverter implements Converter<Modulos> {

        @Override
        public Modulos getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (ModulosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "modulosController");
            return controller.getModulos(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            var key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            var sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Modulos object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdModulo());
        }
    }
}
