package imcode.server.document;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.ConvenienceDatabaseConnection;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.LanguageMapper;
import imcode.server.ImcmsServices;
import imcode.util.DateConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class ConnectionDocumentGetter implements DocumentGetter {
    private ConvenienceDatabaseConnection connection;
    private ImcmsServices services;
    private ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper;
    Logger log = Logger.getLogger(ConnectionDocumentGetter.class) ;

    public ConnectionDocumentGetter(DatabaseConnection connection, ImcmsServices services) {
        this.connection = new ConvenienceDatabaseConnection(connection);
        this.services = services ;
        this.userAndRoleMapper = services.getImcmsAuthenticatorAndUserAndRoleMapper() ;
    }

    public DocumentDomainObject getDocument(DocumentId documentId) {
        log.debug("Getting document " + documentId + " from db.");

        String[] result = sprocGetDocumentInfo(connection, documentId.intValue());

        DocumentDomainObject document = null;
        if (0 != result.length) {
            document = getDocumentFromSqlResultRow(result);
            initDocumentAttributes(connection,document);
            initDocumentCategories(connection,document);
            initRolesMappedToDocumentPermissionSetIds(connection,document);

            document.accept(new DocumentInitializingVisitor(connection, services));
        }
        return document ;

    }

    private String[] sprocGetDocumentInfo(ConvenienceDatabaseConnection connection, int metaId) {
        String[] params = new String[]{String.valueOf(metaId)};
        return connection.executeArrayQuery(DocumentMapper.SQL_GET_DOCUMENT, params);
    }

    private DocumentDomainObject getDocumentFromSqlResultRow(String[] result) {
        final int documentTypeId = Integer.parseInt(result[1]);
        DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(documentTypeId);

        document.setId(Integer.parseInt(result[0]));
        document.setHeadline(result[2]);
        document.setMenuText(result[3]);
        document.setMenuImage(result[4]);
        UserDomainObject creator = userAndRoleMapper.getUser(Integer.parseInt(result[5]));
        document.setCreator(creator);
        document.setRestrictedOneMorePrivilegedThanRestrictedTwo(DocumentMapper.getBooleanFromSqlResultString(result[6]));
        document.setLinkableByOtherUsers(DocumentMapper.getBooleanFromSqlResultString(result[7]));
        document.setVisibleInMenusForUnauthorizedUsers(DocumentMapper.getBooleanFromSqlResultString(result[8]));
        document.setLanguageIso639_2(LanguageMapper.getAsIso639_2OrDefaultLanguage(result[9], services.getDefaultLanguage()));
        DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
        document.setCreatedDatetime(DocumentMapper.parseDateFormat(dateFormat, result[10]));
        Date modifiedDatetime = DocumentMapper.parseDateFormat(dateFormat, result[11]);
        document.setModifiedDatetime(modifiedDatetime);
        document.setLastModifiedDatetime(modifiedDatetime);
        document.setSearchDisabled(DocumentMapper.getBooleanFromSqlResultString(result[12]));
        document.setTarget(result[13]);
        document.setArchivedDatetime(DocumentMapper.parseDateFormat(dateFormat, result[14]));
        String publisherIdStr = result[15];
        if (null != publisherIdStr) {
            UserDomainObject publisher = userAndRoleMapper.getUser(Integer.parseInt(publisherIdStr));
            document.setPublisher(publisher);
        }
        document.setStatus(Integer.parseInt(result[16]));
        document.setPublicationStartDatetime(DocumentMapper.parseDateFormat(dateFormat, result[17]));
        document.setPublicationEndDatetime(DocumentMapper.parseDateFormat(dateFormat, result[18]));
        return document;
    }

    private DocumentPermissionSetMapper getDocumentPermissionSetMapper() {
        return services.getDocumentMapper().getDocumentPermissionSetMapper();
    }

    public void initDocumentAttributes(ConvenienceDatabaseConnection connection, DocumentDomainObject document) {

        document.setSections(getSections(connection,document.getId()));

        document.setKeywords(getKeywords(connection,document.getId()));

        document.setPermissionSetForRestrictedOne(getDocumentPermissionSetMapper().getPermissionSetRestrictedOne(connection, document));
        document.setPermissionSetForRestrictedTwo(getDocumentPermissionSetMapper().getPermissionSetRestrictedTwo(connection, document));

        document.setPermissionSetForRestrictedOneForNewDocuments(getDocumentPermissionSetMapper().getPermissionSetRestrictedOneForNewDocuments(connection, document));
        document.setPermissionSetForRestrictedTwoForNewDocuments(getDocumentPermissionSetMapper().getPermissionSetRestrictedTwoForNewDocuments(connection, document));

    }

    private String[] getKeywords(ConvenienceDatabaseConnection connection, int meta_id) {
        String sqlStr;
        sqlStr =
                "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = ?";
        String[] params = new String[]{"" + meta_id};
        String[] keywords = connection.executeArrayQuery(sqlStr, params);
        return keywords;
    }

    /**
     * @return the sections for a document, empty array if there is none.
     */
    private SectionDomainObject[] getSections(ConvenienceDatabaseConnection connection, int meta_id) {
        String[] parameters = new String[]{String.valueOf(meta_id)};
        String[][] sectionData = connection.execute2dArrayQuery(DocumentMapper.SQL_GET_SECTIONS_FOR_DOCUMENT, parameters);

        SectionDomainObject[] sections = new SectionDomainObject[sectionData.length];

        for (int i = 0; i < sectionData.length; i++) {
            int sectionId = Integer.parseInt(sectionData[i][0]);
            String sectionName = sectionData[i][1];
            sections[i] = new SectionDomainObject(sectionId, sectionName);
        }
        return sections;
    }

    public void initDocumentCategories(ConvenienceDatabaseConnection connection, DocumentDomainObject document) {
        String[][] categories = connection.execute2dArrayQuery( "SELECT categories.category_id, categories.name, categories.image, categories.description, "+
                                                              DocumentMapper.SQL__CATEGORY_TYPE__COLUMNS
                                                              + " FROM document_categories"
                                                              + " JOIN categories"
                                                              + "  ON document_categories.category_id = categories.category_id"
                                                              + " JOIN category_types"
                                                              + "  ON categories.category_type_id = category_types.category_type_id"
                                                              + " WHERE document_categories.meta_id = ?",
                                                              new String[]{"" + document.getId()} );
        for (int i = 0; i < categories.length; i++) {
            String[] categoryArray = categories[i];

            int categoryId = Integer.parseInt(categoryArray[0]);
            String categoryName = categoryArray[1];
            String categoryImage = categoryArray[2];
            String categoryDescription = categoryArray[3];

            CategoryTypeDomainObject categoryType = DocumentMapper.createCategoryTypeFromSqlResult( categoryArray, 4 ) ;
            CategoryDomainObject category = new CategoryDomainObject(categoryId, categoryName, categoryDescription,
                    categoryImage, categoryType);
            document.addCategory(category);
        }

    }

    public void initRolesMappedToDocumentPermissionSetIds(ConvenienceDatabaseConnection connection, DocumentDomainObject document) {

        String[] parameters = new String[]{"" + document.getId()};
        String[][] sprocResult = connection.execute2dArrayQuery("SELECT "
                + ImcmsAuthenticatorAndUserAndRoleMapper.SQL_ROLES_COLUMNS
                + ", rr.set_id\n"
                + "FROM  roles, roles_rights AS rr\n"
                + "WHERE rr.role_id = roles.role_id AND rr.meta_id = ?", parameters);

        for (int i = 0; i < sprocResult.length; ++i) {
            RoleDomainObject role = userAndRoleMapper.getRoleFromSqlResult(sprocResult[i]);

            int rolePermissionSetId = Integer.parseInt(sprocResult[i][4]);
            document.setPermissionSetIdForRole(role, rolePermissionSetId);
        }

    }

}
