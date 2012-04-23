package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject;
import org.springframework.transaction.annotation.Transactional;
import com.imcode.imcms.api.DocumentVersion;

import java.util.Date;

@Transactional(rollbackFor = Array(classOf[Throwable]))
class DocumentVersionDao extends HibernateSupport {

  /**
   * Creates and returns a new version of a document.
   * If document does not have version creates version with number 0 otherwise creates version with next version number.
   *
   * @return new document version.
   */
  //@Transactional
  def createVersion(docId: JInteger, userId: JInteger): DocumentVersion = synchronized {
    val no = getLatestVersion(docId) match {
      case null => 0
      case version => version.getNo.intValue + 1
    }

    hibernate.save(new DocumentVersion(docId, no, userId, new Date))
  }

  //@Transactional
  def getLatestVersion(docId: JInteger) = hibernate.getByNamedQueryAndNamedParams[DocumentVersion](
    "DocumentVersion.getLatestVersion", "docId" -> docId
  )

  /**
   * Returns all versions for the document.
   *
   * @param docId meta id.
   * @return available versions for the document.
   */
  //@Transactional
  def getAllVersions (docId: JInteger) = hibernate.listByNamedQueryAndNamedParams[DocumentVersion](
    "DocumentVersion.getByDocId", "docId" -> docId
  )


  //@Transactional
  def getDefaultVersion(docId: JInteger) = hibernate.getByNamedQuery[DocumentVersion](
    "DocumentVersion.getDefaultVersion", "docId", docId
  )

  //@Transactional
  def changeDefaultVersion(docId: JInteger, version: DocumentVersion, publisher: UserDomainObject): Int =
    changeDefaultVersion(docId, version.getNo, publisher.getId)

  //@Transactional
  def changeDefaultVersion(docId: JInteger, no: JInteger, publisherId: JInteger) =
    getVersion(docId, no) |> { version =>
      require(version != null, "Version must exists")

      hibernate.bulkUpdateByNamedQuery(
        "DocumentVersion.changeDefaultVersion",

        "defaultVersionNo" -> no,
        "publisherId" -> publisherId,
        "docId", docId
      )
    }

  //@Transactional
  def getVersion(docId: JInteger, no: JInteger) = hibernate.getByNamedQueryAndNamedParams[DocumentVersion](
    "DocumentVersion.getByDocIdAndNo", "docId" -> docId
  )
}
