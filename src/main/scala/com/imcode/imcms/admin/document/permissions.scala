package com.imcode
package imcms.admin.document

import imcms.mapping.DocumentSaver
import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.{Imcms}
import imcode.server.document._
import com.imcode.imcms.vaadin._

//todo: check user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document )
class PermissionsEditor(app: ImcmsApplication, meta: Meta, user: UserDomainObject) {
  import DocumentPermissionSetTypeDomainObject.{NONE, FULL, READ, RESTRICTED_1, RESTRICTED_2}
  
  val ui = letret(new PermissionsEditorUI) { ui =>
    val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
    val allButSuperadminRole = roleMapper.getAllRoles.filterNot(_.getId == RoleId.SUPERADMIN)
    val allButSuperadminRoleIds = allButSuperadminRole map { _.getId } 
    val roleIdToPermissionSetType = meta.getRoleIdToDocumentPermissionSetTypeMappings

    ui.tblRolesPermissions.itemsProvider = () => {
      val types = List(READ, RESTRICTED_1, RESTRICTED_2, FULL)

      for {
        mapping <- roleIdToPermissionSetType.getMappings.toSeq
        roleId = mapping.getRoleId
        role <- ?(roleMapper.getRole(roleId))
        documentPermissionSetType = mapping.getDocumentPermissionSetType
        if !(documentPermissionSetType == NONE || RoleId.SUPERADMIN == roleId)
      } yield 
          (roleId, role.getName :: types.map(t => if (t == documentPermissionSetType) "X" else ""))
    }

    ui.miRolePermissionAdd setCommand block {
      app.initAndShow(new OkCancelDialog("Add role permission")) { dlg =>
        let(dlg.setMainContent(new AddRolePermissionDialogContent)) { c =>
          c.ogPermission.setValue("Read")
          allButSuperadminRoleIds.filterNot(ui.tblRolesPermissions.itemIds contains) foreach { roleId =>
            c.lstRoles.addItem(roleId, roleMapper.getRole(roleId).getName)
          }

          dlg.setOkHandler {
            roleIdToPermissionSetType.setPermissionSetTypeForRole(c.lstRoles.value, READ)
            ui.tblRolesPermissions.reload()
          }
        }
      }
    }

    ui.miRolePermissionEdit setCommand block {

    }

    ui.miRolePermissionDelete setCommand block {

    }    
    // user role??

//        if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document )) {
//            return Html.radio(name, value, checked ) ;
//        } else {
//            return checked ? Html.hidden( name, value )+"X" : "O" ;
//        }

    ui.tblRolesPermissions.reload()
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

class PermissionsEditorUI extends VerticalLayout with Spacing {
  class CustomSettingsUI extends HorizontalLayout with Spacing with UndefinedSize {
    val btnEdit = new Button("Define") with LinkStyle
    val btnEditNew = new Button("Define for new document") with LinkStyle
  }

  val customSettings1 = new CustomSettingsUI
  val customSettings2 = new CustomSettingsUI

  // chk box Limited 1 is more privileged than limited 2.
  // lim: Default template for new pages - list
  //------------

  val mbRolesPermissions = new MenuBar
  val miRolePermissionAdd = mbRolesPermissions.addItem("Add", null)
  val miRolePermissionEdit = mbRolesPermissions.addItem("Edit", null)
  val miRolePermissionDelete = mbRolesPermissions.addItem("Delete", null)
  val tblRolesPermissions = new Table with ValueType[RoleId] with ItemIdType[RoleId] with SingleSelect with Immediate with Reloadable
//  val lytRoles = new VerticalLayout with Spacing {
//    addComponents(this, mbRole, tblRolesPermissions)
//  }

  addContainerProperties(tblRolesPermissions,
    ContainerProperty[String]("Role"),
    ContainerProperty[String]("Read"),
    ContainerProperty[String]("Limited-1"),
    ContainerProperty[String]("Limited-2"),
    ContainerProperty[String]("Full"))

  addComponents(this, mbRolesPermissions, tblRolesPermissions, customSettings1, customSettings2)

  // share
  //Share
	// chk Show link to unauthorized users
	// chk Share the document with other administrators
  // label: Created by Super, Admin (admin)
}

class AddRolePermissionDialogContent extends FormLayout {
  val lstRoles = new ListSelect("Roles") with SingleSelect2[RoleId] with NoNullSelection
  val ogPermission = new OptionGroup("Permission", Array("Read", "Limited-1", "Limited-2", "Full").toList) //with ValueType[RoleId]
              
  addComponents(this, lstRoles, ogPermission)
}

class EditRolePermissionDialogContent extends FormLayout {
  val lstRoles = new ListSelect("Roles")
  val ogPermission = new OptionGroup("Permission", Array("Read", "Limited-1", "Limited-2", "Full").toList)

  addComponents(this, lstRoles, ogPermission)
}

class LimitedPermissionsDialogContent extends FormLayout {
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
//class LimitedPermissionsEditorUI {}

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
