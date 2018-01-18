package imcode.server.document.index.service;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.concurrent.Future;

// translated from scala...
public interface DocumentIndexService {

    QueryResponse query(SolrQuery solrQuery);

    /**
     * Updates index asynchronously.
     *
     * @param request index update request.
     */
    void update(IndexUpdateOp request);

    /**
     * Attempts to run a new index rebuild task if one is not already running.
     *
     * @return rebuild future.
     */
    Future rebuild();

    // previously failed indexing not covered here
    default void rebuildIfEmpty() {
        final long foundResults = query(new SolrQuery("*:*")).getResults().getNumFound();
        if (foundResults == 0L) rebuild();
    }

    void shutdown();

    boolean isUpdateDone();

}
