package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject;
import org.springframework.transaction.annotation.Transactional;
import com.imcode.imcms.api.DocumentVersion;

import java.util.Date;

class DocumentVersionDao extends SpringHibernateTemplate {

  /**
   * Creates and returns a new version of a document.
   * If document does not have version creates version with number 0 otherwise creates version with next version number.
   *
   * @return new document version.
   */
  @Transactional
  def createVersion(docId: JInteger, userId: JInteger) = synchronized {
    val no = withSession {
      _.getNamedQuery("DocumentVersion.getLatestVersion")
       .setParameter("docId", docId)
       .uniqueResult().asInstanceOf[DocumentVersion]
    } match {
      case null => 0
      case version => version.getNo.intValue + 1
    }

    letret(new DocumentVersion(docId, no, userId, new Date)) { hibernateTemplate.save }
  }


  /**
   * Returns all versions for the document.
   *
   * @param docId meta id.
   * @return available versions for the document.
   */
  @Transactional
  def getAllVersions (docId: JInteger) = hibernateTemplate
    .findByNamedQueryAndNamedParam("DocumentVersion.getByDocId", "docId", docId).asInstanceOf[JList[DocumentVersion]]


  @Transactional
  def getDefaultVersion(docId: JInteger) = withSession {
    _.getNamedQuery("DocumentVersion.getDefaultVersion")
     .setParameter("docId", docId)
     .uniqueResult().asInstanceOf[DocumentVersion]
  }

  @Transactional
  def changeDefaultVersion(docId: JInteger, version: DocumentVersion, user: UserDomainObject) = withSession {
    _.getNamedQuery("DocumentVersion.changeDefaultVersion")
     .setParameter("defaultVersionNo", version.getNo)
     .setParameter("modifiedDatetime", version.getModifiedDt)
     .setParameter("publisherId", user.getId)
     .setParameter("docId", docId)
     .executeUpdate()
  }

  @Transactional
  def getVersion(docId: JInteger, no: JInteger) = withSession {
    _.getNamedQuery("DocumentVersion.getByDocIdAndNo")
     .setParameter("docId", docId)
     .setParameter("no", no)
     .uniqueResult().asInstanceOf[DocumentVersion]
  }
}
