package com.imcode
package imcms.admin.doc

import com.imcode.imcms._

import com.imcode.imcms.vaadin.ui._
import com.vaadin.ui._
import com.vaadin.server.Page
import imcode.server.document.DocumentDomainObject

object DocOpener extends ImcmsServicesSupport {

//  def openDoc(doc: DocumentDomainObject, target: String = "_blank") {
//    openDoc(doc.getId, target)
//  }

  def openDoc(metaId: MetaId, target: String = "_blank") {
    val url = UI.getCurrent.imcmsDocUrl(metaId)

    Page.getCurrent.getJavaScript.execute(s"window.open('$url', '$target')")
  }
}