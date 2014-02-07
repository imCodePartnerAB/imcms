package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.orm.DocVersion
import imcode.server.user.UserDomainObject
import org.springframework.transaction.annotation.Transactional


import java.util.Date
import com.imcode.imcms.dao.hibernate.HibernateSupport


@Transactional(rollbackFor = Array(classOf[Throwable]))
class DocVersionDao extends HibernateSupport {

  def getByDocIdAndNo(docId: Int, no: Int): DocVersion = hibernate.getByNamedQuery(
    "DocVersion.getByDocIdAndNo",
    "docId" -> docId,
    "no" -> no
  )

  /**
   * Creates and returns a new version of a document.
   * If document does not have version creates version with number 0 otherwise creates version with next version number.
   *
   * @return new document version.
   */
  def createVersion(docId: Int, userId: Int): DocVersion = synchronized {
    val no = getLatestVersion(docId) match {
      case null => 0
      case version => version.getNo.intValue + 1
    }

    hibernate.save(new DocVersion(docId, no, userId, new Date))
  }


  def getLatestVersion(docId: Int): DocVersion = hibernate.getByNamedQueryAndNamedParams(
    "DocVersion.getLatestVersion", "docId" -> docId
  )

  /**
   * Returns all versions for the document.
   *
   * @param docId meta id.
   * @return available versions for the document.
   */
  def getAllVersions (docId: Int): JList[DocVersion] = hibernate.listByNamedQueryAndNamedParams(
    "DocVersion.getByDocId", "docId" -> docId
  )


  def getVersion(docId: Int, no: Int): DocVersion = hibernate.getByNamedQueryAndNamedParams(
    "DocVersion.getByDocIdAndNo", "docId" -> docId, "no" -> no
  )


  def getDefaultVersion(docId: Int): DocVersion = hibernate.getByNamedQueryAndNamedParams(
    "DocVersion.getDefaultVersion", "docId" -> docId
  )


  def changeDefaultVersion(newDefaultVersion: DocVersion, publisher: UserDomainObject) {
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "DocVersion.changeDefaultVersion",

      "docId" -> newDefaultVersion.getDocId,
      "defaultVersionNo" -> newDefaultVersion.getNo,
      "publisherId" -> publisher.getId
    ) |> {
      case 0 => sys.error("Default document version can not be changed. Version %s does not exists.".format(newDefaultVersion))
      case _ =>
    }
  }
}
