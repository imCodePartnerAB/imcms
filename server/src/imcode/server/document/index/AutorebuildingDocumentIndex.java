package imcode.server.document.index;

import imcode.server.ApplicationServer;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.IntervalSchedule;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AutorebuildingDocumentIndex extends DocumentIndex {

    private static final int INDEXING_SCHEDULE_PERIOD__MILLISECONDS = DateUtils.MILLIS_IN_DAY;

    private final static Logger log = Logger.getLogger(AutorebuildingDocumentIndex.class.getName());

    private DirectoryIndex index;
    private Set documentsToAppendToNewIndex = Collections.synchronizedSet(new HashSet());
    private final Object newIndexBuildingLock = new Object();

    static {
        // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow queries?
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    public AutorebuildingDocumentIndex(File indexDirectory) {
        this.index = new DirectoryIndex(indexDirectory);
        Timer scheduledIndexBuildingTimer = new Timer(true);
        scheduledIndexBuildingTimer.scheduleAtFixedRate(new ScheduledIndexingTimerTask(), 0, INDEXING_SCHEDULE_PERIOD__MILLISECONDS);
    }

    public synchronized void indexDocument(DocumentDomainObject document) {
        log.debug("indexDocument - called");
        documentsToAppendToNewIndex.add(document);
        try {
            index.indexDocument(document);
        } catch (IOException e) {
            log.error("Failed to index document " + document.getId() + ". Reindexing...");
            buildNewIndexInBackground();
        }
    }

    public synchronized DocumentDomainObject[] search(Query query, UserDomainObject searchingUser) throws IOException {
        log.debug("search - called");
        try {
            return index.trySearch(query, searchingUser);
        } catch (IOException ex) {
            log.warn("Search failed", ex);
            buildNewIndexInBackground();
            throw  ex;
        }
    }

    private void buildNewIndexInBackground() {
        Thread indexBuildingThread = new Thread("Background indexing thread") {
            public void run() {
                buildNewIndex();
            }
        };
        int callersPriority = Thread.currentThread().getPriority();
        int newPriority = Math.max( callersPriority - 1, Thread.MIN_PRIORITY );
        indexBuildingThread.setPriority( newPriority );
        log.debug("Setting background indexing thread priority to " + indexBuildingThread.getPriority() );

        indexBuildingThread.setDaemon(true);
        indexBuildingThread.start();
    }

    private void buildNewIndex() {
        NDC.push("buildNewIndex");
        try {
            File indexDirectoryFile = this.index.getDirectory();
            File parentFile = indexDirectoryFile.getParentFile();
            String name = indexDirectoryFile.getName();
            synchronized (newIndexBuildingLock) {
                File newIndexDirectoryFile = new File(parentFile, name + ".new");
                DirectoryIndex newIndexDirectory = new DirectoryIndex(newIndexDirectoryFile);
                newIndexDirectory.indexAllDocuments();
                for (Iterator iterator = documentsToAppendToNewIndex.iterator(); iterator.hasNext();) {
                    DocumentDomainObject document = (DocumentDomainObject) iterator.next();
                    newIndexDirectory.indexDocument(document);
                    iterator.remove();
                }
                replaceIndexWithNewIndex(newIndexDirectory);
            }
        } catch (IOException e) {
            log.fatal("Failed to index all documents.", e);
        } finally {
            NDC.pop();
        }
    }

    private synchronized void replaceIndexWithNewIndex(DirectoryIndex newIndex) throws IOException {
        File indexDirectory = index.getDirectory();
        File oldIndex = new File(indexDirectory.getParentFile(), indexDirectory.getName()
                + ".old");
        if (oldIndex.exists()) {
            FileUtils.forceDelete(oldIndex);
        }
        if (indexDirectory.exists() && !indexDirectory.renameTo(oldIndex)) {
            log.error("Failed to rename \"" + indexDirectory + "\" to \"" + oldIndex + "\".");
        }
        File newIndexDirectory = newIndex.getDirectory();
        if (!newIndexDirectory.renameTo(indexDirectory)) {
            throw new IOException("Failed to rename \"" + newIndexDirectory + "\" to \""
                    + indexDirectory
                    + "\".");
        }
        FileUtils.deleteDirectory(oldIndex);
    }

    private class ScheduledIndexingTimerTask extends TimerTask {

        public void run() {
            Date nextExecutionTime = new Date(this.scheduledExecutionTime() + INDEXING_SCHEDULE_PERIOD__MILLISECONDS);
            String nextExecutionTimeString = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(nextExecutionTime);
            log.info("Starting scheduled indexing. Next indexing at " + nextExecutionTimeString);
            buildNewIndexInBackground();
        }
    }

    private class DirectoryIndex {

        private File directory;
        private IndexDocumentAdapter indexDocumentAdapter = new IndexDocumentAdapter();

        private final static int INDEXING_LOG_PERIOD__MILLISECONDS = 10 * 1000;

        DirectoryIndex(File directory) {
            this.directory = directory;
        }

        private DocumentDomainObject[] trySearch(Query query, UserDomainObject searchingUser) throws IOException {
            IndexReader indexReader = IndexReader.open(directory);
            IndexSearcher indexSearcher = null;
            try {
                indexSearcher = new IndexSearcher(indexReader);
                StopWatch searchStopWatch = new StopWatch();
                searchStopWatch.start();
                Hits hits = indexSearcher.search(query);
                long searchTime = searchStopWatch.getTime();
                DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
                List documentList = documentMapper.getDocumentListForHits(hits, searchingUser);
                log.debug("Search for " + query.toString() + ": " + searchTime + "ms. Total: "
                        + searchStopWatch.getTime()
                        + "ms.");
                return (DocumentDomainObject[]) documentList.toArray(new DocumentDomainObject[documentList.size()]);
            } finally {
                if (null != indexSearcher) {
                    indexSearcher.close();
                }
                indexReader.close();
            }
        }

        private void indexDocument(DocumentDomainObject document) throws IOException {
            deleteDocumentFromIndex(document);
            addDocumentToIndex(document);
        }

        private void deleteDocumentFromIndex(DocumentDomainObject document) throws IOException {
            IndexReader indexReader = IndexReader.open(directory);
            try {
                indexReader.delete(new Term("meta_id", "" + document.getId()));
            } finally {
                indexReader.close();
            }
        }

        private void addDocumentToIndex(DocumentDomainObject document) throws IOException {
            IndexWriter indexWriter = createIndexWriter(false);
            try {
                addDocumentToIndex(document, indexWriter);
            } finally {
                indexWriter.close();
            }
        }

        private IndexWriter createIndexWriter(boolean createIndex) throws IOException {
            return new IndexWriter(directory, new AnalyzerImpl(), createIndex);
        }

        private void indexAllDocuments() throws IOException {
            IndexWriter indexWriter = createIndexWriter(true);
            try {
                indexAllDocumentsToIndexWriter(indexWriter);
            } finally {
                indexWriter.close();
            }
        }

        private void addDocumentToIndex(DocumentDomainObject document, IndexWriter indexWriter) throws IOException {
            Document indexDocument = indexDocumentAdapter.createIndexDocument(document);
            indexWriter.addDocument(indexDocument);
        }

        private void indexAllDocumentsToIndexWriter(IndexWriter indexWriter) throws IOException {
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            int[] documentIds = documentMapper.getAllDocumentIds();

            logIndexingStarting(documentIds.length);
            IntervalSchedule indexingLogSchedule = new IntervalSchedule(INDEXING_LOG_PERIOD__MILLISECONDS);

            for (int i = 0; i < documentIds.length; i++) {
                try {
                    addDocumentToIndex(documentMapper.getDocument(documentIds[i]), indexWriter);
                } catch( Exception ex ) {
                    log.error("Couln't index document with meta_id " + documentIds[i] + ", trying next document.", ex );
                }

                if (indexingLogSchedule.isTime()) {
                    logIndexingProgress(i, documentIds.length);
                }
                Thread.yield(); // To make sure other threads with the same priority get a chance to run something once in a while.
            }

            logIndexingCompleted(documentIds.length, indexingLogSchedule.getStopWatch());
            optimizeIndex(indexWriter);
        }

        private void logIndexingStarting(int documentCount) {
            log.info("Building index of all " + documentCount + " documents");
        }

        private void logIndexingProgress(int i, int numberOfDocuments) {
            int indexPercentageCompleted = (int) (i * (100F / numberOfDocuments));
            log.info("Completed " + indexPercentageCompleted + "% of the index.");
        }

        private void logIndexingCompleted(int numberOfDocuments, StopWatch indexingStopWatch) {
            log.info("Completed index of " + numberOfDocuments + " documents in " + indexingStopWatch.getTime()
                    + "ms");
        }

        private void optimizeIndex(IndexWriter indexWriter) throws IOException {
            StopWatch optimizeStopWatch = new StopWatch();
            optimizeStopWatch.start();
            indexWriter.optimize();
            optimizeStopWatch.stop();
            log.info("Optimized index in " + optimizeStopWatch.getTime() + "ms");
        }

        private File getDirectory() {
            return directory;
        }

    }

}
