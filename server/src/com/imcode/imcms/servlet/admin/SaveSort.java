package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

/**
 * Save document sorting (date,name,manual)
 */
public class SaveSort extends HttpServlet {

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    /**
     * service()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        Utility.setDefaultHtmlContentType( res );
        Writer out = res.getWriter();

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        int documentId = Integer.parseInt( req.getParameter( "meta_id" ) );
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !imcref.checkDocAdminRights( documentId, user, IMCConstants.PERM_EDIT_TEXT_DOCUMENT_MENUS ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( documentId, documentId, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        String temp_str;
        String[] selectedChildrenIds;
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( documentId );
        documentMapper.touchDocument( document );

        String[] children = imcref.sqlQuery( "select to_meta_id from childs, menus where childs.menu_id = menus.menu_id AND meta_id = ?", new String[]{
            "" + documentId
        } );

        Vector childs = new Vector();
        Vector sort_no = new Vector();
        for ( int i = 0; i < children.length; ++i ) {
            temp_str = req.getParameter( children[i] );
            if ( temp_str != null ) {
                childs.add( children[i] );
                sort_no.add( temp_str );
            }
        }

        selectedChildrenIds = req.getParameterValues( "archiveDelBox" );

        user.put( "flags", new Integer( IMCConstants.DISPATCH_FLAG__EDIT_MENU ) );

        int menuIndex = Integer.parseInt( req.getParameter( "doc_menu_no" ) );
        String sortParam = req.getParameter( "sort" );
        if ( sortParam != null ) {
            int sort_order = Integer.parseInt( req.getParameter( "sort_order" ) );
            String[] queryResult = imcref.sqlQuery( "select sort_order from menus where meta_id = ? AND menu_index = ?",
                                                    new String[]{"" + documentId, "" + menuIndex} );
            int currentSortOrder = MenuDomainObject.MENU_SORT_ORDER__DEFAULT;
            if ( 0 < queryResult.length ) {
                String currentSortOrderStr = queryResult[0];
                currentSortOrder = Integer.parseInt( currentSortOrderStr );
            }
            if ( currentSortOrder != sort_order ) {
                imcref.sqlUpdateQuery( "update menus set sort_order = ? where meta_id = ? AND menu_index = ?",
                                       new String[]{"" + sort_order, "" + documentId, "" + menuIndex} );
                logSortOrderUpdateToMainLog( imcref, documentId, user );

            } else {
                if ( childs.size() > 0 ) {
                    if ( MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED == sort_order ) {
                        imcref.saveManualSort( documentId, user, childs, sort_no, menuIndex );
                    } else if ( MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER == sort_order ) {
                        imcref.saveTreeSortIndex( documentId, user, childs, sort_no, menuIndex );
                    }
                }
            }
        } else if ( req.getParameter( "delete" ) != null ) {
            if ( selectedChildrenIds != null ) {
                documentMapper.deleteChilds( document, menuIndex, user, selectedChildrenIds );
                logDeletionToMainLog( selectedChildrenIds, imcref, document, menuIndex, user );
            }
        } else if ( req.getParameter( "archive" ) != null ) {
            if ( selectedChildrenIds != null ) {
                imcref.archiveChilds( documentId, user, selectedChildrenIds );
                logArchivationToMainLog( imcref, selectedChildrenIds, documentId, user );

            }
        } else if ( req.getParameter( "copy" ) != null ) {
            if ( selectedChildrenIds != null ) {
                String copyHeadlineSuffix = imcref.getAdminTemplate( COPY_HEADLINE_SUFFIX_TEMPLATE, user, null );

                for ( int i = 0; i < selectedChildrenIds.length; i++ ) {
                    String selectedChildIdStr = selectedChildrenIds[i];
                    int selectedChildId = Integer.parseInt( selectedChildIdStr );
                    DocumentDomainObject selectedChild = documentMapper.getDocument( selectedChildId );
                    selectedChild.setHeadline( selectedChild.getHeadline() + copyHeadlineSuffix );
                    selectedChild.setStatus( DocumentDomainObject.STATUS_NEW );
                    selectedChild.setPublicationStartDatetime( new Date() );
                    documentMapper.saveNewDocument( selectedChild, user );
                    document.getMenu( menuIndex ).addMenuItem( new MenuItemDomainObject( selectedChild ) );
                }
                documentMapper.saveDocument( document, user );
            }
        }

        res.sendRedirect( "AdminDoc?meta_id=" + documentId + "&flags=" + IMCConstants.DISPATCH_FLAG__EDIT_MENU
                          + "&editmenu="
                          + menuIndex );
    }

    private void logSortOrderUpdateToMainLog( IMCServiceInterface imcref, int documentId, UserDomainObject user ) {
        imcref.updateMainLog( "Child sort order for [" + documentId + "] updated by user: [" +
                    user.getFullName() + "]" );
    }

    private void logArchivationToMainLog( IMCServiceInterface imcref, String[] selectedChildrenIds, int documentId, UserDomainObject user ) {
        imcref.updateMainLog( "Childs [" + StringUtils.join( selectedChildrenIds, ", " ) + "] from " +
                         "[" + documentId + "] archived by user: [" + user.getFullName() + "]" );
    }

    private void logDeletionToMainLog( String[] selectedChildrenIds, IMCServiceInterface imcref, TextDocumentDomainObject document, int menuIndex, UserDomainObject user ) {
        for ( int i = 0; i < selectedChildrenIds.length; i++ ) {
            int childId = Integer.parseInt( selectedChildrenIds[i] );
            imcref.updateMainLog( "Link from [" + document.getId() + "] in menu [" + menuIndex + "] to ["
                    + childId
                    + "] removed by user: ["
                    + user.getFullName()
                    + "]" );
        }
    }
}
