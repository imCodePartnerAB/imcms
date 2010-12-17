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
class PermissionsEditor(meta: Meta, user: UserDomainObject) {
  val ui = letret(new PermissionsEditorUI) { ui =>
    val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
    val allButSuperadminRole = roleMapper.getAllRoles.filter(_.getId == RoleId.SUPERADMIN)
    val roleIdToPermissionSetType = meta.getRoleIdToDocumentPermissionSetTypeMappings
    val mappings = roleIdToPermissionSetType.getMappings

    mappings foreach { mapping =>
      val roleId = mapping.getRoleId
      val role = roleMapper.getRole(roleId)

      if (role != null) {

        val documentPermissionSetType = mapping.getDocumentPermissionSetType
        if (DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) || RoleId.SUPERADMIN.equals(roleId)) {
        } else {
          // allButSuperadminRole.remove( role ) ;
//                            %>
//                            <tr align="center">
//                                <td height="22" class="imcmsAdmText" align="left"><% if (user.hasRoleId( roleId )) { %>*<% } else { %>&nbsp;<% } %></td>
//                                <td class="imcmsAdmText" align="left"><%= role.getName() %></td>
//                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.NONE, user, documentPermissionSetType, roleId, document ) %></td>
//                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.READ, user, documentPermissionSetType, roleId, document ) %></td>
//                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.RESTRICTED_2, user, documentPermissionSetType, roleId, document ) %></td>
//                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.RESTRICTED_1, user, documentPermissionSetType, roleId, document ) %></td>
//                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.FULL, user, documentPermissionSetType, roleId, document ) %></td>
//                            </tr>
//                            <%
//
//    String formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject radioButtonDocumentPermissionSetType, UserDomainObject user, DocumentPermissionSetTypeDomainObject documentPermissionSetType,
//                                            RoleId roleId,
//                                            DocumentDomainObject document ) {
//        boolean checked = documentPermissionSetType == radioButtonDocumentPermissionSetType;
//        String name = "role_" + roleId.intValue();
//        String value = "" + radioButtonDocumentPermissionSetType;
//        if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document )) {
//            return Html.radio(name, value, checked ) ;
//        } else {
//            return checked ? Html.hidden( name, value )+"X" : "O" ;
//        }
//    }

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
  }
}
}

class PermissionsEditorUI extends VerticalLayout with Spacing {
  class LimitedSettings extends HorizontalLayout with Spacing with UndefinedSize {
    val btnEdit = new Button("Define") with LinkStyle
    val btnEditNew = new Button("Define for new document") with LinkStyle
  }

  val lim1Settings = new LimitedSettings
  val lim2Settings = new LimitedSettings

  // chk box Limited 1 is more privileged than limited 2.
  // lim: Default template for new pages - list
  //------------
  // todo: add filter UI
  val tblRolesPermissions = new Table with ValueType[RoleId] with SingleSelect with Immediate with Reloadable

  addContainerProperties(tblRolesPermissions,
    ContainerProperty[String]("Role"),
    ContainerProperty[String]("None"),
    ContainerProperty[String]("Read"),
    ContainerProperty[String]("Lim-1"),
    ContainerProperty[JBoolean]("Lim-2"),
    ContainerProperty[JBoolean]("Full"))

  // share
  //Share
	// chk Show link to unauthorized users
	// chk Share the document with other administrators
  // label: Created by Super, Admin (admin)
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
