package mx.org.fimpes.saii.ejb.core;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.Acreditadoras;

/**
 *
 * @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
 */
@Stateless
@LocalBean
public class AcreditadorasFacade extends AbstractFacade<Acreditadoras> {
	
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AcreditadorasFacade() {
        super(Acreditadoras.class);
    }
    
    public List<Acreditadoras> findAvailableOnesForInstitute(Integer id){
        Query q = getEntityManager().createNativeQuery("SELECT a.* FROM acreditadoras a WHERE a.id_acreditadora NOT IN "
        												+ "(SELECT ia.id_acreditadora FROM instituciones_has_acreditadoras ia WHERE ia.id_institucion = ?)", Acreditadoras.class);
        q.setParameter(1, id);
        List<Acreditadoras> a = q.getResultList();
        
        return a;
    }
    
    public List<Acreditadoras> findAvailableOnesForProgram(Integer id){
        Query q = getEntityManager().createNativeQuery("SELECT a.* FROM acreditadoras a WHERE a.id_acreditadora NOT IN "
        												+ "(SELECT pa.id_acreditadora FROM programas_has_acreditadoras pa WHERE pa.id_programa = ?)", Acreditadoras.class);
        q.setParameter(1, id);
        List<Acreditadoras> a = q.getResultList();
        
        return a;
    }
}
