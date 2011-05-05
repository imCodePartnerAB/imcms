package imcode.server.document.index;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.solr.client.solrj.SolrServer;

class IndexBuildingThread extends Thread {

    private final Set<Integer> documentsToAddToNewIndex = Collections.synchronizedSet(new LinkedHashSet<Integer>());
    private final Set<Integer> documentsToRemoveFromNewIndex = Collections.synchronizedSet(new LinkedHashSet<Integer>());
    private final static Logger log = Logger.getLogger(IndexBuildingThread.class.getName());
    private final BackgroundIndexBuilder backgroundIndexBuilder;

    private boolean indexing;
    private SolrServer solrServer;
    private SolrIndexDocumentFactory indexDocumentFactory;

    IndexBuildingThread(BackgroundIndexBuilder backgroundIndexBuilder,
                        SolrServer solrServer,
                        SolrIndexDocumentFactory indexDocumentFactory) {
        this.backgroundIndexBuilder = backgroundIndexBuilder;
        setName(ClassUtils.getShortClassName(getClass())+"-"+getName());
        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
        this.solrServer = solrServer;
        this.indexDocumentFactory = indexDocumentFactory;
    }

    public void run() {
        NDC.push(Thread.currentThread().getName());
        SolrDirectoryIndex newIndex = new SolrDirectoryIndex(solrServer, indexDocumentFactory) ;
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
        for (Iterator<Integer> iterator = documentsToAddToNewIndex.iterator(); iterator.hasNext(); ) {
            Integer docId = iterator.next();
            index.indexDocument(docId);
        }
        
        for (Iterator<Integer> iterator = documentsToRemoveFromNewIndex.iterator(); iterator.hasNext(); ) {
            Integer docId = iterator.next();
            index.removeDocument(docId);
            iterator.remove();
        }
    }

    public synchronized void addDocument(Integer docId) {
        if (indexing) {
            documentsToAddToNewIndex.add(docId) ;
        }
    }

    public synchronized void removeDocument(Integer docId) {
        if (indexing) {
            documentsToRemoveFromNewIndex.add(docId) ;
        }
    }
}
