package com.imcode.imcms.controller.core;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * General controller for document viewing in any mode.
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 18.09.17.
 */
@Controller
@RequestMapping("/viewDoc")
public class ViewDocumentController {

    private final DocumentMapper documentMapper;

    public ViewDocumentController(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    @RequestMapping({"", "/"})
    public ModelAndView goToStartPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
        final TextDocumentDomainObject textDocument = getTextDocument(String.valueOf(ImcmsConstants.DEFAULT_START_DOC_ID), getDefaultLanguageCode(), request);
        return processDocView(textDocument, request, response, mav);
    }

    @RequestMapping("/{docIdentifier}")
    public ModelAndView getDocument(@PathVariable("docIdentifier") String docIdentifier,
                                    @RequestParam(value = "language-code", required = false) String languageCode,
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    ModelAndView mav) {
        final TextDocumentDomainObject textDocument = getTextDocument(docIdentifier, getLanguageCodeOrDefault(languageCode), request);
        return processDocView(textDocument, request, response, mav);
    }

    private ModelAndView processDocView(TextDocumentDomainObject textDocument, HttpServletRequest request, HttpServletResponse response,
                                        ModelAndView mav) {

        final String isEditModeStr = Objects.toString(request.getAttribute("isEditMode"), "false");
        final boolean isEditMode = Boolean.parseBoolean(isEditModeStr);
        final String viewName = textDocument.getTemplateName();

        mav.setViewName(viewName);

        mav.addObject("currentDocument", textDocument);
        mav.addObject("language", textDocument.getLanguage().getCode());
        mav.addObject("isAdmin", Imcms.getUser().isAdmin());
        mav.addObject("isEditMode", isEditMode);
        mav.addObject("contextPath", request.getContextPath());

        return mav;
    }

    private TextDocumentDomainObject getTextDocument(String docId, String languageCode, HttpServletRequest request) {
        return Optional.ofNullable(documentMapper.<TextDocumentDomainObject>getVersionedDocument(docId, languageCode, request))
                .orElseThrow(() -> new DocumentNotExistException(docId));
    }

    private String getDefaultLanguageCode() {
        return Imcms.getUser().getDocGetterCallback().getLanguage().getCode();
    }

    private String getLanguageCodeOrDefault(String languageCode) {
        return Optional.ofNullable(languageCode)
                .orElseGet(this::getDefaultLanguageCode);
    }

}
