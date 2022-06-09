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
@Table(name = "proceso_captura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProcesoCaptura.findByAnio", query = "SELECT p FROM ProcesoCaptura p WHERE p.anio = :anio"),
    @NamedQuery(name = "ProcesoCaptura.findByCompletado", query = "SELECT p FROM ProcesoCaptura p WHERE p.completado = :completado")})
public class ProcesoCaptura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_proceso")
    private Integer idProceso;
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio")
    private short anio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "completado")
    private boolean completado;
    
    @JsonManagedReference
    @JoinColumn(name = "id_ciclo", referencedColumnName = "id_ciclo")
    @ManyToOne(optional = false)
    private Ciclos idCiclo;
    
    @JsonManagedReference
    @JoinColumn(name = "id_institucion", referencedColumnName = "id_institucion")
    @ManyToOne(optional = false)
    private Instituciones idInstitucion;

    public ProcesoCaptura() {
    }

    public ProcesoCaptura(Integer idProceso) {
        this.idProceso = idProceso;
    }

    public ProcesoCaptura(Instituciones institucion, Ciclos ciclo, short anio) {
        this.idInstitucion = institucion;
        this.idCiclo = ciclo;
        this.anio = anio;
    }

    public Integer getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(Integer idProceso) {
        this.idProceso = idProceso;
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

    public boolean getCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public Ciclos getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(Ciclos idCiclo) {
        this.idCiclo = idCiclo;
    }

    public Instituciones getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(Instituciones idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProceso != null ? idProceso.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProcesoCaptura)) {
            return false;
        }
        ProcesoCaptura other = (ProcesoCaptura) object;
        if ((this.idProceso == null && other.idProceso != null) || (this.idProceso != null && !this.idProceso.equals(other.idProceso))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idCiclo.toString().concat(" ").concat(String.valueOf(anio)).concat(" ").concat(idInstitucion.getSiglas());
    }
    
}
