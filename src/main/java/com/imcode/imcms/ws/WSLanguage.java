package com.imcode.imcms.ws;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 */
@Path("/language")
public class WSLanguage {


    @GET
    @Path("")
    @Produces("application/json")
    public List<I18nLanguage> getAll() {
       LanguageDao dao = (LanguageDao)Imcms.getSpringBean("languageDao");
       return dao.getAllLanguages();
    }

    
    @GET
    @Path("default")
    @Produces("application/json")
    public I18nLanguage getDefault() {
        //LanguageDao dao = (LanguageDao)Imcms.getSpringBean("languageDao");
        return Imcms.getI18nSupport().getDefaultLanguage();
    }

    
    @GET // POST
    @Path("{id}")
    @Produces("application/json")
    public I18nLanguage get(@PathParam("id") int id) {
        return Imcms.getI18nSupport().getById(id);
    }


    @POST // POST
    @Path("${id}")
    @Consumes("application/json")
    public void update(@PathParam("id") int id) {
        //return Imcms.getI18nSupport().getById(id);
    }


    @DELETE // POST
    @Path("${id}")
    @Consumes("application/json")
    public void delete(@PathParam("id") int id) {
        LanguageDao dao = (LanguageDao)Imcms.getSpringBean("languageDao");
        // delete
    }
}