package mx.org.fimpes.core.mbean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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

import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.saii.ejb.core.PeriodoAccesoFacade;
import mx.org.fimpes.saii.ejb.core.ProcesoCapturaFacade;
import mx.org.fimpes.saii.ejb.core.RegistroIndicadoresFacade;
import mx.org.fimpes.saii.ejb.core.TipoIndicadorFacade;
import mx.org.fimpes.saii.ejb.mail.MailService;
import mx.org.fimpes.saii.model.core.Campus;
import mx.org.fimpes.saii.model.core.PeriodoAcceso;
import mx.org.fimpes.saii.model.core.ProcesoCaptura;
import mx.org.fimpes.saii.model.core.Programas;
import mx.org.fimpes.saii.model.core.RegistroIndicadores;
import mx.org.fimpes.saii.model.core.Seccion;
import mx.org.fimpes.saii.model.core.TipoIndicador;
import mx.org.fimpes.saii.model.mail.Mail;
import mx.org.fimpes.saii.util.MxDateTime;
import mx.org.fimpes.security.Permissions;
import mx.org.fimpes.security.mbean.LoginMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("procesoController")
@SessionScoped
public class ProcesoController implements Permissions<ProcesoCaptura>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ProcesoController.class);
    private static final String NO_ACTIVE_PERIOD = "No hay periodos activos, ni procesos pendientes para su Institución.";
    private static final String NO_INDICATORS_RECORD = "No ha comenzado la captura de indicadores y/o se encuentra fuera del periodo de captura.";
    private static final ResourceBundle MAIL_CONFIG = ResourceBundle.getBundle("Mail", Locale.getDefault(), Thread.currentThread().getContextClassLoader());
    
    private final Date now;
    private ProcesoCaptura selected;
    private PeriodoAcceso periodo;
    private List<ProcesoCaptura> all;
    private List<ProcesoCaptura> filteredItems;
    private List<TipoIndicador> tiposIndicadores;
    private Campus selectedCampus;
    private List<Campus> campus;
    private Programas selectedPrograma;
    private List<Programas> programas;
    private List<Seccion> avanceSeccion;
    private boolean completado;

    @Resource
    UserTransaction utx;
    @EJB
    private ProcesoCapturaFacade procesoCapturaFacade;
    // Relaciones con otras entidades:    
    @EJB
    private PeriodoAccesoFacade periodoAccesoFacade;
    @EJB
    private RegistroIndicadoresFacade registroFacade;
    @EJB
    private TipoIndicadorFacade tipoIndicadorFacade;
    @EJB
    private MailService mailService;
    @Inject
    LoginMB loginMB;
    @Inject
    InstitucionesController institucionesController;

    public ProcesoController() {
        now = MxDateTime.dateOnly();
    }

    @Override
    public boolean isAbleToEdit() {
        return (!loginMB.isFimpesRol() && !selected.getCompletado() && isOnTime());
    }

    private boolean isOnTime() {
        return !(now.after(periodo.getFechaFin()) || now.before(periodo.getFechaInicio()));
    }

    @PostConstruct
    public void init() {
        try {
            tiposIndicadores = tipoIndicadorFacade.findAll();
            if (loginMB.isFimpesRol()) {
                all = procesoCapturaFacade.findAll();
            } else {
                // Se obtiene(n) el(los) periodo(s) de acceso vigente(s) para ésta institución:
                List<PeriodoAcceso> accesos = periodoAccesoFacade.findCurrentPeriodsBy(loginMB.getAuthenticatedUser().getIdInstitucion());
                // Se obtienen los procesos de captura de la institución:
                all = procesoCapturaFacade.findBy(loginMB.getAuthenticatedUser().getIdInstitucion());

                if (all != null && !all.isEmpty()) {
                    var reload = false;
                    // Se comprueba que exista un proceso p/c periodo vigente:
                    /* Solo se comprueban los últimos registros (Easy path):
                	if(accesos != null && !accesos.isEmpty()){
                    PeriodoAcceso a = accesos.get(0);
                    ProcesoCaptura c = all.get(0);
                    if(c.getIdCiclo().equals(a.getIdCiclo()) && c.getAnio() == a.getAnio()){
                        create(prepareCreate(a));
                        reload = true;
                    }
                	}*/
                    for (PeriodoAcceso a : accesos) {
                        var found = false;
                        for (ProcesoCaptura c : all) {
                        	// Se verifica que por cada periodo de acceso exista un proceso:
                            if (c.getIdCiclo().equals(a.getIdCiclo()) && c.getAnio() == a.getAnio()) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                        	// Si no existe, se genera con la especificación del periodo:
                            create(new ProcesoCaptura(a.getIdInstitucion(), a.getIdCiclo(), a.getAnio()));
                            reload = true;
                        }
                    }
                    if (reload) {
                        // Se actualizan los procesos:
                        all = procesoCapturaFacade.findBy(loginMB.getAuthenticatedUser().getIdInstitucion());
                    }
                } else {
                    if (accesos != null && !accesos.isEmpty()) {
                        // Se registran por primera vez los procesos de captura:
                        accesos.stream().map((a) -> {
                        	// Por cada acceso vigente, se crea un proceso para la Institución:
                            return new ProcesoCaptura(a.getIdInstitucion(), a.getIdCiclo(), a.getAnio());
                        }).forEachOrdered((p) -> {
                            create(p);
                        });
                        all = procesoCapturaFacade.findBy(loginMB.getAuthenticatedUser().getIdInstitucion());
                    } else {
                        log.info(NO_ACTIVE_PERIOD);
                        Context.sendWarn(null, NO_ACTIVE_PERIOD, "Contacte al Administrador de FIMPES");
                    }
                }
            }
        } catch (Exception e) {
            Context.sendWarn(null, DefaultsMB.FAILURE_MESSAGE, "No se logró inicializar su proceso de captura. " + e.getMessage());
        }
    }

    private void initIndicadores() throws Exception {
        try {
            utx.begin();
            // Se verifica que los indicadores hayan sido inicializados:
            tiposIndicadores.stream().forEach((tipo) -> {
                switch (tipo.getIdTipo()) {
                    case 1:
                        // INSTITUCIÓN:
                        if (!registroFacade.isInstituteInitialized(selected)) {
                            tipo.getIndicadoresList().stream().forEach((i) -> {
                                if (i.getActivo()) {
                                    var r = new RegistroIndicadores(selected, i);
                                    registroFacade.create(r);
                                }
                            });
                        }
                        break;
                    case 2:
                        // CAMPUS:
                    	// **** OPCIONAL: Se obtienen los campus activos cuyas temporalidades de sus programas activos 
                    	// **** coincidan con la temporalidad que se esté capturando
                    	getCampusActivos().stream().forEach((c) -> {
                        	//Se generarán unicamente registros de los campus activos:
                            if (!registroFacade.isCampusInitialized(selected, c)) {
                                tipo.getIndicadoresList().stream().forEach((i) -> {
                                    if (i.getActivo()) {
                                        var r2 = new RegistroIndicadores(selected, i);
                                        r2.setIdCampus(c);
                                        registroFacade.create(r2);
                                    }
                                });
                            }
                        });
                        break;
                    case 3:
                        // PROGRAMA:
                    	getCampusActivos().stream().forEach((c) -> {
                        	//Se generarán unicamente registros de los campus activos para los programas activos 
                    		//con la misma temporalidad que el proceso:
                        	getProgramasTemporalidades(c).stream().forEach((p) -> {
                                if (!registroFacade.isProgramInitialized(selected, p)) {
                                    tipo.getIndicadoresList().stream().forEach((i) -> {
                                        if (i.getActivo()) {
                                            var r3 = new RegistroIndicadores(selected, i);
                                            r3.setIdCampus(c);
                                            r3.setIdPrograma(p);
                                            registroFacade.create(r3);
                                        }
                                    });
                                }
                            });
                        });
                        break;
                    default:
                        break;
                }

            });
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            }
            throw e;
        }
    }
    
    private List<Campus> getCampusActivos(){
    	var ca = new ArrayList<Campus>();
    	selected.getIdInstitucion().getCampusList().forEach((c) -> {
    		// Se obtienen los campus activos:
    		if(c.getActivo() == true) {
    			ca.add(c);
    		}
    	});
    	return ca;
    }
    
    private List<Programas> getProgramasTemporalidades(Campus c){
    	var pt = new ArrayList<Programas>();
    	c.getProgramasList().forEach((p) -> {
            log.info("GENERANDO LISTA DE PROGRAMAS");
    		// Se obtienen los programas activos y con la misma temporalidad que el proceso:
    		if(
                p.getActivo() == true && p.getIdTemporalidad().equals(
                    
                    selected.getIdCiclo().getIdTemporalidad()
                )
            ) {
                log.info(p);
    			pt.add(p);
    		}
    	});
    	return pt;
    }

    private void initCampus() {
    	// **** OPCIONAL: Se obtienen los campus activos cuyas temporalidades de sus programas activos 
    	// **** coincidan con la temporalidad que se esté capturando
        // Se obtienen los campus activos del proceso:            
        campus = getCampusActivos();
        if (campus != null && !campus.isEmpty()) {
            selectedCampus = campus.get(0);
                    log.info("CAMPUES ES DIFERENTE DE EMPTY");
                    log.info(programas);
            //Se obtienen los programas del campus con la misma temporalidad que el proceso:
            programas = getProgramasTemporalidades(selectedCampus);
            if (programas != null && !programas.isEmpty()) {
                selectedPrograma = programas.get(0);
            } else {
                selectedPrograma = null;
            }
        } else {
            selectedCampus = null;
        }
    }

    private List<RegistroIndicadores> getIndicadoresBy(Seccion seccion) {
        List<RegistroIndicadores> r = null;
        int tipo = seccion.getIdTipo().getIdTipo();
        switch (tipo) {
            case 1:
                // INSTITUCIÓN:
                r = registroFacade.findByInstitute(selected, seccion.getIndicadoresList());
                break;
            case 2:
                // CAMPUS:
                if (selectedCampus != null) {
                    r = registroFacade.findByCampus(selected, selectedCampus, seccion.getIndicadoresList());
                }
                break;
            case 3:
                // PROGRAMA:
                if (selectedPrograma != null) {
                    r = registroFacade.findByProgram(selected, selectedPrograma, seccion.getIndicadoresList());
                }
                break;
            default:
                break;
        }

        return r;
    }

    private void loadIndicadores() {
        tiposIndicadores.stream().forEach((tipo) -> {
            tipo.getSeccionList().stream().forEach((seccion) -> {
                seccion.setRegistroList(getIndicadoresBy(seccion));
            });
        });
    }

    public void changeProcess(ProcesoCaptura proceso) {
        if (proceso != null) {
            this.selected = proceso;
        }
        try {
            // Se obtiene el periodo asociado al proceso seleccionado:
            periodo = periodoAccesoFacade.findBy(selected.getIdInstitucion(), selected.getIdCiclo(), selected.getAnio());
            // Se actualizan las listas relacionadas al proceso:
            selected = procesoCapturaFacade.find(selected.getIdProceso());
            // Verificación del rol:
            if (loginMB.isFimpesRol()) {
                Context.sendWarn(null, "ACTOR FIMPES", "Unicamente podrá observar los avances de las Instituciones.");
            } else {
                if (isOnTime()) {
                    initIndicadores();
                    Context.sendInfo(null, "PROCESO ACTIVO", selected.toString());
                } else {
                    Context.sendWarn(null, "Se encuentra fuera del periodo de captura", "Unicamente podrá consultar los indicadores.");
                }
            }
            // Carga de indicadores para presentación:
            initCampus();
            loadIndicadores();           
        } catch (Exception e) {
            Context.sendWarn(null, DefaultsMB.FAILURE_MESSAGE, "No se lograron inicializar los indicadores. " + e.getMessage());
        }
    }

    private void initIndicadoresCampus() throws Exception {
        try {
            utx.begin();
            // Se obtienen los indicadores de Campus:
            var c = tipoIndicadorFacade.find(2);
            c.getIndicadoresList().stream().forEach((i) -> {
                if (i.getActivo()) {
                    var r = new RegistroIndicadores(selected, i);
                    r.setIdCampus(selectedCampus);
                    registroFacade.create(r);
                }
            });
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            }
            throw e;
        }
    }

    public void changeCampus() {
        try {
            // Verificación del rol:
            if (!loginMB.isFimpesRol() && isOnTime()) {
                // Se verifica que los indicadores del campus hayan sido inicializados:
                if (!registroFacade.isCampusInitialized(selected, selectedCampus)) {
                    initIndicadoresCampus();
                }
            }
            //Se obtienen los programas del campus con la misma temporalidad que el proceso:
            programas = getProgramasTemporalidades(selectedCampus);
            if (programas != null && !programas.isEmpty()) {
                selectedPrograma = programas.get(0);
            } else {
                selectedPrograma = null;
            }
            // Carga de indicadores para presentación:
            loadIndicadores();
            Context.sendInfo(null, "Se seleccionaron los indicadores del Campus", selectedCampus.getNombre());
        } catch (Exception e) {
            Context.sendWarn(null, DefaultsMB.FAILURE_MESSAGE, "No se lograron inicializar los indicadores del campus " + selectedCampus.getNombre() + ". " + e.getMessage());
        }
    }

    private void initIndicadoresPrograma() throws Exception {
        try {
            utx.begin();
            // Se obtienen los indicadores de Programa:
            var p = tipoIndicadorFacade.find(3);
            p.getIndicadoresList().stream().forEach((i) -> {
                if (i.getActivo()) {
                    log.info("programas activos");
                    log.info(i);
                    var r = new RegistroIndicadores(selected, i);
                    r.setIdPrograma(selectedPrograma);
                    r.setIdCampus(selectedPrograma.getIdCampus());
                    registroFacade.create(r);
                }
            });
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            }
            throw e;
        }
    }

    public void changePrograma() {
        try {
            // Verificación del rol:
            if (!loginMB.isFimpesRol() && isOnTime()) {
                // Se verifica que los indicadores del programa hayan sido inicializados:
                if (!registroFacade.isProgramInitialized(selected, selectedPrograma)) {
                    initIndicadoresPrograma();
                }
            }
            // Carga de indicadores para presentación:
            loadIndicadores();
            Context.sendInfo(null, "Se seleccionaron los indicadores del Programa", selectedPrograma.getNombre());
        } catch (Exception e) {
            Context.sendWarn(null, DefaultsMB.FAILURE_MESSAGE, "No se lograron inicializar los indicadores del programa " + selectedPrograma.getNombre() + ". " + e.getMessage());
        }
    }
    
    private boolean validate(RegistroIndicadores i) {
          log.info("VALIDA INDICADOR RDM");
          
    	if(i.getIdIndicador().getEsOperador()) {
    		try {
    			registroFacade.validateSumOfOperands(selected, i);
    		}catch(Exception e) {
    			Context.sendWarn(null, e.getMessage(), "MODIFIQUE LA(S) CANTIDAD(ES)!");
    			return false;
    		}
    	}
    	
    	return true;
    }

    public void updateValor(RegistroIndicadores i) {
    	if(validate(i)) {
    		var msg = "Actualizó el Indicador " + i.toString();
            log.info("Actualizó el Indicador rdm " + i.toString());
            try {
                utx.begin();
                registroFacade.edit(i);
                loginMB.createEventLog(msg, selected.toString());
                utx.commit();
                
                Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
            } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
                try {
                    log.info(e.getMessage());
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex) {
                    log.error(ex.getMessage());
                } finally {
                    Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, "El indicador no se pudo actualizar. " + e.getMessage());
                }
            }
    	}
    }
    
    public void updateAvance() {    	
    	// Obtención del avance por sección:
        avanceSeccion = registroFacade.getProgress(selected);
        if(!selected.getCompletado()) {
        	for(Seccion s : avanceSeccion){
            	if(s.getTotalIndicadoresRequeridos() != s.getTotalIndicadoresCompletados()) {
            		completado = false;
            		return;
            	}
            }
        	completado = (!loginMB.isFimpesRol() && loginMB.isUserInRol("ADMIN_INSTITUTO"));
        }else {
        	// Se asegura de que la accion finalizar permanezca deshabilitada una vez completado:
        	completado = false;
        }
    }
    
    private Mail sendMail(String subject) throws Exception {
    	var m = new Mail();
    	m.setCc(loginMB.getAuthenticatedUser().getCorreo());
    	m.setSubject(subject);
    	var content = MessageFormat.format(MAIL_CONFIG.getString("msgprocess"), Context.absolutePath(), selected.toString());
    	m.setContent(content);
    	mailService.sendToFimpes(m);
    	
    	return m;
    }
    
    public void finishProcess() {
    	/*
    	 * NOTE: SINCE TOTAL OF CAMPUS AND TOTAL OF PROGRAMS VARY BETWEEN TEMPORALITIES,
    	 * WE NO LONGER NEED THE VALIDATION OF TOTALS:
    	 * 
    	 * if(institucionesController.isCampusCompleted(selected.getIdInstitucion())) {
    		if(institucionesController.isProgramCompleted(selected.getIdInstitucion())) {*/
    			var idx = all.indexOf(selected);
    	        try {
    	        	var msg = "Actualizó el Proceso ";
    	            utx.begin();
    	            procesoCapturaFacade.edit(selected);
    	            msg = msg.concat(selected.toString());
    	            loginMB.createEventLog(msg, msg);
    	            utx.commit();
    	            
    	            try {
    	        		msg = "Envió el informe " + selected.toString();
    	                utx.begin();
    	                var m = sendMail(msg);
    	                loginMB.createEventLog(msg, m);
    	                utx.commit();
    	                
    	                // Se actualiza la lista de procesos:
    	                all.set(idx, selected);
    	                Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
    	                Context.openDialog(msg, DefaultsMB.SUCCESS_MESSAGE);
    	            } catch (Exception e) {
    	                try {
    	                    utx.rollback();
    	                } catch (IllegalStateException | SecurityException | SystemException ex) {
    	                    log.error(ex.getMessage());
    	                }finally{
    	                	Context.sendInfo(null, "Se finalizó el proceso SATISFACTORIAMENTE, pero no se pudo notificar por correo.", "Informe a FIMPES");
    	                }
    	            }
    	        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
    	            try {
    	                utx.rollback();
    	            } catch (IllegalStateException | SecurityException | SystemException ex) {
    	                log.error(ex.getMessage());
    	            }finally{
    	            	Context.sendError(null, "No se pudo finalizar el proceso en estos momentos", DefaultsMB.FAILURE_MESSAGE);
    	            }
    	        }
        /*  }else {
    			var summary = "No puede finalizar el proceso, no coincide el Total de Programas especificados con los Programas registrados";
    			var detail = "Capture los Programas faltantes y posteriormente sus indicadores";
    			Context.sendWarn(null, summary, detail);
    			Context.openWarnDialog(summary, detail);
    		}
    	}else {
    		var summary = "No puede finalizar el proceso, no coincide el Total de Campus especificados con los Campus registrados";
			var detail = "Capture los Campus faltantes y posteriormente sus indicadores";
			Context.sendWarn(null, summary, detail);
    		Context.openWarnDialog(summary, detail);
    	}*/
    }

    private void create(ProcesoCaptura p) {
    	var msg = "Creó el Proceso ";
        try {
            utx.begin();
            procesoCapturaFacade.create(p);
            msg = msg.concat(p.toString());
            loginMB.createEventLog(msg, msg);
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            }
        }
    }

    public RegistroIndicadores getIndicadorById(Integer id) {
        return registroFacade.find(id);
    }

    public List<ProcesoCaptura> getAll() {
        return all;
    }

    public ProcesoCaptura getById(Integer id) {
        return procesoCapturaFacade.find(id);
    }

    public ProcesoCaptura getSelected() {
        return selected;
    }

    public void setSelected(ProcesoCaptura selected) {
        this.selected = selected;
    }

    public PeriodoAcceso getPeriodo() {
        return periodo;
    }

    public List<ProcesoCaptura> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<ProcesoCaptura> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public List<TipoIndicador> getTiposIndicadores() {
        return tiposIndicadores;
    }

    public Campus getSelectedCampus() {
        return selectedCampus;
    }

    public void setSelectedCampus(Campus selectedCampus) {
        this.selectedCampus = selectedCampus;
    }

    public List<Campus> getCampus() {
        return campus;
    }

    public Programas getSelectedPrograma() {
        return selectedPrograma;
    }

    public void setSelectedPrograma(Programas selectedPrograma) {
        this.selectedPrograma = selectedPrograma;
    }

    public List<Programas> getProgramas() {
        return programas;
    }

    public String getNoActivePeriodMsg() {
        return NO_ACTIVE_PERIOD;
    }

    public String getNoIndicatorsRecordMsg() {
        return NO_INDICATORS_RECORD;
    }

    public List<Seccion> getAvanceSeccion() {
		return avanceSeccion;
	}

	public void setAvanceSeccion(List<Seccion> avanceSeccion) {
		this.avanceSeccion = avanceSeccion;
	}
	
    public boolean isCompletado() {
		return completado;
	}

	@FacesConverter(forClass = ProcesoCaptura.class)
    public static class ProcesoControllerConverter implements Converter<ProcesoCaptura> {

        @Override
        public ProcesoCaptura getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            var controller = (ProcesoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "procesoController");
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
        public String getAsString(FacesContext facesContext, UIComponent component, ProcesoCaptura object) {
            if (object == null) {
                return null;
            }
            return getStringKey(object.getIdProceso());
        }
    }
}
