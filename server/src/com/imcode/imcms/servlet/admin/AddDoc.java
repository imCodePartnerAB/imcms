package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddDoc extends HttpServlet {

    public static final String REQUEST_PARAMETER__DOCUMENT_TYPE_ID = "edit_menu";
    public static final String REQUEST_PARAMETER__MENU_INDEX = "doc_menu_no";
    public static final String REQUEST_PARAMETER__PARENT_DOCUMENT_ID = "parent_meta_id";
    public static final String REQUEST_PARAMETER__NEW_TEMPLATE = "defaulttemplate" ;

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        Integer parentMenuIndex = getParentMenuIndexFromRequest(request);
        int parentId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__PARENT_DOCUMENT_ID ) );
        int documentTypeId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__DOCUMENT_TYPE_ID ) );

        if ( 0 == documentTypeId ) {
            addExistingDocPage( parentId, parentMenuIndex.intValue(), request, response );
        } else {

            ImcmsServices services = Imcms.getServices();
            DocumentMapper documentMapper = services.getDocumentMapper();

            DocumentDomainObject parentDocument = documentMapper.getDocument( parentId );
            SaveNewDocumentAndAddToMenuCommand saveNewDocumentAndAddToMenuCommand = new SaveNewDocumentAndAddToMenuCommand( (TextDocumentDomainObject)parentDocument, parentMenuIndex );
            DispatchCommand dispatchCommand = createDispatchCommand(parentMenuIndex, parentDocument, saveNewDocumentAndAddToMenuCommand);

            String templateName = request.getParameter( REQUEST_PARAMETER__NEW_TEMPLATE );
            TemplateDomainObject template = null ;
            if ( null != templateName ) {
                template = services.getTemplateMapper().getTemplateByName( templateName );
            }

            DocumentCreator documentCreator = new DocumentCreator(saveNewDocumentAndAddToMenuCommand,dispatchCommand,getServletContext());
            documentCreator.setTemplate(template) ;
            try {
                documentCreator.createDocumentAndDispatchToCreatePageFlow( documentTypeId, parentDocument, request, response );
            } catch ( NoPermissionToCreateDocumentException e ) {
                throw new ShouldHaveCheckedPermissionsEarlierException(e);
            }
        }
    }

    private Integer getParentMenuIndexFromRequest(HttpServletRequest request) {
        String parentMenuIndexString = request.getParameter(REQUEST_PARAMETER__MENU_INDEX);
        Integer parentMenuIndex = null != parentMenuIndexString ? Integer.valueOf(parentMenuIndexString) : null;
        return parentMenuIndex;
    }

    private DispatchCommand createDispatchCommand(Integer parentMenuIndex, DocumentDomainObject parentDocument,
                                                  final SaveNewDocumentAndAddToMenuCommand saveNewDocumentAndAddToMenuCommand) {
        if (null != parentMenuIndex) {
            return new RedirectToMenuEditDispatchCommand( (TextDocumentDomainObject)parentDocument, parentMenuIndex.intValue() );
        }

        return new RedirectToNewSavedDocumentDispatchCommand(saveNewDocumentAndAddToMenuCommand);
    }

    private void addExistingDocPage( int meta_id, int doc_menu_no,
                                        HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.setContentType( "text/html" );

        List vec = new ArrayList();
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

        final UserDomainObject user = Utility.getLoggedOnUser( request );
        String sortOrderStr = Html.createOptionList( GetExistingDoc.SORT_ORDERS_MAP.entrySet(), new ToStringPairTransformer() {
            public String[] transformToStringPair(Object input) {
                Map.Entry entry = (Map.Entry)input ;
                return new String[] {(String)entry.getKey(), ((LocalizedMessage)entry.getValue()).toLocalizedString( user )} ;
            }
        });
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

    private static class SaveNewDocumentAndAddToMenuCommand implements CreateDocumentPageFlow.SaveDocumentCommand {

        private TextDocumentDomainObject parentDocument;
        private Integer parentMenuIndex;
        private DocumentDomainObject savedDocument;

        SaveNewDocumentAndAddToMenuCommand( TextDocumentDomainObject parentDocument, Integer parentMenuIndex ) {
            this.parentDocument = parentDocument;
            this.parentMenuIndex = parentMenuIndex;
        }

        public synchronized void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException
        {
            if ( null == savedDocument ) {
                final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
                documentMapper.saveNewDocument( document, user );
                this.savedDocument = document ;
                if (null != parentMenuIndex) {
                    MenuDomainObject menu = parentDocument.getMenu(parentMenuIndex.intValue());
                    menu.addMenuItem(new MenuItemDomainObject(documentMapper.getDocumentReference(document)));
                    documentMapper.saveDocument(parentDocument, user);
                }
            }
        }

        public synchronized DocumentDomainObject getSavedDocument() {
            return savedDocument;
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
                                                               HttpServletResponse response ) throws IOException, ServletException, NoPermissionToCreateDocumentException {
            UserDomainObject user = Utility.getLoggedOnUser( request );
            ImcmsServices services = Imcms.getServices();
            DocumentMapper documentMapper = services.getDocumentMapper();
            DocumentDomainObject document = documentMapper.createDocumentOfTypeFromParent( documentTypeId, parentDocument, user );
            PageFlow pageFlow = null;
            if ( document instanceof TextDocumentDomainObject ) {
                TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document;
                if ( null != template ) {
                    textDocument.setTemplateId( template.getId() );
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

    private static class RedirectToNewSavedDocumentDispatchCommand implements DispatchCommand {

        private final SaveNewDocumentAndAddToMenuCommand saveNewDocumentAndAddToMenuCommand;

        RedirectToNewSavedDocumentDispatchCommand(SaveNewDocumentAndAddToMenuCommand saveNewDocumentAndAddToMenuCommand) {
            this.saveNewDocumentAndAddToMenuCommand = saveNewDocumentAndAddToMenuCommand;
        }

        public void dispatch(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
            DocumentDomainObject savedDocument = saveNewDocumentAndAddToMenuCommand.getSavedDocument();
            response.sendRedirect(Utility.getAbsolutePathToDocument(request,savedDocument)) ;
        }
    }
}
