package mx.org.fimpes.saii.model.security;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "restablecimiento_password")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RestablecimientoPassword.findByToken", query = "SELECT r FROM RestablecimientoPassword r WHERE r.token = :token")})
public class RestablecimientoPassword implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_restablecimiento")
    private Integer idRestablecimiento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "cadena_original")
    private String cadenaOriginal;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "token")
    private String token;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_solicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSolicitud;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_caducidad")
    @Temporal(TemporalType.DATE)
    private Date fechaCaducidad;
    @Column(name = "fecha_restablecimiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRestablecimiento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "restablecido")
    private boolean restablecido;
    
    @JsonManagedReference
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne(optional = false)
    private Usuarios idUsuario;

    public RestablecimientoPassword() {
    }

    public RestablecimientoPassword(Integer idRestablecimiento) {
        this.idRestablecimiento = idRestablecimiento;
    }

    public RestablecimientoPassword(Usuarios usuario) {
        this.idUsuario = usuario;
    }

    public Integer getIdRestablecimiento() {
        return idRestablecimiento;
    }

    public void setIdRestablecimiento(Integer idRestablecimiento) {
        this.idRestablecimiento = idRestablecimiento;
    }

    public String getCadenaOriginal() {
        return cadenaOriginal;
    }

    public void setCadenaOriginal(String cadenaOriginal) {
        this.cadenaOriginal = cadenaOriginal;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public Date getFechaRestablecimiento() {
        return fechaRestablecimiento;
    }

    public void setFechaRestablecimiento(Date fechaRestablecimiento) {
        this.fechaRestablecimiento = fechaRestablecimiento;
    }

    public boolean getRestablecido() {
        return restablecido;
    }

    public void setRestablecido(boolean restablecido) {
        this.restablecido = restablecido;
    }

    public Usuarios getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuarios idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRestablecimiento != null ? idRestablecimiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RestablecimientoPassword)) {
            return false;
        }
        RestablecimientoPassword other = (RestablecimientoPassword) object;
        if ((this.idRestablecimiento == null && other.idRestablecimiento != null) || (this.idRestablecimiento != null && !this.idRestablecimiento.equals(other.idRestablecimiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return token;
    }
}