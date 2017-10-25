package com.imcode.imcms.api;

import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateMapper;

public class TemplateService {

    private final TemplateMapper templateMapper;

    TemplateService(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    public Template getTemplate(String templateName) {
        TemplateDomainObject template = templateMapper.getTemplateByName(templateName);
        return null != template ? new Template(template) : null;
    }
}
