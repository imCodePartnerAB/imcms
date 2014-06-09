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
import imcode.server.document.textdocument.TextDocumentDomainObject

@com.vaadin.annotations.Theme("imcms")
class MenuEditorUI extends UI with Log4jLoggerSupport with ImcmsServicesSupport {

  // fixme: check security - see v4 EditMenu servlet
  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)
    getLoadingIndicatorConfiguration.setSecondDelay(2)
    getLoadingIndicatorConfiguration.setThirdDelay(3)

    val contextPath = Current.contextPath
    val docId = request.getParameter("docId").toInt
    val doc: TextDocumentDomainObject = imcmsServices.getDocumentMapper.getWorkingDocument(docId)
    val menuNo = request.getParameter("menuNo").toInt
    val title = request.getParameter("label").trimToOption.getOrElse("menu_editor.title".f(docId.toString, menuNo.toString))
    val returnUrl = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption.getOrElse(
      s"$contextPath/servlet/AdminDoc?meta_id=$docId&flags=${ImcmsConstants.DISPATCH_FLAG__EDIT_MENU}&editmenu=$menuNo"
    )

    val menu = doc.getMenu(menuNo)
    val editor = new MenuEditor(doc, menu)
    val view = new EditorContainerView(title)

    def close() {
      Current.page.setLocation(returnUrl)
    }

    def save(close: Boolean) {
      editor.collectValues().right.get |> {
        menu =>
          imcmsServices.getDocumentMapper.saveTextDocMenu(TextDocMenuContainer.of(doc.getVersionRef, menuNo, menu), Current.imcmsUser)
          Current.page.showInfoNotification("menu_editor.notification.saved".i)

          if (close) {
            Current.page.setLocation(returnUrl)
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
            close()
          }

          Current.ui.addWindow(dlg)
        }
    }

    view.setSize(800, 700)
    setContent(view)

//    Current.page.getJavaScript.addFunction("editDocProperties", new JavaScriptFunction {
//      override def call(arguments: JSONArray) {
//        //arguments.get(0)
//        (imcmsServices.getDocumentMapper.getWorkingDocument(docId): DocumentDomainObject) match {
//          case null => Current.page.showInfoNotification("Not found")
//          case doc =>
//            val dialog = new DocEditorDialog(s"Edit document ${doc.getId}".i, doc)
//
//            Dialog.bind(dialog) { case (editedDoc, i18nMetas) =>
//              imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.asJava, Current.imcmsUser)
//              Current.page.showInfoNotification("Document has been saved".i)
//              Current.page.open(returnUrl, "_self")
//            }
//
//            dialog.btnCancel.addClickHandler { _ =>
//              Current.page.open(returnUrl, "_self")
//            }
//
//            dialog.show()
//        }
//
//      }
//    })
  }
}