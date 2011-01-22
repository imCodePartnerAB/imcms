package com.imcode
package imcms.dao

import org.scalatest.junit.JUnitSuite
import org.scalatest.BeforeAndAfterAll
import org.junit.{Before, Test}

import com.imcode.imcms.test.DB
import com.imcode.imcms.test.Project

import org.junit.Assert._
import imcms.mapping.orm.Include

class IncludeDaoSuite extends JUnitSuite with BeforeAndAfterAll {

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
    val sf = db.createHibernateSessionFactory(classOf[Include])

    db.runScripts("src/test/resources/sql/include_dao.sql")

    metaDao = new MetaDao
    metaDao.setSessionFactory(sf)
  }


  @Test
  def getIncludes() {
    val includes = metaDao.getIncludes(1001)
    assertEquals(3, includes.size)
  }


  @Test
  def saveInclude() {
    val include = new Include
    include.setMetaId(1002)
    include.setIncludedDocumentId(1001)

    metaDao.saveInclude(include)

    assertNotNull(include.getId)
  }


  @Test
  def deleteIncludes() {
    val deletedCount = metaDao.deleteIncludes(1001)

    assertEquals(deletedCount, 3)
  }
}