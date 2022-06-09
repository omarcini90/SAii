package mx.org.fimpes.saii.ejb.core;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.Ciclos;
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.model.core.PeriodoAcceso;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class PeriodoAccesoFacade extends AbstractFacade<PeriodoAcceso> {
    private static final Logger log = Logger.getLogger(PeriodoAccesoFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PeriodoAccesoFacade() {
        super(PeriodoAcceso.class);
    }

    @Override
    public void create(PeriodoAcceso periodo) {
        periodo.setFechaActualizacion(MxDateTime.date());
        super.create(periodo);
    }

    @Override
    public void edit(PeriodoAcceso periodo) {
        periodo.setFechaActualizacion(MxDateTime.date());
        super.edit(periodo);
    }
    
    public PeriodoAcceso findExistence(PeriodoAcceso periodo) {
        return findBy(periodo.getIdInstitucion(), periodo.getIdCiclo(), periodo.getAnio());
    }

    public PeriodoAcceso findBy(Instituciones institucion, Ciclos ciclo, short anio) {
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PeriodoAcceso> cq = builder.createQuery(PeriodoAcceso.class);
        Root<PeriodoAcceso> root = cq.from(PeriodoAcceso.class);

        cq.select(root).where(
                builder.and(builder.equal(root.get("idInstitucion"), institucion),
                        builder.equal(root.get("idCiclo"), ciclo),
                        builder.equal(root.get("anio"), anio))
        );

        TypedQuery<PeriodoAcceso> q = getEntityManager().createQuery(cq);
        PeriodoAcceso p = null;
        try {
            p = q.getSingleResult();
        } catch (NoResultException e) {
        	log.error(e.getMessage());
        }
        
        return p;
    }
    
    public List<PeriodoAcceso> findCurrentPeriodsBy(Instituciones institucion) {
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PeriodoAcceso> cq = builder.createQuery(PeriodoAcceso.class);
        Root<PeriodoAcceso> root = cq.from(PeriodoAcceso.class);
        var now = MxDateTime.dateOnly();
        
        cq.select(root).where(
                builder.and(builder.equal(root.get("idInstitucion"), institucion),
                        builder.lessThanOrEqualTo(root.get("fechaInicio"), now),
                        builder.greaterThanOrEqualTo(root.get("fechaFin"), now))
        ).orderBy(builder.desc(root.get("idPeriodoAcceso")));
        
        TypedQuery<PeriodoAcceso> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findCurrentPeriodsByInstitucion: " + l.size());
        
        return l;
    }
    
    @Override
    public void remove(PeriodoAcceso periodo) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM periodo_acceso WHERE id_periodo_acceso = ? ");
        q.setParameter(1, periodo.getIdPeriodoAcceso());
        q.executeUpdate();
    }
}
