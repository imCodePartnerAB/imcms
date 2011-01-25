package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import imcms.util.Factory
import imcms.api.{I18nLanguage, TextHistory}
import imcode.server.document.textdocument.TextDomainObject
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test.Base.{db}
import imcode.server.user.{RoleId, RoleDomainObject, UserDomainObject}
import org.scalatest.fixture.FixtureFunSuite
import org.springframework.orm.hibernate3.HibernateTemplate

@RunWith(classOf[JUnitRunner])
class TextDaoSuite extends FixtureFunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

  type FixtureParam = I18nLanguage

	var textDao: TextDao = _

  val english = Factory.createLanguage(1, "en", "English")
  val swedish = Factory.createLanguage(2, "sv", "Swedish")
  val languages = Seq(english, swedish)

  val admin = new UserDomainObject(0) {
    addRoleId(RoleId.SUPERADMIN)
  }

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val sf = db.createHibernateSessionFactory(Seq(classOf[I18nLanguage], classOf[TextDomainObject], classOf[TextHistory]),
               "src/main/resources/com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
               "src/main/resources/com/imcode/imcms/hbm/Text.hbm.xml")

    db.runScripts("src/test/resources/sql/text_dao.sql")

    textDao = new TextDao
    textDao.hibernateTemplate = new HibernateTemplate(sf)
  }

  def withFixture(test: OneArgTest) {
    languages foreach { test(_) }
  }

  case class ContentInfo(loopNo: Int, contentNo: Int)

  object Default {
    val docId = 1001
    val docVersionNo = 0
    val no = 0
    val text = ""
  }

  def saveNewTextDoc(
      docId: Int = Default.docId,
      docVersionNo: Int = Default.docVersionNo,
      contentInfo: Option[ContentInfo] = None,
      no: Int = Default.no,
      text: String = Default.text,
      language: I18nLanguage) =

    letret(Factory.createText(docId, no, docVersionNo, language)) { vo =>
      for (ContentInfo(loopNo: Int, contentNo: Int) <- contentInfo) {
        vo.setContentLoopNo(loopNo)
        vo.setContentNo(contentNo)
      }

      vo.setText(text)
      textDao.saveText(vo)
      val id = vo.getId
      id must not be (null)

      val savedVO = textDao.getTextById(id)
      savedVO must not be (null)
      savedVO must have (
        'id (id),
        'docId (docId),
        'docVersionNo (docVersionNo),
        'contentLoopNo (contentInfo.map(_.loopNo).orNull),
        'contentNo (contentInfo.map(_.contentNo).orNull),
        'no (no),
        'text (text),
        'language (language))
    }


	/*
	@Test void getExistingText() {
		def text = textDao.getText(1001, 1, 1)

		Assert.assertNotNull(text)
	}


    @Test void getNonExistingText() {
        def text = textDao.getText(10001, 1, 1)

        assertNull(text, "Text does not exists")
    }
    */


  test("save new text doc's text") { language =>
    val text = saveNewTextDoc(language = language)
    val texts = textDao.getTexts(Default.docId, Default.docVersionNo, language)
    texts must (have size (1) and contain (text))

    val queriedText = texts.head
    queriedText must have (
      'id (text.getId),
      'docId (Default.docId),
      'docVersionNo (Default.docVersionNo),
      'contentLoopNo (null),
      'contentNo (null),
      'no (Default.no),
      'text (Default.text),
      'language (language)
    )
  }


  test("update existing text doc's text") { language =>
    val text = saveNewTextDoc(language = language)
    val textClone = text.clone

    val updatedText = Factory.createText(1001, 0, 0, english)
    updatedText.setId(text.getId)

    updatedText.setText("updated text")
    textDao.saveText(updatedText)
  }


  test("delete text doc's text in a given language") { () =>
    for (no <- 1 to 2) {
      val text_en = Factory.createText(1001, 0, no, english)
      val text_sw = Factory.createText(1001, 0, no, swedish)

      textDao.saveText(text_en)
      textDao.saveText(text_sw)
    }

    val deletedCount_en = textDao.deleteTexts(1001, 0, english.getId)

    assertEquals(deletedCount_en, 3)

    val deletedCount_sw = textDao.deleteTexts(1001, 0, swedish)

    assertEquals(deletedCount_sw, 3)
  }

  test("save text doc's text history") { () =>
    val textHistory = new TextHistory(Factory.createText(1001, 0, 0, english), admin)

    textDao.saveTextHistory(textHistory)
  }


  test("get text doc's texts by doc id and doc version no") { () =>
    for (versionNo <- 0 until 2; no <- 0 until 5) {
      val text_en = Factory.createText(1001, versionNo, no, english)
      val text_sw = Factory.createText(1001, versionNo, no, swedish)

      textDao.saveText(text_en);
      textDao.saveText(text_sw);
    }

    for (versionNo <- 0 to 2) {
      val texts = textDao.getTexts(1001, versionNo);

      assertEquals(texts.size(), 5 * 2);
    }
  }


  test("get text doc's texts by doc id and doc version no and language") { () =>
    for (versionNo <- 0 until 3; no <- 0 until 5) {
      val text_en = Factory.createText(1001, versionNo, no, english)
      val text_sw = Factory.createText(1001, versionNo, no, swedish)

      textDao.saveText(text_en)
      textDao.saveText(text_sw)
    }

    for (versionNo <- 0 until 3) {
      val texts_en = textDao.getTexts(1001, versionNo, english)
      val texts_sw = textDao.getTexts(1001, versionNo, swedish)

      assertEquals(texts_en.size(), 5)
      assertEquals(texts_sw.size(), 5)
    }
  }
}