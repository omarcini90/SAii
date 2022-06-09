package mx.org.fimpes.saii.ejb.mail;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.mail.MailConfig;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class MailFacade extends AbstractFacade<MailConfig> {
    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MailFacade() {
        super(MailConfig.class);
    }
    
    public MailConfig findByKey(String key) {
    	TypedQuery<MailConfig> q = getEntityManager().createNamedQuery("MailConfig.findByPropiedad", MailConfig.class);
        q.setParameter("propiedad", key);
        return q.getSingleResult();
    }
}
