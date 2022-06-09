package mx.org.fimpes.saii.ejb.core;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.Afiliaciones;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class AfiliacionesFacade extends AbstractFacade<Afiliaciones> {
	
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AfiliacionesFacade() {
        super(Afiliaciones.class);
    }
    
    public List<Afiliaciones> findAvailableOnesForInstitute(Integer id){
        Query q = getEntityManager().createNativeQuery("SELECT a.* FROM afiliaciones a WHERE a.id_afiliacion NOT IN "
        												+ "(SELECT ia.id_afiliacion FROM instituciones_has_afiliaciones ia WHERE ia.id_institucion = ?)", Afiliaciones.class);
        q.setParameter(1, id);
        List<Afiliaciones> a = q.getResultList();
        
        return a;
    }
    
}
