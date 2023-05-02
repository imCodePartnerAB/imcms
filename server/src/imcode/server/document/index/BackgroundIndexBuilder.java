package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.util.CounterStringFactory;
import imcode.util.ShouldNotBeThrownException;
import imcode.util.Utility;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ClassUtils;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class BackgroundIndexBuilder {
    private final static Logger log = LogManager.getLogger(BackgroundIndexBuilder.class);

    private final File indexParentDirectory;
    private final RebuildingDirectoryIndex rebuildingDirectoryIndex;

    private IndexBuildingThread indexBuildingThread;
    private long previousIndexParentDirectoryLastModified;
    private IndexDocumentFactory indexDocumentFactory;

    public BackgroundIndexBuilder(File indexParentDirectory, RebuildingDirectoryIndex rebuildingDirectoryIndex,
                                  IndexDocumentFactory indexDocumentFactory) {
        this.indexParentDirectory = indexParentDirectory;
        this.rebuildingDirectoryIndex = rebuildingDirectoryIndex;
        indexParentDirectory.setLastModified(System.currentTimeMillis());
        previousIndexParentDirectoryLastModified = indexParentDirectory.lastModified();
        this.indexDocumentFactory = indexDocumentFactory;
    }

    private static File getNewIndexDirectory(File indexParentDirectory) {
        return (File) Utility.findMatch(new CounterFileFactory(indexParentDirectory), new UniqueFilePredicate());
    }

    public synchronized void start() {
	    try (CloseableThreadContext.Instance ignored =
			         CloseableThreadContext.push(
					         ClassUtils.getShortClassName(getClass()) + "-" + Utility.numberToAlphaNumerics(System.identityHashCode(this)));) {

            touchIndexParentDirectory();

            if (null != indexBuildingThread && indexBuildingThread.isAlive()) {
                log.info("Ignoring request to build new index. Already in progress.");
                return;
            }

            File indexDirectory = getNewIndexDirectory(indexParentDirectory);
            if (!indexDirectory.mkdirs()) {
                log.warn("Failed to create new index directory. Will try again next time.");
                return;
            }

            log.info("Created directory " + indexDirectory);

            rememberIndexParentDirectoryLastModified();

            try {
                log.info("Starting index rebuild thread.");
                indexBuildingThread = new IndexBuildingThread(this, indexDirectory, indexDocumentFactory);
                indexBuildingThread.start();
            } catch (IllegalThreadStateException itse) {
                throw new ShouldNotBeThrownException(itse);
            }
        }
    }

    boolean otherProcessModifiedIndexDirectory() {
        if (previousIndexParentDirectoryLastModified != 0) {
            long lastModified = indexParentDirectory.lastModified();
            if (lastModified > previousIndexParentDirectoryLastModified) {
                log.trace("Expected last modified " + previousIndexParentDirectoryLastModified + " but got " + lastModified);
                rememberIndexParentDirectoryLastModified();
                return true;
            }
        }
        return false;
    }

    void touchIndexParentDirectory() {
        indexParentDirectory.setLastModified(System.currentTimeMillis());
        rememberIndexParentDirectoryLastModified();
    }

    public synchronized boolean addDocument(DocumentDomainObject document) {
        return indexBuildingThread != null && indexBuildingThread.addDocument(document);
    }

    public synchronized boolean removeDocument(DocumentDomainObject document) {
        return indexBuildingThread != null && indexBuildingThread.removeDocument(document);
    }

    public synchronized void notifyRebuildComplete(DefaultDirectoryIndex newIndex) {
        rebuildingDirectoryIndex.notifyRebuildComplete(newIndex);
        rememberIndexParentDirectoryLastModified();
    }

    public boolean isIndexBuildingThreadAlive(){
        return null != indexBuildingThread && indexBuildingThread.isAlive();
    }

    private void rememberIndexParentDirectoryLastModified() {
        previousIndexParentDirectoryLastModified = indexParentDirectory.lastModified();
    }

    private static class CounterFileFactory extends CounterStringFactory {

        private File parentDirectory;

        CounterFileFactory(File parentDirectory) {
            super(1);
            this.parentDirectory = parentDirectory;
        }

        public Object create() {
            return new File(parentDirectory, (String) super.create());
        }

    }

    private static class UniqueFilePredicate implements Predicate {

        public boolean evaluate(Object object) {
            File file = (File) object;
            return !file.exists();
        }
    }
}
