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
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DocumentIndex {

    private final static int INDEX_LOG_TIME_STEP = 2500;
    private final static Logger log = Logger.getLogger( "imcode.server.document.DocumentIndex" );

    private File dir;
    private IndexWriter indexWriter;

    public DocumentIndex( File dir ) {
        this.dir = dir;
    }

    public DocumentDomainObject[] search( Query query, UserDomainObject searchingUser ) throws IOException {
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
            if ( documentMapper.hasPermissionToSearchDocument( searchingUser, document ) ) {
                result.add( document );
            }
        }
        log.debug( "Search and result lookup took " + searchStopWatch.getTime() + "ms." );
        logGetDocumentsStopWatch( getDocumentStopWatch, result.size() );
        indexSearcher.close();
        indexReader.close();
        return (DocumentDomainObject[])result.toArray( new DocumentDomainObject[result.size()] );
    }

    public Query parseLucene( String queryString ) throws ParseException {
        return MultiFieldQueryParser.parse( queryString,
                                            new String[]{"meta_headline", "meta_text", "text", "keyword"},
                                            new WhitespaceLowerCaseAnalyzer() );

    }

    public void indexAllDocuments() {
        NDC.push( "indexAllDocuments" );
        try {
            openIndexWriter( true );
            IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

            String[] documentIds = getAllDocumentIds( imcref );
            log.info( "Building index of all " + documentIds.length + " documents" );
            DocumentMapper documentMapper = imcref.getDocumentMapper();
            int nextIndexLogTime = INDEX_LOG_TIME_STEP;
            StopWatch indexingStopWatch = new StopWatch();
            StopWatch getDocumentStopWatch = new StopWatch();
            indexingStopWatch.start();
            for ( int i = 0; i < documentIds.length; i++ ) {
                int documentId = Integer.parseInt( documentIds[i] );
                DocumentDomainObject document = getDocument( getDocumentStopWatch, documentMapper, documentId );
                addDocumentToIndex( document, indexWriter );
                if ( indexingStopWatch.getTime() >= nextIndexLogTime ) {
                    int indexPercentageCompleted = (int)( i * ( 100F / documentIds.length ) );
                    log.info( "Completed " + indexPercentageCompleted + "% of the index." );
                    nextIndexLogTime += INDEX_LOG_TIME_STEP;
                }
            }
            indexingStopWatch.stop();
            log.info(
                    "Completed index of " + documentIds.length + " documents in " + indexingStopWatch.getTime() + "ms" );
            logGetDocumentsStopWatch( getDocumentStopWatch, documentIds.length );
            optimizeIndex( indexWriter );
            closeIndexWriter();
        } catch ( Exception e ) {
            log.error( "Failed to index all documents", e );
        } finally {
            NDC.pop();
        }
    }

    private String[] getAllDocumentIds( IMCServiceInterface imcref ) {
        return imcref.sqlQuery( "SELECT meta_id FROM meta", new String[0] );
    }

    private synchronized void closeIndexWriter() throws IOException {
        indexWriter.close();
        indexWriter = null;
    }

    private synchronized void openIndexWriter( final boolean createIndex ) throws IOException {
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

    public void reindexOneDocument( DocumentDomainObject document ) throws IOException {
        deleteDocumentFromIndex( document );
        addDocumentToIndex( document );
    }

    private void addDocumentToIndex( DocumentDomainObject document ) throws IOException {
        openIndexWriter( false );
        addDocumentToIndex( document, indexWriter );
        closeIndexWriter();
    }

    private void addDocumentToIndex( DocumentDomainObject document, IndexWriter indexWriter ) throws IOException {
        if ( !document.isSearchDisabled() ) {
            Document indexDocument = createIndexDocument( document );
            indexWriter.addDocument( indexDocument );
        }
    }

    private Document createIndexDocument( DocumentDomainObject document ) {
        Document indexDocument = new Document();
        indexDocument.add( Field.Keyword( "meta_id", "" + document.getMetaId() ) );
        indexDocument.add( unStoredKeyword( "meta_headline_keyword", document.getHeadline().toLowerCase() ) );
        indexDocument.add( Field.UnStored( "meta_headline", document.getHeadline() ) );
        indexDocument.add( Field.UnStored( "meta_text", document.getMenuText() ) );
        indexDocument.add( unStoredKeyword( "doc_type_id", "" + document.getDocumentType() ) );
        SectionDomainObject[] sections = document.getSections();
        for ( int i = 0; i < sections.length; i++ ) {
            SectionDomainObject section = sections[i];
            indexDocument.add( unStoredKeyword( "section", section.getName().toLowerCase() ) );
        }
        if ( null != document.getCreatedDatetime() ) {
            indexDocument.add( unStoredKeyword( "created_datetime", document.getCreatedDatetime() ) );
        }
        if ( null != document.getModifiedDatetime() ) {
            indexDocument.add( unStoredKeyword( "modified_datetime", document.getModifiedDatetime() ) );
        }
        if ( null != document.getActivatedDatetime() ) {
            indexDocument.add( unStoredKeyword( "activated_datetime", document.getActivatedDatetime() ) );
        }
        if ( null != document.getArchivedDatetime() ) {
            indexDocument.add( unStoredKeyword( "archived_datetime", document.getArchivedDatetime() ) );
        }

        Iterator textsIterator = ApplicationServer.getIMCServiceInterface().getDocumentMapper()
                .getTexts( document.getMetaId() ).entrySet().iterator();
        while ( textsIterator.hasNext() ) {
            Map.Entry textEntry = (Map.Entry)textsIterator.next();
            String textIndexString = (String)textEntry.getKey();
            TextDocumentTextDomainObject text = (TextDocumentTextDomainObject)textEntry.getValue();
            indexDocument.add( Field.UnStored( "text", text.getText() ) );
            indexDocument.add( Field.UnStored( "text" + textIndexString, text.getText() ) );
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

    private void deleteDocumentFromIndex( DocumentDomainObject document ) throws IOException {
        IndexReader indexReader = IndexReader.open( dir );
        indexReader.delete( new Term( "meta_id", "" + document.getMetaId() ) );
        indexReader.close();
    }

    private Field unStoredKeyword( String fieldName, String fieldValue ) {
        return new Field( fieldName, fieldValue, false, true, false );
    }

    private Field unStoredKeyword( String fieldName, Date fieldValue ) {
        return new Field( fieldName, DateField.dateToString( fieldValue ), false, true, false );
    }
}