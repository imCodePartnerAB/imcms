package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.fixtures.UserFX.{admin}
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfter, FunSuite, BeforeAndAfterAll}
import imcms.test.Project.{testDB}

@RunWith(classOf[JUnitRunner])
class MetaDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfter {

	var metaDao: MetaDao = _
  var versionDao: DocumentVersionDao = _

  override def beforeAll() = testDB.recreate()

  def before {
    //db.runScripts()
  }

  test("touch") {

  }
}