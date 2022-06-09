package mx.org.fimpes.security.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*
* ServletRequestListener allows you to latch onto the j_security_check post request before it is fully initialized.
*/
@WebListener
public class RequestListener implements ServletRequestListener {
    private static final Logger log = Logger.getLogger(RequestListener.class);
    private static final String JAAS_MECHANISM = "j_security_check";
    public static final String JAAS_USERNAME = "j_username";
    public static final String JAAS_PASSWORD = "j_password";

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        //log.info("Request destroyed!");
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        /*log.info("Request initialized!");
        var request = (HttpServletRequest)sre.getServletRequest();
        if (request.getRequestURI().contains(JAAS_MECHANISM)) {
            final var username = request.getParameter(JAAS_USERNAME);
            final var password = request.getParameter(JAAS_PASSWORD);
			
            var session = request.getSession();
            session.setAttribute(JAAS_USERNAME, username);
            session.setAttribute(JAAS_PASSWORD, password);
        }*/
    }
}