package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.CreateDocumentPageFlow;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.NoPermissionToCreateDocumentException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TreeSortKeyDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MenuEditPage extends OkCancelPage {

    public static final String SORT_KEY = "sortKey";
    public static final String SELECTED = "selected";
    public static final String DOCUMENT_TYPE_ID = "documentTypeId";
    public static final String CREATE = "create";

    private TextDocumentDomainObject textDocument;
    private final MenuDomainObject menu;
    private ServletContext servletContext;
    public static final String COPY = "copy";
    public static final String ARCHIVE = "archive";
    public static final String REMOVE = "remove";
    private int menuIndex;
    public static final String SORT_ORDER = "sortOrder";
    public static final String SORT = "sort";

    public MenuEditPage(DispatchCommand okDispatchCommand, DispatchCommand cancelDispatchCommand,
                        TextDocumentDomainObject textDocument, int menuIndex, ServletContext servletContext) {
        super(okDispatchCommand, cancelDispatchCommand);
        this.textDocument = textDocument;
        this.menuIndex = menuIndex;
        menu = textDocument.getMenu(menuIndex);
        this.servletContext = servletContext;
    }

    protected void dispatchOther(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
        if ( null != request.getParameter(CREATE) ) {
            int documentTypeId = Integer.parseInt(request.getParameter(DOCUMENT_TYPE_ID));
            if ( 0 == documentTypeId ) {
                addExistingDocPage( textDocument.getId(), menuIndex, request, response );
            } else {
                DocumentCreator documentCreator = new DocumentCreator(new SaveNewDocumentAndAddToMenuCommand(textDocument, menuIndex), new DispatchCommand() {
                        public void dispatch(HttpServletRequest request,
                                             HttpServletResponse response) throws IOException, ServletException {
                            forward(request, response);
                        }
                    }, servletContext);
                    try {
                        documentCreator.createDocumentAndDispatchToCreatePageFlow(documentTypeId, textDocument, request, response);
                        return;
                    } catch ( NoPermissionToCreateDocumentException e ) {
                        throw new UnhandledException(e);
                    }
            }
        }

        forward(request, response);
    }

    protected void updateFromRequest(HttpServletRequest request) {
        MenuItemDomainObject[] menuItems = menu.getMenuItems();
        for ( MenuItemDomainObject menuItem : menuItems ) {
            String newSortKey = request.getParameter(SORT_KEY + menuItem.getDocument().getId());
            if (null != newSortKey) {
                if ( MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED == menu.getSortOrder() ) {
                    try {
                        menuItem.setSortKey(new Integer(newSortKey));
                    } catch ( NumberFormatException ignored ) {
                    }
                } else if ( MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER == menu.getSortOrder() ) {
                    menuItem.setTreeSortKey(new TreeSortKeyDomainObject(newSortKey));
                }
            }
        }

        try {
            String[] selectedChildrenIds = request.getParameterValues(SELECTED);
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            UserDomainObject user = Utility.getLoggedOnUser(request);
            if ( request.getParameter(SORT) != null ) {
                int sortOrder = Integer.parseInt(request.getParameter(SORT_ORDER));
                if ( menu.getSortOrder() != sortOrder ) {
                    menu.setSortOrder(sortOrder);
                }
            } else if ( request.getParameter(REMOVE) != null ) {
                if ( selectedChildrenIds != null ) {
                    for ( String selectedChildrenId : selectedChildrenIds ) {
                        int childId = Integer.parseInt(selectedChildrenId);
                        menu.removeMenuItemByDocumentId(childId);
                    }
                }
            } else if ( request.getParameter(ARCHIVE) != null ) {
                if ( selectedChildrenIds != null ) {
                    Date now = new Date();
                    for ( String selectedChildrenId : selectedChildrenIds ) {
                        int childId = Integer.parseInt(selectedChildrenId);
                        DocumentDomainObject child = documentMapper.getDocument(childId);
                        child.setArchivedDatetime(now);
                        documentMapper.saveDocument(child, user);
                    }
                }
            } else if ( request.getParameter(COPY) != null ) {
                if ( selectedChildrenIds != null ) {

                    for ( String selectedChildIdStr : selectedChildrenIds ) {
                        int selectedChildId = Integer.parseInt(selectedChildIdStr);
                        DocumentDomainObject selectedChild = documentMapper.getDocument(selectedChildId);
                        documentMapper.copyDocument(selectedChild, user);
                        menu.addMenuItem(new MenuItemDomainObject(documentMapper.getDocumentReference(selectedChild)));
                    }
                }
            }
            if ( null == request.getParameter(CREATE) ) {
                documentMapper.saveDocument(textDocument, user);
            }
        } catch ( NoPermissionToEditDocumentException e ) {
            throw new ShouldHaveCheckedPermissionsEarlierException(e);
        } catch ( NoPermissionToAddDocumentToMenuException e ) {
            throw new ConcurrentDocumentModificationException(e);
        } catch (DocumentSaveException e) {
            throw new ShouldNotBeThrownException(e);
        }

    }

    public MenuDomainObject getMenu() {
        return menu;
    }

    public TextDocumentDomainObject getTextDocument() {
        return textDocument;
    }

    public String getPath(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser(request);
        return "/imcms/" + user.getLanguageIso639_2() + "/jsp/change_menu.jsp";
    }
    private void addExistingDocPage( int meta_id, int doc_menu_no,
                                     HttpServletRequest request, HttpServletResponse response ) throws IOException {
        Utility.setDefaultHtmlContentType(response);

        List vec = new ArrayList();
        vec.add( "#meta_id#" );
        vec.add( "" + meta_id );
        vec.add( "#doc_menu_no#" );
        vec.add( "" + doc_menu_no );
        vec.add( "#page#" );
        vec.add( getSessionAttributeName() ) ;

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

    public void save(UserDomainObject user) throws DocumentSaveException {
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        documentMapper.saveDocument(textDocument, user);
    }

    public static class SaveNewDocumentAndAddToMenuCommand implements CreateDocumentPageFlow.SaveDocumentCommand {

        private TextDocumentDomainObject parentDocument;
        private Integer parentMenuIndex;
        private DocumentDomainObject savedDocument;

        SaveNewDocumentAndAddToMenuCommand( TextDocumentDomainObject parentDocument, Integer parentMenuIndex ) {
            this.parentDocument = parentDocument;
            this.parentMenuIndex = parentMenuIndex;
        }

        public synchronized void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
            if ( null != savedDocument ) {
                return;
            }
            final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            documentMapper.saveNewDocument( document, user, false);
            savedDocument = document ;
            if ( null == parentMenuIndex ) {
                return;
            }
            MenuDomainObject menu = parentDocument.getMenu(parentMenuIndex.intValue());
            menu.addMenuItem(new MenuItemDomainObject(documentMapper.getDocumentReference(document)));
            documentMapper.saveDocument(parentDocument, user);
        }

        public synchronized DocumentDomainObject getSavedDocument() {
            return savedDocument;
        }
    }
}
