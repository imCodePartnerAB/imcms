package com.imcode
package imcms
package admin.doc.meta.access

import imcode.server.user._
import imcode.server.document.DocumentPermissionSetTypeDomainObject.{FULL, READ, RESTRICTED_1, RESTRICTED_2}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.data.PropertyDescriptor

class AccessEditorView extends VerticalLayout with Spacing with FullWidth {
  object perms {
    val mb = new MenuBar
    val miRole = mb.addItem("Role")
    val miEditPermSets = mb.addItem("Permissions")
    val miRoleAdd = miRole.addItem("Add")
    val miRoleRemove = miRole.addItem("Remove")
    val miRoleChangePermSet = miRole.addItem("Change Permissions")

    val tblRolesPermSets = new Table with MultiSelect[RoleDomainObject] with Immediate with FullWidth with Selectable |>> { tbl =>
      tbl.setPageLength(7)

      addContainerProperties(tbl,
        PropertyDescriptor[RolePermSet](RolePermSetPropertyId))

      tbl.setColumnHeader(RolePermSetPropertyId, "Role")

      for (setType <- Seq(READ, RESTRICTED_1, RESTRICTED_2, FULL)) {
        tbl.addGeneratedColumn(setType, new RolesPermSetsTableColumnGenerator(setType))
        tbl.setColumnHeader(setType, PermSetTypeName(setType))
      }
    }
  }

  object misc {
    val chkShowToUnauthorizedUser = new CheckBox("Unauthorized user can see link(s) to this document")
    val chkShareWithOtherAdmins = new CheckBox("Share the document with other administrators")
  }

  private val pnlRights = new Panel("Rights") with FullWidth {
    val content = new VerticalLayout with Spacing with Margin with FullWidth

    content.addComponents(perms.mb, perms.tblRolesPermSets)

    setContent(content)
  }

  private val pnlMisc = new Panel("Misc") with FullWidth {
    val content = new VerticalLayout with Spacing with Margin

    content.addComponents(misc.chkShowToUnauthorizedUser, misc.chkShareWithOtherAdmins)
    setContent(content)
  }

  this.addComponents(pnlRights, pnlMisc)
}