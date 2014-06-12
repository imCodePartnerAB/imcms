package com.imcode
package imcms
package admin.docadmin

import java.util.Locale
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.admin.doc.DocEditor
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.ImcmsServicesSupport


import com.imcode.imcms.vaadin.Current
import scala.collection.JavaConverters._
import imcode.server.document.DocumentDomainObject
import imcode.server.ImcmsConstants
import scala.util.control.NonFatal
import imcode.util.Utility

@com.vaadin.annotations.Theme("imcms")
class DocEditorUI extends UI with Log4jLogger with ImcmsServicesSupport {

  override def init(request: VaadinRequest) {
    val user = Current.imcmsUser

    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration |> { lic =>
      lic.setFirstDelay(1)
      lic.setSecondDelay(2)
      lic.setThirdDelay(3)
    }

    val docId = request.getParameter("meta_id").toInt
    val doc = imcmsServices.getDocumentMapper.getWorkingDocument(docId) : DocumentDomainObject

    if (!user.canEdit(doc)) {
      Utility.redirectToLoginPage(request)
      return
    }

    val editor = new DocEditor(doc)
    val container = new EditorContainerView("doc.edit.title".f(doc.getId.toString))
    val returnUrl = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption.getOrElse(
      s"${Current.contextPath}/servlet/AdminDoc?meta_id=$docId"
    )

    def close() {
      Current.page.setLocation(returnUrl)
    }

    def save(closeOnSuccess: Boolean = false) {
      editor.collectValues() match {
        case Left(errors) => Current.page.showConstraintViolationNotification(errors)
        case Right((editedDoc, commonContents)) =>
          try {
            imcmsServices.getDocumentMapper.saveDocument(editedDoc, commonContents.asJava, Current.imcmsUser)
            Current.page.showInfoNotification("notification.doc.saved".i)

          } catch {
            case NonFatal(e) =>
              logger.error("Document save error", e)
              Current.page.showUnhandledExceptionNotification(e)
          }
      }
    }

    container.mainComponent = editor.view
    container.buttons.btnSave.addClickHandler { _ => save() }
    container.buttons.btnSaveAndClose.addClickHandler { _ => save(closeOnSuccess = true) }
    container.buttons.btnClose.addClickHandler { _ => close() }
    container.buttons.btnReset.addClickHandler { _ => editor.resetValues() }

    Current.page.getUriFragment.asOption.map(_.toLowerCase).foreach {
      case "info" => editor.metaEditor.view.treeEditors.selection = "doc_meta_editor.menu_item.life_cycle"
      case "access" => editor.metaEditor.view.treeEditors.selection = "doc_meta_editor.menu_item.access"
      case "appearance" => editor.metaEditor.view.treeEditors.selection = "doc_meta_editor.menu_item.appearance"
      case "content" => editor.view.setSelectedTab(1)
      case _ =>
    }

    setContent(container)
  }
}