package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import mx.org.fimpes.saii.model.security.Usuarios;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "instituciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Instituciones.findByNombre", query = "SELECT i FROM Instituciones i WHERE i.nombre = :nombre")})
public class Instituciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_institucion")
    private Integer idInstitucion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "siglas")
    private String siglas;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "rector")
    private String rector;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fundacion")
    @Temporal(TemporalType.DATE)
    private Date fundacion;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "domicilio")
    private String domicilio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "tel_principal")
    private String telPrincipal;
    @Size(max = 45)
    @Column(name = "tel_secundario")
    private String telSecundario;
    @Size(max = 45)
    @Column(name = "sitio_web")
    private String sitioWeb;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_campus")
    private short totalCampus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_alumnos")
    private int totalAlumnos;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_programas")
    private short totalProgramas;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_fimpes")
    @Temporal(TemporalType.DATE)
    private Date fechaFimpes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activa")
    private boolean activa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    
    @JsonManagedReference
    @JoinTable(name = "instituciones_has_afiliaciones", joinColumns = {
        @JoinColumn(name = "id_institucion", referencedColumnName = "id_institucion")}, inverseJoinColumns = {
        @JoinColumn(name = "id_afiliacion", referencedColumnName = "id_afiliacion")})
    @ManyToMany
    private List<Afiliaciones> afiliacionesList;
    
    @JsonManagedReference
    @ManyToMany(mappedBy = "institucionesList")
    private List<Acreditadoras> acreditadorasList;
    
    @JsonManagedReference
    @JoinTable(name = "instituciones_has_temporalidades", joinColumns = {
        @JoinColumn(name = "id_institucion", referencedColumnName = "id_institucion")}, inverseJoinColumns = {
        @JoinColumn(name = "id_temporalidad", referencedColumnName = "id_temporalidad")})
    @ManyToMany
    private List<Temporalidades> temporalidadesList;
    
    @JsonManagedReference
    @JoinColumn(name = "id_status", referencedColumnName = "id_status")
    @ManyToOne(optional = false)
    private StatusFimpes idStatus;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idInstitucion")
    private List<PeriodoAcceso> periodoAccesoList;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idInstitucion")
    private List<ProcesoCaptura> procesoCapturaList;
    
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idInstitucion")
    private List<Campus> campusList;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idInstitucion")
    private List<RegistroIndicadores> registroIndicadoresList;
        
    @JsonBackReference
    @OneToMany(mappedBy = "idInstitucion")
    private List<Usuarios> usuariosList;

    public Instituciones() {
    	this.activa = true;
    }

    public Instituciones(Integer idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public Instituciones(Integer idInstitucion, String siglas, String nombre, String rector, Date fundacion, String domicilio, String telPrincipal, short totalCampus, short totalAlumnos, short totalProgramas, Date fechaFimpes, boolean activa, Date fechaActualizacion) {
        this.idInstitucion = idInstitucion;
        this.siglas = siglas;
        this.nombre = nombre;
        this.rector = rector;
        this.fundacion = fundacion;
        this.domicilio = domicilio;
        this.telPrincipal = telPrincipal;
        this.totalCampus = totalCampus;
        this.totalAlumnos = totalAlumnos;
        this.totalProgramas = totalProgramas;
        this.fechaFimpes = fechaFimpes;
        this.activa = activa;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(Integer idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getSiglas() {
        return siglas;
    }

    public void setSiglas(String siglas) {
        this.siglas = siglas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRector() {
        return rector;
    }

    public void setRector(String rector) {
        this.rector = rector;
    }

    public Date getFundacion() {
        return fundacion;
    }

    public void setFundacion(Date fundacion) {
        this.fundacion = fundacion;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelPrincipal() {
        return telPrincipal;
    }

    public void setTelPrincipal(String telPrincipal) {
        this.telPrincipal = telPrincipal;
    }

    public String getTelSecundario() {
        return telSecundario;
    }

    public void setTelSecundario(String telSecundario) {
        this.telSecundario = telSecundario;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public short getTotalCampus() {
        return totalCampus;
    }

    public void setTotalCampus(short totalCampus) {
        this.totalCampus = totalCampus;
    }

    public int getTotalAlumnos() {
        return totalAlumnos;
    }

    public void setTotalAlumnos(int totalAlumnos) {
        this.totalAlumnos = totalAlumnos;
    }

    public short getTotalProgramas() {
        return totalProgramas;
    }

    public void setTotalProgramas(short totalProgramas) {
        this.totalProgramas = totalProgramas;
    }

    public Date getFechaFimpes() {
        return fechaFimpes;
    }

    public void setFechaFimpes(Date fechaFimpes) {
        this.fechaFimpes = fechaFimpes;
    }

    public boolean getActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @XmlTransient
    public List<Afiliaciones> getAfiliacionesList() {
        return afiliacionesList;
    }

    public void setAfiliacionesList(List<Afiliaciones> afiliacionesList) {
        this.afiliacionesList = afiliacionesList;
    }

    @XmlTransient
    public List<Acreditadoras> getAcreditadorasList() {
        return acreditadorasList;
    }

    public void setAcreditadorasList(List<Acreditadoras> acreditadorasList) {
        this.acreditadorasList = acreditadorasList;
    }

    @XmlTransient
    public List<Temporalidades> getTemporalidadesList() {
        return temporalidadesList;
    }

    public void setTemporalidadesList(List<Temporalidades> temporalidadesList) {
        this.temporalidadesList = temporalidadesList;
    }

    public StatusFimpes getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(StatusFimpes idStatus) {
        this.idStatus = idStatus;
    }
    
    @XmlTransient
    public List<PeriodoAcceso> getPeriodoAccesoList() {
        return periodoAccesoList;
    }

    public void setPeriodoAccesoList(List<PeriodoAcceso> periodoAccesoList) {
        this.periodoAccesoList = periodoAccesoList;
    }

    @XmlTransient
    public List<ProcesoCaptura> getProcesoCapturaList() {
        return procesoCapturaList;
    }

    public void setProcesoCapturaList(List<ProcesoCaptura> procesoCapturaList) {
        this.procesoCapturaList = procesoCapturaList;
    }

    @XmlTransient
    public List<Campus> getCampusList() {
        return campusList;
    }

    public void setCampusList(List<Campus> campusList) {
        this.campusList = campusList;
    }

    @XmlTransient
    public List<RegistroIndicadores> getRegistroIndicadoresList() {
        return registroIndicadoresList;
    }

    public void setRegistroIndicadoresList(List<RegistroIndicadores> registroIndicadoresList) {
        this.registroIndicadoresList = registroIndicadoresList;
    }

    @XmlTransient
    public List<Usuarios> getUsuariosList() {
        return usuariosList;
    }

    public void setUsuariosList(List<Usuarios> usuariosList) {
        this.usuariosList = usuariosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idInstitucion != null ? idInstitucion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Instituciones)) {
            return false;
        }
        Instituciones other = (Instituciones) object;
        if ((this.idInstitucion == null && other.idInstitucion != null) || (this.idInstitucion != null && !this.idInstitucion.equals(other.idInstitucion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idInstitucion + " - " + nombre;
    }
}
