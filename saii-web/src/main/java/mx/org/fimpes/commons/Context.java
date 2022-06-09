package mx.org.fimpes.commons;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public abstract class Context {

    public static void sendInfo(String clientId, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    public static void sendError(String clientId, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    public static void sendWarn(String clientId, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }

    public static void openDialog(String summary, String detail) {
    	PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }
    
    public static void openWarnDialog(String summary, String detail) {
    	PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }

    public static void openDialog(String xhtml, Map<String, Object> options1, Map<String, List<String>> options2) {
    	PrimeFaces.current().dialog().openDynamic(xhtml, options1, options2);
    }
    
    public static String path() {
    	HttpServletRequest request = (HttpServletRequest) external().getRequest();
    	return request.getContextPath();
    }
    
    public static String absolutePath() throws MalformedURLException, URISyntaxException {
        /* // Absolute URL:
        String file = request.getRequestURI();
        if (request.getQueryString() != null) {
            file += '?' + request.getQueryString();
        }
        URL reconstructedURL = new URL(request.getScheme(),request.getServerName(), request.getServerPort(), file);*/
        HttpServletRequest request = (HttpServletRequest) external().getRequest();
        URI uri = new URI(request.getRequestURL().toString());
        URI newUri = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), request.getContextPath(),null, null);
        
        return newUri.toString();
    }

    public static ExternalContext external() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public static RequestContext primefaces() {
        return RequestContext.getCurrentInstance();
    }

    public static HttpSession session() {
        return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    }
    
    public static HttpServletResponse response() {
        return (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
    }

    public static void responseComplete() {
        FacesContext.getCurrentInstance().responseComplete();
    }

    public static void closeSession() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
}
