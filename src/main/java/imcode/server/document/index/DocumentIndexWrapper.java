package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;

import java.util.Iterator;
import java.util.List;

public class DocumentIndexWrapper implements DocumentIndex {

    private final DocumentIndex index;

    public DocumentIndexWrapper(DocumentIndex index) {
        this.index = index ;
    }

    public void indexDocument(DocumentDomainObject document) throws IndexException {
        index.indexDocument(document);
    }

    public void rebuild() throws IndexException {
        index.rebuild();
    }

    public void removeDocument(DocumentDomainObject document) throws IndexException {
        index.removeDocument(document);
    }

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return index.search(query, searchingUser);
    }

    @Override
    public List<DocumentDomainObject> search(SolrQuery solrQuery, UserDomainObject searchingUser) throws IndexException {
        return index.search(solrQuery, searchingUser);
    }

    public DocumentIndexService service() {
        return index.service();
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
    public QueryResponse query(SolrQuery solrQuery) {
        return index.query(solrQuery);
    }
}
