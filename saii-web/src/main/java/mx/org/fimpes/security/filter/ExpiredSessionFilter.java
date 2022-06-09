package mx.org.fimpes.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@WebFilter("/*")
public class ExpiredSessionFilter implements Filter {

    private static final Logger log = Logger.getLogger(ExpiredSessionFilter.class);
    private FilterConfig filterConfig;

    public ExpiredSessionFilter() {
    }

    /**
     * Init method for this filter
     *
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
    
    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var session = httpRequest.getSession(false);
        
        // We redirect all non-rest requests with no active session
        if (session == null && !httpRequest.getServletPath().startsWith("/rest")) {
            log.info("Session has expired! Redirecting -> " + httpRequest.getContextPath());
            var httpResponse = (HttpServletResponse) response;
            httpResponse.sendRedirect(httpRequest.getContextPath());
        } else {
            chain.doFilter(request, response);
        }
    }
}
