package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.Imcms;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * Created by Shadowgun on 24.02.2015.
 */

@RestController
@RequestMapping("/language")
public class LanguageController {

    @RequestMapping
    public Object getLanguagesList() {
        return Imcms.getServices().getDocumentLanguages().getAll().stream().collect(Collectors.toMap(DocumentLanguage::getName, DocumentLanguage::getCode));
    }

}
