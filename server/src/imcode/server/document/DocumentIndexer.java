/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-jan-26
 * Time: 14:06:04
 */
package imcode.server.document;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class DocumentIndexer {

    File dir ;

    public DocumentIndexer( File dir ) {
        this.dir = dir ;
    }

    public void indexAllDocuments() throws IOException {

        IndexWriter indexWriter = new IndexWriter( dir, new SimpleAnalyzer(), true );
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String[] documentIds = imcref.sqlQuery( "SELECT meta_id FROM meta", new String[0] );

        for ( int i = 0; i < documentIds.length; i++ ) {
            int documentId = Integer.parseInt( documentIds[i] );
            DocumentDomainObject document = imcref.getDocument( documentId );
            addDocumentToIndex( document, indexWriter );
        }
        indexWriter.optimize();
        indexWriter.close();
    }

    public void reindexOneDocument( DocumentDomainObject document ) throws IOException {
        deleteDocumentFromIndex( document );
        addDocumentToIndex( document );
    }

    private void addDocumentToIndex( DocumentDomainObject document ) throws IOException {
        IndexWriter indexWriter = new IndexWriter( dir, new SimpleAnalyzer(), false );
        addDocumentToIndex( document, indexWriter );
        indexWriter.close() ;
    }

    private void addDocumentToIndex( DocumentDomainObject document, IndexWriter indexWriter ) throws IOException {
        Document indexDocument = createIndexDocument( document );
        indexWriter.addDocument( indexDocument );
    }

    private Document createIndexDocument( DocumentDomainObject document ) throws IOException {
        Document indexDocument = new Document();
        indexDocument.add( Field.Text( "headline", document.getHeadline() ) );
        indexDocument.add( Field.Keyword( "meta_id", "" + document.getMetaId() ) );
        indexDocument.add( Field.Text( "meta_text", document.getText() ));
        SectionDomainObject[] sections = document.getSections() ;
        for ( int i = 0; i < sections.length; i++ ) {
            SectionDomainObject section = sections[i];
            indexDocument.add( Field.Keyword( "section", section.getName())) ;
        }
        if (null != document.getCreatedDatetime()) {
            indexDocument.add( Field.Keyword( "created_datetime", document.getCreatedDatetime() )) ;
        }
        if (null != document.getModifiedDatetime()) {
            indexDocument.add( Field.Keyword( "modified_datetime", document.getModifiedDatetime() )) ;
        }
        if (null != document.getActivatedDatetime()) {
            indexDocument.add( Field.Keyword( "activated_datetime", document.getActivatedDatetime() )) ;
        }
        if (null != document.getArchivedDatetime()) {
            indexDocument.add( Field.Keyword( "archived_datetime", document.getArchivedDatetime() )) ;
        }

        Iterator textsIterator = ApplicationServer.getIMCServiceInterface().getTexts( document.getMetaId() ).values().iterator() ;
        while ( textsIterator.hasNext() ) {
            TextDocumentTextDomainObject text = (TextDocumentTextDomainObject)textsIterator.next();
            indexDocument.add(Field.Text("text", text.getText())) ;
        }
        return indexDocument;
    }

    private void deleteDocumentFromIndex( DocumentDomainObject document ) throws IOException {
        IndexReader indexReader = IndexReader.open( dir );
        indexReader.delete( new Term("meta_id", ""+document.getMetaId()) ) ;
        indexReader.close();
    }

}