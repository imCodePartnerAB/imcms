package imcode.server.document.index.service.impl;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private final DocumentLanguages documentLanguages;

    @Autowired
    public DocumentIndexServiceOps(DocumentMapper documentMapper,
                                   DocumentIndexer documentIndexer,
                                   DocumentLanguages documentLanguages) {

        this.documentMapper = documentMapper;
        this.documentIndexer = documentIndexer;
        this.documentLanguages = documentLanguages;
    }

    private Collection<SolrInputDocument> mkSolrInputDocs(int docId) {
        return mkSolrInputDocs(docId, documentLanguages.getAll());
    }

    // todo: rewrite using DocumentDTO
    private Collection<SolrInputDocument> mkSolrInputDocs(int docId, Collection<DocumentLanguage> languages) {
        Collection<SolrInputDocument> solrInputDocs = languages.stream()
                .map(language -> (DocumentDomainObject) documentMapper.getDefaultDocument(docId, language))
                .filter(Objects::nonNull)
                .map(doc -> {
                    try {
                        return documentIndexer.index(doc);
                    } catch (Exception e) {
                        logger.error(
                                String.format("Can't create SolrInputDocument from doc %d-%d-%s",
                                        doc.getId(), doc.getVersionNo(), doc.getLanguage().getCode()),
                                e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (logger.isTraceEnabled()) {
            logger.trace(
                    String.format("Created %d solrInputDoc(s) with docId: %d and language(s): %s.",
                            solrInputDocs.size(), docId, languages)
            );
        }

        return solrInputDocs;

    }

    private String mkSolrDocsDeleteQuery(int docId) {
        return String.format("%s:%d", DocumentIndex.FIELD__META_ID, docId);
    }

    public QueryResponse query(SolrServer solrServer, SolrQuery solrQuery) throws SolrServerException {
        return solrServer.query(solrQuery);
    }

    public void addDocsToIndex(SolrServer solrServer, int docId) throws SolrServerException, IOException {
        Collection<SolrInputDocument> solrInputDocs = mkSolrInputDocs(docId);

        if (!solrInputDocs.isEmpty()) {
            solrServer.add(solrInputDocs);
            solrServer.commit(false, false, true);

            logger.info(String.format("Added %d solrInputDoc(s) with docId %d into the index.", solrInputDocs.size(), docId));
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
        final List<DocumentLanguage> languages = documentLanguages.getAll();

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

            Collection<SolrInputDocument> solrInputDocs = mkSolrInputDocs(id, languages);
            if (!solrInputDocs.isEmpty()) {
                solrServer.add(solrInputDocs);
                logger.debug(String.format("Added input docs [%s] to index.", solrInputDocs));
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
