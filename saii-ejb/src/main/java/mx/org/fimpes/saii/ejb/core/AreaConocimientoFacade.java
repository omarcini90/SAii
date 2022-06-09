package mx.org.fimpes.saii.ejb.core;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.AreaConocimiento;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class AreaConocimientoFacade extends AbstractFacade<AreaConocimiento> {
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AreaConocimientoFacade() {
        super(AreaConocimiento.class);
    }
    
}
