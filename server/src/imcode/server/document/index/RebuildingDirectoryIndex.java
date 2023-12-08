package imcode.server.document.index;

import com.imcode.imcms.api.SearchResult;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class RebuildingDirectoryIndex implements DocumentIndex {

    private final static Logger log = LogManager.getLogger(RebuildingDirectoryIndex.class.getName());

    private final BackgroundIndexBuilder backgroundIndexBuilder;
    private final long indexRebuildSchedulePeriodInMilliseconds;
    private final Timer scheduledIndexRebuildTimer = new Timer(true);
    private IndexRebuildTimerTask currentIndexRebuildTimerTask;

    private volatile DirectoryIndex index = new NullDirectoryIndex();

    public RebuildingDirectoryIndex(File indexParentDirectory, float indexRebuildSchedulePeriodInMinutes,
                                    IndexDocumentFactory indexDocumentFactory) {
        indexRebuildSchedulePeriodInMilliseconds = (long) (indexRebuildSchedulePeriodInMinutes * DateUtils.MILLIS_IN_MINUTE);
        backgroundIndexBuilder = new BackgroundIndexBuilder(indexParentDirectory, this, indexDocumentFactory);

        File indexDirectory = findLatestIndexDirectory(indexParentDirectory);
        long indexModifiedTime = 0;
        if (null != indexDirectory) {
            indexModifiedTime = indexDirectory.lastModified();
            try{
                index = new DefaultDirectoryIndex(indexDirectory, indexDocumentFactory);
            }catch (IOException e){
                rebuildBecauseOfError("Exception while creating directory index", new IndexException(e));
            }

        } else {
            rebuildBecauseOfError("No existing index.", null);
        }

        if (isSchedulingIndexRebuilds()) {
            log.info("First index rebuild scheduled at " + formatDatetime(restartIndexRebuildScheduling(indexModifiedTime)));
        } else {
            log.info("Scheduling of index rebuilds is disabled.");
        }
    }

    static File findLatestIndexDirectory(File indexParentDirectory) {
        try {
            if (indexParentDirectory.exists() && !indexParentDirectory.isDirectory()) {
                log.info("Deleting non-directory " + indexParentDirectory);
                FileUtils.forceDelete(indexParentDirectory);
            }
            if (!indexParentDirectory.exists()) {
                log.info("Creating directory " + indexParentDirectory);
                FileUtils.forceMkdir(indexParentDirectory);
            }
            File[] indexDirectories = indexParentDirectory.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
            sortFilesByLastModifiedWithLatestFirst(indexDirectories);
            File indexDirectory = null;
            for (int i = 0; i < indexDirectories.length; i++) {
                File directory = indexDirectories[i];
                final FSDirectory fsDirectory = FSDirectory.open(directory.toPath());
                if (DirectoryReader.indexExists(fsDirectory)) {
                    if (null == indexDirectory) {
                        log.info("Found index in directory " + directory);
                        indexDirectory = directory;
                    } else {
                        log.info("Deleting old index directory " + directory);
                        FileUtils.forceDelete(directory);
                    }
                } else {
                    log.info("Deleting non-index directory " + directory);
                    FileUtils.forceDelete(directory);
                }
            }
            return indexDirectory;
        } catch (IOException ioe) {
            throw new IndexException(ioe);
        }
    }

    private static void sortFilesByLastModifiedWithLatestFirst(File[] indexDirectories) {
        Arrays.sort(indexDirectories, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
    }

    static String formatDatetime(Date nextExecutionTime) {
        return new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(nextExecutionTime);
    }

    private synchronized Date restartIndexRebuildScheduling(long indexModifiedTime) {
        if (!isSchedulingIndexRebuilds()) {
            return null;
        }
        long time = System.currentTimeMillis();
        Date nextTime = new Date(indexModifiedTime + indexRebuildSchedulePeriodInMilliseconds);
        if (nextTime.getTime() < time) {
            nextTime.setTime(time);
        }
        restartIndexRebuildScheduling(nextTime);
        return nextTime;
    }

    private synchronized void restartIndexRebuildScheduling(Date nextTime) {
        if (null != currentIndexRebuildTimerTask) {
            currentIndexRebuildTimerTask.cancel();
            log.trace("Canceled existing index rebuild timer task.");
        }
        try {
            log.info("Restarting scheduling of index rebuilds. First rebuild at " + formatDatetime(nextTime) + ".");
            backgroundIndexBuilder.touchIndexParentDirectory();
            currentIndexRebuildTimerTask = new IndexRebuildTimerTask(indexRebuildSchedulePeriodInMilliseconds, backgroundIndexBuilder);
            scheduledIndexRebuildTimer.scheduleAtFixedRate(currentIndexRebuildTimerTask, nextTime, indexRebuildSchedulePeriodInMilliseconds);
        } catch (IllegalStateException ise) {
            log.error("Failed to start index rebuild scheduling.", ise);
        }
    }

    private boolean isSchedulingIndexRebuilds() {
        return indexRebuildSchedulePeriodInMilliseconds > 0;
    }

    public void indexDocument(DocumentDomainObject document) {
        log.debug("Adding document.");
        backgroundIndexBuilder.addDocument(document);
        try {
            index.indexDocument(document);
        } catch (IndexException e) {
            log.error("Failed to add document " + document.getId() + " to index.", e);
            //rebuildBecauseOfError("Failed to add document " + document.getId() + " to index.", e);
        } catch (Exception e) {
            log.error(String.format("Failed to add document %d to index.", document.getId()), e);
        }
    }

    public void removeDocument(DocumentDomainObject document) {
        log.debug("Removing document.");
        backgroundIndexBuilder.removeDocument(document);
        try {
            index.removeDocument(document);
        } catch (IndexException e) {
            log.error("Failed to remove document " + document.getId() + " from index.", e);
            //rebuildBecauseOfError("Failed to remove document " + document.getId() + " from index.", e);
        } catch (Exception e) {
            log.error(String.format("Failed to remove document %d from index.", document.getId()), e);
        }
    }

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return search(query, searchingUser, 1);
    }


    private List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int retryCount) throws IndexException {
        try {
            List<DocumentDomainObject> documents = index.search(query, searchingUser);
//            if ( index.isInconsistent() ) {
//                rebuildBecauseOfError("Index is inconsistent.", null);
//            }
            return documents;
        } catch (IndexException ex) {
            if (ex.getCause() instanceof FileNotFoundException) {
                log.error("Index directory does not exists. Index swapped?", ex.getCause());
                if (retryCount > 0) {
                    log.debug(String.format("Retrying search: Query: %s, User: %s", query, searchingUser));
                    search(query, searchingUser, retryCount - 1);
                }
            } else {
                rebuildBecauseOfError(String.format("Search failed. Query: %s, User: %s", query, searchingUser), ex);
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.error(String.format("Search failed. Query: %s, User: %s", query, searchingUser), e);
            return Collections.emptyList();
        }
    }

    public SearchResult<DocumentDomainObject> search(DocumentQuery query,
                                                     UserDomainObject searchingUser,
                                                     int startPosition,
                                                     int maxResults) {

        return index.search(query, searchingUser, startPosition, maxResults);
    }

    @Override
    public SearchResult<DocumentDomainObject> search(DocumentQuery query,
                                                     UserDomainObject searchingUser,
                                                     int startPosition,
                                                     int maxResults,
                                                     Predicate<DocumentDomainObject> filterPredicate) throws IndexException {
        try {
            return index.search(query, searchingUser, startPosition, maxResults, filterPredicate);
        } catch (IndexException ex) {
            rebuildBecauseOfError("Search failed.", ex);
            return SearchResult.empty();
        }
    }


    private void rebuildBecauseOfError(String message, IndexException ex) {
        log.error(message + " Starting index rebuild.", ex);
        rebuild();
    }

    public void rebuild() {
        if (isSchedulingIndexRebuilds()) {
            restartIndexRebuildScheduling(new Date());
        } else {
            backgroundIndexBuilder.start();
        }
    }

    @Override
    public boolean isIndexBuildingThreadAlive() {
        return backgroundIndexBuilder.isIndexBuildingThreadAlive();
    }

    void notifyRebuildComplete(DirectoryIndex newIndex) {
        DirectoryIndex oldIndex = index;
        index = newIndex;
        if (!oldIndex.equals(index)) {
            oldIndex.close();
            oldIndex.delete();
        }
    }

    private static class NullDirectoryIndex implements DirectoryIndex {

        public boolean isInconsistent() {
            return false;
        }

        public void delete() {

        }

        public void close() {

        }

        public void indexDocument(DocumentDomainObject document) throws IndexException {
        }

        public void removeDocument(DocumentDomainObject document) throws IndexException {
        }

        public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
            return Collections.emptyList();
        }

        public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException {
            return SearchResult.empty();
        }

        public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults, Predicate<DocumentDomainObject> filterPredicate) throws IndexException {
            return SearchResult.empty();
        }

        public void rebuild() throws IndexException {
        }

        @Override
        public boolean isIndexBuildingThreadAlive() {
            return false;
        }
    }
}
