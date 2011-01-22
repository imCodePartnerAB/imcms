package com.imcode
package imcms.dao

import org.scalatest.junit.JUnitSuite
import org.scalatest.BeforeAndAfterAll
import org.junit.{Before, Test}

import com.imcode.imcms.test.DB
import com.imcode.imcms.test.Project

import org.junit.Assert._
import imcms.mapping.orm.TemplateNames

//todo: Test named queries
class TemplateNamesDaoSuite extends JUnitSuite with BeforeAndAfterAll {

	var metaDao: MetaDao = _

  override def beforeAll {
    val project = Project()
    val db = new DB(project)

    db.recreate()
  }


  @Before
  def resetDBData() {
    val project = Project()
    val db = new DB(project)
    val sf = db.createHibernateSessionFactory(classOf[TemplateNames])

    db.runScripts("src/test/resources/sql/template_names_dao.sql")

    metaDao = new MetaDao
    metaDao.setSessionFactory(sf)
  }


  @Test def getNonExistingTemplateNames() {
    val tns = metaDao.getTemplateNames(10001)

    assertNull(tns)
  }

  @Test def getTemplateNames() {
    val tns = metaDao.getTemplateNames(1001)

    assertNotNull(tns)
  }

  @Test def updateTemplateNames() {
    var tns = metaDao.getTemplateNames(1001)
    assertNotNull(tns);

    tns.setTemplateName("UPDATED")
    metaDao.saveTemplateNames(tns)

    tns = metaDao.getTemplateNames(1001)
    assertEquals("UPDATED", tns.getTemplateName())
  }

  @Test def deleteTemplateNames() {
    var tns = metaDao.getTemplateNames(1001)
    assertNotNull(tns)

    metaDao.deleteTemplateNames(1001)

    tns = metaDao.getTemplateNames(1001)
    assertNull(tns)
  }


  @Test def insertTemplateNames() {
    var tns = metaDao.getTemplateNames(1001)
    assertNotNull(tns)

    metaDao.deleteTemplateNames(1001)
    val tns2 = metaDao.getTemplateNames(1001)
    assertNull(tns2)

    metaDao.saveTemplateNames(tns)
    tns = metaDao.getTemplateNames(1001)
    assertNotNull(tns)
  }
}