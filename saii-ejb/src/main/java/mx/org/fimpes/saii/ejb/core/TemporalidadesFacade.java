package mx.org.fimpes.saii.ejb.core;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.Temporalidades;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class TemporalidadesFacade extends AbstractFacade<Temporalidades> {
	private static final Logger log = Logger.getLogger(TemporalidadesFacade.class);
	
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TemporalidadesFacade() {
        super(Temporalidades.class);
    }
    
    public List<Temporalidades> findAvailableOnesForInstitute(Integer id){
        Query q = getEntityManager().createNativeQuery("SELECT t.* FROM temporalidades t WHERE t.id_temporalidad NOT IN "
        												+ "(SELECT it.id_temporalidad FROM instituciones_has_temporalidades it WHERE it.id_institucion = ?)", Temporalidades.class);
        q.setParameter(1, id);
        List<Temporalidades> t = q.getResultList();
        log.info("findAvailableOnesForInstitute: " + t.size());
        
        return t;
    }
    
}
