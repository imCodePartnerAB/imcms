package com.imcode.imcms.api;

import imcode.server.document.*;
import imcode.server.user.UserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.server.IMCServiceInterface;

import java.io.IOException;

public class DocumentService {

    private SecurityChecker securityChecker;
    private DocumentMapper documentMapper;
    private DocumentPermissionSetMapper documentPermissionSetMapper;
    private UserAndRoleMapper userAndRoleMapper;
    private IMCServiceInterface service;

    Document wrapDocumentDomainObject(DocumentDomainObject document) {
        Document result ;

        if (document instanceof TextDocumentDomainObject) {
            result = new TextDocument( (TextDocumentDomainObject) document, service, securityChecker, this, documentMapper,
                    documentPermissionSetMapper, userAndRoleMapper );
        } else if (document instanceof UrlDocumentDomainObject) {
            result = new UrlDocument( (UrlDocumentDomainObject) document, service, securityChecker, this, documentMapper,
                    documentPermissionSetMapper, userAndRoleMapper );
        } else {
            result = new Document( document, service, securityChecker, this, documentMapper,
                    documentPermissionSetMapper, userAndRoleMapper );
        }
        return result;
    }


    public DocumentService( IMCServiceInterface service, SecurityChecker securityChecker, DocumentMapper documentMapper,
                            DocumentPermissionSetMapper documentPermissionSetMapper,
                            UserAndRoleMapper userAndRoleMapper ) {
        this.service = service;
        this.securityChecker = securityChecker;
        this.documentMapper = documentMapper;
        this.documentPermissionSetMapper = documentPermissionSetMapper;
        this.userAndRoleMapper = userAndRoleMapper;
    }

    /**
     * Used to get a document of any type. If you need a specific document, i.e. a TextDocument, use @see getTextDocument
     * instead.
     *
     * @param documentId The id number of the document requested, also somtimes known as "meta_id"
     * @return The document The type is usually a subclass to document, if there exist one.
     * @throws com.imcode.imcms.api.NoPermissionException
     *          If the current user dosen't have the rights to read this document.
     */
    public Document getDocument( int documentId ) throws NoPermissionException {
        DocumentDomainObject doc = documentMapper.getDocument( documentId );
        Document result = null;
        if( null != doc ) {
            result = wrapDocumentDomainObject(doc);
            securityChecker.hasAtLeastDocumentReadPermission( result );
        }
        return result;
    }


    /**
     * @param documentId The id number of the document requested, also somtimes known as "meta_id"
     * @return The document
     * @throws com.imcode.imcms.api.NoPermissionException
     *          If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument( int documentId ) throws NoPermissionException {
        Document doc = getDocument( documentId );
        return (TextDocument) doc;
    }

    /**
     * @param documentId The id number of the document requested, also somtimes known as "meta_id"
     * @return The document
     * @throws com.imcode.imcms.api.NoPermissionException
     *          If the current user dosen't have the rights to read this document.
     */
    public UrlDocument getUrlDocument( int documentId ) throws NoPermissionException {
        Document doc = getDocument( documentId );
        return (UrlDocument) doc;
    }

    public TextDocument createAndSaveNewTextDocument( Document parent ) throws NoPermissionException, MaxCategoriesOfTypeExceededException {
        DocumentDomainObject newDoc = createAndSaveNewDocument( DocumentDomainObject.DOCTYPE_TEXT, parent );
        return (TextDocument) getDocument( newDoc.getId() );
    }

    public UrlDocument createAndSaveNewUrlDocument( Document parent ) throws NoPermissionException, MaxCategoriesOfTypeExceededException {
        DocumentDomainObject newDoc = createAndSaveNewDocument( DocumentDomainObject.DOCTYPE_URL, parent );
        return (UrlDocument) getDocument( newDoc.getId() );
    }

    private DocumentDomainObject createAndSaveNewDocument( int doctype, Document parent ) throws MaxCategoriesOfTypeExceededException, NoPermissionException {
        securityChecker.hasEditPermission( parent.getId() );
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        DocumentDomainObject newDoc = documentMapper.createDocumentOfTypeFromParent( doctype, parent.internalDocument, user );
        try {
            documentMapper.saveNewDocument( newDoc, user );
        } catch (MaxCategoryDomainObjectsOfTypeExceededException e) {
            throw new MaxCategoriesOfTypeExceededException( e );
        }
        return newDoc;
    }

    /**
     * @deprecated Use createNewUrlDocument( int parentId ) instead.
     */
    public UrlDocument createNewUrlDocument( int parentId, int parentMenuNumber ) throws NoPermissionException {
        Document parentDocument = getDocument( parentId );
        UrlDocument newUrlDocument = null;
        try {
            newUrlDocument = createAndSaveNewUrlDocument( parentDocument );
        } catch (MaxCategoriesOfTypeExceededException e) {
            throw new RuntimeException( e );
        }
        addDocumentToMenu( parentDocument, parentMenuNumber, newUrlDocument );

        return newUrlDocument;
    }

    /**
     * @deprecated Use createNewTextDocument( int parentId ) instead.
     */
    public TextDocument createNewTextDocument( int parentId, int parentMenuNumber ) throws NoPermissionException {
        Document parentDocument = getDocument( parentId );
        TextDocument newTextDocument = null;
        try {
            newTextDocument = createAndSaveNewTextDocument( parentDocument );
        } catch (MaxCategoriesOfTypeExceededException e) {
            throw new RuntimeException( e );
        }
        addDocumentToMenu( parentDocument, parentMenuNumber, newTextDocument );

        return newTextDocument;
    }

    private void addDocumentToMenu( Document parentDocument, int parentMenuNumber, Document document ) {
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        try {
            documentMapper.addDocumentToMenu( user, parentDocument.internalDocument, parentMenuNumber, document.internalDocument );
        } catch (DocumentMapper.DocumentAlreadyInMenuException e) {
            throw new RuntimeException( e );
        }
    }

    public void saveChanges( Document document ) throws NoPermissionException, MaxCategoriesOfTypeExceededException {
        securityChecker.hasEditPermission( document );
        try {
            documentMapper.saveDocument( document.getInternal(), securityChecker.getCurrentLoggedInUser() );
        } catch (MaxCategoryDomainObjectsOfTypeExceededException e) {
            throw new MaxCategoriesOfTypeExceededException( e );
        }
    }

    public Category getCategory( CategoryType categoryType, String categoryName ) {
        // Allow everyone to get a Category. No security check.
        final CategoryDomainObject category = documentMapper.getCategory( categoryType.getInternal(), categoryName );
        if (null != category) {
            return new Category( category );
        } else {
            return null;
        }
    }

    public Category getCategory( int categoryId ) {
        // Allow everyone to get a Category. No security check.
        final CategoryDomainObject category = documentMapper.getCategoryById( categoryId );
        if (null != category) {
            return new Category( category );
        } else {
            return null;
        }
    }

    public CategoryType getCategoryType( String categoryTypeName ) {
        // Allow everyone to get a CategoryType. No security check.
        final CategoryTypeDomainObject categoryType = documentMapper.getCategoryType( categoryTypeName );
        if (null != categoryType) {
            return new CategoryType( categoryType );
        }
        return null;
    }

    public Category[] getAllCategoriesOfType( CategoryType categoryType ) {
        // Allow everyone to get a CategoryType. No security check.
        CategoryDomainObject[] categoryDomainObjects = documentMapper.getAllCategoriesOfType( categoryType.getInternal() );
        Category[] categories = new Category[categoryDomainObjects.length];
        for (int i = 0; i < categoryDomainObjects.length; i++) {
            CategoryDomainObject categoryDomainObject = categoryDomainObjects[i];
            categories[i] = new Category( categoryDomainObject );
        }
        return categories;
    }

    public CategoryType[] getAllCategoryTypes() {
        // Allow everyone to get a CategoryType. No security check.
        CategoryTypeDomainObject[] categoryTypeDomainObjects = documentMapper.getAllCategoryTypes();
        CategoryType[] categoryTypes = new CategoryType[categoryTypeDomainObjects.length];
        for (int i = 0; i < categoryTypeDomainObjects.length; i++) {
            CategoryTypeDomainObject categoryTypeDomainObject = categoryTypeDomainObjects[i];
            categoryTypes[i] = new CategoryType( categoryTypeDomainObject );
        }
        return categoryTypes;
    }

    /**
     *
     * @param name
     * @param maxChoices
     * @return The newly craeated category type.
     * @throws NoPermissionException
     * @throws CategoryTypeAlreadyExistsException
     */
    public CategoryType createNewCategoryType( String name, int maxChoices ) throws NoPermissionException, CategoryTypeAlreadyExistsException {
        securityChecker.isSuperAdmin();
        if( documentMapper.isUniqueCategoryTypeName( name ) ) {
            CategoryTypeDomainObject newCategoryTypeDO = documentMapper.addCategoryTypeToDb( name, maxChoices );
            return new CategoryType( newCategoryTypeDO );
        } else {
            throw new CategoryTypeAlreadyExistsException("A category with name " + name + " already exists." );
        }
    }

    /**
     * @param name
     * @param description
     * @param imageUrl
     * @param categoryType
     * @return The newly created category.
     * @throws NoPermissionException
     * @throws CategoryAlreadyExistsException
     */

    public Category createNewCategory( String name, String description, String imageUrl, CategoryType categoryType ) throws NoPermissionException, CategoryAlreadyExistsException {
        securityChecker.isSuperAdmin();
        if ( !categoryType.getInternal().hasCategoryWithName( categoryType.getInternal(), name )) {
            CategoryDomainObject newCategoryDO = documentMapper.addCategoryToDb( categoryType.getInternal().getId(), name, description, imageUrl );
            return new Category( newCategoryDO );
        } else {
            throw new CategoryAlreadyExistsException( "A category with name " + name + " already exists in category type " + categoryType.getName() + "." );
        }
    }

    public Section getSection( int sectionId ) {
        // Allow everyone to get a Section. No security check.
        SectionDomainObject[] sections = documentMapper.getAllSections();
        Section result = null;
        for (int i = 0; i < sections.length; i++) {
            SectionDomainObject section = sections[i];
            if (sectionId == section.getId()) {
                result = new Section( section );
            }
        }
        return result;
    }

    public Document[] search( SearchQuery query ) throws SearchException {
        try {
            DocumentDomainObject[] documentDomainObjects = documentMapper.getDocumentIndex().search( query.getQuery(), securityChecker.getCurrentLoggedInUser() );
            Document[] documents = new Document[documentDomainObjects.length];
            for (int i = 0; i < documentDomainObjects.length; i++) {
                DocumentDomainObject documentDomainObject = documentDomainObjects[i];
                documents[i] = wrapDocumentDomainObject( documentDomainObject );
            }
            return documents;
        } catch (IOException e) {
            throw new SearchException( e );
        }
    }

    public SearchQuery parseLuceneSearchQuery( String query ) throws BadQueryException {
        return new LuceneParsedQuery( query ) ;
    }

}