package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.MaxCategoryDomainObjectsOfTypeExceededException;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;
import java.util.Date;

/**
 * Save document sorting (date,name,manual)
 */
public class SaveSort extends HttpServlet {

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private final static String FILE_WARNING_TEMPLATE = "copy_file_warning.html";

    /**
     * service()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        int documentId = Integer.parseInt( req.getParameter( "meta_id" ) );
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !imcref.checkDocAdminRights( documentId, user, 262144 ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( documentId, documentId, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        String temp_str;
        String[] selectedChildrenIds;
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( documentId );
        documentMapper.touchDocument( document );

        String[] children = imcref.sqlQuery( "select to_meta_id from childs, menus where childs.menu_id = menus.menu_id AND meta_id = ?", new String[]{"" + documentId} );

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

        user.put( "flags", new Integer( 262144 ) );

        int menuIndex = Integer.parseInt( req.getParameter( "doc_menu_no" ) );
        String sortParam = req.getParameter( "sort" );
        if ( sortParam != null ) {
            int sort_order = Integer.parseInt( req.getParameter( "sort_order" ) );
            String[] queryResult = imcref.sqlQuery( "select sort_order from menus where meta_id = ? AND menu_index = ?",
                                                    new String[]{"" + documentId, ""+menuIndex} );
            int currentSortOrder = IMCConstants.MENU_SORT_DEFAULT ;
            if (0 < queryResult.length) {
                String currentSortOrderStr = queryResult[0];
                currentSortOrder = Integer.parseInt( currentSortOrderStr );
            }
            if ( currentSortOrder != sort_order ) {
                imcref.sqlUpdateQuery( "update menus set sort_order = ? where meta_id = ? AND menu_index = ?",
                                       new String[]{"" + sort_order, "" + documentId, ""+menuIndex} );
            } else {
                if ( childs.size() > 0 ) {
                    if ( IMCConstants.MENU_SORT_BY_MANUAL_ORDER == sort_order ) {
                        imcref.saveManualSort( documentId, user, childs, sort_no, menuIndex );
                    } else if ( IMCConstants.MENU_SORT_BY_MANUAL_TREE_ORDER == sort_order ) {
                        imcref.saveTreeSortIndex( documentId, user, childs, sort_no, menuIndex );
                    }
                }
            }
        } else if ( req.getParameter( "delete" ) != null ) {
            if ( selectedChildrenIds != null ) {
                imcref.deleteChilds( documentId, menuIndex, user, selectedChildrenIds );
            }
        } else if ( req.getParameter( "archive" ) != null ) {
            if ( selectedChildrenIds != null ) {
                imcref.archiveChilds( documentId, user, selectedChildrenIds );
            }
        } else if ( req.getParameter( "copy" ) != null ) {
            if ( selectedChildrenIds != null ) {
                String copyHeadlineSuffix = imcref.getAdminTemplate( COPY_HEADLINE_SUFFIX_TEMPLATE, user, null );

                for ( int i = 0; i < selectedChildrenIds.length; i++ ) {
                    String selectedChildIdStr = selectedChildrenIds[i];
                    int selectedChildId = Integer.parseInt( selectedChildIdStr ) ;
                    DocumentDomainObject selectedChild = documentMapper.getDocument( selectedChildId ) ;
                    try {
                        selectedChild.setHeadline( selectedChild.getHeadline()+copyHeadlineSuffix );
                        selectedChild.setStatus( DocumentDomainObject.STATUS_NEW );
                        selectedChild.setPublicationStartDatetime( new Date() );
                        documentMapper.saveNewDocument( selectedChild, user );
                        documentMapper.addDocumentToMenu( user, documentId, menuIndex, selectedChild.getId());
                    } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
                        throw new RuntimeException(e) ;
                    } catch ( DocumentMapper.DocumentAlreadyInMenuException e ) {
                        throw new RuntimeException(e) ;
                    }
                }
            }
        }

        String output = AdminDoc.adminDoc( documentId, documentId, user, req, res );
        if ( output != null ) {
            out.write( output );
        }
    }
}
