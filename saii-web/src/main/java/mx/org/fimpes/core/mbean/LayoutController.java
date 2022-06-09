package mx.org.fimpes.core.mbean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.apache.tika.mime.MimeTypeException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import dev_project.carga_layout_0_1.Carga_Layout;
import dev_project.carga_layout_update_0_1.Carga_Layout_update;
import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.saii.ejb.core.TemporalidadesFacade;
import mx.org.fimpes.saii.ejb.reports.ReportSB;
import mx.org.fimpes.saii.model.core.Ciclos;
import mx.org.fimpes.saii.model.core.Temporalidades;
import mx.org.fimpes.security.mbean.LoginMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named("layoutController")
@SessionScoped
public class LayoutController implements Serializable {
	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(LayoutController.class);
    private static final String RUTA_LAYOUTS = "RutaLayouts";
    private static final String ETL = "ProcesoEtl";
    private static final String XLS_TYPE = "application/vnd.ms-excel";
    private static final String XLSX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private StreamedContent layout;
    private Integer anio;
    private List<Temporalidades> temporalidades;
    private Temporalidades temporalidad;
    private Ciclos ciclo;
    private boolean initial;
    
    @Resource
    UserTransaction utx;
    @EJB
    private ReportSB reportSB;
    @EJB
    private TemporalidadesFacade temporalidadesFacade;
    @Inject
    LoginMB loginMB;
    
    @PostConstruct
    public void init() {
    	temporalidades = temporalidadesFacade.findAll();
        temporalidad = temporalidades.get(0);
    }
    
    public StreamedContent getLayout() {
        if (layout != null) {
            try {
                // Se reinicializa el flujo de bytes a la posición inicial, previniendo un error en la lectura del mismo:
                layout.getStream().reset();
            } catch (IOException e) {
            }
        }
        return layout;
    }
    
    public void upload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        var contentType = file.getContentType();
        if (contentType.equalsIgnoreCase(XLS_TYPE) || contentType.equalsIgnoreCase(XLSX_TYPE)) {
            try {            	
            	layout = new DefaultStreamedContent(file.getInputstream(), contentType, file.getFileName());
                log.info("Uploaded document: " + file.getFileName() + " [" + file.getSize() + " bytes]");
                Context.sendInfo(null, "Se adjuntó el documento correctamente", layout.getName());
            } catch (IOException e) {
                Context.sendError(null, "No se adjuntó el documento " + file.getFileName(), e.getMessage());
            }
        } else {
        	layout = null;
            Context.sendError(null, "No se adjuntó el archivo " + file.getFileName(), "Solo se permiten archivos en formato Excel");
        }
    }
    
    public void remove() {
    	Context.sendInfo(null, "Se eliminó el archivo " + layout.getName() , DefaultsMB.SUCCESS_MESSAGE);
    	layout = null;
    }
    
    public void process() {
    	var msg = "Ejecutó el proceso de carga del archivo " + layout.getName();
    	String path = null;
        try {
        	var ext = layout.getName().split("\\.")[1];
        	if(!(ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx"))) {
        		ext = "xls";
        	}
            utx.begin();
            path = DefaultsMB.saveFileToDisk(DefaultsMB.getProperty(RUTA_LAYOUTS), "layout_" + loginMB.getAuthenticatedUser().getIdInstitucion().getSiglas(), true, ext, layout.getStream());
    		var params = new ArrayList<String>();
    		params.add("--context_param Archivo=" + path);    		
    		params.add("--context_param Anio=" + String.valueOf(anio));
    		
    		if(initial) {
    			params.add("--context_param Salida=" + DefaultsMB.getProperty(RUTA_LAYOUTS));
    		}else {
    			// NOTE: SINCE THERE'S NO LONGER CYCLES, WE SELECT THE DEFAULT ONE:
    			this.ciclo = this.temporalidad.getCiclosList().get(0);
    			params.add("--context_param Ciclo=" + this.ciclo.getIdCiclo());
    		}
    		
    		var args = params.toArray(new String[0]);
    		var job = initial ? new Carga_Layout() : new Carga_Layout_update();    		
    		/*var command = MessageFormat.format(DefaultsMB.getProperty(ETL), path, DefaultsMB.getProperty(RUTA_LAYOUTS), String.valueOf(anio));
    		DefaultsMB.exec(command); (String[]) args.toArray(new String[args.size()])*/
    		log.info("Running ETL process...");
    		var result = job.runJobInTOS(args);
    		log.info("Exit code: " + result);
    		log.info("Job Class: " + job.getClass().getName());
    		log.info("Job Parameters:");
    		params.forEach(p -> {
    			log.info(p);
    		});
    		if(result != 0) {
    			msg = msg.concat(" con código de error: " + result);
    			Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, msg);
                Context.openWarnDialog(DefaultsMB.FAILURE_MESSAGE, msg);
    		}else {
    			msg = msg.concat(" exitosamente!");
    			Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
    			Context.openDialog(msg, DefaultsMB.SUCCESS_MESSAGE);
    		}
            loginMB.createEventLog(msg, args);
            utx.commit();
            
            layout = null;
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            } finally {
            	log.error(e.getMessage());
            	var detail = "El archivo " + layout.getName() + " no se pudo procesar. " + e.getMessage();
                Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, detail);
                Context.openWarnDialog(DefaultsMB.FAILURE_MESSAGE, detail);
            }
        }finally {
        	try {
				DefaultsMB.removeFileFromDisk(path);
			} catch (MimeTypeException | IOException e) {
				log.error(e.getMessage());
			}
        }
    }
    
    public void download() {
    	var ctxt = Context.external();
    	ctxt.responseReset(); // Reset headers
		ctxt.setResponseContentType(XLS_TYPE);
		ctxt.setResponseHeader("Content-Disposition", "attachment; filename=\"layout_" + loginMB.getAuthenticatedUser().getIdInstitucion().getSiglas() + ".xls\"");
		try {
			byte[] bytes = reportSB.getExcelLayout(loginMB.getAuthenticatedUser().getIdInstitucion().getIdInstitucion());
			ctxt.setResponseContentLength(bytes.length);
			var output = ctxt.getResponseOutputStream();
			output.write(bytes);
		} catch (Exception e) {
			Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
		} finally {
			Context.responseComplete();
		}
    }

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}
	
	public List<Temporalidades> getTemporalidades() {
        return temporalidades;
    }

    public Temporalidades getTemporalidad() {
        return temporalidad;
    }

    public void setTemporalidad(Temporalidades temporalidad) {
        this.temporalidad = temporalidad;
    }

    public Ciclos getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclos ciclo) {
		this.ciclo = ciclo;
	}

	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}
}
