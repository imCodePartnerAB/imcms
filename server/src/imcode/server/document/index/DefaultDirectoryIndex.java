package imcode.server.document.index;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.util.HumanReadable;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.IntervalSchedule;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.AbstractList;

class DefaultDirectoryIndex implements DirectoryIndex {

    private final static Logger log = Logger.getLogger( DefaultDirectoryIndex.class.getName() );
    private final static int INDEXING_LOG_PERIOD__MILLISECONDS = DateUtils.MILLIS_IN_MINUTE ;

    private final File directory;
    private final IndexDocumentFactory indexDocumentFactory;

    private boolean inconsistent;

    static {
        // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow or memoryconsuming queries?
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    DefaultDirectoryIndex(File directory, IndexDocumentFactory indexDocumentFactory) {
        this.directory = directory;
        this.indexDocumentFactory = indexDocumentFactory;
    }

    public List search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        try {
            IndexSearcher indexSearcher = new IndexSearcher( directory.toString() );
            try {
                StopWatch searchStopWatch = new StopWatch();
                searchStopWatch.start();
                Hits hits = indexSearcher.search( query.getQuery(), query.getSort() );
                long searchTime = searchStopWatch.getTime();
                List documentList = getDocumentListForHits( hits, searchingUser );
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

    private List getDocumentListForHits( final Hits hits, final UserDomainObject searchingUser ) {
        DocumentGetter documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter();
        List documentIds = new DocumentIdHitsList(hits) ;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List documentList = documentGetter.getDocuments(documentIds) ;
        stopWatch.stop();
        if (log.isDebugEnabled()) {
            log.debug("Got "+documentList.size()+" documents in "+stopWatch.getTime()+"ms.");
        }
        if (documentList.size() != hits.length()) {
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
                indexReader.delete( new Term( "meta_id", "" + document.getId() ) );
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
        return new IndexWriter( directory, new AnalyzerImpl(), createIndex );
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

    private void indexAllDocumentsToIndexWriter( IndexWriter indexWriter ) throws IOException {
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        int[] documentIds = documentMapper.getAllDocumentIds();

        logIndexingStarting( documentIds.length );
        IntervalSchedule indexingLogSchedule = new IntervalSchedule( INDEXING_LOG_PERIOD__MILLISECONDS );

        for ( int i = 0; i < documentIds.length; i++ ) {
            try {
                addDocumentToIndex( documentMapper.getDocument( documentIds[i] ), indexWriter );
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

    private static class DocumentIdHitsList extends AbstractList {

        private final Hits hits;

        DocumentIdHitsList(Hits hits) {
            this.hits = hits;
        }

        public Object get(int index) {
            try {
                return new Integer(Integer.parseInt( hits.doc( index ).get( DocumentIndex.FIELD__META_ID ) )) ;
            } catch ( IOException e ) {
                throw new IndexException(e);
            }
        }

        public int size() {
            return hits.length() ;
        }
    }
}
