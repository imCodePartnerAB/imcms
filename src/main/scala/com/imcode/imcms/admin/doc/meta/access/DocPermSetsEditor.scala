package com.imcode
package imcms
package admin.doc.meta.access

import imcode.server.user._
import imcms.ImcmsServicesSupport
import imcode.server.document.DocumentPermissionSetTypeDomainObject.{FULL, READ, RESTRICTED_1, RESTRICTED_2}
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.Editor

class DocPermSetsEditor(doc: DocumentDomainObject, user: UserDomainObject) extends Editor with ImcmsServicesSupport {
  case class Data(
                   restrictedOnePermSet: TextDocumentPermissionSetDomainObject = doc.getMeta.getPermissionSets.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject],
                   restrictedTwoPermSet: TextDocumentPermissionSetDomainObject = doc.getMeta.getPermissionSets.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject],
                   isRestrictedOneMorePrivilegedThanRestrictedTwo: Boolean = doc.getMeta.getRestrictedOneMorePrivilegedThanRestrictedTwo
                   )

  private val initialValues = new Data()

  private object editors {
    val fullSet = DocumentPermissionSetDomainObject.FULL.asInstanceOf[TextDocumentPermissionSetDomainObject]
    val readSet = DocumentPermissionSetDomainObject.READ.asInstanceOf[TextDocumentPermissionSetDomainObject]

    val (full, read, restrictedOne, restrictedTwo) = doc match {
      case _: TextDocumentDomainObject => (
        new TextDocPermSetEditor(fullSet, doc, user),
        new TextDocPermSetEditor(readSet, doc, user),
        new TextDocPermSetEditor(initialValues.restrictedOnePermSet, doc, user),
        new TextDocPermSetEditor(initialValues.restrictedTwoPermSet, doc, user)
        )

      case _ => (
        new NonTextDocPermSetEditor(fullSet),
        new NonTextDocPermSetEditor(readSet),
        new NonTextDocPermSetEditor(initialValues.restrictedOnePermSet),
        new NonTextDocPermSetEditor(initialValues.restrictedTwoPermSet)
        )
    }
  }

  override val view = new DocPermSetsEditorView { w =>
    w.tsSets.addTab(editors.read.view, PermSetTypeName(READ))
    w.tsSets.addTab(editors.restrictedOne.view, PermSetTypeName(RESTRICTED_1))
    w.tsSets.addTab(editors.restrictedTwo.view, PermSetTypeName(RESTRICTED_2))
    w.tsSets.addTab(editors.full.view, PermSetTypeName(FULL))

    editors.read.view.setEnabled(false)
    editors.full.view.setEnabled(false)
  }

  resetValues()

  override def resetValues() {
    Seq(editors.full, editors.restrictedOne, editors.restrictedTwo, editors.read).foreach(_.resetValues())
    view.tsSets.setSelectedTab(editors.restrictedOne.view)
    view.chkRestrictedOneIsMorePrivilegedThanRestrictedTwo.checked = doc.getMeta.getRestrictedOneMorePrivilegedThanRestrictedTwo
  }

  override def collectValues(): ErrorsOrData = Data(
    restrictedOnePermSet = editors.restrictedOne.collectValues().right.get,
    restrictedTwoPermSet = editors.restrictedTwo.collectValues().right.get,
    isRestrictedOneMorePrivilegedThanRestrictedTwo = view.chkRestrictedOneIsMorePrivilegedThanRestrictedTwo.checked
  ) |> Right.apply
}