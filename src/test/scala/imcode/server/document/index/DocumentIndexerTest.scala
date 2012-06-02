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
import org.apache.solr.common.SolrInputDocument
import imcode.server.user.RoleId
import imcode.server.document.{DocumentPermissionSetTypeDomainObject, CategoryTypeDomainObject, CategoryDomainObject, DocumentDomainObject}

@RunWith(classOf[JUnitRunner])
class DocumentIndexerTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.nop()

  // text doc to index
  val textDoc = new TextDocumentDomainObject |>> { doc =>
    doc.setId(1001)
    doc.setCreatorId(100)
    doc.setPublisherId(200)
    doc.setCategoryIds(0.to(10).toSet.asJava)
    doc.setLanguage(LanguagesFX.english)
    doc.setKeywords(Set("kw_abc", "kw_def", "kw_xyz", "kw_one kw_two kw_three").asJava)
    doc.setAlias("main")

    //how to index?
    //doc.setProperties(Map("prop_one"), ...)

    // only roles are indexed, permission sets are ignored
    doc.getMeta.getRoleIdToDocumentPermissionSetTypeMappings |> { m =>
      m.setPermissionSetTypeForRole(RoleId.USERS, DocumentPermissionSetTypeDomainObject.FULL)
      m.setPermissionSetTypeForRole(RoleId.USERADMIN, DocumentPermissionSetTypeDomainObject.FULL)
      m.setPermissionSetTypeForRole(RoleId.SUPERADMIN, DocumentPermissionSetTypeDomainObject.FULL)
    }

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
      fx.ParentDoc(1, 0),
      fx.ParentDoc(1, 1),
      fx.ParentDoc(2, 0),
      fx.ParentDoc(2, 1),
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

      assertEquals("FIELD__META_ID", textDoc.getId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__META_ID))
      assertEquals("FIELD__META_ID_LEXICOGRAPHIC", textDoc.getId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__META_ID_LEXICOGRAPHIC))

      assertEquals("FIELD__ROLE_ID",
        Set(RoleId.USERS, RoleId.USERADMIN, RoleId.SUPERADMIN).map(_.toString),
        indexDoc.getFieldValues(DocumentIndex.FIELD__ROLE_ID).asScala.map(_.toString).toSet
      )

      assertEquals("FIELD__META_HEADLINE", textDoc.getHeadline, indexDoc.getFieldValue(DocumentIndex.FIELD__META_HEADLINE))
      assertEquals("FIELD__META_HEADLINE_KEYWORD", textDoc.getHeadline, indexDoc.getFieldValue(DocumentIndex.FIELD__META_HEADLINE_KEYWORD))
      assertEquals("FIELD__META_TEXT", textDoc.getMenuText, indexDoc.getFieldValue(DocumentIndex.FIELD__META_TEXT))

      assertEquals("FIELD__DOC_TYPE_ID", textDoc.getDocumentTypeId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__DOC_TYPE_ID))

      assertEquals("FIELD__DOC_TYPE_ID", textDoc.getCreatorId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__CREATOR_ID))
      assertEquals("FIELD__PUBLISHER_ID", textDoc.getPublisherId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLISHER_ID))

      assertEquals("FIELD__CREATED_DATETIME", textDoc.getCreatedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__CREATED_DATETIME))
      assertEquals("FIELD__MODIFIED_DATETIME", textDoc.getModifiedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__MODIFIED_DATETIME))
      assertEquals("FIELD__ACTIVATED_DATETIME", textDoc.getPublicationStartDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__ACTIVATED_DATETIME))
      assertEquals("FIELD__PUBLICATION_START_DATETIME", textDoc.getPublicationStartDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLICATION_START_DATETIME))
      assertEquals("FIELD__PUBLICATION_END_DATETIME", textDoc.getPublicationEndDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLICATION_END_DATETIME))
      assertEquals("FIELD__ARCHIVED_DATETIME", textDoc.getArchivedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__ARCHIVED_DATETIME))

      assertEquals("FIELD__STATUS", textDoc.getPublicationStatus.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__STATUS))

      // ???
      // ??? properties; AND .properties
      // ???

      assertEquals("FIELD__KEYWORD",
        Set("kw_abc", "kw_def", "kw_xyz", "kw_one kw_two kw_three"),
        indexDoc.getFieldValues(DocumentIndex.FIELD__KEYWORD).asScala.map(_.toString).toSet
      )

      assertEquals("FIELD__PARENT_ID",
        Set("0", "1", "2"),
        indexDoc.getFieldValues(DocumentIndex.FIELD__PARENT_ID).asScala.map(_.toString).toSet
      )

      assertEquals("FIELD__PARENT_MENU_ID",
        Set("0_0", "1_0", "1_1", "2_0", "2_1", "2_2"),
        indexDoc.getFieldValues(DocumentIndex.FIELD__PARENT_MENU_ID).asScala.map(_.toString).toSet
      )


      assertEquals("FIELD__HAS_PARENTS", true.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__HAS_PARENTS))
      assertEquals("FIELD__ALIAS", textDoc.getAlias, indexDoc.getFieldValue(DocumentIndex.FIELD__ALIAS))

      // ???
      // properties wirt prefix
      // ???

      assertEquals("FIELD__CATEGORY_ID",
        Set("1", "2", "3", "4", "5"),
        indexedCategoriesIds
      )

      assertEquals("FIELD__CATEGORY",
        Set("category-one", "category-two", "category-three", "category-four", "category-five"),
        indexedCategoriesNames
      )

      assertEquals("FIELD__CATEGORY_TYPE_ID",
        Set("1", "2", "3", "4", "5"),
        indexedCategoriesTypesIds
      )

      assertEquals("FIELD__CATEGORY_TYPE",
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