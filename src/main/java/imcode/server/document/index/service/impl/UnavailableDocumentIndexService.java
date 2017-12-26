package imcode.server.document.index.service.impl;

import com.imcode.imcms.api.ServiceUnavailableException;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.IndexRebuildTask;
import imcode.server.document.index.service.IndexUpdateOp;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import scala.Option;

// translated from scala
public class UnavailableDocumentIndexService implements DocumentIndexService {
    @Override
    public QueryResponse query(SolrQuery solrQuery) {
        throw new ServiceUnavailableException();
    }

    @Override
    public void update(IndexUpdateOp request) {
        throw new ServiceUnavailableException();
    }

    @Override
    public IndexRebuildTask rebuild() {
        throw new ServiceUnavailableException();
    }

    @Override
    public Option<IndexRebuildTask> rebuildIfEmpty() {
        throw new ServiceUnavailableException();
    }

    @Override
    public Long count() {
        throw new ServiceUnavailableException();
    }

    @Override
    public Option<IndexRebuildTask> currentRebuildTaskOpt() {
        throw new ServiceUnavailableException();
    }

    @Override
    public void shutdown() {
        throw new ServiceUnavailableException();
    }
}
