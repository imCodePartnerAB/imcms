package imcode.server.document.index;

import com.imcode.imcms.api.SearchResult;
import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.util.HumanReadable;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.IntervalSchedule;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class DefaultDirectoryIndex implements DirectoryIndex {

    private static final Logger log = Logger.getLogger(DefaultDirectoryIndex.class.getName());
    private static final long INDEXING_LOG_PERIOD__MILLISECONDS = DateUtils.MILLIS_PER_MINUTE;

    private static final Map<String, DocumentRepository> nameToCustomDocRepository = new HashMap<>();

    static {
        // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow or memory consuming queries?
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    private final File directory;
    private final IndexDocumentFactory indexDocumentFactory;

    DefaultDirectoryIndex(File directory, IndexDocumentFactory indexDocumentFactory) {
        this.directory = directory;
        this.indexDocumentFactory = indexDocumentFactory;
    }

    public static void addCustomDocRepository(String name, DocumentRepository documentRepository) {
        nameToCustomDocRepository.put(name, documentRepository);
    }

    @SuppressWarnings("unused")
    public static void removeCustomDocRepository(String name, DocumentRepository documentRepository) {
        nameToCustomDocRepository.remove(name);
    }

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return search(query, searchingUser, 0, -1).getResult();
    }

    public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException {
        return search(query, searchingUser, startPosition, maxResults, null);
    }

    @Override
    public SearchResult<DocumentDomainObject> search(DocumentQuery query,
                                                     UserDomainObject searchingUser,
                                                     int startPosition,
                                                     int maxResults,
                                                     Predicate<DocumentDomainObject> filterPredicate)
            throws IndexException {

        try (ClosableIndexSearcher indexSearcher = new ClosableIndexSearcher(directory.toString())) {

            final Hits hits = indexSearcher.search(query.getQuery(), query.getSort());

            filterPredicate = Optional.ofNullable(filterPredicate)
                    .map(predicate -> predicate.and(searchingUser::canSearchFor))
                    .orElse(searchingUser::canSearchFor);

            return getDocumentListForHits(hits, filterPredicate, startPosition, maxResults);

        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    public void rebuild() {
        try {
            indexDocuments();
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    public void indexDocument(DocumentDomainObject document) throws IndexException {
        try {
            removeDocument(document);
            addDocument(document);
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    public void removeDocument(DocumentDomainObject document) throws IndexException {
        try (final IndexReaderCloseable indexReader = new IndexReaderCloseable(directory)) {

            indexReader.delete(new Term("meta_id", "" + document.getId()));

        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

    private SearchResult<DocumentDomainObject> getDocumentListForHits(Hits hits,
                                                                      Predicate<DocumentDomainObject> searchingPredicate,
                                                                      int startPosition,
                                                                      int maxResults) {

        int nextSkip = startPosition;
        final DocumentGetter documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter();
        List<Integer> documentIds = new DocumentIdHitsList(hits);

        final int totalCount = documentIds.size();

        if (totalCount < startPosition) {
            return new SearchResult<>(Collections.emptyList(), totalCount, nextSkip);
        }

        documentIds = documentIds.subList(startPosition, totalCount);
        final List<DocumentDomainObject> documentList = new ArrayList<>();
        final int cutResultSize = documentIds.size();

        if ((maxResults <= 0) || (maxResults >= cutResultSize)) { // no limit
            nextSkip = totalCount;

            for (DocumentDomainObject documentDomainObject : documentGetter.getDocuments(documentIds)) {
                if (searchingPredicate.test(documentDomainObject)) {
                    documentList.add(documentDomainObject);
                }
            }
        } else {
            for (Integer documentId : documentIds) {

                if (documentList.size() == maxResults) break;

                nextSkip++;

                final DocumentDomainObject document = documentGetter.getDocument(documentId);

                if (searchingPredicate.test(document)) documentList.add(document);
            }
        }

        return new SearchResult<>(documentList, totalCount, nextSkip);
    }

    private void addDocument(DocumentDomainObject document) throws IOException {
        try (ClosableIndexWriter indexWriter = createIndexWriter(false)) {
            addDocumentToIndex(document, indexWriter);
        }
    }

    private ClosableIndexWriter createIndexWriter(boolean createIndex) throws IOException {
        return new ClosableIndexWriter(directory, new AnalyzerImpl(), createIndex);
    }

    private void addDocumentToIndex(DocumentDomainObject document, IndexWriter indexWriter) throws IOException {
        Document indexDocument = indexDocumentFactory.createIndexDocument(document);
        indexWriter.addDocument(indexDocument);
    }

    private void indexDocuments() throws IOException {
        try (ClosableIndexWriter indexWriter = createIndexWriter(true)) {
            for (Map.Entry<String, DocumentRepository> nameToRepositoryEntry : nameToCustomDocRepository.entrySet()) {
                final String repositoryName = nameToRepositoryEntry.getKey();
                log.info("Indexing docs from " + repositoryName + " document repository started.");

                final Set<DocumentDomainObject> docForIndexing = nameToRepositoryEntry.getValue().getDocs();
                indexDocuments(docForIndexing, indexWriter);

                log.info("Indexing docs from " + repositoryName + " document repository finished.");
            }
        }
    }

    private void indexDocuments(Set<DocumentDomainObject> docsForIndexing, IndexWriter indexWriter) throws IOException {
        final int docsSize = docsForIndexing.size();
        final IntervalSchedule indexingLogSchedule = new IntervalSchedule(INDEXING_LOG_PERIOD__MILLISECONDS);
        int i = 0;

        logIndexingStarting(docsSize);

        for (DocumentDomainObject documentForIndex : docsForIndexing) {
            i++;

            if (documentForIndex == null) {
                continue;
            }

            try {
                addDocumentToIndex(documentForIndex, indexWriter);

            } catch (Exception ex) {
                log.error("Could not index document with meta_id " + documentForIndex.getId() + ", trying next document.", ex);
            }

            if (indexingLogSchedule.isTime()) {
                logIndexingProgress(i, docsSize, indexingLogSchedule.getStopWatch().getTime());
            }
        }

        logIndexingCompleted(docsSize, indexingLogSchedule.getStopWatch());
        optimizeIndex(indexWriter);
    }

    private void logIndexingStarting(int documentCount) {
        log.info("Building index of all " + documentCount + " documents");
    }

    private void logIndexingProgress(int documentsCompleted, int numberOfDocuments, long elapsedTime) {
        int indexPercentageCompleted = (int) (documentsCompleted * (100F / numberOfDocuments));
        int documentsCompletedFixed = (documentsCompleted == 0) ? 1 : documentsCompleted;
        long estimatedTime = numberOfDocuments * elapsedTime / documentsCompletedFixed;
        long estimatedTimeLeft = estimatedTime - elapsedTime;
        Date eta = new Date(System.currentTimeMillis() + estimatedTimeLeft);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        log.info("Indexed " + documentsCompleted + " documents (" + indexPercentageCompleted + "%). ETA " + dateFormat.format(eta));
    }

    private void logIndexingCompleted(int numberOfDocuments, StopWatch indexingStopWatch) {
        long time = indexingStopWatch.getTime();
        String humanReadableTime = HumanReadable.getHumanReadableTimeSpan(time);
        long timePerDocument = time / (numberOfDocuments + 1); // to prevent division by zero
        log.info("Indexed " + numberOfDocuments + " documents in " + humanReadableTime + ". " + timePerDocument + "ms per document.");
    }

    private void optimizeIndex(IndexWriter indexWriter) throws IOException {
        StopWatch optimizeStopWatch = new StopWatch();
        optimizeStopWatch.start();
        indexWriter.optimize();
        optimizeStopWatch.stop();
        log.debug("Optimized index in " + optimizeStopWatch.getTime() + "ms");
    }

    public boolean isInconsistent() {
        return false; //inconsistent;
    }

    public void delete() {
        try {
            log.info("Deleting index directory " + directory);

            if (directory.exists()) {
                FileUtils.forceDelete(directory);
            }
        } catch (IOException e) {
            throw new IndexException(e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DefaultDirectoryIndex that = (DefaultDirectoryIndex) o;

        return directory.equals(that.directory);

    }

    public int hashCode() {
        return directory.hashCode();
    }

    /**
     * Read only non-caching view of found meta ids.
     * <p>
     * Please note that {@link #get(int)} method call is expensive since it always perform additional computation.
     * If you need a reusable sub-list of this view please consider copying its elements explicitly -
     * for example {@code new java.util.ArrayList(DocumentIdHitsList.subList(n, k))}
     */
    // DON'T DO THIS!!
    // This class implementation in almedalen_alpha23_r4 contains overridden {@link java.util.List#subList} method
    // which violates behavioral contract by returning a copy of backing (this) list elements instead of a view.
    private static class DocumentIdHitsList extends AbstractList<Integer> {

        private final Hits hits;

        DocumentIdHitsList(Hits hits) {
            this.hits = hits;
        }

        @Override
        public Integer get(int index) {
            try {
                return Integer.valueOf(hits.doc(index).get(DocumentIndex.FIELD__META_ID));
            } catch (IOException e) {
                throw new IndexException(e);
            }
        }

        @Override
        public int size() {
            return hits.length();
        }
    }

    /**
     * Wrapper used just to make it automatically closeable
     */
    private class ClosableIndexSearcher extends IndexSearcher implements AutoCloseable {
        ClosableIndexSearcher(String path) throws IOException {
            super(path);
        }
    }

    /**
     * Wrapper used just to make it automatically closeable
     */
    private class ClosableIndexWriter extends IndexWriter implements AutoCloseable {
        ClosableIndexWriter(File file, Analyzer a, boolean create) throws IOException {
            super(file, a, create);
        }
    }

    /**
     * Wrapper used just to make it automatically closeable
     */
    private class IndexReaderCloseable implements AutoCloseable {
        IndexReader reader;

        IndexReaderCloseable(File path) throws IOException {
            this.reader = IndexReader.open(path);
        }

        @Override
        public void close() throws Exception {
            if (reader != null) {
                reader.close();
            }
        }

        public void delete(Term term) throws IOException {
            if (reader != null) {
                reader.delete(term);
            }
        }
    }
}
