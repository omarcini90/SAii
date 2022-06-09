package mx.org.fimpes.saii.model.security;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Embeddable
public class CredencialesHasMenuPK implements Serializable {
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
    @NotNull
    @Column(name = "id_credencial")
    private int idCredencial;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_menu")
    private int idMenu;

    public CredencialesHasMenuPK() {
    }

    public CredencialesHasMenuPK(int idCredencial, int idMenu) {
        this.idCredencial = idCredencial;
        this.idMenu = idMenu;
    }

    public int getIdCredencial() {
        return idCredencial;
    }

    public void setIdCredencial(int idCredencial) {
        this.idCredencial = idCredencial;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idCredencial;
        hash += (int) idMenu;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CredencialesHasMenuPK)) {
            return false;
        }
        CredencialesHasMenuPK other = (CredencialesHasMenuPK) object;
        if (this.idCredencial != other.idCredencial) {
            return false;
        }
        if (this.idMenu != other.idMenu) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[ idCredencial=" + idCredencial + ", idMenu=" + idMenu + " ]";
    }

}
