package com.imcode.imcms.web.admin;

import imcode.server.ImcmsConstants;
import imcode.server.document.textdocument.TextDomainObject;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Factory for construction build-in editors URLs.
 */
public class BuildInEditorSupport {

    /** Creates URL for text editing. */
    public static String createTextEditorURL(HttpServletRequest request, String returnURL, int docId, int textNo) {
        String contextPath = request.getContextPath();
        String fullReturnURL = contextPath + "/" + returnURL;
        String editorBaseURL = contextPath + "/servlet/ChangeText";

        //try {
            return //URLEncoder.encode(
                    String.format("%s?%s=%s&meta_id=%d&txt=%d",
                        editorBaseURL,
                        ImcmsConstants.REQUEST_PARAM__RETURN_URL,
                        fullReturnURL,
                        docId,
                        textNo);//,
                  //  "utf-8");
        //} catch (UnsupportedEncodingException e) {
        //    throw new RuntimeException(e);
        //}
    }

//    /** Creates URL for text editing. */
//    public static String createTextEditorURL(TextDomainObject text, String returnURL) {
//        return ...
//    }
}
