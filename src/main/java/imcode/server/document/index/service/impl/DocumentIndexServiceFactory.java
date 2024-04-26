package imcode.server.document.index.service.impl;

import imcode.server.document.index.service.IndexServiceFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class DocumentIndexServiceFactory implements IndexServiceFactory {

    private final DocumentIndexServiceOps serviceOps;

    public DocumentIndexServiceFactory(DocumentIndexServiceOps serviceOps) {
        this.serviceOps = serviceOps;
    }

    public ManagedDocumentIndexService create(SolrClient solrReader, SolrClient solrWriter, Consumer<ServiceFailure> failureHandler) {
        return new ManagedDocumentIndexService(solrReader, solrWriter, serviceOps, failureHandler);
    }
}
