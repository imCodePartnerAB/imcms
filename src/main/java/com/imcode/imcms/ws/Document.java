package com.imcode.imcms.ws;

import javax.ws.rs.*;

/**
 * Document web service
 */
@Path("/ws/doc")
public class Document {
    

    @GET
    @Path("{id : \\d+}/meta")
    @Produces("text/plain")
    public String getMeta(@PathParam("id") int id) {
        return "Meta: " + id;
    }


    @POST
    @Path("{id : \\d+}/meta")
    @Produces("text/plain")
    public String updateMeta(@PathParam("id") int id) {
        return "Meta: " + id;
    }    
}
