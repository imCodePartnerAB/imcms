package com.imcode
package imcms.dao

import imcms.mapping.orm.Include
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, WordSpec, BeforeAndAfterAll}
import org.scalatest.matchers.MustMatchers
import imcms.test.TestSetup.db
import com.imcode.imcms.test.config.HibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.TestSetup


@RunWith(classOf[JUnitRunner])
class IncludeDaoSpec extends WordSpec with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

	var metaDao: MetaDao = _

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val ctx = TestSetup.spring.createCtx(classOf[IncludeDaoSpecConfig])

    metaDao = ctx.getBean(classOf[MetaDao])

    db.runScripts("src/test/resources/sql/include_dao.sql")
  }


  "A MetaDao" should {
    "return all [3] existing text doc's includes" in {
      metaDao.getIncludes(1001) must have size (3)
    }

    "save new text doc's include" in {
      val include = new Include
      include.setDocId(1002)
      include.setIncludedDocumentId(1001)

      metaDao.saveInclude(include)

      include.getId must not be (null)
    }

    "delete all [3] existing text doc's includes" in {
      expectResult(3) {
        metaDao.deleteIncludes(1001)
      }

      metaDao.getIncludes(1001) must be ('empty)
    }
  }
}


@Import(Array(classOf[HibernateConfig]))
class IncludeDaoSpecConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def metaDao = new MetaDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      TestSetup.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      TestSetup.hibernate.configurators.BasicWithSql,
      TestSetup.hibernate.configurators.addAnnotatedClasses(classOf[Include])
    ))
}