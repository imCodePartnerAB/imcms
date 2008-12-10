package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;
import imcode.util.ShouldNotBeThrownException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaveInclude extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        Utility.setDefaultHtmlContentType( res );

        Writer out = res.getWriter();

        String meta_id_str = req.getParameter( "meta_id" );
        int meta_id = Integer.parseInt( meta_id_str );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( meta_id );

        TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );
        if ( !permissionSet.getEditIncludes() ) {	// Checking to see if user may edit this
            sendPermissionDenied( imcref, out, meta_id, user );
            return;
        }

        try {
            String included_meta_id = req.getParameter("include_meta_id");
            String include_id = req.getParameter("include_id");

            if ( included_meta_id != null && include_id != null ) {
                included_meta_id = included_meta_id.trim();
                include_id = include_id.trim();
                DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS " );
                if ( "".equals(included_meta_id) ) {
                    document.removeInclude(Integer.parseInt(include_id));
                    documentMapper.saveDocument(document, user);
                    imcref.updateMainLog(dateFormat.format(new Date()) + "Include nr [" + include_id + "] on ["
                                         + meta_id_str
                                         + "] removed by user: ["
                                         + user.getFullName()
                                         + "]");
                } else {
                    try {
                        int included_meta_id_int = Integer.parseInt(included_meta_id);
                        DocumentDomainObject includedDocument = documentMapper.getDocument(included_meta_id_int);

                        if ( null == includedDocument || !DocumentTypeDomainObject.TEXT.equals(includedDocument.getDocumentType()) ) {
                            sendBadId(imcref, out, meta_id, user);
                            return;
                        }

                        // Make sure the user has permission to share the included document
                        if ( user.canAddDocumentToAnyMenu(includedDocument) ) {
                            document.setInclude(Integer.parseInt(include_id), includedDocument.getId());
                            documentMapper.saveDocument(document, user);
                            imcref.updateMainLog(dateFormat.format(new Date()) + "Include nr [" + include_id
                                                 + "] on ["
                                                 + meta_id_str
                                                 + "] changed to ["
                                                 + included_meta_id
                                                 + "]  by user: ["
                                                 + user.getFullName()
                                                 + "]");
                        } else {
                            sendPermissionDenied(imcref, out, meta_id, user);
                            return;
                        }
                    } catch ( NumberFormatException ignored ) {
                        sendBadId(imcref, out, meta_id, user);
                        return;
                    }
                }
            }

            AdminDoc.adminDoc(meta_id, user, req, res, getServletContext());
        } catch ( NoPermissionToEditDocumentException e ) {
            throw new ShouldHaveCheckedPermissionsEarlierException(e);
        } catch ( NoPermissionToAddDocumentToMenuException e ) {
            throw new ConcurrentDocumentModificationException(e);
        } catch (DocumentSaveException e) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    private void sendPermissionDenied( ImcmsServices imcref, Writer out, int meta_id, UserDomainObject user ) throws IOException {
        List vec = new ArrayList( 2 );
        vec.add( "#meta_id#" );
        vec.add( String.valueOf( meta_id ) );
        String htmlStr = imcref.getAdminTemplate( "include_permission_denied.html", user, vec );
        out.write( htmlStr );
    }

    private void sendBadId( ImcmsServices imcref, Writer out, int meta_id, UserDomainObject user ) throws IOException {
        List vec = new ArrayList( 2 );
        vec.add( "#meta_id#" );
        vec.add( String.valueOf( meta_id ) );
        String htmlStr = imcref.getAdminTemplate( "include_bad_id.html", user, vec );
        out.write( htmlStr );
    }

}
