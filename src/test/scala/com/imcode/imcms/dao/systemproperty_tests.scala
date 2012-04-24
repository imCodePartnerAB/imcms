package com.imcode
package imcms.dao

import imcms.test.Project.{db}
import imcms.api.{SystemProperty}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{WordSpec, BeforeAndAfterEach, BeforeAndAfterAll}
import org.scalatest.matchers.{MustMatchers}

@RunWith(classOf[JUnitRunner])
class SystemPropertyDaoSpec extends WordSpec with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

  var systemDao: SystemDao = _

  val DEFAULT_LANGUAGE_ID = "DefaultLanguageId"
  val START_DOC = "startDocument"

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val sf = db.createHibernateSessionFactory(classOf[SystemProperty])
    db.runScripts("src/test/resources/sql/system_property_dao.sql")

    systemDao = new SystemDao()
    systemDao.setSessionFactory(sf)
  }

  def getExistingProperty(name: String) = doto(systemDao.getProperty(name)) { property =>
    property must not be (null)
  }


  "A SystemDao" should {
    "return all [2] existing properties" in {
      systemDao.getProperties() must have size (2)
    }

    "return existing property by name" in {
      val property = getExistingProperty(START_DOC)
      property.getName must be (START_DOC)
      property.getValue must be (1001.toString)
    }

    "update existing property" in {
      val property = getExistingProperty(DEFAULT_LANGUAGE_ID)
      property.setValue(0.toString)
      systemDao.saveProperty(property)

      val property2 = getExistingProperty(DEFAULT_LANGUAGE_ID)
      assert(property2.getValue() === 0.toString)

      property2.setValue(1.toString)
      systemDao.saveProperty(property2)

      val property3 = getExistingProperty(DEFAULT_LANGUAGE_ID)
      assert(property3.getValue() === 1.toString)
    }
  }
}