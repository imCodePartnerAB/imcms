package com.imcode
package imcms.dao

import org.scalatest.junit.JUnitSuite
import org.scalatest.BeforeAndAfterAll
import org.junit.{Before, Test}

import com.imcode.imcms.test.DB
import com.imcode.imcms.test.Project
import org.hibernate.Session

import org.junit.Assert._
import imcode.server.user.UserDomainObject
import imcms.util.Factory
import imcode.server.document.textdocument.ImageDomainObject
import imcms.api.{ImageHistory, I18nLanguage, Content}

class ImageDaoSuite extends JUnitSuite with BeforeAndAfterAll {

  var imageDao: ImageDao = _

  var languageDao: LanguageDao = _

  val ENGLISH = Factory.createLanguage(1, "en", "English")

  val SWEDISH = Factory.createLanguage(2, "sv", "Swedish")

  val ADMIN = new UserDomainObject(0)


  override def beforeAll {
    val project = Project()
    val db = new DB(project)

    db.recreate()
  }


  @Before
  def resetDBData() {
    val project = Project()
    val db = new DB(project)

    val sf = db.createHibernateSessionFactory(Seq(classOf[I18nLanguage], classOf[ImageDomainObject], classOf[ImageHistory]),
              "src/main/resources/com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
              "src/main/resources/com/imcode/imcms/hbm/Image.hbm.xml")

    db.runScripts("src/test/resources/sql/image_dao.sql")

    imageDao = new ImageDao()
    imageDao.setSessionFactory(sf)

    languageDao = new LanguageDao()
    languageDao.setSessionFactory(sf)

    imageDao.setLanguageDao(languageDao)
  }

  @Test
	def testGetImagesByIndex() {
    val images = imageDao.getImagesByIndex(1001, 0, 0, null, null, false)
    assertEquals(2, images.size)
  }


  @Test
	def getImagesByDocIdAndDocVersionNo() {
    val images = imageDao.getImages(1001, 0)
    assertEquals(6, images.size)
  }

  @Test
	def getImagesByDocIdAndDocVersionNoAndLanguage() {
    val images = imageDao.getImages(1001, 0, ENGLISH.getId)
    assertEquals(3, images.size)
  }


  @Test
	def getImageByDocIdAndDocVersionNoAndNoAndLanguage() {
		val image = imageDao.getImage(ENGLISH.getId().intValue, 1001, 0, 0, null, null)
    assertNotNull(image)
	}


	@Test
	def deleteImages() {
    val deletedCount = imageDao.deleteImages(1001, 0, ENGLISH.getId)

    assertEquals(3, deletedCount)
	}


	@Test
	def saveImage() {
    val image = Factory.createImage(1001, 0, ENGLISH, 1000)

    imageDao.saveImage(image)
	}

	@Test
	def saveImageHistory() {
    val image = Factory.createImage(1001, 0, ENGLISH, 1000)
    val imageHistory = new ImageHistory(image, ADMIN)

    imageDao.saveImageHistory(imageHistory)
	}
}