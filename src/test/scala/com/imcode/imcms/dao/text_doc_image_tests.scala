package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.orm.{DocLanguage, TextDocImageHistory, DocRef}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.config.HibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.TestSetup
import com.imcode.imcms.test.fixtures.UserFX
import com.imcode.imcms.test.fixtures.LanguageFX.mkEnglish
import imcode.server.document.textdocument.{ContentLoopRef, ImageDomainObject}


@RunWith(classOf[JUnitRunner])
class ImageDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfterEach {

  var textDocDao: TextDocDao = _
  var languageDao: LanguageDao = _
  val admin = UserFX.mkSuperAdmin

  override def beforeAll() = TestSetup.db.recreate()

  override def beforeEach() {
    val ctx = TestSetup.spring.createCtx(classOf[ImageDaoSuiteConfig])

    textDocDao = ctx.getBean(classOf[TextDocDao])
    languageDao = ctx.getBean(classOf[LanguageDao])

    TestSetup.db.runScripts("src/test/resources/sql/image_dao.sql")
  }

  test("get text doc's images by no outside of content loop") {
    val images = textDocDao.getImages(DocRef.of(1001, 0), 1, None, false)
    assertEquals(2, images.size)
  }

  test("get text doc's images by doc ref and no inside content loop") {
    val images = textDocDao.getImages(DocRef.of(1001, 0), 1, Some(ContentLoopRef.of(1, 1)), false)
    assertEquals(2, images.size)
  }

  test("get text doc's images by doc ref") {
    val images = textDocDao.getImages(DocRef.of(1001, 0))
    assertEquals(12, images.size)
  }

  test("get text doc's images by doc ref and language") {
    val images = textDocDao.getImages(DocRef.of(1001, 0), mkEnglish)
    assertEquals(6, images.size)
  }


  test("get text doc's image by doc ref, language and no") {
		val image = textDocDao.getImage(DocRef.of(1001, 0), 1, mkEnglish, None)
    assertNotNull(image)
	}

	test("delete text doc's images in a given language") {
    val deletedCount = textDocDao.deleteImages(DocRef.of(1001, 0), mkEnglish)

    assertEquals(6, deletedCount)
	}

	test("save text doc image") {
    val image = ImageDomainObject.builder().docRef(DocRef.of(1001, 0)).language(mkEnglish).no(1000).build()

    textDocDao.saveImage(image)
	}

	test("save text doc's image history") {
    val image = ImageDomainObject.builder().docRef(DocRef.of(1001, 0)).language(mkEnglish).no(1000).build()
    val imageHistory = new TextDocImageHistory(image, admin)

    textDocDao.saveImageHistory(imageHistory)
	}
}


@Import(Array(classOf[HibernateConfig]))
class ImageDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def languageDao = new LanguageDao

  @Bean(autowire = Autowire.BY_TYPE)
  def textDocDao = new TextDocDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      TestSetup.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      TestSetup.hibernate.configurators.BasicWithSql,
      TestSetup.hibernate.configurators.addAnnotatedClasses(
        classOf[DocLanguage],
        classOf[ImageDomainObject],
        classOf[TextDocImageHistory]
      ),
      TestSetup.hibernate.configurators.addXmlFiles(
        "com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
        "com/imcode/imcms/hbm/Image.hbm.xml"
      )
    ))
}