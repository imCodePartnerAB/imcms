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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AutorebuildingDirectoryIndex implements DocumentIndex {
    private final static Logger log = Logger.getLogger(AutorebuildingDirectoryIndex.class.getName());

    private final DirectoryIndex index;
    private final int indexingSchedulePeriodInMilliseconds;
    private final BackgroundIndexBuilder backgroundIndexBuilder;
    private final Timer scheduledIndexBuildingTimer = new Timer(true);

    static {
        // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow queries?
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    public AutorebuildingDirectoryIndex(File indexDirectory, int indexingSchedulePeriodInMinutes) {
        this.index = new DirectoryIndex(indexDirectory);
        this.indexingSchedulePeriodInMilliseconds = indexingSchedulePeriodInMinutes * DateUtils.MILLIS_IN_MINUTE;

        Date nextTime = getNextScheduledIndexingTime(indexDirectory, indexingSchedulePeriodInMilliseconds);
        indexDirectory.setLastModified(System.currentTimeMillis()) ;
        backgroundIndexBuilder = new BackgroundIndexBuilder(indexDirectory);
        if (null != nextTime) {
            scheduledIndexBuildingTimer.schedule(new ScheduledIndexingTimerTask(), nextTime);
        }
    }

    private Date getNextScheduledIndexingTime(File indexDirectory, int indexingSchedulePeriodInMilliseconds) {
        Date nextTime = null ;
        if ( indexingSchedulePeriodInMilliseconds <= 0 ) {
            log.info("Scheduled indexing is disabled.") ;
        } else {
            if ( IndexReader.indexExists(indexDirectory) ) {
                long indexModifiedTime = indexDirectory.lastModified();
                long time = System.currentTimeMillis();
                long headStartOverOlderThreads = 10000 ;
                nextTime = new Date(indexModifiedTime + indexingSchedulePeriodInMilliseconds - headStartOverOlderThreads);
                if ( nextTime.getTime() > time ) {
                    log.info("First indexing scheduled at " + formatDatetime(nextTime));
                }
            }
        }
        return nextTime;
    }

    private String formatDatetime(Date nextExecutionTime) {
        return new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(nextExecutionTime);
    }

    public void indexDocument(DocumentDomainObject document) {
        log.debug("Adding document.");

        backgroundIndexBuilder.addDocument(document);
        try {
            index.indexDocument(document);
        } catch ( IndexException e ) {
            rebuildBecauseOfError("Failed to add document " + document.getId() + " to index.", e);
        }
    }

    public void removeDocument(DocumentDomainObject document) {
        log.debug("Removing document.");
        backgroundIndexBuilder.removeDocument(document) ;
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
            if ( index.isInconsistent() ) {
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
        backgroundIndexBuilder.start() ;
    }

    private class ScheduledIndexingTimerTask extends TimerTask {
        public void run() {
            try {
                Date nextExecutionTime = new Date(this.scheduledExecutionTime() + indexingSchedulePeriodInMilliseconds);
                log.info("Starting scheduled index rebuild. Next rebuild at " + formatDatetime(nextExecutionTime));
                rebuild();
                scheduledIndexBuildingTimer.schedule(new ScheduledIndexingTimerTask(), nextExecutionTime);
            } catch (Exception e) {
                log.info("Caught exception during scheduled index rebuild.",e);
            }
        }
    }

}
