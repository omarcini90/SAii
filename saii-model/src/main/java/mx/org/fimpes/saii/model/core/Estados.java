package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "estados")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estados.findByDescripcion", query = "SELECT e FROM Estados e WHERE e.descripcion = :descripcion")})
public class Estados implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_estado")
    private Integer idEstado;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 5)
    @Column(name = "abreviatura")
    private String abreviatura;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idEstado")
    private List<Campus> campusList;

    public Estados() {
    }

    public Estados(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public Estados(Integer idEstado, String descripcion) {
        this.idEstado = idEstado;
        this.descripcion = descripcion;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    @XmlTransient
    public List<Campus> getCampusList() {
        return campusList;
    }

    public void setCampusList(List<Campus> campusList) {
        this.campusList = campusList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEstado != null ? idEstado.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estados)) {
            return false;
        }
        Estados other = (Estados) object;
        if ((this.idEstado == null && other.idEstado != null) || (this.idEstado != null && !this.idEstado.equals(other.idEstado))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idEstado + " - " + descripcion.concat(" (").concat(abreviatura).concat(")");
    }
    
}
