package com.imcode.imcms.api;

import com.imcode.imcms.mapping.AliasAlreadyExistsInternalException;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import imcode.server.document.*;
import imcode.server.document.index.DocumentQuery;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.util.AbstractList;
import java.util.List;

public class DocumentService {

    private final ContentManagementSystem contentManagementSystem;
    private final DocumentMapper documentMapper;

    public DocumentService(ContentManagementSystem contentManagementSystem) {
        this.contentManagementSystem = contentManagementSystem;
        this.documentMapper = contentManagementSystem.getInternal().getDocumentMapper();
    }

    static Document wrapDocumentDomainObject(DocumentDomainObject document,
                                             ContentManagementSystem contentManagementSystem) {
        if (null == document) {
            return null;
        }
        ApiWrappingDocumentVisitor apiWrappingDocumentVisitor = new ApiWrappingDocumentVisitor(contentManagementSystem);
        document.accept(apiWrappingDocumentVisitor);
        return apiWrappingDocumentVisitor.getDocument();
    }

    /**
     * Used to get a document of any type. If you need a specific document, i.e. a TextDocument, use @see getTextDocument
     * instead.
     *
     * @param documentIdString The unique id or name of the document requested, can be either the int value also known as "meta_id"
     *                         or the document name also known as "alias".
     * @return The document The type is usually a subclass to document, if there exist one.
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public Document getDocument(String documentIdString) throws NoPermissionException {
        DocumentDomainObject doc = documentMapper.getDocument(documentIdString);
        Document result = null;
        if (null != doc) {
            result = wrapDocumentDomainObject(doc, contentManagementSystem);
        }
        return result;
    }

    /**
     * Used to get a document of any type. If you need a specific document, i.e. a TextDocument, use @see getTextDocument
     * instead.
     *
     * @param documentId The id number of the document requested, also sometimes known as "meta_id"
     * @return The document The type is usually a subclass to document, if there exist one.
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public Document getDocument(int documentId) throws NoPermissionException {
        return getDocument("" + documentId);
    }

    /**
     * @param documentIdString The unique id or name of the document requested, can be either the int value alsp known as "meta_id"
     *                         or the document name also known as "alias".
     * @return The document
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument(String documentIdString) throws NoPermissionException {
        return (TextDocument) getDocument(documentIdString);
    }

    /**
     * @param documentId The id number of the document requested, also known as "meta_id"
     * @return The document
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument(int documentId) throws NoPermissionException {
        return (TextDocument) getDocument("" + documentId);
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
        final UserDomainObject user = contentManagementSystem.getCurrentUser().getInternal();
        final DocumentDomainObject parentDoc = parent.getInternal();
        final DocumentDomainObject newDoc = documentMapper.createDocumentOfTypeFromParent(doctype, parentDoc, user);

        return wrapDocumentDomainObject(newDoc, contentManagementSystem);
    }

    /**
     * Saves the changes to a modified document. Note that this method is synchronized.
     *
     * @param document - document that going to be saved
     * @return saved document's id
     */
    public synchronized int saveChanges(Document document) throws SaveException {
        try {
            DocumentDomainObject internalDoc = document.getInternal();
            UserDomainObject user = contentManagementSystem.getCurrentUser().getInternal();

            return (0 == document.getId())
                    ? documentMapper.saveNewDocument(internalDoc, user).getId()
                    : documentMapper.saveDocument(internalDoc, user);

        } catch (MaxCategoryDomainObjectsOfTypeExceededException e) {
            throw new MaxCategoriesOfTypeExceededException(e);
        } catch (AliasAlreadyExistsInternalException e) {
            throw new AliasAlreadyExistsException(e);
        } catch (DocumentSaveException e) {
            throw new SaveException(e);
        }
    }

    public Category getCategory(CategoryType categoryType, String categoryName) {
        final CategoryDomainObject category = getCategoryMapper().getCategoryByTypeAndName(categoryType.getInternal(), categoryName);
        if (null != category) {
            return new Category(category);
        } else {
            return null;
        }
    }

    private CategoryMapper getCategoryMapper() {
        return contentManagementSystem.getInternal().getCategoryMapper();
    }

    public CategoryType getCategoryType(String categoryTypeName) {
        final CategoryTypeDomainObject categoryType = getCategoryMapper().getCategoryTypeByName(categoryTypeName);
        return returnCategoryTypeAPIObjectOrNull(categoryType);
    }

    private CategoryType returnCategoryTypeAPIObjectOrNull(final CategoryTypeDomainObject categoryType) {
        if (null != categoryType) {
            return new CategoryType(categoryType);
        }
        return null;
    }

    public Category[] getAllCategoriesOfType(CategoryType categoryType) {
        // Allow everyone to get a CategoryType. No security check.
        CategoryDomainObject[] categoryDomainObjects = getCategoryMapper().getAllCategoriesOfType(categoryType.getInternal());
        Category[] categories = new Category[categoryDomainObjects.length];
        for (int i = 0; i < categoryDomainObjects.length; i++) {
            CategoryDomainObject categoryDomainObject = categoryDomainObjects[i];
            categories[i] = new Category(categoryDomainObject);
        }
        return categories;
    }

    public CategoryType[] getAllCategoryTypes() {
        CategoryTypeDomainObject[] categoryTypeDomainObjects = getCategoryMapper().getAllCategoryTypes();
        CategoryType[] categoryTypes = new CategoryType[categoryTypeDomainObjects.length];
        for (int i = 0; i < categoryTypeDomainObjects.length; i++) {
            CategoryTypeDomainObject categoryTypeDomainObject = categoryTypeDomainObjects[i];
            categoryTypes[i] = new CategoryType(categoryTypeDomainObject);
        }
        return categoryTypes;
    }

    /**
     * @return The newly created category type.
     */
    public CategoryType createNewCategoryType(String name,
                                              int maxChoices) throws NoPermissionException, CategoryTypeAlreadyExistsException {
        if (getCategoryMapper().isUniqueCategoryTypeName(name)) {
            CategoryTypeDomainObject newCategoryTypeDO = new CategoryTypeDomainObject(0, name, maxChoices, false);
            newCategoryTypeDO = getCategoryMapper().addCategoryTypeToDb(newCategoryTypeDO);
            return new CategoryType(newCategoryTypeDO);
        } else {
            throw new CategoryTypeAlreadyExistsException("A category with name " + name + " already exists.");
        }
    }


    public List getDocuments(final SearchQuery query) throws SearchException {
        try {
            final List<DocumentDomainObject> documentList = documentMapper.getDocumentIndex().search(new DocumentQuery() {
                public Query getQuery() {
                    return query.getQuery();
                }

                public Sort getSort() {
                    return query.getSort();
                }

                public boolean isLogged() {
                    return query.isLogged();
                }
            }, contentManagementSystem.getCurrentUser().getInternal());
            return new ApiDocumentWrappingList(documentList, contentManagementSystem);
        } catch (RuntimeException e) {
            throw new SearchException(e);
        }
    }


    public SearchResult<Document> getDocuments(final SearchQuery query, int startPosition, int maxResults) throws SearchException {
        try {
            SearchResult<DocumentDomainObject> result = documentMapper.getDocumentIndex().search(new DocumentQuery() {
                public Query getQuery() {
                    return query.getQuery();
                }

                public Sort getSort() {
                    return query.getSort();
                }

                public boolean isLogged() {
                    return query.isLogged();
                }
            }, contentManagementSystem.getCurrentUser().getInternal(), startPosition, maxResults);

            ApiDocumentWrappingList documents = new ApiDocumentWrappingList(result.getDocuments(), contentManagementSystem);

            return SearchResult.of(documents, result.getTotalCount());
        } catch (RuntimeException e) {
            throw new SearchException(e);
        }
    }

    public Document[] search(SearchQuery query) throws SearchException {
        List documents = getDocuments(query);
        return (Document[]) documents.toArray(new Document[documents.size()]);
    }

    public void saveCategory(Category category) throws NoPermissionException, CategoryAlreadyExistsException {
        getCategoryMapper().saveCategory(category.getInternal());
    }

    ///////////////////// unused

    /**
     * @param documentIdString The unique id or name of the document requested, can be either the int value also known as "meta_id"
     *                         or the document name also known as "alias".
     * @return The document
     * @throws NoPermissionException If the current user doesn't have the rights to read this document.
     */
    public UrlDocument getUrlDocument(String documentIdString) throws NoPermissionException {
        return (UrlDocument) getDocument(documentIdString);
    }

    /**
     * @param documentId The id number of the document requested, also known as "meta_id"
     * @return The document
     * @throws NoPermissionException If the current user doesn't have the rights to read this document.
     */
    public UrlDocument getUrlDocument(int documentId) throws NoPermissionException {
        return (UrlDocument) getDocument(documentId);
    }

    public Category getCategory(int categoryId) {
        final CategoryDomainObject category = getCategoryMapper().getCategoryById(categoryId);
        if (null != category) {
            return new Category(category);
        } else {
            return null;
        }
    }

    public CategoryType getCategoryType(int categoryTypeId) {
        final CategoryTypeDomainObject categoryType = getCategoryMapper().getCategoryTypeById(categoryTypeId);
        return returnCategoryTypeAPIObjectOrNull(categoryType);
    }

    public SearchQuery parseLuceneSearchQuery(String query) throws BadQueryException {
        return new LuceneParsedQuery(query);
    }

    public org.w3c.dom.Document getXmlDomForDocument(Document document) {
        XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder(contentManagementSystem.getCurrentUser().getInternal());
        xmlDocumentBuilder.addDocument(document.getInternal());
        return xmlDocumentBuilder.getXmlDocument();
    }

    public void deleteDocument(Document document) throws NoPermissionException {
        documentMapper.deleteDocument(document.getInternal());
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

    static class ApiDocumentWrappingList extends AbstractList<Document> {

        private final List<DocumentDomainObject> documentList;
        private ContentManagementSystem contentManagementSystem;

        ApiDocumentWrappingList(List<DocumentDomainObject> documentList, ContentManagementSystem contentManagementSystem) {
            this.documentList = documentList;
            this.contentManagementSystem = contentManagementSystem;
        }

        public Document get(int index) {
            DocumentDomainObject document = documentList.get(index);
            return wrapDocumentDomainObject(document, contentManagementSystem);
        }

        public int size() {
            return documentList.size();
        }

        public Document remove(int index) {
            return wrapDocumentDomainObject(documentList.remove(index), contentManagementSystem);
        }

        public Document set(int index, Document element) {
            return wrapDocumentDomainObject(documentList.set(index, element.getInternal()), contentManagementSystem);
        }
    }
}