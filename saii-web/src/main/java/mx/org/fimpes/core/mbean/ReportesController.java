package mx.org.fimpes.core.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.saii.ejb.core.SeccionFacade;
import mx.org.fimpes.saii.ejb.core.TemporalidadesFacade;
import mx.org.fimpes.saii.ejb.core.TipoIndicadorFacade;
import mx.org.fimpes.saii.ejb.reports.ReportSB;
import mx.org.fimpes.saii.model.core.Ciclos;
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.model.core.Seccion;
import mx.org.fimpes.saii.model.core.Temporalidades;
import mx.org.fimpes.saii.model.core.TipoIndicador;
import mx.org.fimpes.saii.model.reports.Report;
import mx.org.fimpes.security.Permissions;
import mx.org.fimpes.security.mbean.LoginMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("reportesController")
@SessionScoped
public class ReportesController implements Serializable,  Permissions<Report> {
	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ReportesController.class);   
    private Report tipoReporte;
    private String reporte, activos;
    private List<?> result, filteredItems;
    private Instituciones institucion;
    private List<String> anios;
    private List<Temporalidades> temporalidades;
    private List<Temporalidades> selectedTemporalidades;
    private List<TipoIndicador> tiposIndicadores;
    private List<TipoIndicador> tipos;
    private List<Seccion> secciones, seccionesSelected;
    private Ciclos ciclo;
    private boolean todas;
    
    @EJB
    private ReportSB reportSB;
    @EJB
    private TemporalidadesFacade temporalidadesFacade;
    @EJB
    private TipoIndicadorFacade tipoIndicadorFacade;
    @EJB
    private SeccionFacade seccionFacade;
    @Inject
    LoginMB loginMB;

    @Override
    public boolean isAbleToSearch() {
        return loginMB.isFimpesRol();
    }

    @PostConstruct
    public void init() {
    	temporalidades = temporalidadesFacade.findAll();
    	ciclo = temporalidades.get(0).getCiclosList().get(0);
        todas = false;
        activos = "activos";
        tiposIndicadores = tipoIndicadorFacade.findAll();
    }
    
    private void clearResult() {
    	if(result != null) {
			result.clear();
			result = null;
		}
		if(filteredItems != null) {
			filteredItems.clear();
			filteredItems = null;
		}
    }
    
	public String show(Report report) {
		this.tipoReporte = report;
		var outcome = "";
		
		switch (report) {
		case AVANCE:
			reporte = "Reporte de Avance";
			outcome = "/reportes/avance";
			break;
		case INSTITUCION:
			reporte = "Reporte de Institución";
			outcome = "/reportes/institucion";
			break;
		case CAMPUS:
			reporte = "Reporte de Campus";
			outcome = "/reportes/campus";
			break;		
		case VINCULACION:
			reporte = "Reporte de Vinculación";
			outcome = "/reportes/vinculacion";
			break;
		case PROGRAMAS:
			reporte = "Reporte de Programas";
			outcome = "/reportes/programas";
			break;
		case DOCENTES:
			reporte = "Reporte de Docentes";
			outcome = "/reportes/docentes";
			break;
		case PROCESO:
			reporte = "Reporte de Proceso Educativo";
			outcome = "/reportes/proceso";
			break;
		case RESULTADOS:
			reporte = "Reporte de Resultados Educativos";
			outcome = "/reportes/resultados";
			break;
		default:
			break;
		}
		clearResult();
		
		return outcome;
	}
	
	public void changeSeccion() {
        if(tipos.size() == 1) {
        	secciones = tipos.get(0).getSeccionList();
        }else {
        	if(secciones != null && !secciones.isEmpty()) {
        		secciones.clear();
        	}
        	if(seccionesSelected != null && !seccionesSelected.isEmpty()) {
        		seccionesSelected.clear();
        	}
        }
    }
	
    public void search() {
    	var params = new HashMap<String, Object>();
    	if(!todas) {
    		params.put("institucion", institucion.getIdInstitucion());
    	}
    	params.put("anios", anios);
    	// NOTE: SINCE THERE'S NO LONGER CYCLES, WE SELECT THE DEFAULT ONE:
    	var ciclos = new ArrayList<String>();    	
        this.selectedTemporalidades.forEach(t -> {
        	t.getCiclosList().forEach(c -> {
        		ciclos.add(c.getIdCiclo().toString());
            });
        });
        params.put("ciclos", ciclos);
    	  	
    	if(tipoReporte == Report.AVANCE) {
    		if(this.tipos != null && !this.tipos.isEmpty()) {
    			var t  = new ArrayList<String>();
    			this.tipos.forEach(i -> {
    				t.add(i.getIdTipo().toString());
    			});
    			params.put("tipos", t);
    		}
    		if(this.seccionesSelected != null && !this.seccionesSelected.isEmpty()) {
    			var s  = new ArrayList<String>();
    			this.seccionesSelected.forEach(i -> {
    				s.add(i.getIdSeccion().toString());
    			});
    			params.put("secciones", s);
    		}
    	}else {
    		params.put("estatus", activos);
    	}
    	
    	try {
			result = reportSB.getResult(tipoReporte, params);
			if(result != null && !result.isEmpty()) {
				Context.sendInfo(null, reporte, DefaultsMB.SUCCESS_MESSAGE);
			}else {
				Context.sendWarn(null, reporte, "No existe información bajo el criterio seleccionado!");
			}
		} catch (Exception e) {
			Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
		}
    }
    
    public List<?> getResult() {
    	return result;
    }
    
    public List<?> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<?> filteredItems) {
        this.filteredItems = filteredItems;
    }
    
    public String getReporte() {
		return reporte;
	}
    
	public String getActivos() {
		return activos;
	}

	public void setActivos(String activos) {
		this.activos = activos;
	}
    
    public Instituciones getInstitucion() {
		return institucion;
	}

	public void setInstitucion(Instituciones institucion) {
		this.institucion = institucion;
	}

	public List<String> getAnios() {
		return anios;
	}

	public void setAnios(List<String> anios) {
		this.anios = anios;
	}

	public List<Temporalidades> getTemporalidades() {
        return temporalidades;
    }

    public List<Temporalidades> getTemporalidad() {
        return selectedTemporalidades;
    }

    public void setTemporalidad(List<Temporalidades> selectedTemporalidades) {
        this.selectedTemporalidades = selectedTemporalidades;
    }
    
    public List<TipoIndicador> getTiposIndicadores() {
        return tiposIndicadores;
    }
    
    public List<TipoIndicador> getTipos() {
        return tipos;
    }
    
    public void setTipos(List<TipoIndicador> tipos) {
		this.tipos = tipos;
	}
    
    public List<Seccion> getSecciones() {
        return secciones;
    }
    
    public List<Seccion> getSeccionesSelected() {
        return seccionesSelected;
    }
    
    public void setSeccionesSelected(List<Seccion> seccionesSelected) {
		this.seccionesSelected = seccionesSelected;
	}

    public Ciclos getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclos ciclo) {
		this.ciclo = ciclo;
	}

	public boolean isTodas() {
        return todas;
    }

    public void setTodas(boolean todas) {
        this.todas = todas;
    }
}
