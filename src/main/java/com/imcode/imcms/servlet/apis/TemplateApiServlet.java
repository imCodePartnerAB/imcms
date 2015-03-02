package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.util.JSONUtils;
import imcode.server.Imcms;
import imcode.server.document.TemplateDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shadowgun on 26.02.2015.
 */
public class TemplateApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> result = new HashMap<>();

        List<TemplateDomainObject> templates = Imcms.getServices().getTemplateMapper().getAllTemplates();
        for (TemplateDomainObject template : templates)
            result.put(template.getName(), template.getName());
        JSONUtils.defaultJSONAnswer(resp, result);
    }
}
