package mx.org.fimpes.saii.ejb.security;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.ejb.security.crypto.Crypto;
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.model.security.Credenciales;
import mx.org.fimpes.saii.model.security.CredencialesHasMenu;
import mx.org.fimpes.saii.model.security.CredencialesHasMenuPK;
import mx.org.fimpes.saii.model.security.Usuarios;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class CredencialesFacade extends AbstractFacade<Credenciales> {

    private static final Logger log = Logger.getLogger(CredencialesFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CredencialesFacade() {
        super(Credenciales.class);
    }

    @Override
    public void create(Credenciales credencial) {
        credencial.setIdUsuario(credencial.getUsuarios().getIdUsuario());
        String crypted = Crypto.sha256digestOf(credencial.getPassword());
        log.info("Encrypted password: " + crypted);
        credencial.setPassword(crypted);
        credencial.setFechaModificacion(MxDateTime.date());
        super.create(credencial);
    }

    @Override
    public void edit(Credenciales credencial) {
        String crypted = Crypto.sha256digestOf(credencial.getPassword());
        log.info("Encrypted password: " + crypted);
        credencial.setPassword(crypted);
        credencial.setFechaModificacion(MxDateTime.date());
        super.edit(credencial);
    }

    public Credenciales findByCredentials(String username, String password) {
        TypedQuery<Credenciales> q = getEntityManager().createQuery("SELECT c FROM Credenciales c WHERE c.usuario = :usuario AND c.password = :password", Credenciales.class);
        q.setParameter("usuario", username);
        password = Crypto.sha256digestOf(password);
        q.setParameter("password", password);
        var c = q.getSingleResult();
        log.info("findByCredentials: " + c.toString());

        return c;
    }

    public Credenciales findByUsername(String username) {
        TypedQuery<Credenciales> q = getEntityManager().createQuery("SELECT c FROM Credenciales c WHERE c.usuario = :usuario", Credenciales.class);
        q.setParameter("usuario", username);
        var c = q.getSingleResult();
        log.info("findByUsername: " + c.toString());

        return c;
    }

    public Usuarios findUserBy(String username) {
        TypedQuery<Credenciales> q = getEntityManager().createQuery("SELECT c FROM Credenciales c WHERE c.usuario = :usuario", Credenciales.class);
        q.setParameter("usuario", username);
        var u = q.getSingleResult().getUsuarios();
        log.info("findUserBy: " + u.toString());

        return u;
    }
    
    public List<Credenciales> findByInstitucion(Instituciones idInstitucion){
    	TypedQuery<Credenciales> q = getEntityManager().createQuery("SELECT c FROM Credenciales c LEFT JOIN Usuarios u ON c.idUsuario = u.idUsuario WHERE u.idInstitucion = :idInstitucion", Credenciales.class);
        q.setParameter("idInstitucion", idInstitucion);
        var l = q.getResultList();
        log.info("findByInstitucion: " + l.size());
        
        return l;
    }

    public CredencialesHasMenu findMenu(CredencialesHasMenuPK id) {
        var menu = em.find(CredencialesHasMenu.class, id);
        log.info("findMenu: " + menu.toString());

        return menu;
    }

    public void addMenu(CredencialesHasMenu menu) {
        getEntityManager().persist(menu);
        log.info("Menu " + menu.getMenus().getIdMenu() + " added to User " + menu.getCredenciales().getUsuario());
    }
    
     public CredencialesHasMenu removeMenu(CredencialesHasMenuPK id) {
        var menu = findMenu(id);
        getEntityManager().remove(em.contains(menu) ? menu : em.merge(menu));
        em.joinTransaction();
        em.flush();
        log.info("Menu " + menu.getMenus().getIdMenu() + " removed from User " + menu.getCredenciales().getUsuario());

        return menu;
    }

    public void addRol(Integer idCredencial, Integer idRol) {
        Query q = getEntityManager().createNativeQuery("INSERT INTO credenciales_has_roles(id_credencial,id_rol) VALUES(?,?)");
        q.setParameter(1, idCredencial);
        q.setParameter(2, idRol);
        q.executeUpdate();
        log.info("Rol " + idRol + " added to User " + idCredencial);
    }

    public void removeRol(Integer idCredencial, Integer idRol) {
        Query q = getEntityManager().createNativeQuery("DELETE FROM credenciales_has_roles WHERE id_credencial = ? AND id_rol = ?");
        q.setParameter(1, idCredencial);
        q.setParameter(2, idRol);
        q.executeUpdate();
        log.info("Rol " + idRol + " removed from User " + idCredencial);
    }
    
    @Override
    public void remove(Credenciales credencial) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM credenciales WHERE id_credencial = ? ");
        q.setParameter(1, credencial.getIdCredencial());
        q.executeUpdate();
    }
}
