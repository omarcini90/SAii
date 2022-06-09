package mx.org.fimpes.security.mbean;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.saii.ejb.security.BitacoraEventosFacade;
import mx.org.fimpes.saii.ejb.security.CredencialesFacade;
import mx.org.fimpes.saii.model.security.BitacoraEventos;
import mx.org.fimpes.saii.model.security.Credenciales;
import mx.org.fimpes.saii.model.security.Roles;
import mx.org.fimpes.saii.model.security.Usuarios;
import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named(value = "loginMB")
@SessionScoped
public class LoginMB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(LoginMB.class);

    private Credenciales credential;
    private List<String> roles;
    private BitacoraEventos selectedEvent;
    private List<BitacoraEventos> filteredEvents;

    @Resource
    UserTransaction utx;
    @EJB
    private CredencialesFacade credencialesFacade;
    @EJB
    private BitacoraEventosFacade bitacoraFacade;

    /**
     * Creates a new instance of LoginMB
     */
    public LoginMB() {
    }

    @PostConstruct
    public void init() {
        credential = credencialesFacade.findByUsername(getPrincipal());
        try {
            log.info("Welcome " + getFullName() + " to " + Context.absolutePath());
        } catch (MalformedURLException | URISyntaxException e) {
            log.info("Welcome " + getFullName());
        }
        roles = new ArrayList<String>();
        getRoles().forEach((r) -> {
            roles.add(r.getRol());
        });
    }

    @PreDestroy
    public void destroy() {
        utx = null;
        credencialesFacade = null;
        bitacoraFacade = null;
    }

    public String getPrincipal() {
        return Context.external().getUserPrincipal().getName();
    }
    
    public boolean isUserInRol(String rol) {
        // RolxUser: Context.getExternal().isUserInRole(rol);
        return roles.contains(rol);
    }
    
    public boolean isFimpesRol(){
        return isUserInRol("ADMIN_FIMPES") || isUserInRol("SUPERVISOR_FIMPES");
    }

    public Usuarios getAuthenticatedUser() {
        return credential.getUsuarios();
    }

    public String getFullName() {
        return credential.getUsuarios().toString();
    }

    public List<Roles> getRoles() {
        return credential.getRolesList();
    }

    public void changePassword() {
        var msg = "Actualizó la contraseña ".concat(credential.getUsuario());
        try {
            utx.begin();
            credencialesFacade.edit(credential);
            createEventLog(msg, credential);
            utx.commit();

            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            } finally {
                Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
            }
        }
    }

    public void createEventLog(String resume, Object data) {
        var evento = new BitacoraEventos(credential.getUsuarios(), resume, DefaultsMB.toJson(data));
        bitacoraFacade.create(evento);
    }

    public List<BitacoraEventos> getEventLog() {
        List<BitacoraEventos> b;
        if (isFimpesRol()) {
            b = bitacoraFacade.findAll();
        } else { 
        	// Updating the lists:
        	credential = credencialesFacade.find(credential.getIdCredencial());
        		if (isUserInRol("ADMIN_INSTITUTO")) {
        			List<Usuarios> usuarios =  credential.getUsuarios().getIdInstitucion().getUsuariosList();
        			b = new ArrayList<BitacoraEventos>();
        			usuarios.forEach((u) -> {
        				b.addAll(u.getBitacoraEventosList());
        			});
        		} else {       	
        			b = credential.getUsuarios().getBitacoraEventosList();
        		}
        }
        b.sort(Comparator.comparing(BitacoraEventos::getIdBitacora).reversed());

        return b;
    }

    public Credenciales getCredential() {
        return credential;
    }

    public void setCredential(Credenciales credential) {
        this.credential = credential;
    }

    public BitacoraEventos getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(BitacoraEventos selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public List<BitacoraEventos> getFilteredEvents() {
        return filteredEvents;
    }

    public void setFilteredEvents(List<BitacoraEventos> filteredEvents) {
        this.filteredEvents = filteredEvents;
    }
}
