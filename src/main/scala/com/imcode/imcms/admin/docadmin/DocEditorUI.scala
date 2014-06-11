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

@com.vaadin.annotations.Theme("imcms")
class DocEditorUI extends UI with Log4jLoggerSupport with ImcmsServicesSupport {

  //fixme: check permissions
  override def init(request: VaadinRequest) {
    getLoadingIndicatorConfiguration.setFirstDelay(1)
    getLoadingIndicatorConfiguration.setSecondDelay(2)
    getLoadingIndicatorConfiguration.setThirdDelay(3)

    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)

    val docId = request.getParameter("meta_id").toInt
    val doc: DocumentDomainObject = imcmsServices.getDocumentMapper.getWorkingDocument(docId)
    val editor = new DocEditor(doc)
    val container = new EditorContainerView("doc.edit.title".f(doc.getId.toString))
    val returnUrl = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption.getOrElse(
      s"${Current.contextPath}/servlet/AdminDoc?meta_id=$docId"
    )

    container.mainComponent = editor.view
    container.buttons.btnSave.addClickHandler {
      _ =>
        editor.collectValues() match {
          case Left(errors) => Current.page.showConstraintViolationNotification(errors)
          case Right((editedDoc, commonContents)) =>
            try {
              imcmsServices.getDocumentMapper.saveDocument(editedDoc, commonContents.asJava, Current.imcmsUser)
              Current.page.showInfoNotification("notification.doc.saved".i)
              Current.page.open(Current.contextPath, "_self")
            } catch {
              case NonFatal(e) =>
                logger.error("Document save error", e)
                Current.page.showUnhandledExceptionNotification(e)
            }
        }
    }

    container.buttons.btnClose.addClickHandler {
      _ =>
        Current.page.open(returnUrl, "_self")
    }

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