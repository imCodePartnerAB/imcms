package com.imcode.imcms.api;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.List;
import java.util.AbstractList;

public class DocumentService {

    private final ContentManagementSystem contentManagementSystem;

    public DocumentService(ContentManagementSystem contentManagementSystem) {
        this.contentManagementSystem = contentManagementSystem;
    }

    static Document wrapDocumentDomainObject(DocumentDomainObject document,
                                             ContentManagementSystem contentManagementSystem) {
        ApiWrappingDocumentVisitor apiWrappingDocumentVisitor = new ApiWrappingDocumentVisitor(contentManagementSystem);
        document.accept(apiWrappingDocumentVisitor);
        return apiWrappingDocumentVisitor.getDocument();
    }

    /**
     * Used to get a document of any type. If you need a specific document, i.e. a TextDocument, use @see getTextDocument
     * instead.
     *
     * @param documentId The id number of the document requested, also somtimes known as "meta_id"
     * @return The document The type is usually a subclass to document, if there exist one.
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public Document getDocument(int documentId) throws NoPermissionException {
        DocumentDomainObject doc = getDocumentMapper().getDocument(documentId);
        Document result = null;
        if ( null != doc ) {
            result = wrapDocumentDomainObject(doc, contentManagementSystem);
            getSecurityChecker().hasAtLeastDocumentReadPermission(result);
        }
        return result;
    }

    /**
     * @param documentId The id number of the document requested, also known as "meta_id"
     * @return The document
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument(int documentId) throws NoPermissionException {
        return (TextDocument) getDocument(documentId);
    }

    /**
     * @param documentId The id number of the document requested, also known as "meta_id"
     * @return The document
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public UrlDocument getUrlDocument(int documentId) throws NoPermissionException {
        return (UrlDocument) getDocument(documentId);
    }

    public TextDocument createNewTextDocument(Document parent) throws NoPermissionException {
        return (TextDocument) createNewDocument(DocumentTypeDomainObject.TEXT_ID, parent);
    }

    public UrlDocument createNewUrlDocument(Document parent) throws NoPermissionException {
        return (UrlDocument) createNewDocument(DocumentTypeDomainObject.URL_ID, parent);
    }

    public FileDocument createNewFileDocument(Document parent) throws NoPermissionException {
        return (FileDocument) createNewDocument(DocumentTypeDomainObject.FILE_ID, parent);
    }

    private Document createNewDocument(int doctype, Document parent) throws NoPermissionException {
        getSecurityChecker().hasEditPermission(parent);
        return wrapDocumentDomainObject(getDocumentMapper().createDocumentOfTypeFromParent(doctype, parent.getInternal(), contentManagementSystem.getCurrentUser().getInternal()), contentManagementSystem);
    }

    /** Saves the changes to a modified document. Note that this method is synchronized. */
    public synchronized void saveChanges(Document document) throws NoPermissionException, SaveException {
        try {
            if ( 0 == document.getId() ) {
                getDocumentMapper().saveNewDocument(document.getInternal(), contentManagementSystem.getCurrentUser().getInternal());
            } else {
                getDocumentMapper().saveDocument(document.getInternal(), contentManagementSystem.getCurrentUser().getInternal());
            }
        } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
            throw new MaxCategoriesOfTypeExceededException(e);
        }
    }

    public Category getCategory(CategoryType categoryType, String categoryName) {
        final CategoryDomainObject category = getCategoryMapper().getCategoryByTypeAndName(categoryType.getInternal(), categoryName);
        if ( null != category ) {
            return new Category(category);
        } else {
            return null;
        }
    }

    private CategoryMapper getCategoryMapper() {
        return contentManagementSystem.getInternal().getCategoryMapper();
    }

    public Category getCategory(int categoryId) {
        final CategoryDomainObject category = getCategoryMapper().getCategoryById(categoryId);
        if ( null != category ) {
            return new Category(category);
        } else {
            return null;
        }
    }

    public CategoryType getCategoryType(int categoryTypeId) {
        final CategoryTypeDomainObject categoryType = getCategoryMapper().getCategoryTypeById(categoryTypeId);
        return returnCategoryTypeAPIObjectOrNull(categoryType);
    }

    public CategoryType getCategoryType(String categoryTypeName) {
        final CategoryTypeDomainObject categoryType = getCategoryMapper().getCategoryTypeByName(categoryTypeName);
        return returnCategoryTypeAPIObjectOrNull(categoryType);
    }

    private CategoryType returnCategoryTypeAPIObjectOrNull(final CategoryTypeDomainObject categoryType) {
        if ( null != categoryType ) {
            return new CategoryType(categoryType);
        }
        return null;
    }

    public Category[] getAllCategoriesOfType(CategoryType categoryType) {
        // Allow everyone to get a CategoryType. No security check.
        CategoryDomainObject[] categoryDomainObjects = getCategoryMapper().getAllCategoriesOfType(categoryType.getInternal());
        Category[] categories = new Category[categoryDomainObjects.length];
        for ( int i = 0; i < categoryDomainObjects.length; i++ ) {
            CategoryDomainObject categoryDomainObject = categoryDomainObjects[i];
            categories[i] = new Category(categoryDomainObject);
        }
        return categories;
    }

    public CategoryType[] getAllCategoryTypes() {
        CategoryTypeDomainObject[] categoryTypeDomainObjects = getCategoryMapper().getAllCategoryTypes();
        CategoryType[] categoryTypes = new CategoryType[categoryTypeDomainObjects.length];
        for ( int i = 0; i < categoryTypeDomainObjects.length; i++ ) {
            CategoryTypeDomainObject categoryTypeDomainObject = categoryTypeDomainObjects[i];
            categoryTypes[i] = new CategoryType(categoryTypeDomainObject);
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
    public CategoryType createNewCategoryType(String name,
                                              int maxChoices) throws NoPermissionException, CategoryTypeAlreadyExistsException {
        getSecurityChecker().isSuperAdmin();
        if ( getCategoryMapper().isUniqueCategoryTypeName(name) ) {
            CategoryTypeDomainObject newCategoryTypeDO = new CategoryTypeDomainObject(0, name, maxChoices, false);
            newCategoryTypeDO = getCategoryMapper().addCategoryTypeToDb(newCategoryTypeDO);
            return new CategoryType(newCategoryTypeDO);
        } else {
            throw new CategoryTypeAlreadyExistsException("A category with name " + name + " already exists.");
        }
    }

    private SecurityChecker getSecurityChecker() {
        return contentManagementSystem.getSecurityChecker();
    }

    public Section getSection(int sectionId) {
        SectionDomainObject section = getDocumentMapper().getSectionById(sectionId);
        if ( null == section ) {
            return null;
        }
        return new Section(section);
    }

    /** @since 2.0 */
    public Section getSection(String name) {
        SectionDomainObject section = getDocumentMapper().getSectionByName(name);
        if ( null == section ) {
            return null;
        }
        return new Section(section);
    }

    public List getDocuments(SearchQuery query) throws SearchException {
        try {
            final List documentList = getDocumentMapper().getDocumentIndex().search(query.getQuery(), query.getSort(), contentManagementSystem.getCurrentUser().getInternal());
            return new AbstractList() {
                public Object get(int index) {
                    DocumentDomainObject document = (DocumentDomainObject) documentList.get(index);
                    return wrapDocumentDomainObject(document, contentManagementSystem);
                }
                public int size() {
                    return documentList.size();
                }
            };
        } catch ( RuntimeException e ) {
            throw new SearchException(e);
        }
    }

    public Document[] search(SearchQuery query) throws SearchException {
        List documents = getDocuments(query) ;
        return (Document[]) documents.toArray(new Document[documents.size()]);
    }

    private DocumentMapper getDocumentMapper() {
        return contentManagementSystem.getInternal().getDocumentMapper();
    }

    public SearchQuery parseLuceneSearchQuery(String query) throws BadQueryException {
        return new LuceneParsedQuery(query);
    }

    public org.w3c.dom.Document getXmlDomForDocument(Document document) {
        XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder();
        xmlDocumentBuilder.addDocument(document.getInternal());
        return xmlDocumentBuilder.getXmlDocument();
    }

    public void saveCategory(Category category) throws NoPermissionException, CategoryAlreadyExistsException {
        getSecurityChecker().isSuperAdmin();
        getCategoryMapper().saveCategory(category.getInternal());
    }

    public void deleteDocument(Document document) throws NoPermissionException {
        UserDomainObject internalUser = contentManagementSystem.getCurrentUser().getInternal();
        if ( !internalUser.isSuperAdmin() ) {
            throw new NoPermissionException("User must be superadmin to delete documents.");
        }
        getDocumentMapper().deleteDocument(document.getInternal(), internalUser);
    }

    static class ApiWrappingDocumentVisitor extends DocumentVisitor {

        private ContentManagementSystem contentManagementSystem;
        private Document document;

        ApiWrappingDocumentVisitor(ContentManagementSystem contentManagementSystem) {
            this.contentManagementSystem = contentManagementSystem;
        }

        public void visitFileDocument(FileDocumentDomainObject fileDocument) {
            document = new FileDocument(fileDocument, contentManagementSystem);
        }

        public void visitTextDocument(TextDocumentDomainObject textDocument) {
            document = new TextDocument(textDocument, contentManagementSystem);
        }

        public void visitUrlDocument(UrlDocumentDomainObject urlDocument) {
            document = new UrlDocument(urlDocument, contentManagementSystem);
        }

        protected void visitOtherDocument(DocumentDomainObject otherDocument) {
            document = new Document(otherDocument, contentManagementSystem);
        }

        public Document getDocument() {
            return document;
        }
    }

}