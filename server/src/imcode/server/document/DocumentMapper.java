package imcode.server.document;

import com.imcode.imcms.api.TextDocument;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.LanguageMapper;
import imcode.server.WebAppGlobalConstants;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateHelper;
import imcode.util.poll.PollHandlingSystem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentMapper {

    private final static String SPROC_GET_USER_ROLES_DOC_PERMISSONS = "GetUserRolesDocPermissions";
    private static final int UNLIMITED_MAX_CATEGORY_CHOICES = 0;
    private static final String SPROC_GET_DOC_TYPES_FOR_USER = "GetDocTypesForUser";

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

    // Stored procedure names used in this class
    // todo make sure all these is only used in one sprocMethod
    private static final String SPROC_SECTION_GET_INHERIT_ID = "SectionGetInheritId";
    private static final String SPROC_GET_FILE_NAME = "GetFileName";
    private static final String SPROC_GET_DOCUMENT_INFO = "GetDocumentInfo";
    private static final String SPROC_GET_USER_PERMISSION_SET = "GetUserPermissionSet";
    private static final String SPROC_GET_TEXT = "GetText";
    private static final String SPROC_GET_INCLUDES = "GetIncludes";
    private static final String SPROC_INSERT_TEXT = "InsertText";
    private static final String SPROC_UPDATE_PARENTS_DATE_MODIFIED = "UpdateParentsDateModified";
    private static final String SPROC_INHERIT_PERMISSONS = "InheritPermissions";
    private static final String SPROC_CLASSIFICATION_FIX = "Classification_Fix";
    private static final String SPROC_SECTION_GET_ALL = "SectionGetAll";
    protected ImcmsAuthenticatorAndUserMapper imcmsAAUM;

    private Logger log = Logger.getLogger( DocumentMapper.class );
    protected IMCServiceInterface service;
    private DocumentIndex documentIndex;

    public DocumentMapper( IMCServiceInterface service, ImcmsAuthenticatorAndUserMapper imcmsAAUM ) {
        this.service = service;
        this.imcmsAAUM = imcmsAAUM;
        File webAppPath = WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath();
        File indexDirectory = new File( webAppPath, "WEB-INF/index" );

        this.documentIndex = new DocumentIndex( indexDirectory ) ;
    }

    public void addDocumentToMenu( UserDomainObject user, int menuDocumentId, int menuIndex, int toBeAddedId )
            throws DocumentAlreadyInMenuException {
        addDocumentToMenu( service, user, menuDocumentId, menuIndex, toBeAddedId );
    }

    public static void addDocumentToMenu( IMCServiceInterface service, UserDomainObject user, int menuDocumentId,
                                          int menuIndex, int toBeAddedId ) throws DocumentAlreadyInMenuException {
        int updatedRows = service.sqlUpdateProcedure( "AddExistingDocToMenu",
                                                      new String[]{
                                                          "" + menuDocumentId, "" + toBeAddedId, "" + menuIndex
                                                      } );

        if ( 1 == updatedRows ) {	// if existing doc is added to the menu
            service.updateLogs( "Link from [" + menuDocumentId + "] in menu [" + menuIndex + "] to [" + toBeAddedId
                                + "] added by user: ["
                                + user.getFullName()
                                + "]" );
        } else {
            throw new DocumentAlreadyInMenuException( "Failed to add document " + toBeAddedId + " to menu "
                                                      + menuIndex
                                                      + " on document "
                                                      + menuDocumentId );
        }
    }

    public static boolean checkUsersRights( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id,
                                            String lang_prefix, String doc_type ) {
        HashSet user_doc_types = sprocGetDocTypesForUser( imcref, user, parent_meta_id, lang_prefix );
        boolean userHasRights = user_doc_types.contains( doc_type );
        return userHasRights;
    }

    public static void copyTemplateData( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id,
                                         String meta_id ) {
        //ok now lets see what to do with the templates
        String[] temp = sqlSelectTemplateInfoFromTextDocs( imcref, parent_meta_id );

        //lets get the users greatest permission_set for this dokument
        final int perm_set = imcref.getUserHighestPermissionSet( Integer.parseInt( meta_id ), user.getUserId() );
        //ok now we have to setup the template too use

        if ( perm_set == IMCConstants.DOC_PERM_SET_RESTRICTED_1 ) {
            //ok restricted_1 permission lets see if we have a default template fore this one
            //and if so lets put it as the orinary template instead of the parents
            try {
                int tempInt = Integer.parseInt( temp[3] );
                if ( tempInt >= 0 ) {
                    temp[0] = String.valueOf( tempInt );
                }
            } catch ( NumberFormatException nfe ) {

                //there wasn't a number but we dont care, we just catch the exeption and moves on.
            }
        } else if ( perm_set == IMCConstants.DOC_PERM_SET_RESTRICTED_2 ) { //ok we have a restricted_2 permission lets see if we have default template fore this one
            //and if soo lets put it as ordinary instead of the parents
            try {
                int tempInt = Integer.parseInt( temp[4] );
                if ( tempInt >= 0 ) {
                    temp[0] = String.valueOf( tempInt );
                }
            } catch ( NumberFormatException nfe ) {
                //there wasn't a number but we dont care, we just catch the exeption and moves on.
            }
        }
        //ok were set, lets update db
        sqlInsertIntoTemplateInfoIntoTextDocs( imcref, meta_id, temp );
    }

    /**
     * Inspired by the SaveNewMeta servlet... I went throu the code and tried to extract the nessesary parts. Hasse
     * todo: make the SaveNewMeta to use this method instead.
     *
     * @param parentId
     * @param parentMenuNumber
     * @return
     */
    public synchronized DocumentDomainObject createNewTextDocument( UserDomainObject user, int parentId,
                                                                    int parentMenuNumber ) {
        // general for all types of documents
        int docType = DocumentDomainObject.DOCTYPE_TEXT;
        Date nowDateTime = new Date();

        int newMetaId = sqlCreateNewRowInMetaCopyParentData( service, parentId );
        // fix the data that is unique for this document
        sqlUpdateCreatedDate( newMetaId, nowDateTime );
        sqlUpdateModifiedDate( service, newMetaId, nowDateTime );
        sqlUpdateDocType( service, newMetaId, docType );

        // inherit all the different data that's not in meta from parent.
        sprocUpdateInheritPermissions( service, newMetaId, parentId, docType );
        inheritClassifications( parentId, newMetaId );
        inheritSection( parentId, newMetaId );

        // update parents, why? what is this? /Hasse
        try {
            addDocumentToMenu( service, user, parentId, parentMenuNumber, newMetaId );
        } catch ( DocumentAlreadyInMenuException e ) {
            // ok, the document alredy exists in that menu.
        }

        // update parents modfied date because it has gotten an new link
        sqlUpdateModifiedDate( service, parentId, nowDateTime );

        // Text document
        DocumentMapper.copyTemplateData( service, user, String.valueOf( parentId ), String.valueOf( newMetaId ) );
        DocumentMapper.sqlUpdateActivateTheDocument( service, newMetaId );

        return getDocument( newMetaId );
    }

    /**
     * Delete childs from a menu.
     */
    public static void deleteChilds( IMCServiceInterface service, int meta_id, int menu, UserDomainObject user,
                                     String childsThisMenu[] ) {
        StringBuffer childStr = new StringBuffer( "[" );
        // create a db connection an get meta data

        for ( int i = 0; i < childsThisMenu.length; i++ ) {
            int childId = Integer.parseInt( childsThisMenu[i] );

            removeDocumentFromMenu( service, user, meta_id, menu, childId );

            childStr.append( childsThisMenu[i] );
            if ( i < childsThisMenu.length - 1 ) {
                childStr.append( ", " );
            }
        }
        childStr.append( "]" );

    }

    public CategoryDomainObject[] getAllCategoriesOfType( CategoryTypeDomainObject categoryType ) {
        String sqlQuery = "SELECT categories.category_id, categories.name, categories.description\n"
                          + "FROM categories\n"
                          + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                          + "WHERE category_types.name = ?";
        String[][] sqlResult = service.sqlQueryMulti( sqlQuery, new String[]{categoryType.getName()} );
        CategoryDomainObject[] categoryDomainObjects = new CategoryDomainObject[sqlResult.length];
        for ( int i = 0; i < sqlResult.length; i++ ) {
            int categoryId = Integer.parseInt( sqlResult[i][0] );
            String categoryName = sqlResult[i][1];
            String categoryDescription = sqlResult[i][2];

            categoryDomainObjects[i] =
            new CategoryDomainObject( categoryId, categoryName, categoryDescription, categoryType );
        }
        return categoryDomainObjects;
    }

    public CategoryTypeDomainObject[] getAllCategoryTypes() {
        String sqlQuery = "SELECT name, max_choices FROM category_types ORDER BY name";
        String[][] sqlResult = service.sqlQueryMulti( sqlQuery, new String[0] );

        CategoryTypeDomainObject[] categoryTypeDomainObjects = new CategoryTypeDomainObject[sqlResult.length];
        for ( int i = 0; i < categoryTypeDomainObjects.length; i++ ) {
            String typeName = sqlResult[i][0];
            int maxChoices = Integer.parseInt( sqlResult[i][1] );

            categoryTypeDomainObjects[i] = new CategoryTypeDomainObject( typeName, maxChoices );
        }

        return categoryTypeDomainObjects;
    }

    public SectionDomainObject[] getAllSections() {
        String[] sqlResult = service.sqlProcedure( SPROC_SECTION_GET_ALL, new String[0] );
        SectionDomainObject[] allSections = new SectionDomainObject[sqlResult.length / 2];
        for ( int i = 0; i < sqlResult.length; i += 2 ) {
            int sectionId = Integer.parseInt( sqlResult[i] );
            String sectionName = sqlResult[i + 1];
            allSections[i / 2] = new SectionDomainObject( sectionId, sectionName );
        }
        return allSections;
    }

    public CategoryDomainObject getCategory( CategoryTypeDomainObject categoryType, String categoryName ) {
        String sqlQuery = "SELECT categories.category_id, categories.name, categories.description\n"
                          + "FROM categories\n"
                          + "JOIN category_types\n"
                          + "ON categories.category_type_id = category_types.category_type_id\n"
                          + "WHERE category_types.name = ?\n"
                          + "AND categories.name = ?";
        String[] sqlResult = service.sqlQuery( sqlQuery, new String[]{categoryType.getName(), categoryName} );
        if ( 0 != sqlResult.length ) {
            final int categoryId = Integer.parseInt( sqlResult[0] );
            final String categoryNameFromDb = sqlResult[1];
            final String categoryDescription = sqlResult[2];

            return new CategoryDomainObject( categoryId, categoryNameFromDb, categoryDescription, categoryType );
        } else {
            return null;
        }
    }

    public CategoryDomainObject getCategory( int categoryId ) {
        String sqlQuery = "SELECT categories.name, categories.description, category_types.name \n" +
                          "FROM categories\n" + "JOIN category_types\n"
                          + "ON categories.category_type_id = category_types.category_type_id\n" +
                          "WHERE categories.category_id = ?";
        String[] sqlResult = service.sqlQuery( sqlQuery, new String[]{"" + categoryId} );
        if ( 0 != sqlResult.length ) {
            final String categoryNameFromDb = sqlResult[0];
            final String categoryDescription = sqlResult[1];
            final String categoryTypeName = sqlResult[2];
            CategoryTypeDomainObject categoryType = getCategoryType( categoryTypeName );
            return new CategoryDomainObject( categoryId, categoryNameFromDb, categoryDescription, categoryType );
        } else {
            return null;
        }
    }

    public CategoryDomainObject getCategoryById( int categoryId ) {
        String sqlQuery = "SELECT categories.name, categories.description, category_types.name, category_types.max_choices\n"
                          + "FROM categories\n"
                          + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                          + "WHERE categories.category_id = ?";

        String[] categorySqlResult = service.sqlQuery( sqlQuery, new String[]{"" + categoryId} );

        String categoryName = categorySqlResult[0];
        String categoryDescription = categorySqlResult[1];
        String categoryTypeName = categorySqlResult[2];
        int categoryTypeMaxChoices = Integer.parseInt( categorySqlResult[3] );

        CategoryTypeDomainObject categoryType = new CategoryTypeDomainObject( categoryTypeName, categoryTypeMaxChoices );

        return new CategoryDomainObject( categoryId, categoryName, categoryDescription, categoryType );

    }

    public CategoryTypeDomainObject getCategoryType( String categoryTypeName ) {
        String sqlStr = "SELECT category_types.name, category_types.max_choices\n" + "FROM category_types\n"
                        + "WHERE category_types.name = ?";
        String[] sqlResult = service.sqlQuery( sqlStr, new String[]{categoryTypeName} );

        if ( null == sqlResult || 0 == sqlResult.length ) {
            return null;
        } else {
            String categoryTypeNameFromDb = sqlResult[0];
            int categoryTypeMaxChoices = Integer.parseInt( sqlResult[1] );
            return new CategoryTypeDomainObject( categoryTypeNameFromDb, categoryTypeMaxChoices );
        }
    }

    public String getKeywordsAsOneString( int meta_id ) {
        String[] keywords = getKeywords( meta_id );
        return StringUtils.join( keywords, ", " );
    }

    public DocumentDomainObject getDocument( int metaId ) {
        DocumentDomainObject document;
        Map documentData = sprocGetDocumentInfo( service, metaId );
        if ( documentData == null ) {
            throw new IndexOutOfBoundsException( "No such internalDocument: " + metaId );
        }
        DateFormat dateform = new SimpleDateFormat( DateHelper.DATE_TIME_SECONDS_FORMAT_STRING );
        document = new DocumentDomainObject();
        document.setMetaId( Integer.parseInt( (String)documentData.get( "meta_id" ) ) );
        document.setDocumentType( Integer.parseInt( (String)documentData.get( "doc_type" ) ) );
        document.setCreator( imcmsAAUM.getUser( Integer.parseInt( (String)documentData.get( "owner_id" ) ) ) );
        document.setHeadline( (String)documentData.get( "meta_headline" ) );
        document.setText( (String)documentData.get( "meta_text" ) );
        document.setImage( (String)documentData.get( "meta_image" ) );
        document.setTarget( (String)documentData.get( "target" ) );
        String langStr = (String)documentData.get( "lang_prefix" );
        try {
            langStr = LanguageMapper.getAsIso639_2( langStr );
        } catch ( LanguageMapper.LanguageNotSupportedException e ) {
            log.error( "Unsupported language '" + langStr + "' found in database for document " + metaId
                       + ". Using default.",
                       e );
            langStr = service.getDefaultLanguageAsIso639_2();
        }
        document.setLanguageIso639_2( langStr );
        document.setArchivedFlag( "0".equals( documentData.get( "archive" ) ) ? false : true );

        String publisherIDStr = (String)documentData.get( "publisher_id" );
        if ( null != publisherIDStr ) {
            UserDomainObject publisher = imcmsAAUM.getUser( Integer.parseInt( publisherIDStr ) );
            document.setPublisher( publisher );
        }

        document.setSections( getSections( metaId ) );

        document.setKeywords( getKeywords( metaId ) );

        try {
            document.setCreatedDatetime( dateform.parse( (String)documentData.get( "date_created" ) ) );
        } catch ( NullPointerException npe ) {
            document.setCreatedDatetime( null );
        } catch ( ParseException pe ) {
            document.setCreatedDatetime( null );
        }
        try {
            document.setModifiedDatetime( dateform.parse( (String)documentData.get( "date_modified" ) ) );
        } catch ( NullPointerException npe ) {
            document.setModifiedDatetime( null );
        } catch ( ParseException pe ) {
            document.setModifiedDatetime( null );
        }
        try {
            document.setActivatedDatetime( dateform.parse( (String)documentData.get( "activated_datetime" ) ) );
        } catch ( NullPointerException npe ) {
            document.setActivatedDatetime( null );
        } catch ( ParseException pe ) {
            document.setActivatedDatetime( null );
        }
        try {
            document.setArchivedDatetime( dateform.parse( (String)documentData.get( "archived_datetime" ) ) );
        } catch ( NullPointerException npe ) {
            document.setArchivedDatetime( null );
        } catch ( ParseException pe ) {
            document.setArchivedDatetime( null );
        }
        if ( DocumentDomainObject.DOCTYPE_FILE == document.getDocumentType() ) {
            document.setFilename( sprocGetFilename( service, metaId ) );
        }
        if ( DocumentDomainObject.DOCTYPE_TEXT == document.getDocumentType() ) {
            initTextDoc( service, document );
        }
        addCategoriesFromDatabaseToDocument( document );

        String[] sprocResult1 = service.sqlProcedure( SPROC_GET_USER_ROLES_DOC_PERMISSONS,
                                                      new String[]{String.valueOf( document.getMetaId() ), "-1"} );
        int noOfColumns = 4;
        for ( int i = 0, k = 0; i < sprocResult1.length; i = i + noOfColumns, k++ ) {
            int roleId = Integer.parseInt( sprocResult1[i] );
            String roleName = sprocResult1[i + 1];
            RoleDomainObject role = new RoleDomainObject( roleId, roleName );
            int rolePermissionSetId = Integer.parseInt( sprocResult1[i + 2] );
            document.setPermissionSetForRole( role, rolePermissionSetId );
        }

        return document;
    }

    public void getDocumentAndSetCategoriesFromFormAndSaveDocument( HttpServletRequest req, int meta_id_int )
            throws MaxCategoryDomainObjectsOfTypeExceededException {
        DocumentMapper documentMapper = service.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( meta_id_int );
        setDocumentCategoriesFromForm( req, document, documentMapper );
        documentMapper.saveDocument( document );
    }

    public static HashMap getDocumentTypsAndNames( IMCServiceInterface service, int metaId, int userId,
                                                   String lang_prefix ) {
        String[] docTypesQueryResult = service.sqlProcedure( SPROC_GET_DOC_TYPES_FOR_USER,
                                                             new String[]{"" + metaId, "" + userId, lang_prefix} );
        HashMap docTypesIdAndNames = new HashMap();
        for ( int j = 0; j < docTypesQueryResult.length; j += 2 ) {
            String keyId = docTypesQueryResult[j];
            String valueName = docTypesQueryResult[j + 1];
            docTypesIdAndNames.put( keyId, valueName );
        }
        return docTypesIdAndNames;
    }

    public Map getIncludedDocuments( DocumentDomainObject textDocument ) {
        Map result = new HashMap();
        String[] includedMetaIds = sprocGetIncludes( service, textDocument.getMetaId() );
        for ( int i = 0; i < includedMetaIds.length; i += 2 ) {
            int include_id = Integer.parseInt( includedMetaIds[i] );
            int included_meta_id = Integer.parseInt( includedMetaIds[i + 1] );
            result.put( new Integer( include_id ), new Integer( included_meta_id ) );
        }
        return result;
    }

    public MenuItemDomainObject[] getMenuItemsForDocument( int parentId, int menuIndex ) {
        DocumentDomainObject parent = getDocument( parentId );
        int sortOrder = getSortOrderOfDocument( parentId );
        String orderBy = getSortOrderAsSqlOrderBy( sortOrder );
        String sqlStr = "select to_meta_id, menu_sort, manual_sort_order, tree_sort_index from childs,meta where childs.meta_id = meta.meta_id and childs.meta_id = ? and menu_sort = ? order by "
                        + orderBy;
        String[] sqlResult = service.sqlQuery( sqlStr, new String[]{"" + parentId, "" + menuIndex} );
        MenuItemDomainObject[] menuItems = new MenuItemDomainObject[sqlResult.length / 4];
        for ( int i = 0; i < sqlResult.length; i += 4 ) {
            int to_meta_id = Integer.parseInt( sqlResult[i] );
            int menu_sort = Integer.parseInt( sqlResult[i + 1] );
            int manual_sort_order = Integer.parseInt( sqlResult[i + 2] );
            String tree_sort_index = sqlResult[i + 3];
            DocumentDomainObject child = getDocument( to_meta_id );
            menuItems[i / 4] =
            new MenuItemDomainObject( parent, child, menu_sort, manual_sort_order, tree_sort_index );
        }
        Arrays.sort( menuItems, new MenuItemDomainObject.TreeKeyCoparator() );

        return menuItems;
    }

    public SectionDomainObject getSectionById( int sectionId ) {
        String sectionName = service.sqlQueryStr( "SELECT section_name FROM sections WHERE section_id = ?",
                                                  new String[]{"" + sectionId} );
        if ( null == sectionName ) {
            return null;
        }
        return new SectionDomainObject( sectionId, sectionName );
    }

    /**
     * @return the sections for a document, empty array if there is none.
     */
    public SectionDomainObject[] getSections( int meta_id ) {
        String[][] sectionData = service.sqlProcedureMulti( SPROC_SECTION_GET_INHERIT_ID,
                                                            new String[]{String.valueOf( meta_id )} );

        SectionDomainObject[] sections = new SectionDomainObject[sectionData.length];

        for ( int i = 0; i < sectionData.length; i++ ) {
            int sectionId = Integer.parseInt( sectionData[i][0] );
            String sectionName = sectionData[i][1];
            sections[i] = new SectionDomainObject( sectionId, sectionName );
        }
        return sections;
    }

    public TextDocumentTextDomainObject getText( int metaId, int no ) {
        try {
            String[] results = sprocGetText( metaId, no );

            if ( results == null || results.length == 0 ) {
                /* There was no text. Return null. */
                return null;
            }

            /* Return the text */
            String text = results[0];
            int type = Integer.parseInt( results[1] );

            return new TextDocumentTextDomainObject( text, type );

        } catch ( NumberFormatException ex ) {
            /* There was no text, but we shouldn't come here unless the db returned something wrong. */
            log.error( "SProc 'sprocGetText()' returned an invalid text-type.", ex );
            return null;
        }
    }

    public TextDocumentTextDomainObject getTextField( DocumentDomainObject document, int textFieldIndexInDocument ) {
        return service.getText( document.getMetaId(), textFieldIndexInDocument );
    }

    public boolean hasAtLeastDocumentReadPermission( int documentId, UserDomainObject user ) {
        int[] wantedPermissionSetIds = {
            IMCConstants.DOC_PERM_SET_FULL,
            IMCConstants.DOC_PERM_SET_RESTRICTED_1,
            IMCConstants.DOC_PERM_SET_RESTRICTED_2,
            IMCConstants.DOC_PERM_SET_READ
        };
        return userIsSuperAdminOrHasPermissionSetId( documentId, user, wantedPermissionSetIds );
    }

    /**
     * @param documentId
     * @param user
     * @return
     * @deprecated Use hasAtLeastDocumentReadPermission(...) instead.
     */
    public boolean hasDocumentPermission( int documentId, UserDomainObject user ) {
        return hasAtLeastDocumentReadPermission( documentId, user );
    }

    public boolean hasEditPermission( int documentId, UserDomainObject user ) {

        int[] wantedPermissionSetIds = {
            IMCConstants.DOC_PERM_SET_FULL,
            IMCConstants.DOC_PERM_SET_RESTRICTED_1,
            IMCConstants.DOC_PERM_SET_RESTRICTED_2
        };

        return userIsSuperAdminOrHasPermissionSetId( documentId, user, wantedPermissionSetIds );
    }

    public boolean hasSharePermission( UserDomainObject user, int documentId ) {
        return service.checkUserDocSharePermission( user, documentId );
    }

    public void indexDocument( DocumentDomainObject document ) {
        File indexDir = new File( WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath(), "WEB-INF/index" );
        DocumentIndex documentIndexer = new DocumentIndex( indexDir );
        try {
            documentIndexer.reindexOneDocument( document );
        } catch ( IOException e ) {
            log.error( "Failed to index document " + document.getMetaId(), e );
        }
    }

    public void removeDocumentFromMenu( UserDomainObject user, int menuDocumentId, int menuIndex, int toBeRemovedId ) {
        removeDocumentFromMenu( service, user, menuDocumentId, menuIndex, toBeRemovedId );
    }

    public void removeInclusion( int includingMetaId, int includeIndex ) {
        sprocDeleteInclude( service, includingMetaId, includeIndex );
    }

    public void saveDocument( DocumentDomainObject document ) throws MaxCategoryDomainObjectsOfTypeExceededException {
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

        Date now = new Date();
        document.setModifiedDatetime( now );

        // Attributes in alphabetical order, so one can easier find if something is added or removed.
        Date activatedDatetime = document.getActivatedDatetime();
        boolean archived = document.isArchivedFlag();
        Date archivedDatetime = document.getArchivedDatetime();
        Date createdDatetime = document.getCreatedDatetime();
        // String filename = document.filename; // only in file documents, not implemented yet.
        String headline = document.getHeadline();
        String image = document.getImage();
        String language = document.getLanguageIso639_2();
        int menuSortOrder = document.getMenuSortOrder();
        Date modifiedDatetime = document.getModifiedDatetime();
        SectionDomainObject[] sections = document.getSections();
        String target = document.getTarget();
        TemplateDomainObject template = document.getTemplate();
        int templateGroupId = document.getTemplateGroupId();
        String text = document.getText();
        UserDomainObject publisher = document.getPublisher();

        sqlUpdateMeta( service, document.getMetaId(), activatedDatetime, archivedDatetime, createdDatetime, headline,
                       image, modifiedDatetime, target, text, archived, language, publisher );
        setSectionsForDocument( service, document.getMetaId(), sections );

        service.sqlUpdateQuery( "DELETE FROM document_categories WHERE meta_id = ?",
                                new String[]{"" + document.getMetaId()} );
        CategoryDomainObject[] categories = document.getCategories();
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            int categoryId = category.getId();
            service.sqlUpdateQuery( "INSERT INTO document_categories (meta_id, category_id) VALUES(?,?)",
                                    new String[]{"" + document.getMetaId(), "" + categoryId} );
        }

        for ( Iterator it = document.getRolesMappedToPermissionSetIds().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry rolePermissionTuple = (Map.Entry)it.next();
            RoleDomainObject role = (RoleDomainObject)rolePermissionTuple.getKey();
            int permissionSetId = ( (Integer)rolePermissionTuple.getValue() ).intValue();
            sprocSetRoleDocPermissionSetId( service, document.getMetaId(), role.getId(), permissionSetId );
        }

        // TODO Restricted One and Two (Bug 1443)

        if ( DocumentDomainObject.DOCTYPE_TEXT == document.getDocumentType() ) {
            // TEXT_DOC
            int templateId = template.getId();
            String sqlStr = "update text_docs set template_id = ?, sort_order = ?, group_id = ? where meta_id = ?";
            service.sqlUpdateQuery( sqlStr,
                                    new String[]{
                                        "" + templateId,
                                        "" + menuSortOrder,
                                        "" + templateGroupId,
                                        "" + document.getMetaId()
                                    } );
        } else {
            // TODO Handle other document types.
        }

        indexDocument( document );

    }

    /**
     * Store the given TextDocumentTextDomainObject in the DB.
     *
     * @param user      The user
     * @param document  The document
     * @param txt_no    The id of the text in the page.
     * @param text      The text.
     * @param text_type The text_type
     *                  <p/>
     *                  Supported text_types is:
     *                  <p/>
     *                  pollquestion-n		      where n represent the questíon number in this internalDocument
     *                  <p/>
     *                  pollanswer-n-m		          where n represent the questíon number in this internalDocument
     *                  and m represent the answer number in question number n
     *                  <p/>
     *                  pollpointanswer-n-m			  where n represent the questíon number in this internalDocument
     *                  and m represent the answer number in question number n
     *                  <p/>
     *                  pollparameter-popup_frequency    default(0) when > 0 show this poll as a popup on every new session that is a multiple
     *                  of the frequens.
     *                  <p/>
     *                  pollparameter-cookie			  default(0) user is allowed to fill in the poll more then once.
     *                  (1) = set cookie, if cookie exist on client don't allow more answers from that computer.
     *                  <p/>
     *                  pollparameter-hideresults		  default(0) if 1 then we don't send result to browser only a confimation text.
     *                  <p/>
     *                  pollparameter-confirmation_text  message to send back to browser as confirmation of poll participation.
     *                  pollparameter-email_recipients   email adress to reciver of result from free-text answers.
     *                  <p/>
     *                  pollparameter-result_template    template to use when return the result
     *                  <p/>
     *                  pollparameter-name			  name for this poll
     *                  pollparameter-description		  description for this poll
     */

    public void saveText( TextDocumentTextDomainObject text, DocumentDomainObject document, int txt_no, UserDomainObject user,
                          String text_type ) {
        String textstring = text.getText();

        int meta_id = document.getMetaId();
        // update text
        sprocUpdateInsertText( service, meta_id, txt_no, text, textstring );

        // update the date
        touchDocument( document );

        service.updateLogs(
                "Text " + txt_no + " in  " + "[" + meta_id + "] modified by user: [" + user.getFullName() + "]" );

        if ( !( "" ).equals( text_type ) ) {

            if ( text_type.startsWith( "poll" ) ) {
                PollHandlingSystem poll = service.getPollHandlingSystem();
                poll.savePollparameter( text_type, meta_id, txt_no, textstring );
            }
        }
    }

    /**
     * Save template -> text_docs, sort
     */
    public void saveTextDoc( int meta_id, UserDomainObject user, String template, int groupId ) {
        String sqlStr = "update text_docs set template_id = ?, group_id = ? where meta_id = ?";
        service.sqlUpdateQuery( sqlStr, new String[]{template, "" + groupId, "" + meta_id} );

        service.updateLogs( "Text docs  [" + meta_id + "] updated by user: [" + user.getFullName() + "]" );
    }

    public void setInclude( int includingMetaId, int includeIndex, int includedMetaId ) {
        sprocSetInclude( service, includingMetaId, includeIndex, includedMetaId );
    }

    public static void setSectionsForDocument( IMCServiceInterface imcref, int metaId, String[] sectionIdStrings ) {
        removeAllSectionsFromDocument( imcref, metaId );
        for ( int i = 0; null != sectionIdStrings && i < sectionIdStrings.length; i++ ) {
            addSectionToDocument( imcref, metaId, sectionIdStrings[i] );
        }
    }

    public static void sprocDeleteInclude( IMCServiceInterface imcref, int including_meta_id, int include_id ) {
        imcref.sqlUpdateProcedure( "DeleteInclude", new String[]{"" + including_meta_id, "" + include_id} );
    }

    public static String[] sprocGetDocTypeForUser( IMCServiceInterface service, UserDomainObject user, int meta_id,
                                                   String lang_prefix ) {
        return service.sqlProcedure( SPROC_GET_DOC_TYPES_FOR_USER,
                                     new String[]{
                                         String.valueOf( meta_id ), String.valueOf( user.getUserId() ), lang_prefix
                                     } );
    }

    public static String[] sprocGetIncludes( IMCServiceInterface imcref, int meta_id ) {
        String[] included_docs = imcref.sqlProcedure( SPROC_GET_INCLUDES, new String[]{String.valueOf( meta_id )} );
        return included_docs;
    }

    public void saveDocumentKeywords( int meta_id, String separatedKeywords ) {
        Set allKeywords = new HashSet( Arrays.asList( getAllKeywords() ) );
        String[] keywords = separatedKeywords.split( "\\W+" );
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
        int keywordId = Integer.parseInt(
                service.sqlQueryStr( "SELECT class_id FROM classification WHERE code = ?", new String[]{keyword} ) );
        service.sqlUpdateQuery( "INSERT INTO meta_classification (meta_id, class_id) VALUES(?,?)",
                                new String[]{"" + meta_id, "" + keywordId} );
    }

    private void deleteUnusedKeywords() {
        service.sqlUpdateQuery(
                "DELETE FROM classification WHERE class_id NOT IN (SELECT class_id FROM meta_classification)",
                new String[0] );
    }

    private void addKeyword( String keyword ) {
        service.sqlUpdateQuery( "INSERT INTO classification VALUES(?)", new String[]{keyword} );
    }

    private String[] getAllKeywords() {
        return service.sqlQuery( "SELECT code FROM classification", new String[0] );
    }

    private void deleteKeywordsFromDocument( int meta_id ) {
        String sqlDeleteKeywordsFromDocument = "DELETE FROM meta_classification WHERE meta_id = ?";
        service.sqlUpdateQuery( sqlDeleteKeywordsFromDocument, new String[]{"" + meta_id} );
    }

    public static void sprocSetInclude( IMCServiceInterface imcref, int including_meta_id, int include_id,
                                        int included_meta_id ) {
        imcref.sqlUpdateProcedure( "SetInclude",
                                   new String[]{"" + including_meta_id, "" + include_id, "" + included_meta_id} );
    }

    public static void sprocSetRoleDocPermissionSetId( IMCServiceInterface imcref, int metaId, int roleId,
                                                       int newSetId ) {
        imcref.sqlUpdateProcedure( "SetRoleDocPermissionSetId", new String[]{"" + roleId, "" + metaId, "" + newSetId} );
    }

    public static void sprocUpdateInheritPermissions( IMCServiceInterface imcref, int meta_id, int parent_meta_id,
                                                      int doc_type ) {
        imcref.sqlUpdateProcedure( SPROC_INHERIT_PERMISSONS,
                                   new String[]{"" + meta_id, "" + parent_meta_id, "" + doc_type} );
    }

    public static void sprocUpdateParentsDateModified( IMCServiceInterface imcref, int meta_id ) {
        imcref.sqlUpdateProcedure( SPROC_UPDATE_PARENTS_DATE_MODIFIED, new String[]{"" + meta_id} );
    }

    public static void sqlInsertIntoTexts( IMCServiceInterface imcref, String meta_id, String mHeadline, String mText ) {
        imcref.sqlUpdateQuery( "insert into texts (meta_id,name,text,type) values (?, 1, ?, 1)",
                               new String[]{meta_id, mHeadline} );
        imcref.sqlUpdateQuery( "insert into texts (meta_id,name,text,type) values (?, 2, ?, 1)",
                               new String[]{meta_id, mText} );
    }

    public static void sqlUpdateActivateTheDocument( IMCServiceInterface imcref, int meta_id ) {
        imcref.sqlUpdateQuery( "update meta set activate = 1 where meta_id = ?", new String[]{"" + meta_id} );
    }

    public static void sqlUpdateMetaDateCreated( IMCServiceInterface imcref, String meta_id, String created_datetime ) {
        String sqlStr;
        sqlStr = "update meta set date_created = ? where meta_id = ?";
        imcref.sqlUpdateQuery( sqlStr, new String[]{created_datetime, meta_id} );
    }

    public static void sqlUpdateModifiedDate( IMCServiceInterface service, int meta_id, Date date ) {
        String dateModifiedStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( date );
        service.sqlUpdateQuery( "update meta set date_modified = ? where meta_id = ?",
                                new String[]{dateModifiedStr, "" + meta_id} );
    }

    public void sqlUpdateModifiedDatesOnDocumentAndItsParent( int meta_id, Date dateTime ) {
        String modifiedDateTimeStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( dateTime );
        service.sqlUpdateQuery( "update meta set date_modified = ? where meta_id = ?",
                                new String[]{modifiedDateTimeStr, "" + meta_id} );
        // Update the date_modified for all parents.
        sprocUpdateParentsDateModified( service, meta_id );
    }

    /**
     * Set the modified datetime of a document to now
     *
     * @param document The id of the document
     */
    public void touchDocument( DocumentDomainObject document ) {
        touchDocument( document, service.getCurrentDate() );
    }

    private void addCategoriesFromDatabaseToDocument( DocumentDomainObject document ) {
        String[][] categories = service.sqlQueryMulti( "SELECT categories.category_id, categories.name, categories.description, category_types.name, category_types.max_choices"
                                                       + " FROM document_categories"
                                                       + " JOIN categories"
                                                       + "  ON document_categories.category_id = categories.category_id"
                                                       + " JOIN category_types"
                                                       + "  ON categories.category_type_id = category_types.category_type_id"
                                                       + " WHERE document_categories.meta_id = ?",
                                                       new String[]{"" + document.getMetaId()} );
        for ( int i = 0; i < categories.length; i++ ) {
            String[] categoryArray = categories[i];

            int categoryId = Integer.parseInt( categoryArray[0] );
            String categoryName = categoryArray[1];
            String categoryDescription = categoryArray[2];
            String categoryTypeName = categoryArray[3];
            int categoryTypeMaxChoices = Integer.parseInt( categoryArray[4] );

            CategoryTypeDomainObject categoryType = new CategoryTypeDomainObject( categoryTypeName,
                                                                                  categoryTypeMaxChoices );
            CategoryDomainObject category = new CategoryDomainObject( categoryId, categoryName, categoryDescription,
                                                                      categoryType );
            document.addCategory( category );
        }

    }

    private static void addSectionToDocument( IMCServiceInterface imcref, int metaId, String sectionIdString ) {
        try {
            int sectionId = Integer.parseInt( sectionIdString );
            addSectionToDocument( imcref, metaId, sectionId );
        } catch ( NumberFormatException nfe ) {
            // do nothing, illegal section-id, or none chosen.
        }
    }

    private static void addSectionToDocument( IMCServiceInterface imcref, int metaId, int sectionId ) {
        imcref.sqlUpdateQuery( "INSERT INTO meta_section VALUES(?,?)", new String[]{"" + metaId, "" + sectionId} );
    }

    private boolean arrayContains( int[] array, int wantedValue ) {
        boolean result = false;
        for ( int i = 0; i < array.length; ++i ) {
            if ( wantedValue == array[i] ) {
                result = true;
                break;
            }
        }
        return result;
    }

    private String getSortOrderAsSqlOrderBy( int sortOrder ) {
        String orderBy = "meta_headline";
        switch ( sortOrder ) {
            case TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING:
                orderBy = "manual_sort_order desc";
                break;

            case TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING:
                orderBy = "date_modified desc";
                break;

            case TextDocument.Menu.SORT_BY_HEADLINE:
                orderBy = "meta_headline";
                break;
        }
        return orderBy;
    }

    private int getSortOrderOfDocument( int documentId ) {
        return Integer.parseInt( sqlSelectTemplateInfoFromTextDocs( service, String.valueOf( documentId ) )[1] );
    }

    private void inheritClassifications( int from_parentId, int to_newMetaId ) {
        String classifications = getKeywordsAsOneString( from_parentId );
        saveDocumentKeywords( to_newMetaId, classifications );
    }

    private void inheritSection( int from_parentId, int to_metaId ) {
        SectionDomainObject[] sections = getSections( from_parentId );
        setSectionsForDocument( service, to_metaId, sections );
    }

    private static void initTextDoc( IMCServiceInterface service, DocumentDomainObject inout_document ) {
        // all from the table text_doc
        String[] textdoc_data1 = service.sqlProcedure( "GetTextDocData",
                                                       new String[]{String.valueOf( inout_document.getMetaId() )} );
        String[] textdoc_data = textdoc_data1;
        if ( textdoc_data.length >= 4 ) {
            int template_id = Integer.parseInt( textdoc_data[0] );
            //String simple_name = textdoc_data[1];
            int sort_order = Integer.parseInt( textdoc_data[2] );
            int group_id = Integer.parseInt( textdoc_data[3] );
            TemplateDomainObject template = service.getTemplateMapper().getTemplateById( template_id );
            inout_document.setTemplate( template );
            inout_document.setMenuSortOrder( sort_order );
            inout_document.setTemplateGroupId( group_id );
        }
    }

    private static void makeBooleanSQL( String columnName, boolean bool, List sqlUpdateColumns, List sqlUpdateValues ) {
        sqlUpdateColumns.add( columnName + " = ?" );
        sqlUpdateValues.add( bool ? "1" : "0" );
    }

    private static void makeDateSQL( String columnName, Date date, List sqlUpdateColumns, List sqlUpdateValues ) {
        if ( null != date ) {
            String dateStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( date );
            makeStringSQL( columnName, dateStr, sqlUpdateColumns, sqlUpdateValues );
        } else {
            makeStringSQL( columnName, null, sqlUpdateColumns, sqlUpdateValues );
        }
    }

    private static void makeIntSQL( String columnName, Integer integer, ArrayList sqlUpdateColumns,
                                    ArrayList sqlUpdateValues ) {
        if ( null != integer ) {
            sqlUpdateColumns.add( columnName + " = ?" );
            sqlUpdateValues.add( "" + integer );
        } else {
            sqlUpdateColumns.add( columnName + " = NULL" );
        }
    }

    protected static void makeStringSQL( String columnName, String value, List sqlUpdateColumns, List sqlUpdateValues ) {
        if ( null != value ) {
            sqlUpdateColumns.add( columnName + " = ?" );
            sqlUpdateValues.add( value );
        } else {
            sqlUpdateColumns.add( columnName + " = NULL" );
        }
    }

    private static void removeAllSectionsFromDocument( IMCServiceInterface imcref, int metaId ) {
        imcref.sqlUpdateQuery( "DELETE FROM meta_section WHERE meta_id = ?", new String[]{"" + metaId} );
    }

    private static void removeDocumentFromMenu( IMCServiceInterface service, UserDomainObject user, int menuDocumentId,
                                                int menuIndex, int toBeRemovedId ) {
        String sqlStr = "delete from childs\n" + "where to_meta_id = ?\n" + "and meta_id = ?\n" + "and menu_sort = ?";

        int updatedRows = service.sqlUpdateQuery( sqlStr,
                                                  new String[]{
                                                      "" + toBeRemovedId, "" + menuDocumentId, "" + menuIndex
                                                  } );

        if ( 1 == updatedRows ) {	// if existing doc is added to the menu
            service.updateLogs( "Link from [" + menuDocumentId + "] in menu [" + menuIndex + "] to [" + toBeRemovedId
                                + "] removed by user: ["
                                + user.getFullName()
                                + "]" );
        } else {
            throw new RuntimeException( "Failed to remove document " + toBeRemovedId + " from menu " + menuIndex
                                        + " on document "
                                        + menuDocumentId );
        }
    }

    private void setDocumentCategoriesFromForm( HttpServletRequest req, DocumentDomainObject document,
                                                DocumentMapper documentMapper ) {
        document.removeAllCategories();
        String[] categoryIdStrings = req.getParameterValues( "categories" );
        for ( int i = 0; null != categoryIdStrings && i < categoryIdStrings.length; i++ ) {
            try {
                int categoryId = Integer.parseInt( categoryIdStrings[i] );
                CategoryDomainObject categoryDomainObject = documentMapper.getCategoryById( categoryId );
                document.addCategory( categoryDomainObject );
            } catch ( NumberFormatException nfe ) {
                // Illegal category-id, or none selected.
            }
        }
    }

    private static void setSectionsForDocument( IMCServiceInterface service, int metaId,
                                                SectionDomainObject[] sections ) {
        removeAllSectionsFromDocument( service, metaId );
        for ( int i = 0; null != sections && i < sections.length; i++ ) {
            SectionDomainObject section = sections[i];
            addSectionToDocument( service, metaId, section.getId() );
        }
    }

    private static HashSet sprocGetDocTypesForUser( IMCServiceInterface imcref, UserDomainObject user,
                                                    String parent_meta_id, String lang_prefix ) {
        String[] user_dt = imcref.sqlProcedure( SPROC_GET_DOC_TYPES_FOR_USER,
                                                new String[]{parent_meta_id, "" + user.getUserId(), lang_prefix} );
        HashSet user_doc_types = new HashSet();
        for ( int i = 0; i < user_dt.length; i += 2 ) {
            user_doc_types.add( user_dt[i] );
        }
        return user_doc_types;
    }

    private static Map sprocGetDocumentInfo( IMCServiceInterface service, int metaId ) {

        String[] params = new String[]{String.valueOf( metaId )};
        String[] result = service.sqlProcedure( SPROC_GET_DOCUMENT_INFO, params );

        if ( 0 == result.length ) {
            return null;
        }

        Map map = new HashMap();

        map.put( "meta_id", result[0] );
        map.put( "description", result[1] );
        map.put( "doc_type", result[2] );
        map.put( "meta_headline", result[3] );
        map.put( "meta_text", result[4] );
        map.put( "meta_image", result[5] );
        map.put( "owner_id", result[6] );
        map.put( "permissions", result[7] );
        map.put( "shared", result[8] );
        map.put( "expand", result[9] );
        map.put( "show_meta", result[10] );
        map.put( "help_text_id", result[11] );
        map.put( "archive", result[12] );
        map.put( "status_id", result[13] );
        map.put( "lang_prefix", result[14] );
        map.put( "classification", result[15] );
        map.put( "date_created", result[16] );
        map.put( "date_modified", result[17] );
        map.put( "sort_position", result[18] );
        map.put( "menu_position", result[19] );
        map.put( "disable_search", result[20] );
        map.put( "target", result[21] );
        map.put( "frame_name", result[22] );
        map.put( "activated_datetime", result[23] );
        map.put( "archived_datetime", result[24] );
        map.put( "publisher_id", result[25] );

        return map;
    }

    /**
     * @return the filename for a fileupload-internalDocument, or null if the internalDocument isn't a fileupload-docuemnt. *
     */
    private static String sprocGetFilename( IMCServiceInterface service, int meta_id ) {
        String[] params = new String[]{String.valueOf( meta_id )};
        return service.sqlProcedureStr( SPROC_GET_FILE_NAME, params );
    }

    private String[] sprocGetText( int meta_id, int no ) {
        String[] params = new String[]{"" + meta_id, "" + no};
        String[] results = service.sqlProcedure( SPROC_GET_TEXT, params, false );
        return results;
    }

    // todo make sure all sproc and sql mehtods are private
    private static String[] sprocGetUserPermissionSet( IMCServiceInterface service, int metaId, int userId ) {
        String[] sqlParams = {String.valueOf( metaId ), String.valueOf( userId )};
        String[] sqlResult = service.sqlProcedure( SPROC_GET_USER_PERMISSION_SET, sqlParams );
        return sqlResult;
    }

    private static void sprocUpdateInsertText( IMCServiceInterface service, int meta_id, int txt_no,
                                               TextDocumentTextDomainObject text, String textstring ) {
        String[] params = new String[]{"" + meta_id, "" + txt_no, "" + text.getType(), textstring};
        service.sqlUpdateProcedure( SPROC_INSERT_TEXT, params );
    }

    private int sqlCreateNewRowInMetaCopyParentData( IMCServiceInterface service, int parentId ) {
        final String columnsToBeCopied = "description,doc_type,meta_headline,meta_text,meta_image,owner_id,permissions,shared,expand,show_meta,help_text_id,archive,status_id,lang_prefix,classification,date_created,date_modified,sort_position,menu_position,disable_search,target,frame_name,activate,activated_datetime,archived_datetime";

        String metaId = service.sqlQueryStr( "insert into meta (" + columnsToBeCopied + ")\n" + "select "
                                             + columnsToBeCopied
                                             + " from meta where meta_id = ?\n"
                                             + "select @@IDENTITY",
                                             new String[]{"" + parentId} );
        return Integer.parseInt( metaId );
    }

    private static void sqlInsertIntoTemplateInfoIntoTextDocs( IMCServiceInterface imcref, String meta_id,
                                                               String[] temp ) {
        String templateId = temp[0];
        String sortOrder = temp[1];
        String groupId = temp[2];
        String defaultTemplate1 = temp[3];
        String defaultTemplate2 = temp[4];

        String sqlStr = "insert into text_docs (meta_id,template_id,sort_order,group_id,default_template_1,default_template_2)\n"
                        + "values (?, ?, ?, ?, ?, ?)";
        imcref.sqlUpdateQuery( sqlStr,
                               new String[]{
                                   meta_id, templateId, sortOrder, groupId, defaultTemplate1, defaultTemplate2
                               } );
    }

    public String[] getKeywords( int meta_id ) {
        String sqlStr;
        sqlStr =
        "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = ?";
        String[] keywords = service.sqlQuery( sqlStr, new String[]{"" + meta_id} );
        return keywords;
    }

    private static String[] sqlSelectTemplateInfoFromTextDocs( IMCServiceInterface imcref, String parent_meta_id ) {
        String temp[] = imcref.sqlQuery(
                "select template_id, sort_order,group_id,default_template_1,default_template_2 from text_docs where meta_id = ?",
                new String[]{parent_meta_id} );
        return temp;
    }

    private void sqlUpdateCreatedDate( int metaId, Date dateTime ) {
        String dateTimeStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( dateTime );
        service.sqlUpdateQuery( "update meta set date_created = ? where meta_id = ?",
                                new String[]{dateTimeStr, "" + metaId} );
    }

    private void sqlUpdateDocType( IMCServiceInterface service, int metaId, int docType ) {
        service.sqlUpdateQuery( "update meta set doc_type = ? where meta_id = ?",
                                new String[]{"" + docType, "" + metaId} );
    }

    private static void sqlUpdateMeta( IMCServiceInterface service, int meta_id, Date activatedDatetime,
                                       Date archivedDateTime, Date createdDatetime, String headline, String image,
                                       Date modifiedDateTime, String target, String text, boolean isArchived,
                                       String language, UserDomainObject publisher ) {

        StringBuffer sqlStr = new StringBuffer( "update meta set " );

        ArrayList sqlUpdateColumns = new ArrayList();
        ArrayList sqlUpdateValues = new ArrayList();

        makeDateSQL( "activated_datetime", activatedDatetime, sqlUpdateColumns, sqlUpdateValues );
        makeDateSQL( "archived_datetime", archivedDateTime, sqlUpdateColumns, sqlUpdateValues );
        makeDateSQL( "date_created", createdDatetime, sqlUpdateColumns, sqlUpdateValues );
        String headlineThatFitsInDB = headline.substring( 0,
                                                          Math.min( headline.length(), META_HEADLINE_MAX_LENGTH - 1 ) );
        makeStringSQL( "meta_headline", headlineThatFitsInDB, sqlUpdateColumns, sqlUpdateValues );
        makeStringSQL( "meta_image", image, sqlUpdateColumns, sqlUpdateValues );
        makeDateSQL( "date_modified", modifiedDateTime, sqlUpdateColumns, sqlUpdateValues );
        makeStringSQL( "target", target, sqlUpdateColumns, sqlUpdateValues );
        String textThatFitsInDB = text.substring( 0, Math.min( text.length(), META_TEXT_MAX_LENGTH - 1 ) );
        makeStringSQL( "meta_text", textThatFitsInDB, sqlUpdateColumns, sqlUpdateValues );
        makeStringSQL( "lang_prefix", language, sqlUpdateColumns, sqlUpdateValues );
        makeBooleanSQL( "archive", isArchived, sqlUpdateColumns, sqlUpdateValues );
        makeIntSQL( "publisher_id", ( publisher == null ? null : new Integer( publisher.getUserId() ) ), sqlUpdateColumns,
                    sqlUpdateValues );

        // todo: Remove from the meta table all collumns that are not used.
        // Candidates: All not used above.
        sqlStr.append( StringUtils.join( sqlUpdateColumns.iterator(), "," ) );
        sqlStr.append( " where meta_id = ?" );
        sqlUpdateValues.add( "" + meta_id );
        service.sqlUpdateQuery( sqlStr.toString(),
                                (String[])sqlUpdateValues.toArray( new String[sqlUpdateValues.size()] ) );
    }

    /**
     * Set the modified datetime of a document to the given date
     *
     * @param document The id of the document
     * @param date     The datetime to set
     */
    private void touchDocument( DocumentDomainObject document, java.util.Date date ) {
        SimpleDateFormat dateformat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String sqlStr = "update meta set date_modified = ? where meta_id = ?";
        service.sqlUpdateQuery( sqlStr, new String[]{dateformat.format( date ), "" + document.getMetaId()} );
        indexDocument( document );
    }

    /**
     * Retrieve the texts for a internalDocument
     *
     * @param meta_id The id of the internalDocument.
     * @return A Map (String -> TextDocumentTextDomainObject) with all the  texts in the internalDocument.
     */
    public Map getTexts( int meta_id ) {

        // Now we'll get the texts from the db.
        String[] texts = service.sqlProcedure( "GetTexts", new String[]{String.valueOf( meta_id )}, false );
        Map textMap = new HashMap();
        Iterator it = Arrays.asList( texts ).iterator();
        while ( it.hasNext() ) {
            try {
                it.next(); // the key, not needed
                String txt_no = (String)it.next();
                int txt_type = Integer.parseInt( (String)it.next() );
                String value = (String)it.next();
                textMap.put( txt_no, new TextDocumentTextDomainObject( value, txt_type ) );
            } catch ( NumberFormatException e ) {
                log.error( "SProc 'GetTexts " + meta_id + "' returned a non-number where a number was expected.", e );
                return null;
            }
        }
        return textMap;
    }

    private boolean userIsSuperAdminOrHasPermissionSetId( int documentId, UserDomainObject user,
                                                          int[] wantedPermissionSetIds ) {
        boolean result = false;

        boolean userHasSuperAdminRole = imcmsAAUM.hasSuperAdminRole( user );

        if ( userHasSuperAdminRole ) {
            result = true;
        } else {

            String[] perms = sprocGetUserPermissionSet( service, documentId, user.getUserId() );

            if ( perms.length > 0 ) {
                int userPermissionSetId = Integer.parseInt( perms[0] );

                result = arrayContains( wantedPermissionSetIds, userPermissionSetId );
            }
        }
        return result;
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex ;
    }

    public static class DocumentAlreadyInMenuException extends Exception {

        DocumentAlreadyInMenuException( String message ) {
            super( message );
        }
    }

}

