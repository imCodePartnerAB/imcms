package com.imcode
package imcms.dao

import org.junit.Assert._
import scala.collection.JavaConversions._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test._
import imcms.test.Test.{db}
import imcms.test.fixtures.UserFX.{mkSuperAdmin}
import imcms.mapping.orm.{HtmlReference, UrlReference, FileReference}
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}
import imcms.api._
import org.scalatest._
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire

@RunWith(classOf[JUnitRunner])
class DocVersionDaoSpec extends WordSpec with BeforeAndAfterAll with BeforeAndAfter with GivenWhenThen {

  implicit object DocumentVersion extends Ordering[DocumentVersion] {
    def compare(v1: DocumentVersion, v2: DocumentVersion) = v1.getNo.intValue - v2.getNo.intValue
  }

  var versionDao: DocumentVersionDao = _

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[DocVersionDaoSuiteConfig])

    versionDao = ctx.getBean(classOf[DocumentVersionDao])

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


  "DocVersionDao.getVersion" should {
    "return version if it exists" in {
      getVersion(assertExists = true)
    }

    "return [null] if version does *not* exist" in {
      expect(null) {
        getVersion(1002, 0, assertExists = false)
      }
    }
  }


  "DocVersionDao.getLatestVersion" should {
    "return version with highest 'no' for existing document" in {
      val maxVersion = versionDao.getAllVersions(1001).max
      val latestVersion = versionDao.getLatestVersion(1001)

      assertNotNull(maxVersion)
      assertNotNull(latestVersion)

      assertEquals(latestVersion, maxVersion)
    }

    "return [null] if there is *no* version(s) for provided docId" in {
      assert(versionDao.getLatestVersion(1002) == null)
    }
  }


  "DocVersionDao.changeDefaultVersion" should {
    "change version when changing it to an existing version" in {
      given("default version is [0]")
      val version0 = getVersion(1001, 0)
      val version1 = getVersion(1001, 1)

      versionDao.getDefaultVersion(1001) |> { defaultVersion =>
        assertNotNull(defaultVersion)
        assert(version0 === defaultVersion)
      }

      when("change it from 0 to 1")
      versionDao.changeDefaultVersion(1001, 1, 0)

      then("default version should eq [version 1]")
      versionDao.getDefaultVersion(1001) |> { defaultVersion =>
        assert(defaultVersion != null)
        assert(version1 === defaultVersion)
      }
    }

    "throw an exception when changing it to non existing version" in {
      intercept[Exception] {
        versionDao.changeDefaultVersion(1002, 1, 0)
      }
    }
  }


  "DocVersionDao.getDefaultVersion" should {
    "return null when it (or a doc) does not exists" in {
      assert(versionDao.getDefaultVersion(1002) == null)
    }

    "return default version on existing document" in {
      val version0 = getVersion(1001, 0)
      val defaultVersion = versionDao.getDefaultVersion(1001)

      assert(version0 === defaultVersion)
    }
  }


  "DocVersionDao.createVersion" should {
    "create a new version with no [0] if there is no previous versions" in {
      assert(createVersion(1002).getNo === 0)
    }

    "create a new version with no [latest version no + 1] if there is/are existing version(s)" in {
      val latestVersionNo = versionDao.getAllVersions(1001).max.getNo.intValue
       assert(createVersion().getNo === (latestVersionNo + 1))
    }
  }

  "DocVersionDao.getAllVersions" should {
    "return all existing version sorted ascending by 'no'" in {
      val versions = versionDao.getAllVersions(1001)

      assert(versions.size === 3)
      assert(versions.max.getNo === 2)
    }

    "return empty collection if no version exist" in {
      assert(versionDao.getAllVersions(1002).isEmpty)
    }
  }
}


@Import(Array(classOf[AbstractHibernateConfig]))
class DocVersionDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def versionDao = new DocumentVersionDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.BasicWithSql,
      Test.hibernate.configurators.addAnnotatedClasses(
        classOf[Meta],
        classOf[I18nMeta],
        classOf[DocumentVersion],
        classOf[I18nLanguage],
        classOf[DocumentProperty],
        classOf[CategoryDomainObject],
        classOf[CategoryTypeDomainObject],
        classOf[FileReference],
        classOf[UrlReference],
        classOf[HtmlReference]
      ),
      Test.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/Document.hbm.xml")
    ))
}