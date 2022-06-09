package mx.org.fimpes.saii.model.security;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "sistemas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sistemas.findByDescripcion", query = "SELECT s FROM Sistemas s WHERE s.descripcion LIKE '%:descripcion%'")})
public class Sistemas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_sistema")
    private Integer idSistema;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "acronimo")
    private String acronimo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 100)
    @Column(name = "url_repositorio")
    private String urlRepositorio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "ultima_version")
    private String ultimaVersion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idSistema", fetch = FetchType.EAGER)
    private List<Modulos> modulosList;

    public Sistemas() {
    }

    public Sistemas(Integer idSistema) {
        this.idSistema = idSistema;
    }

    public Sistemas(Integer idSistema, String acronimo, String descripcion, String ultimaVersion, Date fechaModificacion) {
        this.idSistema = idSistema;
        this.acronimo = acronimo;
        this.descripcion = descripcion;
        this.ultimaVersion = ultimaVersion;
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(Integer idSistema) {
        this.idSistema = idSistema;
    }

    public String getAcronimo() {
        return acronimo;
    }

    public void setAcronimo(String acronimo) {
        this.acronimo = acronimo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlRepositorio() {
        return urlRepositorio;
    }

    public void setUrlRepositorio(String urlRepositorio) {
        this.urlRepositorio = urlRepositorio;
    }

    public String getUltimaVersion() {
        return ultimaVersion;
    }

    public void setUltimaVersion(String ultimaVersion) {
        this.ultimaVersion = ultimaVersion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @XmlTransient
    public List<Modulos> getModulosList() {
        return modulosList;
    }

    public void setModulosList(List<Modulos> modulosList) {
        this.modulosList = modulosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSistema != null ? idSistema.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sistemas)) {
            return false;
        }
        Sistemas other = (Sistemas) object;
        if ((this.idSistema == null && other.idSistema != null) || (this.idSistema != null && !this.idSistema.equals(other.idSistema))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idSistema + " - " + acronimo;
    }

}
