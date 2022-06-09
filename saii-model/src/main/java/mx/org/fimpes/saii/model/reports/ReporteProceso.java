package mx.org.fimpes.saii.model.reports;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public class ReporteProceso {
	private Integer idInstitucion;
	private String nombreInstitucion;
	private Integer idCampus;
	private String nombreCampus;
	private String entidadFederativa;
	private Integer idPrograma;
	private String nombre;
	private String areaConocimiento;
	private String modalidad;
	private String nivel;
	private String temporalidad;
	private String examenIngreso;
	private Integer admitidos;
	private Integer totalRegulares;
	private Integer regularesHombres;
	private Integer regularesMujeres;
	private Integer totalBecados;
	private Integer becadosHombres;
	private Integer becadosMujeres;
	private Integer totalRemediales;
	private Integer remedialesHombres;
	private Integer remedialesMujeres;
	private Integer totalRetencion;
	private Integer retencionHombres;
	private Integer retencionMujeres;
	private Integer totalDesercion;
	private Integer desertoresHombres;
	private Integer desertoresMujeres;
	private Integer desertoresMenos19;
	private Integer desertores20a24;
	private Integer desertores25a29;
	private Integer desertoresMas30;
	private Integer desertoresDiscapacidad;
	private Integer totalReprobados;
	private Integer reprobadosHombres;
	private Integer reprobadosMujeres;
	private Integer promedio;
	private Integer promedioHombres;
	private Integer promedioMujeres;
	private Integer totalTrabajan;
	private Integer trabajanHombres;
	private Integer trabajanMujeres;
	private Integer totalNacionales;
	private Integer nacionalesHombres;
	private Integer nacionalesMujeres;
	private Integer nacionalesMenos19;
	private Integer nacionales20a24;
	private Integer nacionales25a29;
	private Integer nacionalesMas30;
	private Integer nacionalesDiscapacidad;
	private Integer totalInternacionales;
	private Integer internacionalesHombres;
	private Integer internacionalesMujeres;
	private Integer internacionalesMenos19;
	private Integer internacionales20a24;
	private Integer internacionales25a29;
	private Integer internacionalesMas30;
	private Integer internacionalesDiscapacidad;
	private Integer totalComunidad;
	private Integer comunidadHombres;
	private Integer comunidadMujeres;
	private Integer totalServicioSocial;
	private Integer servicioSocialHombres;
	private Integer servicioSocialMujeres;
	private String practicasProfesionales;
	private Integer anio;
	
	public Integer getIdInstitucion() {
		return idInstitucion;
	}
	public void setIdInstitucion(Integer idInstitucion) {
		this.idInstitucion = idInstitucion;
	}
	public String getNombreInstitucion() {
		return nombreInstitucion;
	}
	public void setNombreInstitucion(String nombreInstitucion) {
		this.nombreInstitucion = nombreInstitucion;
	}
	public Integer getIdCampus() {
		return idCampus;
	}
	public void setIdCampus(Integer idCampus) {
		this.idCampus = idCampus;
	}
	public String getNombreCampus() {
		return nombreCampus;
	}
	public void setNombreCampus(String nombreCampus) {
		this.nombreCampus = nombreCampus;
	}
	public String getEntidadFederativa() {
		return entidadFederativa;
	}
	public void setEntidadFederativa(String entidadFederativa) {
		this.entidadFederativa = entidadFederativa;
	}
	public Integer getIdPrograma() {
		return idPrograma;
	}
	public void setIdPrograma(Integer idPrograma) {
		this.idPrograma = idPrograma;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getAreaConocimiento() {
		return areaConocimiento;
	}
	public void setAreaConocimiento(String areaConocimiento) {
		this.areaConocimiento = areaConocimiento;
	}
	public String getModalidad() {
		return modalidad;
	}
	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}
	public String getNivel() {
		return nivel;
	}
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
	public String getTemporalidad() {
		return temporalidad;
	}
	public void setTemporalidad(String temporalidad) {
		this.temporalidad = temporalidad;
	}
	public String getExamenIngreso() {
		return examenIngreso;
	}
	public void setExamenIngreso(String examenIngreso) {
		this.examenIngreso = examenIngreso;
	}
	public Integer getAdmitidos() {
		return admitidos;
	}
	public void setAdmitidos(Integer admitidos) {
		this.admitidos = admitidos;
	}
	public Integer getTotalRegulares() {
		return totalRegulares;
	}
	public void setTotalRegulares(Integer totalRegulares) {
		this.totalRegulares = totalRegulares;
	}
	public Integer getRegularesHombres() {
		return regularesHombres;
	}
	public void setRegularesHombres(Integer regularesHombres) {
		this.regularesHombres = regularesHombres;
	}
	public Integer getRegularesMujeres() {
		return regularesMujeres;
	}
	public void setRegularesMujeres(Integer regularesMujeres) {
		this.regularesMujeres = regularesMujeres;
	}
	public Integer getTotalBecados() {
		return totalBecados;
	}
	public void setTotalBecados(Integer totalBecados) {
		this.totalBecados = totalBecados;
	}
	public Integer getBecadosHombres() {
		return becadosHombres;
	}
	public void setBecadosHombres(Integer becadosHombres) {
		this.becadosHombres = becadosHombres;
	}
	public Integer getBecadosMujeres() {
		return becadosMujeres;
	}
	public void setBecadosMujeres(Integer becadosMujeres) {
		this.becadosMujeres = becadosMujeres;
	}
	public Integer getTotalRemediales() {
		return totalRemediales;
	}
	public void setTotalRemediales(Integer totalRemediales) {
		this.totalRemediales = totalRemediales;
	}
	public Integer getRemedialesHombres() {
		return remedialesHombres;
	}
	public void setRemedialesHombres(Integer remedialesHombres) {
		this.remedialesHombres = remedialesHombres;
	}
	public Integer getRemedialesMujeres() {
		return remedialesMujeres;
	}
	public void setRemedialesMujeres(Integer remedialesMujeres) {
		this.remedialesMujeres = remedialesMujeres;
	}
	public Integer getTotalRetencion() {
		return totalRetencion;
	}
	public void setTotalRetencion(Integer totalRetencion) {
		this.totalRetencion = totalRetencion;
	}
	public Integer getRetencionHombres() {
		return retencionHombres;
	}
	public void setRetencionHombres(Integer retencionHombres) {
		this.retencionHombres = retencionHombres;
	}
	public Integer getRetencionMujeres() {
		return retencionMujeres;
	}
	public void setRetencionMujeres(Integer retencionMujeres) {
		this.retencionMujeres = retencionMujeres;
	}
	public Integer getTotalDesercion() {
		return totalDesercion;
	}
	public void setTotalDesercion(Integer totalDesercion) {
		this.totalDesercion = totalDesercion;
	}
	public Integer getDesertoresHombres() {
		return desertoresHombres;
	}
	public void setDesertoresHombres(Integer desertoresHombres) {
		this.desertoresHombres = desertoresHombres;
	}
	public Integer getDesertoresMujeres() {
		return desertoresMujeres;
	}
	public void setDesertoresMujeres(Integer desertoresMujeres) {
		this.desertoresMujeres = desertoresMujeres;
	}
	public Integer getDesertoresMenos19() {
		return desertoresMenos19;
	}
	public void setDesertoresMenos19(Integer desertoresMenos19) {
		this.desertoresMenos19 = desertoresMenos19;
	}
	public Integer getDesertores20a24() {
		return desertores20a24;
	}
	public void setDesertores20a24(Integer desertores20a24) {
		this.desertores20a24 = desertores20a24;
	}
	public Integer getDesertores25a29() {
		return desertores25a29;
	}
	public void setDesertores25a29(Integer desertores25a29) {
		this.desertores25a29 = desertores25a29;
	}
	public Integer getDesertoresMas30() {
		return desertoresMas30;
	}
	public void setDesertoresMas30(Integer desertoresMas30) {
		this.desertoresMas30 = desertoresMas30;
	}
	public Integer getDesertoresDiscapacidad() {
		return desertoresDiscapacidad;
	}
	public void setDesertoresDiscapacidad(Integer desertoresDiscapacidad) {
		this.desertoresDiscapacidad = desertoresDiscapacidad;
	}
	public Integer getTotalReprobados() {
		return totalReprobados;
	}
	public void setTotalReprobados(Integer totalReprobados) {
		this.totalReprobados = totalReprobados;
	}
	public Integer getReprobadosHombres() {
		return reprobadosHombres;
	}
	public void setReprobadosHombres(Integer reprobadosHombres) {
		this.reprobadosHombres = reprobadosHombres;
	}
	public Integer getReprobadosMujeres() {
		return reprobadosMujeres;
	}
	public void setReprobadosMujeres(Integer reprobadosMujeres) {
		this.reprobadosMujeres = reprobadosMujeres;
	}
	public Integer getPromedio() {
		return promedio;
	}
	public void setPromedio(Integer promedio) {
		this.promedio = promedio;
	}
	public Integer getPromedioHombres() {
		return promedioHombres;
	}
	public void setPromedioHombres(Integer promedioHombres) {
		this.promedioHombres = promedioHombres;
	}
	public Integer getPromedioMujeres() {
		return promedioMujeres;
	}
	public void setPromedioMujeres(Integer promedioMujeres) {
		this.promedioMujeres = promedioMujeres;
	}
	public Integer getTotalTrabajan() {
		return totalTrabajan;
	}
	public void setTotalTrabajan(Integer totalTrabajan) {
		this.totalTrabajan = totalTrabajan;
	}
	public Integer getTrabajanHombres() {
		return trabajanHombres;
	}
	public void setTrabajanHombres(Integer trabajanHombres) {
		this.trabajanHombres = trabajanHombres;
	}
	public Integer getTrabajanMujeres() {
		return trabajanMujeres;
	}
	public void setTrabajanMujeres(Integer trabajanMujeres) {
		this.trabajanMujeres = trabajanMujeres;
	}
	public Integer getTotalNacionales() {
		return totalNacionales;
	}
	public void setTotalNacionales(Integer totalNacionales) {
		this.totalNacionales = totalNacionales;
	}
	public Integer getNacionalesHombres() {
		return nacionalesHombres;
	}
	public void setNacionalesHombres(Integer nacionalesHombres) {
		this.nacionalesHombres = nacionalesHombres;
	}
	public Integer getNacionalesMujeres() {
		return nacionalesMujeres;
	}
	public void setNacionalesMujeres(Integer nacionalesMujeres) {
		this.nacionalesMujeres = nacionalesMujeres;
	}
	public Integer getNacionalesMenos19() {
		return nacionalesMenos19;
	}
	public void setNacionalesMenos19(Integer nacionalesMenos19) {
		this.nacionalesMenos19 = nacionalesMenos19;
	}
	public Integer getNacionales20a24() {
		return nacionales20a24;
	}
	public void setNacionales20a24(Integer nacionales20a24) {
		this.nacionales20a24 = nacionales20a24;
	}
	public Integer getNacionales25a29() {
		return nacionales25a29;
	}
	public void setNacionales25a29(Integer nacionales25a29) {
		this.nacionales25a29 = nacionales25a29;
	}
	public Integer getNacionalesMas30() {
		return nacionalesMas30;
	}
	public void setNacionalesMas30(Integer nacionalesMas30) {
		this.nacionalesMas30 = nacionalesMas30;
	}
	public Integer getNacionalesDiscapacidad() {
		return nacionalesDiscapacidad;
	}
	public void setNacionalesDiscapacidad(Integer nacionalesDiscapacidad) {
		this.nacionalesDiscapacidad = nacionalesDiscapacidad;
	}
	public Integer getTotalInternacionales() {
		return totalInternacionales;
	}
	public void setTotalInternacionales(Integer totalInternacionales) {
		this.totalInternacionales = totalInternacionales;
	}
	public Integer getInternacionalesHombres() {
		return internacionalesHombres;
	}
	public void setInternacionalesHombres(Integer internacionalesHombres) {
		this.internacionalesHombres = internacionalesHombres;
	}
	public Integer getInternacionalesMujeres() {
		return internacionalesMujeres;
	}
	public void setInternacionalesMujeres(Integer internacionalesMujeres) {
		this.internacionalesMujeres = internacionalesMujeres;
	}
	public Integer getInternacionalesMenos19() {
		return internacionalesMenos19;
	}
	public void setInternacionalesMenos19(Integer internacionalesMenos19) {
		this.internacionalesMenos19 = internacionalesMenos19;
	}
	public Integer getInternacionales20a24() {
		return internacionales20a24;
	}
	public void setInternacionales20a24(Integer internacionales20a24) {
		this.internacionales20a24 = internacionales20a24;
	}
	public Integer getInternacionales25a29() {
		return internacionales25a29;
	}
	public void setInternacionales25a29(Integer internacionales25a29) {
		this.internacionales25a29 = internacionales25a29;
	}
	public Integer getInternacionalesMas30() {
		return internacionalesMas30;
	}
	public void setInternacionalesMas30(Integer internacionalesMas30) {
		this.internacionalesMas30 = internacionalesMas30;
	}
	public Integer getInternacionalesDiscapacidad() {
		return internacionalesDiscapacidad;
	}
	public void setInternacionalesDiscapacidad(Integer internacionalesDiscapacidad) {
		this.internacionalesDiscapacidad = internacionalesDiscapacidad;
	}
	public Integer getTotalComunidad() {
		return totalComunidad;
	}
	public void setTotalComunidad(Integer totalComunidad) {
		this.totalComunidad = totalComunidad;
	}
	public Integer getComunidadHombres() {
		return comunidadHombres;
	}
	public void setComunidadHombres(Integer comunidadHombres) {
		this.comunidadHombres = comunidadHombres;
	}
	public Integer getComunidadMujeres() {
		return comunidadMujeres;
	}
	public void setComunidadMujeres(Integer comunidadMujeres) {
		this.comunidadMujeres = comunidadMujeres;
	}
	public Integer getTotalServicioSocial() {
		return totalServicioSocial;
	}
	public void setTotalServicioSocial(Integer totalServicioSocial) {
		this.totalServicioSocial = totalServicioSocial;
	}
	public Integer getServicioSocialHombres() {
		return servicioSocialHombres;
	}
	public void setServicioSocialHombres(Integer servicioSocialHombres) {
		this.servicioSocialHombres = servicioSocialHombres;
	}
	public Integer getServicioSocialMujeres() {
		return servicioSocialMujeres;
	}
	public void setServicioSocialMujeres(Integer servicioSocialMujeres) {
		this.servicioSocialMujeres = servicioSocialMujeres;
	}
	public String getPracticasProfesionales() {
		return practicasProfesionales;
	}
	public void setPracticasProfesionales(String practicasProfesionales) {
		this.practicasProfesionales = practicasProfesionales;
	}
	public Integer getAnio() {
		return anio;
	}
	public void setAnio(Integer anio) {
		this.anio = anio;
	}
}
