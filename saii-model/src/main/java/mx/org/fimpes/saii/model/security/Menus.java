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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "menus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Menus.findByEtiquetaMenu", query = "SELECT m FROM Menus m WHERE m.etiquetaMenu LIKE '%:etiquetaMenu%'")})
public class Menus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_menu")
    private Integer idMenu;
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
    @Size(max = 50)
    @Column(name = "icono")
    private String icono;
    @Size(max = 45)
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activo")
    private boolean activo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @JsonBackReference
    @JoinTable(name = "roles_has_menus", joinColumns = {
        @JoinColumn(name = "id_menu", referencedColumnName = "id_menu")}, inverseJoinColumns = {
        @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Roles> rolesList;

    @JsonIgnore
    @OneToMany(mappedBy = "menus")
    private List<CredencialesHasMenu> credencialesHasMenuList;

    @JsonIgnore
    @OneToMany(mappedBy = "idMenuSuperior")
    private List<Menus> menusList;

    @JoinColumn(name = "id_menu_superior", referencedColumnName = "id_menu")
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Menus idMenuSuperior;

    @JoinColumn(name = "id_modulo", referencedColumnName = "id_modulo")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Modulos idModulo;

    public Menus() {
    }

    public Menus(Integer idMenu) {
        this.idMenu = idMenu;
    }

    public Menus(Integer idMenu, String etiquetaMenu, String descripcion, boolean activo, Date fechaModificacion) {
        this.idMenu = idMenu;
        this.etiquetaMenu = etiquetaMenu;
        this.descripcion = descripcion;
        this.activo = activo;
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(Integer idMenu) {
        this.idMenu = idMenu;
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

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @XmlTransient
    public List<Roles> getRolesList() {
        return rolesList;
    }

    public void setRolesList(List<Roles> rolesList) {
        this.rolesList = rolesList;
    }

    @XmlTransient
    public List<CredencialesHasMenu> getCredencialesHasMenuList() {
        return credencialesHasMenuList;
    }

    public void setCredencialesHasMenuCollection(List<CredencialesHasMenu> credencialesHasMenuList) {
        this.credencialesHasMenuList = credencialesHasMenuList;
    }

    @XmlTransient
    public List<Menus> getMenusLit() {
        return menusList;
    }

    public void setMenusCollection(List<Menus> menusList) {
        this.menusList = menusList;
    }

    public Menus getIdMenuSuperior() {
        return idMenuSuperior;
    }

    public void setIdMenuSuperior(Menus idMenuSuperior) {
        this.idMenuSuperior = idMenuSuperior;
    }

    public Modulos getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Modulos idModulo) {
        this.idModulo = idModulo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMenu != null ? idMenu.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Menus)) {
            return false;
        }
        Menus other = (Menus) object;
        if ((this.idMenu == null && other.idMenu != null) || (this.idMenu != null && !this.idMenu.equals(other.idMenu))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idMenu + " - " + etiquetaMenu;
    }

}
