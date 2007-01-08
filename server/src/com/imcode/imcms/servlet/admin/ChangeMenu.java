package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.ShouldNotBeThrownException;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChangeMenu extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        int menuIndex = Integer.parseInt(request.getParameter( "menuIndex" )) ;
        final int documentId = Integer.parseInt(request.getParameter("documentId")) ;

        final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        final TextDocumentDomainObject document = (TextDocumentDomainObject) documentMapper.getDocument(documentId);

        final UserDomainObject user = Utility.getLoggedOnUser(request);
        TextDocumentPermissionSetDomainObject permissionSetFor = (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(document);
        if ( !permissionSetFor.getEditMenus() ) {
            AdminDoc.adminDoc( documentId, user, request, response, getServletContext() );
            return ;
        }

        final DispatchCommand cancelCommand = new RedirectToMenuEditDispatchCommand(document, menuIndex);
        DispatchCommand saveCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                try {
                    documentMapper.saveDocument(document, user);
                    cancelCommand.dispatch(request, response);
                } catch ( NoPermissionToAddDocumentToMenuException e ) {
                    throw new UnhandledException(e);
                } catch ( NoPermissionToEditDocumentException e ) {
                    throw new UnhandledException(e);
                } catch (DocumentSaveException e) {
                    throw new ShouldNotBeThrownException(e);
                }
            }
        };
        MenuEditPage menuEditPage = new MenuEditPage(saveCommand, cancelCommand, document, menuIndex, getServletContext());
        menuEditPage.forward(request, response);
    }

    private static class RedirectToMenuEditDispatchCommand implements DispatchCommand {

        private TextDocumentDomainObject parentDocument;
        private int parentMenuIndex;

        RedirectToMenuEditDispatchCommand( TextDocumentDomainObject parentDocument, int parentMenuIndex ) {
            this.parentDocument = parentDocument;
            this.parentMenuIndex = parentMenuIndex;
        }

        public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            response.sendRedirect( "AdminDoc?meta_id=" + parentDocument.getId() + "&flags="
                                   + ImcmsConstants.DISPATCH_FLAG__EDIT_MENU + "&editmenu=" + parentMenuIndex );
        }
    }

}
