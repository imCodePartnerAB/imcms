package imcode.server.document.index.service.impl;

import com.imcode.imcms.api.ServiceUnavailableException;
import com.imcode.imcms.util.ThreadUtility;
import imcode.server.document.index.IndexException;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.IndexRebuildTask;
import imcode.server.document.index.service.IndexUpdateOp;
import imcode.server.document.index.service.IndexUpdateOperation;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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

    private final static Logger logger = Logger.getLogger(ManagedDocumentIndexService.class);
    private final static Object lock = new Object();

    private final ExecutorService serviceFailureExecutor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean shutdownRef = new AtomicBoolean(false);
    private final AtomicReference<Thread> indexRebuildThreadRef = new AtomicReference<>();
    private final AtomicReference<Thread> indexUpdateThreadRef = new AtomicReference<>();
    private final AtomicReference<ServiceFailure> indexWriteFailureRef = new AtomicReference<>();
    private final AtomicReference<IndexRebuildTask> indexRebuildTaskRef = new AtomicReference<>();
    private final LinkedBlockingQueue<IndexUpdateOp> indexUpdateRequests = new LinkedBlockingQueue<>(1000);

    private SolrServer solrServerReader;
    private SolrServer solrServerWriter;
    private DocumentIndexServiceOps serviceOps;
    private Consumer<ServiceFailure> failureHandler;

    ManagedDocumentIndexService(SolrServer solrServerReader, SolrServer solrServerWriter,
                                DocumentIndexServiceOps serviceOps, Consumer<ServiceFailure> failureHandler) {
        this.solrServerReader = solrServerReader;
        this.solrServerWriter = solrServerWriter;
        this.serviceOps = serviceOps;
        this.failureHandler = failureHandler;
    }

    @Override
    public QueryResponse query(SolrQuery solrQuery) {
        try {
            return serviceOps.query(solrServerReader, solrQuery);

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
        ThreadUtility.spawnDaemon(() -> {
            synchronized (lock) {
                if (!shutdownRef.get()) {
                    if (indexUpdateRequests.offer(request)) {
                        startNewIndexUpdateThread();

                    } else {
                        logger.error("Can't submit index update request [" + request + "], requests query is full.");
                        // ??? handle ???
                        // serviceErrorHandler(IndexUpdateQueryFull)
                    }
                }
            }
        });
    }

    /**
     * Creates and starts new index-rebuild-thread if there is no already running one.
     * Any exception or an interruption terminates index-rebuild-thread.
     * <p>
     * An existing index-update-thread is stopped before rebuilding happens and a new index-update-thread is started
     * immediately after running index-rebuild-thread is terminated without errors.
     */
    @Override
    public IndexRebuildTask rebuild() {
        logger.info("attempting to start new document-index-rebuild thread.");

        final ServiceFailure indexWriteFailure = indexWriteFailureRef.get();
        final Thread indexRebuildThread = indexRebuildThreadRef.get();
        final IndexRebuildTask indexRebuildTask = indexRebuildTaskRef.get();

        logger.info("attempting to start new document-index-update thread.");

        if (shutdownRef.get()) {
            final String errorMsg = "new document-index-rebuild thread can not be started - service is shut down.";
            logger.error(errorMsg);
            throw new ServiceUnavailableException(errorMsg);

        } else if (indexWriteFailure != null) {
            logger.error("new document-index-rebuild thread can not be started - previous index write attempt has failed with error [" + indexWriteFailure + "].");
            throw new RuntimeException(indexWriteFailure.getException());

        } else if (ThreadUtility.notTerminated(indexRebuildThread) && (indexRebuildTask != null)) {
            logger.info("new document-index-rebuild thread can not be started - document-index-rebuild thread [" + indexRebuildThread + "] is already running.");
            return indexRebuildTask;

        } else {
            return startNewIndexRebuildThread();
        }
    }

    @Override
    public void shutdown() {
        synchronized (lock) {
            if (shutdownRef.compareAndSet(false, true)) {
                logger.info("Attempting to shut down the service.");

                try {
                    serviceFailureExecutor.shutdown();
                    interruptIndexUpdateThreadAndAwaitTermination();
                    interruptIndexRebuildThreadAndAwaitTermination();

                    try {
                        solrServerReader.shutdown();
                    } catch (Exception e) {
                        logger.warn("An error occurred while shutting down SolrServer reader.", e);
                    }

                    try {
                        solrServerWriter.shutdown();
                    } catch (Exception e) {
                        logger.warn("An error occurred while shutting down SolrServer writer.", e);
                    }

                    logger.info("Service has been shut down.");

                } catch (Exception e) {
                    logger.warn("An error occurred while shutting down the service.", e);
                    throw e;
                }
            }
        }
    }

    @SneakyThrows
    private void interruptIndexUpdateThreadAndAwaitTermination() {
        ThreadUtility.interruptAndAwaitTermination(indexUpdateThreadRef.get());
    }

    @SneakyThrows
    private void interruptIndexRebuildThreadAndAwaitTermination() {
        ThreadUtility.interruptAndAwaitTermination(indexRebuildThreadRef.get());
    }

    private IndexRebuildTask startNewIndexRebuildThread() {
        final IndexRebuildTask indexRebuildTaskImpl = new IndexRebuildTask() {
            AtomicReference<IndexRebuildProgress> progressRef = new AtomicReference<>();

            FutureTask<Void> futureTask = new FutureTask<>(() -> serviceOps.rebuildIndex(
                    solrServerWriter, indexRebuildProgress -> progressRef.set(indexRebuildProgress)
            ), null);

            @Override
            public FutureTask<?> future() {
                return futureTask;
            }

            @Override
            public Optional<IndexRebuildProgress> progress() {
                return Optional.ofNullable(progressRef.get());
            }

        };

        indexRebuildTaskRef.set(indexRebuildTaskImpl);

        final Thread indexRebuildThread = new Thread() {

            private void submitStartNewIndexUpdateThread() {
                ThreadUtility.spawnDaemon(() -> {
                    try {
                        join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    startNewIndexUpdateThread();
                });
            }

            @Override
            public void run() {
                try {
                    interruptIndexUpdateThreadAndAwaitTermination();
                    indexUpdateRequests.clear();
                    final FutureTask<?> futureTask = indexRebuildTaskImpl.future();
                    futureTask.run();
                    futureTask.get();
                    submitStartNewIndexUpdateThread();

                } catch (InterruptedException e) {
                    logger.debug("document-index-rebuild thread [" + toString() + "] was interrupted");
                    submitStartNewIndexUpdateThread();

                } catch (CancellationException e) {
                    logger.debug("document-index-rebuild task was cancelled. document-index-rebuild thread: [" + toString() + "].");
                    submitStartNewIndexUpdateThread();

                } catch (ExecutionException e) {
                    final Throwable cause = e.getCause();
                    final ServiceFailure writeFailure = new ServiceFailure(ManagedDocumentIndexService.this, cause, ServiceFailure.Type.REBUILD);
                    logger.error("document-index-rebuild task has failed. document-index-rebuild thread: [" + toString() + "].", cause);
                    indexWriteFailureRef.set(writeFailure);
                    ThreadUtility.spawnDaemon(() -> failureHandler.accept(writeFailure));
                } finally {
                    logger.info("document-index-rebuild thread [" + toString() + "] is about to terminate.");
                }
            }
        };

        indexRebuildThreadRef.set(indexRebuildThread);
        indexRebuildThread.setDaemon(true);
        indexRebuildThread.setName("document-index-rebuild-" + indexRebuildThread.getId());
        indexRebuildThread.start();
        logger.info("new document-index-rebuild thread [" + indexRebuildThread + "] has been started");

        return indexRebuildTaskImpl;
    }

    /**
     * Creates and starts new index-update-thread if there is no already running index-update or index-rebuild thread.
     * Any exception or an interruption terminates index-update-thread.
     * <p>
     * As the final action, index-update-thread submits start of a new index-update-thread .
     */
    private void startNewIndexUpdateThread() {
        ServiceFailure indexWriteFailure;
        Thread indexRebuildThread;
        Thread indexUpdateThread;

        logger.info("attempting to start new document-index-update thread.");

        if (shutdownRef.get()) {
            logger.error("new document-index-update thread can not be started - service is shut down.");

        } else if ((indexWriteFailure = indexWriteFailureRef.get()) != null) {
            logger.error("new document-index-update thread can not be started - previous index write" +
                    " attempt has failed [" + indexWriteFailure + "].");

        } else if ((((indexRebuildThread = indexRebuildThreadRef.get())) != null)
                && (indexRebuildThread.getState() != Thread.State.TERMINATED))
        {
            logger.info("new document-index-update thread can not be started while document-index-rebuild" +
                    " thread [" + indexRebuildThread + "] is running.");

        } else if ((((indexUpdateThread = indexUpdateThreadRef.get())) != null)
                && (indexUpdateThread.getState() != Thread.State.TERMINATED))
        {
            logger.info("new document-index-update thread can not be started - document-index-update" +
                    " thread [" + indexUpdateThread + "] is already running.");

        } else {
            final Thread newIndexUpdateThread = new Thread(() -> {
                try {
                    while (true) { // todo: rewrite this shit!
                        final IndexUpdateOp updateOp = indexUpdateRequests.take();
                        final IndexUpdateOperation indexUpdateOperation = updateOp.operation();

                        if (IndexUpdateOperation.ADD.equals(indexUpdateOperation)) {
                            serviceOps.addDocsToIndex(solrServerWriter, updateOp.docId());

                        } else if (IndexUpdateOperation.DELETE.equals(indexUpdateOperation)) {
                            serviceOps.deleteDocsFromIndex(solrServerWriter, updateOp.docId());
                        }
                    }
                } catch (InterruptedException e) {
                    logger.debug("document-index-update thread [" + toString() + "] was interrupted");

                } catch (Exception e) {

                    final ServiceFailure writeFailure = new ServiceFailure(ManagedDocumentIndexService.this, e, ServiceFailure.Type.UPDATE);
                    logger.error("error in document-index-update thread [" + toString() + "].", e);
                    indexWriteFailureRef.set(writeFailure);
                    ThreadUtility.spawnDaemon(() -> failureHandler.accept(writeFailure));
                } finally {
                    logger.info("document-index-update thread [" + toString() + "] is about to terminate.");
                }
            });

            indexUpdateThreadRef.set(newIndexUpdateThread);
            newIndexUpdateThread.setDaemon(true);
            newIndexUpdateThread.setName("document-index-update-" + newIndexUpdateThread.getId() + "}");
            newIndexUpdateThread.start();
            logger.info("new document-index-update thread [" + newIndexUpdateThread + "] has been started");

        }

    }

}
