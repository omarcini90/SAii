package mx.org.fimpes.saii.ejb.core;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.TipoRvoe;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class TipoRvoeFacade extends AbstractFacade<TipoRvoe> {
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoRvoeFacade() {
        super(TipoRvoe.class);
    }
    
}
