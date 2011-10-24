package com.imcode
package imcms
package admin.doc.meta.permissions

import scala.collection.breakOut
import scala.collection.JavaConversions._
import com.imcode.imcms.api._
import imcode.server.user._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcms.ImcmsServicesSupport
import imcode.server.document.DocumentPermissionSetTypeDomainObject.{NONE, FULL, READ, RESTRICTED_1, RESTRICTED_2}
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.vaadin.Application
import com.vaadin.ui._
import imcode.server.document._


// Discuss
//        Managed templates in groups:
//          if checked??? is it used somewhere/somehow
//
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
class PermissionsEditor(app: Application, doc: DocumentDomainObject, user: UserDomainObject) extends Editor with ImcmsServicesSupport {
  type DataType = Values

  private val meta = doc.getMeta.clone
  private val types = List(READ, RESTRICTED_1, RESTRICTED_2, FULL)

  case class Values(
    rolesPermissions: RoleIdToDocumentPermissionSetTypeMappings = meta.getRoleIdToDocumentPermissionSetTypeMappings.clone,
    restrictedOnePermSet: TextDocumentPermissionSetDomainObject = meta.getPermissionSets.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject],
    restrictedTwoPermSet: TextDocumentPermissionSetDomainObject = meta.getPermissionSets.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject],
    isRestrictedOneMorePrivilegedThanRestricted2: Boolean = meta.getRestrictedOneMorePrivilegedThanRestrictedTwo,
    isLinkedForUnauthorizedUsers: Boolean = meta.getLinkedForUnauthorizedUsers,
    isLinkableByOtherUsers: Boolean = meta.getLinkableByOtherUsers
  )

  private val initialValues = Values()

  // Initialized in revert()
  // Might be edited in their own pop-up dialogs
  private var restrictedOnePermSet: TextDocumentPermissionSetDomainObject = _
  private var restrictedTwoPermSet: TextDocumentPermissionSetDomainObject = _

  // Check if current user can change permission set for the role
  // if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document ))
  // This can be defined perd doc, so - change must be single select!!
  // Check!! ??? Mapped roles might contain: if ( DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) || RoleId.SUPERADMIN.equals(roleId)
  val ui = letret(new PermissionsEditorUI) { ui =>
    // security
    ui.lytRestrictedPermSets.btnEditRestrictedOnePermSet setReadOnly !user.canDefineRestrictedOneFor(doc)
    ui.lytRestrictedPermSets.btnEditRestrictedTwoPermSet setReadOnly !user.canDefineRestrictedTwoFor(doc)
    ui.chkLim1IsMorePrivilegedThanLim2 setReadOnly !user.isSuperAdminOrHasFullPermissionOn(doc)

    ui.lytRestrictedPermSets.btnEditRestrictedOnePermSet.addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Restriction one")) { dlg =>
        val editor = doc match {
          case _: TextDocumentDomainObject =>
            new DocRestrictedPermSetEditor(restrictedOnePermSet, doc, user) with TextDocRestrictedPermSetEditor
          case _ =>
            new DocRestrictedPermSetEditor(restrictedOnePermSet, doc, user) with NonTextDocRestrictedPermSetEditor
        }

        dlg.wrapOkHandler {
          restrictedOnePermSet = editor.state
        }

        dlg.mainUI = editor.ui
      }
    }

    ui.lytRestrictedPermSets.btnEditRestrictedTwoPermSet.addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Restriction two")) { dlg =>
        val editor = doc match {
          case _: TextDocumentDomainObject =>
            new DocRestrictedPermSetEditor(restrictedTwoPermSet, doc, user) with TextDocRestrictedPermSetEditor
          case _ =>
            new DocRestrictedPermSetEditor(restrictedTwoPermSet, doc, user) with NonTextDocRestrictedPermSetEditor
        }

        dlg.wrapOkHandler {
          restrictedTwoPermSet = editor.state
        }

        dlg.mainUI = editor.ui
      }
    }

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

  def revert() {
    restrictedOnePermSet = meta.getPermissionSets.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject]
    restrictedTwoPermSet = meta.getPermissionSets.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject]

    val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
    val allButSuperadminRole = roleMapper.getAllRoles
      .filterNot(_.getId == RoleId.SUPERADMIN)
      .map(role => (role.getId, role))(breakOut) : Map[RoleId, RoleDomainObject]

    ui.rolesPermsSetTypeUI.tblRolesPermsTypes.removeAllItems()

    for {
      mapping <- initialValues.rolesPermissions.getMappings
      roleId = mapping.getRoleId
      role <- allButSuperadminRole.get(roleId)
      setType = mapping.getDocumentPermissionSetType
      if setType != NONE
    } {
      addRolePermSetType(role, setType)
    }

    ui.frmExtraSettings.chkShareWithOtherAdmins.checked = initialValues.isLinkableByOtherUsers
    ui.frmExtraSettings.chkShowToUnauthorizedUser.checked = initialValues.isLinkedForUnauthorizedUsers

    ui.chkLim1IsMorePrivilegedThanLim2.checked = initialValues.isRestrictedOneMorePrivilegedThanRestricted2

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


  val data = new Data {
    def get() = Right(
      Values(
        letret(new RoleIdToDocumentPermissionSetTypeMappings) { rolesPermissions =>
          import ui.rolesPermsSetTypeUI.tblRolesPermsTypes
          tblRolesPermsTypes.itemIds foreach { role =>
            rolesPermissions.setPermissionSetTypeForRole(
              role.getId,
              tblRolesPermsTypes.getContainerProperty(role, RolePermsSetTypePropertyId).getValue.asInstanceOf[RolePermsSetType].setType
            )
          }
        },
        restrictedOnePermSet, // clone ???
        restrictedTwoPermSet, // clone ???
        ui.chkLim1IsMorePrivilegedThanLim2.checked,
        ui.frmExtraSettings.chkShowToUnauthorizedUser.checked,
        ui.frmExtraSettings.chkShareWithOtherAdmins.checked
      )
    )
  }

  // init
  revert()
}



class PermissionsEditorUI extends VerticalLayout with Spacing with FullWidth {
  setMargin(true, true, false, true)

  class RolesPermsSetTypeUI extends VerticalLayout with Spacing with FullWidth {
    val mb = new MenuBar
    val miAddRole = mb.addItem("Add role")
    val miRemoveRole = mb.addItem("Remove role")
    val miChangeRolePermSetType = mb.addItem("Change permissions set")

    val tblRolesPermsTypes = letret(new Table with MultiSelect[RoleDomainObject] with Immediate with FullWidth with Selectable) { tbl =>
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

  val chkLim1IsMorePrivilegedThanLim2 = new CheckBox("Limited 1 is more privileged than limited 2")
  val rolesPermsSetTypeUI = new RolesPermsSetTypeUI

  val frmExtraSettings = new Form with FullWidth {
    setCaption("Extra settings")

    val chkShowToUnauthorizedUser = new CheckBox("Unauthorized user can see link(s) to this document")
    val chkShareWithOtherAdmins = new CheckBox("Share the document with other administrators")

    addComponents(getLayout, chkShowToUnauthorizedUser, chkShareWithOtherAdmins)
  }

  val lytRestrictedPermSets = new GridLayout(2, 2) with Spacing with UndefinedSize with MiddleLeftAlignment {
    val btnEditRestrictedOnePermSet = new Button("permissions".i) with SmallStyle
    val btnEditRestrictedTwoPermSet = new Button("permissions".i) with SmallStyle

    addComponents(this, new Label("Limited-1"), btnEditRestrictedOnePermSet, new Label("Limited-2"), btnEditRestrictedTwoPermSet)
  }

  addComponents(this, rolesPermsSetTypeUI, lytRestrictedPermSets, frmExtraSettings)
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
  val cbRole = new ComboBox("Role") with SingleSelect[RoleDomainObject] with NoNullSelection with Immediate
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect[DocumentPermissionSetTypeDomainObject]

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
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect[DocumentPermissionSetTypeDomainObject]

  List(READ, RESTRICTED_1, RESTRICTED_2, FULL) foreach { setType =>
    ogPermsSetType.addItem(setType, setType.toString.i)
  }

  addComponents(this, lblRole, ogPermsSetType)
}


/**
 * State of this dialog is represented by {@link TextDocumentPermissionSetDomainObject}
 */
abstract class DocRestrictedPermSetEditor(
      protected val permSet: TextDocumentPermissionSetDomainObject,
      protected val doc: DocumentDomainObject,
      protected val user: UserDomainObject
    ) extends ImcmsServicesSupport {

  require(
    Seq(RESTRICTED_1, RESTRICTED_2) contains permSet.getType,
    "permSet type must be either RESTRICTED_1 or RESTRICTED_2 but was %s" format permSet.getTypeName)

  val ui: DocRestrictedPermSetEditorUI

  def state: TextDocumentPermissionSetDomainObject
}


/**
 * Any but text-doc restricted permission set editor.
 */
trait NonTextDocRestrictedPermSetEditor { this: DocRestrictedPermSetEditor =>
  val ui = letret(new NonTextDocRestrictedPermSetEditorUI) { ui =>
    ui.chkEditMeta.checked = permSet.getEditDocumentInformation
    ui.chkEditPermissions.checked = permSet.getEditPermissions
    ui.chkEditContent.checked = permSet.getEdit
  }

  def state = letret(new TextDocumentPermissionSetDomainObject(permSet.getType)) { ps =>
    ps.setEditDocumentInformation(ui.chkEditMeta.checked)
    ps.setEditPermissions(ui.chkEditPermissions.checked)
    ps.setEdit(ui.chkEditContent.checked)
  }
}


/**
 * Text doc restricted permission set editor.
 */
trait TextDocRestrictedPermSetEditor { this: DocRestrictedPermSetEditor =>
  val ui = letret(new TextDocRestrictedPermSetEditorUI) { ui =>
    // Authorized document types
    val selectedTypeIds = permSet.getAllowedDocumentTypeIds
    for ((typeId, typeName) <- imcmsServices.getDocumentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user)) {
      ui.tcsCreateDocsOfTypes.addItem(typeId, typeName)
      if (selectedTypeIds contains typeId) ui.tcsCreateDocsOfTypes.select(typeId)
    }

    // template groups
    val selectedGroupIds = permSet.getAllowedTemplateGroupIds
    for (group <- imcmsServices.getTemplateMapper.getAllTemplateGroups) {
      ui.tcsUseTemplatesFromTemplateGroups.addItem(group, group.getName)
      if (selectedGroupIds contains group.getId) ui.tcsUseTemplatesFromTemplateGroups.select(group)
    }

    ui.chkEditMeta.checked = permSet.getEditDocumentInformation
    ui.chkEditPermissions.checked = permSet.getEditPermissions
    ui.chkEditContent.checked = permSet.getEdit

    ui.chkEditTemplates.checked = permSet.getEditTemplates
    ui.chkEditImages.checked = permSet.getEditImages
    ui.chkEditMenus.checked = permSet.getEditMenus
    ui.chkEditIncludes.checked = permSet.getEditIncludes
  }

  def state = letret(new TextDocumentPermissionSetDomainObject(permSet.getType)) { ps =>
    ps.setEditDocumentInformation(ui.chkEditMeta.checked)
    ps.setEditPermissions(ui.chkEditPermissions.checked)
    ps.setEdit(ui.chkEditContent.checked)

    ps.setEditTemplates(ui.chkEditTemplates.checked)
    ps.setEditImages(ui.chkEditImages.checked)
    ps.setEditMenus(ui.chkEditMenus.checked)
    ps.setEditIncludes(ui.chkEditIncludes.checked)

    ps.setAllowedDocumentTypeIds(new java.util.HashSet(ui.tcsCreateDocsOfTypes.itemIds))
    ps.setAllowedTemplateGroupIds(new java.util.HashSet(ui.tcsUseTemplatesFromTemplateGroups.itemIds.map(Int box _.getId)))
  }
}


/**
 * Doc's common restricted permission set
 */
trait DocRestrictedPermSetEditorUI extends VerticalLayout with Spacing with UndefinedSize {

  // Decoration; always checked and read only
  val chkViewContent = new CheckBox("Permission to view content") with Checked with ReadOnly
  val chkEditContent = new CheckBox("Permission to edit content")
  val chkEditMeta = new CheckBox("Permission to to edit properties")
  val chkEditPermissions = new CheckBox("Permission to edit permissions")
}


class TextDocRestrictedPermSetEditorUI extends DocRestrictedPermSetEditorUI {

  val chkEditImages = new CheckBox("Permission to edit images")
  val chkEditIncludes = new CheckBox("Permission to edit includes")
  val chkEditMenus = new CheckBox("Permission to edit menues")
  val chkEditTemplates = new CheckBox("Permission to change templates")

  // item caption is a type name in a user language
  val tcsCreateDocsOfTypes = new TwinColSelect("Permission to create documents") with MultiSelect[DocTypeId] { setRows(5) }
  val tcsUseTemplatesFromTemplateGroups = new TwinColSelect("Permission to use templates from groups") with MultiSelect[TemplateGroupDomainObject] { setRows(5) }

  chkEditContent.setCaption("Permission to edit content (texts)")

  addComponents(this, chkViewContent, chkEditMeta, chkEditPermissions, chkEditContent, chkEditIncludes, chkEditMenus, chkEditTemplates, tcsCreateDocsOfTypes, tcsUseTemplatesFromTemplateGroups)
}


/**
 * Non text doc limited permissions.
 */
class NonTextDocRestrictedPermSetEditorUI extends DocRestrictedPermSetEditorUI {

  addComponents(this, chkViewContent, chkEditContent, chkEditMeta, chkEditPermissions)
}