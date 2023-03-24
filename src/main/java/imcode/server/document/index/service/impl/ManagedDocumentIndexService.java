package imcode.server.document.index.service.impl;

import com.imcode.imcms.util.ThreadUtility;
import imcode.server.document.index.IndexException;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.IndexUpdateOp;
import imcode.server.document.index.service.IndexUpdateOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Implements all DocumentIndexService functionality.
 * Ensures that update and rebuild operations never run concurrently.
 * <p>
 * Indexing errors are handled asynchronously as ManagedSolrDocumentIndexService.IndexError events.
 * On index write (update or rebuild) failure the service stops processing write requests.
 * <p>
 * The business-logic (like rebuild scheduling and index recovery) is implemented on higher levels.
 */
// translated from scala
public class ManagedDocumentIndexService implements DocumentIndexService {

    private final static Logger logger = LogManager.getLogger(ManagedDocumentIndexService.class);
    private final static Object lock = new Object();

    private final ExecutorService serviceFailureExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService indexUpdateExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService indexRebuildExecutor = Executors.newSingleThreadExecutor();

    private final AtomicBoolean shutdownRef = new AtomicBoolean(false);
    private final LinkedBlockingQueue<IndexUpdateOp> indexUpdateRequests = new LinkedBlockingQueue<>();

    private volatile Future indexUpdateFuture = CompletableFuture.completedFuture(null);
    private volatile Future indexRebuildFuture = CompletableFuture.completedFuture(null);

    private SolrClient solrClientReader;
    private SolrClient solrClientWriter;
    private DocumentIndexServiceOps serviceOps;
    private Consumer<ServiceFailure> failureHandler;

    ManagedDocumentIndexService(SolrClient solrClientReader, SolrClient solrClientWriter,
                                DocumentIndexServiceOps serviceOps, Consumer<ServiceFailure> failureHandler) {
        this.solrClientReader = solrClientReader;
        this.solrClientWriter = solrClientWriter;
        this.serviceOps = serviceOps;
        this.failureHandler = failureHandler;
    }

    @Override
    public QueryResponse query(SolrQuery solrQuery) {
        try {
            return serviceOps.query(solrClientReader, solrQuery);

        } catch (Exception e) {
            logger.error("Search error. solrQuery: " + solrQuery, e);

            serviceFailureExecutor.submit(() -> failureHandler.accept(new ServiceFailure(
                    ManagedDocumentIndexService.this, e, ServiceFailure.Type.SEARCH
            )));

            throw new IndexException(e);
        }
    }

    @Override
    public void update(IndexUpdateOp request) {
        if (!shutdownRef.get()) {
	        indexUpdateRequests.removeIf(indexUpdateOp -> indexUpdateOp.equals(request));
            if (indexUpdateRequests.offer(request)) {
                invokeIndexUpdateThread();

            } else {
                logger.error("Can't submit index update request [" + request + "], requests query is full.");
                // todo: decide what to do here or how to prevent
            }
        }
    }

    /**
     * Creates and starts new index-rebuild-thread if there is no already running one.
     * Any exception or an interruption terminates index-rebuild-thread.
     * <p>
     * An existing index-update-thread is stopped before rebuilding happens and a new index-update-thread is started
     * immediately after running index-rebuild-thread is terminated without errors.
     */
    @Override
    public Future rebuild() {
        if (!shutdownRef.get() && indexRebuildFuture.isDone()) {
            synchronized (lock) {
                if (indexRebuildFuture.isDone()) {
                    indexRebuildFuture = indexRebuildExecutor.submit(this::rebuildIndexes);
                }
            }
        }

        return indexRebuildFuture;
    }

    @Override
    public void shutdown() {
        synchronized (lock) {
            if (shutdownRef.compareAndSet(false, true)) {
                logger.info("Attempting to shut down the service.");

                try {
                    indexUpdateRequests.clear();
                    serviceFailureExecutor.shutdown();
                    indexUpdateExecutor.shutdown();
                    indexRebuildExecutor.shutdown();

                    try {
                        solrClientReader.commit(true, true, true);
                        solrClientReader.close();
                    } catch (Exception e) {
                        logger.warn("An error occurred while shutting down SolrServer reader.", e);
                    }

                    if (!solrClientReader.equals(solrClientWriter)) {
                        try {
                            solrClientWriter.commit(true, true, true);
                            solrClientWriter.close();
                        } catch (Exception e) {
                            logger.warn("An error occurred while shutting down SolrServer writer.", e);
                        }
                    }

                    logger.info("Service has been shut down.");

                } catch (Exception e) {
                    logger.warn("An error occurred while shutting down the service.", e);
                    throw e;
                }
            }
        }
    }

    @Override
    public boolean isUpdateDone() {
        return indexUpdateFuture.isDone() && indexUpdateRequests.isEmpty();
    }

    private void rebuildIndexes() {
        indexUpdateRequests.clear();
        serviceOps.rebuildIndex(solrClientWriter);
        indexUpdateFuture = indexUpdateExecutor.submit(this::updateIndexes);
    }

    private void invokeIndexUpdateThread() {
        if (indexUpdateFuture.isDone() && indexRebuildFuture.isDone()) {
            indexUpdateFuture = indexUpdateExecutor.submit(this::updateIndexes);
            logger.info("Submitted new index update thread.");
        }
    }

    private void updateIndexes() {
        logger.info("Index update thread invoked for " + indexUpdateRequests.size() + " update requests.");

        while (!indexUpdateRequests.isEmpty()) {
            try {
                final IndexUpdateOp updateOp = indexUpdateRequests.take();
                final IndexUpdateOperation indexUpdateOperation = updateOp.operation();

	            final int docId = updateOp.docId();
	            switch (indexUpdateOperation) {
		            case ADD -> serviceOps.addDocsToIndex(solrClientWriter, docId);
		            case DELETE -> serviceOps.deleteDocsFromIndex(solrClientWriter, docId);
		            case UPDATE_VERSION -> serviceOps.updateDocumentVersionInIndex(solrClientWriter, docId);
	            }
            } catch (InterruptedException e) {
                logger.debug("document-index-update thread [" + toString() + "] was interrupted");

            } catch (Exception e) {
                final ServiceFailure writeFailure = new ServiceFailure(
                        ManagedDocumentIndexService.this, e, ServiceFailure.Type.UPDATE
                );
                logger.error("error in document-index-update thread [" + toString() + "].", e);
                ThreadUtility.spawnDaemon(() -> failureHandler.accept(writeFailure));
            }
        }

        logger.info("Index update thread finished.");
    }
}
