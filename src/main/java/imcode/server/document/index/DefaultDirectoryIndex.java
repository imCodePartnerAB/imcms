package imcode.server.document.index;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.IntervalSchedule;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.util.HumanReadable;
import org.apache.lucene.search.*;

class DefaultDirectoryIndex implements DirectoryIndex {

    private final static Logger log = Logger.getLogger( DefaultDirectoryIndex.class.getName() );
    private final static int INDEXING_LOG_PERIOD__MILLISECONDS = (int) DateUtils.MILLIS_PER_MINUTE ;

    private final File directory;
    private final IndexDocumentFactory indexDocumentFactory;

    private boolean inconsistent;

    private static final int NUM_HITS = 50;

    static {
        // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow or memoryconsuming queries?
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    DefaultDirectoryIndex(File directory, IndexDocumentFactory indexDocumentFactory) {
        this.directory = directory;
        this.indexDocumentFactory = indexDocumentFactory;
    }

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        try {
            IndexSearcher indexSearcher = new IndexSearcher( directory.toString() );
                 
            try {
                StopWatch searchStopWatch = new StopWatch();
                searchStopWatch.start();

                TopDocs topDocs = null;
                if (query.getSort() != null) {
                    topDocs = indexSearcher.search(query.getQuery(), null, NUM_HITS, query.getSort());
                } else {
                    topDocs = indexSearcher.search(query.getQuery(), NUM_HITS);
                }
                
                long searchTime = searchStopWatch.getTime();
                List<DocumentDomainObject> documentList = getDocumentListForHits(topDocs, indexSearcher, searchingUser );
                if (log.isDebugEnabled()) {
                    log.debug( "Search for " + query.getQuery().toString() + ": " + searchTime + "ms. Total: "
                           + searchStopWatch.getTime()
                           + "ms." );
                }
                return documentList ;
            } finally {
                indexSearcher.close();
            }
        } catch ( IOException e ) {
            throw new IndexException( e ) ;
        }
    }

    public void rebuild() {
        try {
            indexAllDocuments();
        } catch ( IOException e ) {
            throw new IndexException( e );
        }
    }


    /**
     * @return working documents.
     */
    private List<DocumentDomainObject> getDocumentListForHits( final TopDocs topDocs, Searcher searcher, final UserDomainObject searchingUser ) {
        DocumentGetter documentGetter = Imcms.getServices().getDocumentMapper();
        List<Integer> documentIds = new DocumentIdHitsList(topDocs, searcher) ;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<DocumentDomainObject> documentList = documentGetter.getDocuments(documentIds);
        stopWatch.stop();
        if (log.isDebugEnabled()) {
            log.debug("Got "+documentList.size()+" documents in "+stopWatch.getTime()+"ms.");
        }
        if (documentList.size() != topDocs.totalHits) {
            inconsistent = true ;
        }
        CollectionUtils.filter(documentList, new Predicate() {
            public boolean evaluate(Object object) {
                DocumentDomainObject document = (DocumentDomainObject) object;
                return searchingUser.canSearchFor(document) ;
            }
        });
        return documentList ;
    }

    public void indexDocument( DocumentDomainObject document ) throws IndexException {
        try {
            removeDocument( document );
            addDocument( document );
        } catch ( IOException e ) {
            throw new IndexException( e );
        }
    }

    
    public void removeDocument( DocumentDomainObject document ) throws IndexException {
        try {
           IndexReader indexReader = IndexReader.open( directory );
            try {
                indexReader.deleteDocuments(new Term( "meta_id", "" + document.getId() ) );
            } finally {
                indexReader.close();
            }
        } catch ( IOException e ) {
            throw new IndexException( e );
        }
    }

    private void addDocument( DocumentDomainObject document ) throws IOException {
        IndexWriter indexWriter = createIndexWriter( false );
        try {
            addDocumentToIndex( document, indexWriter );
        } finally {
            indexWriter.close();
        }
    }

    private IndexWriter createIndexWriter( boolean createIndex ) throws IOException {
        return new IndexWriter( directory, new AnalyzerImpl(), createIndex, IndexWriter.MaxFieldLength.UNLIMITED );
    }

    private void indexAllDocuments() throws IOException {
        IndexWriter indexWriter = createIndexWriter( true );
        try {
            indexAllDocumentsToIndexWriter( indexWriter );
        } finally {
            indexWriter.close();
        }
    }

    private void addDocumentToIndex( DocumentDomainObject document, IndexWriter indexWriter ) throws IOException {
        Document indexDocument = indexDocumentFactory.createIndexDocument( document );
        indexWriter.addDocument( indexDocument );
    }

    
    /**
     * Indexes all working documents.
     *
     * @param indexWriter
     * @throws IOException
     */
    private void indexAllDocumentsToIndexWriter( IndexWriter indexWriter ) throws IOException {
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        int[] documentIds = documentMapper.getAllDocumentIds();

        logIndexingStarting( documentIds.length );
        IntervalSchedule indexingLogSchedule = new IntervalSchedule( INDEXING_LOG_PERIOD__MILLISECONDS );

        for ( int i = 0; i < documentIds.length; i++ ) {
            try {
                DocumentDomainObject document = documentMapper.getDocument( documentIds[i] );

                addDocumentToIndex( document, indexWriter );

                // Published document indexing
                // DocumentDomainObject publishedDocument = documentMapper.getDefaultDocument( documentIds[i] );
                // if (publishedDocument != null) {
                //    addDocumentToIndex( publishedDocument, indexWriter );
                // }
            } catch ( Exception ex ) {
                log.error( "Could not index document with meta_id " + documentIds[i] + ", trying next document.", ex );
            }

            if ( indexingLogSchedule.isTime() ) {
                logIndexingProgress( i, documentIds.length, indexingLogSchedule.getStopWatch().getTime());
            }
            
            Thread.yield(); // To make sure other threads with the same priority get a chance to run something once in a while.
        }

        logIndexingCompleted( documentIds.length, indexingLogSchedule.getStopWatch() );
        optimizeIndex( indexWriter );
    }

    private void logIndexingStarting( int documentCount ) {
        log.debug( "Building index of all " + documentCount + " documents" );
    }

    private void logIndexingProgress(int documentsCompleted, int numberOfDocuments, long elapsedTime) {
        int indexPercentageCompleted = (int)( documentsCompleted * ( 100F / numberOfDocuments ) );
        long estimatedTime = numberOfDocuments * elapsedTime / documentsCompleted;
        long estimatedTimeLeft = estimatedTime - elapsedTime ;
        Date eta = new Date(System.currentTimeMillis() + estimatedTimeLeft) ;
        DateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss");
        log.info( "Indexed " + documentsCompleted + " documents (" + indexPercentageCompleted + "%). ETA "+dateFormat.format( eta ) );
    }

    private void logIndexingCompleted( int numberOfDocuments, StopWatch indexingStopWatch ) {
        long time = indexingStopWatch.getTime();
        String humanReadableTime = HumanReadable.getHumanReadableTimeSpan( time ) ;
        long timePerDocument = time/numberOfDocuments ;
        log.debug( "Indexed " + numberOfDocuments + " documents in " + humanReadableTime+". "+timePerDocument+"ms per document." );
    }

    private void optimizeIndex( IndexWriter indexWriter ) throws IOException {
        StopWatch optimizeStopWatch = new StopWatch();
        optimizeStopWatch.start();
        indexWriter.optimize();
        optimizeStopWatch.stop();
        log.debug( "Optimized index in " + optimizeStopWatch.getTime() + "ms" );
    }

    public boolean isInconsistent() {
        return inconsistent;
    }

    public void delete() {
        try {
            log.debug("Deleting index directory "+directory) ;
            FileUtils.forceDelete(directory);
        } catch ( IOException e ) {
            throw new IndexException(e);
        }
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final DefaultDirectoryIndex that = (DefaultDirectoryIndex) o;

        return directory.equals(that.directory);

    }

    public int hashCode() {
        return directory.hashCode();
    }


    private static class DocumentIdHitsList extends AbstractList<Integer> {

        private final TopDocs topDocs;
        private final Searcher searcher;

        DocumentIdHitsList(TopDocs topDocs, Searcher searcher) {
            this.topDocs = topDocs;
            this.searcher = searcher;
        }

        public Integer get(int index) {
            try {
                Document luceneDocumment = searcher.doc(topDocs.scoreDocs[index].doc);
                
                return new Integer(luceneDocumment.get(DocumentIndex.FIELD__META_ID));
            } catch ( IOException e ) {
                throw new IndexException(e);
            }
        }

        public int size() {
            return topDocs.totalHits ;
        }
    }
}
