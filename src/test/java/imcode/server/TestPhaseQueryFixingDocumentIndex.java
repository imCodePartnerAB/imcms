package imcode.server;

import junit.framework.*;
import imcode.server.PhaseQueryFixingDocumentIndex;
import imcode.server.document.LifeCyclePhase;
import imcode.server.document.index.DocumentIndex;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;

public class TestPhaseQueryFixingDocumentIndex extends TestCase {

    PhaseQueryFixingDocumentIndex index = new PhaseQueryFixingDocumentIndex(null);

    public void testFixQuery() throws Exception {
        for ( LifeCyclePhase lifeCyclePhase : LifeCyclePhase.ALL ) {
            assertNotNull(index.fixQuery(new TermQuery(new Term(DocumentIndex.FIELD__PHASE, lifeCyclePhase.toString()))));
        }
    }

}