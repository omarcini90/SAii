package mx.org.fimpes.security.mbean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.constraints.Pattern;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.saii.ejb.mail.MailService;
import mx.org.fimpes.saii.ejb.security.BitacoraEventosFacade;
import mx.org.fimpes.saii.ejb.security.CredencialesFacade;
import mx.org.fimpes.saii.ejb.security.RestablecimientoPasswordFacade;
import mx.org.fimpes.saii.ejb.security.UsuariosFacade;
import mx.org.fimpes.saii.ejb.security.crypto.Crypto;
import mx.org.fimpes.saii.model.mail.Mail;
import mx.org.fimpes.saii.model.security.BitacoraEventos;
import mx.org.fimpes.saii.model.security.Credenciales;
import mx.org.fimpes.saii.model.security.RestablecimientoPassword;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named(value = "passwordRecoveryMB")
@ViewScoped
public class PasswordRecoveryMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(PasswordRecoveryMB.class);
	private static final ResourceBundle MAIL_CONFIG = ResourceBundle.getBundle("Mail", Locale.getDefault(), Thread.currentThread().getContextClassLoader());

    private Credenciales credential;
    //[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", 
            message = "Por favor ingrese un correo electrónico válido.")
    private String email;
    private String token;
    
    @Resource
    UserTransaction utx;
    @EJB
    private CredencialesFacade credencialesFacade;
    @EJB
    private UsuariosFacade usuariosFacade;
    @EJB
    private RestablecimientoPasswordFacade passwordFacade;
    @EJB
    private BitacoraEventosFacade bitacoraFacade;
    @EJB
    private MailService mailService;
    
    /**
     * Creates a new instance of PasswordRecoveryMB
     */
    public PasswordRecoveryMB() {
    }
    
    @PostConstruct
    public void init() {
        var session = Context.session();
        if(session != null) {
        	token = (String)session.getAttribute("token");
        	if(token != null) {
        		var username = Crypto.decodeFromBase64(token).substring(2).split("\\|")[0];        		
            	credential = credencialesFacade.findByUsername(username);
            	log.info("Welcome back " + username);
        	}
        }
    }
    
    @PreDestroy
    public void destroy() {
        utx = null;
        credencialesFacade = null;
        bitacoraFacade = null;
        credential = null;        
    }
    
    public void restore(){
        var msg = "Restableció la contraseña ".concat(credential.getUsuario());
        try {
            utx.begin();
            credencialesFacade.edit(credential);
            passwordFacade.edit(token);
            bitacoraFacade.create(new BitacoraEventos(credential.getUsuarios(), msg, DefaultsMB.toJson(credential)));
            utx.commit();
            
            credential = null;
            token = null;
            Context.session().removeAttribute("token");
            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
            Context.openDialog(msg, DefaultsMB.SUCCESS_MESSAGE);
            PrimeFaces.current().executeScript("redirectTo('" + Context.path() + "');");
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
    
    private void sendMail(String subject, String token) throws Exception {
    	var m = new Mail();
    	m.setTo(email);
    	m.setSubject(subject);
    	var content = MessageFormat.format(MAIL_CONFIG.getString("msgrecovery"), Context.absolutePath(), email, Context.absolutePath().concat("/rest/security/recovery/").concat(token));
    	m.setContent(content);
    	mailService.send(m);
    }
    
    public void recovery(){
        var u = usuariosFacade.findByCorreo(email);
        if(u != null ){
        	var r = passwordFacade.findActiveOneBy(u);
        	if(r == null){
        		try {
        			var msg = "Solicitó la recuperación de su contraseña";
                    utx.begin();
                    r = new RestablecimientoPassword(u);
                    passwordFacade.create(r);
                    bitacoraFacade.create(new BitacoraEventos(u, msg, DefaultsMB.toJson(r)));
                    sendMail("SAii - Solictud de recuperación de contraseña", r.getToken());
                    utx.commit();
                    
                    email = null;
                    Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
                } catch (Exception e) {
                    try {
                        utx.rollback();
                    } catch (IllegalStateException | SecurityException | SystemException ex) {
                        log.error(ex.getMessage());
                    } finally {
                        Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
                    }
                }
        	}else{
        		try {
        			sendMail("SAii - Reenvío de solictud de recuperación de contraseña", r.getToken());
            		Context.sendInfo(null, "Ya cuenta con una solicitud de recuperación", "Se ha reenviado al correo " + email);
        		}catch(Exception e) {
        			Context.sendWarn(null, "Ya cuenta con una solicitud de recuperación, consulte su bandeja de correo. No se pudo reenviar una copia en este momento. " + e.getMessage(), DefaultsMB.FAILURE_MESSAGE);
        		}
        	}
        }else {
        	Context.sendWarn(null, "El correo " + email + " no ha sido registrado", "Contacte a su Administrador");
        }
    }
    
    public Credenciales getCredential() {
        return credential;
    }
    
    public void setCredential(Credenciales credential) {
        this.credential = credential;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
