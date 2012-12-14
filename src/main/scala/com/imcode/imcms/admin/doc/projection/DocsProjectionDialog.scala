package com.imcode
package imcms
package admin.doc.projection

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import _root_.imcode.server.document.{UrlDocumentDomainObject, FileDocumentDomainObject}
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject


trait DocsProjectionDialog extends CustomSizeDialog { this: OkCancelDialog =>
  val projection = new DocsProjection(new AllDocsContainer) |>> { _.docsUI.setMultiSelect(true) }
  val ops = new DocsProjectionOps(projection)

  mainUI = new DocsProjectionDialogMainUI(projection.ui) |>> { ui =>
    ui.miNewFileDoc.setCommandHandler { ops.mkDocOfType[FileDocumentDomainObject] }
    ui.miNewTextDoc.setCommandHandler { ops.mkDocOfType[TextDocumentDomainObject] }
    ui.miNewUrlDoc.setCommandHandler { ops.mkDocOfType[UrlDocumentDomainObject] }

    ui.miCopySelectedDoc.setCommandHandler { ops.copySelectedDoc() }
    ui.miDeleteSelectedDocs.setCommandHandler { ops.deleteSelectedDocs() }
    ui.miShowSelectedDoc.setCommandHandler { ops.showSelectedDoc() }
    ui.miHelp.setCommandHandler { /* show help in modal dialog */ }

    projection.listen { selection =>
      val isSingleSelection = selection.size == 1
      val isTextDocSelection = isSingleSelection &&
        (projection.docsUI.item(selection.head) match {
          case docItem: FilterableDocsContainer#DocItem => docItem.doc.isInstanceOf[TextDocumentDomainObject]
          case _ => false
        })

      ui.miDeleteSelectedDocs.setEnabled(selection.nonEmpty)

      doto(ui.miShowSelectedDoc, ui.miCopySelectedDoc) { mi => mi.setEnabled(isSingleSelection) }

      doto(ui.miNew, ui.miNewFileDoc, ui.miNewTextDoc, ui.miNewUrlDoc) { mi => mi.setEnabled(isTextDocSelection) }
    }
  }

  projection.listen { selection =>
    btnOk.setEnabled(selection.nonEmpty)
  }

  projection.notifyListeners()
}
