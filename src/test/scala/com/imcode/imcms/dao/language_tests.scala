package com.imcode
package imcms.dao

import imcms.api.{SystemProperty, I18nLanguage}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.Test.{db}
import org.scalatest.{BeforeAndAfter, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.config.{AbstractHibernateConfig}
import com.imcode.imcms.test.{Test}
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.{Autowire}


@RunWith(classOf[JUnitRunner])
class LanguageDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  var systemDao: SystemDao = _
  var languageDao: LanguageDao = _

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[LanguageDaoSuiteConfig])

    systemDao = ctx.getBean(classOf[SystemDao])
    languageDao = ctx.getBean(classOf[LanguageDao])

    db.runScripts("src/test/resources/sql/language_dao.sql")
  }


  test("get all [2] languages") {
    val languages = languageDao.getAllLanguages
    assertTrue("DB contains 2 languages.", languages.size == 2)
  }

  test("save new language") {
    val id = 3
    val code: String = "ee"

    assertNull("Language with id %d does not exists." format id, languageDao.getById(3))
    assertNull("Language with code %s does not exists." format code, languageDao.getByCode(code))

    val builder = new I18nLanguage.Builder

    builder.code(code)
    builder.name("Estonain")
    builder.nativeName("Eesti")
    builder.enabled(true)
    val language = languageDao.saveLanguage(builder.build())
    assertNotNull("Language with id %d exists." format id, languageDao.getById(3))
    assertNotNull("Language with code %s exists." format code, languageDao.getByCode(code))
  }

  test("update existing language") {
    val language = languageDao.getById(1)
    assertTrue("Language is enabled.", language.isEnabled.booleanValue)

    val updatedLanguage = new I18nLanguage.Builder(language).enabled(false).build()
    languageDao.saveLanguage(updatedLanguage)

    val languageFromDb = languageDao.getById(1)
    assertFalse("Language is disabled.", languageFromDb.isEnabled)
  }

  test("get defailt language") {
    val property = systemDao.getProperty("DefaultLanguageId")
    assertNotNull("DefaultLanguageId system property exists.", property)
    assertEquals("DefaultLanguageId system property is set to %d." format 1, new JInteger(1), property.getValueAsInteger)
    val language = languageDao.getById(property.getValueAsInteger)
    assertNotNull("Default language exists.", language)
  }

  test("get existing language by code") {
    for (code <- Array("en", "sv"); language = languageDao.getByCode(code)) {
      assertNotNull("Language with code %s is exists." format code, language)
      assertEquals("Language code is correct.", code, language.getCode)
    }

    "xx" |> { undefinedCode =>
      assertNull("Language with code %s does not exists." format undefinedCode, languageDao.getByCode(undefinedCode))
    }
  }

  test("get existing language by id") {
    for (id <- Array(1, 2); language = languageDao.getById(id)) {
      assertNotNull("Language with id %d is exists." format id, language)
      assertEquals("Language id is correct.", id, language.getId)
    }
    assertNull("Language with id %d does not exists." format 3, languageDao.getById(3))
  }

  test("change default language") {
    val property = systemDao.getProperty("DefaultLanguageId")
    property.setValue("2")
    systemDao.saveProperty(property)
    val language = languageDao.getById(systemDao.getProperty("DefaultLanguageId").getValueAsInteger)
    assertEquals("Language id is correct.", language.getId, 2)
  }
}


@Import(Array(classOf[AbstractHibernateConfig]))
class LanguageDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def languageDao = new LanguageDao

  @Bean(autowire = Autowire.BY_TYPE)
  def systemDao = new SystemDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.BasicWithSql,
      Test.hibernate.configurators.addAnnotatedClasses(classOf[SystemProperty], classOf[I18nLanguage]),
      Test.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/I18nLanguage.hbm.xml")
    ))
}
