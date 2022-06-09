package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "afiliaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Afiliaciones.findByDescripcion", query = "SELECT a FROM Afiliaciones a WHERE a.descripcion = :descripcion")})
public class Afiliaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_afiliacion")
    private Integer idAfiliacion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(min = 1, max = 250)
    @Column(name = "significado")
    private String significado;
    
    @JsonBackReference
    @ManyToMany(mappedBy = "afiliacionesList")
    private List<Instituciones> institucionesList;

    public Afiliaciones() {
    }

    public Afiliaciones(Integer idAfiliacion) {
        this.idAfiliacion = idAfiliacion;
    }

    public Afiliaciones(Integer idAfiliacion, String descripcion) {
        this.idAfiliacion = idAfiliacion;
        this.descripcion = descripcion;
    }

    public Integer getIdAfiliacion() {
        return idAfiliacion;
    }

    public void setIdAfiliacion(Integer idAfiliacion) {
        this.idAfiliacion = idAfiliacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getSignificado() {
        return significado;
    }

    public void setSignificado(String significado) {
        this.significado = significado;
    }

    @XmlTransient
    public List<Instituciones> getInstitucionesList() {
        return institucionesList;
    }

    public void setInstitucionesList(List<Instituciones> institucionesList) {
        this.institucionesList = institucionesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAfiliacion != null ? idAfiliacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Afiliaciones)) {
            return false;
        }
        Afiliaciones other = (Afiliaciones) object;
        if ((this.idAfiliacion == null && other.idAfiliacion != null) || (this.idAfiliacion != null && !this.idAfiliacion.equals(other.idAfiliacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idAfiliacion + " - " + descripcion;
    }
    
}
