package mx.org.fimpes.saii.ejb.security;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.model.security.BitacoraEventos;
import mx.org.fimpes.saii.util.MxDateTime;
import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class BitacoraEventosFacade extends AbstractFacade<BitacoraEventos> {
    
    private static final Logger log = Logger.getLogger(BitacoraEventosFacade.class);

    @PersistenceContext(unitName = "SAiiPersistanceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BitacoraEventosFacade() {
        super(BitacoraEventos.class);
    }
    
    @Override
    public void create(BitacoraEventos evento){
        evento.setFecha(MxDateTime.date());
        super.create(evento);
        log.info("Event logged: " + evento.toString());
    }
}
