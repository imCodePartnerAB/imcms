package com.imcode
package imcms
package admin.doc

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.admin.doc.projection.{DocsProjectionOps, DocsProjection}
import _root_.imcode.server.document.{UrlDocumentDomainObject, FileDocumentDomainObject}
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.user.UserDomainObject


class DocSelectDialog(caption: String, user: UserDomainObject, multiSelect: Boolean = true) extends OkCancelDialog with CustomSizeDialog with Resizable {
  val projection = new DocsProjection(user, multiSelect = multiSelect)
  val projectionOps = new DocsProjectionOps(projection)

  mainUI = new DocSelectDialogMainUI(projection.ui) |>> { ui =>
    ui.miNewFileDoc.setCommandHandler { projectionOps.mkDocOfType[FileDocumentDomainObject] }
    ui.miNewTextDoc.setCommandHandler { projectionOps.mkDocOfType[TextDocumentDomainObject] }
    ui.miNewUrlDoc.setCommandHandler { projectionOps.mkDocOfType[UrlDocumentDomainObject] }

    ui.miCopySelectedDoc.setCommandHandler { projectionOps.copySelectedDoc() }
    ui.miDeleteSelectedDocs.setCommandHandler { projectionOps.deleteSelectedDocs() }
    ui.miShowSelectedDoc.setCommandHandler { projectionOps.showSelectedDoc() }
    ui.miHelp.setCommandHandler { /* todo: ??? show help in modal dialog ??? */ }

    projection.listen { selection =>
      val isSingleSelection = selection.size == 1
      val isTextDocSelection = isSingleSelection && selection.head.isInstanceOf[TextDocumentDomainObject]

      ui.miDeleteSelectedDocs.setEnabled(selection.nonEmpty)

      doto(ui.miShowSelectedDoc, ui.miCopySelectedDoc) { mi => mi.setEnabled(isSingleSelection) }

      doto(ui.miNew, ui.miNewFileDoc, ui.miNewTextDoc, ui.miNewUrlDoc) { mi => mi.setEnabled(isTextDocSelection) }
    }
  }


  this.setSize(700, 600)

  projection.listen { selection => btnOk.setEnabled(selection.nonEmpty) }
  projection.notifyListeners()
}
