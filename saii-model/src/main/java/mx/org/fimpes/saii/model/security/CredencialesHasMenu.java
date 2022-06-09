package mx.org.fimpes.saii.model.security;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "credenciales_has_menu")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CredencialesHasMenu.findAll", query = "SELECT c FROM CredencialesHasMenu c"),
    @NamedQuery(name = "CredencialesHasMenu.findByIdMenu", query = "SELECT c FROM CredencialesHasMenu c WHERE c.credencialesHasMenuPK.idMenu = :idMenu")
})
public class CredencialesHasMenu implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CredencialesHasMenuPK credencialesHasMenuPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "crear")
    private boolean crear;
    @Basic(optional = false)
    @NotNull
    @Column(name = "consultar")
    private boolean consultar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "editar")
    private boolean editar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "eliminar")
    private boolean eliminar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "imprimir")
    private boolean imprimir;

    @JoinColumn(name = "id_credencial", referencedColumnName = "id_credencial", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Credenciales credenciales;

    @JoinColumn(name = "id_menu", referencedColumnName = "id_menu", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Menus menus;

    public CredencialesHasMenu() {
    }

    public CredencialesHasMenu(CredencialesHasMenuPK credencialesHasMenuPK) {
        this.credencialesHasMenuPK = credencialesHasMenuPK;
    }

    public CredencialesHasMenu(CredencialesHasMenuPK credencialesHasMenuPK, boolean crear, boolean consultar, boolean editar, boolean eliminar, boolean imprimir) {
        this.credencialesHasMenuPK = credencialesHasMenuPK;
        this.crear = crear;
        this.consultar = consultar;
        this.editar = editar;
        this.eliminar = eliminar;
        this.imprimir = imprimir;
    }

    public CredencialesHasMenu(int idCredencial, int idMenu) {
        this.credencialesHasMenuPK = new CredencialesHasMenuPK(idCredencial, idMenu);
    }

    public CredencialesHasMenuPK getCredencialesHasMenuPK() {
        return credencialesHasMenuPK;
    }

    public void setCredencialesHasMenuPK(CredencialesHasMenuPK credencialesHasMenuPK) {
        this.credencialesHasMenuPK = credencialesHasMenuPK;
    }

    public boolean getCrear() {
        return crear;
    }

    public void setCrear(boolean crear) {
        this.crear = crear;
    }

    public boolean getConsultar() {
        return consultar;
    }

    public void setConsultar(boolean consultar) {
        this.consultar = consultar;
    }

    public boolean getEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public boolean getEliminar() {
        return eliminar;
    }

    public void setEliminar(boolean eliminar) {
        this.eliminar = eliminar;
    }

    public boolean getImprimir() {
        return imprimir;
    }

    public void setImprimir(boolean imprimir) {
        this.imprimir = imprimir;
    }

    public Credenciales getCredenciales() {
        return credenciales;
    }

    public void setCredenciales(Credenciales credenciales) {
        this.credenciales = credenciales;
    }

    public Menus getMenus() {
        return menus;
    }

    public void setMenus(Menus menus) {
        this.menus = menus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (credencialesHasMenuPK != null ? credencialesHasMenuPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CredencialesHasMenu)) {
            return false;
        }
        CredencialesHasMenu other = (CredencialesHasMenu) object;
        if ((this.credencialesHasMenuPK == null && other.credencialesHasMenuPK != null) || (this.credencialesHasMenuPK != null && !this.credencialesHasMenuPK.equals(other.credencialesHasMenuPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return credencialesHasMenuPK.toString();
    }

}
