package imcode.server.document.index

import com.imcode.{when => _, _}
import scala.collection.JavaConverters._
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.user.RoleId
import com.imcode.imcms.api.I18nLanguage
import org.scalatest.mock.MockitoSugar._
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import scala.collection.mutable.{Map => MMap}
import imcode.server.document.{DocumentDomainObject, CategoryDomainObject, DocumentPermissionSetTypeDomainObject}
import org.mockito.Matchers._
import org.mockito.Mockito.{mock => _, _}
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import com.imcode.imcms.mapping.DocumentMapper.TextDocumentMenuIndexPair
import imcode.server.document.index.solr.{DocumentContentIndexer, DocumentIndexer}
import imcode.server.ImcmsServices
import com.imcode.imcms.test._
import com.imcode.imcms.test.fixtures.{DocFX, LanguageFX}
import java.util.LinkedList


class DocIndexingMocksSetup {
  // refactor out
  type DocId = Int

  case class ParentDoc(docId: Int, menuNo: Int)

  private val documentMapperMock = mock[DocumentMapper]
  private val categoryMapperMock = mock[CategoryMapper]
  private val servicesMock = mock[ImcmsServices]

  private val docs = MMap.empty[DocId, MMap[I18nLanguage, DocumentDomainObject]].withDefaultValue(MMap.empty.withDefaultValue(null))
  private val categories = MMap.empty[DocId, CategoryDomainObject]
  private val parentDocs = MMap.empty[DocId, Seq[ParentDoc]].withDefaultValue(Seq.empty)

  when(documentMapperMock.getImcmsServices).thenReturn(servicesMock)

  when(servicesMock.getI18nSupport).thenReturn(LanguageFX.mkI18nSupport())

  when(categoryMapperMock.getCategories(anyCollectionOf(classOf[JInteger]))).thenAnswer { args: Array[AnyRef] =>
    val availableCategories = for {
      categoryId <- args(0).asInstanceOf[JCollection[JInteger]].asScala
      category <- categories.get(categoryId)
    } yield category

    availableCategories.toSet.asJava
  }


  when(documentMapperMock.getDefaultDocument(anyInt, any[I18nLanguage])).thenAnswer { args: Array[AnyRef] =>
    args match {
      case Array(id: JInteger, language: I18nLanguage) => docs(id)(language)
    }
  }


  when(documentMapperMock.getAllDocumentIds).thenAnswer { () =>
    docs.keys.map(Int.box).toList.asJava
  }

  when(documentMapperMock.getParentDocumentAndMenuIdsForDocument(any[DocumentDomainObject])).thenAnswer {
    args: Array[AnyRef] =>
      args match {
        case Array(doc: DocumentDomainObject) =>
          parentDocs(doc.getId).map {
            case ParentDoc(docId, menuNo) => Array(Int box docId, Int box menuNo)
          } |> { _.asJavaCollection } |> { coll => new java.util.LinkedList(coll) }
      }
  }


  val docIndexer = new DocumentIndexer |>> { di =>
    di.documentMapper = documentMapperMock
    di.categoryMapper = categoryMapperMock
    di.contentIndexer = new DocumentContentIndexer
  }

  // DocumentIndexer uses category id, name and type id, name as string index fields
  def addCategories(categories: CategoryDomainObject*) = this |>> { _ =>
    for (category <- categories) this.categories(category.getId) = category
  }

  // DocumentIndexer uses parent doc id and menu id as index fields
  def addParentDocumentsFor(docId: Int, pds: ParentDoc*) = this |>> { _ =>
    parentDocs(docId) = pds
  }

  def addDocument(doc: DocumentDomainObject) = this |>> { _ =>
    docs.getOrElseUpdate(doc.getId,  MMap.empty[I18nLanguage, DocumentDomainObject].withDefaultValue(null)) |> { langToDoc =>
      langToDoc.put(doc.getLanguage, doc)
    }
  }

  def addDocuments(docs: Seq[DocumentDomainObject]) = this |>> { _ =>
    docs foreach addDocument
  }

  // getDocumentMenuPairsContainingDocument
}