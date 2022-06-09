package mx.org.fimpes.saii.ejb.core;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.Seccion;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class SeccionFacade extends AbstractFacade<Seccion> {
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SeccionFacade() {
        super(Seccion.class);
    }
    
}
