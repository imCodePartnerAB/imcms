package com.imcode
package imcms.dao

import imcms.mapping.orm.Include
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{WordSpec, BeforeAndAfterEach, BeforeAndAfterAll}
import org.scalatest.matchers.MustMatchers
import imcms.test.Base.{db}
import org.springframework.orm.hibernate3.HibernateTemplate

@RunWith(classOf[JUnitRunner])
class IncludeDaoSpec extends WordSpec with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

	var metaDao: MetaDao = _

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val sf = db.createHibernateSessionFactory(classOf[Include])
    db.runScripts("src/test/resources/sql/include_dao.sql")

    metaDao = new MetaDao
    metaDao.hibernateTemplate = new HibernateTemplate(sf)
  }


  "A MetaDao" should {
    "return all [3] existing text doc's includes" in {
      metaDao.getIncludes(1001) must have size (3)
    }

    "save new text doc's include" in {
      val include = new Include
      include.setMetaId(1002)
      include.setIncludedDocumentId(1001)

      metaDao.saveInclude(include)

      include.getId must not be (null)
    }

    "delete all [3] existing text doc's includes" in {
      expect(3) {
        metaDao.deleteIncludes(1001)
      }

      metaDao.getIncludes(1001) must be ('empty)
    }
  }
}