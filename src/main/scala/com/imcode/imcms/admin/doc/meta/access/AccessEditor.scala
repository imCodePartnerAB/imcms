package com.imcode
package imcms
package admin.doc.meta.access

import com.imcode.imcms.vaadin.Current
import scala.collection.breakOut
import scala.collection.JavaConverters._
import imcode.server.user._
import imcms.ImcmsServicesSupport
import imcode.server.document.DocumentPermissionSetTypeDomainObject.{NONE, FULL, READ, RESTRICTED_1, RESTRICTED_2}
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._
import com.vaadin.data.Property
import com.imcode.imcms.vaadin.Editor

// Discuss
//        Managed templates in groups:
//          if checked??? is it used somewhere/somehow
//
//        if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(radioButtonDocumentPermissionSetType, roleId, document )) {
//            return Html.radio(name, value, checked ) ;
//        } else {
//            return checked ? Html.hidden(name, value )+"X" : "O" ;
//        }

// if (user.canDefineRestrictedOneFor(document))
// if (user.canDefineRestrictedTwoFor(document))

// ?????????????????????????????????????????????????????
// if (user.isSuperAdminOrHasFullPermissionOn(document))
// checkbox privileged one over two

/// todo: remove / already present in main???:
//  <td class="imcmsAdmText" nowrap><? global/Created_by ?>&nbsp;<i><%= Utility.formatUser(userMapper.getUser(document.getCreatorId())) %></i></td>


/**
 * Document access editor.
 *
 * Access to a document is controlled using build-in permissions, such as 'view', 'edit', etc.
 * Permissions are organized into collections called permission sets. // ??? assoc with enable/disable ???
 *
 * Any user role can be associated with at most one permission set per document.
 * The system has two build-in unmodifiable permission sets which always contain the same permissions:
 *   READ - contains single permission - 'view'.
 *   FULL - contains all permissions.
 * Additionally, for every document system automatically creates two customizable permission sets called CUSTOM-1 and CUSTOM-2.
 * Initially, those sets contain single permission - 'veiw'.
 * An administrator can customize those sets at any time by adding or removing permissions to/from a set
 * except 'view', which is sealed.
 */
class AccessEditor(doc: DocumentDomainObject, user: UserDomainObject) extends Editor with ImcmsServicesSupport {
  private val meta = doc.getMeta.clone()
  private val types = List(READ, RESTRICTED_1, RESTRICTED_2, FULL)

  case class Data(
                   rolesPermissions: RoleIdToDocumentPermissionSetTypeMappings = meta.getRoleIdToDocumentPermissionSetTypeMappings.clone(),
                   restrictedOnePermSet: TextDocumentPermissionSetDomainObject = meta.getPermissionSets.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject],
                   restrictedTwoPermSet: TextDocumentPermissionSetDomainObject = meta.getPermissionSets.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject],
                   isRestrictedOneMorePrivilegedThanRestrictedTwo: Boolean = meta.getRestrictedOneMorePrivilegedThanRestrictedTwo,
                   isLinkedForUnauthorizedUsers: Boolean = meta.getLinkedForUnauthorizedUsers,
                   isLinkableByOtherUsers: Boolean = meta.getLinkableByOtherUsers
                   )

  private val initialValues = Data()
  private val permSetsEditor = new DocPermSetsEditor(doc, user)

  // Check if current user can change permission set for the role
  // if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(radioButtonDocumentPermissionSetType, roleId, document ))
  // This can be defined perd doc, so - change must be single select!!
  // TODO: Check!! ??? Mapped roles might contain: if (DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) || RoleId.SUPERADMIN.equals(roleId)
  // TODO: !!!
  // security
  // ui.lytRestrictedPermSets.btnEditRestrictedOnePermSet setReadOnly !user.canDefineRestrictedOneFor(doc)
  // ui.lytRestrictedPermSets.btnEditRestrictedTwoPermSet setReadOnly !user.canDefineRestrictedTwoFor(doc)
  // ui.chkLim1IsMorePrivilegedThanLim2 setReadOnly !user.isSuperAdminOrHasFullPermissionOn(doc)
  override val view = new AccessEditorView |>> { editorWidget =>
    editorWidget.perms.miRoleAdd.setCommandHandler { _ =>
      val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
      val assignedRoles = editorWidget.perms.tblRolesPermSets.itemIds.asScala.toSet
      val availableRolesWithPermsSetTypes: Map[RoleDomainObject, List[DocumentPermissionSetTypeDomainObject]] =
        (for {
          role <- roleMapper.getAllRoles
          roleId = role.getId
          if !(role.getId == RoleId.SUPERADMIN || assignedRoles.contains(role))
          setTypes = types.filter(user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(_, roleId, doc))
          if setTypes.nonEmpty
        } yield role -> setTypes)(breakOut)

      if (availableRolesWithPermsSetTypes.isEmpty) {
        Current.page.showWarningNotification("No roles available")
      } else {
        new OkCancelDialog("Add role") |>> { dlg =>
          val availableRoles = availableRolesWithPermsSetTypes.keySet
          dlg.mainComponent = new AddRolePermSetDialogView |>> { c =>
            availableRoles.foreach { role => c.cbRole.addItem(role, role.getName) }

            c.cbRole.addValueChangeHandler { _ =>
              val availablePermSetTypes = availableRolesWithPermsSetTypes(c.cbRole.selection)
              types.foreach { typeSet =>
                c.ogPermsSetType.setItemEnabled(typeSet, availablePermSetTypes contains typeSet)
              }

              c.ogPermsSetType.selection = availablePermSetTypes.head
            }

            c.cbRole.selection = availableRoles.head

            dlg.setOkButtonHandler {
              val role = c.cbRole.selection
              val setType = c.ogPermsSetType.selection

              addRolePermSetType(role, setType)
            }
          }
        } |> Current.ui.addWindow
      }
    }


    editorWidget.perms.miRoleChangePermSet.setCommandHandler { _ =>
      whenSingleton(editorWidget.perms.tblRolesPermSets.value.asScala.toSeq) { role =>
        types.filter(setType => user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(setType, role.getId, doc)) match {
          case Nil => Current.page.showWarningNotification("You are not allowed to edit this role")
          case availableSetTypes =>
            new OkCancelDialog("Change Role Permissions") |>> { dlg =>
              dlg.mainComponent = new ChangeRolePermSetDialogView |>> { c =>
                c.lblRole.value = role.getName

                c.ogPermsSetType.selection = editorWidget.perms.tblRolesPermSets
                  .item(role)
                  .getItemProperty(RolePermSetPropertyId).getValue.asInstanceOf[RolePermSet].setType

                types.foreach { setType =>
                  c.ogPermsSetType.setItemEnabled(setType, availableSetTypes contains setType)
                }

                dlg.setOkButtonHandler {
                  setRolePermSetType(role, c.ogPermsSetType.selection)
                }
              }
            } |> Current.ui.addWindow
        }
      }
    }

    editorWidget.perms.miRoleRemove.setCommandHandler { _ =>
      whenSelected(editorWidget.perms.tblRolesPermSets) { roles =>
        roles.asScala.foreach(editorWidget.perms.tblRolesPermSets.removeItem)
      }
    }

    editorWidget.perms.miEditPermSets.setCommandHandler { _ =>
      new OkCancelDialog("Permissions") |>> { dlg =>
        dlg.mainComponent = permSetsEditor.view
      } |> Current.ui.addWindow
    }
  }

  resetValues()


  override def resetValues() {
    val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
    val allButSuperadminRole = roleMapper.getAllRoles
      .filterNot(_.getId == RoleId.SUPERADMIN)
      .map(role => (role.getId, role))(breakOut) : Map[RoleId, RoleDomainObject]

    view.perms.tblRolesPermSets.removeAllItems()

    for {
      mapping <- initialValues.rolesPermissions.getMappings
      roleId = mapping.getRoleId
      role <- allButSuperadminRole.get(roleId)
      setType = mapping.getDocumentPermissionSetType
      if setType != NONE
    } {
      addRolePermSetType(role, setType)
    }

    view.misc.chkShareWithOtherAdmins.checked = initialValues.isLinkableByOtherUsers
    view.misc.chkShowToUnauthorizedUser.checked = initialValues.isLinkedForUnauthorizedUsers

    permSetsEditor.resetValues()

    doc match {
      case textDoc: TextDocumentDomainObject =>
      case _ =>
    }
  }


  private def addRolePermSetType(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
    view.perms.tblRolesPermSets.addItem(Array[AnyRef](RolePermSet(role, setType)), role)
  }

  private def setRolePermSetType(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
    view.perms.tblRolesPermSets.item(role)
      .getItemProperty(RolePermSetPropertyId)
      .asInstanceOf[Property[RolePermSet]].value = RolePermSet(role, setType)
  }


  override def collectValues(): ErrorsOrData = {
    val permSetsValues = permSetsEditor.collectValues().right.get

    Right(
      Data(
        new RoleIdToDocumentPermissionSetTypeMappings |>> { rolesPermissions =>
          import view.perms.tblRolesPermSets
          tblRolesPermSets.itemIds.asScala.foreach { role =>
            rolesPermissions.setPermissionSetTypeForRole(
              role.getId,
              tblRolesPermSets.getContainerProperty(role, RolePermSetPropertyId).getValue.asInstanceOf[RolePermSet].setType
            )
          }
        },
        permSetsValues.restrictedOnePermSet,
        permSetsValues.restrictedTwoPermSet,
        permSetsValues.isRestrictedOneMorePrivilegedThanRestrictedTwo,
        view.misc.chkShowToUnauthorizedUser.checked,
        view.misc.chkShareWithOtherAdmins.checked
      )
    )
  }
}