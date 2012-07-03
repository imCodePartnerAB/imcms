package imcode.server.document.index;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.solr.DocumentIndexer;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.common.util.NamedList;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RebuildingDirectoryIndex implements DocumentIndex {

    private final static Logger log = Logger.getLogger(RebuildingDirectoryIndex.class.getName());

    private final static String SOLR_INDEX_PARAM = "index";
    private final static String SOLR_INDEX_LAST_MODIFIED_PARAM = "lastModified";
    private final static String SOLR_INDEX_NUMDOCS_PARAM = "numDocs";

    private final BackgroundIndexBuilder backgroundIndexBuilder;
    private final long indexRebuildSchedulePeriodInMilliseconds;
    private final Timer scheduledIndexRebuildTimer = new Timer(true);
    private IndexRebuildTimerTask currentIndexRebuildTimerTask ;
    private DocumentMapper documentMapper;

    private DirectoryIndex index = new NullDirectoryIndex();

    public RebuildingDirectoryIndex(SolrServer solrServer, DocumentMapper documentMapper,
                                    float indexRebuildSchedulePeriodInMinutes,
                                    DocumentIndexer indexDocumentFactory) {
        this.documentMapper = documentMapper;
        indexRebuildSchedulePeriodInMilliseconds = (long) ( indexRebuildSchedulePeriodInMinutes * DateUtils.MILLIS_PER_MINUTE );
        backgroundIndexBuilder = new BackgroundIndexBuilder(solrServer, this, indexDocumentFactory);

        try {
            NamedList solrParams = solrServer.request(new LukeRequest());

            NamedList indexParams = (NamedList)solrParams.get(SOLR_INDEX_PARAM);
            int numDocs = (Integer)indexParams.get(SOLR_INDEX_NUMDOCS_PARAM);
            Date lastModifiedDate = (Date)indexParams.get(SOLR_INDEX_LAST_MODIFIED_PARAM);

            long indexModifiedTime = 0;
            if ( numDocs > 0) {
                indexModifiedTime = lastModifiedDate.getTime();
                index = new SolrDirectoryIndex(solrServer, documentMapper, indexDocumentFactory);
            } else {
                rebuildBecauseOfError("No existing index.", null);
            }

            if ( isSchedulingIndexRebuilds() ) {
                log.info("First index rebuild scheduled at " + formatDatetime(restartIndexRebuildScheduling(indexModifiedTime)));
            } else {
                log.info("Scheduling of index rebuilds is disabled.");
            }
        } catch (SolrServerException e) {
            throw new IndexException(e);
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    private synchronized Date restartIndexRebuildScheduling(long indexModifiedTime) {
        if ( !isSchedulingIndexRebuilds() ) {
            return null ;
        }
        long time = System.currentTimeMillis();
        Date nextTime = new Date(indexModifiedTime + indexRebuildSchedulePeriodInMilliseconds);
        if ( nextTime.getTime() < time ) {
            nextTime.setTime(time);
        }
        restartIndexRebuildScheduling(nextTime);
        return nextTime;
    }

    private synchronized void restartIndexRebuildScheduling(Date nextTime) {
        if (null != currentIndexRebuildTimerTask) {
            currentIndexRebuildTimerTask.cancel() ;
            log.trace("Canceled existing index rebuild timer task.") ;
        }
        try {
            log.debug("Restarting scheduling of index rebuilds. First rebuild at "+formatDatetime(nextTime)+".") ;
//            backgroundIndexBuilder.touchIndexParentDirectory();
            currentIndexRebuildTimerTask = new IndexRebuildTimerTask(indexRebuildSchedulePeriodInMilliseconds, backgroundIndexBuilder);
            scheduledIndexRebuildTimer.scheduleAtFixedRate(currentIndexRebuildTimerTask, nextTime, indexRebuildSchedulePeriodInMilliseconds);
        } catch ( IllegalStateException ise ) {
            log.error("Failed to start index rebuild scheduling.", ise);
        }
    }

    private boolean isSchedulingIndexRebuilds() {
        return indexRebuildSchedulePeriodInMilliseconds > 0;
    }

    private static void sortFilesByLastModifiedWithLatestFirst(File[] indexDirectories) {
        Arrays.sort(indexDirectories, new Comparator() {
            public int compare(Object o1, Object o2) {
                File f1 = (File) o1;
                File f2 = (File) o2;
                return new Long(f2.lastModified()).compareTo(new Long(f1.lastModified()));
            }
        });
    }

    static String formatDatetime(Date nextExecutionTime) {
        return new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(nextExecutionTime);
    }

    public void indexDocument(DocumentDomainObject document) {
        indexDocuments(document.getId());
    }

    public void indexDocuments(int docId) {
        log.debug("Adding document.");
        backgroundIndexBuilder.addDocument(docId);
        try {
            index.indexDocuments(docId);
        } catch ( IndexException e ) {
            rebuildBecauseOfError("Failed to add document " + docId + " to index.", e);
        }
    }

    public void removeDocument(DocumentDomainObject document) {
        removeDocuments(document.getId());
    }

    public void removeDocuments(int docId) {
        log.debug("Removing document.");
        backgroundIndexBuilder.removeDocument(docId);
        try {
            index.removeDocuments(docId);
        } catch ( IndexException e ) {
            rebuildBecauseOfError("Failed to remove document " + docId + " from index.", e);
        }
    }

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        try {
            List<DocumentDomainObject> documents = index.search(query, searchingUser);
            if ( index.isInconsistent() ) {
                rebuildBecauseOfError("Index is inconsistent.", null);
            }
            return documents;
        } catch ( IndexException ex ) {
            rebuildBecauseOfError("Search failed.", ex);
            return Collections.emptyList();
        }
    }

    private void rebuildBecauseOfError(String message, IndexException ex) {
        log.error(message+" Starting index rebuild.", ex);
        rebuild();
    }

    public void rebuild() {
        if (isSchedulingIndexRebuilds()) {
            restartIndexRebuildScheduling(new Date()) ;
        } else {
            backgroundIndexBuilder.start();
        }
    }

    void notifyRebuildComplete(DirectoryIndex newIndex) {
        DirectoryIndex oldIndex = index;
        index = newIndex;
        if (!oldIndex.equals(index) ) {
            oldIndex.delete();
        }
    }

    private static class NullDirectoryIndex implements DirectoryIndex {

        public boolean isInconsistent() {
            return false;
        }

        public void delete() {

        }

        public void indexDocument(DocumentDomainObject document) throws IndexException {
        }

        public void removeDocument(DocumentDomainObject document) throws IndexException {
        }

        public void indexDocuments(int docId) throws IndexException {
        }

        public void removeDocuments(int docId) throws IndexException {
        }

        public List search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
            return Collections.EMPTY_LIST;
        }

        public void rebuild() throws IndexException {
        }
    }
}
