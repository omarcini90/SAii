package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "programas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Programas.findByNombre", query = "SELECT p FROM Programas p WHERE p.nombre = :nombre")})
public class Programas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_programa")
    private Integer idPrograma;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 45)
    @Column(name = "clave_plan")
    private String clavePlan;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duracion")
    private short duracion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_creditos")
    private short totalCreditos;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "numero_rvoe")
    private String numeroRvoe;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "institucion_rvoe")
    private String institucionRvoe;
    @Size(max = 45)
    @Column(name = "convenio_rvoe")
    private String convenioRvoe;
    @Size(max = 45)
    @Column(name = "responsable_rvoe")
    private String responsableRvoe;
    @Basic(optional = false)
    @NotNull
    @Column(name = "acreditan_internacionales")
    private boolean acreditanInternacionales;
    @Basic(optional = false)
    @NotNull
    @Column(name = "liquidacion")
    private boolean liquidacion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activo")
    private boolean activo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    
    @JsonManagedReference
    @ManyToMany(mappedBy = "programasList")
    private List<Acreditadoras> acreditadorasList;
    
    @JsonIgnore
    @OneToMany(mappedBy = "idPrograma")
    private List<RegistroIndicadores> registroIndicadoresList;
    
    @JsonManagedReference
    @JoinColumn(name = "id_area_conocimiento", referencedColumnName = "id_area_conocimiento")
    @ManyToOne(optional = false)
    private AreaConocimiento idAreaConocimiento;
    
    @JsonBackReference
    @JoinColumn(name = "id_campus", referencedColumnName = "id_campus")
    @ManyToOne(optional = false)
    private Campus idCampus;
    
    @JsonManagedReference
    @JoinColumn(name = "id_enfoque", referencedColumnName = "id_enfoque")
    @ManyToOne(optional = false)
    private Enfoque idEnfoque;
    
    @JsonManagedReference
    @JoinColumn(name = "id_modalidad", referencedColumnName = "id_modalidad")
    @ManyToOne(optional = false)
    private Modalidad idModalidad;
    
    @JsonManagedReference
    @JoinColumn(name = "id_nivel", referencedColumnName = "id_nivel")
    @ManyToOne(optional = false)
    private NivelAcademico idNivel;
    
    @JsonManagedReference
    @JoinColumn(name = "id_temporalidad", referencedColumnName = "id_temporalidad")
    @ManyToOne(optional = false)
    private Temporalidades idTemporalidad;
    
    @JsonManagedReference
    @JoinColumn(name = "id_tipo_rvoe", referencedColumnName = "id_tipo_rvoe")
    @ManyToOne(optional = false)
    private TipoRvoe idTipoRvoe;

    public Programas() {
    }

    public Programas(Integer idPrograma) {
        this.idPrograma = idPrograma;
    }

    public Programas(Integer idPrograma, String nombre, short duracion, short totalCreditos, String numeroRvoe, String institucionRvoe, boolean acreditanInternacionales, boolean liquidacion, boolean activo, Date fechaActualizacion) {
        this.idPrograma = idPrograma;
        this.nombre = nombre;
        this.duracion = duracion;
        this.totalCreditos = totalCreditos;
        this.numeroRvoe = numeroRvoe;
        this.institucionRvoe = institucionRvoe;
        this.acreditanInternacionales = acreditanInternacionales;
        this.liquidacion = liquidacion;
        this.activo = activo;
        this.fechaActualizacion = fechaActualizacion;
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

    public String getClavePlan() {
        return clavePlan;
    }

    public void setClavePlan(String clavePlan) {
        this.clavePlan = clavePlan;
    }

    public short getDuracion() {
        return duracion;
    }

    public void setDuracion(short duracion) {
        this.duracion = duracion;
    }

    public short getTotalCreditos() {
        return totalCreditos;
    }

    public void setTotalCreditos(short totalCreditos) {
        this.totalCreditos = totalCreditos;
    }

    public String getNumeroRvoe() {
        return numeroRvoe;
    }

    public void setNumeroRvoe(String numeroRvoe) {
        this.numeroRvoe = numeroRvoe;
    }

    public String getInstitucionRvoe() {
        return institucionRvoe;
    }

    public void setInstitucionRvoe(String institucionRvoe) {
        this.institucionRvoe = institucionRvoe;
    }

    public String getConvenioRvoe() {
        return convenioRvoe;
    }

    public void setConvenioRvoe(String convenioRvoe) {
        this.convenioRvoe = convenioRvoe;
    }

    public String getResponsableRvoe() {
        return responsableRvoe;
    }

    public void setResponsableRvoe(String responsableRvoe) {
        this.responsableRvoe = responsableRvoe;
    }

    public boolean getAcreditanInternacionales() {
        return acreditanInternacionales;
    }

    public void setAcreditanInternacionales(boolean acreditanInternacionales) {
        this.acreditanInternacionales = acreditanInternacionales;
    }

    public boolean getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(boolean liquidacion) {
        this.liquidacion = liquidacion;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @XmlTransient
    public List<Acreditadoras> getAcreditadorasList() {
        return acreditadorasList;
    }

    public void setAcreditadorasList(List<Acreditadoras> acreditadorasList) {
        this.acreditadorasList = acreditadorasList;
    }

    @XmlTransient
    public List<RegistroIndicadores> getRegistroIndicadoresList() {
        return registroIndicadoresList;
    }

    public void setRegistroIndicadoresList(List<RegistroIndicadores> registroIndicadoresList) {
        this.registroIndicadoresList = registroIndicadoresList;
    }

    public AreaConocimiento getIdAreaConocimiento() {
        return idAreaConocimiento;
    }

    public void setIdAreaConocimiento(AreaConocimiento idAreaConocimiento) {
        this.idAreaConocimiento = idAreaConocimiento;
    }

    public Campus getIdCampus() {
        return idCampus;
    }

    public void setIdCampus(Campus idCampus) {
        this.idCampus = idCampus;
    }

    public Enfoque getIdEnfoque() {
        return idEnfoque;
    }

    public void setIdEnfoque(Enfoque idEnfoque) {
        this.idEnfoque = idEnfoque;
    }

    public Modalidad getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(Modalidad idModalidad) {
        this.idModalidad = idModalidad;
    }

    public NivelAcademico getIdNivel() {
        return idNivel;
    }

    public void setIdNivel(NivelAcademico idNivel) {
        this.idNivel = idNivel;
    }

    public Temporalidades getIdTemporalidad() {
        return idTemporalidad;
    }

    public void setIdTemporalidad(Temporalidades idTemporalidad) {
        this.idTemporalidad = idTemporalidad;
    }

    public TipoRvoe getIdTipoRvoe() {
        return idTipoRvoe;
    }

    public void setIdTipoRvoe(TipoRvoe idTipoRvoe) {
        this.idTipoRvoe = idTipoRvoe;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPrograma != null ? idPrograma.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Programas)) {
            return false;
        }
        Programas other = (Programas) object;
        if ((this.idPrograma == null && other.idPrograma != null) || (this.idPrograma != null && !this.idPrograma.equals(other.idPrograma))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idPrograma + " - " + idModalidad.getDescripcion() + " - " + nombre;
    }
    
}
