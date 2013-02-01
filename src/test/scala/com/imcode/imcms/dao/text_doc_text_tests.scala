package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.imcode.imcms.test.Test.{db}
import com.imcode.imcms.test.fixtures.LanguageFX.{mkEnglish, mkSwedish, mkLanguages}
import _root_.imcode.server.user.{RoleId, RoleDomainObject, UserDomainObject}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.context.annotation.Bean._
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Test
import com.imcode.imcms.api.{DocRef, SystemProperty, ContentLanguage, TextHistory}

import org.scalatest.fixture
import imcode.server.document.textdocument.{ContentRef, TextDomainObject}

@RunWith(classOf[JUnitRunner])
class TextDaoSuite extends fixture.FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  type FixtureParam = ContentLanguage

	var textDao: TextDao = _

  val admin = new UserDomainObject(0) |>> { _.addRoleId(RoleId.SUPERADMIN) }

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[TextDaoSuiteConfig])

    textDao = ctx.getBean(classOf[TextDao])

    db.runScripts("src/test/resources/sql/text_dao.sql")
  }

  def withFixture(test: OneArgTest) {
    mkLanguages.foreach(test(_))
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
      contentRefOpt: Option[ContentRef] = None,
      no: Int = Default.no,
      text: String = Default.text,
      language: ContentLanguage) =

    TextDomainObject.builder().docRef(DocRef.of(docId, docVersionNo)).no(no).text(text).language(language) |> { builder =>
      contentRefOpt.foreach(builder.contentRef)
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
      assertEquals("Saved text contentRef", contentRefOpt, savedVO.getContentRef.asOption)
    }


  test("save new text") { language =>
    val text = saveNewText(language = language)
    val texts = textDao.getTexts(DocRef.of(Default.docId, Default.docVersionNo), language)

    assertEquals("Texts count in doc", 1, texts.size)
    assertTrue("Texts cotains saved text", texts.contains(text))
  }


  test("update existing text") { language =>
    val text = saveNewText(text="initial text", language = language)
    val updatedTextValue = "modified text"
    val updatedDocVersionNo = 1
    val updatedDocRef = DocRef.of(text.getDocRef.docId, updatedDocVersionNo)

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


  test("delete text") { () =>
    val contentRef = ContentRef.of(100, 1)
    val contentRefOpts = Seq(None, Some(contentRef))
    val nos = 0 until 5

    for (indexNo <- nos; contentRefOpt <- contentRefOpts; language <- mkLanguages)
      saveNewText(
        docId = Default.docId,
        docVersionNo = Default.docVersionNo,
        no = indexNo,
        contentRefOpt = contentRefOpt,
        language = language)

    for (language <- mkLanguages)
      expect(nos.size * contentRefOpts.size, "texts count inside and outside of content loop") {
        textDao.deleteTexts(DocRef.of(Default.docId, Default.docVersionNo), language)
      }
  }

  test("save text history") { language =>
    val text = saveNewText(language = language)
    val textHistory = new TextHistory(text, admin)

    textDao.saveTextHistory(textHistory)
  }


  test("get texts by doc ref") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5

    for (versionNo <- versionNos; indexNo <- nos; language <- mkLanguages)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, no = indexNo, language = language)

    for (versionNo <- versionNos) {
      val texts = textDao.getTexts(DocRef.of(Default.docId, versionNo))

      expect(nos.size * mkLanguages.size) { texts.size }
    }
  }


  test("get texts by doc ref, no and content ref") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5
    val contentRef = ContentRef.of(100, 1)
    val contentRefOpts = Seq(None, Some(contentRef))
    val languages = mkLanguages

    for (versionNo <- versionNos; no <- nos; contentRefOpt <- contentRefOpts; language <- languages)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, contentRefOpt = contentRefOpt,  no = no, language = language)

    for (versionNo <- versionNos; no <- nos; contentRefOpt <- contentRefOpts) {
      val texts = textDao.getTexts(DocRef.of(Default.docId, versionNo), no, contentRefOpt, createIfNotExists = false)

      assertEquals(languages.size, texts.size)
      assertTrue(texts.asScala.forall(_.getId != null))
    }

    for (versionNo <- versionNos; contentRefOpt <- contentRefOpts) {
      val texts = textDao.getTexts(DocRef.of(Default.docId, versionNo), 999, contentRefOpt, createIfNotExists = true)

      assertEquals(languages.size, texts.size)
      assertTrue(texts.asScala.forall(_.getId == null))
    }
  }


  test("get texts by doc ref and language") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5
    val contentRef = ContentRef.of(100, 1)
    val contentRefOpts = Seq(None, Some(contentRef))

    for (versionNo <- versionNos; no <- nos; contentRefOpt <- contentRefOpts; language <- mkLanguages)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, contentRefOpt = contentRefOpt,  no = no, language = language)


    for (versionNo <- versionNos; language <- mkLanguages) {
      val texts = textDao.getTexts(DocRef.of(Default.docId, versionNo), language).asScala

      expect(nos.size * contentRefOpts.size) { texts.size }

      expect(nos.size, "Texts count outsude content loop") {
        texts.count(_.getContentRef == null)
      }

      expect(nos.size, "Texts count inside content loop") {
        texts.count(text => text.getContentRef != null && text.getContentRef == contentRef)
      }
    }
  }


  test("get text by doc ref, no, language and content ref") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5
    val contentRef = ContentRef.of(100, 1)
    val contentRefOpts = Seq(None, Some(contentRef))

    for (versionNo <- versionNos; no <- nos; language <- mkLanguages; contentRefOpt <- contentRefOpts)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, no = no, language = language, contentRefOpt = contentRefOpt)

    for (versionNo <- versionNos; no <- nos; language <- mkLanguages; contentRefOpt <- contentRefOpts) {
      val text = textDao.getText(DocRef.of(Default.docId, versionNo), no, language, contentRefOpt)

      assertNotNull("Text exists", text)
    }
  }
}




@Import(Array(classOf[AbstractHibernateConfig]))
class TextDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def textDao = new TextDao

  @Bean(autowire = Autowire.BY_TYPE)
  def languageDao = new LanguageDao

  @Bean
  def hibernatePropertiesConfigurator: (org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration) =
    Function.chain(Seq(
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.BasicWithSql,
      Test.hibernate.configurators.addAnnotatedClasses(
        classOf[ContentLanguage],
        classOf[TextDomainObject],
        classOf[TextHistory]
      ),
      Test.hibernate.configurators.addXmlFiles(
        "com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
        "com/imcode/imcms/hbm/Text.hbm.xml"
      )
    ))
}