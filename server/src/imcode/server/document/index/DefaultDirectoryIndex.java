package imcode.server.document.index;

import com.imcode.imcms.api.SearchResult;
import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.util.HumanReadable;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.IntervalSchedule;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class DefaultDirectoryIndex implements DirectoryIndex {

    private static final Logger log = LogManager.getLogger(DefaultDirectoryIndex.class.getName());
    private static final long INDEXING_LOG_PERIOD__MILLISECONDS = DateUtils.MILLIS_PER_MINUTE;

    private static final Map<String, DocumentRepository> nameToCustomDocRepository = new HashMap<>();

    static {
        // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow or memory consuming queries?
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    private final File directory;
    private final IndexDocumentFactory indexDocumentFactory;
    private boolean isIndexBuildingThreadAlive;

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

        try (IndexReaderCloseable indexReaderCloseable = new IndexReaderCloseable(directory)) {

            final IndexReader indexReader = indexReaderCloseable.getReader();
            final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            final Sort sort = query.getSort();
            final TopDocs topDocs = sort == null
                    ? indexSearcher.search(query.getQuery(), indexReader.numDocs())
                    : indexSearcher.search(query.getQuery(), indexReader.numDocs(), sort);
            final ScoreDoc[] hits = topDocs.scoreDocs;

            filterPredicate = Optional.ofNullable(filterPredicate)
                    .map(predicate -> predicate.and(searchingUser::canSearchFor))
                    .orElse(searchingUser::canSearchFor);

            return getDocumentListForHits(hits, indexSearcher, filterPredicate, startPosition, maxResults);

        } catch (Exception e) {
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

    @Override
    public boolean isIndexBuildingThreadAlive() {
        return isIndexBuildingThreadAlive;
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
        try (final IndexWriterCloseable indexWriter = createIndexWriter(IndexWriterConfig.OpenMode.APPEND)) {

            indexWriter.deleteDocuments(new Term("meta_id", "" + document.getId()));

        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

    private SearchResult<DocumentDomainObject> getDocumentListForHits(ScoreDoc[] hits,
                                                                      IndexSearcher searcher,
                                                                      Predicate<DocumentDomainObject> searchingPredicate,
                                                                      int startPosition,
                                                                      int maxResults) {

        int nextSkip = startPosition;
        final DocumentGetter documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter();
        List<Integer> documentIds = new DocumentIdHitsList(hits, searcher);

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
        try (IndexWriterCloseable indexWriter = createIndexWriter(IndexWriterConfig.OpenMode.APPEND)) {
            addDocumentToIndex(document, indexWriter);
        }
    }

    private IndexWriterCloseable createIndexWriter(IndexWriterConfig.OpenMode openMode) throws IOException {
        final IndexWriterConfig config = new IndexWriterConfig(new AnalyzerImpl());
        config.setOpenMode(openMode);

        return new IndexWriterCloseable(directory, config);
    }

    private void addDocumentToIndex(DocumentDomainObject document, IndexWriter indexWriter) throws IOException {
        Document indexDocument = indexDocumentFactory.createIndexDocument(document);
        indexWriter.addDocument(indexDocument);
    }

    private void indexDocuments() throws IOException {
        try (IndexWriterCloseable indexWriter = createIndexWriter(IndexWriterConfig.OpenMode.CREATE)) {
            isIndexBuildingThreadAlive = true;
            for (Map.Entry<String, DocumentRepository> nameToRepositoryEntry : nameToCustomDocRepository.entrySet()) {
                final String repositoryName = nameToRepositoryEntry.getKey();
                log.info("Indexing docs from " + repositoryName + " document repository started.");

                final Set<DocumentDomainObject> docForIndexing = nameToRepositoryEntry.getValue().getDocs();
                indexDocuments(docForIndexing, indexWriter);

                log.info("Indexing docs from " + repositoryName + " document repository finished.");
            }
        }finally {
            isIndexBuildingThreadAlive = false;
        }
    }

    private void indexDocuments(Set<DocumentDomainObject> docsForIndexing, IndexWriter indexWriter) {
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

        private final ScoreDoc[] hits;
        private final IndexSearcher indexSearcher;

        DocumentIdHitsList(ScoreDoc[] hits, IndexSearcher indexSearcher) {
            this.hits = hits;
            this.indexSearcher = indexSearcher;
        }

        @Override
        public Integer get(int index) {
            try {
                return Integer.valueOf(indexSearcher.doc(hits[index].doc).get(DocumentIndex.FIELD__META_ID));
            } catch (IOException e) {
                throw new IndexException(e);
            }
        }

        @Override
        public int size() {
            return hits.length;
        }
    }

    /**
     * Wrapper used just to make it automatically closeable
     */
    private static class IndexWriterCloseable extends IndexWriter implements AutoCloseable {
        IndexWriterCloseable(File file, IndexWriterConfig config) throws IOException {
            super(FSDirectory.open(file.toPath()), config);
        }
    }

    /**
     * Wrapper used just to make it automatically closeable
     */
    @Getter
    private static class IndexReaderCloseable implements AutoCloseable {
        IndexReader reader;

        IndexReaderCloseable(File path) throws IOException {
            this.reader = DirectoryReader.open(FSDirectory.open(path.toPath()));
        }

        @Override
        public void close() throws Exception {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
