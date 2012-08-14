package com.imcode
package imcms.dao

import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfter, FunSuite, BeforeAndAfterAll}
import imcms.test.Test.{db}
import com.imcode.imcms.test.Test
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.api.{ContentLoop}
import org.springframework.orm.hibernate4.HibernateTransactionManager
import imcode.server.document.textdocument.DocIdentity

@RunWith(classOf[JUnitRunner])
class ContentLoopDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  // loops predefined in src/test/resources/dbunit-content_loop.xml: loop_<contents-count>_[sort-order]_id
  val loop_0_id = 0

  val loop_1_id = 1

  val loop_3_asc_id = 2

  val loop_3_desc_id = 3


  var contentLoopDao: ContentLoopDao = _

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[ContentLoopDaoSuiteConfig])

    contentLoopDao = ctx.getBean(classOf[ContentLoopDao])

    db.runScripts("src/test/resources/sql/content_loop_dao.sql")
  }


  test("get all [4] text doc's content loops") {
    assertEquals("loops count", 4, contentLoopDao.getLoops(new DocIdentity(1001, 0)).size())
  }


  test("get text doc's content loop") {
    val loops = Array(
      getLoop(loop_0_id, true),
      getLoop(loop_1_id, true),
      getLoop(loop_3_asc_id, true),
      getLoop(loop_3_desc_id, true))

    assertEquals("Contents count.", loops(0).getContents.size, 0)
    assertEquals("Contents count.", loops(1).getContents.size, 1)
    assertEquals("Contents count.", loops(2).getContents.size, 3)
    assertEquals("Contents count.", loops(3).getContents.size, 3)
  }


  test("check contents order in a loop") {
    val ascSortedContens = getLoop(loop_3_asc_id, true).getContents
    val descSortedContens = getLoop(loop_3_desc_id, true).getContents

    for (i <- 0 to 2) {
      assertEquals("Content order no.", i, ascSortedContens.get(i).getNo)
    }


    for ((no, i) <- 2 to(0, -1) zipWithIndex) {
      assertEquals("Content order no.", no, descSortedContens.get(i).getNo)
    }
  }

  test("create empty content loop") {
    new ContentLoop |> {
      loop =>
        val docIdentity = new DocIdentity(1001, 0)
        loop.setDocIdentity(docIdentity)
        loop.setNo(contentLoopDao.getNextLoopNo(docIdentity))

        contentLoopDao.saveLoop(loop)
    }
  }

  test("update existing content loop") {
    val loop = getLoop(0, true)
    val loopId = loop.getId

    val count = loop.getContents.size

    loop.addLastContent
    val newLoop = contentLoopDao.saveLoop(loop)
    assertEquals(count + 1, newLoop.getContents.size)

    assertNotNull(contentLoopDao.getLoop(newLoop.getId))
  }

  test("delete existing content loop") {
    val loop = getLoop(0, true)

    assertNotNull("Loop exists", loop)

    val loopId = loop.getId

    contentLoopDao.deleteLoop(loopId)

    assertNull(contentLoopDao.getLoop(loopId))
  }

  test("create non empty content loop [with 5 contents]") {
    var loop = new ContentLoop
    val docIdentity = new DocIdentity(1001, 0)
    loop.setDocIdentity(docIdentity)
    loop.setNo(contentLoopDao.getNextLoopNo(docIdentity))

    val contentsCount = 5

    for (_ <- 0 until contentsCount) loop.addFirstContent

    loop = contentLoopDao.saveLoop(loop)

    val savedLoop = contentLoopDao.getLoop(loop.getId)

    assertNotNull("Loop exists", savedLoop)

    val contents = loop.getContents
    val savedContents = savedLoop.getContents

    assertEquals("Content count matches", contentsCount, savedContents.size)

    for (i <- 0 until contentsCount) {
      val content = contents.get(i)
      val savedContent = savedContents.get(i)

      assertEquals("Contents no-s mathces.", content.getNo, savedContent.getNo)
    }
  }


  def getLoop(no: Int): ContentLoop = getLoop(no, false)

  def getLoop(no: Int, assertLoopNotNull: Boolean) = new DocIdentity(1001, 0) |> { docIdentity =>
    contentLoopDao.getLoop(docIdentity, no) |>> { loop =>
      if (assertLoopNotNull)
        assertNotNull("Loop exists - docIdentity: %s, no: %s.".format(docIdentity, no), loop)
    }
  }
}

@Import(Array(classOf[AbstractHibernateConfig]))
class ContentLoopDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def contentLoopDao = new ContentLoopDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.BasicWithSql,
      Test.hibernate.configurators.addAnnotatedClasses(classOf[ContentLoop]),
      Test.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/ContentLoop.hbm.xml")
    ))
}
