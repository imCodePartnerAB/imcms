package imcode.server.document.index.service.impl;

import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

@Component
public class DocumentIndexer {

    private final Logger logger = LogManager.getLogger(getClass());

    private DelegatingByTypeDocumentService documentService;

    public void setDocumentService(DelegatingByTypeDocumentService documentService) {
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

	public SolrInputDocument updateDocumentVersion(int docId){
		try {
			return documentService.updateDocumentVersion(docId);
		} catch (UnsupportedDocumentTypeException e) {
			logger.warn("Updating of document`s version skipped for unsupported document type " + e.getType() + ", doc " + docId);

		} catch (Exception e) {
			logger.error(String.format("Failed to update doc's version in index. Doc id: %d",
					docId), e);
		}
		return null;
	}
}
