package com.imcode
package imcms
package admin.doc.meta.access

import imcode.server.user._
import imcode.server.document.DocumentPermissionSetTypeDomainObject.{FULL, READ, RESTRICTED_1, RESTRICTED_2}
import imcode.server.document._
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._


// todo ??????????????????????????????????
private object RolePermSetPropertyId


private case class RolePermSet(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
  override def toString = role.getName
}


/**
 * Role permission type table column generator for properties ids of type DocumentPermissionSetTypeDomainObject.
 * todo: check Vaadin (bug?) warning:
 *   For some reason when adding items to table, values are not available (assigned) immediately, hence
 *   Property.getValue returns null.
 */
private class RolesPermSetsTableColumnGenerator(setType: DocumentPermissionSetTypeDomainObject) extends Table.ColumnGenerator {
  def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = new Label with UndefinedSize |>> { lbl =>
    val rolePermSetType = source.getItem(itemId)
      .getItemProperty(RolePermSetPropertyId)
      .getValue.asInstanceOf[RolePermSet]

    lbl.value = if (rolePermSetType != null && rolePermSetType.setType == setType) "X" else ""
  }
}


private class AddRolePermSetDialogView extends FormLayout with UndefinedSize {
  val cbRole = new ComboBox("Role") with SingleSelect[RoleDomainObject] with NoNullSelection with Immediate
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect[DocumentPermissionSetTypeDomainObject]

  Seq(READ, RESTRICTED_1, RESTRICTED_2, FULL).foreach { setType =>
    ogPermsSetType.addItem(setType, PermSetTypeName(setType))
  }

  addComponents(cbRole, ogPermsSetType)
}


/**
 * Changes permission set type for a role.
 */
private class ChangeRolePermSetDialogView extends FormLayout with UndefinedSize {
  val lblRole = new Label with UndefinedSize |>> {_ setCaption "Role"}
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect[DocumentPermissionSetTypeDomainObject]

  Seq(READ, RESTRICTED_1, RESTRICTED_2, FULL).foreach { setType =>
    ogPermsSetType.addItem(setType, PermSetTypeName(setType))
  }

  addComponents(lblRole, ogPermsSetType)
}


private object PermSetTypeName extends (DocumentPermissionSetTypeDomainObject => String) {
  private val permSetTypesNames = Map(FULL -> "All [sealed]", RESTRICTED_1 -> "Custom-One", RESTRICTED_2 -> "Custom-Two", READ -> "View [sealed]")

  def apply(permSetType: DocumentPermissionSetTypeDomainObject) = permSetTypesNames(permSetType)
}