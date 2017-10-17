package com.imcode.imcms.controller.core;

import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.api.TextDocumentViewing;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.parser.ParserParameters;
import imcode.util.Utility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static imcode.server.ImcmsConstants.PERM_EDIT_DOCUMENT;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 18.09.17.
 */
@Controller
@RequestMapping("/viewDoc")
public class ViewDocumentController {

    @RequestMapping("/")
    public ModelAndView goToStartPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
        return processDocView(String.valueOf(ImcmsConstants.DEFAULT_START_DOC_ID), request, response, mav);
    }

    @RequestMapping("/{docIdentifier}")
    public ModelAndView getDocument(@PathVariable("docIdentifier") String docIdentifier, HttpServletRequest request,
                                    HttpServletResponse response, ModelAndView mav) {

        return processDocView(docIdentifier, request, response, mav);
    }

    private ModelAndView processDocView(String docIdentifier, HttpServletRequest request, HttpServletResponse response,
                                        ModelAndView mav) {

        final TextDocument textDocument = Utility.getCMS(request)
                .getDocumentService()
                .getTextDocument(docIdentifier);

        // save doc data
        // this should be done to use tags functionality on page

        final ParserParameters parserParameters = Optional.ofNullable(ParserParameters.fromRequest(request))
                .orElse(new ParserParameters(textDocument.getInternal(), request, response));

        final TextDocumentViewing viewing = Optional.ofNullable(TextDocumentViewing.fromRequest(request))
                .orElse(new TextDocumentViewing(parserParameters));

        TextDocumentViewing.putInRequest(viewing);
        ParserParameters.putInRequest(parserParameters);

        final boolean isEditMode = viewing.isEditing();

        if (isEditMode) {
            parserParameters.setFlags(PERM_EDIT_DOCUMENT);
        }

        final String viewName = textDocument.getTemplate().getName();
        mav.setViewName(viewName);

        mav.addObject("currentDocument", textDocument);
        mav.addObject("isAdmin", Imcms.getUser().isAdmin());
        mav.addObject("isEditMode", isEditMode);
        mav.addObject("contextPath", request.getContextPath());

        return mav;
    }
}
