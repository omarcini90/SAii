package mx.org.fimpes.core.mbean;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.commons.DefaultsMB.PersistAction;
import mx.org.fimpes.saii.ejb.core.AcreditadorasFacade;
import mx.org.fimpes.saii.ejb.core.AfiliacionesFacade;
import mx.org.fimpes.saii.ejb.core.InstitucionesFacade;
import mx.org.fimpes.saii.ejb.core.StatusFimpesFacade;
import mx.org.fimpes.saii.ejb.core.TemporalidadesFacade;
import mx.org.fimpes.saii.model.core.Acreditadoras;
import mx.org.fimpes.saii.model.core.Afiliaciones;
import mx.org.fimpes.saii.model.core.Campus;
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.model.core.StatusFimpes;
import mx.org.fimpes.saii.model.core.Temporalidades;
import mx.org.fimpes.security.Permissions;
import mx.org.fimpes.security.mbean.LoginMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("institucionesController")
@SessionScoped
public class InstitucionesController implements Permissions<Instituciones>, Serializable {
	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(InstitucionesController.class);
    private Instituciones selected;
    private List<Instituciones> filteredItems;
    private List<Instituciones> activeOnes, all;
    private DualListModel<Temporalidades> temporalidades;
    private DualListModel<Afiliaciones> afiliaciones;
    private DualListModel<Acreditadoras> acreditadoras;

    @Resource
    UserTransaction utx;
    @EJB
    private InstitucionesFacade institucionesFacade;
    // Relaciones con otras entidades:
    @EJB
    private TemporalidadesFacade temporalidadesFacade;
    @EJB
    private AfiliacionesFacade afiliacionesFacade;
    @EJB
    private AcreditadorasFacade acreditadoraFacade;
    @EJB
    private StatusFimpesFacade statusFacade;
    @Inject
    LoginMB loginMB;

    public InstitucionesController() {
    }
    
    @PostConstruct
    public void init() {
    }
    
    @Override
    public boolean isAbleToEdit(){
        return loginMB.isFimpesRol();
    }
    
    public boolean isCampusCompleted(Instituciones i) {
    	// Se actualiza el estado de la institución
    	i = getById(i.getIdInstitucion());
		short total = i.getTotalCampus();

      
		int current = i.getCampusList().size();

		return (current >= total);
	}

	public boolean isProgramCompleted(Instituciones i) {
		// Se actualiza el estado de la institución
		i = getById(i.getIdInstitucion());
		short total = i.getTotalProgramas();
		int current = 0;
		for (Campus c : i.getCampusList()) {
			current += c.getProgramasList().size();
		}

		return (current >= total);
	}

    public void prepareCreate() {
        selected = new Instituciones();
    }

    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    institucionesFacade.create(selected);
                    msg = msg.concat("Creó el Instituto ");
                    break;
                case UPDATE:
                    institucionesFacade.edit(selected);
                    msg = msg.concat("Actualizó el Instituto ");
                    break;
                case DELETE:
                    institucionesFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Instituto ");
                    break;
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getNombre());
            loginMB.createEventLog(msg,selected);
            utx.commit();

            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
            selected = null;
        } catch (IllegalStateException | SecurityException |  NotSupportedException | HeuristicMixedException | HeuristicRollbackException | RollbackException | SystemException e) { 
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
    
    public void showTemporalities(Instituciones i) {
    	if(i != null){
            selected = i;
        }
        List<Temporalidades> target = selected.getTemporalidadesList();
        List<Temporalidades> source = temporalidadesFacade.findAvailableOnesForInstitute(selected.getIdInstitucion());
        temporalidades = new DualListModel<>(source, target);
    }
    
    public void onTemporalitiesTransfer(TransferEvent event) {
    	var msg = "Actualizó las temporalidades del Instituto ";
        try {
            utx.begin();
            ((List<Temporalidades>) event.getItems()).forEach((t) -> {
                if (event.isAdd()) {
                	t.getInstitucionesList().add(selected);
                	selected.getTemporalidadesList().add(t);                	
                } else {
                	t.getInstitucionesList().remove(selected);
                	selected.getTemporalidadesList().remove(t);
                }
                institucionesFacade.edit(selected);
            });
            msg = msg.concat(selected.getSiglas());
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
    
    public void showAffiliates(Instituciones i) {
    	if(i != null){
            selected = i;
        }
        List<Afiliaciones> target = selected.getAfiliacionesList();
        List<Afiliaciones> source = afiliacionesFacade.findAvailableOnesForInstitute(selected.getIdInstitucion());
        afiliaciones = new DualListModel<>(source, target);
    }
    
    public void onAffiliatesTransfer(TransferEvent event) {
    	var msg = "Actualizó las afiliaciones del Instituto ";
        try {
            utx.begin();
            ((List<Afiliaciones>) event.getItems()).forEach((a) -> {
                if (event.isAdd()) {
                	a.getInstitucionesList().add(selected);
                	selected.getAfiliacionesList().add(a);                	
                } else {
                	a.getInstitucionesList().remove(selected);
                	selected.getAfiliacionesList().remove(a);
                }
                institucionesFacade.edit(selected);
            });
            msg = msg.concat(selected.getSiglas());
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

    public void showCertifiers(Instituciones i) {
    	if(i != null){
        	selected = i;
    	}
    	List<Acreditadoras> target = selected.getAcreditadorasList();
    	List<Acreditadoras> source = acreditadoraFacade.findAvailableOnesForInstitute(selected.getIdInstitucion());
    	acreditadoras = new DualListModel<>(source, target);
    }
    
    public void onCertifiersTransfer(TransferEvent event) {
    	var msg = "Actualizó las acreditadoras del Instituto ";
        try {
            utx.begin();
            ((List<Acreditadoras>) event.getItems()).forEach((a) -> {
                if (event.isAdd()) {
                	a.getInstitucionesList().add(selected);
                	selected.getAcreditadorasList().add(a);                	
                } else {
                	a.getInstitucionesList().remove(selected);
                	selected.getAcreditadorasList().remove(a);
                }
                acreditadoraFacade.edit(a);
            });
            msg = msg.concat(selected.getSiglas());
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
    
    public List<Instituciones> getAll() {
    	if(loginMB.isFimpesRol()){
            all = institucionesFacade.findAll();
        }else{
            all = new ArrayList<>(1);
            all.add(loginMB.getAuthenticatedUser().getIdInstitucion());
        }
    	
    	return all;
    }
    
    public List<Instituciones> getActiveOnes() {
    	if(loginMB.isFimpesRol()){
            activeOnes = institucionesFacade.findActiveOnes();
        }else{
            activeOnes = new ArrayList<>(1);
            activeOnes.add(loginMB.getAuthenticatedUser().getIdInstitucion());
        }
    	
        return activeOnes;
    }

    public Instituciones getById(Integer id) {
        return institucionesFacade.find(id);
    }

    public Instituciones getSelected() {
        return selected;
    }

    public void setSelected(Instituciones selected) {
        this.selected = selected;
    }

    public List<Instituciones> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Instituciones> filteredItems) {
        this.filteredItems = filteredItems;
    }
    
    public List<StatusFimpes> getStatus() {
        return statusFacade.findAll();
    }
    
    public Afiliaciones getAfiliadoraById(Integer id) {
        return afiliacionesFacade.find(id);
    }
    
    public Acreditadoras getAcreditadoraById(Integer id) {
        return acreditadoraFacade.find(id);
    }
    
    public DualListModel<Temporalidades> getTemporalidades() {
		return temporalidades;
	}

	public void setTemporalidades(DualListModel<Temporalidades> temporalidades) {
		this.temporalidades = temporalidades;
	}

	public DualListModel<Afiliaciones> getAfiliaciones() {
		return afiliaciones;
	}

	public void setAfiliaciones(DualListModel<Afiliaciones> afiliadoras) {
		this.afiliaciones = afiliadoras;
	}

	public DualListModel<Acreditadoras> getAcreditadoras() {
		return acreditadoras;
	}

	public void setAcreditadoras(DualListModel<Acreditadoras> acreditadoras) {
		this.acreditadoras = acreditadoras;
	}

	@FacesConverter(forClass = Instituciones.class)
    public static class InstitucionesControllerConverter implements Converter<Instituciones> {

        @Override
        public Instituciones getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (InstitucionesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "institucionesController");
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
        public String getAsString(FacesContext facesContext, UIComponent component, Instituciones object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdInstitucion());
        }
    }
}
