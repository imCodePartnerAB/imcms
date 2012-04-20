package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import imcode.server.user.UserDomainObject
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import imcms.test._
import imcms.test.Base._
import imcms.test.fixtures.UserFX.{admin}
import org.springframework.orm.hibernate3.HibernateTemplate
import imcms.mapping.orm.{HtmlReference, UrlReference, FileReference}
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}
import imcms.api._
import org.scalatest._

@RunWith(classOf[JUnitRunner])
class DocVersionDaoSpec extends WordSpec with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach with GivenWhenThen {
  implicit object DocumentVersion extends Ordering[DocumentVersion] {
    def compare(v1: DocumentVersion, v2: DocumentVersion) = v1.getNo.intValue - v2.getNo.intValue
  }

  def set = afterWord("set")

  var versionDao: DocumentVersionDao = _

  override def beforeAll() = db.recreate()

  override def beforeEach() = withLogFailure {
    val sf = db.createHibernateSessionFactory(
      Seq(
        classOf[Meta],
        classOf[I18nMeta],
        classOf[FileReference],
        classOf[UrlReference],
        classOf[HtmlReference],
        classOf[CategoryDomainObject],
        classOf[CategoryTypeDomainObject],
        classOf[DocumentProperty],
        classOf[DocumentVersion],
        classOf[I18nLanguage]),
      "src/main/resources/com/imcode/imcms/hbm/Document.hbm.xml")

    versionDao = new DocumentVersionDao
    versionDao.hibernateTemplate = new HibernateTemplate(sf)

    db.runScripts("src/test/resources/sql/doc_version.sql")
  }

  def createVersion(docId: JInteger = 1001, userId: JInteger = admin.getId) =
    versionDao.createVersion(docId, userId) |> { savedVO =>
      savedVO.getId must not be (null)

      doto(getVersion(docId, savedVO.getNo)) { loadedVO =>
        loadedVO must have (
          'docId (docId),
          'createdBy (userId),
          'modifiedBy (userId)
        )
      }
    }

  def getVersion(docId: JInteger = 1001, no: JInteger = 0, assertExists: Boolean = true) =
    doto(versionDao.getVersion(docId, no)) { version =>
      if (assertExists) assert(version != null, "Version docId: %s, no: %s".format(docId, no))
    }


  "A DocVersionDao" should {
    "return version when it exists" in {
      getVersion()
    }

    "return [null] if version does *not* exists" in {
      getVersion(1002, 0, assertExists = false) must be (null)
    }

    "return latest version when it exists" in {
      val maxVersion = versionDao.getAllVersions(1001).max
      val latestVersion = versionDao.getLatestVersion(1001)

      maxVersion must not be (null)
      latestVersion must not be (null)

      latestVersion must equal (maxVersion)
    }

    "return [null] if there is *no* versions for provided docId" in {
      versionDao.getLatestVersion(1002) must be (null)
    }
  }

  "A DocVersionDao" when {
    "changing default version" that {
      "exists" should {
        "change it" in {
          given("default version eq [version 0]")
          val version0 = getVersion(1001, 0)
          val version1 = getVersion(1001, 1)

          versionDao.getDefaultVersion(1001) |> { defaultVersion =>
            defaultVersion must not be (null)
            assert(version0 === defaultVersion)
          }

          when("change it from 0 to 1")
          versionDao.changeDefaultVersion(1001, 1, 0)

          then("default version should eq [version 1]")
          versionDao.getDefaultVersion(1001) |> { defaultVersion =>
            defaultVersion must not be (null)
            assert(version1 === defaultVersion)
          }
        }
      }

      "does *not* exist" should {
        "throw an exception" in {
          intercept[Exception] {
            versionDao.changeDefaultVersion(1002, 1, 0)
          }
        }
      }
    }

    "getting default version" that {
      "does *not* exist" should {
        "return [null]" in {
          versionDao.getDefaultVersion(1002) must be (null)
        }
      }

      "is prevosly set for the doc" should {
        "return it" in {
          val version0 = getVersion(1001, 0)
          val defaultVersion = versionDao.getDefaultVersion(1001)

          assert(version0 === defaultVersion)
        }
      }
    }

    "creating a new version and there are *no* existing versions" should set {
      "its no to [0]" in {
        createVersion(1002).getNo must be (0)
      }
    }

    "creating a new version and there is/are existing version(s)" should set {
      "its no to [latest version no + 1]" in {
        val latestVersionNo = versionDao.getAllVersions(1001).max.getNo.intValue
        createVersion().getNo must be (latestVersionNo + 1)
      }
    }

    "getting all versions for doc" that {
      "has existing versions" should {
        "return all [3] of them" in {
          versionDao.getAllVersions(1001) must have size (3)
        }

        "have latest version no eq [2]" in {
          versionDao.getAllVersions(1001).max.getNo.intValue must be (2)
        }
      }

      "has *no* existing versions" should {
        "return an empty listByNamedParams" in {
          versionDao.getAllVersions(1002) must be ('empty)
        }
      }
    }
  }
}