package imcode.server.document.index

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import com.imcode.imcms.test.Test
import com.imcode.db.Database
import org.scalatest.mock.MockitoSugar.mock
import imcode.server.{ImcmsServices, LoggingDocumentIndex, PhaseQueryFixingDocumentIndex}
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import com.imcode.imcms.dao.{TextDao, ImageDao}
import org.mockito.Mockito._
import org.mockito.Matchers._
import com.imcode.{when => _, _}
import org.mockito.stubbing.Answer
import imcode.server.document.CategoryDomainObject
import org.mockito.invocation.InvocationOnMock
import imcode.server.document.textdocument.{ImageDomainObject, TextDomainObject, TextDocumentDomainObject}
import com.imcode.imcms.api.{DocumentVersion, I18nMeta}
import scala.collection.JavaConverters._
import java.io.File
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.core.{SolrCore, CoreDescriptor, CoreContainer}
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.index.solr.{DocumentContentIndexer, DocumentIndexer}

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
    val coreContainer = new CoreContainer(Test.solr.home.getAbsolutePath, new File(Test.solr.home, "solr.xml"))
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
      case e => println("ERRR> " + e.getMessage); e.printStackTrace()
    }
    //srv.deleteById("*:*")
    srv.shutdown()

  }

}


/**
 * REMOVE ???
 */
class DirectoryIndexFixture {

  // when(...getDocumentMenuPairsContainingDocument).thenReturn(...)

  private val documentMapperMock = mock[DocumentMapper]
  private val categoryMapperMock = mock[CategoryMapper]
  private val imageDaoMock = mock[ImageDao]
  private val textDaoMock = mock[TextDao]

  when(categoryMapperMock.getCategories(anyCollectionOf(classOf[JInteger]))).thenAnswer(new Answer[JSet[CategoryDomainObject]]() {
     def answer(invocation: InvocationOnMock): JSet[CategoryDomainObject] = {
       val categoriesIds = invocation.getArguments()(0).asInstanceOf[JCollection[JInteger]].asScala

       Set.empty[CategoryDomainObject].asJava
     }
  })

  val docIndexer = new DocumentIndexer |>> { di =>
    di.documentMapper = documentMapperMock
    di.categoryMapper = categoryMapperMock
    di.contentIndexer = new DocumentContentIndexer
  }


  def addTextDocMock(doc: TextDocumentDomainObject,
                     i18nMetas: Option[Seq[I18nMeta]] = None,
                     texts: Option[Seq[TextDomainObject]] = None,
                     images: Option[Seq[ImageDomainObject]] = None) {

    val docId = doc.getIdValue ensuring (_ != null, "document id must be set")

    when(documentMapperMock.getDefaultDocument(docId)).thenReturn(doc)
    when(documentMapperMock.getI18nMetas(docId)).thenReturn(
      i18nMetas.getOrElse(Seq(doc.getI18nMeta)).asJava
    )

    when(textDaoMock.getTexts(docId, DocumentVersion.WORKING_VERSION_NO)).thenReturn(
      texts.getOrElse(Seq(doc.getTexts.values.asScala, doc.getLoopTexts.values.asScala).flatten).asJava
    )

    when(imageDaoMock.getImages(docId, DocumentVersion.WORKING_VERSION_NO)).thenReturn(
      images.getOrElse(Seq(doc.getImages.values.asScala, doc.getLoopImages.values.asScala).flatten).asJava
    )
  }
}