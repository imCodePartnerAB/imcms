package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Html;
import imcode.util.Utility;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

public class AddDoc extends HttpServlet {

    public static final String REQUEST_PARAMETER__DOCUMENT_TYPE_ID = "edit_menu";
    public static final String REQUEST_PARAMETER__MENU_INDEX = "doc_menu_no";
    public static final String REQUEST_PARAMETER__PARENT_DOCUMENT_ID = "parent_meta_id";
    public static final String REQUEST_PARAMETER__NEW_TEMPLATE = "defaulttemplate" ;

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        int parentMenuIndex = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__MENU_INDEX ) );
        int parentId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__PARENT_DOCUMENT_ID ) );
        int documentTypeId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__DOCUMENT_TYPE_ID ) );

        if ( 0 == documentTypeId ) {
            addExistingDocPage( parentId, parentMenuIndex, request, response );
        } else {

            ImcmsServices services = Imcms.getServices();
            DocumentMapper documentMapper = services.getDocumentMapper();

            DocumentDomainObject parentDocument = documentMapper.getDocument( parentId );
            DocumentPageFlow.SaveDocumentCommand saveNewDocumentAndAddToMenuCommand = new SaveNewDocumentAndAddToMenuCommand( (TextDocumentDomainObject)parentDocument, parentMenuIndex );
            DispatchCommand dispatchToMenuEditCommand = new DispatchToMenuEditCommand( (TextDocumentDomainObject)parentDocument, parentMenuIndex );

            String templateName = request.getParameter( REQUEST_PARAMETER__NEW_TEMPLATE );
            TemplateDomainObject template = null ;
            if ( null != templateName ) {
                template = services.getTemplateMapper().getTemplateByName( templateName );
            }

            DocumentCreator documentCreator = new DocumentCreator(saveNewDocumentAndAddToMenuCommand,dispatchToMenuEditCommand,getServletContext());
            documentCreator.setTemplate(template) ;
            documentCreator.createDocumentAndDispatchToCreatePageFlow( documentTypeId, parentDocument, request, response );
        }
    }

    private void addExistingDocPage( int meta_id, int doc_menu_no,
                                        HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.setContentType( "text/html" );

        Vector vec = new Vector();
        vec.add( "#meta_id#" );
        vec.add( "" + meta_id );
        vec.add( "#doc_menu_no#" );
        vec.add( "" + doc_menu_no );

        // Lets get todays date
        SimpleDateFormat formatter = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
        Date toDay = new Date();
        vec.add( "#start_date#" );
        vec.add( "" );
        vec.add( "#end_date#" );
        vec.add( formatter.format( toDay ) );

        vec.add( "#searchstring#" );
        vec.add( "" );

        vec.add( "#searchResults#" );
        vec.add( "" );

        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( request );
        // Lets fix the sortby list, first get the displaytexts from the database
        String[] sortOrder = imcref.getExceptionUnhandlingDatabase().executeArrayProcedure( "SortOrder_GetExistingDocs", new String[]{
            user.getLanguageIso639_2()
        } );
        String sortOrderStr = Html.createOptionList( Arrays.asList( sortOrder ), "" );
        vec.add( "#sortBy#" );
        vec.add( sortOrderStr );

        // Lets set all the the documenttypes as selected in the html file
        String[][] allDocTypesArray = imcref.getAllDocumentTypes( user.getLanguageIso639_2() );
        for ( int i = 0; i < allDocTypesArray.length; ++i ) {
            vec.add( "#checked_" + allDocTypesArray[i][0] + "#" );
            vec.add( "checked" );
        }

        // Lets set the create/ change types as selected in the html file
        String[] allPossibleIncludeDocsValues = {"created", "changed"};
        for ( int i = 0; i < allPossibleIncludeDocsValues.length; i++ ) {
            vec.add( "#include_check_" + allPossibleIncludeDocsValues[i] + "#" );
            vec.add( "checked" );
        }

        // Lets set the and / or search preposition
        String[] allPossibleSearchPreps = {"and", "or"};
        for ( int i = 0; i < allPossibleSearchPreps.length; i++ ) {
            vec.add( "#search_prep_check_" + allPossibleSearchPreps[i] + "#" );
            if ( i == 0 ) {
                vec.add( "checked" );
            } else {
                vec.add( "" );
            }
        }
        // Lets parse the html page which consists of the add an existing doc
        response.getWriter().write( imcref.getAdminTemplate( "existing_doc.html", user, vec ) );
        return;
    }

    private static class DispatchToMenuEditCommand implements DispatchCommand {

        private TextDocumentDomainObject parentDocument;
        private int parentMenuIndex;

        DispatchToMenuEditCommand( TextDocumentDomainObject parentDocument, int parentMenuIndex ) {
            this.parentDocument = parentDocument;
            this.parentMenuIndex = parentMenuIndex;
        }

        public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            response.sendRedirect( "AdminDoc?meta_id=" + parentDocument.getId() + "&flags="
                                   + ImcmsConstants.DISPATCH_FLAG__EDIT_MENU + "&editmenu=" + parentMenuIndex );
        }
    }

    private static class SaveNewDocumentAndAddToMenuCommand implements CreateDocumentPageFlow.SaveDocumentCommand {

        private TextDocumentDomainObject parentDocument;
        private int parentMenuIndex;
        private boolean saved;

        SaveNewDocumentAndAddToMenuCommand( TextDocumentDomainObject parentDocument, int parentMenuIndex ) {
            this.parentDocument = parentDocument;
            this.parentMenuIndex = parentMenuIndex;
        }

        public synchronized void saveDocument( DocumentDomainObject document, UserDomainObject user ) {
            if ( !saved ) {
                saved = true ;
                final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
                documentMapper.saveNewDocument( document, user );
                documentMapper.addToMenu( parentDocument, parentMenuIndex, document, user );
            }
        }
    }

    public static class DocumentCreator {

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
                                                               HttpServletResponse response ) throws IOException, ServletException {
            UserDomainObject user = Utility.getLoggedOnUser( request );
            ImcmsServices services = Imcms.getServices();
            DocumentMapper documentMapper = services.getDocumentMapper();
            DocumentDomainObject document = documentMapper.createDocumentOfTypeFromParent( documentTypeId, parentDocument, user );
            PageFlow pageFlow = null;
            if ( document instanceof TextDocumentDomainObject ) {
                TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document;
                if ( null != template ) {
                    textDocument.setTemplate( template );
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
            } else if ( document instanceof FormerExternalDocumentDomainObject ) {
                pageFlow = new CreateFormerExternalDocumentPageFlow( (FormerExternalDocumentDomainObject)document, saveDocumentCommand, returnCommand );
            }
            pageFlow.dispatch( request, response );
        }

        public void setTemplate( TemplateDomainObject template ) {
            this.template = template;
        }
    }

}
