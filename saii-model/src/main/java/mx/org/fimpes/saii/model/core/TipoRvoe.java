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
@Table(name = "tipo_rvoe")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoRvoe.findByDescripcion", query = "SELECT t FROM TipoRvoe t WHERE t.descripcion = :descripcion")})
public class TipoRvoe implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_tipo_rvoe")
    private Integer idTipoRvoe;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTipoRvoe")
    private List<Programas> programasList;

    public TipoRvoe() {
    }

    public TipoRvoe(Integer idTipoRvoe) {
        this.idTipoRvoe = idTipoRvoe;
    }

    public TipoRvoe(Integer idTipoRvoe, String descripcion) {
        this.idTipoRvoe = idTipoRvoe;
        this.descripcion = descripcion;
    }

    public Integer getIdTipoRvoe() {
        return idTipoRvoe;
    }

    public void setIdTipoRvoe(Integer idTipoRvoe) {
        this.idTipoRvoe = idTipoRvoe;
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
        hash += (idTipoRvoe != null ? idTipoRvoe.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoRvoe)) {
            return false;
        }
        TipoRvoe other = (TipoRvoe) object;
        if ((this.idTipoRvoe == null && other.idTipoRvoe != null) || (this.idTipoRvoe != null && !this.idTipoRvoe.equals(other.idTipoRvoe))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idTipoRvoe + " - " + descripcion;
    }
    
}
