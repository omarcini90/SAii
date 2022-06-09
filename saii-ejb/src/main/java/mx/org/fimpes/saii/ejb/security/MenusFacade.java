package mx.org.fimpes.saii.ejb.security;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.security.Menus;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class MenusFacade extends AbstractFacade<Menus> {
    private static final Logger log = Logger.getLogger(MenusFacade.class);
    
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MenusFacade() {
        super(Menus.class);
    }
    
    @Override
    public void create(Menus menu){
        menu.setFechaModificacion(MxDateTime.date());
        super.create(menu);
    }
    
    @Override
    public void edit(Menus menu){
        menu.setFechaModificacion(MxDateTime.date());
        super.edit(menu);
    }
    
    public List<Menus> findAvailableOnesForRol(Integer id){
        Query q = getEntityManager().createNativeQuery("SELECT m.* FROM menus m WHERE m.id_menu NOT IN (SELECT rm.id_menu FROM roles_has_menus rm WHERE rm.id_rol = ?)", Menus.class);
        q.setParameter(1, id);
        List<Menus> r = q.getResultList();
        log.info("findAvailableOnesForRol: " + r.size());
        
        return r;
    }
    
    public List<Menus> findActiveOnes(){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Menus> cq = builder.createQuery(Menus.class);
        Root<Menus> root = cq.from(Menus.class);
        
        cq.select(root).where(
                builder.isTrue(root.get("activo"))
        );
        
        TypedQuery<Menus> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnes: " + l.size());
        
        return l;
    }
    
    @Override
    public void remove(Menus menus) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM menus WHERE id_menu = ? ");
        q.setParameter(1, menus.getIdMenu());
        q.executeUpdate();
    }    
}
