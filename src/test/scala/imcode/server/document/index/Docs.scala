package imcode.server.document.index

import com.imcode._
import com.imcode.imcms.mapping.orm.DocLanguage
import scala.collection.JavaConverters._
import org.scalatest.mock.MockitoSugar._
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import scala.collection.mutable.{Map => MMap}
import imcode.server.document.{DocumentDomainObject, CategoryDomainObject}
import org.mockito.Matchers._
import org.mockito.Mockito
import imcode.server.ImcmsServices
import com.imcode.imcms.test._
import com.imcode.imcms.test.fixtures.LanguageFX
import imcode.server.document.index.service.impl.{DocumentIndexer, DocumentContentIndexer}


class DocIndexingMocksSetup {
  // refactor out
  type DocId = Int
  type LanguageCode = String

  case class ParentDoc(docId: Int, menuNo: Int)

  private val documentMapperMock = mock[DocumentMapper]
  private val categoryMapperMock = mock[CategoryMapper]
  private val servicesMock = mock[ImcmsServices]

  private val docs = MMap.empty[DocId, MMap[LanguageCode, DocumentDomainObject]].withDefaultValue(MMap.empty.withDefaultValue(null))
  private val categories = MMap.empty[DocId, CategoryDomainObject]
  private val parentDocs = MMap.empty[DocId, Seq[ParentDoc]].withDefaultValue(Seq.empty)

  Mockito.when(documentMapperMock.getImcmsServices).thenReturn(servicesMock)

  Mockito.when(servicesMock.getDocumentI18nSupport).thenReturn(LanguageFX.mkI18nSupport())

  Mockito.when(categoryMapperMock.getCategories(anyCollectionOf(classOf[JInteger]))).thenAnswer { args: Array[AnyRef] =>
    val availableCategories = for {
      categoryId <- args(0).asInstanceOf[JCollection[JInteger]].asScala
      category <- categories.get(categoryId)
    } yield category

    availableCategories.toSet.asJava
  }


  Mockito.when(documentMapperMock.getDefaultDocument(anyInt, any[DocLanguage])).thenAnswer { args: Array[AnyRef] =>
    args match {
      case Array(id: JInteger, language: DocLanguage) => docs(id)(language.getCode)
    }
  }

  Mockito.when(documentMapperMock.getDefaultDocument(anyInt, any[LanguageCode])).thenAnswer { args: Array[AnyRef] =>
    args match {
      case Array(id: JInteger, languageCode: String) => docs(id)(languageCode)
    }
  }


  Mockito.when(documentMapperMock.getAllDocumentIds).thenAnswer { () =>
    docs.keys.map(Int.box).toList.asJava
  }

  Mockito.when(documentMapperMock.getParentDocumentAndMenuIdsForDocument(any[DocumentDomainObject])).thenAnswer {
    args: Array[AnyRef] =>
      args match {
        case Array(doc: DocumentDomainObject) =>
          parentDocs(doc.getId).map {
            case ParentDoc(docId, menuNo) => Array(docId : JInteger, menuNo : JInteger)
          } |> { _.asJavaCollection } |> { coll => new java.util.LinkedList(coll) }
      }
  }


  val docIndexer = new DocumentIndexer |>> { di =>
    di.documentMapper = documentMapperMock
    di.categoryMapper = categoryMapperMock
    di.contentIndexer = new DocumentContentIndexer(_ => true)
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
    docs.getOrElseUpdate(doc.getId,  MMap.empty[LanguageCode, DocumentDomainObject].withDefaultValue(null)) |> { langToDoc =>
      langToDoc.put(doc.getLanguage.getCode, doc)
    }
  }

  def addDocuments(docs: Seq[DocumentDomainObject]) = this |>> { _ =>
    docs.foreach(addDocument)
  }

  // getDocumentMenuPairsContainingDocument
}