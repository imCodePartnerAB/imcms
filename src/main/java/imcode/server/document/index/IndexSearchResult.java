package imcode.server.document.index;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.util.AbstractList;
import java.util.List;

public class IndexSearchResult {

    private final SolrQuery solrQuery;
    private final SolrDocumentList solrDocumentList;
    private final List<DocumentStoredFields> documentStoredFieldsList;

    private final int size;
    private final boolean isEmpty;

    public IndexSearchResult(SolrQuery solrQuery, QueryResponse queryResponse) {
        this.solrQuery = solrQuery;
        this.solrDocumentList = queryResponse.getResults();
        this.size = solrDocumentList.size();
        this.isEmpty = size == 0;
        this.documentStoredFieldsList = new AbstractList<DocumentStoredFields>() {
            @Override
            public DocumentStoredFields get(int i) {
                return new DocumentStoredFields(solrDocumentList.get(i));
            }

            @Override
            public int size() {
                return size;
            }
        };
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

    public List<DocumentStoredFields> documentStoredFieldsList() {
        return documentStoredFieldsList;
    }

    public SolrQuery solrQuery() {
        return solrQuery.getCopy();
    }

    public SolrDocumentList solrDocumentList(){
        return solrDocumentList;
    }
}
