package mx.org.fimpes.saii.ejb.security;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.security.Roles;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class RolesFacade extends AbstractFacade<Roles> {
    private static final Logger log = Logger.getLogger(RolesFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RolesFacade() {
        super(Roles.class);
    }
    
    @Override
    public void create(Roles rol){
        rol.setFechaModificacion(MxDateTime.date());
        super.create(rol);
    }
    
    @Override
    public void edit(Roles rol){
        rol.setFechaModificacion(MxDateTime.date());
        super.edit(rol);
    }
    
    public List<Roles> findAvailableOnesForUser(Integer idCredencial, boolean fimpesRol){
    	String query = "SELECT r.* FROM roles r WHERE r.id_rol NOT IN (SELECT c.id_rol FROM credenciales_has_roles c WHERE c.id_credencial = ?)";
    	if(!fimpesRol) {
    		query = query.concat(" AND r.rol NOT LIKE '%FIMPES%'");
    	}
        Query q = getEntityManager().createNativeQuery(query, Roles.class);
        q.setParameter(1, idCredencial);
        List<Roles> r = q.getResultList();
        log.info("findAvailableOnesForUser: " + r.size());
        
        return r;
    }
    
    public void addMenu(Integer idRol, Integer idMenu){
        Query q = getEntityManager().createNativeQuery("INSERT INTO roles_has_menus(id_rol,id_menu) VALUES(?,?)");
        q.setParameter(1, idRol);
        q.setParameter(2, idMenu);
        q.executeUpdate();
        log.info("Menu " + idMenu + " added to Rol " + idRol);
    }
    
    public void removeMenu(Integer idRol, Integer idMenu){
        Query q = getEntityManager().createNativeQuery("DELETE FROM roles_has_menus WHERE id_rol = ? AND id_menu = ?");
        q.setParameter(1, idRol);
        q.setParameter(2, idMenu);
        q.executeUpdate();
        log.info("Menu " + idMenu + " removed from Rol " + idRol);
    }
    
    @Override
    public void remove(Roles rol) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM roles WHERE id_rol = ? ");
        q.setParameter(1, rol.getIdRol());
        q.executeUpdate();
    }
}
