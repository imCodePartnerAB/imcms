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
import admin.doc.meta.permissions.{TextDocRestrictedPermSetEditor}
import com.vaadin.ui._

/**
 * Text doc profile.
 *
 * According to latest version (v4.x.x)
 * any text doc can be used as a profile for a new document.
 *
 * A text document profile defines:
 * -default template
 * -restricted permissions and templates.
 */
class ProfileSheet(doc: TextDocumentDomainObject, user: UserDomainObject) extends ImcmsServicesSupport {

  private val restrictedOnePermSet = doc.getPermissionSetsForNewDocuments.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject]
  private val restrictedTwoPermSet = doc.getPermissionSetsForNewDocuments.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject]

  private val restrictedOnePermSetEditor = new TextDocRestrictedPermSetEditor(restrictedOnePermSet, user)
  private val restrictedTwoPermSetEditor = new TextDocRestrictedPermSetEditor(restrictedTwoPermSet, user)

  val ui = letret(new ProfileSheetUI) { ui =>
    ui.btnEditRestrictedOnePermSet addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Edit limited 1 permission set for new document")) { dlg =>
        dlg.mainUI = restrictedOnePermSetEditor.ui
      }
    }

    ui.btnEditRestrictedTwoPermSet addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Edit limited 2 permission set for new document")) { dlg =>
        dlg.mainUI = restrictedTwoPermSetEditor.ui
      }
    }
  }

  revert()

  def revert() {
    restrictedOnePermSetEditor.revert()
    restrictedOnePermSetEditor.revert()

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
}


private class ProfileSheetUI extends VerticalLayout with FullWidth {

  private val frm = new Form { setCaption("Text document profile") }
  private val frmRestrictedPermSets = new Form { setCaption("Limited permissions") }
  private val frmRestrictedDefaultTemplates = new Form { setCaption("Limited templates") }

  val cbDefaultTemplate = new ComboBox("Default template") with SingleSelect2[String] with NoNullSelection
  val cbRestrictedOneDefaultTemplate = new ComboBox("Limited-1") with SingleSelect2[String] with NoNullSelection
  val cbRestrictedTwoDefaultTemplate = new ComboBox("Limited-2") with SingleSelect2[String] with NoNullSelection

  val btnEditRestrictedOnePermSet = new Button("Limited 1") with SmallStyle
  val btnEditRestrictedTwoPermSet = new Button("Limited 2") with SmallStyle

  addComponents(frm.getLayout, cbDefaultTemplate, frmRestrictedPermSets, frmRestrictedDefaultTemplates)
  addComponents(frmRestrictedPermSets.getLayout, btnEditRestrictedOnePermSet, btnEditRestrictedTwoPermSet)
  addComponents(frmRestrictedDefaultTemplates.getLayout, cbRestrictedOneDefaultTemplate, cbRestrictedTwoDefaultTemplate)

  addComponent(frm)
}