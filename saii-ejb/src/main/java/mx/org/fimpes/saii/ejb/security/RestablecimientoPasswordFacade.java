package mx.org.fimpes.saii.ejb.security;

import java.util.Calendar;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.ejb.security.crypto.Crypto;
import mx.org.fimpes.saii.model.security.RestablecimientoPassword;
import mx.org.fimpes.saii.model.security.Usuarios;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class RestablecimientoPasswordFacade extends AbstractFacade<RestablecimientoPassword> {
    private static final Logger log = Logger.getLogger(RestablecimientoPasswordFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RestablecimientoPasswordFacade() {
        super(RestablecimientoPassword.class);
    }
    
    @Override
    public void create(RestablecimientoPassword r){
    	var c = Calendar.getInstance(MxDateTime.CDMX_TIMEZONE);
    	r.setRestablecido(Boolean.FALSE);
        r.setFechaSolicitud(c.getTime());
        // Se tienen 3 días para el restablecimiento de la contraseña:
        c.add(Calendar.DATE, 3);
        r.setFechaCaducidad(c.getTime());
        r.setCadenaOriginal("||" + r.getIdUsuario().getCredenciales().getUsuario() 
        					+ "|" + MxDateTime.DATETIME_FORMAT.format(r.getFechaSolicitud()) 
        					+ "|" + MxDateTime.DATE_FORMAT.format(r.getFechaCaducidad()) 
        					+ "||");
        r.setToken(Crypto.encodeToBase64(r.getCadenaOriginal()));        
        super.create(r);
        log.info("create: " + r.toString());
    }
    
    @Override
    public void edit(RestablecimientoPassword r){
        r.setFechaRestablecimiento(MxDateTime.date());
        r.setRestablecido(Boolean.TRUE);
        super.edit(r);
    }
    
    public void edit(String token){
    	TypedQuery<RestablecimientoPassword> q = getEntityManager().createNamedQuery("RestablecimientoPassword.findByToken", RestablecimientoPassword.class);
    	q.setParameter("token", token);
    	edit(q.getSingleResult());
    }
    
    public RestablecimientoPassword findActiveOneBy(Usuarios usuario) {
    	var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RestablecimientoPassword> cq = builder.createQuery(RestablecimientoPassword.class);
        Root<RestablecimientoPassword> root = cq.from(RestablecimientoPassword.class);
        var now = MxDateTime.dateOnly();
        
        cq.select(root).where(
                builder.and(builder.equal(root.get("idUsuario"), usuario), 
                            builder.isFalse(root.get("restablecido")),
                            builder.greaterThanOrEqualTo(root.get("fechaCaducidad"), now))
        );
        
        TypedQuery<RestablecimientoPassword> q = getEntityManager().createQuery(cq);
        RestablecimientoPassword r = null;
        try {
        	r = q.getSingleResult();
        }catch(NoResultException e) {
        	log.error(e.getMessage());
        }
        
        return r;
    }
    
    public RestablecimientoPassword findActiveOneBy(String token) {
    	var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RestablecimientoPassword> cq = builder.createQuery(RestablecimientoPassword.class);
        Root<RestablecimientoPassword> root = cq.from(RestablecimientoPassword.class);
        var now = MxDateTime.dateOnly();
        
        cq.select(root).where(
                builder.and(builder.equal(root.get("token"), token), 
                            builder.isFalse(root.get("restablecido")),
                            builder.greaterThanOrEqualTo(root.get("fechaCaducidad"), now))
        );
        
        TypedQuery<RestablecimientoPassword> q = getEntityManager().createQuery(cq);
        RestablecimientoPassword r = null;
        try {
        	r = q.getSingleResult();
        }catch(NoResultException e) {
        	log.error(e.getMessage());
        }
        
        return r;
    }

}
