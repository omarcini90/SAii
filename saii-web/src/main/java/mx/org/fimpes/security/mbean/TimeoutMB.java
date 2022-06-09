package mx.org.fimpes.security.mbean;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.URISyntaxException;
import javax.annotation.PostConstruct;
import mx.org.fimpes.commons.Context;
import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named(value = "timeoutMB")
@SessionScoped
public class TimeoutMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(TimeoutMB.class);
    private static final int ONE_SEC = 1000;
    private static final int ONE_MIN = 60;
    private static final int TWO_MIN = 120;
    private int timeout;    
    
    public TimeoutMB() {
    }
    
    @PostConstruct
    public void init(){        
        log.info("MaxInactiveInterval (web.xml): " + Context.session().getMaxInactiveInterval()/ONE_MIN + " min.");
        timeout = Context.session().getMaxInactiveInterval() - TWO_MIN;
        log.info("Timeout: " + timeout/ONE_MIN + " min."); // 20 min.
    }
    
    public void keepSessionAlive() {
        log.info("Session: " + Context.session().getId() + " still alive!");
    }
    
    public String logout() {
        return "/logout.xhtml";
    }
    
    public void onTimerComplete(){
        log.info("Saving state....");
        try{
            var jsession = Context.session().getId();
            Context.closeSession();
            log.info("Session: " + jsession + " destroyed!");
        }catch(IllegalStateException e){
            log.error("Session had been invalidated already. " + e.getMessage());
        }finally{
            try {
                var url = Context.absolutePath();
                Context.external().redirect(url);
                log.info("Redirecting -> " + url);
            } catch (IOException | URISyntaxException ex) {
                log.error(ex.getMessage());
            }
        }
    }
    
    public int getIdleTimeout(){
        return ONE_SEC;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
