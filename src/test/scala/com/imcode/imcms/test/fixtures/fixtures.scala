package com.imcode
package imcms.test
package fixtures

import scala.collection.JavaConverters._
import imcode.server.user.{UserDomainObject, RoleId}
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject, DocumentPermissionSetTypeDomainObject}
import com.imcode.imcms.api.{I18nMeta, I18nLanguage, I18nSupport}
import imcode.server.document.textdocument.{DocRef, TextDomainObject, TextDocumentDomainObject}

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

  def mkTextDoc(docId: Int, language: I18nLanguage): TextDocumentDomainObject = new TextDocumentDomainObject |>> { doc =>
    doc.setId(docId)
    doc.setCreatorId(100)
    doc.setPublisherId(200)
    doc.setCategoryIds(0.until(10).toSet.asJava)
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

  def mkTextDocs(startDocId: Int = DefaultId, count: Int = 10, languages: Seq[I18nLanguage] = LanguageFX.mkLanguages): Seq[TextDocumentDomainObject] =
    for {
      docId <- startDocId until (startDocId + count) toSeq;
      language <- languages
    } yield mkTextDoc(docId, language)
}

object VersionFX {
  val Seq(zero, one, two, three, four, five, six, seven, eight, nine, ten) = 0 to 10

  val DefaultNo = 0

  val NewNo = Int.MaxValue / 2

  val VacantNo = Int.MaxValue
}


object DocRefFX {
  val Default: DocRef = DocRef.of(DocFX.DefaultId, VersionFX.DefaultNo)
}


object DocItemFX {
  val Seq(zero, one, two, three, four, five, six, seven, eight, nine, ten) = 0 to 10

  val DefaultNo = 0

  val NewNo = Int.MaxValue / 2

  val VacantNo = Int.MaxValue
}

object UserFX {
  def mkSuperAdmin: UserDomainObject = mkUser(0, RoleId.SUPERADMIN)
  def mkDefaultUser: UserDomainObject = mkUser(2, RoleId.USERS)

  def mkUser(id: Int, roleIds: RoleId*): UserDomainObject = new UserDomainObject(id) |>> { user =>
    roleIds foreach user.addRoleId
  }
}

object LanguageFX {
  val HostNameEn = "imcode.com"
  val HostNameSe = "imcode.se"

  def mkEnglish: I18nLanguage = I18nLanguage.builder().id(1).code("en").name("English").nativeName("English").build
  def mkSwedish: I18nLanguage = I18nLanguage.builder().id(2).code("sv").name("Swedish").nativeName("Svenska").build

  def mkLanguages: Seq[I18nLanguage] = Seq(mkEnglish, mkSwedish)

  def mkI18nSupport(defaultLanguage: I18nLanguage = mkEnglish): I18nSupport = new I18nSupport(
    mkLanguages.map(l => l.getCode -> l).toMap.asJava,
    Map(HostNameEn -> mkEnglish, HostNameSe -> mkSwedish).asJava,
    defaultLanguage
  )
}

object CategoryFX {
  def mkCategories(starId: Int = 0, count: Int = 10): Seq[CategoryDomainObject] =
    for (id <- starId until (starId + count))
    yield new CategoryDomainObject |>> { c =>
      c.setId(id)
      c.setName("category_" + id)
      c.setType(new CategoryTypeDomainObject(id, "category_type_" + id, id + 1, id % 2 == 0))
    }
}