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
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.model.core.Programas;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class ProgramasFacade extends AbstractFacade<Programas> {
    private static final Logger log = Logger.getLogger(ProgramasFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProgramasFacade() {
        super(Programas.class);
    }
    
    @Override
    public void create(Programas programa){
        programa.setFechaActualizacion(MxDateTime.date());
        super.create(programa);
    }
    
    @Override
    public void edit(Programas programa){
        programa.setFechaActualizacion(MxDateTime.date());
        super.edit(programa);
    }
    
    public List<Programas> findActiveOnes(){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Programas> cq = builder.createQuery(Programas.class);
        Root<Programas> root = cq.from(Programas.class);
        
        cq.select(root).where(
                builder.isTrue(root.get("activo"))
        );
        
        TypedQuery<Programas> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnes: " + l.size());
        
        return l;
    }
    
    public List<Programas> findActiveOnesBy(Instituciones institucion){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Programas> cq = builder.createQuery(Programas.class);
        Root<Programas> root = cq.from(Programas.class);
        
        cq.select(root).where(
                builder.and(root.get("idCampus").in(institucion.getCampusList()), 
                            builder.isTrue(root.get("activo")))
        );
        
        TypedQuery<Programas> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnesByInstitucion: " + l.size());
        
        return l;
    }
    
    public List<Programas> findBy(Instituciones institucion){
         log.info("GENERANDO LISTA DE PROGRAMAS2");
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Programas> cq = builder.createQuery(Programas.class);
        Root<Programas> root = cq.from(Programas.class);
        
        cq.select(root).where(
                root.get("idCampus").in(institucion.getCampusList())
        );
        
        TypedQuery<Programas> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findByInstitucion: " + l.size());
        
        return l;
    }
    
    @Override
    public void remove(Programas programa) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM programas WHERE id_programa = ? ");
        q.setParameter(1, programa.getIdPrograma());
        q.executeUpdate();
    }
}
