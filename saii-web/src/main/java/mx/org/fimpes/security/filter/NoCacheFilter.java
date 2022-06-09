package mx.org.fimpes.security.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public class NoCacheFilter implements Filter {
    
    private FilterConfig filterConfig = null;

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpResponse = (HttpServletResponse) response;
        //it sets the appropriate headers:
        httpResponse.setHeader("Cache-Control", "no-cache");
        httpResponse.setDateHeader("Expires", 0);
        httpResponse.setHeader("Pragma", "No-cache");
        //it invokes the next filter in the chain, in the order they are written in web.xml
        chain.doFilter(request, response);
    }
}
