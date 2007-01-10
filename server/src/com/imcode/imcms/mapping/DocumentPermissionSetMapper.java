package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.TransactionDatabaseCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.*;

public class DocumentPermissionSetMapper {

    private Database database;
    public static final String TABLE_NEW_DOC_PERMISSION_SETS = "new_doc_permission_sets";
    public static final String TABLE_DOC_PERMISSION_SETS = "doc_permission_sets";

    public DocumentPermissionSetMapper(Database database) {
        this.database = database;
    }


    public void saveRestrictedDocumentPermissionSets(DocumentDomainObject document, UserDomainObject user,
                                                     DocumentDomainObject oldDocument) {
        if ( null == oldDocument || user.canDefineRestrictedOneFor(oldDocument) ) {
            saveRestrictedDocumentPermissionSet(document, document.getPermissionSets().getRestricted1(), false);
            saveRestrictedDocumentPermissionSet(document, document.getPermissionSetsForNewDocuments().getRestricted1(), true);
        }
        if ( null == oldDocument || user.canDefineRestrictedTwoFor(oldDocument) ) {
            saveRestrictedDocumentPermissionSet(document, document.getPermissionSets().getRestricted2(), false);
            saveRestrictedDocumentPermissionSet(document, document.getPermissionSetsForNewDocuments().getRestricted2(), true);
        }
    }


    void saveRestrictedDocumentPermissionSet(final DocumentDomainObject document,
                                             final DocumentPermissionSetDomainObject documentPermissionSet,
                                             final boolean forNewDocuments) {

        List permissionPairs = new ArrayList(Arrays.asList(new PermissionPair[] {
                new PermissionPair(DocumentPermissionSetDomainObject.EDIT_DOCINFO_PERMISSION_ID, documentPermissionSet.getEditDocumentInformation()),
                new PermissionPair(DocumentPermissionSetDomainObject.EDIT_PERMISSIONS_PERMISSION_ID, documentPermissionSet.getEditPermissions()),
        }));

        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) documentPermissionSet;
            permissionPairs.add(new PermissionPair(TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEXTS_PERMISSION_ID, textDocumentPermissionSet.getEditTexts()));
            permissionPairs.add(new PermissionPair(TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_IMAGES_PERMISSION_ID, textDocumentPermissionSet.getEditImages()));
            permissionPairs.add(new PermissionPair(TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_MENUS_PERMISSION_ID, textDocumentPermissionSet.getEditMenus()));
            permissionPairs.add(new PermissionPair(TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID, textDocumentPermissionSet.getEditTemplates()));
            permissionPairs.add(new PermissionPair(TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_INCLUDES_PERMISSION_ID, textDocumentPermissionSet.getEditIncludes()));
        } else {
            permissionPairs.add(new PermissionPair(DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID, documentPermissionSet.getEdit()));
        }

        int permissionBits = 0;
        for ( Iterator iterator = permissionPairs.iterator(); iterator.hasNext(); ) {
            PermissionPair permissionPair = (PermissionPair) iterator.next();
            if ( permissionPair.hasPermission ) {
                permissionBits |= permissionPair.bit;
            }
        }

        final int permissionBits1 = permissionBits;
        database.execute(new TransactionDatabaseCommand() {
            public Object executeInTransaction(DatabaseConnection connection) throws DatabaseException {
                sqlDeleteFromExtendedPermissionsTable(document, documentPermissionSet, forNewDocuments, connection);
                String tableName = forNewDocuments ? TABLE_NEW_DOC_PERMISSION_SETS : TABLE_DOC_PERMISSION_SETS;
                connection.executeUpdate("DELETE FROM " + tableName + "\n"
                                         + "WHERE meta_id = ?\n"
                                         + "AND  set_id = ?",
                                         new String[] { "" + document.getId(), "" + documentPermissionSet.getType() });
                connection.executeUpdate("INSERT INTO " + tableName + " (meta_id, set_id, permission_id)\n"
                                         + "VALUES (?,?,?)", new String[] { "" + document.getId(),
                        "" + documentPermissionSet.getType(),
                        "" + permissionBits1 });
                return null;
            }
        });

        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) documentPermissionSet;
            sqlSaveAllowedTemplateGroups(document, textDocumentPermissionSet, forNewDocuments);
            sqlSaveAllowedDocumentTypes(document, textDocumentPermissionSet, forNewDocuments);
        }
    }

    private void sqlSaveAllowedTemplateGroups(DocumentDomainObject document,
                                              TextDocumentPermissionSetDomainObject textDocumentPermissionSet,
                                              boolean forNewDocuments) {
        String table = getExtendedPermissionsTable(forNewDocuments);
        Set allowedTemplateGroupIds = textDocumentPermissionSet.getAllowedTemplateGroupIds();
        String sqlInsertAllowedTemplateGroupId = "INSERT INTO " + table + " VALUES(?,?,"
                                                 + TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID
                                                 + ",?)";
        for ( Iterator iterator = allowedTemplateGroupIds.iterator(); iterator.hasNext(); ) {
            Integer allowedTemplateGroupId = (Integer) iterator.next();
            final Object[] parameters = new String[] {
                    "" + document.getId(), "" + textDocumentPermissionSet.getType(),
                    ""
                    + allowedTemplateGroupId.intValue()
            };
            database.execute(new SqlUpdateCommand(sqlInsertAllowedTemplateGroupId, parameters));
        }
    }

    private void sqlDeleteFromExtendedPermissionsTable(DocumentDomainObject document,
                                                       DocumentPermissionSetDomainObject documentPermissionSet,
                                                       boolean forNewDocuments,
                                                       DatabaseConnection connection) throws DatabaseException {
        String table = getExtendedPermissionsTable(forNewDocuments);
        String sqlDelete = "DELETE FROM " + table
                           + " WHERE meta_id = ? AND set_id = ?";
        connection.executeUpdate(sqlDelete, new String[] {
                "" + document.getId(), "" + documentPermissionSet.getType()
        });
    }

    private void sqlSaveAllowedDocumentTypes(DocumentDomainObject document,
                                             TextDocumentPermissionSetDomainObject textDocumentPermissionSet,
                                             boolean forNewDocuments) {
        String table = getExtendedPermissionsTable(forNewDocuments);
        Set allowedDocumentTypeIds = textDocumentPermissionSet.getAllowedDocumentTypeIds();
        String sqlInsertCreatableDocumentTypeId = "INSERT INTO " + table + " VALUES(?,?,"
                                                  + DocumentInitializer.PERM_CREATE_DOCUMENT
                                                  + ",?)";
        for ( Iterator iterator = allowedDocumentTypeIds.iterator(); iterator.hasNext(); ) {
            Integer allowedDocumentTypeId = (Integer) iterator.next();
            final Object[] parameters = new String[] {
                    "" + document.getId(), "" + textDocumentPermissionSet.getType(),
                    ""
                    + allowedDocumentTypeId.intValue()
            };
            database.execute(new SqlUpdateCommand(sqlInsertCreatableDocumentTypeId, parameters));
        }
    }

    private String getExtendedPermissionsTable(boolean forNewDocuments) {
        String table = "doc_permission_sets_ex";
        if ( forNewDocuments ) {
            table = "new_" + table;
        }
        return table;
    }


    private static class PermissionPair {

        int bit;
        boolean hasPermission;

        private PermissionPair(int bit, boolean hasPermission) {
            this.hasPermission = hasPermission;
            this.bit = bit;
        }
    }

}
