package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import imcms.util.Factory
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import imcms.test.Test.{db}
import imcms.test.fixtures.LanguageFX.{mkEnglish, mkSwedish, mkLanguages}
import imcode.server.user.{RoleId, RoleDomainObject, UserDomainObject}
import org.scalatest.fixture.FixtureFunSuite
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.context.annotation.Bean._
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Test
import com.imcode.imcms.api.{SystemProperty, I18nLanguage, TextHistory}
import imcode.server.document.textdocument.{ContentRef, TextDomainObject}

@RunWith(classOf[JUnitRunner])
class TextDaoSuite extends FixtureFunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfter {

  type FixtureParam = I18nLanguage

	var textDao: TextDao = _

  val admin = new UserDomainObject(0) |>> { _.addRoleId(RoleId.SUPERADMIN) }

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[TextDaoSuiteConfig])

    textDao = ctx.getBean(classOf[TextDao])

    db.runScripts("src/test/resources/sql/text_dao.sql")
  }

  def withFixture(test: OneArgTest) {
    mkLanguages foreach { test(_) }
  }

  object Default {
    val docId = 1001
    val docVersionNo = 0
    val no = 0
    val text = ""
  }

  def saveNewTextDoc(
      docId: Int = Default.docId,
      docVersionNo: Int = Default.docVersionNo,
      contentRef: Option[ContentRef] = None,
      no: Int = Default.no,
      text: String = Default.text,
      language: I18nLanguage) =

    Factory.createText(docId, docVersionNo, no, language) |>> { vo =>
      contentRef.foreach(vo.setContentRef)

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
        'contentLoopNo (contentRef.map(_.getLoopNo).orNull),
        'contentNo (contentRef.map(_.getContentNo).orNull),
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
//      'contentLoopNo (text.getContentLoopNo),
//      'contentNo (text.getContentNo),
      'no (text.getNo),
      'text (updatedTextValue),
      'language (language))
  }


  test("delete text doc's texts") { () =>
    val contentRef = new ContentRef(100, 1)
    val contentRefs = Seq(None, Some(contentRef))
    val nos = 0 until 5

    for (indexNo <- nos; ci <- contentRefs; language <- mkLanguages)
      saveNewTextDoc(
        docId = Default.docId,
        docVersionNo = Default.docVersionNo,
        no = indexNo,
        contentRef = ci,
        language = language)

    for (language <- mkLanguages)
      expect(nos.size * contentRefs.size, "texts count inside and outside of content loop") {
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

    for (versionNo <- versionNos; indexNo <- nos; language <- mkLanguages)
      saveNewTextDoc(docId = Default.docId, docVersionNo = versionNo, no = indexNo, language = language)

    for (versionNo <- versionNos) {
      val texts = textDao.getTexts(1001, versionNo)

      expect(nos.size * mkLanguages.size) { texts.size }
    }
  }


  test("get text doc's texts by doc id and doc version no and language") { () =>
    val contentRef = new ContentRef(100, 1)
    val versionNos = 0 until 2
    val nos = 0 until 5
    val contentRefs = Seq(None, Some(contentRef))

    for (versionNo <- versionNos; orderNo <- nos; ci <- contentRefs; language <- mkLanguages)
      saveNewTextDoc(docId = Default.docId, docVersionNo = versionNo, contentRef = ci,  no = orderNo, language = language)


    for (versionNo <- versionNos; language <- mkLanguages) {
      val texts = textDao.getTexts(Default.docId, versionNo, language).asScala

      expect(nos.size * contentRefs.size) { texts.size }

      expect(nos.size, "Texts count outsude content loop") {
        texts.count(_.getContentRef == null)
      }

      expect(nos.size, "Texts count inside content loop") {
        texts.count(text => text.getContentRef != null && text.getContentRef == contentRef)
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
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.BasicWithSql,
      Test.hibernate.configurators.addAnnotatedClasses(
        classOf[I18nLanguage],
        classOf[TextDomainObject],
        classOf[TextHistory]
      ),
      Test.hibernate.configurators.addXmlFiles(
        "com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
        "com/imcode/imcms/hbm/Text.hbm.xml"
      )
    ))
}