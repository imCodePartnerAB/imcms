package com.imcode
package imcms.dao

import org.junit.Assert._
import scala.collection.JavaConverters._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test._
import imcms.test.TestSetup.db
import imcms.test.fixtures.UserFX.mkSuperAdmin
import com.imcode.imcms.mapping.orm._
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}
import imcms.api._
import org.scalatest._
import com.imcode.imcms.test.config.HibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.fixtures.UserFX

@RunWith(classOf[JUnitRunner])
class DocVersionDaoSpec extends WordSpec with BeforeAndAfterAll with BeforeAndAfterEach with GivenWhenThen {

  implicit object OrderingDocumentVersion extends Ordering[DocVersion] {
    def compare(v1: DocVersion, v2: DocVersion) = v1.getNo.intValue - v2.getNo.intValue
  }

  var versionDao: DocVersionDao = _

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val ctx = TestSetup.spring.createCtx(classOf[DocVersionDaoSuiteConfig])

    versionDao = ctx.getBean(classOf[DocVersionDao])

    db.runScripts("src/test/resources/sql/doc_version.sql")
  }

  def createVersion(docId: Int = 1001, userId: Int = mkSuperAdmin.getId) =
    versionDao.createVersion(docId, userId) |> { savedVO =>
      assert(savedVO.getId != null)

      getVersion(docId, savedVO.getNo) |>> { loadedVO =>
        assert(loadedVO.getDocId === docId, "docId")
        assert(loadedVO.getCreatedBy === userId, "createdBy")
        assert(loadedVO.getModifiedBy === userId, "docId")
      }
    }

  def getVersion(docId: JInteger = 1001, no: JInteger = 0, assertExists: Boolean = true) =
    versionDao.getVersion(docId, no) |>> { version =>
      if (assertExists) assert(version != null, "Version docId: %s, no: %s".format(docId, no))
    }


  ".getVersion" should {
    "return version when the version exists" in {
      getVersion(assertExists = true)
    }

    "return [null] when the version does *not* exist" in {
      expectResult(null) {
        getVersion(1002, 0, assertExists = false)
      }
    }
  }


  ".getLatestVersion" should {
    "return version with highest 'no' for existing document" in {
      val maxVersion = versionDao.getAllVersions(1001).asScala.max
      val latestVersion = versionDao.getLatestVersion(1001)

      assertNotNull(maxVersion)
      assertNotNull(latestVersion)

      assertEquals(latestVersion, maxVersion)
    }

    "return [null] if there is *no* version(s) with provided docId" in {
      assert(versionDao.getLatestVersion(1002) == null)
    }
  }


  ".changeDefaultVersion" should {
    "change version when changing it to an existing version" in {
      Given("default version is [0]")
      val version0 = getVersion(1001, 0)
      val version1 = getVersion(1001, 1)

      versionDao.getDefaultVersion(1001) |> { defaultVersion =>
        assertNotNull(defaultVersion)
        assert(version0 === defaultVersion)
      }

      When("change it from 0 to 1")
      versionDao.changeDefaultVersion(version1, UserFX.mkSuperAdmin)

      Then("default version should eq [version 1]")
      versionDao.getDefaultVersion(1001) |> { defaultVersion =>
        assert(defaultVersion != null)
        assert(version1 === defaultVersion)
      }
    }

    "throw an exception when changing it to non existing version" in {
      val nonExistingVersion = getVersion(1001, 1).clone() |>> { _.setDocId(1002) }

      intercept[Exception] {
        versionDao.changeDefaultVersion(nonExistingVersion, UserFX.mkSuperAdmin)
      }
    }
  }


  ".getDefaultVersion" should {
    "return null when it (or a doc) does not exists" in {
      assert(versionDao.getDefaultVersion(1002) == null)
    }

    "return default version on existing document" in {
      val version0 = getVersion(1001, 0)
      val defaultVersion = versionDao.getDefaultVersion(1001)

      assert(version0 === defaultVersion)
    }
  }


  ".createVersion" should {
    "create a new version with no [0] if there is no previous versions" in {
      assert(createVersion(1002).getNo === 0)
    }

    "create a new version with no [latest version no + 1] if there is/are existing version(s)" in {
      val latestVersionNo = versionDao.getAllVersions(1001).asScala.max.getNo.intValue
       assert(createVersion().getNo === (latestVersionNo + 1))
    }
  }

  ".getAllVersions" should {
    "return all existing version sorted ascending by 'no'" in {
      val versions = versionDao.getAllVersions(1001)

      assert(versions.size === 3)
      assert(versions.asScala.max.getNo === 2)
    }

    "return empty list if no version exist" in {
      assert(versionDao.getAllVersions(1002).isEmpty)
    }
  }
}


@Import(Array(classOf[HibernateConfig]))
class DocVersionDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def versionDao = new DocVersionDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      TestSetup.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      TestSetup.hibernate.configurators.BasicWithSql,
      TestSetup.hibernate.configurators.addAnnotatedClasses(
        classOf[DocMeta],
        classOf[I18nMeta],
        classOf[DocVersion],
        classOf[DocLanguage],
        classOf[DocProperty],
        classOf[CategoryDomainObject],
        classOf[CategoryTypeDomainObject],
        classOf[FileDocItem],
        classOf[UrlDocContent],
        classOf[HtmlDocContent]
      ),
      TestSetup.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/Document.hbm.xml")
    ))
}