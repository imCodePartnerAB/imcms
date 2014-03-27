package com.imcode
package imcms
package admin.docadmin.menu


import com.imcode.imcms.admin.docadmin.EditorContainerView
import com.imcode.imcms.mapping.container.TextDocMenuContainer
import java.util.Locale
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.component.dialog.ConfirmationDialog
import com.imcode.imcms.ImcmsServicesSupport

import com.imcode.imcms.vaadin.Current
import imcode.server.ImcmsConstants

@com.vaadin.annotations.Theme("imcms")
class MenuAdmin extends UI with Log4jLoggerSupport with ImcmsServicesSupport {

  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)

    val contextPath = Current.contextPath
    val docId = request.getParameter("meta_id").toInt
    val menuNo = request.getParameter("menu_no")
    val title = request.getParameter("label").trimToOption.getOrElse("menu_editor.title".f(docId, menuNo))
    val returnUrl = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption.getOrElse(
      s"$contextPath/servlet/AdminDoc?meta_id=$docId&flags=${ImcmsConstants.DISPATCH_FLAG__EDIT_MENU}&editmenu=$menuNo"
    )

    val doc = imcmsServices.getDocumentMapper.getWorkingDocument(docId)
    val menu = doc.getMenu(menuNo)
    val editor = new MenuEditor(doc, menu) |>> {
      _.view.setSize(900, 600)
    }
    val view = new EditorContainerView(title)

    def close() {
      Current.page.open(returnUrl, "_self")
    }

    def save(close: Boolean) {
      editor.collectValues().right.get |> {
        menu =>
          imcmsServices.getDocumentMapper.saveTextDocMenu(TextDocMenuContainer.of(doc.getVersionRef, menuNo, menu), Current.imcmsUser)
          Current.page.showInfoNotification("menu_editor.notification.saved".i)

          if (close) {
            Current.page.open(params.returnUrl, "_self")
          }
      }
    }

    view.mainComponent = editor.view
    view.buttons.btnReset.addClickHandler {
      _ => editor.resetValues()
    }
    view.buttons.btnSave.addClickHandler {
      _ => save(close = false)
    }
    view.buttons.btnSaveAndClose.addClickHandler {
      _ => save(close = true)
    }
    view.buttons.btnClose.addClickHandler {
      _ =>
        val editedMenu = editor.collectValues().right.get
        if (editedMenu.getSortOrder == menu.getSortOrder && editedMenu.getMenuItems.deep == menu.getMenuItems.deep) {
          close()
        } else {
          val dlg = new ConfirmationDialog(
            "menu_editor_dlg.confirmation.close_without_saving.title".i,
            "menu_editor_dlg.confirmation.close_without_saving.message".i
          )

          dlg.setOkButtonHandler {
            closeEditor()
            dlg.close()
          }

          Current.ui.addWindow(dlg)
        }
    }
  }
}