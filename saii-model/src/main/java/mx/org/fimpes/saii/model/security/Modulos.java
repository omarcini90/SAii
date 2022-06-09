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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "modulos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Modulos.findByEtiquetaMenu", query = "SELECT m FROM Modulos m WHERE m.etiquetaMenu LIKE '%:etiquetaMenu%'")})
public class Modulos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_modulo")
    private Integer idModulo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "etiqueta_menu")
    private String etiquetaMenu;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @JoinColumn(name = "id_sistema", referencedColumnName = "id_sistema")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Sistemas idSistema;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idModulo", fetch = FetchType.EAGER)
    private List<Menus> menusList;

    public Modulos() {
    }

    public Modulos(Integer idModulo) {
        this.idModulo = idModulo;
    }

    public Modulos(Integer idModulo, String etiquetaMenu, String descripcion, Date fechaModificacion) {
        this.idModulo = idModulo;
        this.etiquetaMenu = etiquetaMenu;
        this.descripcion = descripcion;
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Integer idModulo) {
        this.idModulo = idModulo;
    }

    public String getEtiquetaMenu() {
        return etiquetaMenu;
    }

    public void setEtiquetaMenu(String etiquetaMenu) {
        this.etiquetaMenu = etiquetaMenu;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Sistemas getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(Sistemas idSistema) {
        this.idSistema = idSistema;
    }

    @XmlTransient
    public List<Menus> getMenusList() {
        return menusList;
    }

    public void setMenusCollection(List<Menus> menusList) {
        this.menusList = menusList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idModulo != null ? idModulo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Modulos)) {
            return false;
        }
        Modulos other = (Modulos) object;
        if ((this.idModulo == null && other.idModulo != null) || (this.idModulo != null && !this.idModulo.equals(other.idModulo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idModulo + " - " + etiquetaMenu;
    }

}
