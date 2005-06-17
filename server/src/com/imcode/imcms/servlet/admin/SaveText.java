package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class SaveText extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException {
        req.setCharacterEncoding( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
        Utility.setDefaultHtmlContentType( res );

        // Check if user has permission to be here
        ImcmsServices imcref = Imcms.getServices();
        int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        UserDomainObject user = Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( meta_id );

        TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );

        if ( permissionSet.getEditTexts()
             && req.getParameter( "cancel" ) == null ) {
            // get text_no
            int txt_no = Integer.parseInt( req.getParameter( "txt_no" ) );

            // get textdocument
            String text_string = req.getParameter( "text" );

            int text_format = Integer.parseInt( req.getParameter( "format_type" ) );

            TextDomainObject text = new TextDomainObject( text_string, text_format );

            saveText( documentMapper, text, document, txt_no, imcref, meta_id, user );

            if (null != req.getParameter( "save" )) {
                res.sendRedirect( "ChangeText?meta_id="+meta_id+"&txt="+txt_no );
                return ;
            }
        }

        res.sendRedirect( "AdminDoc?meta_id=" + meta_id + "&flags="
                          + imcode.server.ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS );

    }

    private void saveText(DocumentMapper documentMapper, TextDomainObject text, TextDocumentDomainObject document,
                          int txt_no, ImcmsServices imcref, int meta_id,
                          UserDomainObject user) {
        document.setText( txt_no, text );

        documentMapper.saveDocument( document, user );

        imcref.updateMainLog( "Text " + txt_no + " in [" + meta_id + "] modified by user: [" + user.getFullName()
                              + "]" );
    }
}
