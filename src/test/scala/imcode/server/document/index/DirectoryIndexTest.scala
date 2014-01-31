package imcode.server.document.index

import com.imcode.imcms.mapping.orm.{DocRef, DocumentVersion, I18nMeta}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfterAll, WordSpec}
import com.imcode.imcms.test.TestSetup
import org.scalatest.mock.MockitoSugar.mock
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import com.imcode.imcms.dao.TextDocDao
import org.mockito.Mockito
import org.mockito.Matchers._
import com.imcode._
import org.mockito.stubbing.Answer
import imcode.server.document.CategoryDomainObject
import org.mockito.invocation.InvocationOnMock
import scala.collection.JavaConverters._
import java.io.File
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.core.CoreContainer
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.textdocument.{ImageDomainObject, TextDomainObject, TextDocumentDomainObject}
import imcode.server.document.index.service.impl.{DocumentIndexer, DocumentContentIndexer}

@RunWith(classOf[JUnitRunner])
class DirectoryIndexTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfterEach {

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

//  "create composite DocumentIndex" ignore {
//    val solrServer = Test.solr.createEmbeddedServer(recreateHome = true)
//    val databaseMock = mock[Database]
//    val docMapper: DocumentMapper = null
//    val imcmsServices: ImcmsServices = null
//
////    val docIndex = new LoggingDocumentIndex(databaseMock,
////        new PhaseQueryFixingDocumentIndex(
////            new RebuildingDirectoryIndex(solrServer, docMapper,
////                        1.0f, //getConfig().getIndexingSchedulePeriodInMinutes(),
////                        new SolrIndexDocumentFactory(imcmsServices))))
//    solrServer.shutdown()
//  }

  "create embedded solr server" in {
    val coreContainer = new CoreContainer(TestSetup.solr.home) |>> { _.load() }
//    val coreContainer = new CoreContainer(Test.solr.home.getAbsolutePath)
//    val coreDescriptor = new CoreDescriptor(coreContainer, "core", new File(Test.solr.home, "core").getAbsolutePath)
//    //coreDescriptor.setDataDir("/Users/ajosua/test/imSolr")
//    val core = coreContainer.create(coreDescriptor)
//    coreContainer.register(core, false)

    println("SORL CONF> " + coreContainer.getCoreNames)
    println("SORL HOME> " + coreContainer.getSolrHome)

    //new EmbeddedSolrServer(coreContainer, "imcms") with SolrServerShutdown
    val srv = new EmbeddedSolrServer(coreContainer, "core")
    val srv2 = new EmbeddedSolrServer(coreContainer, "core")
    //srv.ping()
    while (true) try  {
      println("QUERY>> " + srv.query(new SolrQuery("*:*")))
//      srv.commit()
      srv2.deleteByQuery("*:*")
      println("QUERY>> " + srv.query(new SolrQuery("*:*")))
      srv2.commit()
      println("-------------------")
    } catch {
      case e: Exception => println("ERRR> " + e.getMessage); e.printStackTrace()
    }
    //srv.deleteById("*:*")
    srv.shutdown()

  }

}


/**
 * REMOVE ???
 */
class DirectoryIndexFixture {

  // Mockito.when(...getDocumentMenuPairsContainingDocument).thenReturn(...)

  private val documentMapperMock = mock[DocumentMapper]
  private val categoryMapperMock = mock[CategoryMapper]
  private val textDocDaoMock = mock[TextDocDao]

  Mockito.when(categoryMapperMock.getCategories(anyCollectionOf(classOf[JInteger]))).thenAnswer(new Answer[JSet[CategoryDomainObject]]() {
     def answer(invocation: InvocationOnMock): JSet[CategoryDomainObject] = {
       val categoriesIds = invocation.getArguments()(0).asInstanceOf[JCollection[JInteger]].asScala

       Set.empty[CategoryDomainObject].asJava
     }
  })

  val docIndexer = new DocumentIndexer |>> { di =>
    di.documentMapper = documentMapperMock
    di.categoryMapper = categoryMapperMock
    di.contentIndexer = new DocumentContentIndexer(_ => true)
  }


  def addTextDocMock(doc: TextDocumentDomainObject,
                     i18nMetas: Option[Seq[I18nMeta]] = None,
                     texts: Option[Seq[TextDomainObject]] = None,
                     images: Option[Seq[ImageDomainObject]] = None) {

    val docId = doc.getMetaId ensuring (_ != null, "document id must be set")

    Mockito.when(documentMapperMock.getDefaultDocument[TextDocumentDomainObject](docId)).thenReturn(doc)
    Mockito.when(documentMapperMock.getI18nMetas(docId)).thenReturn(
      i18nMetas.getOrElse(Seq(doc.getI18nMeta)).asJava
    )

    Mockito.when(textDocDaoMock.getTexts(DocRef.of(docId, DocumentVersion.WORKING_VERSION_NO))).thenReturn(
      texts.getOrElse(Seq(doc.getTexts.values.asScala, doc.getLoopTexts.values.asScala).flatten).asJava
    )

    Mockito.when(textDocDaoMock.getImages(DocRef.of(docId, DocumentVersion.WORKING_VERSION_NO))).thenReturn(
      images.getOrElse(Seq(doc.getImages.values.asScala, doc.getLoopImages.values.asScala).flatten).asJava
    )
  }
}