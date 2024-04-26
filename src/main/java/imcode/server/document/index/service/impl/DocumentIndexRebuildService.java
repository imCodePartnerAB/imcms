package imcode.server.document.index.service.impl;

import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.IndexServiceFactory;
import imcode.server.document.index.service.IndexUpdateOp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.apache.solr.common.SolrException.ErrorCode.*;

public class DocumentIndexRebuildService implements DocumentIndexService, IndexRebuildScheduler {

    private final static Logger logger = LogManager.getLogger(DocumentIndexRebuildService.class);
    private final Object lock = new Object();
    private final AtomicBoolean shutdownRef = new AtomicBoolean(false);
    private final AtomicReference<DocumentIndexService> serviceRef = new AtomicReference<>(
            new UnavailableDocumentIndexService()
    );
    private final String pathToSolr;
    private final BiFunction<String, Boolean, SolrClient> solrClientFactory;
    private final IndexServiceFactory documentIndexServiceFactory;

    private Consumer<ServiceFailure> failureHandler = failure -> {
        try {
            throw failure.getException();

        } catch (SolrException e) {
            if (EnumSet.of(BAD_REQUEST).contains(getErrorCode(e.code()))) {
                logger.warn("Bad search request", e);

            } else if (EnumSet.of(SERVER_ERROR, FORBIDDEN, UNAUTHORIZED, NOT_FOUND).contains(getErrorCode(e.code()))) {
                logger.fatal("Configuration error. Shutting down the service.", e);
                synchronized (lock) {
                    if (serviceRef.get().equals(failure.getService())) {
                        shutdown();
                    }
                }
            } else {
                logger.error("Received solr error", e);
            }
        } catch (IOException e) {
            logger.error("Received solr error", e);
            replaceManagedServerInstance(failure);

        } catch (SolrServerException e) {
            logger.error("Received solr error", e);

            if (e.getCause() instanceof IOException) {
                replaceManagedServerInstance(failure);
            }
        } catch (Throwable e) {
            logger.error("Received solr error", e);
        }
        // ??? distinguish between Search & Alter (UPDATE | REBUILD) ???
        // ??? logger.fatal("No more index update or rebuild requests will be accepted.", e) ???
    };

    public DocumentIndexRebuildService(String pathToSolr, BiFunction<String, Boolean, SolrClient> solrClientFactory,
                                       long periodInMinutes, IndexServiceFactory documentIndexServiceFactory) {
        this.pathToSolr = pathToSolr;
        this.solrClientFactory = solrClientFactory;
        this.documentIndexServiceFactory = documentIndexServiceFactory;

        serviceRef.set(newManagedService(false));
        setRebuildIntervalInMinutes(periodInMinutes);
    }

    @Override
    public QueryResponse query(SolrQuery solrQuery) {
        return serviceRef.get().query(solrQuery);
    }

    @Override
    public void update(IndexUpdateOp request) {
        serviceRef.get().update(request);
    }

    @Override
    public Future rebuild() {
        return serviceRef.get().rebuild();
    }

    @Override
    public void shutdown() {
        synchronized (lock) {
            if (shutdownRef.compareAndSet(false, true)) {

                logger.info("Attempting to shut down the service.");

                try {
                    serviceRef.getAndSet(new UnavailableDocumentIndexService()).shutdown();
                    logger.info("Service has been shut down.");

                } catch (Exception e) {
                    logger.error("Error while shutting down solr", e);
                }
            }
        }
    }

    @Override
    public boolean isUpdateDone() {
        return serviceRef.get().isUpdateDone();
    }

    private void replaceManagedServerInstance(ServiceFailure failure) {
        synchronized (lock) {
            if (serviceRef.compareAndSet(failure.getService(), new UnavailableDocumentIndexService())) {
                logger.error("Unrecoverable index error. Managed service instance have to be replaced.", failure.getException());
                failure.getService().shutdown();

                if (shutdownRef.get()) {
                    logger.info("New managed service instance can not be created - service has been shout down.");
                } else {
                    logger.info("Creating new instance of managed service. Data directory will be recreated.");
                    final DocumentIndexService newService = newManagedService(true);
                    serviceRef.set(newService);
                    newService.rebuild();

                    logger.info("New managed service instance has been created.");
                }
            }
        }
    }

    private DocumentIndexService newManagedService(boolean recreateDataDir) {
        final SolrClient solrClient = solrClientFactory.apply(pathToSolr, recreateDataDir);
        return documentIndexServiceFactory.create(solrClient, solrClient, failureHandler);
    }
}
