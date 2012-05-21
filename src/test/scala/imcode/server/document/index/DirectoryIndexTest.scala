package imcode.server.document.index

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import com.imcode.imcms.test.Test
import com.imcode.imcms.mapping.DocumentMapper
import com.imcode.db.Database
import org.scalatest.mock.MockitoSugar.mock
import imcode.server.{ImcmsServices, LoggingDocumentIndex, PhaseQueryFixingDocumentIndex}

@RunWith(classOf[JUnitRunner])
class DirectoryIndexTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

//        IndexService indexService = new IndexService(getConfig());
//        SolrServer solrServer = indexService.solrServer();
//
//        documentMapper = new DocumentMapper(this, this.getDatabase());
//        documentMapper.setDocumentIndex(new LoggingDocumentIndex(database,
//                new PhaseQueryFixingDocumentIndex(
//                    new RebuildingDirectoryIndex(solrServer, documentMapper,
//                        getConfig().getIndexingSchedulePeriodInMinutes(),
//                        new SolrIndexDocumentFactory(this))))) ;

  override def beforeAll() {
//    Test.db.recreate()
//    Test.imcms.init(start = true, prepareDbOnStart = true)
  }

  "create composite DocumentIndex" in {
    val solrServer = Test.solr.createEmbeddedServer()
    val databaseMock = mock[Database]
    val docMapper: DocumentMapper = null
    val imcmsServices: ImcmsServices = null

    val docIndex = new LoggingDocumentIndex(databaseMock,
        new PhaseQueryFixingDocumentIndex(
            new RebuildingDirectoryIndex(solrServer, docMapper,
                        1.0f, //getConfig().getIndexingSchedulePeriodInMinutes(),
                        new SolrIndexDocumentFactory(imcmsServices))))
  }

}