/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-jan-26
 * Time: 14:06:04
 */
package imcode.server.document;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class DocumentIndex {

    private final static int INDEX_LOG_TIME_STEP = 2500;
    private final static Logger log = Logger.getLogger( imcode.server.document.DocumentIndex.class.getName() );

    private File dir;
    private IndexWriter indexWriter;

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
        log.debug( "Search for " + query.toString() + " took " + searchStopWatch.getTime() + "ms." );
        List result = new ArrayList( hits.length() );
        StopWatch getDocumentStopWatch = new StopWatch();
        final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface()
                .getDocumentMapper();
        for ( int i = 0; i < hits.length(); ++i ) {
            int metaId = Integer.parseInt( hits.doc( i ).get( "meta_id" ) );
            DocumentDomainObject document = getDocument( getDocumentStopWatch, documentMapper, metaId );
            if ( documentMapper.userHasPermissionToSearchDocument( searchingUser, document ) ) {
                result.add( document );
            }
        }
        log.debug( "Search and result lookup took " + searchStopWatch.getTime() + "ms." );
        logGetDocumentsStopWatch( getDocumentStopWatch, result.size() );
        indexSearcher.close();
        indexReader.close();
        return (DocumentDomainObject[])result.toArray( new DocumentDomainObject[result.size()] );
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
                DocumentDomainObject document = getDocument( getDocumentStopWatch, documentMapper, documentId );
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
        Query query = QueryParser.parse( queryString,
                                          "default",
                                          new WhitespaceLowerCaseAnalyzer() );
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
            indexWriter = new IndexWriter( dir, new WhitespaceLowerCaseAnalyzer(), createIndex );
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

    private DocumentDomainObject getDocument( StopWatch getDocumentStopWatch, DocumentMapper documentMapper,
                                              int documentId ) {
        getDocumentStopWatch.resume();
        DocumentDomainObject document = documentMapper.getDocument( documentId );
        getDocumentStopWatch.suspend();
        return document;
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
        indexDocument.add( Field.Keyword( "meta_id", "" + documentId ) );
        indexDocument.add( Field.UnStored( "meta_headline", document.getHeadline() ) );
        indexDocument.add( Field.UnStored( "default", document.getHeadline() ) );
        indexDocument.add( Field.UnStored( "meta_text", document.getMenuText() ) );
        indexDocument.add( Field.UnStored( "default", document.getMenuText() ) );
        indexDocument.add( unStoredKeyword( "doc_type_id", "" + document.getDocumentTypeId() ) );
        SectionDomainObject[] sections = document.getSections();
        for ( int i = 0; i < sections.length; i++ ) {
            SectionDomainObject section = sections[i];
            indexDocument.add( unStoredKeyword( "section", section.getName().toLowerCase() ) );
        }
        if ( null != document.getCreatedDatetime() ) {
            try {
                indexDocument.add( unStoredKeyword( "created_datetime", document.getCreatedDatetime() ) );
            } catch ( RuntimeException re ) {
                log.warn( "Indexing document " + documentId, re ) ;
            }
        }

        addDateField( documentId, indexDocument, "modified_datetime", document.getModifiedDatetime() );
        addDateField( documentId, indexDocument, "activated_datetime", document.getPublicationStartDatetime() );
        addDateField( documentId, indexDocument, "publication_start_datetime", document.getPublicationStartDatetime() );
        addDateField( documentId, indexDocument, "publication_end_datetime", document.getPublicationEndDatetime() );
        addDateField( documentId, indexDocument, "archived_datetime", document.getArchivedDatetime() );

        indexDocument.add( unStoredKeyword( "status", "" + document.getStatus() ) );

        Iterator textsIterator = ApplicationServer.getIMCServiceInterface().getDocumentMapper()
                .getTexts( documentId ).entrySet().iterator();
        while ( textsIterator.hasNext() ) {
            Map.Entry textEntry = (Map.Entry)textsIterator.next();
            String textIndexString = (String)textEntry.getKey();
            TextDocumentDomainObject.Text text = (TextDocumentDomainObject.Text)textEntry.getValue();
            indexDocument.add( Field.UnStored( "text", text.getText() ) );
            indexDocument.add( Field.UnStored( "text" + textIndexString, text.getText() ) );
            indexDocument.add( Field.UnStored( "default", text.getText() ) );
        }

        CategoryDomainObject[] categories = document.getCategories();
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            indexDocument.add( unStoredKeyword( "category_id", "" + category.getId() ) );
        }

        String[] documentKeywords = document.getKeywords();
        for ( int i = 0; i < documentKeywords.length; i++ ) {
            String documentKeyword = documentKeywords[i];
            indexDocument.add( unStoredKeyword( "keyword", documentKeyword ) );
            indexDocument.add( unStoredKeyword( "default", documentKeyword ) );
        }

        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        String[][] parentDocumentAndMenuIds = documentMapper.getParentDocumentAndMenuIdsForDocument( document );
        for ( int i = 0; i < parentDocumentAndMenuIds.length; i++ ) {
            String parentId = parentDocumentAndMenuIds[i][0];
            String menuId = parentDocumentAndMenuIds[i][1];
            indexDocument.add( unStoredKeyword( "parent_id", parentId ) );
            indexDocument.add( unStoredKeyword( "parent_menu_id", parentId + "_" + menuId ) );
        }

        return indexDocument;
    }

    private void addDateField( int documentId, Document indexDocument, String fieldName, Date date ) {
        if ( null != date ) {
            try {
                indexDocument.add( unStoredKeyword( fieldName, date ) );
            } catch ( RuntimeException re ) {
                DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_NO_SECONDS_FORMAT_STRING) ;
                log.warn( "Failed to index datetime '"+dateFormat.format(date)+"' in field '"+fieldName+"' of document " + documentId, re ) ;
            }
        }
    }

    private void deleteDocumentFromIndex( DocumentDomainObject document ) throws IOException {
        IndexReader indexReader = IndexReader.open( dir );
        indexReader.delete( new Term( "meta_id", "" + document.getId() ) );
        indexReader.close();
    }

    private Field unStoredKeyword( String fieldName, String fieldValue ) {
        return new Field( fieldName, fieldValue, false, true, false );
    }

    private Field unStoredKeyword( String fieldName, Date fieldValue ) {
        Date truncatedDate = truncateDateToMinutePrecision( fieldValue );
        return new Field( fieldName, DateField.dateToString( truncatedDate ), false, true, false );
    }

    private Date truncateDateToMinutePrecision( Date fieldValue ) {
        Calendar calendar = Calendar.getInstance() ;
        calendar.setTime( fieldValue );
        calendar.set( Calendar.MILLISECOND, 0 );
        calendar.set( Calendar.SECOND, 0 );
        Date roundedDate = calendar.getTime();
        return roundedDate;
    }
}
