package com.imcode
package imcms
package admin.doc.meta.profile

import scala.collection.breakOut
import scala.collection.JavaConversions._
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.document._
import com.imcode.imcms.vaadin._
import imcms.ImcmsServicesSupport
import textdocument.TextDocumentDomainObject
import com.vaadin.ui._
import admin.doc.meta.permissions.{DocRestrictedPermSetEditor, TextDocRestrictedPermSetEditor}

// todo: check: ImcmsConstants.DISPATCH_FLAG__DOCUMENT_PERMISSIONS_PAGE == flags && user.canEditPermissionsFor(document)
// todo: discuss with Hillar/Crister:
//   -Text doc can be used as a template for any doc type - current behavior
//   -Non text doc can be used as a template for all but text docs/for the doc of the same type.
//   -Shared permissions - i.e. saved elsewhere, but referenced by this doc and copied if necessary.

/**
 * Doc profile.
 *
 * According to latest version (v4.x.x)
 * any text doc can be used as a profile for a new doc but API can treat doc of any type as a profile.
 *
 * A text document profile defines:
 * -default template
 * -restricted permissions and templates.
 */
class ProfileSheet(doc: TextDocumentDomainObject, user: UserDomainObject) extends ImcmsServicesSupport {

  case class State(
    defaultTemplate: String,
    restrictedOnePermSet: TextDocumentPermissionSetDomainObject,
    restrictedTwoPermSet: TextDocumentPermissionSetDomainObject,
    restrictedOneTemplate: String,
    restrictedTwoTemplate: String
  )

  private val restrictedOnePermSet = doc.getPermissionSetsForNewDocuments.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject]
  private val restrictedTwoPermSet = doc.getPermissionSetsForNewDocuments.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject]

  private val restrictedOnePermSetEditor = new DocRestrictedPermSetEditor(restrictedOnePermSet, doc, user) with TextDocRestrictedPermSetEditor
  private val restrictedTwoPermSetEditor = new DocRestrictedPermSetEditor(restrictedTwoPermSet, doc, user) with TextDocRestrictedPermSetEditor

  val ui = letret(new ProfileSheetUI) { ui =>
    ui.btnEditRestrictedOnePermSet addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Limited-1 permissions")) { dlg =>
        dlg.mainUI = restrictedOnePermSetEditor.ui
      }
    }

    ui.btnEditRestrictedTwoPermSet addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Limited-2 permissions")) { dlg =>
        dlg.mainUI = restrictedTwoPermSetEditor.ui
      }
    }
  }

  revert()

  def revert() {
    //restrictedOnePermSetEditor.revert()
    //restrictedOnePermSetEditor.revert()

    revertTemplates()

    // todo: add checks:
    //ui.frmLimPermsSet.btnEditLim1.setEnabled(user.canDefineRestrictedOneFor(doc))
    //ui.frmLimPermsSet.btnEditLim2.setEnabled(user.canDefineRestrictedTwoFor(doc))
  }

  private def revertTemplates() {
    val templatesNames = imcmsServices.getTemplateMapper.getAllTemplates map {_.getName}
    val defaultTemplateNameOpt = templatesNames.headOption

    def setTemplatesNamesAsComboBoxItems(cb: ComboBox with SingleSelect2[String], selectedTemplateName: String) {
      cb.removeAllItems()
      templatesNames foreach {cb addItem _}
      defaultTemplateNameOpt orElse ?(selectedTemplateName) foreach cb.select
    }

    setTemplatesNamesAsComboBoxItems(ui.cbDefaultTemplate, doc.getDefaultTemplateName)
    setTemplatesNamesAsComboBoxItems(ui.cbRestrictedOneDefaultTemplate, doc.getDefaultTemplateNameForRestricted1)
    setTemplatesNamesAsComboBoxItems(ui.cbRestrictedTwoDefaultTemplate, doc.getDefaultTemplateNameForRestricted2)
  }


  def state = State(
    ui.cbDefaultTemplate.value,
    restrictedOnePermSet, // ??? clone
    restrictedTwoPermSet, // ??? clone
    ui.cbRestrictedOneDefaultTemplate.value,
    ui.cbRestrictedTwoDefaultTemplate.value
  )
}


class ProfileSheetUI extends VerticalLayoutUI(margin = false) with FullWidth {

  private val frmDefault = new Form { setCaption("Default") }
  private val frmCustom = new Form { setCaption("Custom") }

  private val lytCustomOne = new HorizontalLayoutUI("Limited-1", defaultAlignment = Alignment.MIDDLE_LEFT)
  private val lytCustomTwo = new HorizontalLayoutUI("Limited-2", defaultAlignment = Alignment.MIDDLE_LEFT)

  val cbDefaultTemplate = new ComboBox("Template") with SingleSelect2[String] with NoNullSelection // ??? NullSelection ???
  val cbRestrictedOneDefaultTemplate = new ComboBox("Template") with SingleSelect2[String] with NullSelection
  val cbRestrictedTwoDefaultTemplate = new ComboBox("Template") with SingleSelect2[String] with NullSelection

  val btnEditRestrictedOnePermSet = new Button("permissions") with SmallStyle
  val btnEditRestrictedTwoPermSet = new Button("permissions") with SmallStyle

  addComponents(lytCustomOne, btnEditRestrictedOnePermSet, cbRestrictedOneDefaultTemplate)
  addComponents(lytCustomTwo, btnEditRestrictedTwoPermSet, cbRestrictedTwoDefaultTemplate)

  addComponents(frmDefault.getLayout, cbDefaultTemplate)
  addComponents(frmCustom.getLayout, lytCustomOne, lytCustomTwo)

  addComponents(this, frmDefault, frmCustom)
}