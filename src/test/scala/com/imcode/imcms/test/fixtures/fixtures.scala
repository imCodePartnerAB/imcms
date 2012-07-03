package com.imcode
package imcms.test
package fixtures

import imcms.util.Factory
import imcode.server.user.{RoleId, UserDomainObject}
import scala.collection.JavaConversions._
import imcms.api.{I18nLanguage, I18nSupport}

object DocFX {
  val Seq(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth) = 1001 to 1010

  // default doc/meta id - this doc/meta or an entity which have doc/meta id field always exists (or re-created) before each test.
  val defaultId = first

  // doc/meta id an entity which have doc/meta id field which *never* exists before a test but created during this test.
  val newId = Int.MaxValue / 2

  // vacant doc/meta id an entity which *never* exists before a test.
  val vacantId = Int.MaxValue
}

object VersionFX {
  val Seq(zero, one, two, three, four, five, six, seven, eight, nine, ten) = 0 to 10

  val defaultNo = zero

  val newNo = Int.MaxValue / 2

  val vacantNo = Int.MaxValue
}

object DocItemFX {
  val Seq(zero, one, two, three, four, five, six, seven, eight, nine, ten) = 0 to 10

  val defaultNo = zero

  val newNo = Int.MaxValue / 2

  val vacantNo = Int.MaxValue
}

object UserFX {

  def mkSuperAdmin = new UserDomainObject(0) {
    addRoleId(RoleId.SUPERADMIN)
  }

  def mkDefaultUser = new UserDomainObject(2) {
    addRoleId(RoleId.USERS)
  }
}

object LanguageFX {
  def mkEnglish: I18nLanguage = new I18nLanguage.Builder().id(1).code("en").name("English").nativeName("English").build
  def mkSwedish: I18nLanguage = new I18nLanguage.Builder().id(2).code("sv").name("Swedish").nativeName("Svenska").build

  def mkDefault: I18nLanguage = mkEnglish

  def languages: Seq[I18nLanguage] = Seq(mkEnglish, mkSwedish)

  def mkI18nSupport = new I18nSupport {
    setDefaultLanguage(mkDefault)
    setLanguages(languages)
    setHosts(Map("imcode.com" -> mkEnglish, "imcode.se" -> mkSwedish))
  }
}

// mem; from db?