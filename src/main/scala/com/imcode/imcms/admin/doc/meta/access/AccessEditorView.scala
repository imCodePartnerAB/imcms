package com.imcode
package imcms
package admin.doc.meta.access

import imcode.server.user._
import imcode.server.document.DocumentPermissionSetTypeDomainObject.{FULL, READ, RESTRICTED_1, RESTRICTED_2}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.data.PropertyDescriptor

class AccessEditorView extends TabSheet with TabSheetSmallStyle with FullWidth {

  object perms {
    val mb = new MenuBar with MenuBarInTabStyle with FullWidth
    val miRole = mb.addItem("doc_access_editor.rights.mi.role".i)
    val miEditPermSets = mb.addItem("doc_access_editor.rights.mi.permissions".i)
    val miRoleAdd = miRole.addItem("mi.add".i)
    val miRoleRemove = miRole.addItem("mi.remove".i)
    val miHelp = miRole.addItem("mi.help".i)
    val miRoleChangePermSet = miRole.addItem("doc_access_editor.rights.mi.role.change_permissions")

    val tblRolesPermSets = new Table with BorderlessStyle with MultiSelect[RoleDomainObject] with Immediate with FullSize with Selectable |>> { tbl =>

      addContainerProperties(tbl,
        PropertyDescriptor[RolePermSet](RolePermSetPropertyId)
      )

      tbl.setColumnHeader(RolePermSetPropertyId, "doc_access_editor.rights.container_property_role".i)

      for (setType <- Seq(READ, RESTRICTED_1, RESTRICTED_2, FULL)) {
        tbl.addGeneratedColumn(setType, new RolesPermSetsTableColumnGenerator(setType))
        tbl.setColumnHeader(setType, PermSetTypeName(setType))
        tbl.setColumnAlignment(setType, Table.Align.CENTER)
      }
    }

    val content = new VerticalLayout(mb, tblRolesPermSets) with FullSize |>> {
      _.setExpandRatio(tblRolesPermSets, 1.0f)
    }
  }

  object misc {
    val chkShowToUnauthorizedUser = new CheckBox("Unauthorized user can see link(s) to this document")
    val chkShareWithOtherAdmins = new CheckBox("Share the document with other administrators")

    val content = new VerticalLayout(chkShowToUnauthorizedUser, chkShareWithOtherAdmins) with FullSize
  }

  addTab(perms.content, "doc_access_editor.rights".i)
  addTab(misc.content, "doc_access_editor.misc".i)
}