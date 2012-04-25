package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.fixtures.UserFX.{admin}
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test.Project.{testDB}
import org.springframework.orm.hibernate3.HibernateTemplate

@RunWith(classOf[JUnitRunner])
class MetaDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

	var metaDao: MetaDao = _
  var versionDao: DocumentVersionDao = _

  override def beforeAll() = testDB.recreate()

  override def beforeEach() {
    val sf = testDB.createHibernateSessionFactory(Seq(classOf[MetaDao]),
               "src/main/resources/com/imcode/imcms/hbm/Document.hbm.xml")

    //db.runScripts()
  }

  test("touch") {

  }
}