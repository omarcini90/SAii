package mx.org.fimpes.rs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@ApplicationPath("/rest")
public class ApplicationConfig extends Application {
    
	@Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(mx.org.fimpes.security.rest.AuthREST.class);
        resources.add(mx.org.fimpes.reports.rest.ReportREST.class);
        return resources;
    }
}