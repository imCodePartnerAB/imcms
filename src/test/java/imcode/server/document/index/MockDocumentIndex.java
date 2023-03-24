package imcode.server.document.index;

import com.imcode.imcms.api.SearchResult;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

public class MockDocumentIndex implements DocumentIndex {
    @Override
    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return null;
    }

    @Override
    public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException {
        return null;
    }

    @Override
    public void rebuild() throws IndexException {
    }

    @Override
    public void indexDocument(DocumentDomainObject document) throws IndexException {
    }

    @Override
    public void removeDocument(DocumentDomainObject document) throws IndexException {
    }

	@Override
	public void updateDocumentVersion(DocumentDomainObject document) throws IndexException {
	}

	@Override
    public IndexSearchResult search(SolrQuery query) throws IndexException {
        return null;
    }

    @Override
    public void indexDocument(int docId) throws IndexException {
    }

    @Override
    public void removeDocument(int docId) throws IndexException {
    }

	@Override
	public void updateDocumentVersion(int docId) throws IndexException {
	}

	@Override
    public DocumentIndexService getService() {
        return null;
    }

    @Override
    public boolean isUpdateDone() {
        return false;
    }
}
