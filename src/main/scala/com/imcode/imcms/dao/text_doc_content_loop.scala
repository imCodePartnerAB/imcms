package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.ContentLoop
import imcode.server.document.textdocument.DocRef

@Transactional(rollbackFor = Array(classOf[Throwable]))
class ContentLoopDao extends HibernateSupport {

  def getLoop(loopId: Long) = hibernate.get[ContentLoop](loopId)

  /**
   * Returns loop or null if loop can not be found.
   *
   * @param docId document id.
   * @param no loop no.
   *
   * @return loop or null if loop can not be found.
   */
  def getLoop(docRef: DocRef, no: Int): ContentLoop =
    hibernate.getByNamedQueryAndNamedParams(
      "ContentLoop.getByDocIdAndDocVersionNoAndNo", "docId" -> docRef.getDocId, "docVersionNo" -> docRef.getDocVersionNo, "no" -> no
    )


  /**
   * Returns document content loops.
   *
   * @param docId document id.
   *
   * @return document content loops.
   */
  def getLoops(docRef: DocRef): JList[ContentLoop] = hibernate.listByNamedQueryAndNamedParams(
   "ContentLoop.getByDocIdAndDocVersionNo", "docId" -> docRef.getDocId, "docVersionNo" -> docRef.getDocVersionNo
  )


  def getNextLoopNo(docRef: DocRef): Int = hibernate.getByQuery[JInteger](
      "select max(l.no) from ContentLoop l where l.docId = ? and l.docVersionNo = ?", docRef.getDocId, docRef.getDocVersionNo
    ) match {
      case null => 0
      case n => n.intValue + 1
    }


  /**
   * Saves content loop.
   *
   * @param loop content loop.
   * @return saved content loop.
   */
  def saveLoop(loop: ContentLoop) = loop.clone() |>> { loopClone =>
    hibernate.saveOrUpdate(loopClone)
    hibernate.flush()
  }


  def deleteLoops(docRef: DocRef) =
    getLoops(docRef).asScala.map(hibernate.delete).size


  def deleteLoop(loopId: Long) = getLoop(loopId) match {
    case null => false
    case loop =>
      hibernate.delete(loop)
      true
  }
}