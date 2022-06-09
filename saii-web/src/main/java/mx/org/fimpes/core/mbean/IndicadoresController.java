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
import mx.org.fimpes.saii.ejb.core.IndicadoresFacade;
import mx.org.fimpes.saii.ejb.core.SeccionFacade;
import mx.org.fimpes.saii.ejb.core.TipoIndicadorFacade;
import mx.org.fimpes.saii.model.core.Indicadores;
import mx.org.fimpes.saii.model.core.Seccion;
import mx.org.fimpes.saii.model.core.TipoIndicador;
import mx.org.fimpes.security.mbean.LoginMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("indicadoresController")
@SessionScoped
public class IndicadoresController implements Serializable {
	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(IndicadoresController.class);
    private Indicadores selected;
    private List<Indicadores> filteredItems;
    private List<TipoIndicador> tiposIndicadores;
    private List<Seccion> secciones;
    private boolean tipoDatoNumerico;

    @Resource
    UserTransaction utx;
    @EJB
    private IndicadoresFacade indicadoresFacade;
    // Relaciones con otras entidades:
    @EJB
    private TipoIndicadorFacade tipoIndicadorFacade;
    @EJB
    private SeccionFacade seccionFacade;
    @Inject
    LoginMB loginMB;

    public IndicadoresController() {
    }

    @PostConstruct
    public void init() {
        initOptions();
    }
    
    private void initOptions(){
        tipoDatoNumerico = true;
        tiposIndicadores = tipoIndicadorFacade.findAll();
        secciones = tiposIndicadores.get(0).getSeccionList();
    }

    public void prepareCreate() {
        selected = new Indicadores();
        selected.setActivo(true);
        selected.setRequerido(true);
        initOptions();
    }

    public void prepareEdit(Indicadores indicador) {
        if(indicador != null){
            this.selected = indicador;
        }
        changeTipoDato();
        changeSeccion();
    }

    public void changeTipoDato() {
        tipoDatoNumerico = selected.getTipoDato().equalsIgnoreCase("NUMERICO");
    }

    public void changeSeccion() {
        secciones = selected.getIdTipo().getSeccionList();
    }
    private void persist(PersistAction action) {
        var msg = "";
        try {
            utx.begin();
            switch (action) {
                case CREATE:
                    indicadoresFacade.create(selected);
                    msg = msg.concat("Creó el Indicador ");
                    break;
                case UPDATE:
                 log.info("ACTUALIZANDO INDICADOR");
                    indicadoresFacade.edit(selected);
                    msg = msg.concat("Actualizó el Indicador ");
                    break;
                case DELETE:
                    indicadoresFacade.remove(selected);
                    msg = msg.concat("Eliminó definitivamente el Indicador ");
                    break;
                default:
                	throw new NotSupportedException("PersistAction unknown");
            }
            msg = msg.concat(selected.getEtiqueta());
            loginMB.createEventLog(msg, selected);
            utx.commit();

            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
            selected = null;
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                log.info("ERROR  INDICADOR");
                log.error(e.getMessage());
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            } finally {
                Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
            }
        }
    }

    public void create() {
        if (selected.getEsOperador()) {
            var i = getById(selected.getIdIndicadorTotal());
            if (!i.getIdSeccion().equals(selected.getIdSeccion())) {
                var msg = "LOS INDICADORES DEBEN DE PERTENECER A LA MISMA SECCIÓN";
                PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
                Context.sendError(null, "ADVERTENCIA:", msg);
                Context.openDialog("ADVERTENCIA:", msg);
                return;
            }
        }
        persist(DefaultsMB.PersistAction.CREATE);
    }

    public void update() {
        persist(DefaultsMB.PersistAction.UPDATE);
    }

    public void destroy() {
        persist(DefaultsMB.PersistAction.DELETE);
    }

    public List<Indicadores> getAll() {
        return indicadoresFacade.findAll();
    }

    public List<Indicadores> getAllTotals() {
        return indicadoresFacade.findAllTotals();
    }

    public Indicadores getById(Integer id) {
        return indicadoresFacade.find(id);
    }

    public Indicadores getSelected() {
        return selected;
    }

    public void setSelected(Indicadores selected) {
        this.selected = selected;
    }

    public List<Indicadores> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Indicadores> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public List<TipoIndicador> getTiposIndicadores() {
        return tiposIndicadores;
    }

    public TipoIndicador getTipoIndicadorById(Integer id) {
        return tipoIndicadorFacade.find(id);
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public Seccion getSeccionById(Integer id) {
        return seccionFacade.find(id);
    }

    public boolean isTipoDatoNumerico() {
        return tipoDatoNumerico;
    }

    @FacesConverter(forClass = Indicadores.class)
    public static class IndicadoresControllerConverter implements Converter<Indicadores> {

        @Override
        public Indicadores getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (IndicadoresController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "indicadoresController");
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
        public String getAsString(FacesContext facesContext, UIComponent component, Indicadores object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdIndicador());
        }

    }

}
