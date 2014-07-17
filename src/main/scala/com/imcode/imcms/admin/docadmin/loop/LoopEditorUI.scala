package com.imcode
package imcms
package admin.docadmin.loop


import java.util.Locale
import com.imcode.imcms.admin.docadmin.EditorContainerView
import com.imcode.imcms.mapping.TextDocumentContentSaver
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.vaadin.component._


import com.imcode.imcms.vaadin.Current
import imcode.server.document.textdocument.TextDocumentDomainObject

@com.vaadin.annotations.Theme("imcms")
class LoopEditorUI extends UI with Log4jLogger with ImcmsServicesSupport {

  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)

    val docId = request.getParameter("meta_id").toInt
    val loopNo = request.getParameter("loop_no").toInt
    val doc: TextDocumentDomainObject = imcmsServices.getDocumentMapper.getWorkingDocument(docId)

    val editor = new LoopEditor(doc.getVersionRef, loopNo)

    val view = new EditorContainerView("Edit Loop")
    view.mainComponent = editor.view

    setContent(view)

    view.buttons.btnSave.addClickHandler { e =>
      val saver = imcmsServices.getManagedBean(classOf[TextDocumentContentSaver])
      val loopContainer = editor.collectValues().right.get

      saver.saveLoop(loopContainer)
    }
  }
}