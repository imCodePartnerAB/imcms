package com.imcode
package imcms
package admin.doc.meta.permissions

import scala.collection.breakOut
import imcms.mapping.DocumentSaver
import scala.collection.JavaConversions._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.document._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcms.ImcmsServicesSupport
import com.vaadin.ui._
import servlet.admin.SaveText

//todo: check user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document )
    // user role??

//        if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document )) {
//            return Html.radio(name, value, checked ) ;
//        } else {
//            return checked ? Html.hidden( name, value )+"X" : "O" ;
//        }



class PermissionsSheet(app: ImcmsApplication, meta: Meta, user: UserDomainObject) extends ImcmsServicesSupport {
  import DocumentPermissionSetTypeDomainObject.{NONE, FULL, READ, RESTRICTED_1, RESTRICTED_2}

  case class State(
    rolesPermissions: RoleIdToDocumentPermissionSetTypeMappings = meta.getRoleIdToDocumentPermissionSetTypeMappings.clone(),
    limPermissionsSets: DocumentPermissionSets = meta.getPermissionSets.clone(),
    limPermissionsSetsForNewDoc: DocumentPermissionSets = meta.getPermissionSetsForNewDocuments.clone(),
    isLim1MorePrivilegedThanLim2: Boolean = meta.getRestrictedOneMorePrivilegedThanRestrictedTwo,
    isLinkedForUnauthorizedUsers: Boolean = meta.getLinkedForUnauthorizedUsers,
    isLinkableByOtherUsers: Boolean = meta.getLinkableByOtherUsers
  )

  private val initialState = State()
  
  val ui = letret(new PermissionsSheetUI) { ui =>
    ui.rolesPermissionsUI.miAdd setCommandHandler {
      app.initAndShow(new OkCancelDialog("Add role permission")) { dlg =>
        dlg.mainUI = letret(new AddRolePermissionDialogContent) { c =>
          c.ogPermission.setValue("Read")

          val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
          val selectedRolesIds = ui.rolesPermissionsUI.tblRolesPermissions.itemIds.toSet

          for {
            role <- roleMapper.getAllRolesExceptUsersRole
            roleId = role.getId
            if !(roleId == RoleId.SUPERADMIN || selectedRolesIds.contains(roleId))
          } c.lstRoles.addItem(roleId, role.getName)

          dlg.wrapOkHandler {
            //roleIdToPermissionSetType.setPermissionSetTypeForRole(c.lstRoles.value, READ)
            //ui.tblRolesPermissions.reload()
          }
        }
      }
    }

    ui.rolesPermissionsUI.miEdit setCommandHandler {
      whenSelected(ui.rolesPermissionsUI.tblRolesPermissions) { roleIds =>
        val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
        val roles = roleIds map roleMapper.getRole

        app.initAndShow(new OkCancelDialog("Change permission for selected role(s)")) { dlg =>
          dlg.mainUI = letret(new EditRolePermissionDialogContent) { c =>
            roles foreach (role => c.lstRoles.addItem(role.getId, role.getName))

            c.ogPermission.setValue("Read")

            dlg.wrapOkHandler {

              roles foreach { role =>

              }

              //ui.tblRolesPermissions.reload()
            }
          }
        }
      }
    }

    ui.rolesPermissionsUI.miDelete setCommandHandler {
      whenSelected(ui.rolesPermissionsUI.tblRolesPermissions) { roleIds =>
      }
    }
  }

  revert()


  def revert() {
    val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
    val allButSuperadminRole = roleMapper.getAllRoles
      .filterNot(_.getId == RoleId.SUPERADMIN)
      .map(role => (role.getId, role))(breakOut) : Map[RoleId, RoleDomainObject]

    val types = List(READ, RESTRICTED_1, RESTRICTED_2, FULL)

    ui.rolesPermissionsUI.tblRolesPermissions.removeAllItems()

    for {
      mapping <- initialState.rolesPermissions.getMappings
      roleId = mapping.getRoleId
      role <- allButSuperadminRole.get(roleId)
      documentPermissionSetType = mapping.getDocumentPermissionSetType
      if documentPermissionSetType != NONE
    } {
      ui.rolesPermissionsUI.tblRolesPermissions.addItem(
        (role.getName :: types.map(t => if (t == documentPermissionSetType) "X" else "")).toArray[AnyRef],
        roleId
      )
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

  class RolesPermissionsUI extends VerticalLayout with Spacing with FullWidth {
    //val txtRole = new TextField("Role name")
    //val twsPermissionSet = new TwinColSelect("")

    val tblRolesPermissions = letret(new Table with MultiSelect2[RoleId] with Immediate with FullWidth with Selectable) { tbl =>
      tbl.setPageLength(7)
      addContainerProperties(tbl,
        CP[String]("Role".i),
        CP[String]("Read".i),
        CP[String]("Limited-1"),
        CP[String]("Limited-2"),
        CP[String]("Full"))
    }

    val mb = new MenuBar
    val miAdd = mb.addItem("Add")
    val miEdit = mb.addItem("Edit")
    val miDelete = mb.addItem("Delete")

    addComponents(this, mb, tblRolesPermissions)
  }

  val lim1PermissionSet = new LimitedPermissionSetUI
  val lim2PermissionSet = new LimitedPermissionSetUI
  val chkLim1IsMorePrivilegedThanLim2 = new CheckBox("Limited 1 is more privileged than limited 2")
  val rolesPermissionsUI = new RolesPermissionsUI

  // -----------
  // lim: Default template for new pages - list
  //------------

  val frmExtraSettings = new Form with FullWidth {
    setCaption("Extra settings")

    val chkShowToUnauthorizedUser = new CheckBox("Unauthorized user can see link(s) to this document")
    val chkShareWithOtherAdmins = new CheckBox("Share the document with other administrators")

    addComponents(getLayout, chkShowToUnauthorizedUser, chkShareWithOtherAdmins)
  }


  addComponents(this, rolesPermissionsUI, lim1PermissionSet, lim2PermissionSet, frmExtraSettings)
}


class AddRolePermissionDialogContent extends FormLayout with UndefinedSize {
  val lstRoles = new ListSelect("Roles") with SingleSelect2[RoleId] with NoNullSelection
  val ogPermission = new OptionGroup("Permission", Array("Read", "Limited-1", "Limited-2", "Full").toList) //with ValueType[RoleId]
              
  addComponents(this, lstRoles, ogPermission)
}


class EditRolePermissionDialogContent extends FormLayout with UndefinedSize {
  val lstRoles = new ListSelect("Roles".i) with XSelect[RoleId] with ReadOnly
  val ogPermission = new OptionGroup("Permission", Array("Read", "Limited-1", "Limited-2", "Full").toList)

  addComponents(this, lstRoles, ogPermission)
}

class LimitedPermissionsDialogContent extends FormLayout with UndefinedSize {
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
