package com.imcode
package imcms
package admin
package doc.meta.access

import com.imcode.imcms.admin.doc.meta.access.RolePermSet
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data.PropertyDescriptor
import com.imcode.imcms.vaadin.data.PropertyDescriptor
import com.vaadin.ui.{Label, Table}
import imcode.server.user.RoleDomainObject
import imcode.server.document.DocumentPermissionSetTypeDomainObject

class RolesPermissionsTable extends Table with BorderlessStyle with MultiSelect[RoleDomainObject] with Immediate with FullWidth with Selectable {

//  /**
//   * Role permission type table column generator for properties ids of type DocumentPermissionSetTypeDomainObject.
//   * todo: check Vaadin (bug?) warning:
//   *   For some reason when adding items to table, values are not available (assigned) immediately, hence
//   *   Property.getValue returns null.
//   */
//  private class RolesPermSetsTableColumnGenerator(setType: DocumentPermissionSetTypeDomainObject) extends Table.ColumnGenerator {
//    def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = new Label with UndefinedSize |>> { lbl =>
//      val rolePermSetType = source.getItem(itemId)
//                                  .getItemProperty(RolePermSetPropertyId)
//                                  .getValue.asInstanceOf[RolePermSet]
//
//      lbl.value = if (rolePermSetType != null && rolePermSetType.setType == setType) "X" else ""
//    }
//  }




//  tbl.addGeneratedColumn(setType, new RolesPermSetsTableColumnGenerator(setType))
//
//
//  addContainerProperties(this,
//    PropertyDescriptor[RolePermSet](RolePermSetPropertyId))
//
//  setColumnHeader(RolePermSetPropertyId, "doc_access_editor.rights.container_property_role".i)
//
//  for (setType <- Seq(READ, RESTRICTED_1, RESTRICTED_2, FULL)) {
//    tbl.addGeneratedColumn(setType, new RolesPermSetsTableColumnGenerator(setType))
//    tbl.setColumnHeader(setType, PermSetTypeName(setType))
//    tbl.setColumnAlignment(setType, Table.Align.CENTER)
//  }
}

