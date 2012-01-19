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

/**
 * In charge of document, category type and category opeerations such as creation, saving, deletion and look up.
 * As well as representation of imcms document in as xml document.
 */
public class DocumentService {

    private final ContentManagementSystem contentManagementSystem;

    /**
     * Returns DocumentService with the given cms
     * @param contentManagementSystem cms used by DocumentService
     */
    public DocumentService(ContentManagementSystem contentManagementSystem) {
        this.contentManagementSystem = contentManagementSystem;
    }

    static Document wrapDocumentDomainObject(DocumentDomainObject document,
                                             ContentManagementSystem contentManagementSystem) {
        if (null == document) {
            return null ;
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
     * or the document name also known as "alias".
     * @return The document The type is usually a subclass to document, if there exist one.
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public Document getDocument(String documentIdString) throws NoPermissionException {
        DocumentDomainObject doc = getDocumentMapper().getDocument(documentIdString);
        Document result = null;
        if ( null != doc ) {
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
        return getDocument(""+documentId);
    }

    /**
     * Returns TextDocument with given meta_id or alias.
     * @param documentIdString The unique id or name of the document requested, can be either the int value alsp known as "meta_id"
     * or the document name also known as "alias".
     * @return The document with given id or null if such doesn't exist.
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument(String documentIdString) throws NoPermissionException {
        return (TextDocument) getDocument(documentIdString);
    }

    /**
     * Returns TextDocument with given id.
     * @param documentId The id number of the document requested, also known as "meta_id"
     * @return The document with given id or null if such doesn't exist.
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument(int documentId) throws NoPermissionException {
        return (TextDocument) getDocument(""+documentId);
    }

    /**
     * Returns UrlDocument with given meta_id or alias.
     * @param documentIdString The unique id or name of the document requested, can be either the int value also known as "meta_id"
     * or the document name also known as "alias".
     * @return The document with given id or null if such doesn't exist.
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public UrlDocument getUrlDocument(String documentIdString) throws NoPermissionException {
        return (UrlDocument) getDocument(documentIdString);
    }

    /**
     * Returns UrlDocument with given meta_id.
     * @param documentId The id number of the document requested, also known as "meta_id"
     * @return The document with given id or null if such doesn't exist.
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public UrlDocument getUrlDocument(int documentId) throws NoPermissionException {
        return (UrlDocument) getDocument(documentId);
    }

    /**
     * Creates new TextDocument with given document acting as parent
     * @param parent document to be used as a parent for the new document
     * @return new TextDocument
     * @throws NoPermissionException
     */
    public TextDocument createNewTextDocument(Document parent) throws NoPermissionException {
        return (TextDocument) createNewDocument(DocumentTypeDomainObject.TEXT_ID, parent);
    }

    /**
     * Creates new UrlDocument with given document acting as parent
     * @param parent document to be used as a parent for the new document
     * @return new UrlDocument
     * @throws NoPermissionException
     */
    public UrlDocument createNewUrlDocument(Document parent) throws NoPermissionException {
        return (UrlDocument) createNewDocument(DocumentTypeDomainObject.URL_ID, parent);
    }

    /**
     * Creates new FileDocument with given document acting as parent
     * @param parent document to be used as a parent for the new document
     * @return new FileDocument
     * @throws NoPermissionException
     */
    public FileDocument createNewFileDocument(Document parent) throws NoPermissionException {
        return (FileDocument) createNewDocument(DocumentTypeDomainObject.FILE_ID, parent);
    }

    /**
     * Creates new document with given parent and of specified document type(text, url or file document)
     * @param doctype document type i.e DocumentTypeDomainObject.TEXT_ID, DocumentTypeDomainObject.URL_ID, DocumentTypeDomainObject.FILE_ID
     * @param parent document to serve as new document's parent
     * @return new document
     * @throws NoPermissionException if current user can't create new documents
     */
    private Document createNewDocument(int doctype, Document parent) throws NoPermissionException {
        return wrapDocumentDomainObject(getDocumentMapper().createDocumentOfTypeFromParent(doctype, parent.getInternal(), contentManagementSystem.getCurrentUser().getInternal()), contentManagementSystem);
    }

    /**
     * Saves the changes to a modified document. Note that this method is synchronized.
     * @param document Document to save
     * @throws SaveException if the given document's alias already belongs to some other existing document. Or if the
     * document was assigned more categories of type that that type's maximum category choice number allows.
     * @see imcode.server.document.CategoryTypeDomainObject#getMaxChoices()
     */
    public synchronized void saveChanges(Document document) throws NoPermissionException, SaveException {
        try {
            if ( 0 == document.getId() ) {
                getDocumentMapper().saveNewDocument(document.getInternal(), contentManagementSystem.getCurrentUser().getInternal(), false);
            } else {
                getDocumentMapper().saveDocument(document.getInternal(), contentManagementSystem.getCurrentUser().getInternal());
            }
        } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
            throw new MaxCategoriesOfTypeExceededException(e);
        } catch (AliasAlreadyExistsInternalException e) {
            throw new AliasAlreadyExistsException(e);
        } catch (DocumentSaveException e) {
            throw new SaveException(e) ;
        }
    }

    /**
     * Returns a category of given category type and name
     * @param categoryType category type to look for in
     * @param categoryName category name to look category for by
     * @return category, or null if given category type doesn't have category with given name
     */
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

    /**
     * Returns category with given id
     * @param categoryId category id
     * @return category or null if none exist by given id
     */
    public Category getCategory(int categoryId) {
        final CategoryDomainObject category = getCategoryMapper().getCategoryById(categoryId);
        if ( null != category ) {
            return new Category(category);
        } else {
            return null;
        }
    }

    /**
     * Returns category type with given id
     * @param categoryTypeId category type id
     * @return category type or null if none exist by given id
     */
    public CategoryType getCategoryType(int categoryTypeId) {
        final CategoryTypeDomainObject categoryType = getCategoryMapper().getCategoryTypeById(categoryTypeId);
        return returnCategoryTypeAPIObjectOrNull(categoryType);
    }

    /**
     * Returns category type with given name
     * @param categoryTypeName category type name
     * @return category type or null if none exist by given name
     */
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

    /**
     * Returns all categories of given category type
     * @param categoryType category type whose categories to return
     * @return an array of categories belonging to given category type
     */
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

    /**
     * Returns all category types.
     * @return an array of category types in the cms
     */
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
     * Creates new category type with given name and maximum number of categories a document is allowed to have(of this category type)
     * @param name name of the new category type
     * @param maxChoices maximum number of category choice of this category type
     * @return The newly craeated category type.
     * @throws NoPermissionException
     * @throws CategoryTypeAlreadyExistsException if there's already a category type with given name
     */
    public CategoryType createNewCategoryType(String name,
                                              int maxChoices) throws NoPermissionException, CategoryTypeAlreadyExistsException {
        if ( getCategoryMapper().isUniqueCategoryTypeName(name) ) {
        	CategoryTypeDomainObject newCategoryTypeDO = new CategoryTypeDomainObject(0, name, maxChoices, false, false);
            newCategoryTypeDO = getCategoryMapper().addCategoryTypeToDb(newCategoryTypeDO);
            return new CategoryType(newCategoryTypeDO);
        } else {
            throw new CategoryTypeAlreadyExistsException("A category with name " + name + " already exists.");
        }
    }

    /**
     * Returns a section with given id
     * @param sectionId id of a section
     * @return a section with give id or null if none found
     */
    public Section getSection(int sectionId) {
        SectionDomainObject section = getDocumentMapper().getSectionById(sectionId);
        if ( null == section ) {
            return null;
        }
        return new Section(section);
    }

    /**
     * Returns a section with given name
     * @param name name of a section
     * @return a section with give name or null if none found
     * @since 2.0
     */
    public Section getSection(String name) {
        SectionDomainObject section = getDocumentMapper().getSectionByName(name);
        if ( null == section ) {
            return null;
        }
        return new Section(section);
    }

    /**
     * Searches for documents using given query, takes into account current cms user
     * @param query search query to look for documents with
     * @return a list of documents found
     * @throws SearchException
     * @see com.imcode.imcms.api.LuceneParsedQuery
     */
    public List getDocuments(final SearchQuery query) throws SearchException {
        try {
            final List documentList = getDocumentMapper().getDocumentIndex().search(new DocumentQuery() {
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
        } catch ( RuntimeException e ) {
            throw new SearchException(e);
        }
    }

    /**
     * Searches for documents using given query, takes into account current cms user
     * @param query search query to look for documents with
     * @return an array of documents found
     * @throws SearchException
     * @see com.imcode.imcms.api.LuceneParsedQuery
     */
    public Document[] search(SearchQuery query) throws SearchException {
        List documents = getDocuments(query) ;
        return (Document[]) documents.toArray(new Document[documents.size()]);
    }

    private DocumentMapper getDocumentMapper() {
        return contentManagementSystem.getInternal().getDocumentMapper();
    }

    /**
     * Creates a search query
     * @param query a string representing document search query
     * @return search query
     * @throws BadQueryException if something is wrong with the query, like syntax error
     */
    public SearchQuery parseLuceneSearchQuery(String query) throws BadQueryException {
        return new LuceneParsedQuery(query);
    }

    /**
     * Returns xml representation of the given document
     * @param document a document to get xml representation of
     * @return org.w3c.dom.Document of given document
     */
    public org.w3c.dom.Document getXmlDomForDocument(Document document) {
        XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder(contentManagementSystem.getCurrentUser().getInternal());
        xmlDocumentBuilder.addDocument(document.getInternal());
        return xmlDocumentBuilder.getXmlDocument();
    }

    /**
     * Saves given category
     * @param category category to save, be it a new one or an existing category
     * @throws NoPermissionException
     * @throws CategoryAlreadyExistsException if the given category is new and it's category type already contains a category with the same name
     */
    public void saveCategory(Category category) throws NoPermissionException, CategoryAlreadyExistsException {
        getCategoryMapper().saveCategory(category.getInternal());
    }

    /**
     * Deletes document from cms
     * @param document document to delete
     * @throws NoPermissionException
     */
    public void deleteDocument(Document document) throws NoPermissionException {
        UserDomainObject internalUser = contentManagementSystem.getCurrentUser().getInternal();
        getDocumentMapper().deleteDocument(document.getInternal(), internalUser);
    }
    
    /**
     * Removes all image cache entries.
     * 
     */
    public void clearImageCache() {
        getDocumentMapper().clearImageCache();
    }
    
    /**
     * Removes all image cache entries that have been created for a document that is identified 
     * with {@code metaId}.
     * 
     * If a document contains 3 image fields (1, 2, 3), then the cache entries for these 3 images will be removed.
     * 
     * @param metaId    the ID of a text document
     */
    public void clearImageCache(int metaId) {
        getDocumentMapper().clearImageCache(metaId);
    }
    
    /**
     * Removes a specific image cache entry that is identified with a document ID ({@code metaId}) and an image field 
     * number ({@code no}).
     * 
     * @param metaId    the ID of a text document
     * @param no        the ID of an image field
     */
    public void clearImageCache(int metaId, int no) {
        getDocumentMapper().clearImageCache(metaId, no);
    }
    
    /**
     * Removes a specific image cache entry that is identified with a document ID ({@code metaId}) and a 
     * {@link FileDocument} file ID ({@code fileNo}).
     * 
     * @param metaId    the ID of a text document
     * @param fileNo    the file ID of a {@link FileDocument}
     */
    public void clearImageCache(int metaId, String fileNo) {
        getDocumentMapper().clearImageCache(metaId, fileNo);
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

    static class ApiDocumentWrappingList extends AbstractList {

        private final List documentList;
        private ContentManagementSystem contentManagementSystem;

        ApiDocumentWrappingList(List documentList, ContentManagementSystem contentManagementSystem) {
            this.documentList = documentList;
            this.contentManagementSystem = contentManagementSystem;
        }

        public Object get(int index) {
            DocumentDomainObject document = (DocumentDomainObject) documentList.get(index);
            return wrapDocumentDomainObject(document, contentManagementSystem);
        }

        public int size() {
            return documentList.size();
        }

        public Object remove(int index) {
            return wrapDocumentDomainObject((DocumentDomainObject) documentList.remove(index), contentManagementSystem) ;
        }

        public Object set(int index, Object element) {
            return wrapDocumentDomainObject((DocumentDomainObject) documentList.set(index, ((Document)element).getInternal()), contentManagementSystem) ;
        }
    }
}