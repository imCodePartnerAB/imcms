package com.imcode.imcms.ws;

import com.imcode.imcms.api.DocumentLabels;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.WebServiceContext;

/**
 * Document web service
 */
@Path("/doc")
public class WSDoc {

    @XmlRootElement
    public static class WSLabels {
        
        public String headline;

        public String menuText;
    
        public String menuImageURL;
    }

    
    @GET
    @Path("{id}/labels/xml")
    //@Produces({"application/xml", "application/json"})
    @Produces("application/xml")
    public WSLabels getLabelsXml(@PathParam("id") int id) {
        DocumentMapper dm = Imcms.getServices().getDocumentMapper();

        DocumentDomainObject doc = dm.getDocument(id);

        if (doc == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        WSLabels wsLabels = new WSLabels();
        wsLabels.headline = doc.getHeadline();
        wsLabels.menuText = doc.getMenuText();
        wsLabels.menuImageURL = doc.getMenuImage();

        return wsLabels;
    }


    @GET
    @Path("{id}/labels/json")
    //@Produces({"application/xml", "application/json"})
    @Produces("application/json")
    public WSLabels getLabelsJson(@PathParam("id") int id) {
        return getLabelsXml(id);
    }


    @POST
    @Path("{id}/labels")
    @Consumes("application/x-www-form-urlencoded")
    public void updateLabels(@PathParam("id") int id,
                             @FormParam("headline") String headline,
                             @FormParam("menuText") String menuText,
                             @FormParam("menuImageURL") String menuImageURL) {

        DocumentMapper dm = Imcms.getServices().getDocumentMapper();
        DocumentDomainObject doc = dm.getDocument(id);

        if (doc == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }        

        doc.setHeadline(headline);
        doc.setMenuText(menuText);
        doc.setMenuImage(menuImageURL);

        UserDomainObject admin = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUser(1);

        try {
            dm.saveDocument(doc, admin);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    @POST
    @Path("{id}/labels/xml")
    @Consumes("application/xml")
    public void updateLabelsXml(@PathParam("id") int id, WSLabels wsLabels) {

        DocumentMapper dm = Imcms.getServices().getDocumentMapper();
        DocumentDomainObject doc = dm.getDocument(id);

        if (doc == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        doc.setHeadline(wsLabels.headline);
        doc.setMenuText(wsLabels.menuText);
        doc.setMenuImage(wsLabels.menuImageURL);

        UserDomainObject admin = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUser(1);

        try {
            dm.saveDocument(doc, admin);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    @POST
    @Path("{id}/labels/json")
    @Consumes("application/json")
    public void updateLabelsJson(@PathParam("id") int id, WSLabels wsLabels) {
        updateLabelsXml(id, wsLabels);
    }


    @GET
    @Path("{id}/perms/json")
    //@Produces({"application/xml", "application/json"})
    @Produces("application/json")
    public WSLabels getPermissions(@PathParam("id") int id) {
        DocumentMapper dm = Imcms.getServices().getDocumentMapper();
        DocumentDomainObject doc = dm.getDocument(id);

        return null;
    }
}
