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
@Table(name = "area_conocimiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AreaConocimiento.findByDescripcion", query = "SELECT a FROM AreaConocimiento a WHERE a.descripcion = :descripcion")})
public class AreaConocimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_area_conocimiento")
    private Integer idAreaConocimiento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAreaConocimiento")
    private List<Programas> programasList;

    public AreaConocimiento() {
    }

    public AreaConocimiento(Integer idAreaConocimiento) {
        this.idAreaConocimiento = idAreaConocimiento;
    }

    public AreaConocimiento(Integer idAreaConocimiento, String descripcion) {
        this.idAreaConocimiento = idAreaConocimiento;
        this.descripcion = descripcion;
    }

    public Integer getIdAreaConocimiento() {
        return idAreaConocimiento;
    }

    public void setIdAreaConocimiento(Integer idAreaConocimiento) {
        this.idAreaConocimiento = idAreaConocimiento;
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
        hash += (idAreaConocimiento != null ? idAreaConocimiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AreaConocimiento)) {
            return false;
        }
        AreaConocimiento other = (AreaConocimiento) object;
        if ((this.idAreaConocimiento == null && other.idAreaConocimiento != null) || (this.idAreaConocimiento != null && !this.idAreaConocimiento.equals(other.idAreaConocimiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idAreaConocimiento + " - " + descripcion;
    }
    
}
