package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.ContentLoop

@Transactional(rollbackFor = Array(classOf[Throwable]))
class ContentLoopDao extends HibernateSupport {

  def getLoop(loopId: JLong): ContentLoop = hibernate.get(loopId)

  /**
   * Returns loop or null if loop can not be found.
   *
   * @param docId document id.
   * @param no loop no.
   *
   * @return loop or null if loop can not be found.
   */
  def getLoop(docId: JInteger, docVersionNo: JInteger, no: JInteger): ContentLoop =
    hibernate.getByNamedQueryAndNamedParams(
      "ContentLoop.getByDocIdAndDocVersionNoAndNo", "docId" -> docId, "docVersionNo" -> docVersionNo, "no" -> no
    )


  /**
   * Returns document content loops.
   *
   * @param docId document id.
   *
   * @return document content loops.
   */
  def getLoops(docId: JInteger, docVersionNo: JInteger): JList[ContentLoop] = hibernate.listByNamedQueryAndNamedParams(
    "ContentLoop.getByDocIdAndDocVersionNo", "docId" -> docId, "docVersionNo" -> docVersionNo
  )


  /**
   * Saves content loop.
   *
   * @param loop content loop.
   * @return saved content loop.
   */
  def saveLoop(loop: ContentLoop) = loop.clone() |< { loopClone =>
    hibernate.saveOrUpdate(loopClone)
    hibernate.flush()
  }


  def deleteLoops(docId: JInteger, docVersionNo: JInteger) =
    getLoops(docId, docVersionNo).map(hibernate.delete).size


  def deleteLoop(loopId: JLong) = getLoop(loopId) match {
    case null => false
    case loop =>
      hibernate.delete(loop)
      true
  }
}