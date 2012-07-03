package imcode.server.document.index

import com.imcode.{when => _, _}
import scala.collection.JavaConverters._
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.test.fixtures.LanguageFX
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

object Docs {

  // text doc to index
  val textDocEn = new TextDocumentDomainObject |>> { doc =>
    doc.setId(1001)
    doc.setCreatorId(100)
    doc.setPublisherId(200)
    doc.setCategoryIds(0.to(10).toSet.asJava)
    doc.setLanguage(LanguageFX.mkEnglish)
    doc.setKeywords(Set("kw_1", "kw_2", "kw_3", "kw_compound kw_keyword kw_sentence").asJava)
    doc.setAlias("alis_text_doc_1001")
    doc.setTemplateName("template_1001")

    // only roles are indexed, permission sets are ignored
    doc.getMeta.getRoleIdToDocumentPermissionSetTypeMappings |> { m =>
      m.setPermissionSetTypeForRole(RoleId.USERS, DocumentPermissionSetTypeDomainObject.FULL)
      m.setPermissionSetTypeForRole(RoleId.USERADMIN, DocumentPermissionSetTypeDomainObject.FULL)
      m.setPermissionSetTypeForRole(RoleId.SUPERADMIN, DocumentPermissionSetTypeDomainObject.FULL)
    }

    doc.getI18nMeta |> { m =>
      m.setHeadline("I18nMetaHeadline_en")
      m.setMenuText("I18nMetaMenuText_en")
    }

    doc.setProperties(Map("p1" -> "property_1", "p2" -> "property_2", "p3" -> "property_3").asJava)

    // setup menu items (FIELD__CHILD_ID) as mocks
    // doc.setMenus(Map(
    //   1 -> ...
    //   2 -> ...
    // ))
  }

  def generateTextDoc(docId: Int, language: I18nLanguage): TextDocumentDomainObject = new TextDocumentDomainObject |>> { doc =>
    doc.setId(docId)
    doc.setCreatorId(100)
    doc.setPublisherId(200)
    doc.setCategoryIds(0.until(10).toSet.asJava)
    doc.setLanguage(language)
    doc.setKeywords(0.until(10).map(n => "keyword_%d_%d".format(docId, n)).toSet.asJava)
    doc.setAlias("alias_%d" format docId)
    doc.setTemplateName("template_%d" format docId)

    // only roles are indexed, permission sets are ignored
    doc.getMeta.getRoleIdToDocumentPermissionSetTypeMappings |> { m =>
      m.setPermissionSetTypeForRole(RoleId.USERS, DocumentPermissionSetTypeDomainObject.FULL)
      m.setPermissionSetTypeForRole(RoleId.USERADMIN, DocumentPermissionSetTypeDomainObject.FULL)
      m.setPermissionSetTypeForRole(RoleId.SUPERADMIN, DocumentPermissionSetTypeDomainObject.FULL)
    }

    doc.getI18nMeta |> { m =>
      m.setHeadline("i18n_meta_headline_%d_%s".format(docId, language.getCode))
      m.setMenuText("i18n_meta_menu_text_%d_%s".format(docId, language.getCode))
    }

    doc.setProperties(0.until(10).map(n => ("property_name_%d" format docId, "property_value_%d" format docId)).toMap.asJava)
  }


  def generateTextDocs(startDocId: Int, count: Int): Iterator[TextDocumentDomainObject] =
    for {
      docId <- startDocId.until(startDocId + count).toIterator
    } yield generateTextDoc(docId, LanguageFX.mkEnglish)

}




class DocIndexingMocksSetup {

  case class ParentDoc(docId: Int, menuNo: Int)

  private val documentMapperMock = mock[DocumentMapper]
  private val categoryMapperMock = mock[CategoryMapper]
  private val servicesMock = mock[ImcmsServices]

  private val categories = MMap.empty[Int, CategoryDomainObject]
  private val docs = MMap.empty[Int, MMap[I18nLanguage, DocumentDomainObject]].withDefaultValue(MMap.empty)

  when(documentMapperMock.getImcmsServices).thenReturn(servicesMock)

  when(servicesMock.getI18nSupport).thenReturn(LanguageFX.mkI18nSupport)

  when(categoryMapperMock.getCategories(anyCollectionOf(classOf[JInteger]))).thenAnswer(new Answer[JSet[CategoryDomainObject]] {
    def answer(invocation: InvocationOnMock) = {
       val availableCategories = for {
         categoryId <- invocation.getArguments()(0).asInstanceOf[JCollection[JInteger]].asScala
         category <- categories.get(categoryId)
       } yield category

       availableCategories.toSet.asJava
     }
  })

  when(documentMapperMock.getDocumentMenuPairsContainingDocument(any[DocumentDomainObject])).thenReturn(
      Array.empty[TextDocumentMenuIndexPair]
  )

  when(documentMapperMock.getDefaultDocument(anyInt, any[I18nLanguage])).thenAnswer(new Answer[DocumentDomainObject] {
    def answer(invocation: InvocationOnMock) = invocation.getArguments |> { args =>
      docs(args(0).asInstanceOf[Int])(args(1).asInstanceOf[I18nLanguage])
    }
  })

  when(documentMapperMock.getAllDocumentIds).thenReturn(docs.keys.map(Int.box).toList.asJava)


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

  def addDocument(doc: DocumentDomainObject) = this |>> { _ =>
    docs(doc.getId).put(doc.getLanguage, doc)
  }

  // getDocumentMenuPairsContainingDocument
}