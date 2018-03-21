package imcode.server.document.index.service.impl;

import imcode.server.document.index.service.SolrClientFactory;
import org.apache.solr.client.solrj.SolrClient;

/**
 * Delegates all invocations to the ManagedSolrDocumentIndexService instance.
 * In case of a fatal indexing error replaces managed instance with a new and re-indexes all documents.
 */
// translated from scala...
public class InternalDocumentIndexService extends AbstractDocumentIndexService {

    private final String solrHome;

    public InternalDocumentIndexService(String solrHome, DocumentIndexServiceOps serviceOps, long periodInMinutes) {
        this.solrHome = solrHome;
        init(serviceOps, periodInMinutes);
    }

    @Override
    SolrClient createSolrClient(boolean recreateDataDir) {
        return SolrClientFactory.createEmbeddedSolrClient(solrHome, recreateDataDir);
    }
}
