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

// todo: validate params in filter, create params wrapper, pass params into DocAdmin (no need to examine path in init)?
// todo: template/group
// todo: add [im]cms path element: /[im]cms/sysadmin/...; [im]cms/docadmin/...
@com.vaadin.annotations.Theme("imcms")
class DocAdmin extends UI with Log4jLoggerSupport with ImcmsServicesSupport {

  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)

    val docId = request.getParameter("meta_id").toInt
    val doc: DocumentDomainObject = imcmsServices.getDocumentMapper.getWorkingDocument(docId)
    val editor = new DocEditor(doc)
    val container = new EditorContainerView("doc.edit_properties.title".f(doc.getId))

    editor.view.setSize(900, 600)

    container.mainComponent = editor.view
    container.buttons.btnSave.addClickHandler {
      _ =>
        editor.collectValues() match {
          case Left(errors) => Current.page.showConstraintViolationNotification(errors)
          case Right((editedDoc, i18nMetas)) =>
            try {
              imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.asJava, Current.imcmsUser)
              Current.page.showInfoNotification("notification.doc.saved".i)
              Current.page.open(Current.contextPath, "_self")
            } catch {
              case e: Exception => Current.page.showUnhandledExceptionNotification(e)
            }
        }
    }

    container.buttons.btnClose.addClickHandler {
      _ =>
        Current.page.open(Current.contextPath, "_self")
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