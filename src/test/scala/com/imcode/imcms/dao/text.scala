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

    textDao = new TextDao
    textDao.hibernateTemplate = new HibernateTemplate(sf)

    db.runScripts("src/test/resources/sql/text_dao.sql")
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

    letret(Factory.createText(docId, docVersionNo, no, language)) { vo =>
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


  test("save new text doc's text") { language =>
    val text = saveNewTextDoc(language = language)
    val texts = textDao.getTexts(Default.docId, Default.docVersionNo, language)
    texts must (have size (1) and contain (text))

    val queriedText = texts.head
    queriedText must have (
      'id (text.getId),
      'type (text.getType),
      'docId (Default.docId),
      'docVersionNo (Default.docVersionNo),
      'contentLoopNo (null),
      'contentNo (null),
      'no (Default.no),
      'text (Default.text),
      'language (language))
  }


  test("update existing text doc's text") { language =>
    val text = saveNewTextDoc(text="initial text", language = language)
    val updatedText = text.clone
    val updatedTextValue = "modified text"
    val updatedDocVersionNo = 1

    updatedText.setText(updatedTextValue)
    updatedText.setDocVersionNo(updatedDocVersionNo)

    textDao.saveText(updatedText)
    val queriedText = textDao.getTextById(text.getId)
    queriedText must have (
      'id (text.getId),
      'type (text.getType),
      'docId (text.getDocId),
      'docVersionNo (updatedDocVersionNo),
      'contentLoopNo (text.getContentLoopNo),
      'contentNo (text.getContentNo),
      'no (text.getNo),
      'text (updatedTextValue),
      'language (language))
  }


  test("delete text doc's text") { () =>
    val nos = 0 until 5

    for (indexNo <- nos; language <- languages)
      saveNewTextDoc(docId = Default.docId, docVersionNo = Default.docVersionNo, no = indexNo, language = language)

    for (language <- languages)
      expect(nos.size) {
        textDao.deleteTexts(Default.docId, Default.docVersionNo, language)
      }
  }

  test("save text doc's text history") { language =>
    val text = saveNewTextDoc(language = language)
    val textHistory = new TextHistory(text, admin)

    textDao.saveTextHistory(textHistory)
  }


  test("get text doc's texts by doc id and doc version no") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5

    for (versionNo <- versionNos; indexNo <- nos; language <- languages)
      saveNewTextDoc(docId = Default.docId, docVersionNo = versionNo, no = indexNo, language = language)

    for (versionNo <- versionNos) {
      val texts = textDao.getTexts(1001, versionNo)

      expect(nos.size * languages.size) { texts.size }
    }
  }


  test("get text doc's texts by doc id and doc version no and language") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5

    for (versionNo <- versionNos; indexNo <- nos; language <- languages)
      saveNewTextDoc(docId = Default.docId, docVersionNo = versionNo, no = indexNo, language = language)


    for (versionNo <- versionNos; language <- languages) {
      val texts = textDao.getTexts(Default.docId, versionNo, language)

      expect(5) { texts.size }
    }
  }
}