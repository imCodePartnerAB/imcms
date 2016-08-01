package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.imagearchive.service.Facade;
import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import imcode.server.Imcms;
import imcode.server.document.TemplateDomainObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NameNotFoundException;
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

        try {
            facade.getLinkService().find("readDoc","2331");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (WrongNumberArgsException e) {
            e.printStackTrace();
        }


        return Imcms.getServices().getTemplateMapper().getAllTemplates()
                .stream()
                .collect(Collectors.toMap(TemplateDomainObject::getNameAdmin , TemplateDomainObject::getNameAdmin));
    }
}
