package com.imcode
package imcms.dao

import imcms.mapping.orm.Include
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{WordSpec, BeforeAndAfter, BeforeAndAfterAll}
import org.scalatest.matchers.MustMatchers
import imcms.test.Project.{testDB}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Project


@RunWith(classOf[JUnitRunner])
class IncludeDaoSpec extends WordSpec with MustMatchers with BeforeAndAfterAll with BeforeAndAfter {

	var metaDao: MetaDao = _

  override def beforeAll() = testDB.recreate()

  before {
    val ctx = Project.spring.createCtx(classOf[IncludeDaoSpecConfig])

    metaDao = ctx.getBean(classOf[MetaDao])

    testDB.runScripts("src/test/resources/sql/include_dao.sql")
  }


  "A MetaDao" should {
    "return all [3] existing text doc's includes" in {
      metaDao.getIncludes(1001) must have size (3)
    }

    "save new text doc's include" in {
      val include = new Include
      include.setMetaId(1002)
      include.setIncludedDocumentId(1001)

      metaDao.saveInclude(include)

      include.getId must not be (null)
    }

    "delete all [3] existing text doc's includes" in {
      expect(3) {
        metaDao.deleteIncludes(1001)
      }

      metaDao.getIncludes(1001) must be ('empty)
    }
  }
}


@Import(Array(classOf[AbstractHibernateConfig]))
class IncludeDaoSpecConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def metaDao = new MetaDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Project.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Project.hibernate.configurators.BasicWithSql,
      Project.hibernate.configurators.addAnnotatedClasses(classOf[Include])
    ))
}