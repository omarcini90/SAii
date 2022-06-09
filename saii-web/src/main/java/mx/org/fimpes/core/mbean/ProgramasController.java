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
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.commons.DefaultsMB.PersistAction;
import mx.org.fimpes.saii.ejb.core.AcreditadorasFacade;
import mx.org.fimpes.saii.ejb.core.AreaConocimientoFacade;
import mx.org.fimpes.saii.ejb.core.EnfoqueFacade;
import mx.org.fimpes.saii.ejb.core.ModalidadFacade;
import mx.org.fimpes.saii.ejb.core.NivelAcademicoFacade;
import mx.org.fimpes.saii.ejb.core.ProgramasFacade;
import mx.org.fimpes.saii.ejb.core.TemporalidadesFacade;
import mx.org.fimpes.saii.ejb.core.TipoRvoeFacade;
import mx.org.fimpes.saii.model.core.Acreditadoras;
import mx.org.fimpes.saii.model.core.AreaConocimiento;
import mx.org.fimpes.saii.model.core.Enfoque;
import mx.org.fimpes.saii.model.core.Modalidad;
import mx.org.fimpes.saii.model.core.NivelAcademico;
import mx.org.fimpes.saii.model.core.Programas;
import mx.org.fimpes.saii.model.core.Temporalidades;
import mx.org.fimpes.saii.model.core.TipoRvoe;
import mx.org.fimpes.security.mbean.LoginMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("programasController")
@SessionScoped
public class ProgramasController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ProgramasController.class);
    private Programas selected;
    private List<Programas> filteredItems;
    private List<Programas> activeOnes, all;
    private DualListModel<Acreditadoras> acreditadoras;

    @Resource
    UserTransaction utx;
    @EJB
    private ProgramasFacade programasFacade;
    // Relaciones con otras entidades:
    @EJB
    private ModalidadFacade modalidadFacade;
    @EJB
    private TemporalidadesFacade temporalidadesFacade;
    @EJB
    private NivelAcademicoFacade nivelAcademicoFacade;
    @EJB
    private TipoRvoeFacade tipoRvoeFacade;
    @EJB
    private AreaConocimientoFacade areaConocimientoFacade;
    @EJB
    private EnfoqueFacade enfoqueFacade;
    @EJB
    private AcreditadorasFacade acreditadoraFacade;
    @Inject
    LoginMB loginMB;
    @Inject
    InstitucionesController institucionesController;

    public ProgramasController() {
    }

    @PostConstruct
    public void init() {
    }

    public void prepareCreate() {
        selected = new Programas();
    }

    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    programasFacade.create(selected);
                    msg = msg.concat("Creó el Programa ");
                    break;
                case UPDATE:
                    programasFacade.edit(selected);
                    msg = msg.concat("Actualizó el Programa ");
                    break;
                case DELETE:
                    programasFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Programa ");
                    break;
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getNombre());
            loginMB.createEventLog(msg, selected);
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
    	if(institucionesController.isProgramCompleted(selected.getIdCampus().getIdInstitucion())) {
    		PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
    		Context.sendWarn(null, "Ya registró el total de programas que especificó para la Institución", "Si desea agregar otro Programa, primero actualice el valor de dicho campo");
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
    
    public void showCertifiers(Programas p) {
    	if(p != null){
        	selected = p;
    	}
    	List<Acreditadoras> target = selected.getAcreditadorasList();
    	List<Acreditadoras> source = acreditadoraFacade.findAvailableOnesForProgram(selected.getIdPrograma());
    	acreditadoras = new DualListModel<>(source, target);
    }
    
    public void onCertifiersTransfer(TransferEvent event) {
    	var msg = "Actualizó las acreditadoras del Programa ";
        try {
            utx.begin();
            ((List<Acreditadoras>) event.getItems()).forEach((a) -> {
                if (event.isAdd()) {
                	a.getProgramasList().add(selected);
                	selected.getAcreditadorasList().add(a);                	
                } else {
                	a.getProgramasList().remove(selected);
                	selected.getAcreditadorasList().remove(a);
                }
                acreditadoraFacade.edit(a);
            });
            msg = msg.concat(selected.getClavePlan());
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

    public List<Programas> getAll() {
    	if (loginMB.isFimpesRol()) {
            all = programasFacade.findAll();
        } else {
            log.info("1. LISTADO DE PROGRAMAS POR INSITUCIÓN: ");
            all = programasFacade.findBy(loginMB.getAuthenticatedUser().getIdInstitucion());
        }
    	
        return all;
    }

    public List<Programas> getActiveOnes() {
    	if (loginMB.isFimpesRol()) {
            activeOnes = programasFacade.findActiveOnes();
        } else {
               log.info("2. LISTADO DE PROGRAMAS POR INSITUCIÓN: ");
            activeOnes = programasFacade.findActiveOnesBy(loginMB.getAuthenticatedUser().getIdInstitucion());
        }
    	
        return activeOnes;
    }

    public Programas getById(Integer id) {
        return programasFacade.find(id);
    }

    public Programas getSelected() {
        return selected;
    }

    public void setSelected(Programas selected) {
        this.selected = selected;
    }

    public List<Programas> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Programas> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public List<Modalidad> getModalidades() {
        return modalidadFacade.findAll();
    }

    public Modalidad getModalidadById(Integer id) {
        return modalidadFacade.find(id);
    }

    public List<Temporalidades> getTemporalidades() {
        return temporalidadesFacade.findAll();
    }

    public Temporalidades getTemporalidadById(Integer id) {
        return temporalidadesFacade.find(id);
    }

    public List<NivelAcademico> getNivelesAcademicos() {
        return nivelAcademicoFacade.findAll();
    }

    public NivelAcademico getNivelAcademicoById(Integer id) {
        return nivelAcademicoFacade.find(id);
    }

    public List<TipoRvoe> getTiposRvoe() {
        return tipoRvoeFacade.findAll();
    }

    public TipoRvoe getTipoRvoeById(Integer id) {
        return tipoRvoeFacade.find(id);
    }

    public List<AreaConocimiento> getAreasConocimiento() {
        return areaConocimientoFacade.findAll();
    }

    public AreaConocimiento getAreaConocimientoById(Integer id) {
        return areaConocimientoFacade.find(id);
    }

    public List<Enfoque> getEnfoques() {
        return enfoqueFacade.findAll();
    }

    public Enfoque getEnfoqueById(Integer id) {
        return enfoqueFacade.find(id);
    }
    
    public DualListModel<Acreditadoras> getAcreditadoras() {
		return acreditadoras;
	}

	public void setAcreditadoras(DualListModel<Acreditadoras> acreditadoras) {
		this.acreditadoras = acreditadoras;
	}

    @FacesConverter(forClass = Programas.class)
    public static class ProgramasControllerConverter implements Converter<Programas> {

        @Override
        public Programas getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (ProgramasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "programasController");
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
        public String getAsString(FacesContext facesContext, UIComponent component, Programas object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdPrograma());
        }
    }
}
