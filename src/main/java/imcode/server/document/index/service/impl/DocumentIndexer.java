package imcode.server.document.index.service.impl;

import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import com.imcode.imcms.domain.service.TypedDocumentService;
import com.imcode.imcms.model.Document;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

@Component
public class DocumentIndexer {

    private final Logger logger = Logger.getLogger(getClass());

    private TypedDocumentService<Document> documentService;

    public void setDocumentService(TypedDocumentService<Document> documentService) {
        this.documentService = documentService;
    }

    /**
     * Creates SolrInputDocument based on provided DocumentDomainObject.
     *
     * @return SolrInputDocument
     */
    public SolrInputDocument index(int docId) {

        try {
            return documentService.index(docId);

        } catch (UnsupportedDocumentTypeException e) {
            logger.warn("Indexing skipped for unsupported document type " + e.getType() + ", doc " + docId);

        } catch (Exception e) {
            logger.error(String.format("Failed to index doc's content. Doc id: %d",
                    docId), e);
        }
        return null;
    }
}