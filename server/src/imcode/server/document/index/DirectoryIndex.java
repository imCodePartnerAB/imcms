package imcode.server.document.index;

import com.imcode.util.HumanReadable;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.IntervalSchedule;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class DirectoryIndex implements DocumentIndex {

    private File directory;
    private IndexDocumentFactory indexDocumentFactory = new IndexDocumentFactory();

    private final static int INDEXING_LOG_PERIOD__MILLISECONDS = DateUtils.MILLIS_IN_MINUTE ;
    private boolean inconsistent;

    private final static Logger log = Logger.getLogger( DirectoryIndex.class.getName() );

    DirectoryIndex( File directory ) {
        this.directory = directory;
    }

    public DocumentDomainObject[] search( Query query, UserDomainObject searchingUser ) throws IndexException {
        try {
            IndexReader indexReader = IndexReader.open( directory );
            IndexSearcher indexSearcher = null;
            try {
                indexSearcher = new IndexSearcher( indexReader );
                StopWatch searchStopWatch = new StopWatch();
                searchStopWatch.start();
                Hits hits = indexSearcher.search( query );
                long searchTime = searchStopWatch.getTime();
                List documentList = getDocumentListForHits( hits, searchingUser );
                log.debug( "Search for " + query.toString() + ": " + searchTime + "ms. Total: "
                           + searchStopWatch.getTime()
                           + "ms." );
                return (DocumentDomainObject[])documentList.toArray( new DocumentDomainObject[documentList.size()] );
            } finally {
                if ( null != indexSearcher ) {
                    indexSearcher.close();
                }
                indexReader.close();
            }
        } catch ( IOException e ) {
            throw new IndexException( e ) ;
        }
    }

    private List getDocumentListForHits( Hits hits, UserDomainObject searchingUser ) throws IOException {
        List documentList = new ArrayList( hits.length() );
        final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        for ( int i = 0; i < hits.length(); ++i ) {
            int metaId = Integer.parseInt( hits.doc( i ).get( DocumentIndex.FIELD__META_ID ) );
            DocumentDomainObject document = documentMapper.getDocument( metaId );
            if ( null == document ) {
                inconsistent = true;
                continue;
            }
            if ( searchingUser.canSearchFor( document ) ) {
                documentList.add( document );
            }
        }
        return documentList;
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

    void indexAllDocuments() throws IOException {
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
                logIndexingProgress( i, documentIds.length, indexingLogSchedule.getStopWatch() );
            }
            Thread.yield(); // To make sure other threads with the same priority get a chance to run something once in a while.
        }

        logIndexingCompleted( documentIds.length, indexingLogSchedule.getStopWatch() );
        optimizeIndex( indexWriter );
    }

    private void logIndexingStarting( int documentCount ) {
        log.info( "Building index of all " + documentCount + " documents" );
    }

    private void logIndexingProgress( int documentsCompleted, int numberOfDocuments, StopWatch stopWatch ) {
        int indexPercentageCompleted = (int)( documentsCompleted * ( 100F / numberOfDocuments ) );
        long elapsedTime = stopWatch.getTime() ;
        long estimatedTime = numberOfDocuments * elapsedTime / documentsCompleted;
        long estimatedTimeLeft = estimatedTime - elapsedTime ;
        Date eta = new Date(System.currentTimeMillis() + estimatedTimeLeft) ;
        DateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss");
        log.info( "Indexed " + documentsCompleted + " documents (" + indexPercentageCompleted + "%). ETA "+dateFormat.format( eta ) );
    }

    private void logIndexingCompleted( int numberOfDocuments, StopWatch indexingStopWatch ) {
        long time = indexingStopWatch.getTime();
        String humanReadableTime = HumanReadable.getHumanReadableTimeLength( time ) ;
        long timePerDocument = time/numberOfDocuments ;
        log.info( "Completed index of " + numberOfDocuments + " documents in " + humanReadableTime+". "+timePerDocument+"ms per document." );
    }

    private void optimizeIndex( IndexWriter indexWriter ) throws IOException {
        StopWatch optimizeStopWatch = new StopWatch();
        optimizeStopWatch.start();
        indexWriter.optimize();
        optimizeStopWatch.stop();
        log.info( "Optimized index in " + optimizeStopWatch.getTime() + "ms" );
    }

    File getDirectory() {
        return directory;
    }

    public boolean isInconsistent() {
        return inconsistent;
    }
}
