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
import com.vaadin.ui._

import DocumentPermissionSetTypeDomainObject.{NONE, FULL, READ, RESTRICTED_1, RESTRICTED_2}
import textdocument.TextDocumentDomainObject

// Discuss
//        if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document )) {
//            return Html.radio(name, value, checked ) ;
//        } else {
//            return checked ? Html.hidden( name, value )+"X" : "O" ;
//        }

/// todo: Change permission set vs Change Permssion set TYPE?????  vs change permissions type
/// todo: Move template management to "appearance" (or some other sheet)

/// todo: <td class="imcmsAdmText" nowrap><? global/Created_by ?>&nbsp;<i><%= Utility.formatUser(userMapper.getUser(document.getCreatorId())) %></i></td>

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
  private val doc = letret(new TextDocumentDomainObject){_ setMeta meta}

  case class State(
    rolesPermissions: RoleIdToDocumentPermissionSetTypeMappings = meta.getRoleIdToDocumentPermissionSetTypeMappings.clone(),
    limPermissionsSets: DocumentPermissionSets = meta.getPermissionSets.clone(),
    limPermissionsSetsForNewDoc: DocumentPermissionSets = meta.getPermissionSetsForNewDocuments.clone(),
    isLim1MorePrivilegedThanLim2: Boolean = meta.getRestrictedOneMorePrivilegedThanRestrictedTwo,
    isLinkedForUnauthorizedUsers: Boolean = meta.getLinkedForUnauthorizedUsers,
    isLinkableByOtherUsers: Boolean = meta.getLinkableByOtherUsers
  )

  private val initialState = State()
  // WHY EXPECT USER ROLE ????
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
        app.showWarningNotification("No (more) roles available")
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

              ui.rolesPermsSetTypeUI.tblRolesPermsTypes.addItem(Array[AnyRef](RolePermsSetType(role, setType)), role)
            }
          }
        }
      }
    }


    ui.rolesPermsSetTypeUI.miChangeRolePermSetType setCommandHandler {
      whenSingle(ui.rolesPermsSetTypeUI.tblRolesPermsTypes.value.toSeq) { role =>
        types.filter(user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(_, roleId, doc)) match {
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
                  ui.rolesPermsSetTypeUI.tblRolesPermsTypes.item(role)
                    .getItemProperty(RolePermsSetTypePropertyId)
                    .setValue(RolePermsSetType(role, c.ogPermsSetType.value))
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
      ui.rolesPermsSetTypeUI.tblRolesPermsTypes.addItem(Array[AnyRef](RolePermsSetType(role, setType)), role)
    }

    ui.frmExtraSettings.chkShareWithOtherAdmins.checked = initialState.isLinkableByOtherUsers
    ui.frmExtraSettings.chkShowToUnauthorizedUser.checked = initialState.isLinkedForUnauthorizedUsers

    ui.chkLim1IsMorePrivilegedThanLim2.checked = initialState.isLim1MorePrivilegedThanLim2
  }
}

      // if (user.canDefineRestrictedOneFor( document ))
      //   (document instanceof TextDocumentDomainObject)  FOR NEW

      //if (user.canDefineRestrictedTwoFor(document))
           //if (document instanceof TextDocumentDomainObject) FOR NEW

      //if (user.isSuperAdminOrHasFullPermissionOn(document))
        // checkbox privileged one over two

      //if (document instanceof TextDocumentDomainObject)
        // list templates


      // <% if (document.isLinkedForUnauthorizedUsers()) => Checked
      //<% if (document.isLinkableByOtherUsers()) {%>checked<% } %>> checked

      // JUST INFO
      //<%= Utility.formatUser(userMapper.getUser(document.getCreatorId())) %>


class PermissionsSheetUI extends VerticalLayout with Spacing with FullWidth {
  setMargin(true, true, false, true)

  class LimitedPermissionSetUI(caption: String = "") extends HorizontalLayout with Spacing with UndefinedSize {
    val btnEdit = new Button("Define".i) with LinkStyle
    val btnEditNew = new Button("Define for new document".i) with LinkStyle
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

  //!NB@ if (user.canDefineRestrictedOneFor( document ))
  //     if (user.canDefineRestrictedTwoFor(document))
  val lim1PermissionSet = new LimitedPermissionSetUI
  val lim2PermissionSet = new LimitedPermissionSetUI

  // NB@ if (user.isSuperAdminOrHasFullPermissionOn(document))
  val chkLim1IsMorePrivilegedThanLim2 = new CheckBox("Limited 1 is more privileged than limited 2")
  val rolesPermsSetTypeUI = new RolesPermsSetTypeUI

  // -----------
  // lim: Default template for new pages - list
  //------------

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


private class ChangeRolePermsSetTypeDialogMainUI extends FormLayout with UndefinedSize {
  val lblRole = letret(new Label with UndefinedSize){_ setCaption "Role"}
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect2[DocumentPermissionSetTypeDomainObject]

  List(READ, RESTRICTED_1, RESTRICTED_2, FULL) foreach { setType =>
    ogPermsSetType.addItem(setType, setType.toString.i)
  }

  addComponents(this, lblRole, ogPermsSetType)
}


private class EditLimPermsDialogMainUI extends FormLayout with UndefinedSize {
   //todo: What kind of meta?
  val chkEditMeta = new CheckBox("Permission to edit page meta data")
   //todo: What kind of roles/privilieges?
  val chkEditRoles = new CheckBox("Permission to edit privileges (roles)")
  val chkEditTexts = new CheckBox("Permission to edit texts")
  val chkEditImages = new CheckBox("Permission to edit images")
  val chkEditIncludes = new CheckBox("Permission to edit includes")

  val chkEditMenus = new CheckBox("Permission to edit menues")
  val chkEditTemplates = new CheckBox("Permission to edit templates")

  // add doc types in menus
  val lstDocTypes = new ListSelect with MultiSelect //Authorized document types:
  val lstTemplates = new ListSelect with MultiSelect //Authorized template groups:
}
//
//class LimitedPermissionsEditor {}
//
//class LimitedPermissionsSheetUI {}

/**
    if not text doc ->
ermission to edit page meta data
Permission to edit privileges (roles)
Permission to edit content    <<<<<

else

    public static final String REQUEST_PARAMETER__EDIT_PERMISSIONS = "editPermissions";
    public static final String REQUEST_PARAMETER__EDIT = "edit";
    public static final String REQUEST_PARAMETER__EDIT_DOCUMENT_INFORMATION = "editDocumentInformation";
    public static final String REQUEST_PARAMETER__EDIT_TEXTS = "editTexts";
    public static final String REQUEST_PARAMETER__EDIT_IMAGES = "editImages";
    public static final String REQUEST_PARAMETER__EDIT_INCLUDES = "editIncludes";
    public static final String REQUEST_PARAMETER__EDIT_MENUS = "editMenus";
    public static final String REQUEST_PARAMETER__ALLOWED_DOCUMENT_TYPE_IDS = "allowedDocumentTypeIds";
    public static final String REQUEST_PARAMETER__ALLOWED_TEMPLATE_GROUP_IDS = "allowedTemplateGroupIds";
    public static final String REQUEST_PARAMETER__EDIT_TEMPLATES = "editTemplates";
    public static final String REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID = "defaultTemplateId";
 */
