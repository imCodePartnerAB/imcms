package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Save document sorting (date,name,manual)
 */
public class SaveSort extends HttpServlet {

    /**
     * service()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        Utility.setDefaultHtmlContentType( res );
        Writer out = res.getWriter();

        UserDomainObject user = Utility.getLoggedOnUser( req );

        ImcmsServices imcref = Imcms.getServices();
        int documentId = Integer.parseInt( req.getParameter( "meta_id" ) );
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( documentId );

        TextDocumentPermissionSetDomainObject documentPermissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );
        if ( !documentPermissionSet.getEditMenus() ) {
            String output = AdminDoc.adminDoc( documentId, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        String[] selectedChildrenIds = req.getParameterValues( "archiveDelBox" );
        int menuIndex = Integer.parseInt( req.getParameter( "doc_menu_no" ) );
        MenuDomainObject menu = document.getMenu( menuIndex );

        List logMessages = new ArrayList() ;
        if ( req.getParameter( "sort" ) != null ) {
            int sortOrder = Integer.parseInt( req.getParameter( "sort_order" ) );
            if ( menu.getSortOrder() != sortOrder ) {
                menu.setSortOrder( sortOrder );
            } else {
                MenuItemDomainObject[] menuItems = menu.getMenuItems();
                for ( int i = 0; i < menuItems.length; i++ ) {
                    MenuItemDomainObject menuItem = menuItems[i];
                    String newSortKey = req.getParameter( "" + menuItem.getDocument().getId() );
                    if ( MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED == sortOrder ) {
                        try {
                            menuItem.setSortKey( new Integer( newSortKey ) );
                        } catch ( NumberFormatException ignored ) { }
                    } else if ( MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER == sortOrder ) {
                        menuItem.setTreeSortKey( new TreeSortKeyDomainObject( newSortKey ) );
                    }
                }
            }
            logMessages.add("Child sort order for [" + documentId + "] updated by user: [" + user.getFullName() + "]");
        } else if ( req.getParameter( "delete" ) != null ) {
            if ( selectedChildrenIds != null ) {
                for ( int i = 0; i < selectedChildrenIds.length; i++ ) {
                    int childId = Integer.parseInt(selectedChildrenIds[i]);
                    menu.removeMenuItemByDocumentId(childId) ;
                    imcref.updateMainLog( "Link from [" + document.getId() + "] in menu [" + menuIndex + "] to ["
                                          + childId
                                          + "] removed by user: ["
                                          + user.getFullName()
                                          + "]" );
                }
            }
        } else if ( req.getParameter( "archive" ) != null ) {
            if ( selectedChildrenIds != null ) {
                Date now = new Date();
                for ( int i = 0; i < selectedChildrenIds.length; i++ ) {
                    int childId = Integer.parseInt( selectedChildrenIds[i] );
                    DocumentDomainObject child = documentMapper.getDocument( childId );
                    child.setArchivedDatetime( now );
                    documentMapper.saveDocument( child, user );
                }
                logMessages.add("Childs [" + StringUtils.join( selectedChildrenIds, ", " ) + "] from " + "[" + documentId
                             + "] archived by user: ["
                             + user.getFullName()
                             + "]");
            }
        } else if ( req.getParameter( "copy" ) != null ) {
            if ( selectedChildrenIds != null ) {

                for ( int i = 0; i < selectedChildrenIds.length; i++ ) {
                    String selectedChildIdStr = selectedChildrenIds[i];
                    int selectedChildId = Integer.parseInt( selectedChildIdStr );
                    DocumentDomainObject selectedChild = documentMapper.getDocument( selectedChildId );
                    documentMapper.copyDocument( selectedChild, user );
                    menu.addMenuItem( new MenuItemDomainObject( documentMapper.getDocumentReference( selectedChild ) ) );
                }
            }
        }

        documentMapper.saveDocument( document, user );

        for ( Iterator iterator = logMessages.iterator(); iterator.hasNext(); ) {
            String logMessage = (String)iterator.next();
            imcref.updateMainLog( logMessage );
        }
        res.sendRedirect( "AdminDoc?meta_id=" + documentId + "&flags=" + ImcmsConstants.DISPATCH_FLAG__EDIT_MENU
                          + "&editmenu="
                          + menuIndex );
    }

}
