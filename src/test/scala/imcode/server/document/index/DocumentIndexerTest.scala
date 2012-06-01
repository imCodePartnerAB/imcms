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
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import com.imcode.imcms.test.fixtures.LanguagesFX
import com.imcode.imcms.mapping.DocumentMapper.TextDocumentMenuIndexPair
import scala.collection.mutable.{Map => MMap}
import imcode.server.document.textdocument.{TextDocumentDomainObject, ImageDomainObject, TextDomainObject}
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject, DocumentDomainObject}
import org.apache.solr.common.SolrInputDocument

@RunWith(classOf[JUnitRunner])
class DocumentIndexerTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.nop()

  // text doc to index
  val textDoc = new TextDocumentDomainObject |>> { doc =>
    doc.setId(1001)
    doc.setCreatorId(0)
    doc.setCategoryIds(0.to(10).toSet.asJava)
    doc.setLanguage(LanguagesFX.english)

    doc.getI18nMeta |> { m =>
      m.setHeadline("I18nMetaHeadlineEn")
      m.setMenuText("I18nMetaMenuTextEn")
    }
  }

  val docIndexer: DocumentIndexer = new DocumentIndexerFixture |>> { fx =>
    fx.addCategories(
      new CategoryDomainObject |>> { c =>
        c.setId(1)
        c.setName("category-one")
        c.setType(new CategoryTypeDomainObject(1, "category-type-one", 0, false))
      },
      new CategoryDomainObject |>> { c =>
        c.setId(2)
        c.setName("category-two")
        c.setType(new CategoryTypeDomainObject(2, "category-type-two", 0, false))
      },
      new CategoryDomainObject |>> { c =>
        c.setId(3)
        c.setName("category-three")
        c.setType(new CategoryTypeDomainObject(3, "category-type-three", 0, false))
      },
      new CategoryDomainObject |>> { c =>
        c.setId(4)
        c.setName("category-four")
        c.setType(new CategoryTypeDomainObject(4, "category-type-four", 0, false))
      },
      new CategoryDomainObject |>> { c =>
        c.setId(5)
        c.setName("category-five")
        c.setType(new CategoryTypeDomainObject(5, "category-type-five", 0, false))
      }
    )

    fx.addParentDocumentsFor(textDoc,
      fx.ParentDoc(0, 0),
      fx.ParentDoc(1, 2),
      fx.ParentDoc(2, 2)
    )
  } |> { _.docIndexer }

  "SolrIndexDocumentFactory" should {
    "create SolrInputDocument from TextDocumentDomainObject" in {
      val indexDoc: SolrInputDocument = docIndexer.index(textDoc)

      val indexedCategoriesIds = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_ID).asScala.map(_.toString).toSet
      val indexedCategoriesNames = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY).asScala.map(_.toString).toSet
      val indexedCategoriesTypesIds = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_TYPE_ID).asScala.map(_.toString).toSet
      val indexedCategoriesTypesNames = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_TYPE).asScala.map(_.toString).toSet

      assertEquals("Indexed Categories Ids",
        Set("1", "2", "3", "4", "5"),
        indexedCategoriesIds
      )

      assertEquals("Indexed Categories Names",
        Set("category-one", "category-two", "category-three", "category-four", "category-five"),
        indexedCategoriesNames
      )

      assertEquals("Indexed Categories Types Ids",
        Set("1", "2", "3", "4", "5"),
        indexedCategoriesTypesIds
      )

      assertEquals("Indexed Categories Types Names",
        Set("category-type-one", "category-type-two", "category-type-three", "category-type-four", "category-type-five"),
        indexedCategoriesTypesNames
      )
    }

    "create SolrInputDocument from FileDocumentDomainObject" in {
      pending
    }
  }
}

/**
 * Used to create and configure DocumentIndexer
 */
class DocumentIndexerFixture {

  case class ParentDoc(docId: Int, menuNo: Int)

  private val documentMapperMock = mock[DocumentMapper]
  private val categoryMapperMock = mock[CategoryMapper]
  private val categories = MMap.empty[Int, CategoryDomainObject]

  when(categoryMapperMock.getCategories(anyCollectionOf(classOf[JInteger]))).thenAnswer(new Answer[JSet[CategoryDomainObject]]() {
     def answer(invocation: InvocationOnMock): JSet[CategoryDomainObject] = {
       val availableCategories = for {
         categoryId <- invocation.getArguments()(0).asInstanceOf[JCollection[JInteger]].asScala
         category <- categories.get(categoryId)
       } yield category

       availableCategories.toSet.asJava
     }
  })

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
  def addParentDocumentsFor(doc: DocumentDomainObject, parentDocs: ParentDoc*) = this |>> { _ =>
    when(documentMapperMock.getDocumentMenuPairsContainingDocument(doc)).thenReturn(parentDocs.toArray.map {
      case ParentDoc(docId, menuNo) => new TextDocumentMenuIndexPair(new TextDocumentDomainObject(docId), menuNo)
    })
  }

  // getDocumentMenuPairsContainingDocument
}