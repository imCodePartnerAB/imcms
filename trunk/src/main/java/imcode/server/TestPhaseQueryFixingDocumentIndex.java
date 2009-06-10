package imcode.server;

import imcode.server.document.LifeCyclePhase;
import imcode.server.document.index.DocumentIndex;
import junit.framework.TestCase;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

public class TestPhaseQueryFixingDocumentIndex extends TestCase {

    PhaseQueryFixingDocumentIndex index = new PhaseQueryFixingDocumentIndex(null);

    public void testFixQuery() throws Exception {
        for ( LifeCyclePhase lifeCyclePhase : LifeCyclePhase.ALL ) {
            assertNotNull(index.fixQuery(new TermQuery(new Term(DocumentIndex.FIELD__PHASE, lifeCyclePhase.toString()))));
        }
    }

}