package mx.org.fimpes.security.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@WebListener
public class SessionListener implements HttpSessionListener{
    private static final Logger log = Logger.getLogger(SessionListener.class);
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        var s = se.getSession();
        log.info("Session: " + s.getId() + " created at " + MxDateTime.dateFrom(s.getCreationTime()));
    }
    
    // Called just before session expires:
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {        
        var s = se.getSession();
        log.info("Session: " + s.getId() + " is being invalidated at " + MxDateTime.now());
    }
    
}
