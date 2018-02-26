package imcode.server.document.index.service.impl;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.index.DocumentIndex;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Document index service low level operations.
 * <p>
 * An instance of this class is thread save.
 */
// todo: document search might return doc which is not present in db (deleted) - return stub instead
@Component
public class DocumentIndexServiceOps {

    private static final Logger logger = Logger.getLogger(DocumentIndexServiceOps.class);

    private final DocumentMapper documentMapper;
    private final DocumentIndexer documentIndexer;

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

    private String mkSolrDocsDeleteQuery(int docId) {
        return String.format("%s:%d", DocumentIndex.FIELD__META_ID, docId);
    }

    public QueryResponse query(SolrServer solrServer, SolrQuery solrQuery) throws SolrServerException {
        return solrServer.query(solrQuery);
    }

    public void addDocsToIndex(SolrServer solrServer, int docId) throws SolrServerException, IOException {
        final SolrInputDocument solrInputDoc = mkSolrInputDoc(docId);

        if (solrInputDoc != null) {
            solrServer.add(solrInputDoc);
            solrServer.commit(false, false, true);

            logger.info(String.format("Added solrInputDoc with docId %d into the index.", docId));
        }
    }

    public void deleteDocsFromIndex(SolrServer solrServer, int docId) throws SolrServerException, IOException {
        String query = mkSolrDocsDeleteQuery(docId);

        solrServer.deleteByQuery(query);
        solrServer.commit(false, false, true);
        logger.info(String.format("Removed document with docId %d from index.", docId));
    }

    public void rebuildIndex(SolrServer solrServer) {
        rebuildIndex(solrServer, indexRebuildProgress -> {
        });
    }

    @SneakyThrows
    private void rebuildIndex(SolrServer solrServer, Consumer<IndexRebuildProgress> progressCallback) {
        logger.debug("Rebuilding index.");

        final List<Integer> ids = documentMapper.getAllDocumentIds();

        final int docsCount = ids.size();
        int docNo = 0;
        final Date rebuildStartDt = new Date();
        final long rebuildStartTime = rebuildStartDt.getTime();

        progressCallback.accept(new IndexRebuildProgress(rebuildStartTime, rebuildStartTime, docsCount, docNo));

        for (int id : ids) {
            if (Thread.interrupted()) {
                solrServer.rollback();
                throw new InterruptedException();
            }

            SolrInputDocument solrInputDoc = mkSolrInputDoc(id);
            if (solrInputDoc != null) {
                solrServer.add(solrInputDoc);
                logger.debug(String.format("Added input docs [%s] to index.", solrInputDoc));
            }

            docNo += 1;
            progressCallback.accept(new IndexRebuildProgress(rebuildStartTime, System.currentTimeMillis(), docsCount, docNo));
        }

        logger.debug("Deleting old documents from index.");

        solrServer.deleteByQuery(String.format("timestamp:{* TO %s}", DateUtil.getThreadLocalDateFormat().format(rebuildStartDt)));
        solrServer.commit();

        logger.debug("Index rebuild is complete.");
    }
}
