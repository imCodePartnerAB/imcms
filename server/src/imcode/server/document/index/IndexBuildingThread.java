package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.util.io.FileUtility;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.commons.lang.ClassUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

class IndexBuildingThread extends Thread {
    private Set documentsToAddToNewIndex = Collections.synchronizedSet(new LinkedHashSet());
    private Set documentsToRemoveFromNewIndex = Collections.synchronizedSet(new LinkedHashSet());
    private boolean indexing;
    private final static Logger log = Logger.getLogger(IndexBuildingThread.class.getName());
    private BackgroundIndexBuilder backgroundIndexBuilder;

    IndexBuildingThread(BackgroundIndexBuilder backgroundIndexBuilder) {
        setName(ClassUtils.getShortClassName(getClass())+" "+getName());
        this.backgroundIndexBuilder = backgroundIndexBuilder;
        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
    }

    public void run() {
        NDC.push(Thread.currentThread().getName());
        try {
            DirectoryIndex newIndex = new DirectoryIndex(backgroundIndexBuilder.getNewIndexDirectory()) ;
            synchronized ( this ) {
                indexing = true ;
            }
            newIndex.rebuild();
            synchronized ( this ) {
                indexing = false ;
                considerDocumentsAddedOrRemovedDuringIndexing(newIndex);
                FileUtility.backupRename( newIndex.getDirectory(), backgroundIndexBuilder.getIndexDirectory() );
            }
            log.info("Indexing completed.");
        } catch ( Throwable e ) {
            log.fatal("Failed to index all documents.", e);
        } finally {
            synchronized ( this ) {
                indexing = false ;
            }
            backgroundIndexBuilder.notifyIndexingDone();
            NDC.pop() ;
        }
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
