package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.container.DocRef
import com.imcode.imcms.mapping.jpa.VersionRepository
import com.imcode.imcms.mapping.DocumentCommonContent
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Include
import com.imcode.imcms.mapping.jpa.doc.content.{HtmlDocContent, FileDocItem, UrlDocContent}
import com.imcode.imcms.mapping.jpa.doc._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.TestSetup.db
import org.scalatest.{BeforeAndAfterEach, WordSpec, BeforeAndAfterAll}
import com.imcode.imcms.test.config.HibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.TestSetup
import com.imcode.imcms.api._
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}
import com.imcode.imcms.mapping.jpa._
import org.junit.Assert._
import java.util.Date
import scala.collection.JavaConverters._
import com.imcode.imcms.test.fixtures.{DocRefFX, UserFX, DocFX}
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class MetaDaoTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfterEach {

	var metaRepository: DocRepository = _
  var versionRepository: DocVersionRepository = _

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    //db.runScripts()

    val ctx = TestSetup.spring.createCtx(classOf[MetaDaoTestConfig])

    metaRepository = ctx.getBean(classOf[DocRepository])
    versionRepository = ctx.getBean(classOf[DocVersionRepository])
  }

//  def createMeta() = new DocMeta |>> { m =>
//    m.setDocumentType(2)
//    m.setCreatorId(3)
//    m.setRestrictedOneMorePrivilegedThanRestrictedTwo(true)
//    m.setLinkableByOtherUsers(true)
//    m.setLinkedForUnauthorizedUsers(true)
//    m.setCreatedDatetime(new Date)
//    m.setModifiedDatetime(new Date)
//    m.setSearchDisabled(true)
//    m.setTarget("_top")
//
//    //m.setProperties(Map("p1" -> "v1", "p2" -> "v2", "p3" -> "v3"))
//    m.setCategoryIds(Set[JInteger](0,1,2,3,4,5).asJava)
//  } |> metaRepository.saveMeta


  ".getMeta" should {
    "return meta when it exists" in {
      val newMeta = createMeta()
      val meta = metaRepository.findMeta(newMeta.getId)

      assertNotNull("meta exist", meta)
    }

    "return null when no meta with provided id exists" in {
      expectResult(null) {
        metaRepository.findMeta(DocFX.VacantId)
      }
    }
  }


  ".touch" should {
    "update meta and version" in {
      val dt = new DateTime(2012, 12, 10, 13, 30).toDate
      val meta = createMeta()
      val version = versionRepository.create(meta.getId, UserFX.mkSuperAdmin.getId)

      metaRepository.touch(DocRef.of(meta.getId, 0), UserFX.mkDefaultUser, dt)

      val updatedMeta = metaRepository.findMeta(meta.getId)
      val updatedVersion = versionRepository.findByDocIdAndNo(meta.getId, 0)

      assertEquals("meta modified datetime", dt, updatedMeta.getModifiedDatetime)
      assertEquals("version modified datetime", dt, updatedVersion.getModifiedDt)
      assertEquals("version modified by", UserFX.mkDefaultUser.getId, updatedVersion.getModifiedBy)
    }
  }

  ".deleteFileReferences" in {
    val docIdentity = DocRefFX.Default
    metaRepository.deleteFileReferences(docIdentity)
  }

  ".saveFileReferences" in {
    val docIdentity = DocRefFX.Default
    val fileRef = new FileDocItem |>> { ref =>
      ref.setDocRef(docIdentity)
    }

    metaRepository.saveFileReference(fileRef)
  }

  ".getFileReferences" in {
    val docIdentity = DocRefFX.Default
    metaRepository.getFileDocItems(docIdentity)
  }



}


@Import(Array(classOf[HibernateConfig]))
class MetaDaoTestConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def versionRepository = new DocVersionRepository

  @Bean(autowire = Autowire.BY_TYPE)
  def metaRepository = new DocRepository

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      TestSetup.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      TestSetup.hibernate.configurators.Basic,
      TestSetup.hibernate.configurators.addAnnotatedClasses(
        classOf[Property],
        classOf[Meta],
        classOf[DocumentCommonContent],
        classOf[DocVersion],
        classOf[Language],
        classOf[CategoryDomainObject],
        classOf[CategoryTypeDomainObject],
        classOf[FileDocItem],
        classOf[UrlDocContent],
        classOf[HtmlDocContent],
        classOf[Include]
      ),
      TestSetup.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/Document.hbm.xml")
    ))
}