package imcode.server.document.index.service.impl;

import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.IndexUpdateOp;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.Future;

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

    @Override
    public QueryResponse query(SolrQuery solrQuery) {
        throw new NotImplementedException();
    }

    @Override
    public void update(IndexUpdateOp request) {
        throw new NotImplementedException();
    }

    @Override
    public Future rebuild() {
        throw new NotImplementedException();
    }

    @Override
    public void shutdown() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isUpdateDone() {
        throw new NotImplementedException();
    }
}
