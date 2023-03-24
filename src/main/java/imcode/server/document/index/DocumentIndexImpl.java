package imcode.server.document.index;

import com.imcode.imcms.api.SearchResult;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.service.AddDocToIndex;
import imcode.server.document.index.service.DeleteDocFromIndex;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.UpdateDocumentVersionInIndex;
import imcode.server.user.UserDomainObject;
import lombok.val;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

// translated from scala...
public class DocumentIndexImpl implements DocumentIndex {

    private final static Logger logger = LogManager.getLogger(DocumentIndexImpl.class);
    private final DocumentIndexService service;

    DocumentIndexImpl(DocumentIndexService service) {
        this.service = service;
    }

    /**
     * @deprecated use {@link DocumentIndexImpl#search(org.apache.solr.client.solrj.SolrQuery)}
     */
    @Override
    @Deprecated
    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        final String queryString = query.getQuery().toString();

        if (logger.isDebugEnabled()) {
            logger.debug("Searching using *legacy* document query " + queryString);
        }

        final SolrQuery solrQuery = new SolrQuery(queryString);
        solrQuery.setRows(DEFAULT_PAGE_SIZE * 1000); // dummy limit, use SearchDocumentService for better control

        final Sort sort = query.getSort();
        if (sort != null) {
            final SortField[] sortFields = sort.getSort();

            for (SortField sortField : sortFields) {
                final String field = sortField.getField();
                solrQuery.addSort(field, (sortField.getReverse()) ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
            }
        }

        try {
            final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

            return search(solrQuery)
                    .documentStoredFieldsList()
                    .stream()
                    .map(DocumentStoredFields::id)
                    .map(documentMapper::<DocumentDomainObject>getDefaultDocument)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * @deprecated use {@link DocumentIndexImpl#search(org.apache.solr.client.solrj.SolrQuery)}
     */
    @Override
    @Deprecated
    public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException {
        throw new NotImplementedException("Method " + getClass().getName() + "#search SearchResult<DocumentDomainObject> is not implemented");
    }

    @Override
    public void rebuild() throws IndexException {
        service.rebuild();
    }

    @Override
    public void indexDocument(DocumentDomainObject document) throws IndexException {
        indexDocument(document.getId());
    }

    @Override
    public void removeDocument(DocumentDomainObject document) throws IndexException {
        removeDocument(document.getId());
    }

	@Override
	public void updateDocumentVersion(DocumentDomainObject document) throws IndexException {
		updateDocumentVersion(document.getId());
	}

    @Override
    public IndexSearchResult search(SolrQuery solrQuery) throws IndexException {
        val queryResponse = service.query(solrQuery);
        return new IndexSearchResult(solrQuery, queryResponse);
    }

    @Override
    public void indexDocument(int docId) throws IndexException {
        service.update(new AddDocToIndex(docId));
    }

    @Override
    public void removeDocument(int docId) throws IndexException {
        service.update(new DeleteDocFromIndex(docId));
    }

	@Override
	public void updateDocumentVersion(int docId) throws IndexException {
		service.update(new UpdateDocumentVersionInIndex(docId));
	}

    @Override
    public DocumentIndexService getService() {
        return service;
    }

    @Override
    public boolean isUpdateDone() {
        return service.isUpdateDone();
    }
}
