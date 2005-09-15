package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.TransactionDatabaseCommand;
import com.imcode.imcms.db.DatabaseConnectionUtils;
import com.imcode.imcms.db.DatabaseUtils;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DocumentPermissionSetMapper {

    /**
     * Stored procedure names used in this class
     */
    static final String SQL_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS = "SELECT group_id,group_name\n"
                                                                     + "FROM   templategroups tg\n"
                                                                     + "JOIN doc_permission_sets_ex dpse\n"
                                                                     + "       ON dpse.permission_data = tg.group_id\n"
                                                                     + "       AND dpse.meta_id = ?\n"
                                                                     + "       AND dpse.set_id = ?\n"
                                                                     + "       AND dpse.permission_id = 524288\n"
                                                                     + "ORDER  BY group_name" ;

    static final String SQL_GET_TEMPLATE_GROUPS_WITH_NEW_PERMISSIONS = "SELECT group_id,group_name\n"
                                                                       + "FROM   templategroups tg\n"
                                                                       + "JOIN new_doc_permission_sets_ex dpse\n"
                                                                       + "       ON dpse.permission_data = tg.group_id\n"
                                                                       + "       AND dpse.meta_id = ?\n"
                                                                       + "       AND dpse.set_id = ?\n"
                                                                       + "       AND dpse.permission_id = 524288\n"
                                                                       + "ORDER  BY group_name";

    static final String SQL_SELECT_PERMISSON_DATA__PREFIX = "SELECT permission_data FROM ";

    private final static int EDIT_DOCINFO_PERMISSION_ID = ImcmsConstants.PERM_EDIT_DOCINFO;
    private final static int EDIT_PERMISSIONS_PERMISSION_ID = ImcmsConstants.PERM_EDIT_PERMISSIONS;
    final static int EDIT_DOCUMENT_PERMISSION_ID = ImcmsConstants.PERM_EDIT_DOCUMENT;

    final static int EDIT_TEXT_DOCUMENT_TEXTS_PERMISSION_ID = EDIT_DOCUMENT_PERMISSION_ID;
    private final static int EDIT_TEXT_DOCUMENT_IMAGES_PERMISSION_ID = ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_IMAGES;
    private final static int EDIT_TEXT_DOCUMENT_MENUS_PERMISSION_ID = ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_MENUS;
    private final static int EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID = ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE;
    private final static int EDIT_TEXT_DOCUMENT_INCLUDES_PERMISSION_ID = ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_INCLUDES;

    private Database database;
    private ImcmsServices services;
    public static final String TABLE_NEW_DOC_PERMISSION_SETS = "new_doc_permission_sets";
    public static final String TABLE_DOC_PERMISSION_SETS = "doc_permission_sets";

    public DocumentPermissionSetMapper( Database database, ImcmsServices services ) {
        this.database = database ;
        this.services = services;
    }

    public DocumentPermissionSetDomainObject getRestrictedPermissionSet(DatabaseConnection connection, DocumentDomainObject document,
                                                                        DocumentPermissionSetTypeDomainObject documentPermissionSetType,
                                                                        boolean forNewDocuments) {
        DocumentPermissionSetDomainObject documentPermissionSet;
        if ( document instanceof TextDocumentDomainObject ) {
            documentPermissionSet = new TextDocumentPermissionSetDomainObject( documentPermissionSetType );
        } else {
            documentPermissionSet = new DocumentPermissionSetDomainObject( documentPermissionSetType );
        }
        setDocumentPermissionSetBitsFromDb( connection,document, documentPermissionSet, forNewDocuments );

        return documentPermissionSet;
    }

    private void setDocumentPermissionSetBitsFromDb(DatabaseConnection connection, DocumentDomainObject document,
                                                    DocumentPermissionSetDomainObject documentPermissionSet,
                                                    boolean forNewDocuments) {
        String table = getPermissionsTable( forNewDocuments );
        String sqlStr = "SELECT permission_id FROM " + table + " WHERE meta_id = ? AND set_id = ?";
        String[] parameters = new String[]{
                                                                            String.valueOf( document.getId() ),
                                                                            String.valueOf( documentPermissionSet.getType() )
                                                                    };
        String permissionBitsString = DatabaseConnectionUtils.executeStringQuery(connection, sqlStr, parameters);
        int permissionBits = 0;
        if ( null != permissionBitsString ) {
            permissionBits = Integer.parseInt( permissionBitsString );
        }
        documentPermissionSet.setFromBits( document, this, permissionBits, forNewDocuments );
    }

    private String getPermissionsTable( boolean forNewDocuments ) {
        String table = "doc_permission_sets";
        if ( forNewDocuments ) {
            table = "new_" + table;
        }
        return table;
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedOne(DatabaseConnection connection, DocumentDomainObject document) {
        return getRestrictedPermissionSet( connection, document, DocumentPermissionSetTypeDomainObject.RESTRICTED_1, false );
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedTwo(DatabaseConnection connection, DocumentDomainObject document) {
        return getRestrictedPermissionSet(connection, document, DocumentPermissionSetTypeDomainObject.RESTRICTED_2, false );
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedOneForNewDocuments(DatabaseConnection connection, DocumentDomainObject document) {
        return getRestrictedPermissionSet(connection, document, DocumentPermissionSetTypeDomainObject.RESTRICTED_1, true );
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedTwoForNewDocuments(DatabaseConnection connection, DocumentDomainObject document) {
        return getRestrictedPermissionSet(connection, document, DocumentPermissionSetTypeDomainObject.RESTRICTED_2, true );
    }

    public void saveRestrictedDocumentPermissionSets( DocumentDomainObject document, UserDomainObject user,
                                                      DocumentDomainObject oldDocument ) {
        if ( null == oldDocument || user.canDefineRestrictedOneFor( oldDocument ) ) {
            saveRestrictedDocumentPermissionSet( document, document.getPermissionSetForRestrictedOne(), false );
            saveRestrictedDocumentPermissionSet( document, document.getPermissionSetForRestrictedOneForNewDocuments(), true );
        }
        if ( null == oldDocument || user.canDefineRestrictedTwoFor( oldDocument ) ) {
            saveRestrictedDocumentPermissionSet( document, document.getPermissionSetForRestrictedTwo(), false );
            saveRestrictedDocumentPermissionSet( document, document.getPermissionSetForRestrictedTwoForNewDocuments(), true );
        }
    }

    public void saveRestrictedDocumentPermissionSet( final DocumentDomainObject document,
                                                     final DocumentPermissionSetDomainObject documentPermissionSet,
                                                     final boolean forNewDocuments ) {

        List permissionPairs = new ArrayList( Arrays.asList( new PermissionPair[]{
             new PermissionPair( EDIT_DOCINFO_PERMISSION_ID, documentPermissionSet.getEditDocumentInformation() ),
             new PermissionPair( EDIT_PERMISSIONS_PERMISSION_ID, documentPermissionSet.getEditPermissions() ),
        } ) );

        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet;
            permissionPairs.add( new PermissionPair( EDIT_TEXT_DOCUMENT_TEXTS_PERMISSION_ID, textDocumentPermissionSet.getEditTexts() ) );
            permissionPairs.add( new PermissionPair( EDIT_TEXT_DOCUMENT_IMAGES_PERMISSION_ID, textDocumentPermissionSet.getEditImages() ) );
            permissionPairs.add( new PermissionPair( EDIT_TEXT_DOCUMENT_MENUS_PERMISSION_ID, textDocumentPermissionSet.getEditMenus() ) );
            permissionPairs.add( new PermissionPair( EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID, textDocumentPermissionSet.getEditTemplates() ) );
            permissionPairs.add( new PermissionPair( EDIT_TEXT_DOCUMENT_INCLUDES_PERMISSION_ID, textDocumentPermissionSet.getEditIncludes() ) );
        } else {
            permissionPairs.add( new PermissionPair( EDIT_DOCUMENT_PERMISSION_ID, documentPermissionSet.getEdit() ) ) ;
        }

        int permissionBits = 0;
        for ( Iterator iterator = permissionPairs.iterator(); iterator.hasNext(); ) {
            PermissionPair permissionPair = (PermissionPair)iterator.next();
            if ( permissionPair.hasPermission ) {
                permissionBits |= permissionPair.bit;
            }
        }

        final int permissionBits1 = permissionBits;
        database.executeCommand( new TransactionDatabaseCommand() {
            public Object executeInTransaction( DatabaseConnection connection ) throws DatabaseException {
                sqlDeleteFromExtendedPermissionsTable( document, documentPermissionSet, forNewDocuments, connection );
                String tableName = forNewDocuments ? TABLE_NEW_DOC_PERMISSION_SETS : TABLE_DOC_PERMISSION_SETS ;
                connection.executeUpdate( "DELETE FROM "+tableName+"\n"
                                               + "WHERE meta_id = ?\n"
                                               + "AND  set_id = ?",
                        new String[] {"" + document.getId(), "" + documentPermissionSet.getType()} );
                connection.executeUpdate( "INSERT INTO "+tableName+" (meta_id, set_id, permission_id)\n"
                                               + "VALUES (?,?,?)", new String[] {"" + document.getId(), "" + documentPermissionSet.getType(),
                                                                           "" + permissionBits1} );
                return null ;
            }
        } );

        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet;
            sqlSaveAllowedTemplateGroups( document, textDocumentPermissionSet, forNewDocuments );
            sqlSaveAllowedDocumentTypes( document, textDocumentPermissionSet, forNewDocuments );
        }
    }

    private void sqlSaveAllowedTemplateGroups( DocumentDomainObject document,
                                               TextDocumentPermissionSetDomainObject textDocumentPermissionSet,
                                               boolean forNewDocuments ) {
        String table = getExtendedPermissionsTable( forNewDocuments );
        TemplateGroupDomainObject[] allowedTemplateGroups = textDocumentPermissionSet.getAllowedTemplateGroups( services );
        if ( null == allowedTemplateGroups ) {
            return;
        }
        String sqlInsertAllowedTemplateGroupId = "INSERT INTO " + table + " VALUES(?,?,"
                                                 + EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID
                                                 + ",?)";
        for ( int i = 0; i < allowedTemplateGroups.length; i++ ) {
            TemplateGroupDomainObject allowedTemplateGroup = allowedTemplateGroups[i];
            final Object[] parameters = new String[]{
                                                     "" + document.getId(), "" + textDocumentPermissionSet.getType(),
                                                     ""
                                                     + allowedTemplateGroup.getId()
                                             };
            DatabaseUtils.executeUpdate(database, sqlInsertAllowedTemplateGroupId, parameters);
        }
    }

    private void sqlDeleteFromExtendedPermissionsTable( DocumentDomainObject document,
                                                        DocumentPermissionSetDomainObject documentPermissionSet,
                                                        boolean forNewDocuments, DatabaseConnection connection ) throws DatabaseException {
        String table = getExtendedPermissionsTable( forNewDocuments );
        String sqlDelete = "DELETE FROM " + table
                           + " WHERE meta_id = ? AND set_id = ?";
        connection.executeUpdate( sqlDelete, new String[]{
                                                 "" + document.getId(), "" + documentPermissionSet.getType()
                                         } );
    }

    private void sqlSaveAllowedDocumentTypes( DocumentDomainObject document,
                                              TextDocumentPermissionSetDomainObject textDocumentPermissionSet,
                                              boolean forNewDocuments ) {
        String table = getExtendedPermissionsTable( forNewDocuments );
        int[] allowedDocumentTypeIds = textDocumentPermissionSet.getAllowedDocumentTypeIds();
        if ( null == allowedDocumentTypeIds ) {
            return;
        }
        String sqlInsertCreatableDocumentTypeId = "INSERT INTO " + table + " VALUES(?,?,"
                                                  + ImcmsConstants.PERM_CREATE_DOCUMENT
                                                  + ",?)";
        for ( int i = 0; i < allowedDocumentTypeIds.length; i++ ) {
            int creatableDocumentTypeId = allowedDocumentTypeIds[i];
            final Object[] parameters = new String[]{
                                                     "" + document.getId(), "" + textDocumentPermissionSet.getType(),
                                                     ""
                                                     + creatableDocumentTypeId
                                             };
            DatabaseUtils.executeUpdate(database, sqlInsertCreatableDocumentTypeId, parameters);
        }
    }

    private String getExtendedPermissionsTable( boolean forNewDocuments ) {
        String table = "doc_permission_sets_ex";
        if ( forNewDocuments ) {
            table = "new_" + table;
        }
        return table;
    }

    public void setDocumentPermissionSetFromBits( DocumentPermissionSetDomainObject documentPermissionSet,
                                           int permissionBits ) {
        documentPermissionSet.setEditDocumentInformation( 0 != ( permissionBits & EDIT_DOCINFO_PERMISSION_ID ) );
        documentPermissionSet.setEditPermissions( 0 != ( permissionBits & EDIT_PERMISSIONS_PERMISSION_ID ) );
        documentPermissionSet.setEdit( 0 != ( permissionBits & EDIT_DOCUMENT_PERMISSION_ID ) );
    }

    public void setTextDocumentPermissionSetFromBits( DocumentDomainObject document,
                                               TextDocumentPermissionSetDomainObject textDocumentPermissionSet,
                                               int permissionBits, boolean forNewDocuments ) {
        setDocumentPermissionSetFromBits( textDocumentPermissionSet, permissionBits );
        textDocumentPermissionSet.setEditTexts( 0 != ( permissionBits & EDIT_TEXT_DOCUMENT_TEXTS_PERMISSION_ID ) );
        textDocumentPermissionSet.setEditImages( 0 != ( permissionBits & EDIT_TEXT_DOCUMENT_IMAGES_PERMISSION_ID ) );
        textDocumentPermissionSet.setEditMenus( 0 != ( permissionBits & EDIT_TEXT_DOCUMENT_MENUS_PERMISSION_ID ) );
        textDocumentPermissionSet.setEditIncludes( 0
                                                   != ( permissionBits & EDIT_TEXT_DOCUMENT_INCLUDES_PERMISSION_ID ) );
        textDocumentPermissionSet.setEditTemplates( 0
                                                    != ( permissionBits & EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID ) );

        TemplateGroupDomainObject[] allowedTemplateGroups = sqlGetTemplateGroupsWithPermissions( document.getId(), textDocumentPermissionSet, forNewDocuments );
        textDocumentPermissionSet.setAllowedTemplateGroups( allowedTemplateGroups );
        int[] documentTypeIds = sqlGetDocTypesWithPermissions( document.getId(), textDocumentPermissionSet, forNewDocuments );
        textDocumentPermissionSet.setAllowedDocumentTypeIds( documentTypeIds );
    }

    private int[] sqlGetDocTypesWithPermissions( int metaId,
                                                 DocumentPermissionSetDomainObject documentPermissionSet,
                                                 boolean forNewDocuments ) {
        String table = getExtendedPermissionsTable( forNewDocuments );
        String sqlStr = SQL_SELECT_PERMISSON_DATA__PREFIX + table
                        + " WHERE meta_id = ? AND set_id = ? AND permission_id = "
                        + ImcmsConstants.PERM_CREATE_DOCUMENT;
        final Object[] parameters = new String[]{
                                                                            "" + metaId,
                                                                            "" + documentPermissionSet.getType()
                                                                    };
        String[] documentTypeIdStrings = DatabaseUtils.executeStringArrayQuery(database, sqlStr, parameters);
        int[] documentTypeIds = new int[documentTypeIdStrings.length];
        for ( int i = 0; i < documentTypeIdStrings.length; i++ ) {
            documentTypeIds[i] = Integer.parseInt( documentTypeIdStrings[i] );
        }
        return documentTypeIds;
    }

    private TemplateGroupDomainObject[] sqlGetTemplateGroupsWithPermissions( int metaId,
                                                                             DocumentPermissionSetDomainObject documentPermissionSet,
                                                                             boolean forNewDocuments ) {
        String[] params = new String[]{
                        String.valueOf( metaId ), String.valueOf( documentPermissionSet.getType() )
                };
        String sproc = forNewDocuments
                        ? SQL_GET_TEMPLATE_GROUPS_WITH_NEW_PERMISSIONS : SQL_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS;
        String[][] sprocResult = DatabaseUtils.execute2dStringArrayQuery(database, sproc, params);
        List templateGroups = new ArrayList();
        for ( int i = 0; i < sprocResult.length; i++ ) {
            int groupId = Integer.parseInt( sprocResult[i][0] );
            String groupName = sprocResult[i][1];
            TemplateGroupDomainObject templateGroup = new TemplateGroupDomainObject( groupId, groupName );
            templateGroups.add( templateGroup );
        }
        return (TemplateGroupDomainObject[])templateGroups.toArray( new TemplateGroupDomainObject[templateGroups.size()] );
    }

    private static class PermissionPair {

        int bit;
        boolean hasPermission;

        private PermissionPair( int permissionBit, boolean hasPermission ) {
            this.hasPermission = hasPermission;
            this.bit = permissionBit;
        }
    }

}
