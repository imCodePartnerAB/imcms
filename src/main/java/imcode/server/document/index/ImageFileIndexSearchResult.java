package imcode.server.document.index;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.AbstractList;
import java.util.List;

public class ImageFileIndexSearchResult extends IndexSearchResult<ImageFileStoredFields> {

	public ImageFileIndexSearchResult(SolrQuery solrQuery, QueryResponse queryResponse) {
		super(solrQuery, queryResponse);
	}

	@Override
	public List<ImageFileStoredFields> storedFieldsList() {
		return new AbstractList<ImageFileStoredFields>() {
			@Override
			public ImageFileStoredFields get(int index) {
				return new ImageFileStoredFields(solrDocumentList().get(index));
			}

			@Override
			public int size() {
				return ImageFileIndexSearchResult.this.size();
			}
		};
	}
}
