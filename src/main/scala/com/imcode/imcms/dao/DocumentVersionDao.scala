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
  def createVersion(docId: Int, userId: Int): DocumentVersion = synchronized {
    val no = getLatestVersion(docId) match {
      case null => 0
      case version => version.getNo.intValue + 1
    }

    hibernate.save(new DocumentVersion(docId, no, userId, new Date))
  }


  def getLatestVersion(docId: Int): DocumentVersion = hibernate.getByNamedQueryAndNamedParams(
    "DocumentVersion.getLatestVersion", "docId" -> docId
  )

  /**
   * Returns all versions for the document.
   *
   * @param docId meta id.
   * @return available versions for the document.
   */
  def getAllVersions (docId: Int): JList[DocumentVersion] = hibernate.listByNamedQueryAndNamedParams(
    "DocumentVersion.getByDocId", "docId" -> docId
  )


  def getVersion(docId: Int, no: Int): DocumentVersion = hibernate.getByNamedQueryAndNamedParams(
    "DocumentVersion.getByDocIdAndNo", "docId" -> docId, "no" -> no
  )


  def getDefaultVersion(docId: Int): DocumentVersion = hibernate.getByNamedQueryAndNamedParams(
    "DocumentVersion.getDefaultVersion", "docId" -> docId
  )


  def changeDefaultVersion(newDefaultVersion: DocumentVersion, publisher: UserDomainObject) {
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "DocumentVersion.changeDefaultVersion",

      "docId" -> newDefaultVersion.getDocId,
      "defaultVersionNo" -> newDefaultVersion.getNo,
      "publisherId" -> publisher.getId
    ) |> {
      case 0 => sys.error("Default document version can not be changed. Version %s does not exists.".format(newDefaultVersion))
      case _ =>
    }
  }
}
