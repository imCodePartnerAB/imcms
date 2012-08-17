package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfter, FunSuite, BeforeAndAfterAll}
import imcms.test.Test.{db}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Test
import com.imcode.imcms.api.{ImageHistory, I18nLanguage}
import com.imcode.imcms.test.fixtures.LanguageFX.{mkEnglish, mkSwedish}
import imcode.server.document.textdocument.{DocRef, ContentRef, ImageDomainObject}


@RunWith(classOf[JUnitRunner])
class ImageDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  var imageDao: ImageDao = _
  var languageDao: LanguageDao = _

  val admin = new UserDomainObject(0)

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[ImageDaoSuiteConfig])

    imageDao = ctx.getBean(classOf[ImageDao])
    languageDao = ctx.getBean(classOf[LanguageDao])

    db.runScripts("src/test/resources/sql/image_dao.sql")
  }

  test("get text doc's images by no outside of content loop") {
    val images = imageDao.getImages(new DocRef(1001, 0), 1, None, false)
    assertEquals(2, images.size)
  }

  test("get text doc's images by doc ref and no inside content loop") {
    val images = imageDao.getImages(new DocRef(1001, 0), 1, Some(new ContentRef(1, 1)), false)
    assertEquals(2, images.size)
  }

  test("get text doc's images by doc ref") {
    val images = imageDao.getImages(new DocRef(1001, 0))
    assertEquals(12, images.size)
  }

  test("get text doc's images by doc ref and language") {
    val images = imageDao.getImages(new DocRef(1001, 0), mkEnglish)
    assertEquals(6, images.size)
  }


  test("get text doc's image by doc ref, language and no") {
		val image = imageDao.getImage(new DocRef(1001, 0), 1, mkEnglish, None)
    assertNotNull(image)
	}


	test("delete text doc's images in a given language") {
    val deletedCount = imageDao.deleteImages(new DocRef(1001, 0), mkEnglish)

    assertEquals(6, deletedCount)
	}


	test("save text doc image") {
    val image = ImageDomainObject.builder().docRef(new DocRef(1001, 0)).language(mkEnglish).no(1000).build()

    imageDao.saveImage(image)
	}

	test("save text doc's image history") {
    val image = ImageDomainObject.builder().docRef(new DocRef(1001, 0)).language(mkEnglish).no(1000).build()
    val imageHistory = new ImageHistory(image, admin)

    imageDao.saveImageHistory(imageHistory)
	}
}


@Import(Array(classOf[AbstractHibernateConfig]))
class ImageDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def languageDao = new LanguageDao

  @Bean(autowire = Autowire.BY_TYPE)
  def imageDao = new ImageDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.BasicWithSql,
      Test.hibernate.configurators.addAnnotatedClasses(
        classOf[I18nLanguage],
        classOf[ImageDomainObject],
        classOf[ImageHistory]
      ),
      Test.hibernate.configurators.addXmlFiles(
        "com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
        "com/imcode/imcms/hbm/Image.hbm.xml"
      )
    ))
}