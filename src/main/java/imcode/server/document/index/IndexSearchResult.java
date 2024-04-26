package imcode.server.document.index;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.util.List;

public abstract class IndexSearchResult<T> {
    private final SolrQuery solrQuery;
    private final SolrDocumentList solrDocumentList;

    private final int size;
    private final boolean isEmpty;

    public IndexSearchResult(SolrQuery solrQuery, QueryResponse queryResponse) {
        this.solrQuery = solrQuery;
        this.solrDocumentList = queryResponse.getResults();
        this.size = solrDocumentList.size();
        this.isEmpty = size == 0;
    }

    public long found() {
        return solrDocumentList.getNumFound();
    }

    public long start() {
        return solrDocumentList.getStart();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean nonEmpty() {
        return !isEmpty;
    }

    public boolean contains(int index) {
        return !isEmpty && index >= 0 && index < size;
    }

    public SolrQuery solrQuery() {
        return solrQuery.getCopy();
    }

    public SolrDocumentList solrDocumentList(){
        return solrDocumentList;
    }

    public abstract List<T> storedFieldsList();
}
