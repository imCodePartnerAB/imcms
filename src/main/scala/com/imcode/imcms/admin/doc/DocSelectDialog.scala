package com.imcode
package imcms
package admin.doc

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.admin.doc.projection.{DocsProjectionOps, DocsProjection}
import _root_.imcode.server.document.{UrlDocumentDomainObject, FileDocumentDomainObject}
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.user.UserDomainObject


class DocSelectDialog(caption: String, user: UserDomainObject, multiSelect: Boolean = true) extends OkCancelDialog with CustomSizeDialog with Resizable {
  val projection = new DocsProjection(user, multiSelect = multiSelect)
  val projectionOps = new DocsProjectionOps(projection)

  mainComponent = new DocSelectDialogView(projection.view) |>> { w =>
    w.miNewFileDoc.setCommandHandler { _ => projectionOps.mkDocOfType[FileDocumentDomainObject] }
    w.miNewTextDoc.setCommandHandler { _ => projectionOps.mkDocOfType[TextDocumentDomainObject] }
    w.miNewUrlDoc.setCommandHandler { _ => projectionOps.mkDocOfType[UrlDocumentDomainObject] }

    w.miCopySelectedDoc.setCommandHandler { _ => projectionOps.copySelectedDoc() }
    w.miDeleteSelectedDocs.setCommandHandler { _ =>projectionOps.deleteSelectedDocs()
    }
    w.miShowSelectedDoc.setCommandHandler { _ => projectionOps.showSelectedDoc() }
    w.miHelp.setCommandHandler { _ => /* todo: ??? show help in modal dialog ??? */ }

    projection.listen { selection =>
      val isSingleSelection = selection.size == 1
      // fixme: invalid test - I18nDocRef
      val isTextDocSelection = isSingleSelection && selection.head.isInstanceOf[TextDocumentDomainObject]

      w.miDeleteSelectedDocs.setEnabled(selection.nonEmpty)

      Seq(w.miShowSelectedDoc, w.miCopySelectedDoc).foreach(mi => mi.setEnabled(isSingleSelection))

      Seq(w.miNew, w.miNewFileDoc, w.miNewTextDoc, w.miNewUrlDoc).foreach(mi => mi.setEnabled(isTextDocSelection))
    }
  }


  this.setSize(700, 600)

  projection.listen { selection => btnOk.setEnabled(selection.nonEmpty) }
  projection.notifyListeners()
}
