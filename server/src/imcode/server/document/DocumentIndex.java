package imcode.server.document;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.IntervalSchedule;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentIndex {

    public static final String FIELD__DOC_TYPE_ID = "doc_type_id";
    public static final String FIELD__IMAGE_LINK_URL = "image_link_url";
    public static final String FIELD__TEXT = "text";

    private static final String FIELD__ACTIVATED_DATETIME = "activated_datetime";
    private static final String FIELD__ARCHIVED_DATETIME = "archived_datetime";
    private static final String FIELD__CATEGORY_ID = "category_id";
    private static final String FIELD__CREATED_DATETIME = "created_datetime";
    private static final String FIELD__KEYWORD = "keyword";
    private static final String FIELD__META_HEADLINE = "meta_headline";
    private static final String FIELD__META_ID = "meta_id";
    private static final String FIELD__META_TEXT = "meta_text";
    private static final String FIELD__MODIFIED_DATETIME = "modified_datetime";
    private static final String FIELD__PARENT_ID = "parent_id";
    private static final String FIELD__PARENT_MENU_ID = "parent_menu_id";
    private static final String FIELD__PUBLICATION_END_DATETIME = "publication_end_datetime";
    private static final String FIELD__PUBLICATION_START_DATETIME = "publication_start_datetime";
    private static final String FIELD__SECTION = "section";
    private static final String FIELD__STATUS = "status";

    private static final int INDEXING_SCHEDULE_PERIOD__MILLISECONDS = DateUtils.MILLIS_IN_DAY;
    private final static int INDEXING_LOG_PERIOD__MILLISECONDS = 10 * 1000;
    private final static Logger log = Logger.getLogger( imcode.server.document.DocumentIndex.class.getName() );

    private File indexDirectory;
    private Thread indexingThread;
    private Timer indexingTimer;

    public DocumentIndex( File dir ) {
        this.indexDirectory = createIndexDirectoryInDirectory( dir );
        BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE ); // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow queries?
        indexingTimer = new Timer(true) ;
        indexingTimer.scheduleAtFixedRate(new IndexingTimerTask(), 0, INDEXING_SCHEDULE_PERIOD__MILLISECONDS);
    }

    public synchronized void indexDocument( DocumentDomainObject document ) {
        try {
            deleteDocumentFromIndex( document );
            addDocumentToIndex( document );
        } catch ( IOException e ) {
            log.error( "Failed to index document " + document.getId() + " to index. Reindexing..." );
            indexAllDocumentsInTheBackground();
        }
    }

    public Query parseLucene( String queryString ) throws ParseException {
        Query query = MultiFieldQueryParser.parse( queryString,
                                                   new String[]{"meta_headline", "meta_text", "text", "keyword"},
                                                   new Analyzer() );
        return query;
    }

    public synchronized DocumentDomainObject[] search( Query query, UserDomainObject searchingUser ) throws IOException {
        try {
            return trySearch( query, searchingUser );
        } catch ( IOException e1 ) {
            log.error( "Search failed. Reindexing and retrying...", e1 );
            indexAllDocuments();
            try {
                return trySearch( query, searchingUser );
            } catch ( IOException e2 ) {
                String errorMessage = "Search failed again, after reindexing and retrying.";
                log.fatal( errorMessage, e2 );
                IOException ex = new IOException( errorMessage );
                ex.initCause( e2 );
                throw ex;
            }
        }
    }

    private DocumentDomainObject[] trySearch( Query query, UserDomainObject searchingUser ) throws IOException {
        IndexReader indexReader = IndexReader.open( indexDirectory );
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
    }

    private List getDocumentListForHits( Hits hits, UserDomainObject searchingUser ) throws IOException {
        List documentList = new ArrayList( hits.length() );
        final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface()
                .getDocumentMapper();
        for ( int i = 0; i < hits.length(); ++i ) {
            int metaId = Integer.parseInt( hits.doc( i ).get( "meta_id" ) );
            DocumentDomainObject document = documentMapper.getDocument( metaId );
            if ( documentMapper.userHasPermissionToSearchDocument( searchingUser, document ) ) {
                documentList.add( document );
            }
        }
        return documentList;
    }

    private void deleteDocumentFromIndex( DocumentDomainObject document ) throws IOException {
        IndexReader indexReader = IndexReader.open( indexDirectory );
        try {
            indexReader.delete( new Term( "meta_id", "" + document.getId() ) );
        } finally {
            indexReader.close();
        }
    }

    private void addDocumentToIndex( DocumentDomainObject document ) throws IOException {
        IndexWriter indexWriter = createIndexWriter( indexDirectory, false );
        try {
            addDocumentToIndex( document, indexWriter );
        } finally {
            indexWriter.close();
        }
    }

    private synchronized void indexAllDocumentsInTheBackground() {
        if ( null != indexingThread && indexingThread.isAlive() ) {
            return;
        }
        indexingThread = new Thread( "Background indexing thread" ) {
            public void run() {
                indexAllDocuments();
                indexingThread = null;
            }
        };
        indexingThread.setDaemon( true );
        indexingThread.start();
    }

    private void indexAllDocuments() {
        NDC.push( "indexAllDocuments" );
        try {
            File newIndexDirectory = createIndexDirectoryInDirectory( indexDirectory.getParentFile() );
            indexAllDocumentsToDirectory( newIndexDirectory );
            replaceIndexDirectoryWith( newIndexDirectory );
        } catch ( IOException e ) {
            log.fatal( "Failed to index all documents.", e );
        } finally {
            NDC.pop();
        }
    }

    private File createIndexDirectoryInDirectory( File dir ) {
        File indexDirectory = null;
        try {
            if (!dir.isDirectory()) {
                FileUtils.deleteDirectory( dir );
                FileUtils.forceMkdir( dir );
            }
            indexDirectory = File.createTempFile( "index", "", dir );
            FileUtils.forceDelete( indexDirectory );
            FileUtils.forceMkdir( indexDirectory );
        } catch ( IOException e ) {
            log.fatal("Failed to create index directory.") ;
            throw new UnhandledException( e );
        }
        return indexDirectory;
    }

    private void indexAllDocumentsToDirectory( File indexDirectory ) throws IOException {
        IndexWriter indexWriter = createIndexWriter( indexDirectory, true );
        try {
            indexAllDocumentsToIndexWriter( indexWriter );
        } finally {
            indexWriter.close();
        }
    }

    private void indexAllDocumentsToIndexWriter( IndexWriter indexWriter ) throws IOException {
        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        int[] documentIds = documentMapper.getAllDocumentIds();

        logIndexingStarting( documentIds.length );
        IntervalSchedule indexingLogSchedule = new IntervalSchedule( INDEXING_LOG_PERIOD__MILLISECONDS );

        for ( int i = 0; i < documentIds.length; i++ ) {
            addDocumentToIndex( documentMapper.getDocument( documentIds[i] ), indexWriter );

            if ( indexingLogSchedule.isTime() ) {
                logIndexingProgress( i, documentIds.length );
            }
        }

        logIndexingCompleted( documentIds.length, indexingLogSchedule.getStopWatch() );
        optimizeIndex( indexWriter );
    }

    private synchronized void replaceIndexDirectoryWith( File directory ) throws IOException {
        File oldIndexDirectory = indexDirectory;
        indexDirectory = directory;
        FileUtils.deleteDirectory( oldIndexDirectory );
    }

    private IndexWriter createIndexWriter( File dir, boolean createIndex ) throws IOException {
        return new IndexWriter( dir, new Analyzer(), createIndex );
    }

    private void addDocumentToIndex( DocumentDomainObject document, IndexWriter indexWriter ) throws IOException {
        Document indexDocument = createIndexDocument( document );
        indexWriter.addDocument( indexDocument );
    }

    private void logIndexingStarting( int documentCount ) {
        log.info( "Building index of all " + documentCount + " documents" );
    }

    private void logIndexingProgress( int i, int numberOfDocuments ) {
        int indexPercentageCompleted = (int)( i * ( 100F / numberOfDocuments ) );
        log.info( "Completed " + indexPercentageCompleted + "% of the index." );
    }

    private void logIndexingCompleted( int numberOfDocuments, StopWatch indexingStopWatch ) {
        log.info( "Completed index of " + numberOfDocuments + " documents in " + indexingStopWatch.getTime()
                  + "ms" );
    }

    private void optimizeIndex( IndexWriter indexWriter ) throws IOException {
        StopWatch optimizeStopWatch = new StopWatch();
        optimizeStopWatch.start();
        indexWriter.optimize();
        optimizeStopWatch.stop();
        log.info( "Optimized index in " + optimizeStopWatch.getTime() + "ms" );
    }

    private Document createIndexDocument( DocumentDomainObject document ) {
        Document indexDocument = new Document();

        int documentId = document.getId();
        indexDocument.add( Field.Keyword( FIELD__META_ID, "" + documentId ) );
        indexDocument.add( Field.UnStored( FIELD__META_HEADLINE, document.getHeadline() ) );
        indexDocument.add( Field.UnStored( FIELD__META_TEXT, document.getMenuText() ) );
        indexDocument.add( unStoredKeyword( FIELD__DOC_TYPE_ID, "" + document.getDocumentTypeId() ) );
        SectionDomainObject[] sections = document.getSections();
        for ( int i = 0; i < sections.length; i++ ) {
            SectionDomainObject section = sections[i];
            indexDocument.add( unStoredKeyword( FIELD__SECTION, section.getName() ) );
        }

        addDateFieldToIndexDocument( documentId, indexDocument, FIELD__CREATED_DATETIME, document.getCreatedDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, FIELD__MODIFIED_DATETIME, document.getModifiedDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, FIELD__ACTIVATED_DATETIME, document.getPublicationStartDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, FIELD__PUBLICATION_START_DATETIME, document.getPublicationStartDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, FIELD__PUBLICATION_END_DATETIME, document.getPublicationEndDatetime() );
        addDateFieldToIndexDocument( documentId, indexDocument, FIELD__ARCHIVED_DATETIME, document.getArchivedDatetime() );

        indexDocument.add( unStoredKeyword( FIELD__STATUS, "" + document.getStatus() ) );

        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();

        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document;
            Iterator textsIterator = textDocument.getTexts().entrySet().iterator();
            while ( textsIterator.hasNext() ) {
                Map.Entry textEntry = (Map.Entry)textsIterator.next();
                Integer textIndex = (Integer)textEntry.getKey();
                TextDomainObject text = (TextDomainObject)textEntry.getValue();
                indexDocument.add( Field.UnStored( FIELD__TEXT, text.getText() ) );
                indexDocument.add( Field.UnStored( FIELD__TEXT + textIndex, text.getText() ) );
            }

            Iterator imagesIterator = textDocument.getImages().values().iterator();
            while ( imagesIterator.hasNext() ) {
                ImageDomainObject image = (ImageDomainObject)imagesIterator.next();
                String imageLinkUrl = image.getLinkUrl();
                if ( null != imageLinkUrl && imageLinkUrl.length() > 0 ) {
                    indexDocument.add( unStoredKeyword( FIELD__IMAGE_LINK_URL, imageLinkUrl ) );
                }
            }
        }

        CategoryDomainObject[] categories = document.getCategories();
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            indexDocument.add( unStoredKeyword( FIELD__CATEGORY_ID, "" + category.getId() ) );
        }

        String[] documentKeywords = document.getKeywords();
        for ( int i = 0; i < documentKeywords.length; i++ ) {
            String documentKeyword = documentKeywords[i];
            indexDocument.add( unStoredKeyword( FIELD__KEYWORD, documentKeyword ) );
        }

        String[][] parentDocumentAndMenuIds = documentMapper.getParentDocumentAndMenuIdsForDocument( document );
        for ( int i = 0; i < parentDocumentAndMenuIds.length; i++ ) {
            String parentId = parentDocumentAndMenuIds[i][0];
            String menuId = parentDocumentAndMenuIds[i][1];
            indexDocument.add( unStoredKeyword( FIELD__PARENT_ID, parentId ) );
            indexDocument.add( unStoredKeyword( FIELD__PARENT_MENU_ID, parentId + "_" + menuId ) );
        }

        return indexDocument;
    }

    private void addDateFieldToIndexDocument( int documentId, Document indexDocument, String fieldName, Date date ) {
        if ( null != date ) {
            try {
                indexDocument.add( unStoredKeyword( fieldName, date ) );
            } catch ( RuntimeException re ) {
                DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_NO_SECONDS_FORMAT_STRING );
                log.warn( "Failed to index datetime '" + dateFormat.format( date ) + "' in field '" + fieldName
                          + "' of document "
                          + documentId, re );
            }
        }
    }

    private Field unStoredKeyword( String fieldName, String fieldValue ) {
        return new Field( fieldName, fieldValue.toLowerCase(), false, true, false );
    }

    private Field unStoredKeyword( String fieldName, Date fieldValue ) {
        Date truncatedDate = truncateDateToMinutePrecision( fieldValue );
        return new Field( fieldName, DateField.dateToString( truncatedDate ), false, true, false );
    }

    private Date truncateDateToMinutePrecision( Date fieldValue ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( fieldValue );
        calendar.set( Calendar.MILLISECOND, 0 );
        calendar.set( Calendar.SECOND, 0 );
        Date truncatedDate = calendar.getTime();
        return truncatedDate;
    }

    private static class Analyzer extends org.apache.lucene.analysis.Analyzer {

        public TokenStream tokenStream( String fieldName, Reader reader ) {

            Tokenizer tokenizer;
            if ( FIELD__SECTION.equals( fieldName ) || FIELD__KEYWORD.equals( fieldName ) ) {
                tokenizer = new NullTokenizer( reader );
            } else {
                tokenizer = new LetterOrDigitTokenizer( reader );
            }
            return new LowerCaseFilter( tokenizer );
        }

        private static class NullTokenizer extends CharTokenizer {

            private NullTokenizer( Reader reader ) {
                super( reader );
            }

            protected boolean isTokenChar( char c ) {
                return true;
            }
        }

        private static class LetterOrDigitTokenizer extends CharTokenizer {

            private LetterOrDigitTokenizer( Reader reader ) {
                super( reader );
            }

            protected boolean isTokenChar( char c ) {
                return Character.isLetterOrDigit( c ) || '_' == c;
            }
        }
    }

    private class IndexingTimerTask extends TimerTask {

        public void run() {
            Date nextExecutionTime = new Date(this.scheduledExecutionTime()+INDEXING_SCHEDULE_PERIOD__MILLISECONDS) ;
            String nextExecutionTimeString = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_NO_SECONDS_FORMAT_STRING).format( nextExecutionTime ) ;
            log.info( "Starting scheduled indexing. Next indexing at "+nextExecutionTimeString);
            indexAllDocumentsInTheBackground();
        }
    }
}
