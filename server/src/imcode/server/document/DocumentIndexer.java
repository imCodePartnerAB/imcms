/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-jan-26
 * Time: 14:06:04
 */
package imcode.server.document;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class DocumentIndexer {

    private final static int INDEX_LOG_TIME_STEP = 2500;
    private final static Logger log = Logger.getLogger( "imcode.server.document.DocumentIndexer" );

    File dir;

    public DocumentIndexer( File dir ) {
        this.dir = dir;
    }

    public void indexAllDocuments() {
        try {
            IndexWriter indexWriter = new IndexWriter( dir, new WhitespaceLowerCaseAnalyzer(), true );
            IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

            String[] documentIds = imcref.sqlQuery( "SELECT meta_id FROM meta", new String[0] );
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
            log.info( "Time spent querying the database for documents: " + getDocumentStopWatch.getTime() + "ms" );
            optimizeIndex( indexWriter );
            indexWriter.close();
        } catch ( Exception e ) {
            log.error( "Failed to index all documents", e );
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
        IndexWriter indexWriter = new IndexWriter( dir, new SimpleAnalyzer(), false );
        addDocumentToIndex( document, indexWriter );
        indexWriter.close();
    }

    private void addDocumentToIndex( DocumentDomainObject document, IndexWriter indexWriter ) throws IOException {
        Document indexDocument = createIndexDocument( document );
        indexWriter.addDocument( indexDocument );
    }

    private Document createIndexDocument( DocumentDomainObject document ) throws IOException {
        Document indexDocument = new Document();
        indexDocument.add( Field.Keyword( "meta_id", "" + document.getMetaId() ) );
        indexDocument.add( unStoredKeyword( "meta_headline_keyword", document.getHeadline().toLowerCase() ) );
        indexDocument.add( Field.UnStored( "meta_headline", document.getHeadline() ) );
        indexDocument.add( Field.UnStored( "meta_text", document.getText() ) );
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

        CategoryDomainObject[] categories = document.getCategories() ;
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            indexDocument.add( unStoredKeyword( "category_id", ""+category.getId()));
        }

        String[] documentKeywords = document.getKeywords() ;
        for ( int i = 0; i < documentKeywords.length; i++ ) {
            String documentKeyword = documentKeywords[i];
            indexDocument.add( unStoredKeyword( "keyword", documentKeyword)) ;
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