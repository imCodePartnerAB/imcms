package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.dao.{SystemDao, DocLanguageDao}
import com.imcode.imcms.mapping.orm.{DocLanguage, SystemProperty}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.TestSetup.db
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.config.HibernateConfig
import com.imcode.imcms.test.TestSetup
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire


@RunWith(classOf[JUnitRunner])
class LanguageDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfterEach {

  var systemDao: SystemDao = _
  var languageDao: DocLanguageDao = _

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val ctx = TestSetup.spring.createCtx(classOf[LanguageDaoSuiteConfig])

    systemDao = ctx.getBean(classOf[SystemDao])
    languageDao = ctx.getBean(classOf[DocLanguageDao])

    db.runScripts("src/test/resources/sql/language_dao.sql")
  }


  test("get all [2] languages") {
    val languages = languageDao.findAll
    assertTrue("DB contains 2 languages.", languages.size == 2)
  }

  test("save new language") {
    val id = 3
    val code: String = "ee"

    assertNull("Language with id %d does not exists." format id, languageDao.findOne(3))
    assertNull("Language with code %s does not exists." format code, languageDao.getByCode(code))

    val builder = DocLanguage.builder()

    builder.code(code)
    builder.name("Estonain")
    builder.nativeName("Eesti")
    builder.enabled(true)
    val language = languageDao.save(builder.build())
    assertNotNull("Language with id %d exists." format id, languageDao.findOne(3))
    assertNotNull("Language with code %s exists." format code, languageDao.getByCode(code))
  }

  test("update existing language") {
    val language = languageDao.findOne(1)
    assertTrue("Language is enabled.", language.isEnabled.booleanValue)

    val updatedLanguage = DocLanguage.builder(language).enabled(false).build()
    languageDao.save(updatedLanguage)

    val languageFromDb = languageDao.findOne(1)
    assertFalse("Language is disabled.", languageFromDb.isEnabled)
  }

  test("get defailt language") {
    val property = systemDao.getProperty("DefaultLanguageId")
    assertNotNull("DefaultLanguageId system property exists.", property)
    assertEquals("DefaultLanguageId system property is set to %d." format 1, new JInteger(1), property.getValueAsInteger)
    val language = languageDao.findOne(property.getValueAsInteger)
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
    for (id <- Array(1, 2); language = languageDao.findOne(id)) {
      assertNotNull("Language with id %d is exists." format id, language)
      assertEquals("Language id is correct.", id, language.getId)
    }
    assertNull("Language with id %d does not exists." format 3, languageDao.findOne(3))
  }

  test("change default language") {
    val property = systemDao.getProperty("DefaultLanguageId")
    property.setValue("2")
    systemDao.saveProperty(property)
    val language = languageDao.findOne(systemDao.getProperty("DefaultLanguageId").getValueAsInteger)
    assertEquals("Language id is correct.", language.getId, 2)
  }
}


@Import(Array(classOf[HibernateConfig]))
class LanguageDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def languageDao = new DocLanguageDao

  @Bean(autowire = Autowire.BY_TYPE)
  def systemDao = new SystemDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      TestSetup.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      TestSetup.hibernate.configurators.BasicWithSql,
      TestSetup.hibernate.configurators.addAnnotatedClasses(classOf[SystemProperty], classOf[DocLanguage]),
      TestSetup.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/DocLanguage.hbm.xml")
    ))
}
