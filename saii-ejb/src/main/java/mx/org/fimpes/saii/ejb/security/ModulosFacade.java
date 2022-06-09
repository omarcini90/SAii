package mx.org.fimpes.saii.ejb.security;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.security.Modulos;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class ModulosFacade extends AbstractFacade<Modulos> {
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ModulosFacade() {
        super(Modulos.class);
    }
    
    @Override
    public void create(Modulos modulo){
        modulo.setFechaModificacion(MxDateTime.date());
        super.create(modulo);
    }
    
    @Override
    public void edit(Modulos modulo){
        modulo.setFechaModificacion(MxDateTime.date());
        super.edit(modulo);
    }
    
    @Override
    public void remove(Modulos modulo) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM modulos WHERE id_modulo = ? ");
        q.setParameter(1, modulo.getIdModulo());
        q.executeUpdate();
    }
}
