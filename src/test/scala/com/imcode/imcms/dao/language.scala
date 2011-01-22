package com.imcode
package imcms.dao

import org.scalatest.junit.JUnitSuite
import org.scalatest.BeforeAndAfterAll
import org.junit.{Before, Test}

import com.imcode.imcms.test.DB
import com.imcode.imcms.test.Project
import org.hibernate.Session

import org.junit.Assert._
import org.springframework.orm.hibernate3.HibernateCallback
import imcms.api.{SystemProperty, I18nLanguage, Content}

class LanguageDaoSuite extends JUnitSuite with BeforeAndAfterAll {

  var systemDao: SystemDao = _
  var languageDao: LanguageDao = _

  override def beforeAll {
    val project = Project()
    val db = new DB(project)

    db.recreate();
  }

  @Before
  def resetDBData {
    val project = Project()
    val db = new DB(project)

    val sf = db.createHibernateSessionFactory(Seq(classOf[SystemProperty], classOf[I18nLanguage]),
              "src/main/resources/com/imcode/imcms/hbm/I18nLanguage.hbm.xml")

    db.runScripts("src/test/resources/sql/language_dao.sql")

    systemDao = new SystemDao
    languageDao = new LanguageDao
    systemDao.setSessionFactory(sf)
    languageDao.setSessionFactory(sf)
  }

  @Test
  def getAllLanguages {
    val languages = languageDao.getAllLanguages
    assertTrue("DB contains 2 languages.", languages.size == 2)
  }

  @Test
  def saveLanguage {
    val id = 3
    val code: String = "ee"

    assertNull("Language with id %d does not exists." format id, languageDao.getById(3))
    assertNull("Language with code %s does not exists." format code, languageDao.getByCode(code))

    var language = new I18nLanguage

    language.setCode(code)
    language.setName("Estonain")
    language.setNativeName("Eesti")
    language.setEnabled(true)
    languageDao.saveLanguage(language)
    assertNotNull("Language with id %d exists." format id, languageDao.getById(3))
    assertNotNull("Language with code %s exists." format code, languageDao.getByCode(code))
  }

  @Test
  def updateLanguage {
    var language = languageDao.getById(1)
    assertTrue("Language is enabled.", language.isEnabled.booleanValue)
    language.setEnabled(false)
    languageDao.saveLanguage(language)
    language = languageDao.getById(1)
    assertFalse("Language is disabled.", language.isEnabled.booleanValue)
  }

  @Test
  def getDefaultLanguage {
    val property = systemDao.getProperty("DefaultLanguageId")
    assertNotNull("DefaultLanguageId system property exists.", property)
    assertEquals("DefaultLanguageId system property is set to %d." format 1, new JInteger(1), property.getValueAsInteger)
    val language = languageDao.getById(property.getValueAsInteger)
    assertNotNull("Default language exists.", language)
  }

  @Test
  def getLanguageByCode {
    for (code <- Array("en", "sv"); language = languageDao.getByCode(code)) {
      assertNotNull("Language with code %s is exists." format code, language)
      assertEquals("Language code is correct.", code, language.getCode)
    }
    let("xx") { undefinedCode =>
      assertNull("Language with code %s does not exists." format undefinedCode, languageDao.getByCode(undefinedCode))
    }
  }

  @Test
  def getLanguageById {
    for (id <- Array(1, 2); language = languageDao.getById(id)) {
      assertNotNull("Language with id %d is exists." format id, language)
      assertEquals("Language id is correct.", id, language.getId)
    }
    assertNull("Language with id %d does not exists." format 3, languageDao.getById(3))
  }


  @Test
  def changeDefaultLanguage {
    val property = systemDao.getProperty("DefaultLanguageId")
    property.setValue("2")
    systemDao.saveProperty(property)
    val language = languageDao.getById(systemDao.getProperty("DefaultLanguageId").getValueAsInteger)
    assertEquals("Language id is correct.", language.getId, new Integer(2))
  }
}