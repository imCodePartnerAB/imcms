package imcode.server.document.index.service.impl;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.index.DocumentIndex;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Document index service low level operations.
 * <p>
 * An instance of this class is thread save.
 */
@Component
public class DocumentIndexServiceOps {

    private static final Logger logger = LogManager.getLogger(DocumentIndexServiceOps.class);

    private final DocumentMapper documentMapper;
    private final DocumentIndexer documentIndexer;

    private AtomicLong indexedDocumentsAmount = new AtomicLong(-1);

    @Autowired
    public DocumentIndexServiceOps(DocumentMapper documentMapper,
                                   DocumentIndexer documentIndexer) {

        this.documentMapper = documentMapper;
        this.documentIndexer = documentIndexer;
    }

    private SolrInputDocument mkSolrInputDoc(int docId) {
        try {
            return documentIndexer.index(docId);
        } catch (Exception e) {
            logger.error(
                    String.format("Can't create SolrInputDocument from doc %d", docId),
                    e);
            return null;
        }
    }

	private SolrInputDocument prepareSolrInputDocForVersionUpdate(int docId){
		try {
			return documentIndexer.updateDocumentVersion(docId);
		}catch (Exception e){
			logger.error(
					String.format("Can`t prepare SolrInputDocument for update from doc %d", docId), e
			);
			return null;
		}
	}

    private String mkSolrDocsDeleteQuery(int docId) {
        return String.format("%s:%d", DocumentIndex.FIELD__META_ID, docId);
    }

    public QueryResponse query(SolrClient solrClient, SolrQuery solrQuery) throws SolrServerException, IOException {
        return solrClient.query(solrQuery);
    }

    public void addDocsToIndex(SolrClient solrClient, int docId) throws SolrServerException, IOException {
        final SolrInputDocument solrInputDoc = mkSolrInputDoc(docId);

        if (solrInputDoc != null) {
            solrClient.add(solrInputDoc);
            solrClient.commit(false, false, true);

            logger.error(String.format("Added solrInputDoc with docId %d into the index.", docId));
        }
    }

    public void deleteDocsFromIndex(SolrClient solrClient, int docId) throws SolrServerException, IOException {
        String query = mkSolrDocsDeleteQuery(docId);

        solrClient.deleteByQuery(query);
        solrClient.commit(false, false, true);
        logger.info(String.format("Removed document with docId %d from index.", docId));
    }

	public void updateDocumentVersionInIndex(SolrClient solrClient, int docId) throws SolrServerException, IOException {
		final SolrInputDocument solrInputDocument = prepareSolrInputDocForVersionUpdate(docId);

		if (solrInputDocument != null) {
			solrClient.add(solrInputDocument);
			solrClient.commit(false, false, true);

			logger.error(String.format("Updated document version in index with docId %d.", docId));
		}
	}

    public void rebuildIndex(SolrClient solrClient) {
        rebuildIndex(solrClient, indexRebuildProgress -> {
        });
    }

    @SneakyThrows
    private void rebuildIndex(SolrClient solrClient, Consumer<IndexRebuildProgress> progressCallback) {
        logger.debug("Rebuilding index.");

        final List<Integer> ids = documentMapper.getAllDocumentIds();

        final int docsCount = ids.size();
        int docNo = 0;
        final Date rebuildStartDt = new Date();
        final long rebuildStartTime = rebuildStartDt.getTime();

        progressCallback.accept(new IndexRebuildProgress(rebuildStartTime, rebuildStartTime, docsCount, docNo));

        indexedDocumentsAmount.set(0);

        for (int id : ids) {
            if (Thread.interrupted()) {
                solrClient.rollback();
                throw new InterruptedException();
            }

            SolrInputDocument solrInputDoc = mkSolrInputDoc(id);
            if (solrInputDoc != null) {
                solrClient.add(solrInputDoc);
                logger.debug("Added input doc with id " + id + " to index.");
            }
            indexedDocumentsAmount.incrementAndGet();

            docNo += 1;
            progressCallback.accept(new IndexRebuildProgress(rebuildStartTime, System.currentTimeMillis(), docsCount, docNo));
        }

        logger.debug("Deleting old documents from index.");

        solrClient.deleteByQuery(String.format("timestamp:{* TO %s}", rebuildStartDt.toInstant().toString()));
        solrClient.commit();
        indexedDocumentsAmount.set(-1);

        logger.debug("Index rebuild is complete.");
    }

    public long getAmountOfIndexedDocuments() {
        return indexedDocumentsAmount.get();
    }
}
