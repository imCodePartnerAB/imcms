package com.imcode
package imcms
package admin.doc.meta.profile

import scala.collection.breakOut
import scala.collection.JavaConverters._
import imcode.server.user._
import imcode.server.document._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import imcms.ImcmsServicesSupport
import textdocument.TextDocumentDomainObject
import com.imcode.imcms.admin.doc.meta.access.{TextDocPermSetEditorView, TextDocPermSetEditor}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.Editor


// todo: check: ImcmsConstants.DISPATCH_FLAG__DOCUMENT_PERMISSIONS_PAGE == flags && user.canEditPermissionsFor(document)
// todo: discuss with Hillar/Crister:
//   -Text doc can be used as a template for any doc type - current behavior
//   -Non text doc can be used as a template for all but text docs/for the doc of the same type.
//   -Shared permissions - i.e. saved elsewhere, but referenced by this doc and copied if necessary.

/**
 * Doc profile editor.
 *
 * According to latest version (v4.x.x)
 * any text doc can be used as a profile for a new doc but API can treat doc of any type as a profile.
 *
 * A text document profile defines:
 * -default template
 * -restricted permissions and templates.
 */
class ProfileEditor(doc: TextDocumentDomainObject, user: UserDomainObject) extends Editor with ImcmsServicesSupport {

  case class Data(
    defaultTemplate: String,
    restrictedOnePermSet: TextDocumentPermissionSetDomainObject,
    restrictedTwoPermSet: TextDocumentPermissionSetDomainObject,
    restrictedOneTemplate: String,
    restrictedTwoTemplate: String
  )

  private val restrictedOnePermSet = doc.getPermissionSetsForNewDocuments.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject]
  private val restrictedTwoPermSet = doc.getPermissionSetsForNewDocuments.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject]

  private val restrictedOnePermSetEditor = new TextDocPermSetEditor(restrictedOnePermSet, doc, user)
  private val restrictedTwoPermSetEditor = new TextDocPermSetEditor(restrictedTwoPermSet, doc, user)
  private val defaultPermSetEditor = new TextDocPermSetEditor(
    DocumentPermissionSetDomainObject.READ.asInstanceOf[TextDocumentPermissionSetDomainObject], doc, user
  ) |>> { _.view.setEnabled(false) }

  override val view = new ProfileEditorView(defaultPermSetEditor.view, restrictedOnePermSetEditor.view, restrictedTwoPermSetEditor.view)

  override def collectValues(): ErrorsOrData =
    Right(
      Data(
        view.cbDefaultTemplate.value,
        restrictedOnePermSet, // ??? clone
        restrictedTwoPermSet, // ??? clone
        view.cbRestrictedOneDefaultTemplate.value,
        view.cbRestrictedTwoDefaultTemplate.value
      )
    )

  override def resetValues() {
    restrictedOnePermSetEditor.resetValues()
    restrictedOnePermSetEditor.resetValues()

    resetTemplatesValues()
  }

  private def resetTemplatesValues() {
    val templatesNames = imcmsServices.getTemplateMapper.getAllTemplates.asScala.map(_.getName)
    val defaultTemplateNameOpt = templatesNames.headOption

    def setTemplatesNamesAsComboBoxItems(cb: ComboBox with SingleSelect[String], selectedTemplateName: String) {
      cb.removeAllItems()
      templatesNames.foreach(cb.addItem)
      defaultTemplateNameOpt.orElse(selectedTemplateName.asOption).foreach(cb.select)
    }

    setTemplatesNamesAsComboBoxItems(view.cbDefaultTemplate, doc.getDefaultTemplateName)
    setTemplatesNamesAsComboBoxItems(view.cbRestrictedOneDefaultTemplate, doc.getDefaultTemplateNameForRestricted1)
    setTemplatesNamesAsComboBoxItems(view.cbRestrictedTwoDefaultTemplate, doc.getDefaultTemplateNameForRestricted2)
  }

  resetValues()
}


class ProfileEditorView(
    defaultPermSetEditorView: TextDocPermSetEditorView,
    restrictedOnePermSetEditorView: TextDocPermSetEditorView,
    restrictedTwoPermSetEditorView: TextDocPermSetEditorView) extends VerticalLayout with FullWidth {

  val cbDefaultTemplate = new ComboBox("Template") with SingleSelect[String] with NoNullSelection // ??? NullSelection ???
  val cbRestrictedOneDefaultTemplate = new ComboBox("Template") with SingleSelect[String] with NullSelection
  val cbRestrictedTwoDefaultTemplate = new ComboBox("Template") with SingleSelect[String] with NullSelection

  defaultPermSetEditorView.setCaption("Permissions")
  restrictedOnePermSetEditorView.setCaption("Permissions")
  restrictedTwoPermSetEditorView.setCaption("Permissions")

  private val pnlSettings = new Panel("Settings") with FullWidth {
    val content = new VerticalLayout with FullWidth with Margin

    val tsSettings = new TabSheet
    val lytDefault = new FormLayout with Margin
    val lytRestrictedOne = new FormLayout with Margin
    val lytRestrictedTwo = new FormLayout with Margin

    tsSettings.addTab(lytDefault, "Default")
    tsSettings.addTab(lytRestrictedOne, "Custom-One")
    tsSettings.addTab(lytRestrictedTwo, "Custom-Two")

    lytDefault.addComponents(cbDefaultTemplate, defaultPermSetEditorView)
    lytRestrictedOne.addComponents(cbRestrictedOneDefaultTemplate, restrictedOnePermSetEditorView)
    lytRestrictedTwo.addComponents(cbRestrictedTwoDefaultTemplate, restrictedTwoPermSetEditorView)

    content.addComponent(tsSettings)

    setContent(content)
  }

  this.addComponents(pnlSettings)
}