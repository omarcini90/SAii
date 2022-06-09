package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "ciclos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ciclos.findByDescripcion", query = "SELECT c FROM Ciclos c WHERE c.descripcion = :descripcion")})
public class Ciclos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_ciclo")
    private Integer idCiclo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "descripcion")
    private String descripcion;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCiclo")
    private List<PeriodoAcceso> periodoAccesoList;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCiclo")
    private List<ProcesoCaptura> procesoCapturaList;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCiclo")
    private List<RegistroIndicadores> registroIndicadoresList;
    
    @JsonManagedReference
    @JoinColumn(name = "id_temporalidad", referencedColumnName = "id_temporalidad")
    @ManyToOne(optional = false)
    private Temporalidades idTemporalidad;

    public Ciclos() {
    }

    public Ciclos(Integer idCiclo) {
        this.idCiclo = idCiclo;
    }

    public Ciclos(Integer idCiclo, String descripcion) {
        this.idCiclo = idCiclo;
        this.descripcion = descripcion;
    }

    public Integer getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(Integer idCiclo) {
        this.idCiclo = idCiclo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
    public List<RegistroIndicadores> getRegistroIndicadoresList() {
        return registroIndicadoresList;
    }

    public void setRegistroIndicadoresList(List<RegistroIndicadores> registroIndicadoresList) {
        this.registroIndicadoresList = registroIndicadoresList;
    }

    public Temporalidades getIdTemporalidad() {
        return idTemporalidad;
    }

    public void setIdTemporalidad(Temporalidades idTemporalidad) {
        this.idTemporalidad = idTemporalidad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCiclo != null ? idCiclo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ciclos)) {
            return false;
        }
        Ciclos other = (Ciclos) object;
        if ((this.idCiclo == null && other.idCiclo != null) || (this.idCiclo != null && !this.idCiclo.equals(other.idCiclo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idTemporalidad.getDescripcion();
    }
    
}
