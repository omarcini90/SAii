package mx.org.fimpes.saii.ejb.reports;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import com.google.common.net.MediaType;

import mx.org.fimpes.saii.model.reports.Report;
import mx.org.fimpes.saii.model.reports.ReporteAvance;
import mx.org.fimpes.saii.model.reports.ReporteCampus;
import mx.org.fimpes.saii.model.reports.ReporteDocentes;
import mx.org.fimpes.saii.model.reports.ReporteInstitucion;
import mx.org.fimpes.saii.model.reports.ReporteProceso;
import mx.org.fimpes.saii.model.reports.ReporteProgramas;
import mx.org.fimpes.saii.model.reports.ReporteResultados;
import mx.org.fimpes.saii.model.reports.ReporteVinculacion;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxExporterConfiguration;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class ReportSB {

    private static final Logger log = Logger.getLogger(ReportSB.class);

    @Resource(lookup = "java:jboss/datasources/SAiiDS")
    DataSource dataSource;

    private void exportToPdf(JasperPrint jasperPrint, String outputFilename) throws JRException {
        var pdfExporter = new JRPdfExporter();
        var exporterInput = new SimpleExporterInput(jasperPrint);
        var exporterOutput = new SimpleOutputStreamExporterOutput(outputFilename);
        pdfExporter.setExporterInput(exporterInput);
        pdfExporter.setExporterOutput(exporterOutput);
        // Configuracion:
        var config = new SimplePdfExporterConfiguration();
        pdfExporter.setConfiguration(config);
        pdfExporter.exportReport();
    }

    private void exportToXLSX(JasperPrint jasperPrint, String outputFilename) throws JRException {
        var exporterInput = new SimpleExporterInput(jasperPrint);
        var exporterOutput = new SimpleOutputStreamExporterOutput(outputFilename);
        var excelExporter = new JRXlsxExporter();
        excelExporter.setExporterInput(exporterInput);
        excelExporter.setExporterOutput(exporterOutput);
        // Configuracion:
        var config = new SimpleXlsxExporterConfiguration();
		     log.info("CONFIGURACION");
			      log.info(config);
				  log.info("-----------------------");
				  log.info(exporterInput);
        excelExporter.setConfiguration(config);
        excelExporter.exportReport();
    }

    public void export(Report report, MediaType format, Map<String, Object> params, String directory, String filename) throws Exception {
        params = (Map<String, Object>) (params != null ? new HashMap<>(params) : new HashMap<>());
        params.put(JRParameter.REPORT_LOCALE, new Locale("es", "MX"));
        try {
            Path path = Paths.get(directory);
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }
            var outputFilename = directory.concat(filename);
            /* Compilacion en tiempo de ejecucion:
            JasperReport jasperReport = JasperCompileManager.compileReport(report.jrxml());*/
            if (format.is(MediaType.PDF)) {
                params.put(JRParameter.IS_IGNORE_PAGINATION, false);
                log.info("Exportando " + report + " a " + format.subtype() + " > " + outputFilename);
                JasperPrint jasperPrint = JasperFillManager.fillReport(report.jasper(), params, dataSource.getConnection());
                exportToPdf(jasperPrint, outputFilename);
                /* METHOD B:
                JasperExportManager.exportReportToPdfFile(jasperPrint,outputFileName); */
            } else if (format.is(MediaType.MICROSOFT_EXCEL)) {
                params.put(JRParameter.IS_IGNORE_PAGINATION, true);
                log.info("Exportando " + report + " a " + format.subtype() + " > " + outputFilename);
                JasperPrint jasperPrint = JasperFillManager.fillReport(report.jasper(), params, dataSource.getConnection());
                exportToXLSX(jasperPrint, outputFilename);
            } else {
                var message = "FORMATO " + format.subtype() + " NO SOPORTADO PARA LA GENERACION DEL DOCUMENTO: " + report;
                log.error(message);
                throw new IllegalArgumentException(message);
            }
        } catch (JRException | SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public byte[] exportToPdf(Report report, Map<String, Object> params) throws Exception {
        params = (Map<String, Object>) (params != null ? new HashMap<>(params) : new HashMap<>());
        params.put(JRParameter.REPORT_LOCALE, new Locale("es", "MX"));
        try {
            /* Compilacion en tiempo de ejecucion:
            JasperReport jasperReport = JasperCompileManager.compileReport(report.jrxml());*/
            params.put(JRParameter.IS_IGNORE_PAGINATION, false);            
            JasperPrint jasperPrint = JasperFillManager.fillReport(report.jasper(), params, dataSource.getConnection());
            log.info("Exportando... " + report + " (pdf)");
            
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
    
    public List getResult(Report report, Map<String, Object> params) throws Exception {
		StringBuilder sql = null;
		Statement stmt = null;
		ResultSet rs = null;
		List result = null;
		var estatus = (String)params.get("estatus");
		try {
			stmt = dataSource.getConnection().createStatement();
			switch (report) {			
			case AVANCE:
				result = new ArrayList<ReporteInstitucion>();
				sql = new StringBuilder("SELECT id_registro, institucion, tipo, seccion, campus, programa, temporalidad, anio, indicador, valor, fecha_actualizacion " +
						"FROM view_registro_indicadores WHERE ");		
				sql.append("anio IN (").append(String.join(",", (List)params.get("anios"))).append(") ");
				sql.append("AND id_ciclo IN (").append(String.join(",", (List)params.get("ciclos"))).append(") ");				
				if(params.get("institucion") != null) {
					sql.append("AND id_institucion = ").append(params.get("institucion"));
				}
				if(params.get("tipos") != null) {
					sql.append(" AND id_tipo IN (").append(String.join(",", (List)params.get("tipos"))).append(")");
				}
				if(params.get("secciones") != null) {
					sql.append(" AND id_seccion IN (").append(String.join(",", (List)params.get("secciones"))).append(")");
				}
				log.info(report.name() + ": " + sql.toString());
				
				rs = stmt.executeQuery(sql.toString());
				while(rs.next()) {
					var r = new ReporteAvance();
					r.setAnio(rs.getInt("anio"));
					r.setCampus(rs.getString("campus"));
					r.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
					//r.setIdCiclo(rs.getInt("id_ciclo"));
					//r.setIdInstitucion(rs.getInt("id_institucion"));
					r.setIdRegistro(rs.getInt("id_registro"));
					//r.setIdSeccion(rs.getInt("id_seccion"));
					//r.setIdTipo(rs.getInt("id_tipo"));
					r.setIndicador(rs.getString("indicador"));
					r.setPrograma(rs.getString("programa"));
					r.setSeccion(rs.getString("seccion"));
					r.setSiglas(rs.getString("institucion"));
					r.setTemporalidad(rs.getString("temporalidad"));
					r.setTipo(rs.getString("tipo"));
					r.setValor(rs.getString("valor"));
					
					result.add(r);
				}
				break;
			case INSTITUCION:
				result = new ArrayList<ReporteInstitucion>();
				sql = new StringBuilder("SELECT id_institucion,siglas,nombre,domicilio,rector,total_alumnos,total_campus,tel_rectoria,tel_secundario,fecha_fimpes,sitio_web,fundacion, " + 
						"nombrec,cargo,correo,tel_principal, " + 
						"acreditadoras,afiliaciones,temporalidades, " + 
						"i38,i39,i40,i43,i42,i189,i190,i192,anio " +
						"FROM view_reporte_institucion WHERE ");				
				sql.append("anio IN (").append(String.join(",", (List)params.get("anios"))).append(") ");
				sql.append("AND id_ciclo IN (").append(String.join(",", (List)params.get("ciclos"))).append(") ");				
				if(params.get("institucion") != null) {
					sql.append("AND id_institucion = ").append(params.get("institucion"));
				}				
				if(estatus.equalsIgnoreCase("activos")) {
					sql.append(" AND activo_institucion IS TRUE");
				}else if(estatus.equalsIgnoreCase("inactivos")) {
					sql.append(" AND activo_institucion IS FALSE");
				}				
				log.info(report.name() + ": " + sql.toString());
				
				rs = stmt.executeQuery(sql.toString());
				while(rs.next()) {
					var r = new ReporteInstitucion();
					r.setAnio(rs.getInt("anio"));
					r.setIdInstitucion(rs.getInt("id_institucion"));
					r.setNombre(rs.getString("nombre"));					
					r.setDomicilio(rs.getString("domicilio"));
					r.setFechaFimpes(rs.getDate("fecha_fimpes"));
					r.setFundacion(rs.getDate("fundacion"));
					r.setRector(rs.getString("rector"));
					r.setSiglas(rs.getString("siglas"));
					r.setSitioWeb(rs.getString("sitio_web"));
					r.setTelPrincipal(rs.getString("tel_rectoria"));					
					r.setTelSecundario(rs.getString("tel_secundario"));
					r.setTotalCampus(rs.getShort("total_alumnos"));
					r.setTotalAlumnos(rs.getShort("total_alumnos"));
					r.setAcreditadoras(rs.getString("acreditadoras"));
					r.setAfiliaciones(rs.getString("afiliaciones"));
					r.setTemporalidades(rs.getString("temporalidades"));
					r.setNombreResponsable(rs.getString("nombrec"));
					r.setCargoResponsable(rs.getString("cargo"));
					r.setTelResponsable(rs.getString("tel_principal"));
					r.setCorreoResponsable(rs.getString("correo"));
					r.setTotalDocentes(rs.getInt("i38"));
					r.setDocentesHombres(rs.getInt("i39"));
					r.setDocentesMujeres(rs.getInt("i40"));
					r.setDocentesTiempoCompleto(rs.getInt("i43"));
					r.setDocentesMedioTiempo(rs.getInt("i42"));					
					r.setNivelMediaSuperior(rs.getString("i189"));
					r.setTotalAlumnosMediaSuperior(rs.getInt("i190"));
					r.setTotalEgresadosMediaSuperior(rs.getInt("i192"));
					
					result.add(r);
				}
				break;
			case CAMPUS:
				result = new ArrayList<ReporteCampus>();
				sql = new StringBuilder("SELECT id_institucion,nombre_institucion,id_campus,nombre,domicilio,estado,razon_social,rector,tel_principal, " + 
						"i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11,i12,i13,i14,i15,i16,i44,i45,i46,i48,i49,anio " +
						"FROM view_reporte_campus WHERE ");
				sql.append("anio IN (").append(String.join(",", (List)params.get("anios"))).append(") ");
				sql.append("AND id_ciclo IN (").append(String.join(",", (List)params.get("ciclos"))).append(") ");				
				if(params.get("institucion") != null) {
					sql.append("AND id_institucion = ").append(params.get("institucion"));
				}
				if(estatus.equalsIgnoreCase("activos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE");
				}else if(estatus.equalsIgnoreCase("inactivos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS FALSE");
				}				
				log.info(report.name() + ": " + sql.toString());
				
				rs = stmt.executeQuery(sql.toString());
				while(rs.next()) {
					var r = new ReporteCampus();
					r.setAnio(rs.getInt("anio"));
					r.setIdInstitucion(rs.getInt("id_institucion"));
					r.setNombreInstitucion(rs.getString("nombre_institucion"));
					r.setIdCampus(rs.getInt("id_campus"));
					r.setNombre(rs.getString("nombre"));
					r.setRazonSocial(rs.getString("razon_social"));
					r.setEntidadFederativa(rs.getString("estado"));
					r.setDomicilio(rs.getString("domicilio"));
					r.setRector(rs.getString("rector"));
					r.setTelPrincipal(rs.getString("tel_principal"));
					r.setCafeterias(rs.getInt("i1"));
					r.setAreasDeportivas(rs.getInt("i2"));
					r.setLaboratorios(rs.getInt("i3"));
					r.setTalleres(rs.getInt("i4"));
					r.setAuditorios(rs.getInt("i10"));
					r.setBibliotecas(rs.getInt("i5"));
					r.setTitulos(rs.getInt("i6"));
					r.setLibrosElectronicos(rs.getInt("i7"));
					r.setUsoBiblioteca(rs.getInt("i8"));
					r.setColaboracionBiblioteca(rs.getInt("i9"));
					r.setEqComputo(rs.getInt("i11"));
					r.setLicenciasSw(rs.getInt("i12"));
					r.setPersonalProteccionCivil(rs.getInt("i13"));
					r.setTotalAdministrativo(rs.getInt("i14"));
					r.setAdministrativosHombres(rs.getInt("i16"));
					r.setAdministrativosMujeres(rs.getInt("i15"));
					r.setTotalDocentes(rs.getInt("i44"));
					r.setDocentesHombres(rs.getInt("i45"));
					r.setDocentesMujeres(rs.getInt("i46"));
					r.setDocentesTiempoCompleto(rs.getInt("i49"));
					r.setDocentesMedioTiempo(rs.getInt("i48"));
					
					result.add(r);
				}
				break;
			case VINCULACION:
				result = new ArrayList<ReporteVinculacion>();
				sql = new StringBuilder("SELECT id_institucion,nombre_institucion,id_campus,nombre,estado, " + 
						"i182,i183,i184,i185,i186,i187,anio " +
						"FROM view_reporte_campus WHERE ");
				sql.append("anio IN (").append(String.join(",", (List)params.get("anios"))).append(") ");
				sql.append("AND id_ciclo IN (").append(String.join(",", (List)params.get("ciclos"))).append(") ");				
				if(params.get("institucion") != null) {
					sql.append("AND id_institucion = ").append(params.get("institucion"));
				}
				if(estatus.equalsIgnoreCase("activos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE");
				}else if(estatus.equalsIgnoreCase("inactivos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS FALSE");
				}
				log.info(report.name() + ": " + sql.toString());
				
				rs = stmt.executeQuery(sql.toString());
				while(rs.next()) {
					var r = new ReporteVinculacion();
					r.setAnio(rs.getInt("anio"));
					r.setIdInstitucion(rs.getInt("id_institucion"));
					r.setNombreInstitucion(rs.getString("nombre_institucion"));	
					r.setIdCampus(rs.getInt("id_campus"));
					r.setNombre(rs.getString("nombre"));
					r.setEntidadFederativa(rs.getString("estado"));
					r.setConveniosPrivados(rs.getInt("i182"));
					r.setConveniosSociales(rs.getInt("i183"));
					r.setConveniosPublicos(rs.getInt("i184"));
					r.setActividadesVinculacion(rs.getInt("i185"));
					r.setPersonasBeneficiadas(rs.getInt("i186"));
					r.setProyectosIncubadoras(rs.getInt("i187"));
				
					result.add(r);
				}
				break;
			case PROGRAMAS:
				result = new ArrayList<ReporteProgramas>();
				sql = new StringBuilder("SELECT id_institucion,nombre_institucion,id_campus,nombre_campus,estado,id_programa,nombre,nivel,modalidad,clave_plan,numero_rvoe,tipo_rvoe,institucion_rvoe,convenio_rvoe, " + 
						"temporalidad,duracion,enfoque,area_conocimiento,total_creditos,acreditan_internacionales,acreditadoras, " +
						"i17,i18,i19,i21,i22,i23,i24,i25,i26,i27,i28,i29,i31,i32,i33,i34,i35,i36,i37,i83,i84,i85,i87,i88,i89,i90,i91,i192,anio " +
						"FROM view_reporte_programas WHERE ");
				sql.append("anio IN (").append(String.join(",", (List)params.get("anios"))).append(") ");
				sql.append("AND id_ciclo IN (").append(String.join(",", (List)params.get("ciclos"))).append(") ");				
				if(params.get("institucion") != null) {
					sql.append("AND id_institucion = ").append(params.get("institucion"));
				}
				if(estatus.equalsIgnoreCase("activos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE AND activo_programa IS TRUE");
				}else if(estatus.equalsIgnoreCase("inactivos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE AND activo_programa IS FALSE");
				}				
				log.info(report.name() + ": " + sql.toString());
				
				rs = stmt.executeQuery(sql.toString());
				while(rs.next()) {
					var r = new ReporteProgramas();
					r.setAnio(rs.getInt("anio"));
					r.setIdInstitucion(rs.getInt("id_institucion"));
					r.setNombreInstitucion(rs.getString("nombre_institucion"));
					r.setIdCampus(rs.getInt("id_campus"));
					r.setNombreCampus(rs.getString("nombre_campus"));
					r.setIdPrograma(rs.getInt("id_programa"));
					r.setNombre(rs.getString("nombre"));
					r.setEntidadFederativa(rs.getString("estado"));
					r.setNivel(rs.getString("nivel"));
					r.setModalidad(rs.getString("modalidad"));
					r.setClavePlan(rs.getString("clave_plan"));
					r.setNumeroRvoe(rs.getString("numero_rvoe"));
					r.setTipoRvoe(rs.getString("tipo_rvoe"));
					r.setInstitucionRvoe(rs.getString("institucion_rvoe"));
					r.setConvenioRvoe(rs.getString("convenio_rvoe"));
					r.setTemporalidad(rs.getString("temporalidad"));
					r.setDuracion(rs.getShort("duracion"));
					r.setEnfoque(rs.getString("enfoque"));
					r.setAreaConocimiento(rs.getString("area_conocimiento"));
					r.setTotalCreditos(rs.getShort("total_creditos"));					
					r.setAcreditanInternacionales(rs.getBoolean("acreditan_internacionales") ? "SI" : "NO");
					r.setAcreditadoras(rs.getString("acreditadoras"));					
					r.setTotalAlumnos(rs.getInt("i17"));
					r.setAlumnosHombres(rs.getInt("i18"));
					r.setAlumnosMujeres(rs.getInt("i19"));
					r.setMenos19(rs.getInt("i21"));
					r.setEntre20y24(rs.getInt("i22"));
					r.setEntre25y29(rs.getInt("i23"));
					r.setMas30(rs.getInt("i24"));
					r.setDiscapacidad(rs.getInt("i25"));
					r.setExtranjeros(rs.getInt("i26"));
					r.setTotalNuevos(rs.getInt("i27"));
					r.setNuevosHombres(rs.getInt("i28"));
					r.setNuevosMujeres(rs.getInt("i29"));
					r.setNuevosMenos19(rs.getInt("i31"));
					r.setNuevosEntre20y24(rs.getInt("i32"));
					r.setNuevosEntre25y29(rs.getInt("i33"));
					r.setNuevosMas30(rs.getInt("i34"));
					r.setNuevosDiscapacidad(rs.getInt("i35"));
					r.setNuevosExtranjeros(rs.getInt("i36"));
					r.setTotalReingreso(rs.getInt("i83"));
					r.setReingresoHombres(rs.getInt("i84"));
					r.setReingresoMujeres(rs.getInt("i85"));
					r.setReingresoMenos19(rs.getInt("i87"));
					r.setReingresoEntre20y24(rs.getInt("i88"));
					r.setReingresoEntre25y29(rs.getInt("i89"));
					r.setReingresoMas30(rs.getInt("i90"));
					r.setReingresoDiscapacidad(rs.getInt("i91"));					
					r.setReingresoExtranjeros(rs.getInt("i192"));
					r.setProvienenNivelPrevio(rs.getInt("i37"));
					
					result.add(r);
				}
				break;
			case DOCENTES:
				result = new ArrayList<ReporteDocentes>();
				sql = new StringBuilder("SELECT id_institucion,nombre_institucion,id_campus,nombre_campus,estado,id_programa,nombre,nivel,modalidad,temporalidad,area_conocimiento, " +
						"i17,i50,i51,i52,i54,i55,i56,i57,i58,i60,i61,i62,i63,i65,i66,i67,i68,i69,i70,i71,i72,i73,i74,i75,i76,i77,i78,i79,i80,anio " +
						"FROM view_reporte_programas WHERE ");
				sql.append("anio IN (").append(String.join(",", (List)params.get("anios"))).append(") ");
				sql.append("AND id_ciclo IN (").append(String.join(",", (List)params.get("ciclos"))).append(") ");				
				if(params.get("institucion") != null) {
					sql.append("AND id_institucion = ").append(params.get("institucion"));
				}
				if(estatus.equalsIgnoreCase("activos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE AND activo_programa IS TRUE");
				}else if(estatus.equalsIgnoreCase("inactivos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE AND activo_programa IS FALSE");
				}				
				log.info(report.name() + ": " + sql.toString());
				
				rs = stmt.executeQuery(sql.toString());
				while(rs.next()) {
					var r = new ReporteDocentes();
					r.setAnio(rs.getInt("anio"));
					r.setIdInstitucion(rs.getInt("id_institucion"));
					r.setNombreInstitucion(rs.getString("nombre_institucion"));					
					r.setIdCampus(rs.getInt("id_campus"));
					r.setNombreCampus(rs.getString("nombre_campus"));
					r.setIdPrograma(rs.getInt("id_programa"));
					r.setNombre(rs.getString("nombre"));
					r.setEntidadFederativa(rs.getString("estado"));
					r.setNivel(rs.getString("nivel"));
					r.setModalidad(rs.getString("modalidad"));
					r.setTemporalidad(rs.getString("temporalidad"));
					r.setAreaConocimiento(rs.getString("area_conocimiento"));
					r.setTotalAlumnos(rs.getInt("i17"));
					r.setTotalDocentes(rs.getInt("i50"));
					r.setDocentesHombres(rs.getInt("i51"));
					r.setDocentesMujeres(rs.getInt("i52"));
					r.setMenos30(rs.getInt("i54"));
					r.setEntre31y40(rs.getInt("i55"));
					r.setMas50(rs.getInt("i56"));
					r.setDiscapacidad(rs.getInt("i57"));					
					r.setExtranjeros(rs.getInt("i58"));
					r.setAntiguedad0a5(rs.getInt("i60"));					
					r.setAntiguedad6a15(rs.getInt("i61"));
					r.setAntiguedad16a25(rs.getInt("i62"));
					r.setAntiguedadMas26(rs.getInt("i63"));
					r.setDoctores(rs.getInt("i65"));
					r.setMaestros(rs.getInt("i66"));
					r.setEspecialistas(rs.getInt("i67"));
					r.setLicenciados(rs.getInt("i68"));
					r.setOtraFormacion(rs.getInt("i69"));
					r.setTotalTiempoCompleto(rs.getInt("i70"));
					r.setTiempoCompletoHombres(rs.getInt("i71"));
					r.setTiempoCompletoMujeres(rs.getInt("i72"));
					r.setTotalMedioTiempo(rs.getInt("i73"));
					r.setMedioTiempoHombres(rs.getInt("i74"));
					r.setMedioTiempoMujeres(rs.getInt("i75"));
					r.setParticipacionSNI(rs.getInt("i76"));
					r.setFormacionProfesional(rs.getInt("i77"));					
					r.setColaboracionAcademica(rs.getInt("i78"));
					r.setMovilidadNacional(rs.getInt("i79"));
					r.setMovilidadInternacional(rs.getInt("i80"));
					
					result.add(r);
				}
				break;
			case PROCESO:
				result = new ArrayList<ReporteProceso>();
				sql = new StringBuilder("SELECT id_institucion,nombre_institucion,id_campus,nombre_campus,estado,id_programa,nombre,nivel,modalidad,temporalidad,area_conocimiento, " +
						"i81,i82,i92,i93,i94,i95,i96,i97,i98,i99,i100,i101,i102,i103,i104,i105,i106,i108,i109,i110,i111,i112,i113,i114,i115,i116,i117,i118,i119,i120, " +
						"i121,i122,i123,i124,i126,i127,i128,i129,i130,i131,i132,i133,i135,i136,i137,i138,i139,i140,i141,i142,i143,i144,i145,i146,anio " +
						"FROM view_reporte_programas WHERE ");
				sql.append("anio IN (").append(String.join(",", (List)params.get("anios"))).append(") ");
				sql.append("AND id_ciclo IN (").append(String.join(",", (List)params.get("ciclos"))).append(") ");				
				if(params.get("institucion") != null) {
					sql.append("AND id_institucion = ").append(params.get("institucion"));
				}
				if(estatus.equalsIgnoreCase("activos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE AND activo_programa IS TRUE");
				}else if(estatus.equalsIgnoreCase("inactivos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE AND activo_programa IS FALSE");
				}				
				log.info(report.name() + ": " + sql.toString());
				
				rs = stmt.executeQuery(sql.toString());
				while(rs.next()) {
					var r = new ReporteProceso();
					r.setAnio(rs.getInt("anio"));
					r.setIdInstitucion(rs.getInt("id_institucion"));
					r.setNombreInstitucion(rs.getString("nombre_institucion"));					
					r.setIdCampus(rs.getInt("id_campus"));
					r.setNombreCampus(rs.getString("nombre_campus"));
					r.setIdPrograma(rs.getInt("id_programa"));
					r.setNombre(rs.getString("nombre"));
					r.setEntidadFederativa(rs.getString("estado"));
					r.setNivel(rs.getString("nivel"));
					r.setModalidad(rs.getString("modalidad"));
					r.setTemporalidad(rs.getString("temporalidad"));
					r.setAreaConocimiento(rs.getString("area_conocimiento"));
					r.setExamenIngreso(rs.getString("i81"));
					r.setAdmitidos(rs.getInt("i82"));
					r.setTotalRegulares(rs.getInt("i92"));
					r.setRegularesHombres(rs.getInt("i93"));
					r.setRegularesMujeres(rs.getInt("i94"));
					r.setTotalBecados(rs.getInt("i95"));
					r.setBecadosHombres(rs.getInt("i96"));
					r.setBecadosMujeres(rs.getInt("i97"));
					r.setTotalRemediales(rs.getInt("i98"));
					r.setRemedialesHombres(rs.getInt("i99"));
					r.setRemedialesMujeres(rs.getInt("i100"));
					r.setTotalRetencion(rs.getInt("i101"));
					r.setRetencionHombres(rs.getInt("i102"));
					r.setRetencionMujeres(rs.getInt("i103"));
					r.setTotalDesercion(rs.getInt("i104"));
					r.setDesertoresHombres(rs.getInt("i105"));
					r.setDesertoresMujeres(rs.getInt("i106"));
					r.setDesertoresMenos19(rs.getInt("i108"));
					r.setDesertores20a24(rs.getInt("i109"));
					r.setDesertores25a29(rs.getInt("i110"));
					r.setDesertoresMas30(rs.getInt("i111"));
					r.setDesertoresDiscapacidad(rs.getInt("i112"));
					r.setTotalReprobados(rs.getInt("i113"));
					r.setReprobadosHombres(rs.getInt("i114"));
					r.setReprobadosMujeres(rs.getInt("i115"));
					r.setPromedio(rs.getInt("i116"));
					r.setPromedioHombres(rs.getInt("i117"));
					r.setPromedioMujeres(rs.getInt("i118"));
					r.setTotalTrabajan(rs.getInt("i119"));
					r.setTrabajanHombres(rs.getInt("i120"));
					r.setTrabajanMujeres(rs.getInt("i121"));
					r.setTotalNacionales(rs.getInt("i122"));
					r.setNacionalesHombres(rs.getInt("i123"));
					r.setNacionalesMujeres(rs.getInt("i124"));
					r.setNacionalesMenos19(rs.getInt("i126"));
					r.setNacionales20a24(rs.getInt("i127"));
					r.setNacionales25a29(rs.getInt("i128"));
					r.setNacionalesMas30(rs.getInt("i129"));
					r.setNacionalesDiscapacidad(rs.getInt("i130"));
					r.setTotalInternacionales(rs.getInt("i131"));
					r.setInternacionalesHombres(rs.getInt("i132"));
					r.setInternacionalesMujeres(rs.getInt("i133"));
					r.setInternacionalesMenos19(rs.getInt("i135"));
					r.setInternacionales20a24(rs.getInt("i136"));
					r.setInternacionales25a29(rs.getInt("i137"));
					r.setInternacionalesMas30(rs.getInt("i138"));
					r.setInternacionalesDiscapacidad(rs.getInt("i139"));
					r.setTotalComunidad(rs.getInt("i140"));
					r.setComunidadHombres(rs.getInt("i141"));
					r.setComunidadMujeres(rs.getInt("i142"));
					r.setTotalServicioSocial(rs.getInt("i143"));
					r.setServicioSocialHombres(rs.getInt("i144"));
					r.setServicioSocialMujeres(rs.getInt("i145"));
					r.setPracticasProfesionales(rs.getString("i146"));
					
					result.add(r);
				}
				break;
			case RESULTADOS:
				result = new ArrayList<ReporteResultados>();
				sql = new StringBuilder("SELECT id_institucion,nombre_institucion,id_campus,nombre_campus,estado,id_programa,nombre,nivel,modalidad,temporalidad,area_conocimiento, " +
						"i17,i147,i148,i149,i151,i152,i153,i154,i155,i156,i157,i158,i160,i161,i162,i163,i164,i165,i166,i167,i168,i169,i170,i171,i172,i173,i174,i176,i177,i178,i179,i180,i181,anio " +
						"FROM view_reporte_programas WHERE ");
				sql.append("anio IN (").append(String.join(",", (List)params.get("anios"))).append(") ");
				sql.append("AND id_ciclo IN (").append(String.join(",", (List)params.get("ciclos"))).append(") ");				
				if(params.get("institucion") != null) {
					sql.append("AND id_institucion = ").append(params.get("institucion"));
				}
				if(estatus.equalsIgnoreCase("activos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE AND activo_programa IS TRUE");
				}else if(estatus.equalsIgnoreCase("inactivos")) {
					sql.append(" AND activo_institucion IS TRUE AND activo_campus IS TRUE AND activo_programa IS FALSE");
				}				
				log.info(report.name() + ": " + sql.toString());
				
				rs = stmt.executeQuery(sql.toString());
				while(rs.next()) {
					var r = new ReporteResultados();
					r.setAnio(rs.getInt("anio"));
					r.setIdInstitucion(rs.getInt("id_institucion"));
					r.setNombreInstitucion(rs.getString("nombre_institucion"));					
					r.setIdCampus(rs.getInt("id_campus"));
					r.setNombreCampus(rs.getString("nombre_campus"));
					r.setIdPrograma(rs.getInt("id_programa"));
					r.setNombre(rs.getString("nombre"));
					r.setEntidadFederativa(rs.getString("estado"));
					r.setNivel(rs.getString("nivel"));
					r.setModalidad(rs.getString("modalidad"));
					r.setTemporalidad(rs.getString("temporalidad"));
					r.setAreaConocimiento(rs.getString("area_conocimiento"));
					r.setTotalAlumnos(rs.getInt("i17"));			
					r.setTotalUltimoGrado(rs.getInt("i147"));
					r.setUltimoGradoHombres(rs.getInt("i148"));
					r.setUltimoGradoMujeres(rs.getInt("i149"));
					r.setUltimoGradoMenos19(rs.getInt("i151"));					
					r.setUltimoGrado20a24(rs.getInt("i152"));
					r.setUltimoGrado25a29(rs.getInt("i153"));
					r.setUltimoGradoMas30(rs.getInt("i154"));
					r.setUltimoGradoDiscapacidad(rs.getInt("i155"));
					r.setTotalEgresados(rs.getInt("i156"));
					r.setEgresadosHombres(rs.getInt("i157"));
					r.setEgresadosMujeres(rs.getInt("i158"));
					r.setEgresadosMenos19(rs.getInt("i160"));
					r.setEgresados20a24(rs.getInt("i161"));
					r.setEgresados25a29(rs.getInt("i162"));
					r.setEgresadosMas30(rs.getInt("i163"));
					r.setEgresadosDiscapacidad(rs.getInt("i164"));
					r.setPromedioEgresados(rs.getInt("i165"));
					r.setRealizanEGEL(rs.getInt("i166"));
					r.setApruebanEGEL(rs.getInt("i167"));
					r.setTitulados(rs.getInt("i168"));
					r.setEficienciaTerminal(rs.getInt("i169"));
					r.setDobleTitulacion(rs.getString("i170"));
					r.setSeguimientoEgresados(rs.getString("i171"));
					r.setTotalTrabajan(rs.getInt("i172"));
					r.setTrabajanHombres(rs.getInt("i173"));
					r.setTrabajanMujeres(rs.getInt("i174"));
					r.setTrabajanMenos19(rs.getInt("i176"));
					r.setTrabajan20a24(rs.getInt("i177"));
					r.setTrabajan25a29(rs.getInt("i178"));
					r.setTrabajanMas30(rs.getInt("i179"));
					r.setTrabajanDiscapacidad(rs.getInt("i180"));					
					r.setPorcentajeTrabajan(rs.getInt("i181"));
					
					result.add(r);
				}
				break;
			default:
				break;
			}
		}catch(SQLException e) {
			log.error(e.getMessage());
			throw e;
		}finally {
			if(rs != null && !rs.isClosed()) {
				rs.close();
			}
			if(!stmt.isClosed()) {
				stmt.close();
			}
		}
		
		return result;
    }
    
    public byte[] getExcelLayout(Integer idInstitucion) throws Exception {
    	@SuppressWarnings("resource")
		var book = new HSSFWorkbook();
    	var output = new ByteArrayOutputStream();
    	Statement stmt = null;
		ResultSet rs = null;
    	try {
    		stmt = dataSource.getConnection().createStatement();
    		// Indicadores de Institución:
    		var sql = "SELECT a.id_institucion AS ID_INSTITUCION,a.siglas AS SIGLAS,a.nombre AS INSTITUCION, d.id_seccion AS ID_SECCION,d.descripcion AS SECCION, " + 
    		        "c.id_indicador AS ID_INDICADOR,c.etiqueta AS ETIQUETA,c.descripcion AS DESCRIPCION,c.explicacion AS EXPLICACION,CASE WHEN c.requerido=1 THEN '*' ELSE '' END AS REQUERIDO,NULL AS VALOR " + 
    				"FROM instituciones a ,indicadores c " + 
    				"INNER JOIN tipo_indicador b ON b.id_tipo=c.id_tipo " + 
    				"INNER JOIN seccion d ON b.id_tipo=d.id_tipo AND c.id_seccion=d.id_seccion " + 
    				"WHERE b.id_tipo='1' AND c.activo = true AND a.id_institucion = '" + idInstitucion + "'";
			rs = stmt.executeQuery(sql);
		    fillInSheet(rs, book.createSheet("Institucion"));
		    rs.close();
    		
    		// Indicadores de Campus:
    		sql = "SELECT DISTINCT a.id_institucion AS ID_INSTITUCION,a.siglas AS SIGLAS,a.nombre AS INSTITUCION,b.id_campus AS ID_CAMPUS,b.nombre AS CAMPUS,d.id_seccion AS ID_SECCION,d.descripcion AS SECCION, " + 
    		        "e.id_indicador AS ID_INDICADOR,e.etiqueta AS ETIQUETA,e.descripcion AS DESCRIPCION,e.explicacion AS EXPLICACION,CASE WHEN e.requerido=1 THEN '*' ELSE '' END AS REQUERIDO,NULL AS VALOR " + 
    				"FROM instituciones a " + 
    				"INNER JOIN campus b ON a.id_institucion=b.id_institucion " + 
    				"INNER JOIN tipo_indicador c ON c.id_tipo='2' " + 
    				"INNER JOIN seccion d ON d.id_tipo=c.id_tipo " + 
    				"INNER JOIN indicadores e ON e.id_seccion=d.id_seccion AND e.id_tipo=c.id_tipo " + 
    				"WHERE e.activo = true AND a.id_institucion = '" + idInstitucion + "'";
    		rs = stmt.executeQuery(sql);
			fillInSheet(rs, book.createSheet("Campus"));
			rs.close();
    		
    		// Indicadores de Programa:
    		sql = "SELECT DISTINCT a.id_institucion AS ID_INSTITUCION,a.siglas AS SIGLAS,a.nombre AS INSTITUCION,b.id_campus AS ID_CAMPUS,b.nombre AS CAMPUS,f.id_programa AS ID_PROGRAMA,f.nombre AS PROGRAMA,d.id_seccion AS ID_SECCION,d.descripcion AS SECCION, " + 
    		        "e.id_indicador AS ID_INDICADOR,e.etiqueta AS ETIQUETA,e.descripcion AS DESCRIPCION,e.explicacion AS EXPLICACION,CASE WHEN e.requerido=1 THEN '*' ELSE '' END AS REQUERIDO,NULL AS VALOR " + 
    				"FROM instituciones a " + 
    				"INNER JOIN campus b ON a.id_institucion=b.id_institucion " + 
    				"INNER JOIN tipo_indicador c ON c.id_tipo='3' " + 
    				"INNER JOIN seccion d ON d.id_tipo=c.id_tipo " + 
    				"INNER JOIN indicadores e ON e.id_seccion=d.id_seccion AND e.id_tipo=c.id_tipo " + 
    				"INNER JOIN programas f ON f.id_campus=b.id_campus " + 
    				"WHERE e.activo = true AND a.id_institucion = '" + idInstitucion + "'";
    		rs = stmt.executeQuery(sql);
			fillInSheet(rs, book.createSheet("Programas"));
			rs.close();
    	}catch(SQLException e) {
			log.error(e.getMessage());
			throw e;
		}finally {			
			if(!rs.isClosed()) {
				rs.close();
			}
			if(!stmt.isClosed()) {
				stmt.close();
			}
			try {
			    book.write(output);
			} finally {
			    output.close();
			}
		}
    	
    	return output.toByteArray();
    }
    
    private void fillInSheet(ResultSet result, HSSFSheet sheet) throws SQLException {
    	var meta = result.getMetaData();
 	   	int columns = meta.getColumnCount();
 	   	sheet.protectSheet("f1mp35");
 	   	var unlockedCellStyle = sheet.getWorkbook().createCellStyle();
 	   	unlockedCellStyle.setLocked(false);
    	// Header:
    	var header = sheet.createRow(0);
        for(int col=0 ;col < columns;col++) {
            var cell = header.createCell(col);
           	cell.setCellValue(meta.getColumnLabel(col+1));
        }
        // Content:
        while (result.next()) {
        	var row = sheet.createRow(result.getRow());
            for(int col=0 ; col<columns; col++) {
                var cell = row.createCell(col);
                var value = result.getString(col+1);                
                try {
                	var num = Integer.parseInt(value);
                	cell.setCellType(CellType.NUMERIC);
                	cell.setCellValue(num);
                }catch(Exception e) {
                	cell.setCellType(CellType.STRING);
                	cell.setCellValue(value);
                }
                if(col == columns-1) {
                	// We only allow the VALOR column to be editable:
                	cell.setCellStyle(unlockedCellStyle);
                }
            }
        }
        // Columns adjustment:
        for(int col=0 ;col < columns;col++) {
            sheet.autoSizeColumn(col);
        }
    }
}
