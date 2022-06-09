package mx.org.fimpes.security.mbean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.constraints.Pattern;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

import mx.org.fimpes.commons.Context;
import mx.org.fimpes.commons.DefaultsMB;
import mx.org.fimpes.saii.ejb.mail.MailService;
import mx.org.fimpes.saii.exceptions.ValidationException;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named(value = "mailController")
@SessionScoped
public class MailController implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MailController.class);
	private String host, port, name, password;
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", 
            message = "Por favor ingrese un correo electrónico válido.")
    private String user;
	private String fimpesusers;
	private Map<String, String> props;

	@Resource
    UserTransaction utx;
    @EJB
    private MailService mailService;
    @Inject
    LoginMB loginMB;
    
    public MailController() {
    }
    
    @PostConstruct
    public void init() {    	
    	props = mailService.getProperties();
    	propsToFields();
    }
    
    private void propsToFields() {
    	Field[] fields = this.getClass().getDeclaredFields();
    	props.forEach((k,v) -> {
    		for(Field f :fields) {
        		if(k.equals(f.getName())) {
        			try {
        				// Reflection: map's value -> member's value
						f.set(this, v);
						break;
					} catch (IllegalArgumentException | IllegalAccessException e) {
						log.error(e.getMessage());
					}
        		}
        	}
    	});
    }
    
	private void fieldsToProps() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (props.containsKey(f.getName())) {
				// Reflection: member's value -> map's value
				props.replace(f.getName(), f.get(this).toString());
			}
		}
	}
    
    public void save() {
    	var msg = "Actualizó la configuración del correo";
        try {
            utx.begin();
            fieldsToProps();
            mailService.updateProperties(props);
            loginMB.createEventLog(msg, props);
            utx.commit();

            Context.sendInfo(null, msg, DefaultsMB.SUCCESS_MESSAGE);
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                log.error(ex.getMessage());
            } finally {
            	log.error(e.getMessage());
            	if(e instanceof ValidationException) {            		
            		PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
            		Context.sendWarn(null, "No fué posible comunicarse con el Servidor de Correo", e.getMessage());
            	}else {
            		Context.sendError(null, DefaultsMB.FAILURE_MESSAGE, e.getMessage());
            	}
            }
        }
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<String> getFimpesusers() {
		return Arrays.asList(fimpesusers.split(","));
	}

	public void setFimpesusers(List<String> fimpesusers) {
		this.fimpesusers = String.join(",", fimpesusers);
	}

}
