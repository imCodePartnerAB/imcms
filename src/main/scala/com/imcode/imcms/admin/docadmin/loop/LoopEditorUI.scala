package com.imcode
package imcms
package admin.docadmin.loop

import java.util.Locale
import com.imcode.imcms.admin.docadmin.EditorContainerView
import com.imcode.imcms.mapping.TextDocumentContentSaver
import com.imcode.imcms.vaadin.component.dialog.ConfirmationDialog
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.server._


import com.imcode.imcms.vaadin.Current
import imcode.server.ImcmsConstants
import imcode.server.document.textdocument.TextDocumentDomainObject

@com.vaadin.annotations.Theme("imcms")
class LoopEditorUI extends UI with Log4jLogger with ImcmsServicesSupport {

  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)
    getLoadingIndicatorConfiguration.setSecondDelay(2)
    getLoadingIndicatorConfiguration.setThirdDelay(2)

    val docId = request.getParameter("meta_id").toInt
    val loopNo = request.getParameter("loop_no").toInt
    val contextPath = Current.contextPath
    val returnUrl = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption.getOrElse(
      s"$contextPath/servlet/AdminDoc?meta_id=$docId&flags=${ImcmsConstants.DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_TEXTS}"
    )

    val doc: TextDocumentDomainObject = imcmsServices.getDocumentMapper.getWorkingDocument(docId)
    val editor = new LoopEditor(doc.getRef, loopNo)
    val view = new EditorContainerView("Edit Loop")

    setContent(view)

    def close() {
      Current.page.setLocation(returnUrl)
    }

    def save(closeOnSuccess: Boolean = false) {
      editor.collectValues().right.get |> { loopContainer =>
        val saver = imcmsServices.getManagedBean(classOf[TextDocumentContentSaver])
        val loopContainer = editor.collectValues().right.get

        saver.saveLoop(loopContainer)
        imcmsServices.getDocumentMapper.invalidateDocument(docId)

        Current.page.showInfoNotification("loop_editor.notification.saved".i)

        if (closeOnSuccess) {
          close()
        }
      }
    }

    view.mainComponent = editor.view
    view.buttons.btnReset.addClickHandler { _ => editor.resetValues() }
    view.buttons.btnSave.addClickHandler { _ => save() }
    view.buttons.btnSaveAndClose.addClickHandler { _ => save(closeOnSuccess = true) }
    view.buttons.btnClose.addClickHandler {  _ =>
      close()
//      val editedMenu = editor.collectValues().right.get
//      if (editedMenu.getSortOrder == menu.getSortOrder && editedMenu.getMenuItems.deep == menu.getMenuItems.deep) {
//        close()
//      } else {
//        val dlg = new ConfirmationDialog(
//          "menu_editor_dlg.confirmation.close_without_saving.title".i,
//          "menu_editor_dlg.confirmation.close_without_saving.message".i
//        )
//
//        dlg.setOkButtonHandler {
//          close()
//        }
//
//        Current.ui.addWindow(dlg)
//      }
    }
  }
}