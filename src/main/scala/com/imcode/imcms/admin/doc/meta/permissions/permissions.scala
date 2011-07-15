package com.imcode
package imcms
package admin.doc.meta.permissions

import scala.collection.breakOut
import scala.collection.JavaConversions._
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.document._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcms.ImcmsServicesSupport
import DocumentPermissionSetTypeDomainObject.{NONE, FULL, READ, RESTRICTED_1, RESTRICTED_2}
import textdocument.TextDocumentDomainObject
import com.vaadin.ui._

// Discuss
//        if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document )) {
//            return Html.radio(name, value, checked ) ;
//        } else {
//            return checked ? Html.hidden( name, value )+"X" : "O" ;
//        }

// if (user.canDefineRestrictedOneFor(document))
// if (user.canDefineRestrictedTwoFor(document))

// ?????????????????????????????????????????????????????
// if (user.isSuperAdminOrHasFullPermissionOn(document))
   // checkbox privileged one over two


/// todo: Change permission set vs Change Permssion set TYPE?????  vs change permissions type
/// todo: Move template management to "appearance" (or some other sheet)


/// todo: remove / already present in main???:
//  <td class="imcmsAdmText" nowrap><? global/Created_by ?>&nbsp;<i><%= Utility.formatUser(userMapper.getUser(document.getCreatorId())) %></i></td>


/**
 * Doc's roles permissions settings.
 *
 * Document access is controlled at the (users) roles level.
 * Each role can have exactly one set of permissions per document.
 * -READ and FULL perm sets are non-customizable and always contain the same predefined permissions.
 * -Lim1 and Lim2 perm sets can be customized - i.e. predefined permissions can be added/removed to/from those sets.
 */
class PermissionsSheet(app: ImcmsApplication, meta: Meta, user: UserDomainObject) extends ImcmsServicesSupport {

  private val types = List(READ, RESTRICTED_1, RESTRICTED_2, FULL)

  // Stub doc
  // Legacy API uses DocumentDomainObject to access Meta data
  private val doc: DocumentDomainObject = letret(new TextDocumentDomainObject){_ setMeta meta}

  case class State(
    rolesPermissions: RoleIdToDocumentPermissionSetTypeMappings = meta.getRoleIdToDocumentPermissionSetTypeMappings.clone(),
    limPermissionsSets: DocumentPermissionSets = meta.getPermissionSets.clone(),
    limPermissionsSetsForNewDoc: DocumentPermissionSets = meta.getPermissionSetsForNewDocuments.clone(),
    isLim1MorePrivilegedThanLim2: Boolean = meta.getRestrictedOneMorePrivilegedThanRestrictedTwo,
    isLinkedForUnauthorizedUsers: Boolean = meta.getLinkedForUnauthorizedUsers,
    isLinkableByOtherUsers: Boolean = meta.getLinkableByOtherUsers
  )

  private val initialState = State()
  // Check if current user can change permission set for the role
  // if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document ))
  // This can be defined perd doc, so - change must be single select!!
  // Check!! ??? Mapped roles might contain: if ( DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) || RoleId.SUPERADMIN.equals(roleId)
  val ui = letret(new PermissionsSheetUI) { ui =>
    ui.rolesPermsSetTypeUI.miAddRole setCommandHandler {
      val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
      val assignedRoles = ui.rolesPermsSetTypeUI.tblRolesPermsTypes.itemIds.toSet
      val availableRolesWithPermsSetTypes: Map[RoleDomainObject, List[DocumentPermissionSetTypeDomainObject]] =
        (for {
          role <- roleMapper.getAllRoles
          roleId = role.getId
          if !(role.getId == RoleId.SUPERADMIN || assignedRoles.contains(role))
          setTypes = types.filter(user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(_, roleId, doc))
          if setTypes.nonEmpty
        } yield role -> setTypes)(breakOut)

      if (availableRolesWithPermsSetTypes.isEmpty) {
        app.showWarningNotification("No roles available")
      } else {
        ui.getApplication.initAndShow(new OkCancelDialog("Add role")) { dlg =>
          val availableRoles = availableRolesWithPermsSetTypes.keySet
          dlg.mainUI = letret(new AddRolePermsSetTypeDialogMainUI) { c =>
            availableRoles foreach { role => c.cbRole.addItem(role, role.getName) }

            c.cbRole.addValueChangeHandler {
              val availablePermSetTypes = availableRolesWithPermsSetTypes(c.cbRole.value)
              types foreach { typeSet =>
                c.ogPermsSetType.setItemEnabled(typeSet, availablePermSetTypes contains typeSet)
              }

              c.ogPermsSetType.value = availablePermSetTypes.head
            }

            c.cbRole.value = availableRoles.head

            dlg.wrapOkHandler {
              val role = c.cbRole.value
              val setType = c.ogPermsSetType.value

              addRolePermSetType(role, setType)
            }
          }
        }
      }
    }


    ui.rolesPermsSetTypeUI.miChangeRolePermSetType setCommandHandler {
      whenSingle(ui.rolesPermsSetTypeUI.tblRolesPermsTypes.value.toSeq) { role =>
        types.filter(setType => user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(setType, role.getId, doc)) match {
          case Nil => app.showWarningNotification("You are not allowed to edit this role")
          case availableSetTypes =>
            app.initAndShow(new OkCancelDialog("Change role permissions type")) { dlg =>
              dlg.mainUI = letret(new ChangeRolePermsSetTypeDialogMainUI) { c =>
                c.lblRole.value = role.getName

                c.ogPermsSetType.value = ui.rolesPermsSetTypeUI.tblRolesPermsTypes
                  .item(role)
                  .getItemProperty(RolePermsSetTypePropertyId).getValue.asInstanceOf[RolePermsSetType].setType

                types foreach {setType => c.ogPermsSetType.setItemEnabled(setType, availableSetTypes contains setType)}

                dlg.wrapOkHandler {
                  setRolePermSetType(role, c.ogPermsSetType.value)
                }
              }
            }
        }
      }
    }

    ui.rolesPermsSetTypeUI.miRemoveRole setCommandHandler {
      whenSelected(ui.rolesPermsSetTypeUI.tblRolesPermsTypes) { roles =>
        roles foreach (ui.rolesPermsSetTypeUI.tblRolesPermsTypes.removeItem)
      }
    }
  }

  revert()


  def revert() {
    val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
    val allButSuperadminRole = roleMapper.getAllRoles
      .filterNot(_.getId == RoleId.SUPERADMIN)
      .map(role => (role.getId, role))(breakOut) : Map[RoleId, RoleDomainObject]

    ui.rolesPermsSetTypeUI.tblRolesPermsTypes.removeAllItems()

    for {
      mapping <- initialState.rolesPermissions.getMappings
      roleId = mapping.getRoleId
      role <- allButSuperadminRole.get(roleId)
      setType = mapping.getDocumentPermissionSetType
      if setType != NONE
    } {
      addRolePermSetType(role, setType)
    }

    ui.frmExtraSettings.chkShareWithOtherAdmins.checked = initialState.isLinkableByOtherUsers
    ui.frmExtraSettings.chkShowToUnauthorizedUser.checked = initialState.isLinkedForUnauthorizedUsers

    ui.chkLim1IsMorePrivilegedThanLim2.checked = initialState.isLim1MorePrivilegedThanLim2

    doc match {
      case textDoc: TextDocumentDomainObject =>
      case _ =>
    }
  }


  private def addRolePermSetType(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
    ui.rolesPermsSetTypeUI.tblRolesPermsTypes.addItem(Array[AnyRef](RolePermsSetType(role, setType)), role)
  }

  private def setRolePermSetType(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
    ui.rolesPermsSetTypeUI.tblRolesPermsTypes.item(role)
      .getItemProperty(RolePermsSetTypePropertyId)
      .setValue(RolePermsSetType(role, setType))
  }
}

      // <% if (document.isLinkedForUnauthorizedUsers()) => Checked
      //<% if (document.isLinkableByOtherUsers()) {%>checked<% } %>> checked


class PermissionsSheetUI extends VerticalLayout with Spacing with FullWidth {
  setMargin(true, true, false, true)

  class LimPermsSetUI(caption: String = "") extends HorizontalLayout with Spacing with UndefinedSize {
    val btnEdit = new Button("Edit".i) with SmallStyle

    addComponents(this, btnEdit)
  }


  class RolesPermsSetTypeUI extends VerticalLayout with Spacing with FullWidth {
    val mb = new MenuBar
    val miAddRole = mb.addItem("Add role")
    val miRemoveRole = mb.addItem("Remove role")
    val miChangeRolePermSetType = mb.addItem("Change permissions set")

    val tblRolesPermsTypes = letret(new Table with MultiSelect2[RoleDomainObject] with Immediate with FullWidth with Selectable) { tbl =>
      tbl.setPageLength(7)

      addContainerProperties(tbl,
        CP[RolePermsSetType](RolePermsSetTypePropertyId))

      tbl.setColumnHeader(RolePermsSetTypePropertyId, "Role")

      for (setType <- Seq(READ, RESTRICTED_1, RESTRICTED_2, FULL)) {
        tbl.addGeneratedColumn(setType, new RolePermsSetTypeTableColumnGenerator(setType))
        tbl.setColumnHeader(setType, setType.toString)
      }
    }

    addComponents(this, mb, tblRolesPermsTypes)
  }

  //!NB@ Only if this is a text doc.
  //     if (user.canDefineRestrictedOneFor( document ))
  //     if (user.canDefineRestrictedTwoFor(document))
  val lim1PermissionSet = new LimPermsSetUI("Limited 1")
  val lim2PermissionSet = new LimPermsSetUI("Linited 2")

  // NB@ if (user.isSuperAdminOrHasFullPermissionOn(document))
  val chkLim1IsMorePrivilegedThanLim2 = new CheckBox("Limited 1 is more privileged than limited 2")
  val rolesPermsSetTypeUI = new RolesPermsSetTypeUI

  val frmExtraSettings = new Form with FullWidth {
    setCaption("Extra settings")

    val chkShowToUnauthorizedUser = new CheckBox("Unauthorized user can see link(s) to this document")
    val chkShareWithOtherAdmins = new CheckBox("Share the document with other administrators")

    addComponents(getLayout, chkShowToUnauthorizedUser, chkShareWithOtherAdmins)
  }


  addComponents(this, rolesPermsSetTypeUI, lim1PermissionSet, lim2PermissionSet, frmExtraSettings)
}


private object RolePermsSetTypePropertyId


private case class RolePermsSetType(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
  override def toString = role.getName
}


/**
 * Role permission type table column generator for properties ids of type DocumentPermissionSetTypeDomainObject.
 * Vaddin (bug?) warning:
 *   For some reasons when adding items to table, values are not available (assigned) immediately, hence
 *   Property.getValue returns null.
 */
private class RolePermsSetTypeTableColumnGenerator(setType: DocumentPermissionSetTypeDomainObject) extends Table.ColumnGenerator {
  def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = letret(new Label with UndefinedSize) {
    val rolePermissionSetType = source.getItem(itemId)
      .getItemProperty(RolePermsSetTypePropertyId)
      .getValue.asInstanceOf[RolePermsSetType]

    _.value = if (rolePermissionSetType != null && rolePermissionSetType.setType == setType) "X" else ""
  }
}





private class AddRolePermsSetTypeDialogMainUI extends FormLayout with UndefinedSize {
  val cbRole = new ComboBox("Role") with SingleSelect2[RoleDomainObject] with NoNullSelection with Immediate
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect2[DocumentPermissionSetTypeDomainObject]

  List(READ, RESTRICTED_1, RESTRICTED_2, FULL) foreach { setType =>
    ogPermsSetType.addItem(setType, setType.toString.i)
  }

  addComponents(this, cbRole, ogPermsSetType)
}

/**
 * Changes permission set type for a role.
 */
private class ChangeRolePermsSetTypeDialogMainUI extends FormLayout with UndefinedSize {
  val lblRole = letret(new Label with UndefinedSize){_ setCaption "Role"}
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect2[DocumentPermissionSetTypeDomainObject]

  List(READ, RESTRICTED_1, RESTRICTED_2, FULL) foreach { setType =>
    ogPermsSetType.addItem(setType, setType.toString.i)
  }

  addComponents(this, lblRole, ogPermsSetType)
}


/**
 * Doc's restricted permission set
 */
trait DocRestrictedPermSetEditorUI extends FormLayout with UndefinedSize {

  // Decoration; always checked and read only
  val chkRead = new CheckBox("Permission to view content") with Checked with ReadOnly

  val chkEditMeta = new CheckBox("Permission to edit properties")

  val chkEditRoles = new CheckBox("Permission to edit permissions")
}


/**
 * Any but 'text doc' limited permission set editor.
 */
class NonTextDocRestrictedPermSetEditor(permSet: DocumentPermissionSetDomainObject) extends ImcmsServicesSupport {
  val ui = letret(new NonTextDocRestrictedPermSetEditorUI) { ui =>
  }

  def revert() {
    ui.chkEditMeta.checked = permSet.getEditDocumentInformation
    ui.chkEditRoles.checked = permSet.getEditPermissions
    ui.chkEditContent.checked = permSet.getEdit
  }
}


/**
 * Text doc limited permission set editor.
 */
class TextDocRestrictedPermSetEditor(permSet: TextDocumentPermissionSetDomainObject, user: UserDomainObject) extends ImcmsServicesSupport {

  val ui = letret(new TextDocRestrictedPermSetEditorUI) { ui =>
  }

  def revert() {
    // Authorized document types
    val selectedTypeIds = permSet.getAllowedDocumentTypeIds
    for ((typeId, typeName) <- imcmsServices.getDocumentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user)) {
      ui.tcsDocTypes.addItem(typeId, typeName)
      if (selectedTypeIds contains typeId) ui.tcsDocTypes.select(typeId)
    }

    // template groups
    val selectedGroupIds = permSet.getAllowedTemplateGroupIds
    for (group <- imcmsServices.getTemplateMapper.getAllTemplateGroups) {
      ui.tcsTemplates.addItem(group.getName)
      if (selectedGroupIds contains group.getId) ui.tcsTemplates.select(group.getName)
    }

    ui.chkEditMeta.checked = permSet.getEditDocumentInformation
    ui.chkEditRoles.checked = permSet.getEditPermissions

    ui.chkEditTemplates.checked = permSet.getEditTemplates
    ui.chkEditTexts.checked = permSet.getEditTexts
    ui.chkEditImages.checked = permSet.getEditImages
    ui.chkEditMenus.checked = permSet.getEditMenus
    ui.chkEditIncludes.checked = permSet.getEditIncludes
  }
}


class TextDocRestrictedPermSetEditorUI extends DocRestrictedPermSetEditorUI {

  val chkEditTexts = new CheckBox("Permission to edit texts")
  val chkEditImages = new CheckBox("Permission to edit images")
  val chkEditIncludes = new CheckBox("Permission to edit includes")
  val chkEditMenus = new CheckBox("Permission to edit menues")
  val chkEditTemplates = new CheckBox("Permission to edit templates")

  // add doc types in menus
  // item caption is a type name in a user language
  val tcsDocTypes = new TwinColSelect("Authorized document types") with MultiSelect2[DocTypeId]
  val tcsTemplates = new TwinColSelect("Authorized template groups") with MultiSelect2[String]
}


/**
 * Non text doc limited permissions.
 */
class NonTextDocRestrictedPermSetEditorUI extends DocRestrictedPermSetEditorUI {

  val chkEditContent = new CheckBox("Edit content")
}






