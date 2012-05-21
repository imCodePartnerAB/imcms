package com.imcode
package imcms.dao

import imcms.test.Test.{db}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{WordSpec, BeforeAndAfter, BeforeAndAfterAll}
import org.scalatest.matchers.{MustMatchers}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Test
import com.imcode.imcms.api.{SystemProperty}

@RunWith(classOf[JUnitRunner])
class SystemPropertyDaoSpec extends WordSpec with MustMatchers with BeforeAndAfter with BeforeAndAfterAll {

  var systemDao: SystemDao = _

  val DEFAULT_LANGUAGE_ID = "DefaultLanguageId"
  val START_DOC = "startDocument"

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[SystemPropertyDaoSuiteConfig])

    systemDao = ctx.getBean(classOf[SystemDao])

    db.runScripts("src/test/resources/sql/system_property_dao.sql")
  }

  def getExistingProperty(name: String) = systemDao.getProperty(name) |>> { property =>
    property must not be (null)
  }


  "A SystemDao" should {
    "return all [2] existing properties" in {
      systemDao.getProperties() must have size (2)
    }

    "return existing property by name" in {
      val property = getExistingProperty(START_DOC)
      property.getName must be (START_DOC)
      property.getValue must be (1001.toString)
    }

    "update existing property" in {
      val property = getExistingProperty(DEFAULT_LANGUAGE_ID)
      property.setValue(0.toString)
      systemDao.saveProperty(property)

      val property2 = getExistingProperty(DEFAULT_LANGUAGE_ID)
      assert(property2.getValue() === 0.toString)

      property2.setValue(1.toString)
      systemDao.saveProperty(property2)

      val property3 = getExistingProperty(DEFAULT_LANGUAGE_ID)
      assert(property3.getValue() === 1.toString)
    }
  }
}


@Import(Array(classOf[AbstractHibernateConfig]))
class SystemPropertyDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def systemDao = new SystemDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.BasicWithSql,
      Test.hibernate.configurators.addAnnotatedClasses(classOf[SystemProperty])
    ))
}