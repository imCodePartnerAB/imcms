package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

class IndexBuildingThread extends Thread {

    private final Set<DocumentDomainObject> documentsToAddToNewIndex = Collections.synchronizedSet(new LinkedHashSet<DocumentDomainObject>());
    private final Set<DocumentDomainObject> documentsToRemoveFromNewIndex = Collections.synchronizedSet(new LinkedHashSet<DocumentDomainObject>());
    private final static Logger log = Logger.getLogger(IndexBuildingThread.class.getName());
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
        NDC.push(Thread.currentThread().getName());
        DefaultDirectoryIndex newIndex = new DefaultDirectoryIndex(indexDirectory, indexDocumentFactory);
        try {
            acceptUpdatesRef.set(true);
            newIndex.rebuild();
            considerDocumentsAddedOrRemovedDuringIndexing(newIndex);
            log.info("Index rebuild completed.");
        } catch (Throwable e) {
            log.fatal("Failed to index all documents.", e);
        } finally {
            acceptUpdatesRef.set(false);
            backgroundIndexBuilder.notifyRebuildComplete(newIndex);
            NDC.pop();
        }
    }

    private void considerDocumentsAddedOrRemovedDuringIndexing(DirectoryIndex index) throws IndexException {
        log.debug("Considering documents added and removed during index rebuild.");

        synchronized (acceptUpdatesRef) {
            acceptUpdatesRef.set(false);
        }

        for (DocumentDomainObject document : documentsToAddToNewIndex) {
            index.indexDocument(document);
        }

        for (DocumentDomainObject document : documentsToRemoveFromNewIndex) {
            index.removeDocument(document);
        }
    }

    public boolean addDocument(DocumentDomainObject document) {
        synchronized (acceptUpdatesRef) {
            boolean acceptUpdates = acceptUpdatesRef.get();

            if (acceptUpdates) {
                documentsToAddToNewIndex.add(document);
            }

            return acceptUpdates;
        }
    }

    public boolean removeDocument(DocumentDomainObject document) {
        synchronized (acceptUpdatesRef) {
            boolean acceptUpdates = acceptUpdatesRef.get();

            if (acceptUpdates) {
                documentsToRemoveFromNewIndex.add(document);
            }

            return acceptUpdates;
        }
    }
}
