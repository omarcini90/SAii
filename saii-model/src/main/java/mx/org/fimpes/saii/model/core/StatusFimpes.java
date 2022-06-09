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
@Table(name = "status_fimpes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StatusFimpes.findByDescripcion", query = "SELECT s FROM StatusFimpes s WHERE s.descripcion = :descripcion")})
public class StatusFimpes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_status")
    private Integer idStatus;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idStatus")
    private List<Instituciones> institucionesList;

    public StatusFimpes() {
    }

    public StatusFimpes(Integer idStatus) {
        this.idStatus = idStatus;
    }

    public StatusFimpes(Integer idStatus, String descripcion) {
        this.idStatus = idStatus;
        this.descripcion = descripcion;
    }

    public Integer getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Integer idStatus) {
        this.idStatus = idStatus;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idStatus != null ? idStatus.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StatusFimpes)) {
            return false;
        }
        StatusFimpes other = (StatusFimpes) object;
        if ((this.idStatus == null && other.idStatus != null) || (this.idStatus != null && !this.idStatus.equals(other.idStatus))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idStatus + " - " + descripcion;
    }
    
}
