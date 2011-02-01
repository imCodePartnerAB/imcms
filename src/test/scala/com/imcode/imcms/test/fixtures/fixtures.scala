package com.imcode
package imcms.test
package fixtures

import imcms.util.Factory
import imcode.server.user.{RoleId, UserDomainObject}

object DocFX {
  // default doc/meta id - this doc/meta or an entity which have doc/meta id field always exists (or re-created) before each test.
  val defaultId = 1001

  // doc/meta id an entity which have doc/meta id field which *may* not exists (see a test doc) before a test.
  val nextId = defaultId + 1

  // doc/meta id an entity which have doc/meta id field which *never* exists before a test.
  val newId = Int.MaxValue
}

object VersionFX {
  val defaultNo = 0
  val nextNo = defaultNo + 1
  val newNo = Int.MaxValue
}

object DocItem {
  val defaultNo = 0
  val nextNo = defaultNo + 1
  val newNo = Int.MaxValue
}

object UserFX {

  def admin = new UserDomainObject(0) {
    addRoleId(RoleId.SUPERADMIN)
  }

  def user = new UserDomainObject(0)
}

object LanguagesFX {
  def english = Factory.createLanguage(1, "en", "English")
  def swedish = Factory.createLanguage(2, "sv", "Swedish")
  def default = english
  def languages = Seq(english, swedish)
}

// mem; from db?