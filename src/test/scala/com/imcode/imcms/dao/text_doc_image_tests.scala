package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject
import imcms.util.Factory
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
import imcode.server.document.textdocument.{ContentLoopRef, ImageDomainObject}


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
    val images = imageDao.getImagesByNo(1001, 0, 1, None, false)
    assertEquals(2, images.size)
  }

  test("get text doc's images by no inside content loop") {
    val images = imageDao.getImagesByNo(1001, 0, 1, Some(new ContentLoopRef(1, 1)), false)
    assertEquals(2, images.size)
  }

  test("get text doc's images by doc id and doc version no") {
    val images = imageDao.getImages(1001, 0)
    assertEquals(12, images.size)
  }

  test("get text doc's images by doc id, doc version no and language") {
    val images = imageDao.getImages(1001, 0, mkEnglish.getId)
    assertEquals(6, images.size)
  }


  test("get text doc's image by doc id, doc version no, language and no") {
		val image = imageDao.getImage(mkEnglish.getId, 1001, 0, 1, None)
    assertNotNull(image)
	}


	test("delete text doc's images in a given language") {
    val deletedCount = imageDao.deleteImages(1001, 0, mkEnglish.getId)

    assertEquals(6, deletedCount)
	}


	test("save text doc image") {
    val image = Factory.createImage(1001, 0, mkEnglish, 1000)

    imageDao.saveImage(image)
	}

	test("save text doc's image history") {
    val image = Factory.createImage(1001, 0, mkEnglish, 1000)
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