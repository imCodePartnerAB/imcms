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

  // doc/meta id an entity which have doc/meta id field which *never* exists before a test.
  val newId = Int.MaxValue / 2

  // missing doc/meta id an entity which have doc/meta id field
  val missingId = Int.MaxValue
}

object VersionFX {
  val Seq(zero, one, two, three, four, five, six, seven, eight, nine, ten) = 0 to 10

  val defaultNo = zero
  val newNo = Int.MaxValue / 2
  val missingNo = Int.MaxValue
}

object DocItemFX {
  val Seq(zero, one, two, three, four, five, six, seven, eight, nine, ten) = 0 to 10

  val defaultNo = zero
  val newNo = Int.MaxValue / 2
  val missingNo = Int.MaxValue
}

object UserFX {

  def admin = new UserDomainObject(0) {
    addRoleId(RoleId.SUPERADMIN)
  }

  def user = new UserDomainObject(2) {
    addRoleId(RoleId.USERS)
  }
}

object LanguagesFX {
  def english = Factory.createLanguage(1, "en", "English")
  def swedish = Factory.createLanguage(2, "sv", "Swedish")
  def default = english
  def languages = Seq(english, swedish)
  def i18nSupport = new I18nSupport {
    setDefaultLanguage(default)
    setLanguages(languages)
    setHosts(Map.empty[String, I18nLanguage])
  }
}

// mem; from db?