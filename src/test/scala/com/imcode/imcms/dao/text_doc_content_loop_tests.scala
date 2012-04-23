package com.imcode
package imcms.dao

import com.imcode.imcms.api.ContentLoop
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test.Base.{db}
import org.springframework.orm.hibernate3.{HibernateTemplate}

@RunWith(classOf[JUnitRunner])
class ContentLoopDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

  // loops predefined in src/test/resources/dbunit-content_loop.xml: loop_<contents-count>_[sort-order]_id
  val loop_0_id = 0

  val loop_1_id = 1

  val loop_3_asc_id = 2

  val loop_3_desc_id = 3


  var contentLoopDao: ContentLoopDao = _

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val sf = db.createHibernateSessionFactory(Seq(classOf[ContentLoop]),
               "src/main/resources/com/imcode/imcms/hbm/ContentLoop.hbm.xml")

    contentLoopDao = new ContentLoopDao |< { _.sessionFactory = sf }

    db.runScripts("src/test/resources/sql/content_loop_dao.sql")
  }


  test("get all [4] text doc's content loops") {
    contentLoopDao.getLoops(1001, 0) must have size (4)
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
      assertEquals("Content order no.", new JInteger(i), ascSortedContens.get(i).getNo)
    }


    for (i <- 2 to (0, -1)) {
      assertEquals("Content order no.", new JInteger(i), descSortedContens.get(i).getNo)
    }
	}

  test("create empty content loop") {
    new ContentLoop |> { loop =>
      loop.setDocId(1001);
      loop.setDocVersionNo(0);
      loop.setNo(getNextLoopNo())

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
    loop.setDocId(1001)
    loop.setDocVersionNo(0)
    loop.setNo(getNextLoopNo())

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

	def getLoop(no: Int, assertLoopNotNull: Boolean) = doto(contentLoopDao.getLoop(1001, 0, no)) { loop =>
    if (assertLoopNotNull)
      assertNotNull("Loop exists - docId: %s, docVersionNo: %s, no: %s.".format(1001, 0, no), loop)
  }


  def getNextLoopNo(): JInteger = contentLoopDao.hibernate.getByQuery[JInteger](
    "select max(l.no) from ContentLoop l where l.docId = 1001 and l.docVersionNo = 0"
  ) match {
    case null => 0
    case n => n.intValue + 1
  }
}
