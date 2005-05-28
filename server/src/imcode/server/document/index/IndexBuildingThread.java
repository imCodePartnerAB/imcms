package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.util.io.FileUtility;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;

import java.io.File;
import java.util.*;

class IndexBuildingThread extends Thread {
    private Set documentsToAddToNewIndex = Collections.synchronizedSet(new LinkedHashSet());
    private Set documentsToRemoveFromNewIndex = Collections.synchronizedSet(new LinkedHashSet());
    private final File indexDirectory;
    private boolean indexing;
    private final static Logger log = Logger.getLogger(IndexBuildingThread.class.getName());

    IndexBuildingThread(File indexDirectory) {
        super("Background indexing thread");
        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
        this.indexDirectory = indexDirectory ;
    }

    public void run() {
        try {
            File newIndexDirectory = getDirectoryForNewIndex( indexDirectory );
            DirectoryIndex newIndex = new DirectoryIndex(newIndexDirectory) ;
            synchronized ( this ) {
                indexing = true ;
            }
            newIndex.rebuild();
            synchronized ( this ) {
                indexing = false ;
                considerDocumentsAddedOrRemovedDuringIndexing(newIndex);
                FileUtility.backupRename( newIndex.getDirectory(), indexDirectory );
            }
            log.info("Indexing completed.");
        } catch ( Throwable e ) {
            log.fatal("Failed to index all documents.", e);
        } finally {
            synchronized ( this ) {
                indexing = false ;
            }
        }
    }

    private File getDirectoryForNewIndex(File indexDirectory) {
        File newIndexDirectory = indexDirectory ;
        if (IndexReader.indexExists( newIndexDirectory )) {
            newIndexDirectory = new File(newIndexDirectory.getParentFile(), newIndexDirectory.getName()
                                                                             + ".new");
        }
        return newIndexDirectory;
    }

    private synchronized void considerDocumentsAddedOrRemovedDuringIndexing(DirectoryIndex index) throws IndexException {
        log.debug( "Considering documents added and removed during indexing.");
        for (Iterator iterator = documentsToAddToNewIndex.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject)iterator.next();
            index.indexDocument(document);
            iterator.remove();
        }
        for (Iterator iterator = documentsToRemoveFromNewIndex.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject)iterator.next();
            index.removeDocument(document);
            iterator.remove();
        }
    }

    public synchronized void addDocument(DocumentDomainObject document) {
        if (indexing) {
            documentsToAddToNewIndex.add(document) ;
        }
    }

    public synchronized void removeDocument(DocumentDomainObject document) {
        if (indexing) {
            documentsToRemoveFromNewIndex.add(document) ;
        }
    }
}
