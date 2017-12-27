package imcode.server.document.index.service.impl;

import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.IndexRebuildTask;
import imcode.server.document.index.service.IndexUpdateOp;
import imcode.server.document.index.service.SolrServerFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// translated from scala...
public class RemoteDocumentIndexService implements DocumentIndexService, IndexRebuildScheduler {

    private String solrReadUrl;
    private String solrWriteUrl;
    private DocumentIndexServiceOps serviceOps;

    public RemoteDocumentIndexService(String solrReadUrl, String solrWriteUrl, DocumentIndexServiceOps serviceOps, long periodInMinutes) {
        this.solrReadUrl = solrReadUrl;
        this.solrWriteUrl = solrWriteUrl;
        this.serviceOps = serviceOps;
        setRebuildIntervalInMinutes(periodInMinutes);
    }

    private ManagedDocumentIndexService newManagedService() {
        final HttpSolrServer solrServerReader = SolrServerFactory.createHttpSolrServer(solrReadUrl);
        final HttpSolrServer solrServerWriter = SolrServerFactory.createHttpSolrServer(solrWriteUrl);

        return new ManagedDocumentIndexService(solrServerReader, solrServerWriter, serviceOps, serviceFailure -> {
        });
    }

    @Override
    public QueryResponse query(SolrQuery solrQuery) {
        throw new NotImplementedException();
    }

    @Override
    public void update(IndexUpdateOp request) {
        throw new NotImplementedException();
    }

    @Override
    public IndexRebuildTask rebuild() {
        throw new NotImplementedException();
    }

    @Override
    public void shutdown() {
        throw new NotImplementedException();
    }
}
