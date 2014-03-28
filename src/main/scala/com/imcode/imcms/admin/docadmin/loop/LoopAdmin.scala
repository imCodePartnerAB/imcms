package com.imcode
package imcms
package admin.docadmin.loop


import java.util.Locale
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.ImcmsServicesSupport


import com.imcode.imcms.vaadin.Current
import imcode.server.document.textdocument.TextDocumentDomainObject

@com.vaadin.annotations.Theme("imcms")
class LoopAdmin extends UI with Log4jLoggerSupport with ImcmsServicesSupport {

  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)

    val docId = request.getParameter("meta_id").toInt
    val loopNo = request.getParameter("loop_no").toInt
    val doc: TextDocumentDomainObject = imcmsServices.getDocumentMapper.getWorkingDocument(docId)

    val editor = new LoopEditor(doc.getVersionRef, loopNo)
  }
}