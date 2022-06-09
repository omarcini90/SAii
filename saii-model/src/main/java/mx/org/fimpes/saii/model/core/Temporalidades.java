package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
@Table(name = "temporalidades")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Temporalidades.findByDescripcion", query = "SELECT t FROM Temporalidades t WHERE t.descripcion = :descripcion")})
public class Temporalidades implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_temporalidad")
    private Integer idTemporalidad;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "descripcion")
    private String descripcion;
    
    @JsonBackReference
    @ManyToMany(mappedBy = "temporalidadesList")
    private List<Instituciones> institucionesList;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTemporalidad", fetch = FetchType.EAGER)
    private List<Ciclos> ciclosList;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTemporalidad")
    private List<Programas> programasList;

    public Temporalidades() {
    }

    public Temporalidades(Integer idTemporalidad) {
        this.idTemporalidad = idTemporalidad;
    }

    public Temporalidades(Integer idTemporalidad, String descripcion) {
        this.idTemporalidad = idTemporalidad;
        this.descripcion = descripcion;
    }

    public Integer getIdTemporalidad() {
        return idTemporalidad;
    }

    public void setIdTemporalidad(Integer idTemporalidad) {
        this.idTemporalidad = idTemporalidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<Instituciones> getInstitucionesList() {
        return institucionesList;
    }

    public void setInstitucionesList(List<Instituciones> institucionesList) {
        this.institucionesList = institucionesList;
    }

    @XmlTransient
    public List<Ciclos> getCiclosList() {
        return ciclosList;
    }

    public void setCiclosList(List<Ciclos> ciclosList) {
        this.ciclosList = ciclosList;
    }

    @XmlTransient
    public List<Programas> getProgramasList() {
        return programasList;
    }

    public void setProgramasList(List<Programas> programasList) {
        this.programasList = programasList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTemporalidad != null ? idTemporalidad.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Temporalidades)) {
            return false;
        }
        Temporalidades other = (Temporalidades) object;
        if ((this.idTemporalidad == null && other.idTemporalidad != null) || (this.idTemporalidad != null && !this.idTemporalidad.equals(other.idTemporalidad))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idTemporalidad + " - " + descripcion;
    }
    
}
