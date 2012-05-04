package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject
import imcms.util.Factory
import imcode.server.document.textdocument.ImageDomainObject
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfter, FunSuite, BeforeAndAfterAll}
import imcms.test.Project.{testDB}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Project
import com.imcode.imcms.api.{ImageHistory, I18nLanguage}

@RunWith(classOf[JUnitRunner])
class ImageDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfter {

  var imageDao: ImageDao = _
  var languageDao: LanguageDao = _

  val english = Factory.createLanguage(1, "en", "English")
  val swedish = Factory.createLanguage(2, "sv", "Swedish")

  val admin = new UserDomainObject(0)

  override def beforeAll() = testDB.recreate()

  before {
    val ctx = Project.spring.createCtx(classOf[ImageDaoSuiteConfig])

    imageDao = ctx.getBean(classOf[ImageDao])
    languageDao = ctx.getBean(classOf[LanguageDao])

    testDB.runScripts("src/test/resources/sql/image_dao.sql")
  }

  test("get text doc's images by no") {
    val images = imageDao.getImagesByIndex(1001, 0, 1, null, null, false)
    assertEquals(2, images.size)
  }


  test("get text doc's images by doc id and doc version no") {
    val images = imageDao.getImages(1001, 0)
    assertEquals(6, images.size)
  }

  test("get text doc's images by doc id, doc version no and language") {
    val images = imageDao.getImages(1001, 0, english.getId)
    assertEquals(3, images.size)
  }


  test("get text doc's image by doc id, doc version no, language and no") {
		val image = imageDao.getImage(english.getId().intValue, 1001, 0, 1, null, null)
    assertNotNull(image)
	}


	test("delete text doc's images in a given language") {
    val deletedCount = imageDao.deleteImages(1001, 0, english.getId)

    assertEquals(3, deletedCount)
	}


	test("save text doc image") {
    val image = Factory.createImage(1001, 0, english, 1000)

    imageDao.saveImage(image)
	}

	test("save text doc's image history") {
    val image = Factory.createImage(1001, 0, english, 1000)
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
      Project.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Project.hibernate.configurators.BasicWithSql,
      Project.hibernate.configurators.addAnnotatedClasses(
        classOf[I18nLanguage],
        classOf[ImageDomainObject],
        classOf[ImageHistory]
      ),
      Project.hibernate.configurators.addXmlFiles(
        "com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
        "com/imcode/imcms/hbm/Image.hbm.xml"
      )
    ))
}