package imcode.server.document.index;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.AbstractList;
import java.util.List;

public class DocumentIndexSearchResult extends IndexSearchResult<DocumentStoredFields> {

	public DocumentIndexSearchResult(SolrQuery solrQuery, QueryResponse queryResponse) {
		super(solrQuery, queryResponse);
	}

	@Override
	public List<DocumentStoredFields> storedFieldsList() {
		return new AbstractList<DocumentStoredFields>() {
			@Override
			public DocumentStoredFields get(int index) {
				return new DocumentStoredFields(solrDocumentList().get(index));
			}

			@Override
			public int size() {
				return DocumentIndexSearchResult.this.size();
			}
		};
	}
}
