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
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class InstitucionesFacade extends AbstractFacade<Instituciones> {
    private static final Logger log = Logger.getLogger(InstitucionesFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InstitucionesFacade() {
        super(Instituciones.class);
    }
    
    @Override
    public void create(Instituciones institucion){
        institucion.setFechaActualizacion(MxDateTime.date());
        super.create(institucion);
    }
    
    @Override
    public void edit(Instituciones institucion){
        institucion.setFechaActualizacion(MxDateTime.date());
        super.edit(institucion);
    }
    
    public List<Instituciones> findActiveOnes(){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Instituciones> cq = builder.createQuery(Instituciones.class);
        Root<Instituciones> root = cq.from(Instituciones.class);
        
        cq.select(root).where(
                builder.isTrue(root.get("activa"))
        );
        
        TypedQuery<Instituciones> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnes: " + l.size());
        
        return l;
    }
    
    @Override
    public void remove(Instituciones institucion) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM instituciones WHERE id_institucion = ? ");
        q.setParameter(1, institucion.getIdInstitucion());
        q.executeUpdate();
    }
}
