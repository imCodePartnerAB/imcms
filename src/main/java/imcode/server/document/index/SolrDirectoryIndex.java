package imcode.server.document.index;

import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.DateUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class SolrDirectoryIndex implements DirectoryIndex {

    private final static Logger log = Logger.getLogger( SolrDirectoryIndex.class.getName() );
    private final static int INDEXING_BULK_SIZE = 1000;
    private final static String TIMESTAMP_FIELD = "timestamp";

    private final static DateFormat solrDateFormat = DateUtil.getThreadLocalDateFormat();

    private final SolrServer solrServer;
    private final SolrIndexDocumentFactory indexDocumentFactory;

    private boolean inconsistent;

    private static final int NUM_HITS = 1;

    SolrDirectoryIndex(SolrServer solrServer, SolrIndexDocumentFactory indexDocumentFactory) {
        this.solrServer = solrServer;
        this.indexDocumentFactory = indexDocumentFactory;
    }
    
    public void indexDocument(Integer docId) throws IndexException {
        try {
            addDocumentToIndex(docId);
        } catch (Exception e ) {
            throw new IndexException( e );
        }
    }

    public void indexDocument(DocumentDomainObject document) throws IndexException {
        indexDocument(document.getId());
    }

    private void addDocumentToIndex(Integer docId) throws SolrServerException, IOException {
        SolrInputDocument indexDocument = indexDocumentFactory.createIndexDocument(docId);
        solrServer.add(indexDocument);
        solrServer.commit();
    }

    public void removeDocument(Integer docId) throws IndexException {
        try {
            solrServer.deleteByQuery("meta_id:" + docId);
            solrServer.commit();
        }
        catch (Exception e) {
            throw new IndexException(e);
        }
    }

    public void removeDocument(DocumentDomainObject document) throws IndexException {
        removeDocument(document.getId());
    }

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        try {
            StopWatch searchStopWatch = new StopWatch();
            searchStopWatch.start();
            SolrQuery sq = new SolrQuery(query.getQuery().toString());
            sq.setRows(NUM_HITS);

            QueryResponse qRes = solrServer.query(sq);
            sq.setRows((int)qRes.getResults().getNumFound());
            qRes = solrServer.query(sq);

            long searchTime = searchStopWatch.getTime();
            List<DocumentDomainObject> documentList = getDocumentList(qRes.getResults(), searchingUser );
            if (log.isDebugEnabled()) {
                log.debug( "Search for " + query.getQuery().toString() + ": " + searchTime + "ms. Total: "
                       + searchStopWatch.getTime()
                       + "ms." );
            }
            return documentList ;
        } 
        catch ( SolrServerException e ) {
            throw new IndexException( e ) ;
        }
    }

    public void rebuild() throws IndexException {
        try {
            Date baseDate = indexAllDocuments();
            deleteObsoleteDocuments(baseDate);
        } catch ( Exception e ) {
            throw new IndexException( e );
        }
    }

    public void deleteObsoleteDocuments(Date baseDate) throws IOException, SolrServerException {
        String deleteQuery = String.format("%1$s:[* TO %2$s]", TIMESTAMP_FIELD, solrDateFormat.format(baseDate));
        solrServer.deleteByQuery(deleteQuery);
        solrServer.commit();
    }

    public boolean isInconsistent() throws IndexException {
        return inconsistent;
    }

    public void delete() throws IndexException {
        try {
            solrServer.deleteByQuery("*");
            solrServer.commit();
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

    private List<DocumentDomainObject> getDocumentList( final SolrDocumentList docsList, 
                                                               final UserDomainObject searchingUser ) {
        DocumentGetter documentGetter = Imcms.getServices().getDocumentMapper();
        List<Integer> documentIds = new DocumentIdsList(docsList) ;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<DocumentDomainObject> documentList = documentGetter.getDocuments(documentIds);
        stopWatch.stop();
        if (log.isDebugEnabled()) {
            log.debug("Got "+documentList.size()+" documents in "+stopWatch.getTime()+"ms.");
        }
        if (documentList.size() != docsList.getNumFound()) {
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

    private Date indexAllDocuments() throws SolrServerException, IOException {
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        List<Integer> documentIds = documentMapper.getAllDocumentIds();
        int documentsCount = documentIds.size();

        logIndexingStarting(documentsCount);

        Date baseDate = new Date();
        ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>(INDEXING_BULK_SIZE);
        for (Integer docId: documentIds) {
            SolrInputDocument indexDocument = indexDocumentFactory.createIndexDocument(docId);
            docs.add(indexDocument);

            if (docs.size() >= INDEXING_BULK_SIZE) {
                solrServer.add(docs);
                solrServer.commit();
                docs.clear();
            }
        }

        if (docs.size() > 0) {
            solrServer.add(docs);
            solrServer.commit();
        }

        logIndexingCompleted(documentsCount);
        solrServer.optimize();

        return baseDate;
    }

    private void logIndexingStarting( int numberOfDocuments ) {
        log.debug( "Building index of all " + numberOfDocuments + " documents" );
    }

    private void logIndexingCompleted( int numberOfDocuments) {
        log.debug( "Building index of all " + numberOfDocuments + " documents completed" );    
    }

    private static class DocumentIdsList extends AbstractList<Integer> {

        private final SolrDocumentList docsList;

        DocumentIdsList(SolrDocumentList docsList) {
            this.docsList = docsList;
        }

        public Integer get(int index) {
            SolrDocument solrDocument = docsList.get(index);
            return new Integer(solrDocument.getFieldValue(DocumentIndex.FIELD__META_ID).toString());
        }

        public int size() {
            return docsList.size();
        }
    }
}
