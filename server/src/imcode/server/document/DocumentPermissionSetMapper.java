package imcode.server.document;

import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.*;

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

    public DocumentPermissionSetMapper( Database service, ImcmsServices services ) {
        this.database = service;
        this.services = services;
    }

    public DocumentPermissionSetDomainObject getRestrictedPermissionSet( DocumentDomainObject document,
                                                                         int permissionTypeId,
                                                                         boolean forNewDocuments ) {
        DocumentPermissionSetDomainObject documentPermissionSet;
        if ( document instanceof TextDocumentDomainObject ) {
            documentPermissionSet = new TextDocumentPermissionSetDomainObject( permissionTypeId );
        } else {
            documentPermissionSet = new DocumentPermissionSetDomainObject( permissionTypeId );
        }
        setDocumentPermissionSetBitsFromDb( document, documentPermissionSet, forNewDocuments );

        return documentPermissionSet;
    }

    private void setDocumentPermissionSetBitsFromDb( DocumentDomainObject document,
                                                     DocumentPermissionSetDomainObject documentPermissionSet,
                                                     boolean forNewDocuments ) {
        String table = getPermissionsTable( forNewDocuments );
        String sqlStr = "SELECT permission_id FROM " + table + " WHERE meta_id = ? AND set_id = ?";
        String permissionBitsString = database.sqlQueryStr( sqlStr, new String[]{
                                                                            String.valueOf( document.getId() ),
                                                                            String.valueOf( documentPermissionSet.getTypeId() )
                                                                    } );
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

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedOne( DocumentDomainObject document ) {
        return getRestrictedPermissionSet( document, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1, false );
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedTwo( DocumentDomainObject document ) {
        return getRestrictedPermissionSet( document, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, false );
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedOneForNewDocuments(
            DocumentDomainObject document ) {
        return getRestrictedPermissionSet( document, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1, true );
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedTwoForNewDocuments(
            DocumentDomainObject document ) {
        return getRestrictedPermissionSet( document, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, true );
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
        database.executeTransaction( new DatabaseCommand() {
            public void executeOn( DatabaseConnection connection ) {
                sqlDeleteFromExtendedPermissionsTable( document, documentPermissionSet, forNewDocuments, connection );
                String tableName = forNewDocuments ? TABLE_NEW_DOC_PERMISSION_SETS : TABLE_DOC_PERMISSION_SETS ;
                connection.executeUpdate( "DELETE FROM "+tableName+"\n"
                                               + "WHERE meta_id = ?\n"
                                               + "AND  set_id = ?",
                        new String[] {"" + document.getId(), "" + documentPermissionSet.getTypeId()} );
                connection.executeUpdate( "INSERT INTO "+tableName+" (meta_id, set_id, permission_id)\n"
                                               + "VALUES (?,?,?)", new String[] {"" + document.getId(), "" + documentPermissionSet.getTypeId(),
                                                                           "" + permissionBits1} );
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
            database.sqlUpdateQuery( sqlInsertAllowedTemplateGroupId, new String[]{
                                                     "" + document.getId(), "" + textDocumentPermissionSet.getTypeId(),
                                                     ""
                                                     + allowedTemplateGroup.getId()
                                             } );
        }
    }

    private void sqlDeleteFromExtendedPermissionsTable( DocumentDomainObject document,
                                                        DocumentPermissionSetDomainObject documentPermissionSet,
                                                        boolean forNewDocuments, DatabaseConnection connection ) {
        String table = getExtendedPermissionsTable( forNewDocuments );
        String sqlDelete = "DELETE FROM " + table
                           + " WHERE meta_id = ? AND set_id = ?";
        connection.executeUpdate( sqlDelete, new String[]{
                                                 "" + document.getId(), "" + documentPermissionSet.getTypeId()
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
            database.sqlUpdateQuery( sqlInsertCreatableDocumentTypeId, new String[]{
                                                     "" + document.getId(), "" + textDocumentPermissionSet.getTypeId(),
                                                     ""
                                                     + creatableDocumentTypeId
                                             } );
        }
    }

    private String getExtendedPermissionsTable( boolean forNewDocuments ) {
        String table = "doc_permission_sets_ex";
        if ( forNewDocuments ) {
            table = "new_" + table;
        }
        return table;
    }

    void setDocumentPermissionSetFromBits( DocumentPermissionSetDomainObject documentPermissionSet,
                                           int permissionBits ) {
        documentPermissionSet.setEditDocumentInformation( 0 != ( permissionBits & EDIT_DOCINFO_PERMISSION_ID ) );
        documentPermissionSet.setEditPermissions( 0 != ( permissionBits & EDIT_PERMISSIONS_PERMISSION_ID ) );
        documentPermissionSet.setEdit( 0 != ( permissionBits & EDIT_DOCUMENT_PERMISSION_ID ) );
    }

    void setTextDocumentPermissionSetFromBits( DocumentDomainObject document,
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
        String[] documentTypeIdStrings = database.sqlQuery( sqlStr, new String[]{
                                                                            "" + metaId,
                                                                            "" + documentPermissionSet.getTypeId()
                                                                    } );
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
                        String.valueOf( metaId ), String.valueOf( documentPermissionSet.getTypeId() )
                };
        String sproc = forNewDocuments
                        ? SQL_GET_TEMPLATE_GROUPS_WITH_NEW_PERMISSIONS : SQL_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS;
        String[][] sprocResult = database.sqlQueryMulti( sproc, params );
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
