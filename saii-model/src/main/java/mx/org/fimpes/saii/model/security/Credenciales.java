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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "credenciales")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Credenciales.findByUsuario", query = "SELECT c FROM Credenciales c WHERE c.usuario = :usuario")
})
public class Credenciales implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_credencial")
    private Integer idCredencial;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "usuario")
    private String usuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "password")
    private String password;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;

    @JsonIgnore
    @JoinTable(name = "credenciales_has_roles", joinColumns = {
        @JoinColumn(name = "id_credencial", referencedColumnName = "id_credencial")}, inverseJoinColumns = {
        @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")})
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<Roles> rolesList;

    @JsonIgnore
    @OneToMany(mappedBy = "credenciales", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<CredencialesHasMenu> credencialesHasMenuList;

    @JsonBackReference
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", insertable = false, updatable = false)
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private Usuarios usuarios;

    public Credenciales() {
    }

    public Credenciales(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Credenciales(Integer idUsuario, String usuario, String password, Date fechaModificacion) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.password = password;
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getIdCredencial() {
        return idCredencial;
    }

    public void setIdCredencial(Integer idCredencial) {
        this.idCredencial = idCredencial;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setCredencialesHasMenuList(List<CredencialesHasMenu> credencialesHasMenuList) {
        this.credencialesHasMenuList = credencialesHasMenuList;
    }

    public Usuarios getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuarios usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsuario != null ? idUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Credenciales)) {
            return false;
        }
        Credenciales other = (Credenciales) object;
        /*if ((this.idUsuario == null && other.idUsuario != null) || (this.idUsuario != null && !this.idUsuario.equals(other.idUsuario))) {
            return false;*/
        if ((this.idCredencial == null && other.idCredencial != null) || (this.idCredencial != null && !this.idCredencial.equals(other.idCredencial))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return usuario;
    }

}
