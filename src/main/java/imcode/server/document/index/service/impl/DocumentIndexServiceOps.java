package imcode.server.document.index.service.impl;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.DateUtil;

import java.net.URLDecoder;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Document index service low level operations.
 * <p>
 * An instance of this class is thread save.
 */
// todo: document search might return doc which is not present in db (deleted) - return stub instead
public class DocumentIndexServiceOps {

    private static final Logger logger = Logger.getLogger(DocumentIndexServiceOps.class);

    private final DocumentMapper documentMapper;

    private final DocumentIndexer documentIndexer;

    public DocumentIndexServiceOps(DocumentMapper documentMapper, DocumentIndexer documentIndexer) {
        this.documentMapper = documentMapper;
        this.documentIndexer = documentIndexer;
    }

    private <T> T withExceptionWrapper(Callable<? extends T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new SolrInputDocumentCreateException(e);
        }
    }

    private <T> T withRuntimeExceptionWrapper(Callable<? extends T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new SolrInputDocumentCreateException(e);
        }
    }


    public Collection<SolrInputDocument> mkSolrInputDocs(int docId) throws SolrInputDocumentCreateException {
        return withExceptionWrapper(() ->
                        mkSolrInputDocs(docId, documentMapper.getImcmsServices().getDocumentLanguages().getAll())
        );
    }


    public Collection<SolrInputDocument> mkSolrInputDocs(int docId, Collection<DocumentLanguage> languages)
            throws SolrInputDocumentCreateException {

        Collection<SolrInputDocument> solrInputDocs = languages.stream()
                .map(language -> (DocumentDomainObject) documentMapper.getDefaultDocument(docId, language))
                .filter(Objects::nonNull)
                .map(documentIndexer::index)
                .collect(Collectors.toList());

        if (logger.isTraceEnabled()) {
            logger.trace(
                    String.format(
                            "Created %d solrInputDoc(s) with docId: %d and language(s): %s.",
                            solrInputDocs.size(), docId, languages
                    )
            );
        }

        return solrInputDocs;

    }

    public String mkSolrDocsDeleteQuery(int docId) {
        return String.format("%s:%d", DocumentIndex.FIELD__META_ID, docId);
    }

    public QueryResponse query(SolrServer solrServer, SolrQuery solrQuery) throws SolrInputDocumentCreateException {
        return withExceptionWrapper(() -> {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Searching using SOLr query: %s.", URLDecoder.decode(solrQuery.toString(), "UTF-8")));
            }

            return solrServer.query(solrQuery);

        });
    }


    public void addDocsToIndex(SolrServer solrServer, int docId) throws SolrInputDocumentCreateException {
        Collection<SolrInputDocument> solrInputDocs = mkSolrInputDocs(docId);

        if (!solrInputDocs.isEmpty()) {
            withRuntimeExceptionWrapper(() -> {
                solrServer.add(solrInputDocs);
                solrServer.commit();
                return null;
            });

            logger.trace(String.format("Added %d solrInputDoc(s) with docId %d into the index.", solrInputDocs.size(), docId));
        }
    }


    public void deleteDocsFromIndex(SolrServer solrServer, int docId) {
        String query = mkSolrDocsDeleteQuery(docId);

        withRuntimeExceptionWrapper(() -> {
            solrServer.deleteByQuery(query);
            solrServer.commit();
            return null;
        });
    }


    public void rebuildIndex(SolrServer solrServer, Consumer<IndexRebuildProgress> progressCallback)
            throws SolrInputDocumentCreateException, InterruptedException {
        logger.debug("Rebuilding index.");

        List<Integer> ids = documentMapper.getAllDocumentIds();
        List<DocumentLanguage> languages = documentMapper.getImcmsServices().getDocumentLanguages().getAll();

        int docsCount = ids.size();
        int docNo = 0;
        Date rebuildStartDt = new Date();
        long rebuildStartTime = rebuildStartDt.getTime();

        progressCallback.accept(new IndexRebuildProgress(rebuildStartTime, rebuildStartTime, docsCount, docNo));

        for (int id : ids) {
            if (Thread.interrupted()) {
                withRuntimeExceptionWrapper(solrServer::rollback);
                throw new InterruptedException();
            }

            Collection<SolrInputDocument> solrInputDocs = mkSolrInputDocs(id, languages);
            if (!solrInputDocs.isEmpty()) {
                withRuntimeExceptionWrapper(() -> solrServer.add(solrInputDocs));
                logger.debug(String.format("Added input docs [%s] to index.", solrInputDocs));
            }

            docNo += 1;
            progressCallback.accept(new IndexRebuildProgress(rebuildStartTime, System.currentTimeMillis(), docsCount, docNo));
        }

        logger.debug("Deleting old documents from index.");
        withRuntimeExceptionWrapper(() -> {
            solrServer.deleteByQuery(String.format("timestamp:{* TO %s}", DateUtil.getThreadLocalDateFormat().format(rebuildStartDt)));
            solrServer.commit();
            return null;
        });

        logger.debug("Index rebuild is complete.");
    }
}