package com.imcode
package imcms.dao

import org.scalatest.BeforeAndAfterAll

import imcode.server.user.UserDomainObject
import imcms.util.Factory
import imcode.server.document.textdocument.ImageDomainObject
import imcms.api.{ImageHistory, I18nLanguage}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test.Base.{db}

@RunWith(classOf[JUnitRunner])
class ImageDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

  var imageDao: ImageDao = _
  var languageDao: LanguageDao = _

  val ENGLISH = Factory.createLanguage(1, "en", "English")
  val SWEDISH = Factory.createLanguage(2, "sv", "Swedish")

  val ADMIN = new UserDomainObject(0)

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val sf = db.createHibernateSessionFactory(Seq(classOf[I18nLanguage], classOf[ImageDomainObject], classOf[ImageHistory]),
               "src/main/resources/com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
               "src/main/resources/com/imcode/imcms/hbm/Image.hbm.xml")

    db.runScripts("src/test/resources/sql/image_dao.sql")

    imageDao = new ImageDao
    imageDao.setSessionFactory(sf)

    languageDao = new LanguageDao
    languageDao.setSessionFactory(sf)

    imageDao.setLanguageDao(languageDao)
  }

  test("get text doc's images by index") {
    val images = imageDao.getImagesByIndex(1001, 0, 0, null, null, false)
    assertEquals(2, images.size)
  }


  test("get text doc's images by doc id and doc version no") {
    val images = imageDao.getImages(1001, 0)
    assertEquals(6, images.size)
  }

  test("get text doc's images by doc id, doc version no and language") {
    val images = imageDao.getImages(1001, 0, ENGLISH.getId)
    assertEquals(3, images.size)
  }


  test("get text doc's image by doc id, doc version no, language and no") {
		val image = imageDao.getImage(ENGLISH.getId().intValue, 1001, 0, 0, null, null)
    assertNotNull(image)
	}


	test("delete text doc's images in a given language") {
    val deletedCount = imageDao.deleteImages(1001, 0, ENGLISH.getId)

    assertEquals(3, deletedCount)
	}


	test("save text doc image") {
    val image = Factory.createImage(1001, 0, ENGLISH, 1000)

    imageDao.saveImage(image)
	}

	test("save text doc's image history") {
    val image = Factory.createImage(1001, 0, ENGLISH, 1000)
    val imageHistory = new ImageHistory(image, ADMIN)

    imageDao.saveImageHistory(imageHistory)
	}
}