package imcode.server.document.index

import com.imcode.{when => _, _}
import junit.framework.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import org.scalatest.mock.MockitoSugar.mock
import org.mockito.Mockito._
import org.mockito.Matchers.{anyInt, anyCollectionOf, eq}
import scala.collection.JavaConverters._
import imcode.server.ImcmsServices
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import com.imcode.imcms.api.{I18nMeta, DocumentVersion, DocumentVersionInfo}
import com.imcode.imcms.test.Test
import com.imcode.imcms.dao.{TextDao, ImageDao, MetaDao}
import java.util.{Collections, Date}
import imcode.server.document.textdocument.{ImageDomainObject, TextDomainObject, TextDocumentDomainObject}
import imcode.server.document.{DocumentDomainObject, CategoryDomainObject}
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

@RunWith(classOf[JUnitRunner])
class DocumentIndexerTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.nop()

  val docIndexer: DocumentIndexer = new DocumentIndexerFixture |>> { fx =>
    val textDoc = new TextDocumentDomainObject |>> { doc =>
      doc.setId(1001)
      doc.setCreatorId(0)
      doc.setCategoryIds(1.to(10).toSet.asJava)
    }

    val i18nMetas: Seq[I18nMeta] = {
      val metaEn = new I18nMeta |>> { m =>
        m.setHeadline("I18nMetaHeadlineEn")
        m.setMenuText("I18nMetaMenuTextEn")
      }

      val metaSv = new I18nMeta |>> { m =>
        m.setHeadline("I18nMetaHeadlineSv")
        m.setMenuText("I18nMetaMenuTextSv")
      }

      Seq(metaEn, metaSv)
    }

    fx.addTextDocMock(textDoc, Some(i18nMetas))
  } |> { _.docIndexer }


  "SolrIndexDocumentFactory" should {
    "create SolrInputDocument from TextDocumentDomainObject" in {
      docIndexer.index(null)
    }

    "create SolrInputDocument from FileDocumentDomainObject" in {
      pending
    }
  }
}

/**
 *
 */
class DocumentIndexerFixture {

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