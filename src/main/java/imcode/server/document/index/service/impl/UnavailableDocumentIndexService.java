package imcode.server.document.index.service.impl;

import com.imcode.imcms.api.ServiceUnavailableException;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.IndexRebuildTask;
import imcode.server.document.index.service.IndexUpdateOp;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import scala.Option;
import scala.Some;

import java.util.Objects;

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
    public IndexRebuildTask rebuild() {
        throw new ServiceUnavailableException();
    }

    @Override
    public Long count() {
        return query(new SolrQuery("*:*")).getResults().getNumFound();
    }

    @Override
    public Option<IndexRebuildTask> rebuildIfEmpty() {
        if (Objects.equals(count(), 0L)) {
            return Some.apply(rebuild());

        } else return Option.empty();
    }

    @Override
    public Option<IndexRebuildTask> currentRebuildTaskOpt() {
        return Option.empty();
    }

    @Override
    public void shutdown() {
    }
}
