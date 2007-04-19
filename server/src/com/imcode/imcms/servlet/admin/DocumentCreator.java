package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import com.imcode.imcms.mapping.DocumentMapper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.ImcmsServices;
import imcode.server.Imcms;
import imcode.util.Utility;

import java.io.IOException;

public class DocumentCreator {

    ServletContext servletContext ;
    private DocumentPageFlow.SaveDocumentCommand saveDocumentCommand;
    private DispatchCommand returnCommand;
    TemplateDomainObject template ;

    public DocumentCreator( DocumentPageFlow.SaveDocumentCommand saveDocumentCommand,
                            DispatchCommand returnCommand, ServletContext servletContext ) {
        this.servletContext = servletContext ;
        this.saveDocumentCommand = saveDocumentCommand;
        this.returnCommand = returnCommand;
    }

    public void createDocumentAndDispatchToCreatePageFlow( int documentTypeId,
                                                           DocumentDomainObject parentDocument,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response ) throws IOException, ServletException, NoPermissionToCreateDocumentException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        if (!user.canCreateDocumentOfTypeIdFromParent(documentTypeId, parentDocument)) {
            throw new NoPermissionToCreateDocumentException("User can't create documents from document " + parentDocument.getId());
        }
        ImcmsServices services = Imcms.getServices();
        DocumentMapper documentMapper = services.getDocumentMapper();
        DocumentDomainObject document = documentMapper.createDocumentOfTypeFromParent( documentTypeId, parentDocument, user );
        PageFlow pageFlow;
        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document;
            if ( null != template ) {
                textDocument.setTemplateName( template.getName() );
            }
            pageFlow = new CreateTextDocumentPageFlow( textDocument, saveDocumentCommand, returnCommand );
        } else if ( document instanceof UrlDocumentDomainObject ) {
            pageFlow = new CreateDocumentWithEditPageFlow( new EditUrlDocumentPageFlow( (UrlDocumentDomainObject)document, returnCommand, saveDocumentCommand ) );
        } else if ( document instanceof HtmlDocumentDomainObject ) {
            pageFlow = new CreateDocumentWithEditPageFlow( new EditHtmlDocumentPageFlow( (HtmlDocumentDomainObject)document, returnCommand, saveDocumentCommand ) );
        } else if ( document instanceof FileDocumentDomainObject ) {
            pageFlow = new CreateDocumentWithEditPageFlow( new EditFileDocumentPageFlow( (FileDocumentDomainObject)document, servletContext, returnCommand, saveDocumentCommand, null ) );
        } else if ( document instanceof BrowserDocumentDomainObject ) {
            pageFlow = new CreateDocumentWithEditPageFlow( new EditBrowserDocumentPageFlow( (BrowserDocumentDomainObject)document, returnCommand, saveDocumentCommand ) );
        } else {
            return ;
        }
        pageFlow.dispatch( request, response );
    }

    public void setTemplate( TemplateDomainObject template ) {
        this.template = template;
    }
}
