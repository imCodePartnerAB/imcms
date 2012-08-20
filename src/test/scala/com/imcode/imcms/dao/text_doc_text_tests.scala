package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.Test.{db}
import imcms.test.fixtures.LanguageFX.{mkEnglish, mkSwedish, mkLanguages}
import imcode.server.user.{RoleId, RoleDomainObject, UserDomainObject}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.context.annotation.Bean._
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Test
import com.imcode.imcms.api.{SystemProperty, I18nLanguage, TextHistory}

import org.scalatest.fixture
import imcode.server.document.textdocument.{DocRef, ContentRef, TextDomainObject}

@RunWith(classOf[JUnitRunner])
class TextDaoSuite extends fixture.FunSuite with BeforeAndAfterAll with BeforeAndAfter {

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

  def saveNewText(
      docId: Int = Default.docId,
      docVersionNo: Int = Default.docVersionNo,
      contentLoopRef: Option[ContentRef] = None,
      no: Int = Default.no,
      text: String = Default.text,
      language: I18nLanguage) =

    TextDomainObject.builder().docRef(DocRef.of(docId, docVersionNo)).no(no).text(text).language(language) |> { builder =>
      contentLoopRef.foreach(builder.contentLoopIdentity)
      builder.build()
    } |>> { vo =>
      textDao.saveText(vo)
      val id = vo.getId
      assertNotNull("Id has been assigned during text save", id)

      val savedVO = textDao.getTextById(id)
      assertNotNull("Saved text", savedVO)
      assertEquals("Saved text id", id, savedVO.getId)
      assertEquals("Saved text docRef", DocRef.of(docId, docVersionNo), savedVO.getDocRef)
      assertEquals("Saved text no", no, savedVO.getNo)
      assertEquals("Saved text text", text, savedVO.getText)
      assertEquals("Saved text language", language, savedVO.getLanguage)
      assertEquals("Saved text contentRef", contentLoopRef, Option(savedVO.getContentRef))
    }


  test("save new text doc's text") { language =>
    val text = saveNewText(language = language)
    val texts = textDao.getTexts(DocRef.of(Default.docId, Default.docVersionNo), language)

    assertEquals("Texts count in doc", 1, texts.size)
    assertTrue("Texts cotains saved text", texts.contains(text))
  }


  test("update existing text doc's text") { language =>
    val text = saveNewText(text="initial text", language = language)
    val updatedTextValue = "modified text"
    val updatedDocVersionNo = 1
    val updatedDocRef = DocRef.of(text.getDocRef.getDocId, updatedDocVersionNo)

    text.clone |> { textToUpdate =>
      textToUpdate.setText(updatedTextValue)
      textToUpdate.setDocRef(updatedDocRef)

      textDao.saveText(textToUpdate)
    }

    val updatedText = textDao.getTextById(text.getId)
    assertNotNull("Updated text", updatedText)
    assertEquals("Updated text id", text.getId, updatedText.getId)
    assertEquals("Updated text docRef", updatedDocRef, updatedText.getDocRef)
    assertEquals("Updated text no", text.getNo, updatedText.getNo)
    assertEquals("Updated text text", updatedTextValue, updatedText.getText)
    assertEquals("Updated text language", text.getLanguage, updatedText.getLanguage)
    assertEquals("Updated text contentRef", text.getContentRef, updatedText.getContentRef)
  }


  test("delete text doc's texts") { () =>
    val contentRef = new ContentRef(100, 1)
    val contentRefs = Seq(None, Some(contentRef))
    val nos = 0 until 5

    for (indexNo <- nos; ci <- contentRefs; language <- mkLanguages)
      saveNewText(
        docId = Default.docId,
        docVersionNo = Default.docVersionNo,
        no = indexNo,
        contentLoopRef = ci,
        language = language)

    for (language <- mkLanguages)
      expect(nos.size * contentRefs.size, "texts count inside and outside of content loop") {
        textDao.deleteTexts(DocRef.of(Default.docId, Default.docVersionNo), language)
      }
  }

  test("save text doc's text history") { language =>
    val text = saveNewText(language = language)
    val textHistory = new TextHistory(text, admin)

    textDao.saveTextHistory(textHistory)
  }


  test("get text doc's texts by doc id and doc version no") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5

    for (versionNo <- versionNos; indexNo <- nos; language <- mkLanguages)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, no = indexNo, language = language)

    for (versionNo <- versionNos) {
      val texts = textDao.getTexts(DocRef.of(1001, versionNo))

      expect(nos.size * mkLanguages.size) { texts.size }
    }
  }


  test("get text doc's texts by doc id and doc version no and language") { () =>
    val contentRef = new ContentRef(100, 1)
    val versionNos = 0 until 2
    val nos = 0 until 5
    val contentRefs = Seq(None, Some(contentRef))

    for (versionNo <- versionNos; orderNo <- nos; ci <- contentRefs; language <- mkLanguages)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, contentLoopRef = ci,  no = orderNo, language = language)


    for (versionNo <- versionNos; language <- mkLanguages) {
      val texts = textDao.getTexts(DocRef.of(Default.docId, versionNo), language).asScala

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