package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.orm.{DocLanguage, DocRef, TextDocTextHistory, I18nDocRef}
import scala.collection.JavaConverters._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.imcode.imcms.test.fixtures.LanguageFX.mkLanguages
import org.scalatest.{fixture, BeforeAndAfterEach, BeforeAndAfterAll}
import com.imcode.imcms.test.config.HibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.TestSetup

import imcode.server.document.textdocument.{ContentLoopRef, TextDomainObject}
import com.imcode.imcms.test.fixtures.UserFX

@RunWith(classOf[JUnitRunner])
class TextDaoSuite extends fixture.FunSuite with BeforeAndAfterAll with BeforeAndAfterEach {

  type FixtureParam = DocLanguage

	var textDao: TextDocDao = _

  val admin = UserFX.mkSuperAdmin

  override def beforeAll() = TestSetup.db.recreate()

  override def beforeEach() {
    val ctx = TestSetup.spring.createCtx(classOf[TextDaoSuiteConfig])

    textDao = ctx.getBean(classOf[TextDocDao])

    TestSetup.db.runScripts("src/test/resources/sql/text_dao.sql")
  }

  override def withFixture(test: OneArgTest) = {
    test.apply(mkLanguages.head)
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
      contentRefOpt: Option[ContentLoopRef] = None,
      no: Int = Default.no,
      text: String = Default.text,
      language: DocLanguage) =

    TextDomainObject.builder().i18nDocRef(I18nDocRef.of(docId, docVersionNo, language)).no(no).text(text) |> { builder =>
      contentRefOpt.foreach(builder.contentRef)
      builder.build()
    } |>> { vo =>
      textDao.saveText(vo)
      val id = vo.getId
      assertNotNull("Id has been assigned during text save", id)

      val savedVO = textDao.getTextById(id)
      assertNotNull("Saved text", savedVO)
      assertEquals("Saved text id", id, savedVO.getId)
      assertEquals("Saved text docI18nRef", I18nDocRef.of(docId, docVersionNo, language), savedVO.getI18nDocRef)
      assertEquals("Saved text no", no, savedVO.getNo)
      assertEquals("Saved text text", text, savedVO.getText)
      assertEquals("Saved text contentRef", contentRefOpt, savedVO.getContentLoopRef.asOption)
    }


  test("save new text") { language =>
    val text = saveNewText(language = language)
    val texts = textDao.getTexts(I18nDocRef.of(Default.docId, Default.docVersionNo, language))

    assertEquals("Texts count in doc", 1, texts.size)
    assertTrue("Texts contains saved text", texts.contains(text))
  }


  test("update existing text") { language =>
    val text = saveNewText(text="initial text", language = language)
    val updatedTextValue = "modified text"
    val updatedDocVersionNo = 1
    val updatedI18nDocRef = I18nDocRef.of(text.getI18nDocRef.docId, updatedDocVersionNo, language)

    text.clone |> { textToUpdate =>
      textToUpdate.setText(updatedTextValue)
      textToUpdate.setI18nDocRef(updatedI18nDocRef)

      textDao.saveText(textToUpdate)
    }

    val updatedText = textDao.getTextById(text.getId)
    assertNotNull("Updated text", updatedText)
    assertEquals("Updated text id", text.getId, updatedText.getId)
    assertEquals("Updated text i18nDocRef", updatedI18nDocRef, updatedText.getI18nDocRef)
    assertEquals("Updated text no", text.getNo, updatedText.getNo)
    assertEquals("Updated text text", updatedTextValue, updatedText.getText)
    assertEquals("Updated text contentRef", text.getContentRef, updatedText.getContentLoopRef)
  }


  test("delete text") { () =>
    val contentRef = ContentLoopRef.of(100, 1)
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
      expectResult(nos.size * contentRefOpts.size, "texts count inside and outside of content loop") {
        textDao.deleteTexts(I18nDocRef.of(Default.docId, Default.docVersionNo, language))
      }
  }

  test("save text history") { language =>
    val text = saveNewText(language = language)
    val textHistory = new TextDocTextHistory(text, admin)

    textDao.saveTextHistory(textHistory)
  }


  test("get texts by doc ref") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5

    for (versionNo <- versionNos; indexNo <- nos; language <- mkLanguages)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, no = indexNo, language = language)

    for (versionNo <- versionNos) {
      val texts = textDao.getTexts(DocRef.of(Default.docId, versionNo))

      expectResult(nos.size * mkLanguages.size) { texts.size }
    }
  }


  test("get texts by doc ref, no and content ref") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5
    val contentRef = ContentLoopRef.of(100, 1)
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
    val contentRef = ContentLoopRef.of(100, 1)
    val contentRefOpts = Seq(None, Some(contentRef))

    for (versionNo <- versionNos; no <- nos; contentRefOpt <- contentRefOpts; language <- mkLanguages)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, contentRefOpt = contentRefOpt,  no = no, language = language)


    for (versionNo <- versionNos; language <- mkLanguages) {
      val texts = textDao.getTexts(I18nDocRef.of(Default.docId, versionNo, language)).asScala

      expectResult(nos.size * contentRefOpts.size) { texts.size }

      expectResult(nos.size, "Texts count outsude content loop") {
        texts.count(_.getContentLoopRef == null)
      }

      expectResult(nos.size, "Texts count inside content loop") {
        texts.count(text => text.getContentLoopRef != null && text.getContentLoopRef == contentRef)
      }
    }
  }


  test("get text by doc ref, no, language and content ref") { () =>
    val versionNos = 0 until 2
    val nos = 0 until 5
    val contentRef = ContentLoopRef.of(100, 1)
    val contentRefOpts = Seq(None, Some(contentRef))

    for (versionNo <- versionNos; no <- nos; language <- mkLanguages; contentRefOpt <- contentRefOpts)
      saveNewText(docId = Default.docId, docVersionNo = versionNo, no = no, language = language, contentRefOpt = contentRefOpt)

    for (versionNo <- versionNos; no <- nos; language <- mkLanguages; contentRefOpt <- contentRefOpts) {
      val text = textDao.getText(I18nDocRef.of(Default.docId, versionNo, language), no, contentRefOpt)

      assertNotNull("Text exists", text)
    }
  }
}




@Import(Array(classOf[HibernateConfig]))
class TextDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def textDao = new TextDocDao

  @Bean(autowire = Autowire.BY_TYPE)
  def languageDao = new DocLanguageDao

  @Bean
  def hibernatePropertiesConfigurator: (org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration) =
    Function.chain(Seq(
      TestSetup.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      TestSetup.hibernate.configurators.BasicWithSql,
      TestSetup.hibernate.configurators.addAnnotatedClasses(
        classOf[DocLanguage],
        classOf[TextDomainObject],
        classOf[TextDocTextHistory]
      ),
      TestSetup.hibernate.configurators.addXmlFiles(
        "com/imcode/imcms/hbm/DocLanguage.hbm.xml",
        "com/imcode/imcms/hbm/TextDocText.hbm.xml"
      )
    ))
}