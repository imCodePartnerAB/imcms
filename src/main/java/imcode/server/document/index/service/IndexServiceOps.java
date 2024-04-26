package imcode.server.document.index.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;

public interface IndexServiceOps {

	QueryResponse query(SolrClient solrClient, SolrQuery query) throws SolrServerException, IOException;

	void rebuildIndex(SolrClient solrClient);

	void addToIndex(SolrClient solrClient, String id) throws SolrServerException, IOException;

	void updateDocumentVersionInIndex(SolrClient solrClient, String id) throws SolrServerException, IOException;

	void deleteFromIndex(SolrClient solrClient, String id) throws SolrServerException, IOException;

	long getAmountOfIndexedDocuments();
}
