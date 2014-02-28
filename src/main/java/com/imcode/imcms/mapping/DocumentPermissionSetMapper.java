package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.Meta;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.*;

/**
 * Copies documents permissions into meta.
 */
//todo: cleanup legacy SQL code
public class DocumentPermissionSetMapper {

    public void saveRestrictedDocumentPermissionSets(Meta ormMeta, DocumentDomainObject document, UserDomainObject user,
                                                     DocumentDomainObject oldDocument) {
        if (null == oldDocument || user.canDefineRestrictedOneFor(oldDocument)) {
            saveRestrictedDocumentPermissionSet(ormMeta, document, document.getPermissionSets().getRestricted1(), false);
            saveRestrictedDocumentPermissionSet(ormMeta, document, document.getPermissionSetsForNewDocument().getRestricted1(), true);
        }
        if (null == oldDocument || user.canDefineRestrictedTwoFor(oldDocument)) {
            saveRestrictedDocumentPermissionSet(ormMeta, document, document.getPermissionSets().getRestricted2(), false);
            saveRestrictedDocumentPermissionSet(ormMeta, document, document.getPermissionSetsForNewDocument().getRestricted2(), true);
        }
    }


    void saveRestrictedDocumentPermissionSet(Meta ormMeta, DocumentDomainObject document,
                                             DocumentPermissionSetDomainObject documentPermissionSet,
                                             boolean forNewDocuments) {

        List<PermissionPair> permissionPairs = new ArrayList<>();

        permissionPairs.add(new PermissionPair(DocumentPermissionSetDomainObject.EDIT_DOCINFO_PERMISSION_ID, documentPermissionSet.getEditDocumentInformation()));
        permissionPairs.add(new PermissionPair(DocumentPermissionSetDomainObject.EDIT_PERMISSIONS_PERMISSION_ID, documentPermissionSet.getEditPermissions()));

        if (document instanceof TextDocumentDomainObject) {
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
        for (PermissionPair permissionPair : permissionPairs) {
            if (permissionPair.hasPermission) {
                permissionBits |= permissionPair.bit;
            }
        }

        Set<Meta.PermissionSetEx> permissionSetEx = forNewDocuments
                ? ormMeta.getPermissionSetExForNew()
                : ormMeta.getPermissionSetEx();

        Map<Integer, Integer> permissionSetBitsMap = forNewDocuments
                ? ormMeta.getPermissionSetBitsForNewMap()
                : ormMeta.getPermissionSetBitsMap();

        Integer setId = documentPermissionSet.getType().getId();

        permissionSetBitsMap.put(setId, permissionBits);

        // -> sqlDeleteFromExtendedPermissionsTable db command block        
        //TODO: Optimize - currently in prototype state: removes permisionSetEx 
        // for current setId
        Set<Meta.PermissionSetEx> filteredPermissionSetEx = new HashSet<>();
        for (Meta.PermissionSetEx setEx : permissionSetEx) {
            if (!setEx.getSetId().equals(setId)) {
                filteredPermissionSetEx.add(setEx);
            }
        }

        permissionSetEx = filteredPermissionSetEx;
        if (forNewDocuments) {
            ormMeta.setPermissionSetExForNew(permissionSetEx);
        } else {
            ormMeta.setPermissionSetEx(permissionSetEx);
        }

        //TODO: end of optimize
                
        /*        
        int permissionBits1 = permissionBits;
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
        */

        if (document instanceof TextDocumentDomainObject) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) documentPermissionSet;
            sqlSaveAllowedTemplateGroups(document, textDocumentPermissionSet, forNewDocuments, permissionSetEx);
            sqlSaveAllowedDocumentTypes(document, textDocumentPermissionSet, forNewDocuments, permissionSetEx);
        }
    }

    private void sqlSaveAllowedTemplateGroups(DocumentDomainObject document,
                                              TextDocumentPermissionSetDomainObject textDocumentPermissionSet,
                                              boolean forNewDocuments, Set<Meta.PermissionSetEx> permissionSetEx) {

        Set<Integer> allowedTemplateGroupIds = textDocumentPermissionSet.getAllowedTemplateGroupIds();
        Integer setId = textDocumentPermissionSet.getType().getId();

        for (Integer allowedTemplateGroupId : allowedTemplateGroupIds) {
            Meta.PermissionSetEx setEx = new Meta.PermissionSetEx();

            setEx.setSetId(setId);
            setEx.setPermissionId(TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID);
            setEx.setPermissionData(allowedTemplateGroupId);

            permissionSetEx.add(setEx);
        }

    	/*
        String table = getExtendedPermissionsTable(forNewDocuments);        
        String sqlInsertAllowedTemplateGroupId = "INSERT INTO " + table + " VALUES(?,?,"
                                                 + TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID
                                                 + ",?)";
        for ( Iterator iterator = allowedTemplateGroupIds.iterator(); iterator.hasNext(); ) {
            Integer allowedTemplateGroupId = (Integer) iterator.next();
            Object[] parameters = new String[] {
                    "" + document.getId(), "" + textDocumentPermissionSet.getType(),
                    ""
                    + allowedTemplateGroupId.intValue()
            };
            database.execute(new SqlUpdateCommand(sqlInsertAllowedTemplateGroupId, parameters));
        }
        */

    }

    /*
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
    */

    private void sqlSaveAllowedDocumentTypes(DocumentDomainObject document,
                                             TextDocumentPermissionSetDomainObject textDocumentPermissionSet,
                                             boolean forNewDocuments, Set<Meta.PermissionSetEx> permissionSetEx) {
        Set<Integer> allowedDocumentTypeIds = textDocumentPermissionSet.getAllowedDocumentTypeIds();
        Integer setId = textDocumentPermissionSet.getType().getId();

        for (Integer allowedDocumentTypeId : allowedDocumentTypeIds) {
            Meta.PermissionSetEx setEx = new Meta.PermissionSetEx();

            setEx.setSetId(setId);
            setEx.setPermissionId(DocumentLoader.PERM_CREATE_DOCUMENT);
            setEx.setPermissionData(allowedDocumentTypeId);

            permissionSetEx.add(setEx);
        }

    	/*
        String table = getExtendedPermissionsTable(forNewDocuments);        
        String sqlInsertCreatableDocumentTypeId = "INSERT INTO " + table + " VALUES(?,?,"
                                                  + DocumentLoader.PERM_CREATE_DOCUMENT
                                                  + ",?)";
        for ( Iterator iterator = allowedDocumentTypeIds.iterator(); iterator.hasNext(); ) {
            Integer allowedDocumentTypeId = (Integer) iterator.next();
            Object[] parameters = new String[] {
                    "" + document.getId(), "" + textDocumentPermissionSet.getType(),
                    ""
                    + allowedDocumentTypeId.intValue()
            };
            database.execute(new SqlUpdateCommand(sqlInsertCreatableDocumentTypeId, parameters));
        }
        */
    }

    /*
    private String getExtendedPermissionsTable(boolean forNewDocuments) {
        String table = "doc_permission_sets_ex";
        if ( forNewDocuments ) {
            table = "new_" + table;
        }
        return table;
    }
	*/

    private static class PermissionPair {

        int bit;
        boolean hasPermission;

        private PermissionPair(int bit, boolean hasPermission) {
            this.hasPermission = hasPermission;
            this.bit = bit;
        }
    }

}
