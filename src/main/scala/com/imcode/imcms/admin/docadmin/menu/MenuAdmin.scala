package com.imcode
package imcms
package admin.docadmin.menu


import _root_.java.nio.charset.StandardCharsets
import com.imcode.imcms.admin.doc.DocEditorDialog
import com.imcode.imcms.admin.docadmin.EditorContainerView
import com.imcode.imcms.mapping.container.TextDocMenuContainer
import java.util.Locale
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.component.dialog.{Dialog, ConfirmationDialog}
import com.imcode.imcms.ImcmsServicesSupport

import com.imcode.imcms.vaadin.Current
import imcode.server.ImcmsConstants
import imcode.server.document.textdocument.TextDocumentDomainObject
import org.apache.http.client.utils.URLEncodedUtils
import org.json.JSONArray
import scala.collection.JavaConverters._
import imcode.server.document.DocumentDomainObject

// Workaround:
// When Vaadin is running in embedded mode,  VaadinRequest#getParameter(String) (as passed to UI#init)
// and related methods fail to return actual HTTP GET request parameters (as requested by browser),
// instead, they are wrapped inside "v-loc" parameter.
// Query string can be obtained using Page.getCurrent().getLocation().getQuery() method.

@com.vaadin.annotations.Theme("imcms")
class MenuAdmin extends UI with Log4jLoggerSupport with ImcmsServicesSupport {

  def mkRequestParamFn(request: VaadinRequest): (String => String) = {
    val embeddedModeParms = URLEncodedUtils.parse(Page.getCurrent.getLocation.getQuery, StandardCharsets.UTF_8).asScala.map { pair =>
      pair.getName -> pair.getValue
    }.toMap

    name => Option(request.getParameter(name).trimToNull).orElse(embeddedModeParms.get(name)).orNull
  }

  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)

    val requestParam = mkRequestParamFn(request)
    val contextPath = Current.contextPath
    val docId = requestParam("docId").toInt
    val doc: TextDocumentDomainObject = imcmsServices.getDocumentMapper.getWorkingDocument(docId)
    val menuNo = requestParam("menuNo").toInt
    val title = requestParam("label").trimToOption.getOrElse("menu_editor.title".f(docId, menuNo))
    val returnUrl = requestParam(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption.getOrElse(
      s"$contextPath/servlet/AdminDoc?meta_id=$docId&flags=${ImcmsConstants.DISPATCH_FLAG__EDIT_MENU}&editmenu=$menuNo"
    )

    val menu = doc.getMenu(menuNo)
    val editor = new MenuEditor(doc, menu)
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
            Current.page.open(returnUrl, "_self")
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
            dlg.close()
          }

          Current.ui.addWindow(dlg)
        }
    }

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