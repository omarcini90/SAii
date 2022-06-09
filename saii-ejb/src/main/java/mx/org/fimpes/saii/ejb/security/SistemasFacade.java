package mx.org.fimpes.saii.ejb.security;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.security.Sistemas;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class SistemasFacade extends AbstractFacade<Sistemas> {
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public SistemasFacade() {
        super(Sistemas.class);
    }
    
    @Override
    public void create(Sistemas sistema){
        sistema.setFechaModificacion(MxDateTime.date());
        super.create(sistema);
    }
    
    @Override
    public void edit(Sistemas sistema){
        sistema.setFechaModificacion(MxDateTime.date());
        super.edit(sistema);
    }
}
