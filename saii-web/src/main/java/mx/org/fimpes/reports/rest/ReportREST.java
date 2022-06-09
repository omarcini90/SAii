package mx.org.fimpes.reports.rest;

import java.io.InputStream;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import mx.org.fimpes.commons.DefaultsMB;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Path("/reports")
@RequestScoped
public class ReportREST {
    private static final Logger log = Logger.getLogger(ReportREST.class);
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ReportREST()
     */
    public ReportREST() {
    }

    /**
     * @return a file
     */
    @GET
    @Path("/pdf/fimpes/{folio}")
    @Produces("application/pdf")
    public Response getOficio(@PathParam("folio") String folio) throws Exception {
        log.info("Obteniendo documento firmado digitalmente...");
        //Object o = facade.findByFolio(folio);
        String ruta = folio; //o.getRuta();
        InputStream stream = DefaultsMB.readFileFromDisk(ruta).getStream();
        Response.ResponseBuilder responseBuilder = Response.ok((Object) stream);
        responseBuilder.type("application/pdf");
        responseBuilder.header("Content-Disposition", "filename=OF" + folio + ".pdf");
        log.info("OK!");
        
        return responseBuilder.build();
    }
}
