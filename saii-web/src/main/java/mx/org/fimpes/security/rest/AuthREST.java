package mx.org.fimpes.security.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import mx.org.fimpes.saii.ejb.security.CredencialesFacade;
import mx.org.fimpes.saii.ejb.security.RestablecimientoPasswordFacade;
import mx.org.fimpes.saii.model.security.Roles;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Path("/security")
public class AuthREST {
    private static final Logger log = Logger.getLogger(AuthREST.class);

    @EJB
    CredencialesFacade credencialesFacade;
    @EJB
    private RestablecimientoPasswordFacade passwordFacade;
    @Context
    private HttpServletRequest request;
    @Context 
    private HttpServletResponse response;

    /**
     * Creates a new instance of AuthenticationREST
     */
    public AuthREST() {
    }

    /**
     * Autentica clientes externos a traves de HTTP (REST)
     *
     * @param usuario
     * @param password
     * @return JSON Response
     */
    @GET
    @Path("/login/{usuario}/{password}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON) // javax.ws.rs.core.Response;
    public Response autenticar(@PathParam(value = "usuario") String usuario, @PathParam(value = "password") String password) {
        ResponseBuilder rb;
        String json;
        log.info("Autenticando al usuario " + usuario + "...");
        try {
            /* Web service call:
            var service = new AuthenticationWS_Service();
            AuthenticationWS port = service.getAuthenticationWSPort();
            port.autenticar(usuario, password);*/
        	var user = credencialesFacade.findByCredentials(usuario, password);
            List<Roles> roles = null;
            if (user != null) {
                roles = new ArrayList<>(user.getRolesList());
            }

            if (roles != null && !roles.isEmpty()) {
                // JSON Parsing:
            	var arrayBuilder = Json.createArrayBuilder();
                roles.stream().forEach((rol) -> {
                	var jsonBuilder = Json.createObjectBuilder();
                	var c = Calendar.getInstance(MxDateTime.CDMX_TIMEZONE);
                    c.setTime(rol.getFechaModificacion());
                    jsonBuilder.add("rol", rol.getRol())
                            .add("descripcion", rol.getDescripcion())
                            .add("fechaModificacion", c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR));
                    arrayBuilder.add(jsonBuilder);
                });
                json = arrayBuilder.build().toString();                
            } else {
                json = Json.createObjectBuilder()
                        .add("mensaje", "No existen roles para el usuario " + usuario).build().toString();
            }
            rb = Response.ok().entity(json);
            log.info(json);
        } catch (Exception ex) {
            json = Json.createObjectBuilder()
                    .add("mensaje", ex.getMessage()).build().toString();
            rb = Response.status(503).entity(json);
        }

        return rb.build();
    }
    
    @GET
    @Path("/recovery/{token}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response recovery(@PathParam(value = "token") String token) {
    	ResponseBuilder rb = null;
    	String json;
    	if(token != null && !token.isEmpty()) {
    		var r = passwordFacade.findActiveOneBy(token);
    		if(r != null) {
				try {
					var session = request.getSession(true);
					session.setAttribute("token", token);
					// request.getServletContext().getRequestDispatcher("/faces/restorePassword.xhtml").forward(request,
					// response);
					response.sendRedirect(request.getContextPath() + "/faces/restorePassword.xhtml");
					rb = Response.ok().type(MediaType.TEXT_HTML);
				} catch (IOException e) {
					log.error(e.getMessage());
					json = Json.createObjectBuilder()
							.add("Aviso","Lo sentimos, su solicitud no pudo ser completada. Intentelo más tarde por favor")
							.add("Error", e.getMessage()).build().toString();
					rb = Response.status(503).type(MediaType.APPLICATION_JSON).entity(json);
				}
    		}else {
    			var jb = Json.createObjectBuilder()
    										.add("Aviso", "Lo sentimos, su solicitud ya ha sido atendida y/o ha expirado!")
    										.add("Accion", "Solicite nuevamente la recuperación de su contraseña");
    			try {
    				var u = new URI(request.getRequestURL().toString());
    				var uri = new URI(u.getScheme(), null, u.getHost(), u.getPort(), request.getContextPath(),null, null);
    				jb.add("Direccion", uri.toString());
    			}catch(Exception e) {
    				log.error(e.getMessage());
    			}finally {
    				json = jb.build().toString();
            		rb = Response.ok().type(MediaType.APPLICATION_JSON).entity(json);
    			}
    		}
    	}else {
    		json = Json.createObjectBuilder()
    				.add("Aviso", "Es necesario proporcionar el token que le fué enviado por correo")
    				.build().toString();
    		rb = Response.status(400).type(MediaType.APPLICATION_JSON).entity(json);
    	}
    	
    	return rb.build();
    }
}
