package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Html;
import imcode.util.Utility;

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

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        int parentMenuIndex = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__MENU_INDEX ) );
        int parentId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__PARENT_DOCUMENT_ID ) );
        int documentTypeId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__DOCUMENT_TYPE_ID ) );

        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();

        DocumentDomainObject parentDocument = documentMapper.getDocument( parentId ) ;
        UserDomainObject user = Utility.getLoggedOnUser( request );
        DocumentDomainObject document = documentMapper.createDocumentOfTypeFromParent( documentTypeId, parentDocument, user) ;

        if ( 0 == documentTypeId ) {
            createExistingDocPage( parentId, parentMenuIndex, request, response );
        } else {
            HttpPageFlow httpPageFlow = null ;
            DispatchCommand dispatchToMenuEditCommand = new DispatchToMenuEditCommand( (TextDocumentDomainObject)parentDocument, parentMenuIndex );
            DocumentPageFlow.SaveDocumentCommand saveNewDocumentAndAddToMenuCommand = new SaveNewDocumentAndAddToMenuCommand((TextDocumentDomainObject)parentDocument, parentMenuIndex );
            if (document instanceof TextDocumentDomainObject) {
                httpPageFlow = new CreateTextDocumentPageFlow( (TextDocumentDomainObject)document, saveNewDocumentAndAddToMenuCommand, dispatchToMenuEditCommand ) ;
            } else if (document instanceof UrlDocumentDomainObject) {
                httpPageFlow = new CreateDocumentWithEditPageFlow( new EditUrlDocumentPageFlow( (UrlDocumentDomainObject)document, dispatchToMenuEditCommand, saveNewDocumentAndAddToMenuCommand ));
            } else if (document instanceof HtmlDocumentDomainObject) {
                httpPageFlow = new CreateDocumentWithEditPageFlow( new EditHtmlDocumentPageFlow( (HtmlDocumentDomainObject)document, dispatchToMenuEditCommand, saveNewDocumentAndAddToMenuCommand ));
            } else if (document instanceof FileDocumentDomainObject) {
                httpPageFlow = new CreateDocumentWithEditPageFlow( new EditFileDocumentPageFlow( (FileDocumentDomainObject)document, getServletContext(), dispatchToMenuEditCommand, saveNewDocumentAndAddToMenuCommand, null ));
            } else if (document instanceof BrowserDocumentDomainObject) {
                httpPageFlow = new CreateDocumentWithEditPageFlow( new EditBrowserDocumentPageFlow( (BrowserDocumentDomainObject)document, dispatchToMenuEditCommand, saveNewDocumentAndAddToMenuCommand ));
            } else if (document instanceof FormerExternalDocumentDomainObject) {
                httpPageFlow = new CreateFormerExternalDocumentPageFlow( (FormerExternalDocumentDomainObject)document, saveNewDocumentAndAddToMenuCommand, dispatchToMenuEditCommand ) ;
            }
            httpPageFlow.dispatch( request, response );
        }
    }

    private void createExistingDocPage( int meta_id, int doc_menu_no,
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

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        UserDomainObject user = Utility.getLoggedOnUser( request );
        // Lets fix the sortby list, first get the displaytexts from the database
        String[] sortOrder = imcref.sqlProcedure( "SortOrder_GetExistingDocs", new String[]{
            user.getLanguageIso639_2()
        } );
        String sortOrderStr = Html.createOptionList( "", Arrays.asList( sortOrder ) );
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
                                   + IMCConstants.DISPATCH_FLAG__EDIT_MENU + "&editmenu=" + parentMenuIndex );
        }
    }

    private static class SaveNewDocumentAndAddToMenuCommand implements CreateDocumentPageFlow.SaveDocumentCommand {

        private TextDocumentDomainObject parentDocument;
        private int parentMenuIndex;

        SaveNewDocumentAndAddToMenuCommand( TextDocumentDomainObject parentDocument, int parentMenuIndex ) {
            this.parentDocument = parentDocument;
            this.parentMenuIndex = parentMenuIndex;
        }

        public void saveDocument( DocumentDomainObject document, UserDomainObject user ) {
            final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            documentMapper.saveNewDocument( document, user );
            documentMapper.addToMenu( parentDocument, parentMenuIndex, document, user );
        }
    }
}
