package imcode.server.document;

import com.imcode.imcms.flow.DocumentPageFlow;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.LanguageMapper;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.index.AutorebuildingDirectoryIndex;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.*;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.IdNamePair;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.NDC;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentMapper {

    private static final int UNLIMITED_MAX_CATEGORY_CHOICES = 0;

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

    // Stored procedure names used in this class
    // todo make sure all these is only used in one sprocMethod
    private static final String SPROC_SECTION_GET_INHERIT_ID = "SectionGetInheritId";
    private static final String SPROC_GET_DOCUMENT_INFO = "GetDocumentInfo";
    private static final String SPROC_GET_TEXT = "GetText";
    private static final String SPROC_UPDATE_PARENTS_DATE_MODIFIED = "UpdateParentsDateModified";
    private static final String SPROC_SECTION_GET_ALL = "SectionGetAll";
    private static final String SPROC_GET_DOC_TYPES_FOR_USER = "GetDocTypesForUser";

    private ImcmsAuthenticatorAndUserMapper imcmsAAUM;

    private IMCServiceInterface service;
    private DocumentIndex documentIndex;

    private static final String TEMPLATE__STATUS_NEW = "status/new.frag";
    private static final String TEMPLATE__STATUS_DISAPPROVED = "status/disapproved.frag";
    private static final String TEMPLATE__STATUS_PUBLISHED = "status/published.frag";
    private static final String TEMPLATE__STATUS_UNPUBLISHED = "status/unpublished.frag";
    private static final String TEMPLATE__STATUS_ARCHIVED = "status/archived.frag";
    private static final String TEMPLATE__STATUS_APPROVED = "status/approved.frag";

    public DocumentMapper( IMCServiceInterface service, ImcmsAuthenticatorAndUserMapper imcmsAAUM ) {
        this.service = service;
        this.imcmsAAUM = imcmsAAUM;
        File webAppPath = WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath();
        File indexDirectory = new File( webAppPath, "WEB-INF/index" );

        this.documentIndex = new AutorebuildingDirectoryIndex( indexDirectory );
    }

    private boolean userCanCreateDocumentOfTypeIdFromParent( UserDomainObject user, int documentTypeId,
                                                             DocumentDomainObject parent ) {
        TextDocumentPermissionSetDomainObject documentPermissionSet = (TextDocumentPermissionSetDomainObject)getDocumentPermissionSetForUser( parent, user );
        return ArrayUtils.contains( documentPermissionSet.getAllowedDocumentTypeIds(), documentTypeId );
    }

    private boolean userIsSuperAdminOrFullAdminOnDocument( UserDomainObject user, DocumentDomainObject parent ) {
        return user.isSuperAdmin()
               || userHasAtLeastPermissionSetIdOnDocument( user, DocumentPermissionSetDomainObject.TYPE_ID__FULL, parent );
    }

    public DocumentPermissionSetDomainObject getDocumentPermissionSetForUser( DocumentDomainObject document,
                                                                              UserDomainObject user ) {
        int permissionSetId = getDocumentPermissionSetIdForUser( document, user );
        switch ( permissionSetId ) {
            case DocumentPermissionSetDomainObject.TYPE_ID__FULL:
                return DocumentPermissionSetDomainObject.FULL;
            case DocumentPermissionSetDomainObject.TYPE_ID__READ:
                return DocumentPermissionSetDomainObject.READ;
            case DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1:
                return document.getPermissionSetForRestrictedOne();
            case DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2:
                return document.getPermissionSetForRestrictedTwo();
            default:
                return null;
        }
    }

    public int getDocumentPermissionSetIdForUser( DocumentDomainObject document, UserDomainObject user ) {
        if (null == document) {
            return DocumentPermissionSetDomainObject.TYPE_ID__NONE ;
        }
        if ( user.isSuperAdmin() ) {
            return DocumentPermissionSetDomainObject.TYPE_ID__FULL;
        }
        Map rolesMappedToPermissionSetIds = document.getRolesMappedToPermissionSetIds();
        RoleDomainObject[] usersRoles = user.getRoles();
        int mostPrivilegedPermissionSetIdFoundYet = DocumentPermissionSetDomainObject.TYPE_ID__NONE;
        for ( int i = 0; i < usersRoles.length; i++ ) {
            RoleDomainObject usersRole = usersRoles[i];
            Integer permissionSetId = (Integer)rolesMappedToPermissionSetIds.get( usersRole );
            if ( null != permissionSetId && permissionSetId.intValue() < mostPrivilegedPermissionSetIdFoundYet ) {
                mostPrivilegedPermissionSetIdFoundYet = permissionSetId.intValue();
                if ( DocumentPermissionSetDomainObject.TYPE_ID__FULL == mostPrivilegedPermissionSetIdFoundYet ) {
                    break;
                }
            }
        }
        return mostPrivilegedPermissionSetIdFoundYet;
    }

    public DocumentDomainObject createDocumentOfTypeFromParent( int documentTypeId, final DocumentDomainObject parent,
                                                                UserDomainObject user ) {
        if ( !userCanCreateDocumentOfTypeIdFromParent( user, documentTypeId, parent ) ) {
            return null;
        }
        DocumentDomainObject newDocument;
        try {
            if ( DocumentDomainObject.DOCTYPE_TEXT == documentTypeId ) {
                newDocument = (DocumentDomainObject)parent.clone();
                TextDocumentDomainObject newTextDocument = (TextDocumentDomainObject)newDocument;
                newTextDocument.removeAllTexts();
                newTextDocument.removeAllImages();
                newTextDocument.removeAllIncludes();
                newTextDocument.removeAllMenus();
                int permissionSetId = getDocumentPermissionSetIdForUser( parent, user );
                TemplateMapper templateMapper = service.getTemplateMapper();
                TemplateDomainObject template = null;
                if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 == permissionSetId ) {
                    template = templateMapper.getTemplateById( newTextDocument.getDefaultTemplateIdForRestrictedPermissionSetOne() );
                } else if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2 == permissionSetId ) {
                    template = templateMapper.getTemplateById( newTextDocument.getDefaultTemplateIdForRestrictedPermissionSetTwo() );
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
        newDocument.setCreator( user );
        newDocument.setStatus( DocumentDomainObject.STATUS_NEW );
        newDocument.setHeadline( "" );
        newDocument.setMenuText( "" );
        newDocument.setMenuImage( "" );
        newDocument.setPublicationStartDatetime( new Date() );
        newDocument.setArchivedDatetime( null );
        newDocument.setPublicationEndDatetime( null );
        return newDocument;
    }

    /**
     * Delete childs from a menu.
     */
    public void deleteChilds( DocumentDomainObject document, int menu, UserDomainObject user,
                              String[] childsThisMenu ) {
        StringBuffer childStr = new StringBuffer( "[" );
        // create a db connection an get meta data

        for ( int i = 0; i < childsThisMenu.length; i++ ) {
            int childId = Integer.parseInt( childsThisMenu[i] );
            DocumentDomainObject child = getDocument( childId );
            removeDocumentFromMenu( user, document, menu, child );
            childStr.append( childsThisMenu[i] );
            if ( i < childsThisMenu.length - 1 ) {
                childStr.append( ", " );
            }
        }
        childStr.append( "]" );
        documentIndex.indexDocument( document );
    }

    public CategoryDomainObject[] getAllCategoriesOfType( CategoryTypeDomainObject categoryType ) {
        String sqlQuery = "SELECT categories.category_id, categories.name, categories.description, categories.image\n"
                          + "FROM categories\n"
                          + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                          + "WHERE categories.category_type_id = ?";
        String[][] sqlResult = service.sqlQueryMulti( sqlQuery, new String[]{"" + categoryType.getId()} );
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
        String[][] sqlResult = service.sqlQueryMulti( sqlQuery, new String[0] );

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
        String[][] sqlRows = service.sqlProcedureMulti( SPROC_SECTION_GET_ALL, new String[0] );
        SectionDomainObject[] allSections = new SectionDomainObject[sqlRows.length];
        for ( int i = 0; i < sqlRows.length; i++ ) {
            int sectionId = Integer.parseInt( sqlRows[i][0] );
            String sectionName = sqlRows[i][1];
            allSections[i] = new SectionDomainObject( sectionId, sectionName );
        }
        return allSections;
    }

    public CategoryDomainObject getCategory( CategoryTypeDomainObject categoryType, String categoryName ) {
        String sqlQuery = "SELECT categories.category_id, categories.name, categories.description, categories.image\n"
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

        String[] categorySqlResult = service.sqlQuery( sqlQuery, new String[]{"" + categoryId} );

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
        String[] sqlResult = service.sqlQuery( sqlStr, new String[]{categoryTypeName} );

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
        String[] sqlResult = service.sqlQuery( sqlStr, new String[]{"" + categoryTypeId} );

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
        service.sqlUpdateQuery( sqlstr, new String[]{categoryType.getId() + ""} );
    }

    public CategoryTypeDomainObject addCategoryTypeToDb( String name, int max_choices ) {
        String sqlstr = "insert into category_types (name, max_choices) values(?,?) SELECT @@IDENTITY";
        String newId = service.sqlQueryStr( sqlstr, new String[]{name, max_choices + ""} );
        return getCategoryTypeById( Integer.parseInt( newId ) );
    }

    public void updateCategoryType( CategoryTypeDomainObject categoryType ) {
        String sqlstr = "update category_types set name= ?, max_choices= ?  where category_type_id = ? ";
        service.sqlUpdateQuery( sqlstr, new String[]{
            categoryType.getName(), categoryType.getMaxChoices() + "", categoryType.getId() + ""
        } );
    }

    public CategoryDomainObject addCategoryToDb( int categoryTypeId, String name, String description, String image ) {
        String sqlstr = "insert into categories  (category_type_id, name, description, image) values(?,?,?,?) SELECT @@IDENTITY";
        String newId = service.sqlQueryStr( sqlstr, new String[]{
            categoryTypeId + "", name, description, image
        } );
        return getCategoryById( Integer.parseInt( newId ) );
    }

    public void updateCategory( CategoryDomainObject category ) {
        String sqlstr = "update categories set category_type_id = ?, name= ?, description = ?, image = ?  where category_id = ? ";
        service.sqlUpdateQuery( sqlstr, new String[]{
            category.getType().getId() + "", category.getName(), category.getDescription(), category.getImageUrl(),
            category.getId() + ""
        } );
    }

    public void deleteCategoryFromDb( CategoryDomainObject category ) {
        String sqlstr = "delete from categories where category_id = ?";
        service.sqlUpdateQuery( sqlstr, new String[]{category.getId() + ""} );
    }

    public DocumentDomainObject getDocument( int metaId ) {
        NDC.push( "getDocument" );

        String[] result = sprocGetDocumentInfo( metaId );

        if ( 0 == result.length ) {
            return null;
        }
        DocumentDomainObject document = getDocumentFromSqlResultRow( result );
        document.accept( new DocumentInitializingVisitor() );

        NDC.pop();
        return document;
    }

    public void initLazilyLoadedDocumentAttributes( DocumentDomainObject document ) {

        document.setSections( getSections( document.getId() ) );

        document.setKeywords( getKeywords( document.getId() ) );

        DocumentPermissionSetMapper documentPermissionSetMapper = new DocumentPermissionSetMapper( service );
        document.setPermissionSetForRestrictedOne( documentPermissionSetMapper.getPermissionSetRestrictedOne( document ) );
        document.setPermissionSetForRestrictedTwo( documentPermissionSetMapper.getPermissionSetRestrictedTwo( document ) );

        document.setPermissionSetForRestrictedOneForNewDocuments( documentPermissionSetMapper.getPermissionSetRestrictedOneForNewDocuments( document ) );
        document.setPermissionSetForRestrictedTwoForNewDocuments( documentPermissionSetMapper.getPermissionSetRestrictedTwoForNewDocuments( document ) );

    }

    public void initLazilyLoadedDocumentCategories( DocumentDomainObject document ) {

        addCategoriesFromDatabaseToDocument( document );

    }

    public void initLazilyLoadedRolesMappedToDocumentPermissionSetIds( DocumentDomainObject document ) {

        String[][] sprocResult = service.sqlQueryMulti( "SELECT  r.role_id, r.role_name, r.admin_role, rr.set_id\n"
                                                        + "FROM  roles AS r, roles_rights AS rr\n"
                                                        + "WHERE rr.role_id = r.role_id AND rr.meta_id = ?",
                                                        new String[]{"" + document.getId()} );

        for ( int i = 0; i < sprocResult.length; ++i ) {
            int roleId = Integer.parseInt( sprocResult[i][0] );
            String roleName = sprocResult[i][1];
            int adminRoleId = Integer.parseInt( sprocResult[i][2] );
            RoleDomainObject role = new RoleDomainObject( roleId, roleName, adminRoleId );

            int rolePermissionSetId = Integer.parseInt( sprocResult[i][3] );
            document.setPermissionSetIdForRole( role, rolePermissionSetId );
        }

    }

    public void initLazilyLoadedTextDocumentAttributes( TextDocumentDomainObject document ) {
        // all from the table text_doc
        String[] sqlResult = service.sqlQuery( "SELECT template_id, group_id, default_template_1, default_template_2 FROM text_docs WHERE meta_id = ?",
                                               new String[]{String.valueOf( document.getId() )} );
        if ( sqlResult.length >= 4 ) {
            int template_id = Integer.parseInt( sqlResult[0] );
            int group_id = Integer.parseInt( sqlResult[1] );
            int defaultTemplateIdForRestrictedPermissionSetOne = Integer.parseInt( sqlResult[2] );
            int defaultTemplateIdForRestrictedPermissionSetTwo = Integer.parseInt( sqlResult[3] );

            TemplateDomainObject template = service.getTemplateMapper().getTemplateById( template_id );
            document.setTemplate( template );
            document.setTemplateGroupId( group_id );
            document.setDefaultTemplateIdForRestrictedPermissionSetOne( defaultTemplateIdForRestrictedPermissionSetOne );
            document.setDefaultTemplateIdForRestrictedPermissionSetTwo( defaultTemplateIdForRestrictedPermissionSetTwo );
        }

        setDocumentTexts( document );
        setDocumentImages( document );
        setDocumentIncludes( document );
        setDocumentMenus( document );

    }

    private void setDocumentMenus( TextDocumentDomainObject document ) {
        String sqlSelectDocumentMenus = "SELECT menus.menu_id, menu_index, sort_order, to_meta_id, manual_sort_order, tree_sort_index FROM menus,childs WHERE menus.menu_id = childs.menu_id AND meta_id = ? ORDER BY menu_index";
        String[][] sqlRows = service.sqlQueryMulti( sqlSelectDocumentMenus, new String[]{"" + document.getId()} );
        MenuDomainObject menu = null;
        int previousMenuIndex = 0;
        for ( int i = 0; i < sqlRows.length; i++ ) {
            String[] sqlRow = sqlRows[i];
            int menuId = Integer.parseInt( sqlRow[0] );
            int menuIndex = Integer.parseInt( sqlRow[1] );
            int sortOrder = Integer.parseInt( sqlRow[2] );
            int childId = Integer.parseInt( sqlRow[3] );
            int manualSortKey = Integer.parseInt( sqlRow[4] );
            String treeSortKey = sqlRow[5];
            if ( null == menu || menuIndex != previousMenuIndex ) {
                previousMenuIndex = menuIndex;
                menu = new MenuDomainObject( menuId, sortOrder );
                document.setMenu( menuIndex, menu );
            }
            menu.addMenuItem( new MenuItemDomainObject( getDocument( childId ), new Integer( manualSortKey ), treeSortKey ) );
        }
    }

    private void setDocumentIncludes( TextDocumentDomainObject document ) {
        String sqlSelectDocumentIncludes = "SELECT include_id, included_meta_id FROM includes WHERE meta_id = ?";
        String[][] documentIncludesSqlResult = service.sqlQueryMulti( sqlSelectDocumentIncludes, new String[]{
            "" + document.getId()
        } );
        for ( int i = 0; i < documentIncludesSqlResult.length; i++ ) {
            String[] documentIncludeSqlRow = documentIncludesSqlResult[i];
            int includeIndex = Integer.parseInt( documentIncludeSqlRow[0] );
            int includedDocumentId = Integer.parseInt( documentIncludeSqlRow[1] );
            document.setInclude( includeIndex, includedDocumentId );
        }
    }

    private void setDocumentImages( TextDocumentDomainObject document ) {
        document.setImages( getDocumentImages( document ) );
    }

    private void setDocumentTexts( TextDocumentDomainObject document ) {
        String sqlSelectTexts = "SELECT name, text, type FROM texts WHERE meta_id = ?";
        String[][] sqlTextsResult = service.sqlQueryMulti( sqlSelectTexts, new String[]{"" + document.getId()} );
        for ( int i = 0; i < sqlTextsResult.length; i++ ) {
            String[] sqlTextsRow = sqlTextsResult[i];
            int textIndex = Integer.parseInt( sqlTextsRow[0] );
            String text = sqlTextsRow[1];
            int textType = Integer.parseInt( sqlTextsRow[2] );
            document.setText( textIndex, new TextDomainObject( text, textType ) );
        }
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
    private SectionDomainObject[] getSections( int meta_id ) {
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

    public boolean userHasPermissionToSearchDocument( UserDomainObject searchingUser, DocumentDomainObject document ) {
        boolean searchingUserHasPermissionToFindDocument = false;
        if ( document.isSearchDisabled() ) {
            if ( searchingUser.isSuperAdmin() ) {
                searchingUserHasPermissionToFindDocument = true;
            }
        } else {
            if ( document.isPublished() ) {
                searchingUserHasPermissionToFindDocument = userHasAtLeastDocumentReadPermission( searchingUser, document );
            } else {
                searchingUserHasPermissionToFindDocument = userHasMoreThanReadPermissionOnDocument( searchingUser, document );
            }
        }
        return searchingUserHasPermissionToFindDocument;
    }

    public boolean userHasAtLeastDocumentReadPermission( UserDomainObject user, DocumentDomainObject document ) {
        return userIsSuperAdminOrHasAtLeastPermissionSetIdOnDocument( user, DocumentPermissionSetDomainObject.TYPE_ID__READ, document );
    }

    public boolean userHasMoreThanReadPermissionOnDocument( UserDomainObject user, DocumentDomainObject document ) {
        return userIsSuperAdminOrHasAtLeastPermissionSetIdOnDocument( user, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, document );
    }

    public boolean userHasPermissionToAddDocumentToAnyMenu( UserDomainObject user, DocumentDomainObject document ) {
        return user.isSuperAdmin()
               || userHasMoreThanReadPermissionOnDocument( user, document )
               || document.isLinkableByOtherUsers();
    }

    public void removeInclusion( int includingMetaId, int includeIndex ) {
        sprocDeleteInclude( service, includingMetaId, includeIndex );
    }

    public void saveNewDocument( DocumentDomainObject document, UserDomainObject user )
            throws MaxCategoryDomainObjectsOfTypeExceededException {

        if ( !userHasMoreThanReadPermissionOnDocument( user, document ) ) {
            return; // TODO: More specific check needed. Throw exception ?
        }

        int newMetaId = sqlInsertIntoMeta( document );

        if ( !userIsSuperAdminOrFullAdminOnDocument( user, document ) ) {
            document.setPermissionSetForRestrictedOne( document.getPermissionSetForRestrictedOneForNewDocuments() );
            document.setPermissionSetForRestrictedTwo( document.getPermissionSetForRestrictedTwoForNewDocuments() );
        }

        document.setId( newMetaId );

        document.accept(new DocumentCreatingVisitor(user)) ;

        saveDocument( document, user );
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
        sqlColumnValues.add( makeSqlStringFromBoolean( document.isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo() ) );
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

        String metaIdStr = service.sqlQueryStr( sqlStr, (String[])sqlColumnValues.toArray( new String[sqlColumnValues.size()] ) );
        final int metaId = Integer.parseInt( metaIdStr );
        return metaId;
    }

    private String makeSqlStringFromBoolean( final boolean bool ) {
        return bool ? "1" : "0";
    }

    public void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws MaxCategoryDomainObjectsOfTypeExceededException {

        if ( !userHasMoreThanReadPermissionOnDocument( user, document ) ) {
            return; // TODO: More specific check needed. Throw exception ?
        }

        try {
            checkMaxDocumentCategoriesOfType( document );

            boolean modifiedDatetimeChanged = !document.getLastModifiedDatetime().equals( document.getModifiedDatetime() );
            if ( !modifiedDatetimeChanged ) {
                document.setModifiedDatetime( service.getCurrentDate() );
            }

            sqlUpdateMeta( document );

            updateDocumentSections( document.getId(), document.getSections() );

            updateDocumentCategories( document );

            updateDocumentRolePermissions( document );

            updateDocumentKeywords( document.getId(), document.getKeywords() );

            DocumentPermissionSetMapper documentPermissionSetMapper = new DocumentPermissionSetMapper( service );
            documentPermissionSetMapper.saveRestrictedDocumentPermissionSets( document );

            document.accept(new DocumentSavingVisitor(user)) ;
        } finally {
            documentIndex.indexDocument( document );
        }
    }

    private void updateDocumentRolePermissions( DocumentDomainObject document ) {
        for ( Iterator it = document.getRolesMappedToPermissionSetIds().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry rolePermissionTuple = (Map.Entry)it.next();
            RoleDomainObject role = (RoleDomainObject)rolePermissionTuple.getKey();
            int permissionSetId = ( (Integer)rolePermissionTuple.getValue() ).intValue();
            sprocSetRoleDocPermissionSetId( service, document.getId(), role.getId(), permissionSetId );
        }
        // TODO Restricted One and Two (Bug 1443)
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
        service.sqlUpdateQuery( "INSERT INTO document_categories (meta_id, category_id) VALUES(?,?)",
                                new String[]{"" + document.getId(), "" + categoryId} );
    }

    public String[] getAllDocumentsOfOneCategory( CategoryDomainObject category ) {

        String sqlstr = "select meta_id from document_categories where category_id = ? ";
        String[] res = service.sqlQuery( sqlstr, new String[]{category.getId() + ""} );

        return res;
    }

    private void removeAllCategoriesFromDocument( DocumentDomainObject document ) {
        service.sqlUpdateQuery( "DELETE FROM document_categories WHERE meta_id = ?",
                                new String[]{"" + document.getId()} );
    }

    public void deleteOneCategoryFromDocument( DocumentDomainObject document, CategoryDomainObject category ) {
        service.sqlUpdateQuery( "DELETE FROM document_categories WHERE meta_id = ? and category_id = ?",
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
        makeBooleanSqlUpdateClause( "permissions", document.isPermissionSetOneIsMorePrivilegedThanPermissionSetTwo(), sqlUpdateColumns, sqlUpdateValues );
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
        service.sqlUpdateQuery( sqlStr.toString(),
                                (String[])sqlUpdateValues.toArray( new String[sqlUpdateValues.size()] ) );
    }

    public void setInclude( int includingMetaId, int includeIndex, int includedMetaId ) {
        sprocSetInclude( service, includingMetaId, includeIndex, includedMetaId );
    }

    public static void sprocDeleteInclude( IMCServiceInterface imcref, int including_meta_id, int include_id ) {
        imcref.sqlUpdateProcedure( "DeleteInclude", new String[]{"" + including_meta_id, "" + include_id} );
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
        int keywordId = Integer.parseInt( service.sqlQueryStr( "SELECT class_id FROM classification WHERE code = ?", new String[]{
            keyword
        } ) );
        service.sqlUpdateQuery( "INSERT INTO meta_classification (meta_id, class_id) VALUES(?,?)",
                                new String[]{"" + meta_id, "" + keywordId} );
    }

    private void deleteUnusedKeywords() {
        service.sqlUpdateQuery( "DELETE FROM classification WHERE class_id NOT IN (SELECT class_id FROM meta_classification)",
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

    public static void sprocUpdateParentsDateModified( IMCServiceInterface imcref, int meta_id ) {
        imcref.sqlUpdateProcedure( SPROC_UPDATE_PARENTS_DATE_MODIFIED, new String[]{"" + meta_id} );
    }

    private void addCategoriesFromDatabaseToDocument( DocumentDomainObject document ) {
        String[][] categories = service.sqlQueryMulti( "SELECT categories.category_id, categories.name, categories.image, categories.description, category_types.category_type_id, category_types.name, category_types.max_choices"
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

    private static void addSectionToDocument( IMCServiceInterface imcref, int metaId, int sectionId ) {
        imcref.sqlUpdateQuery( "INSERT INTO meta_section VALUES(?,?)", new String[]{"" + metaId, "" + sectionId} );
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

    private static void removeAllSectionsFromDocument( IMCServiceInterface imcref, int metaId ) {
        imcref.sqlUpdateQuery( "DELETE FROM meta_section WHERE meta_id = ?", new String[]{"" + metaId} );
    }

    public void removeDocumentFromMenu( UserDomainObject user, DocumentDomainObject menuDocument,
                                        int menuIndex, DocumentDomainObject toBeRemoved ) {
        String sqlStr = "delete from childs\n" + "where to_meta_id = ?\n"
                        + "and menu_id = (SELECT menu_id FROM menus WHERE meta_id = ? AND menu_index = ?)";

        service.sqlUpdateQuery( sqlStr,
                                new String[]{
                                    "" + toBeRemoved.getId(), "" + menuDocument.getId(), ""
                                                                                         + menuIndex
                                } );

        documentIndex.indexDocument( toBeRemoved );
    }

    private void updateDocumentSections( int metaId,
                                         SectionDomainObject[] sections ) {
        removeAllSectionsFromDocument( service, metaId );
        for ( int i = 0; null != sections && i < sections.length; i++ ) {
            SectionDomainObject section = sections[i];
            addSectionToDocument( service, metaId, section.getId() );
        }
    }

    private String[] sprocGetDocumentInfo( int metaId ) {
        String[] result = service.sqlProcedure( SPROC_GET_DOCUMENT_INFO, new String[]{String.valueOf( metaId )} );
        return result;
    }

    private DocumentDomainObject getDocumentFromSqlResultRow( String[] result ) {
        final int documentTypeId = Integer.parseInt( result[1] );
        DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId( documentTypeId );

        document.setId( Integer.parseInt( result[0] ) );
        document.setHeadline( result[2] );
        document.setMenuText( result[3] );
        document.setMenuImage( result[4] );
        document.setCreator( imcmsAAUM.getUser( Integer.parseInt( result[5] ) ) );
        document.setPermissionSetOneIsMorePrivilegedThanPermissionSetTwo( getBooleanFromSqlResultString( result[6] ) );
        document.setLinkableByOtherUsers( getBooleanFromSqlResultString( result[7] ) );
        document.setVisibleInMenusForUnauthorizedUsers( getBooleanFromSqlResultString( result[8] ) );
        document.setLanguageIso639_2( LanguageMapper.getAsIso639_2OrDefaultLanguage( result[9], service ) );
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING );
        document.setCreatedDatetime( parseDateFormat( dateFormat, result[10] ) );
        Date modifiedDatetime = parseDateFormat( dateFormat, result[11] );
        document.setModifiedDatetime( modifiedDatetime );
        document.setLastModifiedDatetime( modifiedDatetime ) ;
        document.setSearchDisabled( getBooleanFromSqlResultString( result[12] ) );
        document.setTarget( result[13] );
        document.setArchivedDatetime( parseDateFormat( dateFormat, result[14] ) );
        String publisherIdStr = result[15];
        if ( null != publisherIdStr ) {
            UserDomainObject publisher = imcmsAAUM.getUser( Integer.parseInt( publisherIdStr ) );
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
        String[] results = service.sqlProcedure( SPROC_GET_TEXT, params );
        return results;
    }

    private String[] getKeywords( int meta_id ) {
        String sqlStr;
        sqlStr =
        "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = ?";
        String[] keywords = service.sqlQuery( sqlStr, new String[]{"" + meta_id} );
        return keywords;
    }

    private boolean userIsSuperAdminOrHasAtLeastPermissionSetIdOnDocument( UserDomainObject user,
                                                                           int leastPrivilegedPermissionSetIdWanted,
                                                                           DocumentDomainObject document ) {
        return user.isSuperAdmin()
               || userHasAtLeastPermissionSetIdOnDocument( user, leastPrivilegedPermissionSetIdWanted, document );
    }

    private boolean userHasAtLeastPermissionSetIdOnDocument( UserDomainObject user,
                                                             int leastPrivilegedPermissionSetIdWanted,
                                                             DocumentDomainObject document ) {
        return getDocumentPermissionSetIdForUser( document, user )
               <= leastPrivilegedPermissionSetIdWanted;
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public String[][] getParentDocumentAndMenuIdsForDocument( DocumentDomainObject document ) {
        String sqlStr = "SELECT meta_id,menu_index FROM childs, menus WHERE menus.menu_id = childs.menu_id AND to_meta_id = ?";
        return service.sqlQueryMulti( sqlStr, new String[]{"" + document.getId()} );
    }

    private final static String IMAGE_SQL_COLUMNS = "name,image_name,imgurl,width,height,border,v_space,h_space,target,align,alt_text,low_scr,linkurl,type";

    private Map getDocumentImages( DocumentDomainObject document ) {
        String[][] imageRows = service.sqlQueryMulti( "select " + IMAGE_SQL_COLUMNS + " from images\n"
                                                      + "where meta_id = ?",
                                                      new String[]{"" + document.getId()} );
        Map imageMap = new HashMap();
        for ( int i = 0; i < imageRows.length; i++ ) {
            String[] imageRow = imageRows[i];
            Integer imageIndex = Integer.valueOf( imageRow[0] );
            ImageDomainObject image = createImageFromSqlResultRow( imageRow );
            imageMap.put( imageIndex, image );
        }
        return imageMap;
    }

    private ImageDomainObject createImageFromSqlResultRow( String[] sqlResult ) {
        ImageDomainObject image = new ImageDomainObject();

        image.setName( sqlResult[1] );
        image.setUrl( sqlResult[2] );
        image.setWidth( Integer.parseInt( sqlResult[3] ) );
        image.setHeight( Integer.parseInt( sqlResult[4] ) );
        image.setBorder( Integer.parseInt( sqlResult[5] ) );
        image.setVerticalSpace( Integer.parseInt( sqlResult[6] ) );
        image.setHorizontalSpace( Integer.parseInt( sqlResult[7] ) );
        image.setTarget( sqlResult[8] );
        image.setAlign( sqlResult[9] );
        image.setAlternateText( sqlResult[10] );
        image.setLowResolutionUrl( sqlResult[11] );
        image.setLinkUrl( sqlResult[12] );
        image.setType( Integer.parseInt( sqlResult[13] ) );
        return image;
    }

    public String[][] getAllMimeTypesWithDescriptions( UserDomainObject user ) {
        String sqlStr = "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id";
        String[][] mimeTypes = service.sqlQueryMulti( sqlStr, new String[]{user.getLanguageIso639_2()} );
        return mimeTypes;
    }

    public String[] getAllMimeTypes() {
        String sqlStr = "SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id";
        String[] mimeTypes = service.sqlQuery( sqlStr, new String[]{} );
        return mimeTypes;
    }

    public void addToMenu( TextDocumentDomainObject parentDocument, int parentMenuIndex,
                           DocumentDomainObject documentToAddToMenu, UserDomainObject user ) {
        parentDocument.getMenu( parentMenuIndex ).addMenuItem( new MenuItemDomainObject( documentToAddToMenu ) );
        saveDocument( parentDocument, user );
    }

    public BrowserDocumentDomainObject.Browser[] getAllBrowsers() {
        String sqlStr = "SELECT browser_id, name, value FROM browsers WHERE browser_id != 0";
        String[][] sqlResult = service.sqlQueryMulti( sqlStr, new String[0] );
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
        String[] sqlRow = service.sqlQuery( sqlStr, new String[]{"" + browserIdToGet} );
        BrowserDocumentDomainObject.Browser browser = createBrowserFromSqlRow( sqlRow );
        return browser;
    }

    private BrowserDocumentDomainObject.Browser createBrowserFromSqlRow( String[] sqlRow ) {
        int browserId = Integer.parseInt( sqlRow[0] );
        String browserName = sqlRow[1];
        int browserSpecificity = Integer.parseInt( sqlRow[2] );
        BrowserDocumentDomainObject.Browser browser = new BrowserDocumentDomainObject.Browser( browserId, browserName, browserSpecificity );
        return browser;
    }

    public void deleteDocument( DocumentDomainObject document, UserDomainObject user ) {

        documentIndex.removeDocument( document );

        //If meta_id is a file document we have to delete the file from file system
        if ( !new File( service.getFilePath(), "" + document.getId() ).delete() ) {
            new File( service.getFilePath(), document.getId() + "_se" ).delete();
        }

        // Create a db connection and execte sp DocumentDelete on meta_id
        service.sqlUpdateProcedure( "DocumentDelete", new String[]{"" + document.getId()} );
    }

    public String getStatusIconTemplate( DocumentDomainObject document, UserDomainObject user ) {
        String statusIconTemplateName ;
        if ( DocumentDomainObject.STATUS_NEW == document.getStatus() ) {
            statusIconTemplateName = TEMPLATE__STATUS_NEW;
        } else if ( DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED == document.getStatus() ) {
            statusIconTemplateName = TEMPLATE__STATUS_DISAPPROVED;
        } else if ( document.isPublishedAndNotArchived() ) {
            statusIconTemplateName = TEMPLATE__STATUS_PUBLISHED;
        } else if ( document.isNoLongerPublished() ) {
            statusIconTemplateName = TEMPLATE__STATUS_UNPUBLISHED;
        } else if ( document.isArchived() ) {
            statusIconTemplateName = TEMPLATE__STATUS_ARCHIVED;
        } else {
            statusIconTemplateName = TEMPLATE__STATUS_APPROVED;
        }
        return service.getAdminTemplate( statusIconTemplateName, user, null );
    }

    public IdNamePair[] getCreatableDocumentTypeIdsAndNamesInUsersLanguage( DocumentDomainObject document,
                                                                            UserDomainObject user ) {
        String[][] sqlRows = service.sqlProcedureMulti( SPROC_GET_DOC_TYPES_FOR_USER, new String[]{
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
        String[][] rows = service.sqlQueryMulti( "SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type", new String[]{
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

    public int[] getAllDocumentTypeIds() {
        String[] documentTypeIdStrings = service.sqlQuery( "SELECT DISTINCT doc_type FROM doc_types", new String[0] );
        int[] documentTypeIds = new int[documentTypeIdStrings.length];
        for ( int i = 0; i < documentTypeIdStrings.length; i++ ) {
            documentTypeIds[i] = Integer.parseInt( documentTypeIdStrings[i] );
        }
        return documentTypeIds;
    }

    public TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingDocument( DocumentDomainObject document ) {
        String sqlSelectMenus = "SELECT meta_id, menu_index FROM menus, childs WHERE menus.menu_id = childs.menu_id AND childs.to_meta_id = ? ORDER BY meta_id, menu_index";
        String[][] sqlRows = service.sqlQueryMulti( sqlSelectMenus, new String[]{"" + document.getId()} );
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
        String[] documentIdStrings = service.sqlQuery( sqlSelectIds, new String[]{
            "" + idRange.getMinimumInteger(), "" + idRange.getMaximumInteger()
        } );
        int[] documentIds = new int[documentIdStrings.length];
        for ( int i = 0; i < documentIdStrings.length; i++ ) {
            documentIds[i] = Integer.parseInt( documentIdStrings[i] );
        }
        return documentIds;
    }

    public int[] getAllDocumentIds() {
        String[] documentIdStrings = service.sqlQuery( "SELECT meta_id FROM meta ORDER BY meta_id", new String[0] );
        int[] documentIds = new int[documentIdStrings.length];
        for ( int i = 0; i < documentIdStrings.length; i++ ) {
            documentIds[i] = Integer.parseInt( documentIdStrings[i] );
        }
        return documentIds;
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
            ApplicationServer.getIMCServiceInterface().getDocumentMapper().saveDocument( document, user );
        }
    }

}

