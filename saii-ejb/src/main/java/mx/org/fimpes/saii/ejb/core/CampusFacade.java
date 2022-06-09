package mx.org.fimpes.saii.ejb.core;

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
import mx.org.fimpes.saii.model.core.Campus;
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class CampusFacade extends AbstractFacade<Campus> {
    private static final Logger log = Logger.getLogger(CampusFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CampusFacade() {
        super(Campus.class);
    }
    
    @Override
    public void create(Campus campus){
        campus.setFechaActualizacion(MxDateTime.date());
        super.create(campus);
    }
    
    @Override
    public void edit(Campus campus){
        campus.setFechaActualizacion(MxDateTime.date());
        super.edit(campus);
    }
    
    public List<Campus> findActiveOnes(){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Campus> cq = builder.createQuery(Campus.class);
        Root<Campus> root = cq.from(Campus.class);
        
        cq.select(root).where(
                builder.isTrue(root.get("activo"))
        );
        
        TypedQuery<Campus> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnes: " + l.size());
        
        return l;
    }
    
    public List<Campus> findActiveOnesBy(Instituciones institucion){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Campus> cq = builder.createQuery(Campus.class);
        Root<Campus> root = cq.from(Campus.class);
        
        cq.select(root).where(
                builder.and(builder.equal(root.get("idInstitucion"), institucion), 
                            builder.isTrue(root.get("activo")))
        );
        
        TypedQuery<Campus> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnesByInstitucion: " + l.size());
        
        return l;
    }
    
    public List<Campus> findBy(Instituciones institucion){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Campus> cq = builder.createQuery(Campus.class);
        Root<Campus> root = cq.from(Campus.class);
        
        cq.select(root).where(
                builder.equal(root.get("idInstitucion"), institucion)
        );
        
        TypedQuery<Campus> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findByInstitucion: " + l.size());
        
        return l;
    }
    
    @Override
    public void remove(Campus campus) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM campus WHERE id_campus = ? ");
        q.setParameter(1, campus.getIdCampus());
        q.executeUpdate();
    }
}
