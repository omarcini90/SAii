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
@Table(name = "enfoque")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Enfoque.findByDescripcion", query = "SELECT e FROM Enfoque e WHERE e.descripcion = :descripcion")})
public class Enfoque implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_enfoque")
    private Integer idEnfoque;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idEnfoque")
    private List<Programas> programasList;

    public Enfoque() {
    }

    public Enfoque(Integer idEnfoque) {
        this.idEnfoque = idEnfoque;
    }

    public Enfoque(Integer idEnfoque, String descripcion) {
        this.idEnfoque = idEnfoque;
        this.descripcion = descripcion;
    }

    public Integer getIdEnfoque() {
        return idEnfoque;
    }

    public void setIdEnfoque(Integer idEnfoque) {
        this.idEnfoque = idEnfoque;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        hash += (idEnfoque != null ? idEnfoque.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Enfoque)) {
            return false;
        }
        Enfoque other = (Enfoque) object;
        if ((this.idEnfoque == null && other.idEnfoque != null) || (this.idEnfoque != null && !this.idEnfoque.equals(other.idEnfoque))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idEnfoque + " - " + descripcion;
    }
    
}
