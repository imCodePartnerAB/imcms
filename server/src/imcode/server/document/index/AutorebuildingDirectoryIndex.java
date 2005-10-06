package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;

public class AutorebuildingDirectoryIndex implements DocumentIndex {

    private final static Logger log = Logger.getLogger(AutorebuildingDirectoryIndex.class.getName());

    private final BackgroundIndexBuilder backgroundIndexBuilder;
    private final long indexRebuildSchedulePeriodInMilliseconds;
    private final Timer scheduledIndexRebuildTimer = new Timer(true);
    private IndexRebuildTimerTask currentIndexRebuildTimerTask ;
    
    private DirectoryIndex index = new NullDirectoryIndex();

    public AutorebuildingDirectoryIndex(File indexParentDirectory, float indexRebuildSchedulePeriodInMinutes) {
        indexRebuildSchedulePeriodInMilliseconds = (long) ( indexRebuildSchedulePeriodInMinutes * DateUtils.MILLIS_IN_MINUTE );
        backgroundIndexBuilder = new BackgroundIndexBuilder(indexParentDirectory, this);
        
        File indexDirectory = findLatestIndexDirectory(indexParentDirectory);
        long indexModifiedTime = 0;
        if ( null != indexDirectory ) {
            indexModifiedTime = indexDirectory.lastModified();
            index = new DefaultDirectoryIndex(indexDirectory);
        } else {
            rebuildBecauseOfError("No existing index.", null);
        }

        if ( indexRebuildSchedulePeriodInMilliseconds <= 0 ) {
            log.info("Scheduling of index rebuilds is disabled.");
        } else {
            log.info("First index rebuild scheduled at " + formatDatetime(startIndexRebuildScheduling(indexModifiedTime)));
        }
    }

    private synchronized Date startIndexRebuildScheduling(long indexModifiedTime) {
        long time = System.currentTimeMillis();
        Date nextTime = new Date(indexModifiedTime + indexRebuildSchedulePeriodInMilliseconds);
        if ( nextTime.getTime() < time ) {
            nextTime.setTime(time);
        }
        if (null != currentIndexRebuildTimerTask) {
            currentIndexRebuildTimerTask.cancel() ;
        }
        try {
            currentIndexRebuildTimerTask = new IndexRebuildTimerTask(indexRebuildSchedulePeriodInMilliseconds, backgroundIndexBuilder);
            scheduledIndexRebuildTimer.scheduleAtFixedRate(currentIndexRebuildTimerTask, nextTime, indexRebuildSchedulePeriodInMilliseconds);
        } catch ( IllegalStateException ise ) {
            log.error("Failed to start index rebuild scheduling.", ise);
        }
        return nextTime;
    }

    static File findLatestIndexDirectory(File indexParentDirectory) {
        try {
            if ( indexParentDirectory.exists() && !indexParentDirectory.isDirectory() ) {
                log.debug("Deleting non-directory " + indexParentDirectory);
                FileUtils.forceDelete(indexParentDirectory);
            }
            if ( !indexParentDirectory.exists() ) {
                log.debug("Creating directory " + indexParentDirectory);
                FileUtils.forceMkdir(indexParentDirectory);
            }
            File[] indexDirectories = indexParentDirectory.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
            sortFilesByLastModifiedWithLatestFirst(indexDirectories);
            File indexDirectory = null;
            for ( int i = 0; i < indexDirectories.length; i++ ) {
                File directory = indexDirectories[i];
                if ( IndexReader.indexExists(directory) ) {
                    if ( null == indexDirectory ) {
                        log.debug("Found index in directory " + directory);
                        indexDirectory = directory;
                    } else {
                        log.debug("Deleting old index directory " + directory);
                        FileUtils.forceDelete(directory);
                    }
                } else {
                    log.debug("Deleting non-index directory " + directory);
                    FileUtils.forceDelete(directory);
                }
            }
            return indexDirectory;
        } catch ( IOException ioe ) {
            throw new IndexException(ioe);
        }
    }

    private static void sortFilesByLastModifiedWithLatestFirst(File[] indexDirectories) {
        Arrays.sort(indexDirectories, new Comparator() {
            public int compare(Object o1, Object o2) {
                File f1 = (File) o1;
                File f2 = (File) o2;
                return Long.valueOf(f2.lastModified()).compareTo(Long.valueOf(f1.lastModified()));
            }
        });
    }

    static String formatDatetime(Date nextExecutionTime) {
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
        backgroundIndexBuilder.removeDocument(document);
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
        log.error(message, ex);
        try {
            startIndexRebuildScheduling(0) ;
            rebuild() ;
        } catch(AlreadyRebuildingIndexException arie) {
            log.debug("Already indexing.") ;
        }
    }

    public void rebuild() {
        backgroundIndexBuilder.start();
    }

    void notifyRebuildComplete(DirectoryIndex newIndex) {
        DirectoryIndex oldIndex = index;
        index = newIndex;
        if (!oldIndex.equals(index) ) {
            oldIndex.delete();
        }
    }

    void notifyRebuildSchedulerDied() {
        log.debug("Rebuild scheduler died.") ;
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

        public DocumentDomainObject[] search(Query query, UserDomainObject searchingUser) throws IndexException {
            return new DocumentDomainObject[0];
        }

        public void rebuild() throws IndexException {
        }
    }
}
