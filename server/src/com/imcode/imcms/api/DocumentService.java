package com.imcode.imcms.api;

import imcode.server.document.*;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.document.index.IndexException;
import imcode.server.document.textdocument.TextDocumentDomainObject;

public class DocumentService {

    private final ContentManagementSystem contentManagementSystem;

    public DocumentService( ContentManagementSystem contentManagementSystem ) {
        this.contentManagementSystem = contentManagementSystem;
    }

    static Document wrapDocumentDomainObject( DocumentDomainObject document,
                                              ContentManagementSystem contentManagementSystem ) {
        ApiWrappingDocumentVisitor apiWrappingDocumentVisitor = new ApiWrappingDocumentVisitor( contentManagementSystem );
        document.accept( apiWrappingDocumentVisitor );
        return apiWrappingDocumentVisitor.getDocument();
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
        DocumentDomainObject doc = getDocumentMapper().getDocument( documentId );
        Document result = null;
        if ( null != doc ) {
            result = wrapDocumentDomainObject( doc, contentManagementSystem );
            getSecurityChecker().hasAtLeastDocumentReadPermission( result );
        }
        return result;
    }

    /**
     * @param documentId The id number of the document requested, also known as "meta_id"
     * @return The document
     * @throws com.imcode.imcms.api.NoPermissionException
     *          If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument( int documentId ) throws NoPermissionException {
        return (TextDocument)getDocument( documentId );
    }

    /**
     * @param documentId The id number of the document requested, also known as "meta_id"
     * @return The document
     * @throws com.imcode.imcms.api.NoPermissionException
     *          If the current user dosen't have the rights to read this document.
     */
    public UrlDocument getUrlDocument( int documentId ) throws NoPermissionException {
        return (UrlDocument)getDocument( documentId );
    }

    public TextDocument createNewTextDocument( Document parent ) throws NoPermissionException {
        return (TextDocument)createNewDocument( DocumentTypeDomainObject.TEXT_ID, parent );
    }

    public UrlDocument createNewUrlDocument( Document parent ) throws NoPermissionException {
        return (UrlDocument)createNewDocument( DocumentTypeDomainObject.URL_ID, parent );
    }

    public FileDocument createNewFileDocument( Document parent ) throws NoPermissionException {
        return (FileDocument)createNewDocument( DocumentTypeDomainObject.FILE_ID, parent );
    }

    private Document createNewDocument( int doctype, Document parent ) throws NoPermissionException {
        getSecurityChecker().hasEditPermission( parent );
        return wrapDocumentDomainObject( getDocumentMapper().createDocumentOfTypeFromParent( doctype, parent.getInternal(), contentManagementSystem.getCurrentUser().getInternal() ), contentManagementSystem );
    }

    /**
     * Saves the changes to a modified document. Note that this method is synchronized.
     */
    public synchronized void saveChanges( Document document ) throws NoPermissionException, SaveException {
        getSecurityChecker().hasEditPermission( document );
        try {
            if ( 0 == document.getId() ) {
                getDocumentMapper().saveNewDocument( document.getInternal(), contentManagementSystem.getCurrentUser().getInternal() );
            } else {
                getDocumentMapper().saveDocument( document.getInternal(), contentManagementSystem.getCurrentUser().getInternal() );
            }
        } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
            throw new MaxCategoriesOfTypeExceededException( e );
        } catch (DocumentMapper.DocumentsAddedToMenuWithoutPermissionException e ) {
            throw new NoPermissionException(e.getMessage());
        }
    }

    public Category getCategory( CategoryType categoryType, String categoryName ) {
        final CategoryDomainObject category = getCategoryMapper().getCategory( categoryType.getInternal(), categoryName );
        if ( null != category ) {
            return new Category( category );
        } else {
            return null;
        }
    }

    private CategoryMapper getCategoryMapper() {
        return contentManagementSystem.getInternal().getCategoryMapper() ;
    }

    public Category getCategory( int categoryId ) {
        final CategoryDomainObject category = getCategoryMapper().getCategoryById( categoryId );
        if ( null != category ) {
            return new Category( category );
        } else {
            return null;
        }
    }

    public CategoryType getCategoryType( int categoryTypeId ) {
        final CategoryTypeDomainObject categoryType = getCategoryMapper().getCategoryTypeById( categoryTypeId );
        return returnCategoryTypeAPIObjectOrNull( categoryType );
    }

    public CategoryType getCategoryType( String categoryTypeName ) {
        final CategoryTypeDomainObject categoryType = getCategoryMapper().getCategoryType( categoryTypeName );
        return returnCategoryTypeAPIObjectOrNull( categoryType );
    }

    private CategoryType returnCategoryTypeAPIObjectOrNull( final CategoryTypeDomainObject categoryType ) {
        if ( null != categoryType ) {
            return new CategoryType( categoryType );
        }
        return null;
    }

    public Category[] getAllCategoriesOfType( CategoryType categoryType ) {
        // Allow everyone to get a CategoryType. No security check.
        CategoryDomainObject[] categoryDomainObjects = getCategoryMapper().getAllCategoriesOfType( categoryType.getInternal() );
        Category[] categories = new Category[categoryDomainObjects.length];
        for ( int i = 0; i < categoryDomainObjects.length; i++ ) {
            CategoryDomainObject categoryDomainObject = categoryDomainObjects[i];
            categories[i] = new Category( categoryDomainObject );
        }
        return categories;
    }

    public CategoryType[] getAllCategoryTypes() {
        // Allow everyone to get a CategoryType. No security check.
        CategoryTypeDomainObject[] categoryTypeDomainObjects = getCategoryMapper().getAllCategoryTypes();
        CategoryType[] categoryTypes = new CategoryType[categoryTypeDomainObjects.length];
        for ( int i = 0; i < categoryTypeDomainObjects.length; i++ ) {
            CategoryTypeDomainObject categoryTypeDomainObject = categoryTypeDomainObjects[i];
            categoryTypes[i] = new CategoryType( categoryTypeDomainObject );
        }
        return categoryTypes;
    }

    /**
     * @param name
     * @param maxChoices
     * @return The newly craeated category type.
     * @throws NoPermissionException
     * @throws CategoryTypeAlreadyExistsException
     *
     */
    public CategoryType createNewCategoryType( String name, int maxChoices ) throws NoPermissionException, CategoryTypeAlreadyExistsException {
        getSecurityChecker().isSuperAdmin();
        if ( getCategoryMapper().isUniqueCategoryTypeName( name ) ) {
            CategoryTypeDomainObject newCategoryTypeDO = new CategoryTypeDomainObject( 0, name, maxChoices, false );
            newCategoryTypeDO = getCategoryMapper().addCategoryTypeToDb( newCategoryTypeDO );
            return new CategoryType( newCategoryTypeDO );
        } else {
            throw new CategoryTypeAlreadyExistsException( "A category with name " + name + " already exists." );
        }
    }

    private SecurityChecker getSecurityChecker() {
        return contentManagementSystem.getSecurityChecker();
    }

    public Section getSection( int sectionId ) {
        SectionDomainObject section = getDocumentMapper().getSectionById( sectionId );
        if ( null == section ) {
            return null;
        }
        return new Section( section );
    }

    /**
     * @since 2.0
     */
    public Section getSection( String name ) {
        SectionDomainObject section = getDocumentMapper().getSectionByName( name );
        if ( null == section ) {
            return null;
        }
        return new Section( section );
    }

    public Document[] search( SearchQuery query ) throws SearchException {
        try {
            DocumentDomainObject[] documentDomainObjects = getDocumentMapper().getDocumentIndex().search( query.getQuery(), contentManagementSystem.getCurrentUser().getInternal() );
            Document[] documents = new Document[documentDomainObjects.length];
            for ( int i = 0; i < documentDomainObjects.length; i++ ) {
                DocumentDomainObject documentDomainObject = documentDomainObjects[i];
                documents[i] = wrapDocumentDomainObject( documentDomainObject, contentManagementSystem );
            }
            return documents;
        } catch ( IndexException e ) {
            throw new SearchException( e );
        }
    }

    private DocumentMapper getDocumentMapper() {
        return contentManagementSystem.getInternal().getDocumentMapper();
    }

    public SearchQuery parseLuceneSearchQuery( String query ) throws BadQueryException {
        return new LuceneParsedQuery( query );
    }

    public org.w3c.dom.Document getXmlDomForDocument( Document document ) {
        XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder();
        xmlDocumentBuilder.addDocument( document.getInternal() );
        org.w3c.dom.Document xmlDocument = xmlDocumentBuilder.getXmlDocument();
        return xmlDocument;
    }

    public void saveCategory( Category category ) throws NoPermissionException, CategoryAlreadyExistsException {
        getSecurityChecker().isSuperAdmin();
        getDocumentMapper().saveCategory( category.getInternal() );
    }

    static class ApiWrappingDocumentVisitor extends DocumentVisitor {

        private ContentManagementSystem contentManagementSystem;
        private Document document;

        ApiWrappingDocumentVisitor( ContentManagementSystem contentManagementSystem ) {
            this.contentManagementSystem = contentManagementSystem;
        }

        public void visitFileDocument( FileDocumentDomainObject fileDocument ) {
            document = new FileDocument( fileDocument, contentManagementSystem );
        }

        public void visitTextDocument( TextDocumentDomainObject textDocument ) {
            document = new TextDocument( textDocument, contentManagementSystem );
        }

        public void visitUrlDocument( UrlDocumentDomainObject urlDocument ) {
            document = new UrlDocument( urlDocument, contentManagementSystem );
        }

        protected void visitOtherDocument( DocumentDomainObject otherDocument ) {
            this.document = new Document( otherDocument, contentManagementSystem );
        }

        public Document getDocument() {
            return document;
        }
    }

}