package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AutorebuildingDirectoryIndex implements DocumentIndex {

    private final static Logger log = Logger.getLogger(AutorebuildingDirectoryIndex.class.getName());

    private final DirectoryIndex index;
    private final File indexDirectory;
    private final int indexingSchedulePeriodInMilliseconds;

    private IndexBuildingThread indexBuildingThread ;

    static {
        // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow queries?
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    public AutorebuildingDirectoryIndex(File indexDirectory, int indexingSchedulePeriodInMinutes) {
        this.indexDirectory = indexDirectory ;
        this.indexingSchedulePeriodInMilliseconds = indexingSchedulePeriodInMinutes * DateUtils.MILLIS_IN_MINUTE ;
        this.index = new DirectoryIndex(indexDirectory);
        Timer scheduledIndexBuildingTimer = new Timer(true);
        long scheduledIndexDelay = 0 ;
        if ( IndexReader.indexExists(indexDirectory) ) {
            try {
                long indexModifiedTime = IndexReader.lastModified(indexDirectory);
                long time = System.currentTimeMillis();
                long nextTime = indexModifiedTime + indexingSchedulePeriodInMilliseconds;
                if (nextTime > time) {
                    log.info("First indexing scheduled at " + formatDatetime(new Date(nextTime)));
                    scheduledIndexDelay = nextTime - time;
                }
            } catch ( IOException e ) {
                log.warn("Failed to get last modified time of index.", e) ;
            }
        }
        scheduledIndexBuildingTimer.scheduleAtFixedRate(new ScheduledIndexingTimerTask(), scheduledIndexDelay, indexingSchedulePeriodInMilliseconds);
    }

    private String formatDatetime(Date nextExecutionTime) {
        return new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(nextExecutionTime);
    }

    public void indexDocument(DocumentDomainObject document) {
        log.debug( "Adding document.") ;
        if (null != indexBuildingThread) {
            indexBuildingThread.addDocument(document) ;
        }
        try {
            index.indexDocument(document);
        } catch ( IndexException e ) {
            rebuildBecauseOfError("Failed to add document " + document.getId() + " to index.", e);
        }
    }

    public void removeDocument(DocumentDomainObject document) {
        log.debug( "Removing document.") ;
        if (null != indexBuildingThread) {
            indexBuildingThread.removeDocument(document) ;
        }
        try {
            index.removeDocument(document);
        } catch ( IndexException e ) {
            rebuildBecauseOfError("Failed to remove document " + document.getId() + " from index.", e);
        }
    }

    public DocumentDomainObject[] search(Query query,
                                         UserDomainObject searchingUser) throws IndexException {
        try {
            DocumentDomainObject[] documents = index.search(query, searchingUser);
            if (index.isInconsistent()) {
                rebuildBecauseOfError("Index is inconsistent.", null);
            }
            return documents;
        } catch ( IndexException ex ) {
            rebuildBecauseOfError("Search failed.", ex);
            return new DocumentDomainObject[0];
        }
    }

    private void rebuildBecauseOfError(String message, IndexException ex) {
        log.error(message + " Rebuilding index...", ex);
        rebuild();
    }

    public void rebuild() {
        rebuildInBackground();
    }

    private void rebuildInBackground() {
        if (null == indexBuildingThread || !indexBuildingThread.isAlive()) {
            indexBuildingThread = new IndexBuildingThread(indexDirectory);
        }
        try {
            indexBuildingThread.start();
        } catch ( IllegalThreadStateException itse ) {
            log.debug("Ignoring request to build new index. Already in progress.") ;
        }
    }

    private class ScheduledIndexingTimerTask extends TimerTask {
        public void run() {
            Date nextExecutionTime = new Date(this.scheduledExecutionTime() + indexingSchedulePeriodInMilliseconds);
            log.info("Starting scheduled index rebuild. Next rebuild at " + formatDatetime(nextExecutionTime));
            rebuild();
        }
    }

}
