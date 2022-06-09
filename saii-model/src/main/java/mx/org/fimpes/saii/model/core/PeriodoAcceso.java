package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "periodo_acceso")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeriodoAcceso.findByAnio", query = "SELECT p FROM PeriodoAcceso p WHERE p.anio = :anio")})
public class PeriodoAcceso implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_periodo_acceso")
    private Integer idPeriodoAcceso;
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio")
    private short anio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    
    @JsonManagedReference
    @JoinColumn(name = "id_institucion", referencedColumnName = "id_institucion")
    @ManyToOne(optional = false)
    private Instituciones idInstitucion;
    
    @JsonManagedReference
    @JoinColumn(name = "id_ciclo", referencedColumnName = "id_ciclo")
    @ManyToOne(optional = false)
    private Ciclos idCiclo;

    public PeriodoAcceso() {
    }

    public PeriodoAcceso(Integer idPeriodoAcceso) {
        this.idPeriodoAcceso = idPeriodoAcceso;
    }

    public PeriodoAcceso(Instituciones institucion, Ciclos ciclo, short anio) {
        this.idInstitucion = institucion;
        this.idCiclo = ciclo;
        this.anio = anio;
    }

    public Integer getIdPeriodoAcceso() {
        return idPeriodoAcceso;
    }

    public void setIdPeriodoAcceso(Integer idPeriodoAcceso) {
        this.idPeriodoAcceso = idPeriodoAcceso;
    }
    
    public Instituciones getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(Instituciones idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public short getAnio() {
        return anio;
    }

    public void setAnio(short anio) {
        this.anio = anio;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Ciclos getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(Ciclos idCiclo) {
        this.idCiclo = idCiclo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPeriodoAcceso != null ? idPeriodoAcceso.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PeriodoAcceso)) {
            return false;
        }
        PeriodoAcceso other = (PeriodoAcceso) object;
        if ((this.idPeriodoAcceso == null && other.idPeriodoAcceso != null) || (this.idPeriodoAcceso != null && !this.idPeriodoAcceso.equals(other.idPeriodoAcceso))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idCiclo.toString().concat(" ").concat(String.valueOf(anio));
    }
    
}
