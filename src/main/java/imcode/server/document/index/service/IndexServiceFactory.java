package imcode.server.document.index.service;

import imcode.server.document.index.service.impl.ServiceFailure;
import org.apache.solr.client.solrj.SolrClient;

import java.util.function.Consumer;

public interface IndexServiceFactory {

	DocumentIndexService create(SolrClient solrReader, SolrClient solrWriter, Consumer<ServiceFailure> failureHandler);
}
