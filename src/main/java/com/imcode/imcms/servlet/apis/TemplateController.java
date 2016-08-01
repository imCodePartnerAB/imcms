package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.imagearchive.service.Facade;
import imcode.server.Imcms;
import imcode.server.document.TemplateDomainObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * Created by Shadowgun on 26.02.2015.
 */
@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    Facade facade;

    @RequestMapping
    protected Object getTemplatesList() {
    //TODO REMOVE Testing stuff
            String str1 = facade.getLinkService().get("document.read","2331");
            String str3 = facade.getLinkService().forward("document.read","2331");
            String str4 = facade.getLinkService().forward("document.read","2331","draft");
            String str5 = facade.getLinkService().forward("document.read","2331","draft");


        return Imcms.getServices().getTemplateMapper().getAllTemplates()
                .stream()
                .collect(Collectors.toMap(TemplateDomainObject::getNameAdmin , TemplateDomainObject::getNameAdmin));
    }
}
