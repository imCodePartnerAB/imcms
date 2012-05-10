package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import imcms.util.Factory
import imcode.server.document.textdocument.TextDomainObject
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import imcms.test.Project.{testDB}
import imcms.test.fixtures.LanguagesFX.{english, swedish, languages}
import imcode.server.user.{RoleId, RoleDomainObject, UserDomainObject}
import org.scalatest.fixture.FixtureFunSuite
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.context.annotation.Bean._
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Project
import com.imcode.imcms.api.{SystemProperty, I18nLanguage, TextHistory}

@RunWith(classOf[JUnitRunner])
class TextDaoSuite extends FixtureFunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfter {

  type FixtureParam = I18nLanguage

	var textDao: TextDao = _

  val admin = new UserDomainObject(0) |>> { _.addRoleId(RoleId.SUPERADMIN) }

  override def beforeAll() = testDB.recreate()

  before {
    val ctx = Project.spring.createCtx(classOf[TextDaoSuiteConfig])

    textDao = ctx.getBean(classOf[TextDao])

    testDB.runScripts("src/test/resources/sql/text_dao.sql")
  }

  def withFixture(test: OneArgTest) {
    languages foreach { test(_) }
  }

  case class ContentInfo(loopNo: JInteger, contentNo: JInteger)

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

    Factory.createText(docId, docVersionNo, no, language) |>> { vo =>
      for (ContentInfo(loopNo, contentNo) <- contentInfo) {
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


  test("delete text doc's texts") { () =>
    val contentInfo = ContentInfo(100, 1)
    val contentInfos = Seq(None, Some(contentInfo))
    val nos = 0 until 5

    for (indexNo <- nos; ci <- contentInfos; language <- languages)
      saveNewTextDoc(
        docId = Default.docId,
        docVersionNo = Default.docVersionNo,
        no = indexNo,
        contentInfo = ci,
        language = language)

    for (language <- languages)
      expect(nos.size * contentInfos.size, "texts count inside and outside of content loop") {
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
    val contentInfo = ContentInfo(100, 1)
    val versionNos = 0 until 2
    val nos = 0 until 5
    val contentInfos = Seq(None, Some(contentInfo))

    for (versionNo <- versionNos; orderNo <- nos; ci <- contentInfos; language <- languages)
      saveNewTextDoc(docId = Default.docId, docVersionNo = versionNo, contentInfo = ci,  no = orderNo, language = language)


    for (versionNo <- versionNos; language <- languages) {
      val texts = textDao.getTexts(Default.docId, versionNo, language)

      expect(nos.size * contentInfos.size) { texts.size }

      expect(nos.size, "Texts count outsude content loop") {
        texts filter { text => text.getContentLoopNo == null && text.getContentNo == null} size
      }

      expect(nos.size, "Texts count inside content loop") {
        texts filter { text =>
          text.getContentLoopNo == contentInfo.loopNo &&
          text.getContentNo == contentInfo.contentNo
        } size
      }
    }
  }
}




@Import(Array(classOf[AbstractHibernateConfig]))
class TextDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def textDao = new TextDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Project.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Project.hibernate.configurators.BasicWithSql,
      Project.hibernate.configurators.addAnnotatedClasses(
        classOf[I18nLanguage],
        classOf[TextDomainObject],
        classOf[TextHistory]
      ),
      Project.hibernate.configurators.addXmlFiles(
        "com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
        "com/imcode/imcms/hbm/Text.hbm.xml"
      )
    ))
}