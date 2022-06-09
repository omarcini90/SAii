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
import mx.org.fimpes.saii.model.core.Indicadores;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class IndicadoresFacade extends AbstractFacade<Indicadores> {
    private static final Logger log = Logger.getLogger(IndicadoresFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IndicadoresFacade() {
        super(Indicadores.class);
    }
    
    @Override
    public void create(Indicadores indicador){
        indicador.setFechaActualizacion(MxDateTime.date());
        super.create(indicador);
    }
    
    @Override
    public void edit(Indicadores indicador){
        log.info("ACTUALIZANDO INDICADOR");
        log.info(indicador);
        indicador.setFechaActualizacion(MxDateTime.date());
        super.edit(indicador);
    }
    
    public List<Indicadores> findAllTotals(){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Indicadores> cq = builder.createQuery(Indicadores.class);
        Root<Indicadores> root = cq.from(Indicadores.class);       
        
        cq.select(root).where(
                builder.and(builder.isTrue(root.get("esOperador")),
                            builder.isTrue(root.get("activo")))
        );        
        
        TypedQuery<Indicadores> q = getEntityManager().createQuery(cq);
        var i = q.getResultList();
        log.info("findAllTotals: " + i.size());
        
        return i;
    }
    
    public List<Indicadores> findActiveOnes(){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Indicadores> cq = builder.createQuery(Indicadores.class);
        Root<Indicadores> root = cq.from(Indicadores.class);
        
        cq.select(root).where(
                builder.isTrue(root.get("activo"))
        );
        
        TypedQuery<Indicadores> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnes: " + l.size());
        
        return l;
    }
    
    public List<Indicadores> findOperands(Integer idTotal){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Indicadores> cq = builder.createQuery(Indicadores.class);
        Root<Indicadores> root = cq.from(Indicadores.class);
        
        cq.select(root).where(
                builder.equal(root.get("idIndicadorTotal"), idTotal)
        );
        
        TypedQuery<Indicadores> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findOperands: " + l.size());
        
        return l;
    }
}
