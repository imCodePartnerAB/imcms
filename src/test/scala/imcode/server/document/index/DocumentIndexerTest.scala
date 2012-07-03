package imcode.server.document.index

import com.imcode.{when => _, _}
import junit.framework.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import org.scalatest.mock.MockitoSugar.mock
import org.mockito.Mockito._
import org.mockito.Matchers.{anyInt, anyCollectionOf, eq, anyObject, any}
import scala.collection.JavaConverters._
import imcode.server.ImcmsServices
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import com.imcode.imcms.test.Test
import com.imcode.imcms.dao.{TextDao, ImageDao, MetaDao}
import java.util.{Collections, Date}
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import com.imcode.imcms.test.fixtures.LanguageFX
import com.imcode.imcms.mapping.DocumentMapper.TextDocumentMenuIndexPair
import scala.collection.mutable.{Map => MMap}
import org.apache.solr.common.SolrInputDocument
import imcode.server.user.RoleId
import imcode.server.document._
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile
import imcode.util.io.FileInputStreamSource
import imcode.server.document.textdocument.{MenuDomainObject, TextDocumentDomainObject, ImageDomainObject, TextDomainObject}
import imcode.server.document.index.solr.{DocumentContentIndexer, DocumentIndexer}
import com.imcode.imcms.api.{I18nLanguage, I18nMeta, DocumentVersion, DocumentVersionInfo}
import imcode.server.document.index.Docs.textDocEn

@RunWith(classOf[JUnitRunner])
class DocumentIndexerTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.init()

  val docIndexer: DocumentIndexer = new DocIndexingMocksSetup |>> { fx =>
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

    fx.addParentDocumentsFor(textDocEn,
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
      val indexDoc: SolrInputDocument = docIndexer.index(textDocEn)

      val indexedCategoriesIds = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_ID).asScala.map(_.toString).toSet
      val indexedCategoriesNames = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY).asScala.map(_.toString).toSet
      val indexedCategoriesTypesIds = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_TYPE_ID).asScala.map(_.toString).toSet
      val indexedCategoriesTypesNames = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_TYPE).asScala.map(_.toString).toSet

      assertEquals("FIELD__META_ID", textDocEn.getId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__META_ID))
      assertEquals("FIELD__META_ID_LEXICOGRAPHIC", textDocEn.getId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__META_ID_LEXICOGRAPHIC))

      assertEquals("FIELD__ROLE_ID",
        Set(RoleId.USERS, RoleId.USERADMIN, RoleId.SUPERADMIN).map(_.toString),
        indexDoc.getFieldValues(DocumentIndex.FIELD__ROLE_ID).asScala.map(_.toString).toSet
      )

      assertEquals("FIELD__META_HEADLINE", textDocEn.getHeadline, indexDoc.getFieldValue(DocumentIndex.FIELD__META_HEADLINE))
      assertEquals("FIELD__META_HEADLINE_KEYWORD", textDocEn.getHeadline, indexDoc.getFieldValue(DocumentIndex.FIELD__META_HEADLINE_KEYWORD))
      assertEquals("FIELD__META_TEXT", textDocEn.getMenuText, indexDoc.getFieldValue(DocumentIndex.FIELD__META_TEXT))

      assertEquals("FIELD__DOC_TYPE_ID", textDocEn.getDocumentTypeId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__DOC_TYPE_ID))

      assertEquals("FIELD__DOC_TYPE_ID", textDocEn.getCreatorId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__CREATOR_ID))
      assertEquals("FIELD__PUBLISHER_ID", textDocEn.getPublisherId.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLISHER_ID))

      assertEquals("FIELD__CREATED_DATETIME", textDocEn.getCreatedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__CREATED_DATETIME))
      assertEquals("FIELD__MODIFIED_DATETIME", textDocEn.getModifiedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__MODIFIED_DATETIME))
      assertEquals("FIELD__ACTIVATED_DATETIME", textDocEn.getPublicationStartDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__ACTIVATED_DATETIME))
      assertEquals("FIELD__PUBLICATION_START_DATETIME", textDocEn.getPublicationStartDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLICATION_START_DATETIME))
      assertEquals("FIELD__PUBLICATION_END_DATETIME", textDocEn.getPublicationEndDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLICATION_END_DATETIME))
      assertEquals("FIELD__ARCHIVED_DATETIME", textDocEn.getArchivedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__ARCHIVED_DATETIME))

      assertEquals("FIELD__STATUS", textDocEn.getPublicationStatus.toString, indexDoc.getFieldValue(DocumentIndex.FIELD__STATUS))

      assertEquals("FIELD__KEYWORD",
        Set("kw_1", "kw_2", "kw_3", "kw_compound kw_keyword kw_sentence"),
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
      assertEquals("FIELD__ALIAS", textDocEn.getAlias, indexDoc.getFieldValue(DocumentIndex.FIELD__ALIAS))

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

      def propertyValue(name: String) = indexDoc.getFieldValue(DocumentIndex.FIELD__PROPERTY_PREFIX + name)

      assertEquals("FIELD__PROPERTY_PREFIX",
        Set("property_1", "property_2", "property_3"),
        Set(propertyValue("p1"), propertyValue("p2"), propertyValue("p3"))
      )

      // ???
      // properties wirt prefix
      // ???

      // content
      assertEquals("FIELD__TEMPLATE", "template_main", indexDoc.getFieldValue(DocumentIndex.FIELD__TEMPLATE))

    }

    "create SolrInputDocument from FileDocumentDomainObject" in {
      for (file <- Test.dir("src/test/resources/test-file-doc-files").listFiles) {
        val fdf = new FileDocumentFile |>> { fdf =>
          fdf.setInputStreamSource(new FileInputStreamSource(file))
          fdf.setMimeType("");
        }

        new FileDocumentDomainObject |>> { d =>
          d.setCreatorId(0)
          d.addFile(file.getName, fdf)
        } |> docIndexer.index
      }
    }
  }
}