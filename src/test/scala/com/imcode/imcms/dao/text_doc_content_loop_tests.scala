package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite, BeforeAndAfterAll}
import imcms.test.Test.{db}
import com.imcode.imcms.test.Test
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.api.{ContentLoop}
import imcode.server.document.textdocument.{DocRef}

@RunWith(classOf[JUnitRunner])
class ContentLoopDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

//  object ContentsSort extends Enumeration {
//    val Asc, Desc = Value
//  }
//
//  case class ContentsDesc(count: Int, sort: ContentsSort.Value)
//  case class ContentLoopDesc(loopId: Long, docRef: DocRef, contentLoopIdentity: ContentRef, contentsDesc: ContentsDesc)
//
//  val loop0 = ContentLoopDesc(0, DocRef.of(1001, 0), new ContentRef(0, 0), ContentsDesc(count = 0, sort = ContentsSort.Asc))
//  val loop1 = ContentLoopDesc(1, DocRef.of(1001, 0), new ContentRef(0, 1), ContentsDesc(count = 1, sort = ContentsSort.Asc))
//  val loop2 = ContentLoopDesc(2, DocRef.of(1001, 0), new ContentRef(0, 2), ContentsDesc(count = 2, sort = ContentsSort.Asc))
//  val loop3 = ContentLoopDesc(3, DocRef.of(1001, 0), new ContentRef(0, 3), ContentsDesc(count = 3, sort = ContentsSort.Desc))

  var contentLoopDao: ContentLoopDao = _

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[ContentLoopDaoSuiteConfig])

    contentLoopDao = ctx.getBean(classOf[ContentLoopDao])

    db.runScripts("src/test/resources/sql/content_loop_dao.sql")
  }


  test("get all content loops") {
    assertEquals("loops count", 4, contentLoopDao.getLoops(DocRef.of(1001, 0)).size())
  }


  test("get content loop") {
    val loops = Array(
      getLoop(0, true),
      getLoop(1, true),
      getLoop(2, true),
      getLoop(3, true))

    assertEquals("Contents count.", loops(0).getContents.size, 0)
    assertEquals("Contents count.", loops(1).getContents.size, 1)
    assertEquals("Contents count.", loops(2).getContents.size, 3)
    assertEquals("Contents count.", loops(3).getContents.size, 3)
  }


  test("check contents order in a loop") {
    val ascSortedContents = getLoop(2, true).getContents.asScala.toList
    val descSortedContents = getLoop(3, true).getContents.asScala.toList

    for (i <- 0 to 2) {
      assertEquals("Content order no.", i, ascSortedContents(i).getNo)
    }


    for ((no, i) <- 2 to 0 by -1 zipWithIndex) {
      assertEquals("Content order no.", no, descSortedContents(i).getNo)
    }
  }

  test("create empty content loop") {
    ContentLoop.builder() |> { builder =>
      val docRef = DocRef.of(1001, 0)
      builder.docRef(docRef)
      builder.no(contentLoopDao.getNextLoopNo(docRef))

      contentLoopDao.saveLoop(builder.build())
    }
  }

  test("update existing content loop") {
    val loop = getLoop(0, true)
    val loopId = loop.getId

    val count = loop.getContents.size

    val newLoop = contentLoopDao.saveLoop(loop.addLastContent._1)
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
    val contentsCount = 5
    val docRef = DocRef.of(1001, 0)
    val loop = ContentLoop.builder().docRef(docRef).no(contentLoopDao.getNextLoopNo(docRef)).build() |> { emptyLoop =>
      1.to(contentsCount).foldLeft(emptyLoop) { case (loop, _) => loop.addFirstContent()._1 }
    }

    contentLoopDao.saveLoop(loop) |> { savedLoop =>
      assertNotNull("Loop exists", savedLoop)

      val contents = loop.getContents.asScala.toList
      val savedContents = savedLoop.getContents.asScala.toList

      assertEquals("Content count matches", contentsCount, savedContents.size)

      for (i <- 0 until contentsCount) {
        val content = contents(i)
        val savedContent = savedContents(i)

        assertEquals("Contents no-s mathces.", content.getNo, savedContent.getNo)
      }
    }
  }


  def getLoop(no: Int): ContentLoop = getLoop(no, false)

  def getLoop(no: Int, assertLoopNotNull: Boolean) = DocRef.of(1001, 0) |> { docRef =>
    contentLoopDao.getLoop(docRef, no) |>> { loop =>
      if (assertLoopNotNull)
        assertNotNull("Loop exists - docRef: %s, no: %s.".format(docRef, no), loop)
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
