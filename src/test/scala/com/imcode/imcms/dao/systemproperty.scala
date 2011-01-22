package com.imcode
package imcms.dao

import org.scalatest.junit.JUnitSuite
import org.scalatest.BeforeAndAfterAll
import org.junit.{Before, Test}
import org.junit.Assert._

import com.imcode.imcms.test.DB
import com.imcode.imcms.test.Project
import org.hibernate.Session
import imcms.api.{SystemProperty, Content}

class SystemPropertyDaoSuite extends JUnitSuite with BeforeAndAfterAll {

  var systemDao: SystemDao = _

  val DEFAULT_LANGUAGE_ID = "DefaultLanguageId"
  val START_DOC = "startDocument"

  override def beforeAll {
    val project = Project()
    val db = new DB(project)

    db.recreate()
  }

  @Before
  def resetDBData() {
    val project = Project()
    val db = new DB(project)
    val sf = db.createHibernateSessionFactory(classOf[SystemProperty])

    db.runScripts("src/test/resources/sql/system_property_dao.sql")

    systemDao = new SystemDao()
    systemDao.setSessionFactory(sf)
  }


  @Test
  def getProperties() {
    val properties = systemDao.getProperties()

    assertTrue(properties.size > 0);
  }


  @Test
  def getProperty() {
    val property = getExistingProperty(START_DOC)

    assertEquals(property.getValue(), 1001.toString)
  }


  @Test
  def savePropery() {
    val property = getExistingProperty(DEFAULT_LANGUAGE_ID)

    property.setValue(0.toString)

    systemDao.saveProperty(property)

    val property2 = getExistingProperty(DEFAULT_LANGUAGE_ID)

    assertEquals(property2.getValue(), 0.toString)

    property2.setValue(1.toString)

    systemDao.saveProperty(property2)

    val property3 = getExistingProperty(DEFAULT_LANGUAGE_ID)

    assertEquals(property3.getValue(), 1.toString)
  }


  def getExistingProperty(name: String) = letret(systemDao.getProperty(name)) { property =>
    assertNotNull(property)
  }
}