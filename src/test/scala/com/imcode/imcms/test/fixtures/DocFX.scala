package com.imcode
package imcms.test
package fixtures

import com.imcode.imcms.api.{DocumentLanguage, I18nMeta}
import scala.collection.JavaConverters._
import imcode.server.user.RoleId
import imcode.server.document.DocumentPermissionSetTypeDomainObject
import imcode.server.document.textdocument.{TextDomainObject, TextDocumentDomainObject}


object DocFX {

  val Seq(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth) = 1001 to 1010

  // default doc/meta id - this doc/meta or an entity which have doc/meta id field always exists (or re-created) before each test.
  val DefaultId = 1001

  // doc/meta id an entity which have doc/meta id field which *never* exists before a test but created during this test.
  val NewId = Int.MaxValue / 2

  // id of nonexistent document
  val VacantId = Int.MaxValue

  def mkDefaultTextDocEn: TextDocumentDomainObject = mkTextDoc(DocFX.DefaultId, LanguageFX.mkEnglish)
  def mkDefaultTextDocSe: TextDocumentDomainObject = mkTextDoc(DocFX.DefaultId, LanguageFX.mkSwedish)

  def mkTextDoc(docId: Int, language: DocumentLanguage): TextDocumentDomainObject = new TextDocumentDomainObject |>> { doc =>
    doc.setId(docId)
    doc.setCreatorId(100)
    doc.setPublisherId(200)
    doc.setCategoryIds(0.until(10).toSet.map(Int.box).asJava)
    doc.setLanguage(language)
    doc.setKeywords(0.until(10).map(n => "keyword_%d_%d".format(docId, n)).:+("keyword %d %d".format(docId, 10)).toSet.asJava)
    doc.setAlias("alias_%d" format docId)
    doc.setTemplateName("template_%d" format docId)
    doc.setSearchDisabled(false)

    // only roles are indexed, permission sets are ignored
    doc.getMeta.getRoleIdToDocumentPermissionSetTypeMappings |> { m =>
      m.setPermissionSetTypeForRole(RoleId.USERS, DocumentPermissionSetTypeDomainObject.FULL)
      m.setPermissionSetTypeForRole(RoleId.USERADMIN, DocumentPermissionSetTypeDomainObject.FULL)
      m.setPermissionSetTypeForRole(RoleId.SUPERADMIN, DocumentPermissionSetTypeDomainObject.FULL)
    }

    doc.getI18nMeta |> { m =>
      I18nMeta.builder(m)
        .headline("i18n_meta_headline_%d_%s".format(docId, language.getCode))
        .menuText("i18n_meta_menu_text_%d_%s".format(docId, language.getCode))
        .build() |> doc.setI18nMeta
    }

    doc.setProperties(0.until(10).map(n => ("property_name_%d_%d".format(docId, n), "property_value_%d_%d".format(docId, n))).
      :+("property_name_%d_%d".format(docId, 10), "property value %d %d".format(docId, 10)).toMap.asJava)

    // setup menu items (FIELD__CHILD_ID) as mocks
    // doc.setMenus(Map(
    //   1 -> ...
    //   2 -> ...
    // ))

    for (textNo <- 0 until 10) {
      doc.setText(textNo, new TextDomainObject("text_%d_%d_%s".format(docId, textNo, language.getCode)))
    }
  }

  def mkTextDocs(startDocId: Int = DefaultId, count: Int = 10, languages: Seq[DocumentLanguage] = LanguageFX.mkLanguages): Seq[TextDocumentDomainObject] =
    for {
      docId <- startDocId until (startDocId + count) toSeq;
      language <- languages
    } yield mkTextDoc(docId, language)
}
