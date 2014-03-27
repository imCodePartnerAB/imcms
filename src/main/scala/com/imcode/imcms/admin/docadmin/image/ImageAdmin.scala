package com.imcode
package imcms
package admin.docadmin.image


import com.imcode.imcms.admin.docadmin.EditorContainerView
import java.util.Locale
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.ImcmsServicesSupport

import _root_.imcode.server.document.textdocument._

import com.imcode.imcms.vaadin.Current

@com.vaadin.annotations.Theme("imcms")
class ImageAdmin extends UI with Log4jLoggerSupport with ImcmsServicesSupport {

  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)

  }

  def wrapTextDocImageEditor(request: VaadinRequest, doc: TextDocumentDomainObject, imageNo: Int): EditorContainerView = {
    val imageEditor = new ImagesEditor(doc.getRef, imageNo)
    val editorContainerView = new EditorContainerView("doc.edit_image.title".i)

    editorContainerView.mainComponent = imageEditor.view
    editorContainerView.buttons.btnSave.addClickHandler {
      _ =>
    }
    editorContainerView.buttons.btnReset.addClickHandler {
      _ => imageEditor.resetValues()
    }
    editorContainerView.buttons.btnSaveAndClose.addClickHandler {
      _ =>
    }
    editorContainerView.buttons.btnClose.addClickHandler {
      _ =>
    }

    imageEditor.view.setSize(900, 600)
    imageEditor.resetValues()

    editorContainerView
  }

}