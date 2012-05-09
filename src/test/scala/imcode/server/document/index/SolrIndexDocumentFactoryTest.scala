package imcode.server.document.index

import com.imcode.{when => _, _}
import junit.framework.Assert._
import java.util.Date
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import org.scalatest.mock.MockitoSugar.mock
import org.mockito.Mockito._
import org.mockito.Matchers.{anyInt, anyCollectionOf, eq}
import imcode.server.document.textdocument.TextDocumentDomainObject
import scala.collection.JavaConverters._
import com.imcode.imcms.dao.MetaDao
import imcode.server.ImcmsServices
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import com.imcode.imcms.api.{I18nMeta, DocumentVersion, DocumentVersionInfo}
import imcode.server.document.CategoryDomainObject

@RunWith(classOf[JUnitRunner])
class SolrIndexDocumentFactoryTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  val imcmsServicesMock = mock[ImcmsServices]
  val metaDaoMock = mock[MetaDao]
  val documentMapperMock = mock[DocumentMapper]
  val categoryMapperMock = mock[CategoryMapper]
  val docVersion = new DocumentVersion |< { v =>
    v.setNo(DocumentVersion.WORKING_VERSION_NO)
  }
  val i18nMetas: JList[I18nMeta] = {
    val metaEn = new I18nMeta |< { m =>
      m.setHeadline("I18nMetaHeadlineEn")
      m.setMenuText("I18nMetaMenuTextEn")
    }

    val metaSv = new I18nMeta |< { m =>
      m.setHeadline("I18nMetaHeadlineSv")
      m.setMenuText("I18nMetaMenuTextSv")
    }

    List(metaEn, metaSv).asJava
  }

  when(metaDaoMock.getI18nMetas(1001)).thenReturn(i18nMetas)

  when(categoryMapperMock.getCategories(anyCollectionOf(classOf[JInteger]))).thenReturn(Set.empty[CategoryDomainObject].asJava)

  when(imcmsServicesMock.getDocumentMapper).thenReturn(documentMapperMock)
  when(imcmsServicesMock.getCategoryMapper).thenReturn(categoryMapperMock)
  when(imcmsServicesMock.getComponent(classOf[MetaDao])).thenReturn(metaDaoMock)


  val docVersionInfo = new DocumentVersionInfo(1001, List(docVersion).asJava, docVersion, docVersion)

  val textDoc = new TextDocumentDomainObject |< { doc =>
    doc.setCreatorId(0)
    doc.setVersion(docVersion)
  }

  when(documentMapperMock.getDocumentVersionInfo(1001)).thenReturn(docVersionInfo)
  when(documentMapperMock.getCustomDocument(1001, 0)).thenReturn(textDoc)


  val solrIndexDocumentFactory = new SolrIndexDocumentFactory(imcmsServicesMock)

  "SolrIndexDocumentFactory" should {
    "create SolrInputDocument from TextDocumentDomainObject" in {
      solrIndexDocumentFactory.createIndexDocument(1001)
    }
  }
}