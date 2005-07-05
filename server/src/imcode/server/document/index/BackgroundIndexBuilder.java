package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.util.ShouldNotBeThrownException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.lucene.index.IndexReader;

import java.io.File;
import java.io.IOException;

public class BackgroundIndexBuilder {
    private final static Logger log = Logger.getLogger(BackgroundIndexBuilder.class.getName());

    private final File indexDirectory;

    private IndexBuildingThread indexBuildingThread;
    private File newIndexDirectory;
    private long previousIndexDirectoryLastModified;
    private long previousNewIndexDirectoryLastModified;

    public BackgroundIndexBuilder(File indexDirectory) {
        this.indexDirectory = indexDirectory;
        this.newIndexDirectory = getDirectoryForNewIndex(indexDirectory);
        previousIndexDirectoryLastModified = indexDirectory.lastModified() ;
    }

    public synchronized void start() {
        try {
            NDC.push(ClassUtils.getShortClassName(getClass())+" "+Integer.toString(System.identityHashCode(this),Character.MAX_RADIX));

            if ( null == indexBuildingThread || !indexBuildingThread.isAlive() ) {
                indexBuildingThread = new IndexBuildingThread(this);
            } else {
                log.debug("Ignoring request to build new index. Already in progress.");
                return;
            }

            if (previousIndexDirectoryLastModified != 0 && indexDirectory.lastModified() != previousIndexDirectoryLastModified ) {
                log.debug("Other process modified index directory. Aborting.") ;
                throw new AlreadyIndexingException() ;
            }

            if ( newIndexDirectory.mkdir() ) {
                resetPreviousNewIndexDirectoryLastModified();
            } else if ( newIndexDirectory.exists() ) {
                if ( 0 == previousNewIndexDirectoryLastModified ) {
                    log.debug("New index directory already exists. Will abort next time.");
                    rememberNewIndexDirectoryLastModified();
                    return;
                } else {
                    if ( newIndexDirectory.lastModified() != previousNewIndexDirectoryLastModified ) {
                        log.info("New index directory existed previously, and has been modified by other process. Aborting.");
                        rememberNewIndexDirectoryLastModified();
                        throw new AlreadyIndexingException();
                    } else {
                        log.debug("Deleting new index directory which existed on previous indexing and is probably stale.");
                        try {
                            FileUtils.deleteDirectory(newIndexDirectory);
                            rememberNewIndexDirectoryLastModified();
                        } catch ( IOException e ) {
                            log.warn("Failed to delete probably stale new index directory.");
                        }
                    }
                }
            } else {
                log.warn("Failed to create new index directory. Will try again next time.");
                return;
            }

            try {
                log.debug("Starting indexing thread.") ;
                indexBuildingThread.start();
            } catch ( IllegalThreadStateException itse ) {
                throw new ShouldNotBeThrownException(itse);
            }
        } finally {
            NDC.pop();
        }
    }

    private void resetPreviousNewIndexDirectoryLastModified() {
        previousNewIndexDirectoryLastModified = 0;
    }

    private void rememberNewIndexDirectoryLastModified() {
        previousNewIndexDirectoryLastModified = newIndexDirectory.lastModified();
    }

    private boolean isIndexing() {
        return null != indexBuildingThread;
    }

    public void addDocument(DocumentDomainObject document) {
        if ( isIndexing() ) {
            indexBuildingThread.addDocument(document);
        }
    }

    public void removeDocument(DocumentDomainObject document) {
        if ( isIndexing() ) {
            indexBuildingThread.removeDocument(document);
        }
    }

    private File getDirectoryForNewIndex(File indexDirectory) {
        File newIndexDirectory = indexDirectory;
        if ( IndexReader.indexExists(newIndexDirectory) ) {
            newIndexDirectory = new File(newIndexDirectory.getParentFile(), newIndexDirectory.getName()
                                                                            + ".new");
        }
        return newIndexDirectory;
    }

    public File getNewIndexDirectory() {
        return newIndexDirectory;
    }

    public File getIndexDirectory() {
        return indexDirectory;
    }

    public void notifyIndexingDone() {
        previousIndexDirectoryLastModified = indexDirectory.lastModified() ;
    }
}
