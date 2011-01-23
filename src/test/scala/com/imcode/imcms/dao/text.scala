package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject
import imcms.util.Factory
import imcms.api.{I18nLanguage, TextHistory}
import imcode.server.document.textdocument.TextDomainObject
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test.Base.{db}

@RunWith(classOf[JUnitRunner])
class TextDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

	var textDao: TextDao = _

  val ENGLISH = Factory.createLanguage(1, "en", "English")
  val SWEDISH = Factory.createLanguage(2, "sv", "Swedish")

  val ADMIN = new UserDomainObject(0)

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val sf = db.createHibernateSessionFactory(Seq(classOf[I18nLanguage], classOf[TextDomainObject], classOf[TextHistory]),
               "src/main/resources/com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
               "src/main/resources/com/imcode/imcms/hbm/Text.hbm.xml")

    db.runScripts("src/test/resources/sql/text_dao.sql")

    textDao = new TextDao
    textDao.setSessionFactory(sf)
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


  test("save new text doc's text") {
    val text = Factory.createText(1001, 0, 0, ENGLISH)

    textDao.saveText(text)
  }


  test("update existing text doc's text") {
    val text = Factory.createText(1001, 0, 0, ENGLISH)

    textDao.saveText(text)

    val updatedText = Factory.createText(1001, 0, 0, ENGLISH)
    updatedText.setId(text.getId)

    updatedText.setText("updated text")
    textDao.save(updatedText)
  }


  test("delete text doc's text in a given language") {
    for (no <- 1 to 2) {
      val text_en = Factory.createText(1001, 0, no, ENGLISH)
      val text_sw = Factory.createText(1001, 0, no, SWEDISH)

      textDao.saveText(text_en)
      textDao.saveText(text_sw)
    }

    val deletedCount_en = textDao.deleteTexts(1001, 0, ENGLISH.getId)

    assertEquals(deletedCount_en, 3)

    val deletedCount_sw = textDao.deleteTexts(1001, 0, SWEDISH)

    assertEquals(deletedCount_sw, 3)
  }

  test("save text doc's text history") {
    val textHistory = new TextHistory(Factory.createText(1001, 0, 0, ENGLISH), ADMIN)

    textDao.saveTextHistory(textHistory)
  }


  test("get text doc's texts by doc id and doc version no") {
    for (versionNo <- 0 until 2; no <- 0 until 5) {
      val text_en = Factory.createText(1001, versionNo, no, ENGLISH)
      val text_sw = Factory.createText(1001, versionNo, no, SWEDISH)

      textDao.saveText(text_en);
      textDao.saveText(text_sw);
    }

    for (versionNo <- 0 to 2) {
      val texts = textDao.getTexts(1001, versionNo);

      assertEquals(texts.size(), 5 * 2);
    }
  }


  test("get text doc's texts by doc id and doc version no and language") {
    for (versionNo <- 0 until 3; no <- 0 until 5) {
      val text_en = Factory.createText(1001, versionNo, no, ENGLISH)
      val text_sw = Factory.createText(1001, versionNo, no, SWEDISH)

      textDao.saveText(text_en)
      textDao.saveText(text_sw)
    }

    for (versionNo <- 0 until 3) {
      val texts_en = textDao.getTexts(1001, versionNo, ENGLISH)
      val texts_sw = textDao.getTexts(1001, versionNo, SWEDISH)

      assertEquals(texts_en.size(), 5)
      assertEquals(texts_sw.size(), 5)
    }
  }
}