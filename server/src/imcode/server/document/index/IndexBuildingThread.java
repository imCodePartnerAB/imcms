package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

class IndexBuildingThread extends Thread {

    private final Set documentsToAddToNewIndex = Collections.synchronizedSet(new LinkedHashSet());
    private final Set documentsToRemoveFromNewIndex = Collections.synchronizedSet(new LinkedHashSet());
    private final static Logger log = Logger.getLogger(IndexBuildingThread.class.getName());
    private final BackgroundIndexBuilder backgroundIndexBuilder;
    private final File indexDirectory;

    private boolean indexing;
    private IndexDocumentFactory indexDocumentFactory;

    IndexBuildingThread(BackgroundIndexBuilder backgroundIndexBuilder, File indexDirectory,
                        IndexDocumentFactory indexDocumentFactory) {
        this.indexDirectory = indexDirectory;
        this.backgroundIndexBuilder = backgroundIndexBuilder;
        setName(ClassUtils.getShortClassName(getClass())+"-"+getName());
        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
        this.indexDocumentFactory = indexDocumentFactory;
    }

    public void run() {
        NDC.push(Thread.currentThread().getName());
        DefaultDirectoryIndex2 newIndex = null;
        try {
            newIndex = new DefaultDirectoryIndex2(FSDirectory.getDirectory(indexDirectory), indexDocumentFactory) ;
        } catch (IOException e) {
            throw new RuntimeException(e);  
        }

        try {
            synchronized ( this ) {
                indexing = true ;
            }
            newIndex.rebuild();
            synchronized ( this ) {
                indexing = false ;
                considerDocumentsAddedOrRemovedDuringIndexing(newIndex);
            }
            log.info("Index rebuild completed.");
        } catch ( Throwable e ) {
            log.fatal("Failed to index all documents.", e);
        } finally {
            synchronized ( this ) {
                indexing = false ;
            }
            backgroundIndexBuilder.notifyRebuildComplete(newIndex);
            NDC.pop() ;
        }
    }

    private synchronized void considerDocumentsAddedOrRemovedDuringIndexing(DirectoryIndex index) throws IndexException {
        log.debug( "Considering documents added and removed during index rebuild.");
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
