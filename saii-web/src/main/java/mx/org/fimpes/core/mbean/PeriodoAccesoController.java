package mx.org.fimpes.core.mbean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.log4j.Logger;
import org.primefaces.component.selectonemenu.SelectOneMenu;

import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.commons.DefaultsMB.PersistAction;
import mx.org.fimpes.saii.ejb.core.CiclosFacade;
import mx.org.fimpes.saii.ejb.core.InstitucionesFacade;
import mx.org.fimpes.saii.ejb.core.PeriodoAccesoFacade;
import mx.org.fimpes.saii.ejb.core.TemporalidadesFacade;
import mx.org.fimpes.saii.model.core.Ciclos;
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.model.core.PeriodoAcceso;
import mx.org.fimpes.saii.model.core.Temporalidades;
import mx.org.fimpes.saii.util.MxDateTime;
import mx.org.fimpes.security.Permissions;
import mx.org.fimpes.security.mbean.LoginMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("periodoAccesoController")
@SessionScoped
public class PeriodoAccesoController implements Permissions<PeriodoAcceso>, Serializable {
	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PeriodoAccesoController.class);
    private final Date now;
    private Date maxDate;
    private PeriodoAcceso selected;
    private List<PeriodoAcceso> filteredItems;
    private List<Temporalidades> temporalidades;
    private Temporalidades selectedTemporalidad;
    private boolean todas;

    @Resource
    UserTransaction utx;
    @EJB
    private PeriodoAccesoFacade periodoAccesoFacade;
    // Relaciones con otras entidades:
    @EJB
    private TemporalidadesFacade temporalidadesFacade;
    @EJB
    private CiclosFacade ciclosFacade;
    @EJB
    private InstitucionesFacade institucionesFacade;
    @Inject
    LoginMB loginMB;

    public PeriodoAccesoController() {
    	now = MxDateTime.dateOnly();
    }
    
    private void resetMaxDate(Integer year) {
    	var c = Calendar.getInstance(MxDateTime.CDMX_TIMEZONE);
    	c.set(Calendar.YEAR, year < MxDateTime.now().getYear() ? MxDateTime.now().getYear() : year);   	
    	c.set(Calendar.MONTH, Calendar.DECEMBER);
    	c.set(Calendar.DAY_OF_MONTH, 31);
    	maxDate = c.getTime();
    }
    
    @Override
    public boolean isAbleToEdit() {
    	// (selected.getAnio() >= MxDateTime.now().getYear())
        return true;
    }

    @PostConstruct
    public void init() {
        prepareCreate();
        temporalidades = temporalidadesFacade.findAll();
        resetMaxDate(MxDateTime.now().getYear());
    }

    public void prepareCreate() {
        selected = new PeriodoAcceso();
    }
    
    public void prepareEdit(PeriodoAcceso periodo) {
        if (periodo != null) {
            this.selected = periodo;
        }
        selectedTemporalidad = selected.getIdCiclo().getIdTemporalidad();
        resetMaxDate(Integer.valueOf(selected.getAnio()));
    }
    
    public void changeYear(AjaxBehaviorEvent event) {
    	var a = (SelectOneMenu)event.getComponent();
    	resetMaxDate(Integer.parseUnsignedInt(a.getValue().toString()));
    }

    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    periodoAccesoFacade.create(selected);
                    msg = msg.concat("Creó el Periodo ");
                    break;
                case UPDATE:
                    periodoAccesoFacade.edit(selected);
                    msg = msg.concat("Actualizó el Periodo ");
                    break;
                case DELETE:
                    periodoAccesoFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Periodo ");
                    break;
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getIdCiclo().toString()).concat(" para " + selected.getIdInstitucion().getNombre());
            loginMB.createEventLog(msg, msg);
            utx.commit();

            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
            prepareCreate();
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
        var msg = "";
        /*
         * NOTE: SINCE THERE'S NO LONGER CYCLES, WE SELECT THE DEFAULT ONE:
         */
        this.selected.setIdCiclo(this.selectedTemporalidad.getCiclosList().get(0));
        if (this.todas) {
            try {
                //Bulk persistence:
                utx.begin();
                var all = institucionesFacade.findAll();
                for (Instituciones i : all) {
                    var c = (PeriodoAcceso) BeanUtilsBean2.getInstance().cloneBean(this.selected);
                    c.setIdInstitucion(i);                    
                    var stored = periodoAccesoFacade.findExistence(c);
                    if (stored == null) {
                        periodoAccesoFacade.create(c);
                        msg = msg.concat("Creó el Periodo ").concat(c.getIdCiclo().toString()).concat(" para " + c.getIdInstitucion().getNombre());
                        loginMB.createEventLog(msg, msg);
                    } else {
                        stored.setFechaInicio(c.getFechaInicio());
                        stored.setFechaFin(c.getFechaFin());
                        periodoAccesoFacade.edit(stored);
                        msg = msg.concat("Actualizó el Periodo ").concat(stored.getIdCiclo().toString()).concat(" para " + stored.getIdInstitucion().getNombre());
                        loginMB.createEventLog(msg, msg);
                    }
                    msg = "";
                }
                utx.commit();

                msg = msg.concat("Creó el Periodo para todas las Instituciones");
                Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
                prepareCreate();
            } catch (IllegalAccessException | IllegalStateException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException | 
                    HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex) {
                    log.error(ex.getMessage());
                } finally {
                    Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
                }
            }
        } else {
            if (periodoAccesoFacade.findExistence(selected) == null) {
                persist(DefaultsMB.PersistAction.CREATE);
            } else {
                msg = msg.concat("EL PERIODO DE ACCESO PARA LA INSTITUCIÓN YA EXISTE!");
                Context.sendError(null, "ADVERTENCIA:", msg);
                Context.openDialog("ADVERTENCIA:", msg);
            }
        }
    }

    public void update() {
        persist(DefaultsMB.PersistAction.UPDATE);
    }

    public void destroy() {
        persist(DefaultsMB.PersistAction.DELETE);
    }

    public boolean isOnTime(PeriodoAcceso periodo) {
        return !(now.after(periodo.getFechaFin()) || now.before(periodo.getFechaInicio()));
    }
    
    public Date getNow() {
		return now;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public List<PeriodoAcceso> getAll() {
        return periodoAccesoFacade.findAll();
    }

    public PeriodoAcceso getById(Integer id) {
        return periodoAccesoFacade.find(id);
    }

    public PeriodoAcceso getSelected() {
        return selected;
    }

    public void setSelected(PeriodoAcceso selected) {
        this.selected = selected;
    }

    public List<PeriodoAcceso> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<PeriodoAcceso> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public Ciclos getCicloById(Integer id) {
        return ciclosFacade.find(id);
    }
    
    public List<Temporalidades> getTemporalidades() {
        return temporalidades;
    }

    public Temporalidades getSelectedTemporalidad() {
        return selectedTemporalidad;
    }

    public void setSelectedTemporalidad(Temporalidades selectedTemporalidad) {
        this.selectedTemporalidad = selectedTemporalidad;
    }

    public boolean isTodas() {
        return todas;
    }

    public void setTodas(boolean todas) {
        this.todas = todas;
    }

    @FacesConverter(forClass = PeriodoAcceso.class)
    public static class PeriodoAccesoControllerConverter implements Converter<PeriodoAcceso> {

        @Override
        public PeriodoAcceso getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (PeriodoAccesoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "periodoAccesoController");
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
        public String getAsString(FacesContext facesContext, UIComponent component, PeriodoAcceso object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdPeriodoAcceso());
        }
    }
}
