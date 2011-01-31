package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import imcode.server.user.UserDomainObject
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import imcms.test.Base._
import org.springframework.orm.hibernate3.HibernateTemplate
import org.scalatest.{WordSpec, BeforeAndAfterEach, BeforeAndAfterAll}
import imcms.mapping.orm.{HtmlReference, UrlReference, FileReference}
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}
import imcms.api._

@RunWith(classOf[JUnitRunner])
class DocVersionDaoSpec extends WordSpec with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {
  implicit object DocumentVersion extends Ordering[DocumentVersion] {
    def compare(v1: DocumentVersion, v2: DocumentVersion) = v1.getNo.intValue - v2.getNo.intValue
  }

  def set = afterWord("set")

  var versionDao: DocumentVersionDao = _

  val admin = new UserDomainObject(0)

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
    letret(versionDao.createVersion(docId, userId)) { savedVO =>
      savedVO.getId must not be (null)
      savedVO must have (
        'docId (docId),
        'createdBy (userId),
        'modifiedBy (userId)
      )
    }

  "A DocVersionDao" when {
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

        "have latest version no [2]" in {
          versionDao.getAllVersions(1001).max.getNo.intValue must be (2)
        }
      }

      "has *no* existing versions" should {
        "return an empty list" in {
          versionDao.getAllVersions(1002) must be ('empty)
        }
      }
    }
  }
}