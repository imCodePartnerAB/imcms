package imcode.server.document;

import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.api.CategoryAlreadyExistsException;
import imcode.server.*;
import imcode.server.db.Database;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.collections.map.AbstractMapDecorator;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.oro.text.perl.Perl5Util;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentMapper {

    private final static Logger log = Logger.getLogger( DocumentMapper.class.getName() );

    private static final int UNLIMITED_MAX_CATEGORY_CHOICES = 0;

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

    // Stored procedure names used in this class
    // todo make sure all these is only used in one sprocMethod
    private static final String SPROC_GET_TEXT = "GetText";
    private static final String SQL_GET_ALL_SECTIONS = "SELECT section_id, section_name FROM sections";
    private static final String SPROC_GET_DOC_TYPES_FOR_USER = "GetDocTypesForUser";
    static final String SPROC_SET_PERMISSION_SET_ID_FOR_ROLE_ON_DOCUMENT = "SetRoleDocPermissionSetId";

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private final ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper;
    private final Database database;
    private final DocumentPermissionSetMapper documentPermissionSetMapper;
    private final DocumentIndex documentIndex;
    private final DocumentCache documentCache;
    private final Clock clock;
    private final ImcmsServices services;
    public static final String SQL_GET_ALL_CATEGORIES_OF_TYPE = "SELECT categories.category_id, categories.name, categories.description, categories.image\n"
                      + "FROM categories\n"
                      + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                      + "WHERE categories.category_type_id = ?\n"
                      + "ORDER BY categories.name";
    public static final String SQL_GET_CATEGORY = "SELECT categories.category_id, categories.name, categories.description, categories.image\n"
                      + "FROM categories\n"
                      + "JOIN category_types\n"
                      + "ON categories.category_type_id = category_types.category_type_id\n"
                      + "WHERE category_types.name = ?\n"
                      + "AND categories.name = ?";
    public static final String SQL_GET_DOCUMENT = "SELECT meta_id,\n"
    + "doc_type,\n"
    + "meta_headline,\n"
    + "meta_text,\n"
    + "meta_image,\n"
    + "owner_id,\n"
    + "permissions,\n"
    + "shared,\n"
    + "show_meta,\n"
    + "lang_prefix,\n"
    + "date_created,\n"
    + "date_modified,\n"
    + "disable_search,\n"
    + "target,\n"
    + "archived_datetime,\n"
    + "publisher_id,\n"
    + "status,\n"
    + "publication_start_datetime,\n"
    + "publication_end_datetime\n"
    + "FROM meta\n"
    + "WHERE meta_id = ?";
    public static final String SQL_GET_SECTIONS_FOR_DOCUMENT = "SELECT s.section_id, s.section_name\n"
                                                         + " FROM sections s, meta_section ms, meta m\n"
                                                         + "where m.meta_id=ms.meta_id\n"
                                                         + "and m.meta_id=?\n"
                                                         + "and ms.section_id=s.section_id";

    public DocumentMapper( ImcmsServices services, Database database,
                           ImcmsAuthenticatorAndUserAndRoleMapper userRegistry,
                           DocumentPermissionSetMapper documentPermissionSetMapper, DocumentIndex documentIndex,
                           Clock clock, Config config ) {
        this.database = database;
        this.clock = clock;
        this.services = services;
        this.userAndRoleMapper = userRegistry;
        this.documentPermissionSetMapper = documentPermissionSetMapper;
        this.documentIndex = documentIndex;
        int documentCacheMaxSize = config.getDocumentCacheMaxSize() ;
        documentCache = new DocumentCache( new LRUMap( documentCacheMaxSize ), this );
    }

    public DocumentDomainObject createDocumentOfTypeFromParent( int documentTypeId, final DocumentDomainObject parent,
                                                                UserDomainObject user ) {
        if ( !user.canCreateDocumentOfTypeIdFromParent( documentTypeId, parent ) ) {
            throw new SecurityException( "User can't create documents from document " + parent.getId() );
        }
        DocumentDomainObject newDocument;
        try {
            if ( DocumentTypeDomainObject.TEXT_ID == documentTypeId ) {
                newDocument = (DocumentDomainObject)parent.clone();
                TextDocumentDomainObject newTextDocument = (TextDocumentDomainObject)newDocument;
                newTextDocument.removeAllTexts();
                newTextDocument.removeAllImages();
                newTextDocument.removeAllIncludes();
                newTextDocument.removeAllMenus();
                int permissionSetId = user.getPermissionSetIdFor( parent );
                TemplateDomainObject template = null;
                if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 == permissionSetId ) {
                    template = ( (TextDocumentPermissionSetDomainObject)newTextDocument.getPermissionSetForRestrictedOneForNewDocuments() ).getDefaultTemplate();
                } else if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2 == permissionSetId ) {
                    template = ( (TextDocumentPermissionSetDomainObject)newTextDocument.getPermissionSetForRestrictedTwoForNewDocuments() ).getDefaultTemplate();
                } else if ( parent instanceof TextDocumentDomainObject ) {
                    template = ( (TextDocumentDomainObject)parent ).getDefaultTemplate();
                }
                if ( null != template ) {
                    newTextDocument.setTemplate( template );
                }
            } else {
                newDocument = DocumentDomainObject.fromDocumentTypeId( documentTypeId );
                newDocument.setAttributes( (DocumentDomainObject.Attributes)parent.getAttributes().clone() );
            }
        } catch ( CloneNotSupportedException e ) {
            throw new UnhandledException( e );
        }
        newDocument.setId( 0 );
        newDocument.setHeadline( "" );
        newDocument.setMenuText( "" );
        newDocument.setMenuImage( "" );
        makeDocumentLookNew( newDocument, user );
        return newDocument;
    }

    private void makeDocumentLookNew( DocumentDomainObject document, UserDomainObject user ) {
        Date now = new Date();
        makeDocumentLookCreated( document, user, now );
        document.setPublicationStartDatetime( now );
        document.setArchivedDatetime( null );
        document.setPublicationEndDatetime( null );
        document.setStatus( DocumentDomainObject.STATUS_NEW );
    }

    private void makeDocumentLookCreated( DocumentDomainObject document, UserDomainObject user, Date now ) {
        document.setCreator( user );
        document.setCreatedDatetime( now );
        document.setModifiedDatetime( now );
    }

    public CategoryDomainObject[] getAllCategoriesOfType( CategoryTypeDomainObject categoryType ) {
        String sqlQuery = SQL_GET_ALL_CATEGORIES_OF_TYPE;
        String[][] sqlResult = database.sqlQueryMulti( sqlQuery, new String[]{"" + categoryType.getId()} );
        CategoryDomainObject[] categoryDomainObjects = new CategoryDomainObject[sqlResult.length];
        for ( int i = 0; i < sqlResult.length; i++ ) {
            int categoryId = Integer.parseInt( sqlResult[i][0] );
            String categoryName = sqlResult[i][1];
            String categoryDescription = sqlResult[i][2];
            String categoryImage = sqlResult[i][3];

            categoryDomainObjects[i] =
            new CategoryDomainObject( categoryId, categoryName, categoryDescription, categoryImage, categoryType );
        }
        return categoryDomainObjects;
    }

    public boolean isUniqueCategoryTypeName( String categoryTypeName ) {
        CategoryTypeDomainObject[] categoryTypes = getAllCategoryTypes();
        for ( int i = 0; i < categoryTypes.length; i++ ) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];
            if ( categoryType.getName().equalsIgnoreCase( categoryTypeName ) ) {
                return false;
            }
        }
        return true;
    }

    public CategoryTypeDomainObject[] getAllCategoryTypes() {
        String sqlQuery = "SELECT category_type_id, name, max_choices FROM category_types ORDER BY name";
        String[][] sqlResult = database.sqlQueryMulti( sqlQuery, new String[0] );

        CategoryTypeDomainObject[] categoryTypeDomainObjects = new CategoryTypeDomainObject[sqlResult.length];
        for ( int i = 0; i < categoryTypeDomainObjects.length; i++ ) {
            int categoryTypeId = Integer.parseInt( sqlResult[i][0] );
            String typeName = sqlResult[i][1];
            int maxChoices = Integer.parseInt( sqlResult[i][2] );

            categoryTypeDomainObjects[i] = new CategoryTypeDomainObject( categoryTypeId, typeName, maxChoices );
        }

        return categoryTypeDomainObjects;
    }

    public SectionDomainObject[] getAllSections() {
        String[][] sqlRows = database.sqlQueryMulti( SQL_GET_ALL_SECTIONS, new String[0] );
        SectionDomainObject[] allSections = new SectionDomainObject[sqlRows.length];
        for ( int i = 0; i < sqlRows.length; i++ ) {
            int sectionId = Integer.parseInt( sqlRows[i][0] );
            String sectionName = sqlRows[i][1];
            allSections[i] = new SectionDomainObject( sectionId, sectionName );
        }
        Arrays.sort( allSections, new SectionNameComparator() );
        return allSections;
    }

    public CategoryDomainObject getCategory( CategoryTypeDomainObject categoryType, String categoryName ) {
        String sqlQuery = SQL_GET_CATEGORY;
        String[] sqlResult = database.sqlQuery( sqlQuery, new String[]{categoryType.getName(), categoryName} );
        if ( 0 != sqlResult.length ) {
            final int categoryId = Integer.parseInt( sqlResult[0] );
            final String categoryNameFromDb = sqlResult[1];
            final String categoryDescription = sqlResult[2];
            final String categoryImge = sqlResult[3];

            return new CategoryDomainObject( categoryId, categoryNameFromDb, categoryDescription, categoryImge,
                                             categoryType );
        } else {
            return null;
        }
    }

    public CategoryDomainObject getCategoryById( int categoryId ) {
        String sqlQuery = "SELECT categories.name, categories.description, categories.image, category_types.category_type_id, category_types.name, category_types.max_choices\n"
                          + "FROM categories\n"
                          + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                          + "WHERE categories.category_id = ?";

        String[] categorySqlResult = database.sqlQuery( sqlQuery, new String[]{"" + categoryId} );

        if ( 0 != categorySqlResult.length ) {
            String categoryName = categorySqlResult[0];
            String categoryDescription = categorySqlResult[1];
            String categoryImage = categorySqlResult[2];
            int categoryTypeId = Integer.parseInt( categorySqlResult[3] );
            String categoryTypeName = categorySqlResult[4];
            int categoryTypeMaxChoices = Integer.parseInt( categorySqlResult[5] );

            CategoryTypeDomainObject categoryType = new CategoryTypeDomainObject( categoryTypeId, categoryTypeName,
                                                                                  categoryTypeMaxChoices );

            return new CategoryDomainObject( categoryId, categoryName, categoryDescription, categoryImage, categoryType );
        } else {
            return null;
        }
    }

    public CategoryTypeDomainObject getCategoryType( String categoryTypeName ) {
        String sqlStr = "SELECT category_types.category_type_id, category_types.name, category_types.max_choices\n"
                        + "FROM category_types\n"
                        + "WHERE category_types.name = ?";
        String[] sqlResult = database.sqlQuery( sqlStr, new String[]{categoryTypeName} );

        if ( null == sqlResult || 0 == sqlResult.length ) {
            return null;
        } else {
            int categoryTypeId = Integer.parseInt( sqlResult[0] );
            String categoryTypeNameFromDb = sqlResult[1];
            int categoryTypeMaxChoices = Integer.parseInt( sqlResult[2] );
            return new CategoryTypeDomainObject( categoryTypeId, categoryTypeNameFromDb, categoryTypeMaxChoices );
        }
    }

    public CategoryTypeDomainObject getCategoryTypeById( int categoryTypeId ) {
        String sqlStr = "select name, max_choices  from category_types where category_type_id = ? ";
        String[] sqlResult = database.sqlQuery( sqlStr, new String[]{"" + categoryTypeId} );

        if ( null == sqlResult || 0 == sqlResult.length ) {
            return null;
        } else {
            String categoryTypeNameFromDb = sqlResult[0];
            int categoryTypeMaxChoices = Integer.parseInt( sqlResult[1] );
            return new CategoryTypeDomainObject( categoryTypeId, categoryTypeNameFromDb, categoryTypeMaxChoices );
        }
    }

    public void deleteCategoryTypeFromDb( CategoryTypeDomainObject categoryType ) {
        String sqlstr = "delete from category_types where category_type_id = ?";
        database.sqlUpdateQuery( sqlstr, new String[]{categoryType.getId() + ""} );
    }

    public CategoryTypeDomainObject addCategoryTypeToDb( String name, int max_choices ) {
        String sqlstr = "insert into category_types (name, max_choices) values(?,?) SELECT @@IDENTITY";
        String newId = database.sqlQueryStr( sqlstr, new String[]{name, max_choices + ""} );
        return getCategoryTypeById( Integer.parseInt( newId ) );
    }

    public void updateCategoryType( CategoryTypeDomainObject categoryType ) {
        String sqlstr = "update category_types set name= ?, max_choices= ?  where category_type_id = ? ";
        database.sqlUpdateQuery( sqlstr, new String[]{
            categoryType.getName(), categoryType.getMaxChoices() + "", categoryType.getId() + ""
        } );
    }

    public CategoryDomainObject addCategory( CategoryDomainObject category ) throws CategoryAlreadyExistsException {
        String sqlstr = "insert into categories  (category_type_id, name, description, image) values(?,?,?,?) SELECT @@IDENTITY";
        String newId = database.sqlQueryStr( sqlstr, new String[]{
            category.getType().getId() + "", category.getName(), category.getDescription(), category.getImageUrl()
        } );
        int categoryId = Integer.parseInt( newId );
        category.setId(categoryId);
        return getCategoryById( categoryId ) ;
    }

    public void updateCategory( CategoryDomainObject category ) {
        String sqlstr = "update categories set category_type_id = ?, name= ?, description = ?, image = ?  where category_id = ? ";
        database.sqlUpdateQuery( sqlstr, new String[]{
            category.getType().getId() + "", category.getName(), category.getDescription(), category.getImageUrl(),
            category.getId() + ""
        } );
    }

    public void deleteCategoryFromDb( CategoryDomainObject category ) {
        String sqlstr = "delete from categories where category_id = ?";
        database.sqlUpdateQuery( sqlstr, new String[]{category.getId() + ""} );
    }

    public DocumentDomainObject getDocument( int metaId ) {
        NDC.push( "getDocument" );

        DocumentDomainObject document;
        try {
            document = (DocumentDomainObject)documentCache.get( new Integer( metaId ) );
            if ( null != document ) {
                document = (DocumentDomainObject)document.clone();
            }
        } catch ( CloneNotSupportedException e ) {
            throw new UnhandledException( e );
        }

        NDC.pop();
        return document;
    }

    public DocumentReference getDocumentReference( DocumentDomainObject document ) {
        return getDocumentReference( document.getId() );
    }

    DocumentReference getDocumentReference( int childId ) {
        return new DocumentReference( childId, this );
    }

    private DocumentDomainObject getDocumentFromDb( int metaId ) {
        NDC.push( "getDocumentFromDb" );
        log.debug( "Getting document " + metaId + " from db." );

        String[] result = sprocGetDocumentInfo( metaId );

        DocumentDomainObject document = null;
        if ( 0 != result.length ) {
            document = getDocumentFromSqlResultRow( result );
            initDocumentAttributes( document );
            initDocumentCategories( document );
            initRolesMappedToDocumentPermissionSetIds( document );

            document.accept( new DocumentInitializingVisitor( services, database ) );
        }
        NDC.pop();
        return document;
    }

    public void initDocumentAttributes( DocumentDomainObject document ) {

        document.setSections( getSections( document.getId() ) );

        document.setKeywords( getKeywords( document.getId() ) );

        document.setPermissionSetForRestrictedOne( documentPermissionSetMapper.getPermissionSetRestrictedOne( document ) );
        document.setPermissionSetForRestrictedTwo( documentPermissionSetMapper.getPermissionSetRestrictedTwo( document ) );

        document.setPermissionSetForRestrictedOneForNewDocuments( documentPermissionSetMapper.getPermissionSetRestrictedOneForNewDocuments( document ) );
        document.setPermissionSetForRestrictedTwoForNewDocuments( documentPermissionSetMapper.getPermissionSetRestrictedTwoForNewDocuments( document ) );

    }

    public void initDocumentCategories( DocumentDomainObject document ) {

        addCategoriesFromDatabaseToDocument( document );

    }

    public void initRolesMappedToDocumentPermissionSetIds( DocumentDomainObject document ) {

        String[][] sprocResult = database.sqlQueryMulti( "SELECT "
                                                         + ImcmsAuthenticatorAndUserAndRoleMapper.SQL_ROLES_COLUMNS
                                                         + ", rr.set_id\n"
                                                         + "FROM  roles, roles_rights AS rr\n"
                                                         + "WHERE rr.role_id = roles.role_id AND rr.meta_id = ?",
                                                         new String[]{"" + document.getId()} );

        for ( int i = 0; i < sprocResult.length; ++i ) {
            RoleDomainObject role = userAndRoleMapper.getRoleFromSqlResult( sprocResult[i] );

            int rolePermissionSetId = Integer.parseInt( sprocResult[i][4] );
            document.setPermissionSetIdForRole( role, rolePermissionSetId );
        }

    }

    public SectionDomainObject getSectionById( int sectionId ) {
        String sectionName = database.sqlQueryStr( "SELECT section_name FROM sections WHERE section_id = ?",
                                                   new String[]{"" + sectionId} );
        if ( null == sectionName ) {
            return null;
        }
        return new SectionDomainObject( sectionId, sectionName );
    }

    public SectionDomainObject getSectionByName( String name ) {
        String[] sectionSqlRow = database.sqlQuery( "SELECT section_id, section_name FROM sections WHERE section_name = ?",
                                                    new String[]{name} );
        if ( 0 == sectionSqlRow.length ) {
            return null;
        }
        int sectionId = Integer.parseInt( sectionSqlRow[0] );
        String sectionName = sectionSqlRow[1];
        return new SectionDomainObject( sectionId, sectionName );
    }

    /**
     * @return the sections for a document, empty array if there is none.
     */
    private SectionDomainObject[] getSections( int meta_id ) {
        String[][] sectionData = database.sqlQueryMulti( SQL_GET_SECTIONS_FOR_DOCUMENT,
                                                             new String[]{String.valueOf( meta_id )} );

        SectionDomainObject[] sections = new SectionDomainObject[sectionData.length];

        for ( int i = 0; i < sectionData.length; i++ ) {
            int sectionId = Integer.parseInt( sectionData[i][0] );
            String sectionName = sectionData[i][1];
            sections[i] = new SectionDomainObject( sectionId, sectionName );
        }
        return sections;
    }

    public TextDomainObject getText( int metaId, int no ) {
        String[] results = sprocGetText( metaId, no );

        if ( results == null || results.length == 0 ) {
            /* There was no text. Return null. */
            return null;
        }

        /* Return the text */
        String text = results[0];
        int type = Integer.parseInt( results[1] );

        return new TextDomainObject( text, type );

    }

    public void removeInclusion( int includingMetaId, int includeIndex ) {
        deleteInclude( includingMetaId, includeIndex );
    }

    public void saveNewDocument( DocumentDomainObject document, UserDomainObject user )
            throws MaxCategoryDomainObjectsOfTypeExceededException {

        if ( !user.canEdit( document ) ) {
            return; // TODO: More specific check needed. Throw exception ?
        }

        checkMaxDocumentCategoriesOfType( document );

        makeDocumentLookCreated( document, user, new Date() );

        int newMetaId = sqlInsertIntoMeta( document );

        if ( !user.isSuperAdminOrHasFullPermissionOn( document ) ) {
            document.setPermissionSetForRestrictedOne( document.getPermissionSetForRestrictedOneForNewDocuments() );
            document.setPermissionSetForRestrictedTwo( document.getPermissionSetForRestrictedTwoForNewDocuments() );
        }

        document.setId( newMetaId );

        updateDocumentSectionsCategoriesKeywords( document );

        updateDocumentRolePermissions( document, user, null );

        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets( document, user, null );

        document.accept( new DocumentCreatingVisitor( user, database ) );

        invalidateDocument( document );
    }

    private void updateDocumentSectionsCategoriesKeywords( DocumentDomainObject document ) {
        updateDocumentSections( document.getId(), document.getSections() );

        updateDocumentCategories( document );

        updateDocumentKeywords( document.getId(), document.getKeywords() );
    }

    private int sqlInsertIntoMeta( DocumentDomainObject document ) {
        String[] metaColumnNames = {
            "doc_type", "meta_headline", "meta_text", "meta_image",
            "owner_id", "permissions", "shared", "show_meta",
            "lang_prefix", "date_created", "date_modified", "disable_search",
            "target", "activate", "archived_datetime", "publisher_id",
            "status", "publication_start_datetime", "publication_end_datetime"
        };

        String sqlPlaceHolders = "?" + StringUtils.repeat( ",?", metaColumnNames.length - 1 );
        String sqlStr = "INSERT INTO meta (" + StringUtils.join( metaColumnNames, "," ) + ") VALUES ("
                        + sqlPlaceHolders
                        + ") SELECT @@IDENTITY";
        List sqlColumnValues = new ArrayList();
        sqlColumnValues.add( document.getDocumentTypeId() + "" );
        sqlColumnValues.add( document.getHeadline() );
        sqlColumnValues.add( document.getMenuText() );
        sqlColumnValues.add( document.getMenuImage() );
        sqlColumnValues.add( document.getCreator().getId() + "" );
        sqlColumnValues.add( makeSqlStringFromBoolean( document.isRestrictedOneMorePrivilegedThanRestrictedTwo() ) );
        sqlColumnValues.add( makeSqlStringFromBoolean( document.isLinkableByOtherUsers() ) );
        sqlColumnValues.add( makeSqlStringFromBoolean( document.isVisibleInMenusForUnauthorizedUsers() ) );
        sqlColumnValues.add( document.getLanguageIso639_2() );
        sqlColumnValues.add( makeSqlStringFromDate( document.getCreatedDatetime() ) );
        sqlColumnValues.add( makeSqlStringFromDate( document.getModifiedDatetime() ) );
        sqlColumnValues.add( makeSqlStringFromBoolean( document.isSearchDisabled() ) );
        sqlColumnValues.add( document.getTarget() );
        sqlColumnValues.add( "1" );
        sqlColumnValues.add( makeSqlStringFromDate( document.getArchivedDatetime() ) );
        sqlColumnValues.add( null != document.getPublisher() ? document.getPublisher().getId() + "" : null );
        sqlColumnValues.add( "" + document.getStatus() );
        sqlColumnValues.add( makeSqlStringFromDate( document.getPublicationStartDatetime() ) );
        sqlColumnValues.add( makeSqlStringFromDate( document.getPublicationEndDatetime() ) );

        String metaIdStr = database.sqlQueryStr( sqlStr, (String[])sqlColumnValues.toArray( new String[sqlColumnValues.size()] ) );
        final int metaId = Integer.parseInt( metaIdStr );
        return metaId;
    }

    private String makeSqlStringFromBoolean( final boolean bool ) {
        return bool ? "1" : "0";
    }

    public void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws MaxCategoryDomainObjectsOfTypeExceededException {

        DocumentDomainObject oldDocument = getDocument( document.getId() );

        if ( !user.canEdit( oldDocument ) ) {
            return;
        }

        checkMaxDocumentCategoriesOfType( document );

        try {
            Date lastModifiedDatetime = Utility.truncateDateToMinutePrecision( document.getLastModifiedDatetime() );
            Date modifiedDatetime = Utility.truncateDateToMinutePrecision( document.getModifiedDatetime() );
            boolean modifiedDatetimeUnchanged = lastModifiedDatetime.equals( modifiedDatetime );
            if ( modifiedDatetimeUnchanged ) {
                document.setModifiedDatetime( this.clock.getCurrentDate() );
            }

            sqlUpdateMeta( document );

            updateDocumentSectionsCategoriesKeywords( document );

            if ( user.canEditPermissionsFor( oldDocument ) ) {
                updateDocumentRolePermissions( document, user, oldDocument );

                documentPermissionSetMapper.saveRestrictedDocumentPermissionSets( document, user, oldDocument );
            }

            document.accept( new DocumentSavingVisitor( user, oldDocument, database ) );
        } finally {
            invalidateDocument( document );
        }
    }

    public void invalidateDocument( DocumentDomainObject document ) {
        documentIndex.indexDocument( document );
        documentCache.remove( new Integer( document.getId() ) );
    }

    void updateDocumentRolePermissions( DocumentDomainObject document, UserDomainObject user,
                                        DocumentDomainObject oldDocument ) {
        Map rolesMappedtoPermissionSetIds = new HashMap();
        if ( null != oldDocument ) {
            Set rolesMappedToPermissionsForOldDocument = oldDocument.getRolesMappedToPermissionSetIds().keySet();
            for ( Iterator iterator = rolesMappedToPermissionsForOldDocument.iterator(); iterator.hasNext(); ) {
                RoleDomainObject role = (RoleDomainObject)iterator.next();
                rolesMappedtoPermissionSetIds.put( role, new Integer( DocumentPermissionSetDomainObject.TYPE_ID__NONE ) );
            }
        }
        rolesMappedtoPermissionSetIds.putAll( document.getRolesMappedToPermissionSetIds() );
        for ( Iterator it = rolesMappedtoPermissionSetIds.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry rolePermissionTuple = (Map.Entry)it.next();
            RoleDomainObject role = (RoleDomainObject)rolePermissionTuple.getKey();
            int permissionSetId = ( (Integer)rolePermissionTuple.getValue() ).intValue();

            if ( null == oldDocument
                 || user.canSetPermissionSetIdForRoleOnDocument( permissionSetId, role, oldDocument ) ) {
                database.sqlUpdateProcedure( SPROC_SET_PERMISSION_SET_ID_FOR_ROLE_ON_DOCUMENT, new String[]{
                    "" + role.getId(), "" + document.getId(), "" + permissionSetId} );
            }
        }
    }

    private void checkMaxDocumentCategoriesOfType( DocumentDomainObject document )
            throws MaxCategoryDomainObjectsOfTypeExceededException {
        CategoryTypeDomainObject[] categoryTypes = getAllCategoryTypes();
        for ( int i = 0; i < categoryTypes.length; i++ ) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];
            int maxChoices = categoryType.getMaxChoices();
            CategoryDomainObject[] documentCategoriesOfType = document.getCategoriesOfType( categoryType );
            if ( UNLIMITED_MAX_CATEGORY_CHOICES != maxChoices && documentCategoriesOfType.length > maxChoices ) {
                throw new MaxCategoryDomainObjectsOfTypeExceededException( "Document may have at most " + maxChoices
                                                                           + " categories of type '"
                                                                           + categoryType.getName()
                                                                           + "'" );
            }
        }
    }

    private void updateDocumentCategories( DocumentDomainObject document ) {
        removeAllCategoriesFromDocument( document );
        CategoryDomainObject[] categories = document.getCategories();
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            addCategoryToDocument( category, document );
        }
    }

    private void addCategoryToDocument( CategoryDomainObject category, DocumentDomainObject document ) {
        int categoryId = category.getId();
        database.sqlUpdateQuery( "INSERT INTO document_categories (meta_id, category_id) VALUES(?,?)",
                                 new String[]{"" + document.getId(), "" + categoryId} );
    }

    public String[] getAllDocumentsOfOneCategory( CategoryDomainObject category ) {

        String sqlstr = "select meta_id from document_categories where category_id = ? ";
        String[] res = database.sqlQuery( sqlstr, new String[]{category.getId() + ""} );

        return res;
    }

    private void removeAllCategoriesFromDocument( DocumentDomainObject document ) {
        database.sqlUpdateQuery( "DELETE FROM document_categories WHERE meta_id = ?",
                                 new String[]{"" + document.getId()} );
    }

    public void deleteOneCategoryFromDocument( DocumentDomainObject document, CategoryDomainObject category ) {
        database.sqlUpdateQuery( "DELETE FROM document_categories WHERE meta_id = ? and category_id = ?",
                                 new String[]{document.getId() + "", category.getId() + ""} );
    }

    private void sqlUpdateMeta( DocumentDomainObject document ) {
        String headline = document.getHeadline();
        String text = document.getMenuText();

        StringBuffer sqlStr = new StringBuffer( "update meta set " );

        ArrayList sqlUpdateColumns = new ArrayList();
        ArrayList sqlUpdateValues = new ArrayList();

        makeDateSqlUpdateClause( "publication_start_datetime", document.getPublicationStartDatetime(), sqlUpdateColumns, sqlUpdateValues );
        makeDateSqlUpdateClause( "publication_end_datetime", document.getPublicationEndDatetime(), sqlUpdateColumns, sqlUpdateValues );
        makeDateSqlUpdateClause( "archived_datetime", document.getArchivedDatetime(), sqlUpdateColumns, sqlUpdateValues );
        makeDateSqlUpdateClause( "date_created", document.getCreatedDatetime(), sqlUpdateColumns, sqlUpdateValues );
        String headlineThatFitsInDB = headline.substring( 0,
                                                          Math.min( headline.length(), META_HEADLINE_MAX_LENGTH - 1 ) );
        makeStringSqlUpdateClause( "meta_headline", headlineThatFitsInDB, sqlUpdateColumns, sqlUpdateValues );
        makeStringSqlUpdateClause( "meta_image", document.getMenuImage(), sqlUpdateColumns, sqlUpdateValues );
        makeDateSqlUpdateClause( "date_modified", document.getModifiedDatetime(), sqlUpdateColumns, sqlUpdateValues );
        makeStringSqlUpdateClause( "target", document.getTarget(), sqlUpdateColumns, sqlUpdateValues );
        String textThatFitsInDB = text.substring( 0, Math.min( text.length(), META_TEXT_MAX_LENGTH - 1 ) );
        makeStringSqlUpdateClause( "meta_text", textThatFitsInDB, sqlUpdateColumns, sqlUpdateValues );
        makeStringSqlUpdateClause( "lang_prefix", document.getLanguageIso639_2(), sqlUpdateColumns, sqlUpdateValues );
        makeBooleanSqlUpdateClause( "disable_search", document.isSearchDisabled(), sqlUpdateColumns, sqlUpdateValues );
        makeBooleanSqlUpdateClause( "shared", document.isLinkableByOtherUsers(), sqlUpdateColumns, sqlUpdateValues );
        makeBooleanSqlUpdateClause( "show_meta", document.isVisibleInMenusForUnauthorizedUsers(), sqlUpdateColumns, sqlUpdateValues );
        makeBooleanSqlUpdateClause( "permissions", document.isRestrictedOneMorePrivilegedThanRestrictedTwo(), sqlUpdateColumns, sqlUpdateValues );
        UserDomainObject publisher = document.getPublisher();
        makeIntSqlUpdateClause( "publisher_id", publisher == null ? null : new Integer( publisher.getId() ), sqlUpdateColumns,
                                sqlUpdateValues );
        UserDomainObject creator = document.getCreator();
        if ( null != creator ) {
            makeIntSqlUpdateClause( "owner_id", new Integer( creator.getId() ), sqlUpdateColumns,
                                    sqlUpdateValues );
        }
        makeIntSqlUpdateClause( "status", new Integer( document.getStatus() ), sqlUpdateColumns, sqlUpdateValues );

        sqlStr.append( StringUtils.join( sqlUpdateColumns.iterator(), "," ) );
        sqlStr.append( " where meta_id = ?" );
        sqlUpdateValues.add( "" + document.getId() );
        database.sqlUpdateQuery( sqlStr.toString(),
                                 (String[])sqlUpdateValues.toArray( new String[sqlUpdateValues.size()] ) );
    }

    public void setInclude( int includingMetaId, int includeIndex, int includedMetaId ) {
        database.sqlUpdateProcedure( "SetInclude",
                                     new String[]{"" + includingMetaId, "" + includeIndex, "" + includedMetaId} );
    }

    public void deleteInclude( int including_meta_id, int include_id ) {
        database.sqlUpdateProcedure( "DeleteInclude", new String[]{"" + including_meta_id, "" + include_id} );
    }

    private void updateDocumentKeywords( int meta_id, String[] keywords ) {
        Set allKeywords = new HashSet( Arrays.asList( getAllKeywords() ) );
        deleteKeywordsFromDocument( meta_id );
        for ( int i = 0; i < keywords.length; i++ ) {
            String keyword = keywords[i];
            final boolean keywordExists = allKeywords.contains( keyword );
            if ( !keywordExists ) {
                addKeyword( keyword );
            }
            addExistingKeywordToDocument( meta_id, keyword );
        }
        deleteUnusedKeywords();
    }

    private void addExistingKeywordToDocument( int meta_id, String keyword ) {
        int keywordId = Integer.parseInt( database.sqlQueryStr( "SELECT class_id FROM classification WHERE code = ?", new String[]{
            keyword
        } ) );
        database.sqlUpdateQuery( "INSERT INTO meta_classification (meta_id, class_id) VALUES(?,?)",
                                 new String[]{"" + meta_id, "" + keywordId} );
    }

    private void deleteUnusedKeywords() {
        database.sqlUpdateQuery( "DELETE FROM classification WHERE class_id NOT IN (SELECT class_id FROM meta_classification)",
                                 new String[0] );
    }

    private void addKeyword( String keyword ) {
        database.sqlUpdateQuery( "INSERT INTO classification VALUES(?)", new String[]{keyword} );
    }

    private String[] getAllKeywords() {
        return database.sqlQuery( "SELECT code FROM classification", new String[0] );
    }

    private void deleteKeywordsFromDocument( int meta_id ) {
        String sqlDeleteKeywordsFromDocument = "DELETE FROM meta_classification WHERE meta_id = ?";
        database.sqlUpdateQuery( sqlDeleteKeywordsFromDocument, new String[]{"" + meta_id} );
    }

    private void addCategoriesFromDatabaseToDocument( DocumentDomainObject document ) {
        String[][] categories = database.sqlQueryMulti( "SELECT categories.category_id, categories.name, categories.image, categories.description, category_types.category_type_id, category_types.name, category_types.max_choices"
                                                        + " FROM document_categories"
                                                        + " JOIN categories"
                                                        + "  ON document_categories.category_id = categories.category_id"
                                                        + " JOIN category_types"
                                                        + "  ON categories.category_type_id = category_types.category_type_id"
                                                        + " WHERE document_categories.meta_id = ?",
                                                        new String[]{"" + document.getId()} );
        for ( int i = 0; i < categories.length; i++ ) {
            String[] categoryArray = categories[i];

            int categoryId = Integer.parseInt( categoryArray[0] );
            String categoryName = categoryArray[1];
            String categoryImage = categoryArray[2];
            String categoryDescription = categoryArray[3];
            int categoryTypeId = Integer.parseInt( categoryArray[4] );
            String categoryTypeName = categoryArray[5];
            int categoryTypeMaxChoices = Integer.parseInt( categoryArray[6] );

            CategoryTypeDomainObject categoryType = new CategoryTypeDomainObject( categoryTypeId, categoryTypeName,
                                                                                  categoryTypeMaxChoices );
            CategoryDomainObject category = new CategoryDomainObject( categoryId, categoryName, categoryDescription,
                                                                      categoryImage, categoryType );
            document.addCategory( category );
        }

    }

    private void addSectionToDocument( int metaId, int sectionId ) {
        database.sqlUpdateQuery( "INSERT INTO meta_section VALUES(?,?)", new String[]{"" + metaId, "" + sectionId} );
    }

    private static void makeBooleanSqlUpdateClause( String columnName, boolean bool, List sqlUpdateColumns,
                                                    List sqlUpdateValues ) {
        sqlUpdateColumns.add( columnName + " = ?" );
        sqlUpdateValues.add( bool ? "1" : "0" );
    }

    private static void makeDateSqlUpdateClause( String columnName, Date date, List sqlUpdateColumns,
                                                 List sqlUpdateValues ) {
        makeStringSqlUpdateClause( columnName, makeSqlStringFromDate( date ), sqlUpdateColumns, sqlUpdateValues );
    }

    private static String makeSqlStringFromDate( Date date ) {
        if ( null == date ) {
            return null;
        }
        return new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING ).format( date );
    }

    private static void makeIntSqlUpdateClause( String columnName, Integer integer, ArrayList sqlUpdateColumns,
                                                ArrayList sqlUpdateValues ) {
        if ( null != integer ) {
            sqlUpdateColumns.add( columnName + " = ?" );
            sqlUpdateValues.add( "" + integer );
        } else {
            sqlUpdateColumns.add( columnName + " = NULL" );
        }
    }

    private static void makeStringSqlUpdateClause( String columnName, String value, List sqlUpdateColumns,
                                                   List sqlUpdateValues ) {
        if ( null != value ) {
            sqlUpdateColumns.add( columnName + " = ?" );
            sqlUpdateValues.add( value );
        } else {
            sqlUpdateColumns.add( columnName + " = NULL" );
        }
    }

    private void removeAllSectionsFromDocument( int metaId ) {
        database.sqlUpdateQuery( "DELETE FROM meta_section WHERE meta_id = ?", new String[]{"" + metaId} );
    }

    private void updateDocumentSections( int metaId,
                                         SectionDomainObject[] sections ) {
        removeAllSectionsFromDocument( metaId );
        for ( int i = 0; null != sections && i < sections.length; i++ ) {
            SectionDomainObject section = sections[i];
            addSectionToDocument( metaId, section.getId() );
        }
    }

    private String[] sprocGetDocumentInfo( int metaId ) {
        return database.sqlQuery( SQL_GET_DOCUMENT, new String[]{String.valueOf( metaId )} );
    }

    private DocumentDomainObject getDocumentFromSqlResultRow( String[] result ) {
        final int documentTypeId = Integer.parseInt( result[1] );
        DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId( documentTypeId );

        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = userAndRoleMapper;
        document.setId( Integer.parseInt( result[0] ) );
        document.setHeadline( result[2] );
        document.setMenuText( result[3] );
        document.setMenuImage( result[4] );
        UserDomainObject creator = imcmsAuthenticatorAndUserAndRoleMapper.getUser( Integer.parseInt( result[5] ) );
        document.setCreator( creator );
        document.setRestrictedOneMorePrivilegedThanRestrictedTwo( getBooleanFromSqlResultString( result[6] ) );
        document.setLinkableByOtherUsers( getBooleanFromSqlResultString( result[7] ) );
        document.setVisibleInMenusForUnauthorizedUsers( getBooleanFromSqlResultString( result[8] ) );
        document.setLanguageIso639_2( LanguageMapper.getAsIso639_2OrDefaultLanguage( result[9], services.getDefaultLanguage() ) );
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING );
        document.setCreatedDatetime( parseDateFormat( dateFormat, result[10] ) );
        Date modifiedDatetime = parseDateFormat( dateFormat, result[11] );
        document.setModifiedDatetime( modifiedDatetime );
        document.setLastModifiedDatetime( modifiedDatetime );
        document.setSearchDisabled( getBooleanFromSqlResultString( result[12] ) );
        document.setTarget( result[13] );
        document.setArchivedDatetime( parseDateFormat( dateFormat, result[14] ) );
        String publisherIdStr = result[15];
        if ( null != publisherIdStr ) {
            UserDomainObject publisher = imcmsAuthenticatorAndUserAndRoleMapper.getUser( Integer.parseInt( publisherIdStr ) );
            document.setPublisher( publisher );
        }
        document.setStatus( Integer.parseInt( result[16] ) );
        document.setPublicationStartDatetime( parseDateFormat( dateFormat, result[17] ) );
        document.setPublicationEndDatetime( parseDateFormat( dateFormat, result[18] ) );
        return document;
    }

    private boolean getBooleanFromSqlResultString( final String columnValue ) {
        return !"0".equals( columnValue );
    }

    private Date parseDateFormat( DateFormat dateFormat, String dateString ) {
        try {
            return dateFormat.parse( dateString );
        } catch ( NullPointerException npe ) {
            return null;
        } catch ( ParseException pe ) {
            return null;
        }
    }

    private String[] sprocGetText( int meta_id, int no ) {
        String[] params = new String[]{"" + meta_id, "" + no};
        String[] results = database.sqlProcedure( SPROC_GET_TEXT, params );
        return results;
    }

    private String[] getKeywords( int meta_id ) {
        String sqlStr;
        sqlStr =
        "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = ?";
        String[] keywords = database.sqlQuery( sqlStr, new String[]{"" + meta_id} );
        return keywords;
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public String[][] getParentDocumentAndMenuIdsForDocument( DocumentDomainObject document ) {
        String sqlStr = "SELECT meta_id,menu_index FROM childs, menus WHERE menus.menu_id = childs.menu_id AND to_meta_id = ?";
        return database.sqlQueryMulti( sqlStr, new String[]{"" + document.getId()} );
    }

    public String[][] getAllMimeTypesWithDescriptions( UserDomainObject user ) {
        String sqlStr = "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id";
        String[][] mimeTypes = database.sqlQueryMulti( sqlStr, new String[]{user.getLanguageIso639_2()} );
        return mimeTypes;
    }

    public String[] getAllMimeTypes() {
        String sqlStr = "SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id";
        String[] mimeTypes = database.sqlQuery( sqlStr, new String[]{} );
        return mimeTypes;
    }

    public void addToMenu( TextDocumentDomainObject parentDocument, int parentMenuIndex,
                           DocumentDomainObject documentToAddToMenu, UserDomainObject user ) {
        parentDocument.getMenu( parentMenuIndex ).addMenuItem( new MenuItemDomainObject( this.getDocumentReference( documentToAddToMenu ) ) );
        saveDocument( parentDocument, user );
    }

    public BrowserDocumentDomainObject.Browser[] getAllBrowsers() {
        String sqlStr = "SELECT browser_id, name, value FROM browsers WHERE browser_id != 0";
        String[][] sqlResult = database.sqlQueryMulti( sqlStr, new String[0] );
        List browsers = new ArrayList();
        for ( int i = 0; i < sqlResult.length; i++ ) {
            browsers.add( createBrowserFromSqlRow( sqlResult[i] ) );
        }
        return (BrowserDocumentDomainObject.Browser[])browsers.toArray( new BrowserDocumentDomainObject.Browser[browsers.size()] );
    }

    public BrowserDocumentDomainObject.Browser getBrowserById( int browserIdToGet ) {
        if ( browserIdToGet == BrowserDocumentDomainObject.Browser.DEFAULT.getId() ) {
            return BrowserDocumentDomainObject.Browser.DEFAULT;
        }
        String sqlStr = "SELECT browser_id, name, value FROM browsers WHERE browser_id = ?";
        String[] sqlRow = database.sqlQuery( sqlStr, new String[]{"" + browserIdToGet} );
        BrowserDocumentDomainObject.Browser browser = createBrowserFromSqlRow( sqlRow );
        return browser;
    }

    protected BrowserDocumentDomainObject.Browser createBrowserFromSqlRow( String[] sqlRow ) {
        int browserId = Integer.parseInt( sqlRow[0] );
        String browserName = sqlRow[1];
        int browserSpecificity = Integer.parseInt( sqlRow[2] );
        BrowserDocumentDomainObject.Browser browser = new BrowserDocumentDomainObject.Browser( browserId, browserName, browserSpecificity );
        return browser;
    }

    public void deleteDocument( DocumentDomainObject document, UserDomainObject user ) {
        // Create a db connection and execte sp DocumentDelete on meta_id
        database.sqlUpdateProcedure( "DocumentDelete", new String[]{"" + document.getId()} );
        document.accept( new DocumentDeletingVisitor() );
        invalidateDocument( document );
    }

    public IdNamePair[] getCreatableDocumentTypeIdsAndNamesInUsersLanguage( DocumentDomainObject document,
                                                                            UserDomainObject user ) {
        String[][] sqlRows = database.sqlProcedureMulti( SPROC_GET_DOC_TYPES_FOR_USER, new String[]{
            "" + document.getId(), "" + user.getId(),
            user.getLanguageIso639_2()
        } );
        IdNamePair[] idNamePairs = new IdNamePair[sqlRows.length];
        for ( int i = 0; i < sqlRows.length; i++ ) {
            String[] sqlRow = sqlRows[i];
            idNamePairs[i] = new IdNamePair( Integer.parseInt( sqlRow[0] ), sqlRow[1] );
        }
        return idNamePairs;
    }

    public Map getAllDocumentTypeIdsAndNamesInUsersLanguage( UserDomainObject user ) {
        String[][] rows = database.sqlQueryMulti( "SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type", new String[]{
            user.getLanguageIso639_2()
        } );
        Map allDocumentTypeIdsAndNamesInUsersLanguage = new TreeMap();
        for ( int i = 0; i < rows.length; i++ ) {
            String[] row = rows[i];
            Integer documentTypeId = Integer.valueOf( row[0] );
            String documentTypeNameInUsersLanguage = row[1];
            allDocumentTypeIdsAndNamesInUsersLanguage.put( documentTypeId, documentTypeNameInUsersLanguage );
        }
        return allDocumentTypeIdsAndNamesInUsersLanguage;
    }

    public TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingDocument( DocumentDomainObject document ) {
        String sqlSelectMenus = "SELECT meta_id, menu_index FROM menus, childs WHERE menus.menu_id = childs.menu_id AND childs.to_meta_id = ? ORDER BY meta_id, menu_index";
        String[][] sqlRows = database.sqlQueryMulti( sqlSelectMenus, new String[]{"" + document.getId()} );
        TextDocumentMenuIndexPair[] documentMenuPairs = new TextDocumentMenuIndexPair[sqlRows.length];
        for ( int i = 0; i < sqlRows.length; i++ ) {
            String[] sqlRow = sqlRows[i];
            int containingDocumentId = Integer.parseInt( sqlRow[0] );
            int menuIndex = Integer.parseInt( sqlRow[1] );
            TextDocumentDomainObject containingDocument = (TextDocumentDomainObject)getDocument( containingDocumentId );
            documentMenuPairs[i] = new TextDocumentMenuIndexPair( containingDocument, menuIndex );
        }
        return documentMenuPairs;
    }

    public Iterator getDocumentsIterator( final IntRange idRange ) {
        return new DocumentsIterator( getDocumentIds( idRange ) );
    }

    private int[] getDocumentIds( IntRange idRange ) {
        String sqlSelectIds = "SELECT meta_id FROM meta WHERE meta_id >= ? AND meta_id <= ? ORDER BY meta_id";
        String[] documentIdStrings = database.sqlQuery( sqlSelectIds, new String[]{
            "" + idRange.getMinimumInteger(), "" + idRange.getMaximumInteger()
        } );
        int[] documentIds = new int[documentIdStrings.length];
        for ( int i = 0; i < documentIdStrings.length; i++ ) {
            documentIds[i] = Integer.parseInt( documentIdStrings[i] );
        }
        return documentIds;
    }

    public int[] getAllDocumentIds() {
        String[] documentIdStrings = database.sqlQuery( "SELECT meta_id FROM meta ORDER BY meta_id", new String[0] );
        int[] documentIds = new int[documentIdStrings.length];
        for ( int i = 0; i < documentIdStrings.length; i++ ) {
            documentIds[i] = Integer.parseInt( documentIdStrings[i] );
        }
        return documentIds;
    }

    static void deleteFileDocumentFilesAccordingToFileFilter( FileFilter fileFilter ) {
        File filePath = Imcms.getServices().getConfig().getFilePath();
        File[] filesToDelete = filePath.listFiles( fileFilter );
        for ( int i = 0; i < filesToDelete.length; i++ ) {
            filesToDelete[i].delete();
        }
    }

    static void deleteAllFileDocumentFiles( FileDocumentDomainObject fileDocument ) {
        deleteFileDocumentFilesAccordingToFileFilter( new FileDocumentFileFilter( fileDocument ) );
    }

    public DocumentPermissionSetMapper getDocumentPermissionSetMapper() {
        return documentPermissionSetMapper;
    }

    static void deleteOtherFileDocumentFiles( final FileDocumentDomainObject fileDocument ) {
        deleteFileDocumentFilesAccordingToFileFilter( new SuperfluousFileDocumentFilesFileFilter( fileDocument ) );
    }

    public void clearDocumentCache() {
        documentCache.clear();
    }

    public int getLowestDocumentId() {
        return Integer.parseInt( database.sqlQueryStr( "SELECT MIN(meta_id) FROM meta", new String[0] ) );
    }

    public int getHighestDocumentId() {
        return Integer.parseInt( database.sqlQueryStr( "SELECT MAX(meta_id) FROM meta", new String[0] ) );
    }

    public void copyDocument( DocumentDomainObject selectedChild,
                              UserDomainObject user ) {
        String copyHeadlineSuffix = services.getAdminTemplate( COPY_HEADLINE_SUFFIX_TEMPLATE, user, null );
        selectedChild.setHeadline( selectedChild.getHeadline() + copyHeadlineSuffix );
        makeDocumentLookNew( selectedChild, user );
        services.getDocumentMapper().saveNewDocument( selectedChild, user );
    }

    public void saveCategory( CategoryDomainObject category ) throws CategoryAlreadyExistsException {
        CategoryDomainObject categoryInDb = getCategory( category.getType(), category.getName() );
        if ( null != categoryInDb && category.getId() != categoryInDb.getId() ) {
            throw new CategoryAlreadyExistsException( "A category with name \"" + category.getName()
                                                      + "\" already exists in category type \""
                                                      + category.getType().getName()
                                                      + "\"." );
        }
        if (0 == category.getId()) {
            addCategory( category );
        } else {
            updateCategory( category );
        }
    }

    public static class TextDocumentMenuIndexPair {

        private TextDocumentDomainObject document;
        private int menuIndex;

        public TextDocumentMenuIndexPair( TextDocumentDomainObject document, int menuIndex ) {
            this.document = document;
            this.menuIndex = menuIndex;
        }

        public TextDocumentDomainObject getDocument() {
            return document;
        }

        public int getMenuIndex() {
            return menuIndex;
        }
    }

    private class DocumentsIterator implements Iterator {

        int[] documentIds;
        int index = 0;

        DocumentsIterator( int[] documentIds ) {
            this.documentIds = (int[])documentIds.clone();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return index < documentIds.length;
        }

        public Object next() {
            if ( !hasNext() ) {
                throw new NoSuchElementException();
            }
            return getDocument( documentIds[index++] );
        }
    }

    public static class SaveEditedDocumentCommand implements DocumentPageFlow.SaveDocumentCommand {

        public void saveDocument( DocumentDomainObject document, UserDomainObject user ) {
            Imcms.getServices().getDocumentMapper().saveDocument( document, user );
        }
    }

    private static class FileDocumentFileFilter implements FileFilter {

        protected final FileDocumentDomainObject fileDocument;

        protected FileDocumentFileFilter( FileDocumentDomainObject fileDocument ) {
            this.fileDocument = fileDocument;
        }

        public boolean accept( File file ) {
            String filename = file.getName();
            Perl5Util perl5Util = new Perl5Util();
            if ( perl5Util.match( "/(\\d+)(?:_se|\\.(.*))?/", filename ) ) {
                String idStr = perl5Util.group( 1 );
                String variantName = FileUtility.unescapeFilename( StringUtils.defaultString( perl5Util.group( 2 ) ) );
                return accept( file, Integer.parseInt( idStr ), variantName );
            }
            return false;
        }

        public boolean accept( File file, int fileDocumentId, String fileId ) {
            if ( fileDocumentId == fileDocument.getId() ) {
                return true;
            }
            return false;
        }
    }

    private static class SuperfluousFileDocumentFilesFileFilter extends FileDocumentFileFilter {

        private SuperfluousFileDocumentFilesFileFilter( FileDocumentDomainObject fileDocument ) {
            super( fileDocument );
        }

        public boolean accept( File file, int fileDocumentId, String fileId ) {
            boolean correctFileForFileDocumentFile = file.equals( DocumentSavingVisitor.getFileForFileDocument( fileDocumentId, fileId ) );
            boolean fileDocumentHasFile = null != fileDocument.getFile( fileId );
            return super.accept( file, fileDocumentId, fileId )
                   && ( !correctFileForFileDocumentFile || !fileDocumentHasFile );
        }
    }

    private static class DocumentCache extends AbstractMapDecorator {

        private DocumentMapper documentMapper;

        DocumentCache( Map map, DocumentMapper documentMapper ) {
            super( map );
            this.documentMapper = documentMapper;
        }

        public Object get( Object key ) {

            SoftReference[] documentSoftReferenceArray = (SoftReference[])map.get( key );
            DocumentDomainObject document = null;
            if ( null != documentSoftReferenceArray && null != documentSoftReferenceArray[0] ) {
                document = (DocumentDomainObject)documentSoftReferenceArray[0].get();
            }
            if ( null == document ) {
                int documentId = ( (Integer)key ).intValue();
                documentSoftReferenceArray = new SoftReference[1];
                map.put( key, documentSoftReferenceArray );
                document = documentMapper.getDocumentFromDb( documentId );
                documentSoftReferenceArray[0] = new SoftReference( document );
            }
            return document;
        }

    }

    private static class SectionNameComparator implements Comparator {

        public int compare( Object o1, Object o2 ) {
            SectionDomainObject section1 = (SectionDomainObject)o1;
            SectionDomainObject section2 = (SectionDomainObject)o2;
            return section1.getName().compareToIgnoreCase( section2.getName() );
        }
    }
}

