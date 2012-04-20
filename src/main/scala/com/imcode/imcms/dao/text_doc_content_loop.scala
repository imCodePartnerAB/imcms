package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.ContentLoop

@Transactional(rollbackFor = Array(classOf[Throwable]))
class ContentLoopDao extends HibernateSupport {

  //@Transactional
  def getLoop(loopId: JLong) = hibernate.get[ContentLoop](loopId)

  /**
   * Returns loop or null if loop can not be found.
   *
   * @param docId document id.
   * @param no loop no.
   *
   * @return loop or null if loop can not be found.
   */
  //@Transactional
  def getLoop(docId: JInteger, docVersionNo: JInteger, no: JInteger) = hibernate.findByNamedQueryAndNamedParams[ContentLoop](
    "ContentLoop.getByDocIdAndDocVersionNoAndNo",
    "docId" -> docId,
    "docVersionNo" -> docVersionNo,
    "no" -> no
  )


  /**
   * Returns document content loops.
   *
   * @param docId document id.
   *
   * @return document content loops.
   */
  //@Transactional
  def getLoops(docId: JInteger, docVersionNo: JInteger) = hibernate.listByNamedQueryAndNamedParams[ContentLoop](
    "ContentLoop.getByDocIdAndDocVersionNo", "docId" -> docId, "docVersionNo" -> docVersionNo
  )


  /**
   * Saves content loop.
   *
   * @param loop content loop.
   * @return saved content loop.
   */
  //@Transactional
  def saveLoop(loop: ContentLoop) = loop.clone() |< { loopClone =>
    hibernate.saveOrUpdate(loopClone)
    hibernate.flush()
  }


  //@Transactional
  def deleteLoops(docId: JInteger, docVersionNo: JInteger) =
    getLoops(docId, docVersionNo).map(hibernate.delete).size


  //@Transactional
  def deleteLoop(loopId: JLong) = getLoop(loopId) match {
    case null => false
    case loop =>
      hibernate.delete(loop)
      true
  }
}