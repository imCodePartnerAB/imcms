package imcode.server.document.index

import com.imcode._
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
import com.imcode.imcms.mapping.DocumentMapper.TextDocumentMenuIndexPair
import scala.collection.mutable.{Map => MMap}
import org.apache.solr.common.SolrInputDocument
import _root_.imcode.server.user.RoleId
import _root_.imcode.server.document._
import _root_.imcode.server.document.FileDocumentDomainObject.FileDocumentFile
import _root_.imcode.util.io.FileInputStreamSource
import _root_.imcode.server.document.textdocument.{MenuDomainObject, TextDocumentDomainObject, ImageDomainObject, TextDomainObject}
import _root_.imcode.server.document.index.solr.{DocumentContentIndexer, DocumentIndexer}
import com.imcode.imcms.api.{I18nLanguage, I18nMeta, DocumentVersion, DocumentVersionInfo}
import com.imcode.imcms.test.fixtures.{CategoryFX, DocFX, LanguageFX}

@RunWith(classOf[JUnitRunner])
class DocumentIndexerTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.initLogging()

  val defaultTextDocEn = DocFX.mkDefaultTextDocEn
  val docIndexer: DocumentIndexer = new DocIndexingMocksSetup |>> { fx =>
    fx.addCategories(CategoryFX.mkCategories(): _*)
    fx.addParentDocumentsFor(defaultTextDocEn.getId,
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
      val indexDoc: SolrInputDocument = docIndexer.index(defaultTextDocEn)

      val indexedCategoriesIds = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_ID).asScala.toSet
      val indexedCategoriesNames = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY).asScala.map(_.toString).toSet
      val indexedCategoriesTypesIds = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_TYPE_ID).asScala.toSet
      val indexedCategoriesTypesNames = indexDoc.getFieldValues(DocumentIndex.FIELD__CATEGORY_TYPE).asScala.map(_.toString).toSet

      assertEquals("FIELD__ID", "%d_%s".format(defaultTextDocEn.getId, defaultTextDocEn.getLanguage.getCode), indexDoc.getFieldValue(DocumentIndex.FIELD__ID))
      assertNotNull("FIELD__TIMESTAMP is set", indexDoc.getFieldValue(DocumentIndex.FIELD__TIMESTAMP).asInstanceOf[Date])
      assertEquals("FIELD__META_ID", defaultTextDocEn.getId, indexDoc.getFieldValue(DocumentIndex.FIELD__META_ID))

      assertEquals("FIELD__ROLE_ID",
        Set(RoleId.USERS, RoleId.USERADMIN, RoleId.SUPERADMIN).map(_.toString),
        indexDoc.getFieldValues(DocumentIndex.FIELD__ROLE_ID).asScala.map(_.toString).toSet
      )

      assertEquals("FIELD__META_HEADLINE", defaultTextDocEn.getHeadline, indexDoc.getFieldValue(DocumentIndex.FIELD__META_HEADLINE))
      assertEquals("FIELD__META_HEADLINE_KEYWORD", defaultTextDocEn.getHeadline, indexDoc.getFieldValue(DocumentIndex.FIELD__META_HEADLINE_KEYWORD))
      assertEquals("FIELD__META_TEXT", defaultTextDocEn.getMenuText, indexDoc.getFieldValue(DocumentIndex.FIELD__META_TEXT))

      assertEquals("FIELD__DOC_TYPE_ID", defaultTextDocEn.getDocumentTypeId, indexDoc.getFieldValue(DocumentIndex.FIELD__DOC_TYPE_ID))

      assertEquals("FIELD__CREATOR_ID", defaultTextDocEn.getCreatorId, indexDoc.getFieldValue(DocumentIndex.FIELD__CREATOR_ID))
      assertEquals("FIELD__PUBLISHER_ID", defaultTextDocEn.getPublisherId, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLISHER_ID))

      assertEquals("FIELD__CREATED_DATETIME", defaultTextDocEn.getCreatedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__CREATED_DATETIME))
      assertEquals("FIELD__MODIFIED_DATETIME", defaultTextDocEn.getModifiedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__MODIFIED_DATETIME))
      assertEquals("FIELD__ACTIVATED_DATETIME", defaultTextDocEn.getPublicationStartDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__ACTIVATED_DATETIME))
      assertEquals("FIELD__PUBLICATION_START_DATETIME", defaultTextDocEn.getPublicationStartDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLICATION_START_DATETIME))
      assertEquals("FIELD__PUBLICATION_END_DATETIME", defaultTextDocEn.getPublicationEndDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__PUBLICATION_END_DATETIME))
      assertEquals("FIELD__ARCHIVED_DATETIME", defaultTextDocEn.getArchivedDatetime, indexDoc.getFieldValue(DocumentIndex.FIELD__ARCHIVED_DATETIME))

      assertEquals("FIELD__STATUS", defaultTextDocEn.getPublicationStatus.asInt(), indexDoc.getFieldValue(DocumentIndex.FIELD__STATUS))

      assertEquals("FIELD__KEYWORD",
        defaultTextDocEn.getKeywords.asScala,
        indexDoc.getFieldValues(DocumentIndex.FIELD__KEYWORD).asScala.map(_.toString).toSet
      )

      assertEquals("FIELD__PARENT_ID",
        Set(0, 1, 2),
        indexDoc.getFieldValues(DocumentIndex.FIELD__PARENT_ID).asScala.toSet
      )

      assertEquals("FIELD__PARENT_MENU_ID",
        Set("0_0", "1_0", "1_1", "2_0", "2_1", "2_2"),
        indexDoc.getFieldValues(DocumentIndex.FIELD__PARENT_MENU_ID).asScala.map(_.toString).toSet
      )

      assertEquals("FIELD__HAS_PARENTS", true, indexDoc.getFieldValue(DocumentIndex.FIELD__HAS_PARENTS))
      assertEquals("FIELD__ALIAS", defaultTextDocEn.getAlias, indexDoc.getFieldValue(DocumentIndex.FIELD__ALIAS))

      assertEquals("FIELD__CATEGORY_ID",
        defaultTextDocEn.getCategoryIds.asScala,
        indexedCategoriesIds
      )

      assertEquals("FIELD__CATEGORY",
        0 until 10 map {id => "category_" + id} toSet,
        indexedCategoriesNames
      )

      assertEquals("FIELD__CATEGORY_TYPE_ID",
        0 until 10 toSet,
        indexedCategoriesTypesIds
      )

      assertEquals("FIELD__CATEGORY_TYPE",
        0 until 10 map {id => "category_type_" + id} toSet,
        indexedCategoriesTypesNames
      )

//      def propertyValue(name: String) = indexDoc.getFieldValue(DocumentIndex.FIELD__PROPERTY_PREFIX + name)
//
//      assertEquals("FIELD__PROPERTY_PREFIX",
//        defaultTextDocEn.getProperties.values.asScala.toSet.map()
//        Set("property_1", "property_2", "property_3"),
//      )

      // content
      assertEquals("FIELD__TEMPLATE", defaultTextDocEn.getTemplateName, indexDoc.getFieldValue(DocumentIndex.FIELD__TEMPLATE))

      assertEquals("FIELD__TEXT",
        indexDoc.getFieldValues(DocumentIndex.FIELD__TEXT).asScala.toList,
        defaultTextDocEn.getTexts.values().asScala.map(_.getText).toList
      )

      for ((textNo, text) <- defaultTextDocEn.getTexts.asScala) {
        val fieldId = DocumentIndex.FIELD__TEXT + textNo
        assertEquals(fieldId, text.getText, indexDoc.getFieldValue(fieldId))
      }
    }

    "create SolrInputDocument from FileDocumentDomainObject" in {
      for (file <- Test.dir("src/test/resources/test-file-doc-files").listFiles) {
        val fdf = new FileDocumentFile |>> { fdf =>
          fdf.setInputStreamSource(new FileInputStreamSource(file))
          fdf.setMimeType("");
        }

        new FileDocumentDomainObject |>> { d =>
          d.setCreatorId(0)
          d.setLanguage(LanguageFX.mkEnglish)
          d.addFile(file.getName, fdf)
        } |> docIndexer.index
      }
    }
  }
}