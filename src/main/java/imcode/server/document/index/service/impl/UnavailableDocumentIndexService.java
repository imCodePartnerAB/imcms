package imcode.server.document.index.service.impl;

import com.imcode.imcms.api.exception.ServiceUnavailableException;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.IndexUpdateOp;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.concurrent.Future;

// translated from scala
public class UnavailableDocumentIndexService implements DocumentIndexService {
    @Override
    public QueryResponse query(SolrQuery solrQuery) {
        throw new ServiceUnavailableException();
    }

    @Override
    public void update(IndexUpdateOp request) {
    }

    @Override
    public Future rebuild() {
        throw new ServiceUnavailableException();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean isUpdateDone() {
        throw new ServiceUnavailableException();
    }
}
