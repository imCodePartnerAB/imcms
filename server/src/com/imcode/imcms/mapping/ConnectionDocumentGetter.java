package com.imcode.imcms.mapping;

import com.imcode.db.DatabaseConnection;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.db.DatabaseConnectionUtils;
import imcode.server.ImcmsServices;
import imcode.server.LanguageMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentGetter;
import imcode.server.document.DocumentId;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.SectionDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.ArraySet;
import imcode.util.DateConstants;
import imcode.util.Utility;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class ConnectionDocumentGetter implements DocumentGetter {
    private DatabaseConnection connection;
    private ImcmsServices services;
    private ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper;
    Logger log = Logger.getLogger(ConnectionDocumentGetter.class) ;

    public ConnectionDocumentGetter(DatabaseConnection connection, ImcmsServices services) {
        this.connection = connection;
        this.services = services ;
        this.userAndRoleMapper = services.getImcmsAuthenticatorAndUserAndRoleMapper() ;
    }

    public DocumentDomainObject getDocument(DocumentId documentId) {
        log.trace("Getting document " + documentId + " from db.");

        String[] result = sprocGetDocumentInfo(connection, documentId.intValue());

        DocumentDomainObject document = null;
        if (0 != result.length) {
            document = getDocumentFromSqlResultRow(result);
            initDocumentAttributes(connection,document);
            CategoryMapper categoryMapper = services.getCategoryMapper();
            categoryMapper.initDocumentCategories(connection,document);
            initRolesMappedToDocumentPermissionSetIds(connection,document);

            document.accept(new DocumentInitializingVisitor(connection, services));
        }
        return document ;

    }

    private String[] sprocGetDocumentInfo(DatabaseConnection connection, int metaId) {
        String[] params = new String[]{String.valueOf(metaId)};
        return DatabaseConnectionUtils.executeStringArrayQuery(connection, DefaultDocumentMapper.SQL_GET_DOCUMENT, params);
    }

    private DocumentDomainObject getDocumentFromSqlResultRow(String[] result) {
        final int documentTypeId = Integer.parseInt(result[1]);
        DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(documentTypeId);

        int documentId = Integer.parseInt(result[0]);
        document.setId(documentId);
        document.setHeadline(result[2]);
        document.setMenuText(result[3]);
        document.setMenuImage(result[4]);
        int creatorId = Integer.parseInt(result[5]);
        UserDomainObject creator = userAndRoleMapper.getUser(creatorId);
        if (null == creator) {
            log.error("Creator of document "+documentId+", user "+creatorId+" is non-existent. Missing foreign key in database?");
        }
        document.setCreator(creator);
        document.setRestrictedOneMorePrivilegedThanRestrictedTwo(getBooleanFromSqlResultString(result[6]));
        document.setLinkableByOtherUsers(getBooleanFromSqlResultString(result[7]));
        document.setVisibleInMenusForUnauthorizedUsers(getBooleanFromSqlResultString(result[8]));
        document.setLanguageIso639_2(LanguageMapper.getAsIso639_2OrDefaultLanguage(result[9], services.getLanguageMapper().getDefaultLanguage()));
        DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
        document.setCreatedDatetime(Utility.parseDateFormat(dateFormat, result[10]));
        Date modifiedDatetime = Utility.parseDateFormat(dateFormat, result[11]);
        document.setModifiedDatetime(modifiedDatetime);
        document.setActualModifiedDatetime(modifiedDatetime);
        document.setSearchDisabled(getBooleanFromSqlResultString(result[12]));
        document.setTarget(result[13]);
        document.setArchivedDatetime(Utility.parseDateFormat(dateFormat, result[14]));
        String publisherIdStr = result[15];
        if (null != publisherIdStr) {
            UserDomainObject publisher = userAndRoleMapper.getUser(Integer.parseInt(publisherIdStr));
            document.setPublisher(publisher);
        }
        int publicationStatusInt = Integer.parseInt(result[16]);
        Document.PublicationStatus publicationStatus = publicationStatusFromInt(publicationStatusInt) ;
        document.setPublicationStatus(publicationStatus);
        document.setPublicationStartDatetime(Utility.parseDateFormat(dateFormat, result[17]));
        document.setPublicationEndDatetime(Utility.parseDateFormat(dateFormat, result[18]));
        return document;
    }

    private Document.PublicationStatus publicationStatusFromInt(int publicationStatusInt) {
        Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;
        if ( Document.STATUS_PUBLICATION_APPROVED == publicationStatusInt ) {
            publicationStatus = Document.PublicationStatus.APPROVED;
        } else if ( Document.STATUS_PUBLICATION_DISAPPROVED == publicationStatusInt ) {
                publicationStatus = Document.PublicationStatus.DISAPPROVED;
        }
        return publicationStatus;
    }

    private DocumentPermissionSetMapper getDocumentPermissionSetMapper() {
        return services.getDefaultDocumentMapper().getDocumentPermissionSetMapper();
    }

    public void initDocumentAttributes(DatabaseConnection connection, DocumentDomainObject document) {

        document.setSections(getSections(connection,document.getId()));

        document.setKeywords(getKeywords(connection,document.getId()));

        document.setPermissionSetForRestrictedOne(getDocumentPermissionSetMapper().getPermissionSetRestrictedOne(connection, document));
        document.setPermissionSetForRestrictedTwo(getDocumentPermissionSetMapper().getPermissionSetRestrictedTwo(connection, document));

        document.setPermissionSetForRestrictedOneForNewDocuments(getDocumentPermissionSetMapper().getPermissionSetRestrictedOneForNewDocuments(connection, document));
        document.setPermissionSetForRestrictedTwoForNewDocuments(getDocumentPermissionSetMapper().getPermissionSetRestrictedTwoForNewDocuments(connection, document));

    }

    private Set getKeywords(DatabaseConnection connection, int meta_id) {
        String sqlStr;
        sqlStr =
        "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = ?";
        String[] params = new String[]{"" + meta_id};
        final String[] keywords = DatabaseConnectionUtils.executeStringArrayQuery(connection, sqlStr, params);
        return new ArraySet(keywords);
    }

    /**
     * @return the sections for a document, empty array if there is none.
     */
    private SectionDomainObject[] getSections(DatabaseConnection connection, int meta_id) {
        String[] parameters = new String[]{String.valueOf(meta_id)};
        String[][] sectionData = DatabaseConnectionUtils.execute2dStringArrayQuery(connection, DefaultDocumentMapper.SQL_GET_SECTIONS_FOR_DOCUMENT, parameters);

        SectionDomainObject[] sections = new SectionDomainObject[sectionData.length];

        for (int i = 0; i < sectionData.length; i++) {
            int sectionId = Integer.parseInt(sectionData[i][0]);
            String sectionName = sectionData[i][1];
            sections[i] = new SectionDomainObject(sectionId, sectionName);
        }
        return sections;
    }

    public void initRolesMappedToDocumentPermissionSetIds(DatabaseConnection connection, DocumentDomainObject document) {

        String[] parameters = new String[]{"" + document.getId()};
        String[][] sprocResult = DatabaseConnectionUtils.execute2dStringArrayQuery(connection, "SELECT "
                                                                                               + ImcmsAuthenticatorAndUserAndRoleMapper
                .SQL_ROLES_COLUMNS
                                                                                               + ", rr.set_id\n"
                                                                                               + "FROM  roles, roles_rights AS rr\n"
                                                                                               + "WHERE rr.role_id = roles.role_id AND rr.meta_id = ?", parameters);

        for (int i = 0; i < sprocResult.length; ++i) {
            RoleId roleId = new RoleId(Integer.parseInt(sprocResult[i][0]));
            int rolePermissionSetTypeId = Integer.parseInt(sprocResult[i][4]);
            DocumentPermissionSetTypeDomainObject documentPermissionSetType = DocumentPermissionSetTypeDomainObject.fromInt(rolePermissionSetTypeId);
            document.setDocumentPermissionSetTypeForRoleId(roleId, documentPermissionSetType);
        }

    }

    private static boolean getBooleanFromSqlResultString(final String columnValue) {
        return !"0".equals(columnValue);
    }
}
