package imcode.server.document.index.service.impl;

import imcode.server.document.index.service.SolrClientFactory;
import org.apache.solr.client.solrj.SolrClient;

// translated from scala...
public class RemoteDocumentIndexService extends AbstractDocumentIndexService {

    private final String solrUrl;

    public RemoteDocumentIndexService(String solrUrl, DocumentIndexServiceOps serviceOps, long periodInMinutes) {
        this.solrUrl = solrUrl;
        init(serviceOps, periodInMinutes);
    }

    @Override
    SolrClient createSolrClient(boolean recreateDataDir) {
        return SolrClientFactory.createHttpSolrClient(solrUrl, recreateDataDir);
    }
}
