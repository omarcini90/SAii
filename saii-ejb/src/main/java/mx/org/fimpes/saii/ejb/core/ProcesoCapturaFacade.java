package mx.org.fimpes.saii.ejb.core;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.model.core.ProcesoCaptura;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class ProcesoCapturaFacade extends AbstractFacade<ProcesoCaptura> {
    private static final Logger log = Logger.getLogger(ProcesoCapturaFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public ProcesoCapturaFacade() {
        super(ProcesoCaptura.class);
    }
    
    @Override
    public void create(ProcesoCaptura proceso) {
        proceso.setFechaInicio(MxDateTime.date());
        proceso.setCompletado(false);
        super.create(proceso);
    }

    @Override
    public void edit(ProcesoCaptura proceso) {
        proceso.setFechaFin(MxDateTime.date());
        proceso.setCompletado(true);
        super.edit(proceso);
    }
    
    @Override
    public List<ProcesoCaptura> findAll(){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProcesoCaptura> cq = builder.createQuery(ProcesoCaptura.class);
        Root<ProcesoCaptura> root = cq.from(ProcesoCaptura.class);
        
        cq.select(root).orderBy(builder.desc(root.get("idProceso")));
        
        TypedQuery<ProcesoCaptura> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findAll: " + l.size());
        
        return l;
    }
    
    public List<ProcesoCaptura> findBy(Instituciones institucion){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProcesoCaptura> cq = builder.createQuery(ProcesoCaptura.class);
        Root<ProcesoCaptura> root = cq.from(ProcesoCaptura.class);
        
        cq.select(root).where(
                builder.equal(root.get("idInstitucion"), institucion)
        ).orderBy(builder.desc(root.get("idProceso")));
        
        TypedQuery<ProcesoCaptura> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findByInstitucion: " + l.size());
        
        return l;
    }
}
