package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.TextDocumentViewing;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.util.Utility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller provides possibility to go to page with elements editing without going to it's docs.
 * Created by Serhii from Ubrainians for Imcode
 * on 26.08.16.
 */
@Controller
@RequestMapping("/edit")
public class EditElementController {

    /**
     * Method goes to elements edition jsp.
     *
     * @param metaId  - document meta_id
     * @param textNo  - [optional] - desired textNo to edit
     * @param imageNo - [optional] - desired imageNo to edit
     * @param menuNo  - [optional] - desired menuNo to edit
     * @return ModelAndView of element editing jsp with all parameters.
     */
    @RequestMapping
    public ModelAndView editText(@RequestParam Integer metaId,
                                 @RequestParam(required = false) Integer textNo,
                                 @RequestParam(required = false) Integer imageNo,
                                 @RequestParam(required = false) Integer menuNo,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {

        if (!Imcms.getUser().isSuperAdmin()) {
            Utility.forwardToLogin(request, response);
        }

        DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(metaId);

        if (null == document) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        prepareRequest(document, request, response);

        ModelAndView mav = new ModelAndView("editElement"); // jsp for element editing
        mav.addObject("flags", ImcmsConstants.PERM_EDIT_DOCUMENT); // flags to use admin functionality

        // add one or none of parameters
        if (textNo != null) {
            mav.addObject("textNo", textNo);

        } else if (imageNo != null) {
            mav.addObject("imageNo", imageNo);

        } else if (menuNo != null) {
            mav.addObject("menuNo", menuNo);
        }

        return mav;
    }

    private void prepareRequest(DocumentDomainObject document,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        // this should be done to use tags functionality on page
        ParserParameters parserParameters = new ParserParameters(document, request, response);
        TextDocumentViewing.putInRequest(new TextDocumentViewing(parserParameters));
        ParserParameters.putInRequest(parserParameters);

        final int flags = ImcmsConstants.PERM_EDIT_DOCUMENT;
        parserParameters.setFlags(flags);
    }
}
