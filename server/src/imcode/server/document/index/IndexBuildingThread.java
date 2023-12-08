package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import org.apache.commons.lang.ClassUtils;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

class IndexBuildingThread extends Thread {

    private final static Logger log = LogManager.getLogger(IndexBuildingThread.class.getName());
    private final Map<Integer, DocumentDomainObject> idToDocumentsToAddToNewIndex = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<Integer, DocumentDomainObject> idToDocumentsToRemoveFromNewIndex = Collections.synchronizedMap(new LinkedHashMap<>());
    private final BackgroundIndexBuilder backgroundIndexBuilder;
    private final File indexDirectory;

    private final AtomicBoolean acceptUpdatesRef = new AtomicBoolean();
    private IndexDocumentFactory indexDocumentFactory;

    IndexBuildingThread(BackgroundIndexBuilder backgroundIndexBuilder, File indexDirectory,
                        IndexDocumentFactory indexDocumentFactory) {
        this.indexDirectory = indexDirectory;
        this.backgroundIndexBuilder = backgroundIndexBuilder;
        setName(ClassUtils.getShortClassName(getClass()) + "-" + getName());
        setDaemon(true);
        this.indexDocumentFactory = indexDocumentFactory;
    }

    public void run() {
        DefaultDirectoryIndex newIndex = null;
        try(CloseableThreadContext.Instance ignored=CloseableThreadContext.push(Thread.currentThread().getName());) {
            newIndex = new DefaultDirectoryIndex(indexDirectory, indexDocumentFactory);
            acceptUpdatesRef.set(true);
            newIndex.rebuild();
            considerDocumentsAddedOrRemovedDuringIndexing(newIndex);
            log.info("Index rebuild completed.");
        } catch (Throwable e) {
            log.fatal("Failed to index all documents.", e);
        } finally {
            acceptUpdatesRef.set(false);
            if(newIndex != null){
                backgroundIndexBuilder.notifyRebuildComplete(newIndex);
            }
        }
    }

    private void considerDocumentsAddedOrRemovedDuringIndexing(DirectoryIndex index) throws IndexException {
        log.debug("Considering documents added and removed during index rebuild.");

        synchronized (acceptUpdatesRef) {
            acceptUpdatesRef.set(false);
        }

        for (DocumentDomainObject document : idToDocumentsToAddToNewIndex.values()) {
            index.indexDocument(document);
        }

        for (DocumentDomainObject document : idToDocumentsToRemoveFromNewIndex.values()) {
            index.removeDocument(document);
        }
    }

    public boolean addDocument(DocumentDomainObject document) {
        return insertDocToProcessByNewIndex(document, idToDocumentsToAddToNewIndex);
    }

    public boolean removeDocument(DocumentDomainObject document) {
        return insertDocToProcessByNewIndex(document, idToDocumentsToRemoveFromNewIndex);
    }

    private boolean insertDocToProcessByNewIndex(DocumentDomainObject document, Map<Integer, DocumentDomainObject> mapToAdd) {
        synchronized (acceptUpdatesRef) {
            boolean acceptUpdates = acceptUpdatesRef.get();

            if (acceptUpdates) {
                mapToAdd.put(document.getId(), document);
            }

            return acceptUpdates;
        }
    }
}
