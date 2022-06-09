package mx.org.fimpes.saii.ejb.security;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.core.Instituciones;
import mx.org.fimpes.saii.model.security.Usuarios;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class UsuariosFacade extends AbstractFacade<Usuarios> {
    private static final Logger log = Logger.getLogger(UsuariosFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuariosFacade() {
        super(Usuarios.class);
    }
    
    @Override
    public void create(Usuarios usuario){
        usuario.setFechaModificacion(MxDateTime.date());
        super.create(usuario);
    }
    
    @Override
    public void edit(Usuarios usuario){
        usuario.setFechaModificacion(MxDateTime.date());
        super.edit(usuario);
    }
    
    public Usuarios findByCorreo(String correo){
        TypedQuery<Usuarios> q = getEntityManager().createNamedQuery("Usuarios.findByEmail", Usuarios.class);
        q.setParameter("correo", correo);
        Usuarios u = null;
        try {
        	u = q.getSingleResult();
        }catch(NoResultException e) {
        	log.error(e.getMessage());
        }
        
        return u;
    }
    
    public List<Usuarios> findByInstitucion(Integer idInstitucion){
    	TypedQuery<Usuarios> q = getEntityManager().createQuery("SELECT u FROM Usuarios u WHERE u.idInstitucion.idInstitucion = :idInstitucion", Usuarios.class);
        q.setParameter("idInstitucion", idInstitucion);
        var l = q.getResultList();
        log.info("findByInstitucion: " + l.size());
        
        return l;
    }
    
    public List<Usuarios> findActiveOnes(){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Usuarios> cq = builder.createQuery(Usuarios.class);
        Root<Usuarios> root = cq.from(Usuarios.class);
        
        cq.select(root).where(
                builder.isTrue(root.get("activo"))
        );
        
        TypedQuery<Usuarios> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnes: " + l.size());
        
        return l;
    }
    
    public List<Usuarios> findActiveOnesBy(Instituciones institucion){
        var builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Usuarios> cq = builder.createQuery(Usuarios.class);
        Root<Usuarios> root = cq.from(Usuarios.class);
        
        cq.select(root).where(
                builder.and(builder.equal(root.get("idInstitucion"), institucion), 
                            builder.isTrue(root.get("activo")))
        );
        
        TypedQuery<Usuarios> q = getEntityManager().createQuery(cq);
        var l = q.getResultList();
        log.info("findActiveOnesByInstitucion: " + l.size());
        
        return l;
    }
    
    @Override
    public void remove(Usuarios usuario) {
    	Query q = getEntityManager().createNativeQuery("DELETE FROM usuarios WHERE id_usuario = ? ");
        q.setParameter(1, usuario.getIdUsuario());
        q.executeUpdate();
    }
}
