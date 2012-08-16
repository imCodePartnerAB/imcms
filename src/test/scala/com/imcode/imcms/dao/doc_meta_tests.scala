package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.fixtures.UserFX.{mkSuperAdmin}
import org.scalatest.matchers.MustMatchers
import imcms.test.Test.{db}
import org.scalatest.{WordSpec, BeforeAndAfter, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.context.annotation.Bean._
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.Test
import com.imcode.imcms.api._
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}
import com.imcode.imcms.mapping.orm.{HtmlReference, UrlReference, FileReference}
import org.junit.Assert._
import java.lang.Integer
import com.google.common.collect.Maps
import java.util.{HashMap, HashSet, Date}
import scala.collection.JavaConverters._
import com.imcode.imcms.test.fixtures.{UserFX, DocFX}
import org.joda.time.DateTime
import imcode.server.document.textdocument.DocRef

@RunWith(classOf[JUnitRunner])
class MetaDaoTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

	var metaDao: MetaDao = _
  var versionDao: DocumentVersionDao = _

  override def beforeAll() = db.recreate()

  before {
    //db.runScripts()

    val ctx = Test.spring.createCtx(classOf[MetaDaoTestConfig])

    metaDao = ctx.getBean(classOf[MetaDao])
    versionDao = ctx.getBean(classOf[DocumentVersionDao])
  }

  def createMeta() = new Meta |>> { m =>
    m.setActivate(1)
    m.setDocumentType(2)
    m.setCreatorId(3)
    m.setRestrictedOneMorePrivilegedThanRestrictedTwo(true)
    m.setLinkableByOtherUsers(true)
    m.setLinkedForUnauthorizedUsers(true)
    m.setCreatedDatetime(new Date)
    m.setModifiedDatetime(new Date)
    m.setSearchDisabled(true)
    m.setTarget("_top")

    //m.setProperties(Map("p1" -> "v1", "p2" -> "v2", "p3" -> "v3"))
    m.setCategoryIds(Set[JInteger](0,1,2,3,4,5).asJava)
  } |> metaDao.saveMeta


  "MetaDao.getMeta" should {
    "return meta when it exists" in {
      val newMeta = createMeta()
      val meta = metaDao.getMeta(newMeta.getId)

      assertNotNull("meta exist", meta)
    }

    "return null when no meta with provided id exists" in {
      expect(null) {
        metaDao.getMeta(DocFX.VacantId)
      }
    }
  }


  "MetaDao.touch should update meta and version" in {
    val dt = new DateTime(2012, 12, 10, 13, 30).toDate;
    val meta = createMeta()
    val version = versionDao.createVersion(meta.getId, UserFX.mkSuperAdmin.getId)

    metaDao.touch(new DocRef(meta.getId, 0), UserFX.mkDefaultUser, dt)

    val updatedMeta = metaDao.getMeta(meta.getId)
    val updatedVersion = versionDao.getVersion(meta.getId, 0)

    assertEquals("meta modified datetime", dt, updatedMeta.getModifiedDatetime)
    assertEquals("version modified datetime", dt, updatedVersion.getModifiedDt)
    assertEquals("version modified by", UserFX.mkDefaultUser.getId, updatedVersion.getModifiedBy)
  }

}


@Import(Array(classOf[AbstractHibernateConfig]))
class MetaDaoTestConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def versionDao = new DocumentVersionDao

  @Bean(autowire = Autowire.BY_TYPE)
  def metaDao = new MetaDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.Basic,
      Test.hibernate.configurators.addAnnotatedClasses(
        classOf[DocumentProperty],
        classOf[Meta],
        classOf[I18nMeta],
        classOf[DocumentVersion],
        classOf[I18nLanguage],
        classOf[CategoryDomainObject],
        classOf[CategoryTypeDomainObject],
        classOf[FileReference],
        classOf[UrlReference],
        classOf[HtmlReference]
      ),
      Test.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/Document.hbm.xml")
    ))
}