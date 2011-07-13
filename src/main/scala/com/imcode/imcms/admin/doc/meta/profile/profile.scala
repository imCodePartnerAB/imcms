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
import DocumentPermissionSetTypeDomainObject.{NONE, FULL, READ, RESTRICTED_1, RESTRICTED_2}
import textdocument.TextDocumentDomainObject
import admin.doc.meta.permissions.LimPermsDialogMainUI
import com.vaadin.ui._

/**
 * More clarifications needed.
 *
 * According to latest version (v4.x.x)
 * Any text doc can be marked as a profile.
 * A profile defines lim1 and lim2 permissions
 */
class ProfileSheet(doc: TextDocumentDomainObject, user: UserDomainObject) extends ImcmsServicesSupport {

  val ui = letret(new ProfileSheetUI) { ui =>
    ui.frmLimPermsSet.btnEditLim1 addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Edit limited 1 permission set for new document")) { dlg =>
        dlg.mainUI = letret(new LimPermsDialogMainUI) { mainUI =>

        }
      }
    }

    ui.frmLimPermsSet.btnEditLim2 addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Edit limited 2 permission set for new document")) { dlg =>
        dlg.mainUI = letret(new LimPermsDialogMainUI) { mainUI =>

        }
      }
    }
  }

  revert()

  def revert() {
    ui.cbTemplate.removeAllItems()

    imcmsServices.getTemplateMapper.getAllTemplates foreach { ui.cbTemplate addItem _.getName }
    ?(doc.getDefaultTemplateName) orElse ui.cbTemplate.itemIds.headOption foreach { ui.cbTemplate.value = _ }

    //ui.frmLimPermsSet.btnEditLim1.setEnabled(user.canDefineRestrictedOneFor(doc))
    //ui.frmLimPermsSet.btnEditLim2.setEnabled(user.canDefineRestrictedTwoFor(doc))
  }
}


class ProfileSheetUI extends VerticalLayout with FullWidth {
  private val frm = new Form {
    setCaption("New document")
  }

  // NO NULL???
  val cbTemplate = new ComboBox("Template") with SingleSelect2[String] with NoNullSelection

  //!NB@ Only if this is a text doc.
  val frmLimPermsSet = new Form {
    setCaption("Permissions")

    val btnEditLim1 = new Button("Limited 1") with SmallStyle
    val btnEditLim2 = new Button("Limited 2") with SmallStyle
  }

  addComponents(frm.getLayout, cbTemplate, frmLimPermsSet)

  addComponent(frm)
}