package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

public class DocumentIndexWrapper implements DocumentIndex {

    private final DocumentIndex index;

    public DocumentIndexWrapper(DocumentIndex index) {
        this.index = index;
    }

    @Override
    public void indexDocument(DocumentDomainObject document) throws IndexException {
        index.indexDocument(document);
    }

    @Override
    public void rebuild() throws IndexException {
        index.rebuild();
    }

    @Override
    public void removeDocument(DocumentDomainObject document) throws IndexException {
        index.removeDocument(document);
    }

	@Override
	public void updateDocumentVersion(DocumentDomainObject document) throws IndexException {
		index.updateDocumentVersion(document);
	}

    @Override
    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return index.search(query, searchingUser);
    }

    @Override
    public com.imcode.imcms.api.SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException {
        return index.search(query, searchingUser, startPosition, maxResults);
    }

    @Override
    public IndexSearchResult search(SolrQuery solrQuery) throws IndexException {
        return index.search(solrQuery);
    }

    @Override
    public DocumentIndexService getService() {
        return index.getService();
    }

    @Override
    public void indexDocument(int docId) throws IndexException {
        index.indexDocument(docId);
    }

    @Override
    public void removeDocument(int docId) throws IndexException {
        index.removeDocument(docId);
    }

	@Override
	public void updateDocumentVersion(int docId) throws IndexException {
		index.updateDocumentVersion(docId);
	}

    @Override
    public boolean isUpdateDone() {
        return index.isUpdateDone();
    }
}
