package imcode.server.document;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.lucene.analysis.*;
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

    private final static int INDEX_LOG_TIME_STEP = 2500;
    private final static Logger log = Logger.getLogger( imcode.server.document.DocumentIndex.class.getName() );

    private File dir;
    private IndexWriter indexWriter;

    private static final String FIELD__META_ID = "meta_id";
    private static final String FIELD__META_HEADLINE = "meta_headline";
    private static final String FIELD__META_TEXT = "meta_text";
    public static final String FIELD__DOC_TYPE_ID = "doc_type_id";
    private static final String FIELD__SECTION = "section";
    private static final String FIELD__CREATED_DATETIME = "created_datetime";
    private static final String FIELD__MODIFIED_DATETIME = "modified_datetime";
    private static final String FIELD__ACTIVATED_DATETIME = "activated_datetime";
    private static final String FIELD__PUBLICATION_START_DATETIME = "publication_start_datetime";
    private static final String FIELD__PUBLICATION_END_DATETIME = "publication_end_datetime";
    private static final String FIELD__ARCHIVED_DATETIME = "archived_datetime";
    private static final String FIELD__STATUS = "status";
    public static final String FIELD__TEXT = "text";
    private static final String FIELD__CATEGORY_ID = "category_id";
    private static final String FIELD__KEYWORD = "keyword";
    private static final String FIELD__PARENT_ID = "parent_id";
    private static final String FIELD__PARENT_MENU_ID = "parent_menu_id";
    public static final String FIELD__IMAGE_LINK_URL = "image_link_url";

    public DocumentIndex( File dir ) {
        this.dir = dir;
        BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE ); // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow queries?
    }

    public synchronized DocumentDomainObject[] search( Query query, UserDomainObject searchingUser ) throws IOException {
        IndexReader indexReader = IndexReader.open( dir );
        IndexSearcher indexSearcher = new IndexSearcher( indexReader );
        StopWatch searchStopWatch = new StopWatch();
        searchStopWatch.start();
        Hits hits = indexSearcher.search( query );
        long searchTime = searchStopWatch.getTime();
        List documentList = getDocumentListForHits( hits, searchingUser );
        log.debug( "Search for " + query.toString() + ": " + searchTime + "ms. Total: " + searchStopWatch.getTime()
                   + "ms." );
        indexSearcher.close();
        return (DocumentDomainObject[])documentList.toArray( new DocumentDomainObject[documentList.size()] );
    }

    private List getDocumentListForHits( Hits hits, UserDomainObject searchingUser ) throws IOException {
        List documentList = new ArrayList( hits.length() );
        final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface()
                .getDocumentMapper();
        StopWatch getDocumentStopWatch = new StopWatch();
        for ( int i = 0; i < hits.length(); ++i ) {
            int metaId = Integer.parseInt( hits.doc( i ).get( "meta_id" ) );
            getDocumentStopWatch.resume();
            DocumentDomainObject document = documentMapper.getDocument( metaId );
            if ( documentMapper.userHasPermissionToSearchDocument( searchingUser, document ) ) {
                documentList.add( document );
            }
            getDocumentStopWatch.suspend();
        }
        logGetDocumentsStopWatch( getDocumentStopWatch, documentList.size() );
        return documentList;
    }

    public synchronized void indexAllDocuments() {
        NDC.push( "indexAllDocuments" );
        try {
            openIndexWriter( true );
            IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

            String[] documentIds = getAllDocumentIds( imcref );
            int numberOfDocuments = documentIds.length;
            log.info( "Building index of all " + numberOfDocuments + " documents" );
            DocumentMapper documentMapper = imcref.getDocumentMapper();
            int nextIndexLogTime = INDEX_LOG_TIME_STEP;
            StopWatch indexingStopWatch = new StopWatch();
            indexingStopWatch.start();
            StopWatch getDocumentStopWatch = new StopWatch();
            for ( int i = 0; i < numberOfDocuments; i++ ) {
                int documentId = Integer.parseInt( documentIds[i] );
                getDocumentStopWatch.resume();
                DocumentDomainObject document = documentMapper.getDocument( documentId );
                getDocumentStopWatch.suspend();
                addDocumentToIndex( document, indexWriter );
                if ( indexingStopWatch.getTime() >= nextIndexLogTime ) {
                    logIndexingProgress( i, numberOfDocuments );
                    nextIndexLogTime += INDEX_LOG_TIME_STEP;
                }
            }
            indexingStopWatch.stop();
            logIndexingCompleted( numberOfDocuments, indexingStopWatch );
            logGetDocumentsStopWatch( getDocumentStopWatch, numberOfDocuments );
            optimizeIndex( indexWriter );
            closeIndexWriter();
        } catch ( Exception e ) {
            log.error( "Failed to index all documents", e );
        } finally {
            NDC.pop();
        }
    }

    public synchronized void reindexOneDocument( DocumentDomainObject document ) throws IOException {
        deleteDocumentFromIndex( document );
        addDocumentToIndex( document );
    }

    public Query parseLucene( String queryString ) throws ParseException {
        Query query = MultiFieldQueryParser.parse( queryString,
                                                   new String[]{"meta_headline", "meta_text", "text", "keyword"},
                                                   new Analyzer() );
        return query;
    }

    private void logIndexingCompleted( int numberOfDocuments, StopWatch indexingStopWatch ) {
        log.info( "Completed index of " + numberOfDocuments + " documents in " + indexingStopWatch.getTime()
                  + "ms" );
    }

    private void logIndexingProgress( int i, int numberOfDocuments ) {
        int indexPercentageCompleted = (int)( i * ( 100F / numberOfDocuments ) );
        log.info( "Completed " + indexPercentageCompleted + "% of the index." );
    }

    private String[] getAllDocumentIds( IMCServiceInterface imcref ) {
        return imcref.sqlQuery( "SELECT meta_id FROM meta", new String[0] );
    }

    private void closeIndexWriter() throws IOException {
        indexWriter.close();
        indexWriter = null;
    }

    private void openIndexWriter( final boolean createIndex ) throws IOException {
        if ( null == indexWriter ) {
            indexWriter = new IndexWriter( dir, new Analyzer(), createIndex );
        }
    }

    private void logGetDocumentsStopWatch( StopWatch getDocumentStopWatch, int documentCount ) {
        long time = getDocumentStopWatch.getTime();
        if ( 0 != documentCount ) {
            long millisecondsPerDocument = time / documentCount;
            log.debug( "Spent " + time + "ms (" + millisecondsPerDocument + "ms/document) fetching " + documentCount
                       + " documents from the database." );
        }
    }

    private void optimizeIndex( IndexWriter indexWriter ) throws IOException {
        StopWatch optimizeStopWatch = new StopWatch();
        optimizeStopWatch.start();
        indexWriter.optimize();
        optimizeStopWatch.stop();
        log.info( "Optimized index in " + optimizeStopWatch.getTime() + "ms" );
    }

    private void addDocumentToIndex( DocumentDomainObject document ) throws IOException {
        openIndexWriter( false );
        addDocumentToIndex( document, indexWriter );
        closeIndexWriter();
    }

    private void addDocumentToIndex( DocumentDomainObject document, IndexWriter indexWriter ) throws IOException {
        Document indexDocument = createIndexDocument( document );
        indexWriter.addDocument( indexDocument );
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
        if ( null != document.getCreatedDatetime() ) {
            try {
                indexDocument.add( unStoredKeyword( FIELD__CREATED_DATETIME, document.getCreatedDatetime() ) );
            } catch ( RuntimeException re ) {
                log.warn( "Indexing document " + documentId, re );
            }
        }

        addDateField( documentId, indexDocument, FIELD__MODIFIED_DATETIME, document.getModifiedDatetime() );
        addDateField( documentId, indexDocument, FIELD__ACTIVATED_DATETIME, document.getPublicationStartDatetime() );
        addDateField( documentId, indexDocument, FIELD__PUBLICATION_START_DATETIME, document.getPublicationStartDatetime() );
        addDateField( documentId, indexDocument, FIELD__PUBLICATION_END_DATETIME, document.getPublicationEndDatetime() );
        addDateField( documentId, indexDocument, FIELD__ARCHIVED_DATETIME, document.getArchivedDatetime() );

        indexDocument.add( unStoredKeyword( FIELD__STATUS, "" + document.getStatus() ) );

        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document;
            Iterator textsIterator = textDocument.getTexts().entrySet().iterator();
            while ( textsIterator.hasNext() ) {
                Map.Entry textEntry = (Map.Entry)textsIterator.next();
                Integer textIndex = (Integer)textEntry.getKey();
                TextDocumentDomainObject.Text text = (TextDocumentDomainObject.Text)textEntry.getValue();
                indexDocument.add( Field.UnStored( FIELD__TEXT, text.getText() ) );
                indexDocument.add( Field.UnStored( FIELD__TEXT + textIndex, text.getText() ) );
            }

            Iterator imagesIterator = textDocument.getImages().values().iterator();
            while ( imagesIterator.hasNext() ) {
                TextDocumentDomainObject.Image image = (TextDocumentDomainObject.Image)imagesIterator.next();
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

    private void addDateField( int documentId, Document indexDocument, String fieldName, Date date ) {
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

    private void deleteDocumentFromIndex( DocumentDomainObject document ) throws IOException {
        IndexReader indexReader = IndexReader.open( dir );
        indexReader.delete( new Term( "meta_id", "" + document.getId() ) );
        indexReader.close();
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
                return '_' == c || Character.isLetterOrDigit( c ) ;
            }
        }

    }
}
