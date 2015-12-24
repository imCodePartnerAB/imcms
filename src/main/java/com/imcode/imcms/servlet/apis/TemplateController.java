package com.imcode.imcms.servlet.apis;

import imcode.server.Imcms;
import imcode.server.document.TemplateDomainObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * Created by Shadowgun on 26.02.2015.
 */
@RestController
@RequestMapping("/template")
public class TemplateController {
    @RequestMapping
    protected Object getTemplatesList() {
        return Imcms.getServices().getTemplateMapper().getAllTemplates()
                .stream()
                .collect(Collectors.toMap(TemplateDomainObject::getNameAdmin , TemplateDomainObject::getNameAdmin));
    }
}
