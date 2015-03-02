package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.util.JSONUtils;
import imcode.server.Imcms;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shadowgun on 24.02.2015.
 */
public class LanguageApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> result = new HashMap<>();

        List<DocumentLanguage> languages = Imcms.getServices().getDocumentLanguages().getAll();
        for (DocumentLanguage language : languages)
            result.put(language.getName(), language.getCode());
        JSONUtils.defaultJSONAnswer(resp, result);
    }
}
