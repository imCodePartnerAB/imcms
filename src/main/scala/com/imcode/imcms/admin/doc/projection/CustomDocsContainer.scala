package com.imcode
package imcms
package admin.doc.projection

import scala.PartialFunction._
import scala.collection.JavaConverters._
import _root_.imcode.server.user.UserDomainObject

/**
 * Provides access to fully customizable set of docs.
 */
class CustomDocsContainer extends FilterableDocsContainer {

  private var docIds = Seq.empty[DocId]

  private var filteredDocIds = Seq.empty[DocId]

  def idRange = condOpt(docIds) { case ids if ids.nonEmpty => (ids.min, ids.max) }

  protected def innerFilter(solrQuery: Option[String], user: UserDomainObject) {
    filteredDocIds = docIds
  }

  def removeItem(itemId: AnyRef) = ??? //itemId.asInstanceOf[DocId] |> { docId => docIds ... }

  def addItem(itemId: AnyRef) = new DocItem(itemId.asInstanceOf[DocId]) |>> { docItem =>
    docIds :+= docItem.docId
  }

  def removeAllItems() = true |>> { _ =>
    docIds = Seq.empty
    notifyItemSetChanged()
  }

  def getItemIds = filteredDocIds.asJava
}
