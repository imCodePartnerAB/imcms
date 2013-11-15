package com.imcode
package imcms.dao

import imcms.mapping.orm.TemplateNames
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test.TestSetup.db
import com.imcode.imcms.test.TestSetup
import com.imcode.imcms.test.config.HibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire

@RunWith(classOf[JUnitRunner])
//todo: Test named queries
class TemplateNamesDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

	var metaDao: MetaDao = _

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val ctx = TestSetup.spring.createCtx(classOf[TemplateNamesDaoSuiteConfig])

    metaDao = ctx.getBean(classOf[MetaDao])

    db.runScripts("src/test/resources/sql/template_names_dao.sql")
  }


  test("get non exiting template names") {
    val tns = metaDao.getTemplateNames(10001)

    assertNull(tns)
  }

  test("get existing template names") {
    val tns = metaDao.getTemplateNames(1001)

    assertNotNull(tns)
  }

  test("update existing template names") {
    var tns = metaDao.getTemplateNames(1001)
    assertNotNull(tns);

    tns.setTemplateName("UPDATED")
    metaDao.saveTemplateNames(tns)

    tns = metaDao.getTemplateNames(1001)
    assertEquals("UPDATED", tns.getTemplateName())
  }

  test("delete existing template names") {
    var tns = metaDao.getTemplateNames(1001)
    assertNotNull(tns)

    metaDao.deleteTemplateNames(1001)

    tns = metaDao.getTemplateNames(1001)
    assertNull(tns)
  }


  test("create and save template names") {
    var tns = metaDao.getTemplateNames(1001)
    assertNotNull(tns)

    metaDao.deleteTemplateNames(1001)
    val tns2 = metaDao.getTemplateNames(1001)
    assertNull(tns2)

    metaDao.saveTemplateNames(tns)
    tns = metaDao.getTemplateNames(1001)
    assertNotNull(tns)
  }
}

@Import(Array(classOf[HibernateConfig]))
class TemplateNamesDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def metaDao = new MetaDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      TestSetup.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      TestSetup.hibernate.configurators.BasicWithSql,
      TestSetup.hibernate.configurators.addAnnotatedClasses(classOf[TemplateNames])
    ))
}