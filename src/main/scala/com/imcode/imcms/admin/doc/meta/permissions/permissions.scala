package com.imcode
package imcms
package admin.doc.meta.permissions

import scala.collection.breakOut
import scala.collection.JavaConverters._
import com.imcode.imcms.api._
import imcode.server.user._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcms.ImcmsServicesSupport
import imcode.server.document.DocumentPermissionSetTypeDomainObject.{NONE, FULL, READ, RESTRICTED_1, RESTRICTED_2}
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document._
import com.vaadin.ui._


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

/// todo: rename to access.scala
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
  private val meta = doc.getMeta.clone
  private val types = List(READ, RESTRICTED_1, RESTRICTED_2, FULL)

  case class Data(
    rolesPermissions: RoleIdToDocumentPermissionSetTypeMappings = meta.getRoleIdToDocumentPermissionSetTypeMappings.clone,
    restrictedOnePermSet: TextDocumentPermissionSetDomainObject = meta.getPermissionSets.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject],
    restrictedTwoPermSet: TextDocumentPermissionSetDomainObject = meta.getPermissionSets.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject],
    isRestrictedOneMorePrivilegedThanRestricted2: Boolean = meta.getRestrictedOneMorePrivilegedThanRestrictedTwo,
    isLinkedForUnauthorizedUsers: Boolean = meta.getLinkedForUnauthorizedUsers,
    isLinkableByOtherUsers: Boolean = meta.getLinkableByOtherUsers
  )

  private val initialValues = Data()

  // Initialized in revert()
  // Might be edited in their own pop-up dialogs
  private var restrictedOnePermSet: TextDocumentPermissionSetDomainObject = _
  private var restrictedTwoPermSet: TextDocumentPermissionSetDomainObject = _

  // Check if current user can change permission set for the role
  // if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document ))
  // This can be defined perd doc, so - change must be single select!!
  // Check!! ??? Mapped roles might contain: if ( DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) || RoleId.SUPERADMIN.equals(roleId)
  val ui = new AccessEditorUI |>> { ui =>
    // security
    ui.lytRestrictedPermSets.btnEditRestrictedOnePermSet setReadOnly !user.canDefineRestrictedOneFor(doc)
    ui.lytRestrictedPermSets.btnEditRestrictedTwoPermSet setReadOnly !user.canDefineRestrictedTwoFor(doc)
    ui.chkLim1IsMorePrivilegedThanLim2 setReadOnly !user.isSuperAdminOrHasFullPermissionOn(doc)

    ui.lytRestrictedPermSets.btnEditRestrictedOnePermSet.addClickHandler {
      ui.topWindow.initAndShow(new OkCancelDialog("Restriction one")) { dlg =>
        val editor = doc match {
          case _: TextDocumentDomainObject =>
            new PermSetEditor(restrictedOnePermSet, doc, user) with TextDocPermSetEditor
          case _ =>
            new PermSetEditor(restrictedOnePermSet, doc, user) with NonTextDocPermSetEditor
        }

        dlg.wrapOkHandler {
          restrictedOnePermSet = editor.collectValues().right.get
        }

        dlg.mainUI = editor.ui
      }
    }

    ui.lytRestrictedPermSets.btnEditRestrictedTwoPermSet.addClickHandler {
      ui.topWindow.initAndShow(new OkCancelDialog("Restriction two")) { dlg =>
        val editor = doc match {
          case _: TextDocumentDomainObject =>
            new PermSetEditor(restrictedTwoPermSet, doc, user) with TextDocPermSetEditor
          case _ =>
            new PermSetEditor(restrictedTwoPermSet, doc, user) with NonTextDocPermSetEditor
        }

        dlg.wrapOkHandler {
          restrictedTwoPermSet = editor.collectValues().right.get
        }

        dlg.mainUI = editor.ui
      }
    }

    ui.rolesPermSetsUI.miRoleAdd.setCommandHandler {
      val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
      val assignedRoles = ui.rolesPermSetsUI.tblRolesPermSets.itemIds.asScala.toSet
      val availableRolesWithPermsSetTypes: Map[RoleDomainObject, List[DocumentPermissionSetTypeDomainObject]] =
        (for {
          role <- roleMapper.getAllRoles
          roleId = role.getId
          if !(role.getId == RoleId.SUPERADMIN || assignedRoles.contains(role))
          setTypes = types.filter(user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(_, roleId, doc))
          if setTypes.nonEmpty
        } yield role -> setTypes)(breakOut)

      if (availableRolesWithPermsSetTypes.isEmpty) {
        ui.topWindow.showWarningNotification("No roles available")
      } else {
        ui.topWindow.initAndShow(new OkCancelDialog("Add role")) { dlg =>
          val availableRoles = availableRolesWithPermsSetTypes.keySet
          dlg.mainUI = new AddRolePermSetDialogMainUI |>> { c =>
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


    ui.rolesPermSetsUI.miRoleChangePermissionSet.setCommandHandler {
      whenSingle(ui.rolesPermSetsUI.tblRolesPermSets.value.asScala.toSeq) { role =>
        types.filter(setType => user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(setType, role.getId, doc)) match {
          case Nil => ui.topWindow.showWarningNotification("You are not allowed to edit this role")
          case availableSetTypes =>
            ui.topWindow.initAndShow(new OkCancelDialog("Change role permissions type")) { dlg =>
              dlg.mainUI = new ChangeRolePermSetDialogMainUI |>> { c =>
                c.lblRole.value = role.getName

                c.ogPermsSetType.value = ui.rolesPermSetsUI.tblRolesPermSets
                  .item(role)
                  .getItemProperty(RolePermSetPropertyId).getValue.asInstanceOf[RolePermSet].setType

                types foreach {setType => c.ogPermsSetType.setItemEnabled(setType, availableSetTypes contains setType)}

                dlg.wrapOkHandler {
                  setRolePermSetType(role, c.ogPermsSetType.value)
                }
              }
            }
        }
      }
    }

    ui.rolesPermSetsUI.miRoleRemove.setCommandHandler {
      whenSelected(ui.rolesPermSetsUI.tblRolesPermSets) { roles =>
        roles.asScala.foreach(ui.rolesPermSetsUI.tblRolesPermSets.removeItem)
      }
    }

    ui.rolesPermSetsUI.miPermissionSets.setCommandHandler {
      ui.topWindow.initAndShow(new OkCancelDialog("Permission Sets")) { dlg =>
        dlg.mainUI = new PermSetsEditor(doc, user).ui
      }
    }
  }

  def resetValues() {
    restrictedOnePermSet = meta.getPermissionSets.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject]
    restrictedTwoPermSet = meta.getPermissionSets.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject]

    val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
    val allButSuperadminRole = roleMapper.getAllRoles
      .filterNot(_.getId == RoleId.SUPERADMIN)
      .map(role => (role.getId, role))(breakOut) : Map[RoleId, RoleDomainObject]

    ui.rolesPermSetsUI.tblRolesPermSets.removeAllItems()

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
    ui.rolesPermSetsUI.tblRolesPermSets.addItem(Array[AnyRef](RolePermSet(role, setType)), role)
  }

  private def setRolePermSetType(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
    ui.rolesPermSetsUI.tblRolesPermSets.item(role)
      .getItemProperty(RolePermSetPropertyId)
      .setValue(RolePermSet(role, setType))
  }


  def collectValues(): ErrorsOrData = Right(
    Data(
      new RoleIdToDocumentPermissionSetTypeMappings |>> { rolesPermissions =>
        import ui.rolesPermSetsUI.tblRolesPermSets
        tblRolesPermSets.itemIds.asScala.foreach { role =>
          rolesPermissions.setPermissionSetTypeForRole(
            role.getId,
            tblRolesPermSets.getContainerProperty(role, RolePermSetPropertyId).getValue.asInstanceOf[RolePermSet].setType
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

  resetValues()
}



class AccessEditorUI extends VerticalLayout with Spacing with FullWidth {
  setMargin(true, true, false, true)

  class RolesPermSetsUI extends VerticalLayout with Spacing with FullWidth {
    val mb = new MenuBar
    val miRole = mb.addItem("Role")
    val miPermissionSets = mb.addItem("Permission Sets")
    val miRoleAdd = miRole.addItem("Add")
    val miRoleRemove = miRole.addItem("Remove")
    val miRoleChangePermissionSet = miRole.addItem("Change Permission Set")

    val tblRolesPermSets = new Table with MultiSelect[RoleDomainObject] with Immediate with FullWidth with Selectable |>> { tbl =>
      tbl.setPageLength(7)

      addContainerProperties(tbl,
        CP[RolePermSet](RolePermSetPropertyId))

      tbl.setColumnHeader(RolePermSetPropertyId, "Role")

      for (setType <- Seq(READ, RESTRICTED_1, RESTRICTED_2, FULL)) {
        tbl.addGeneratedColumn(setType, new RolesPermSetsTableColumnGenerator(setType))
        tbl.setColumnHeader(setType, setType.toString)
      }
    }

    addComponentsTo(this, mb, tblRolesPermSets)
  }

  val chkLim1IsMorePrivilegedThanLim2 = new CheckBox("Limited 1 is more privileged than limited 2")
  val rolesPermSetsUI = new RolesPermSetsUI

  val frmExtraSettings = new Form with FullWidth {
    setCaption("Extra settings")

    val chkShowToUnauthorizedUser = new CheckBox("Unauthorized user can see link(s) to this document")
    val chkShareWithOtherAdmins = new CheckBox("Share the document with other administrators")

    addComponentsTo(getLayout, chkShowToUnauthorizedUser, chkShareWithOtherAdmins)
  }

  val lytRestrictedPermSets = new GridLayout(2, 2) with Spacing with UndefinedSize with MiddleLeftAlignment {
    val btnEditRestrictedOnePermSet = new Button("permissions".i) with SmallStyle
    val btnEditRestrictedTwoPermSet = new Button("permissions".i) with SmallStyle

    addComponentsTo(this, new Label("Limited-1"), btnEditRestrictedOnePermSet, new Label("Limited-2"), btnEditRestrictedTwoPermSet)
  }

  addComponentsTo(this, rolesPermSetsUI, lytRestrictedPermSets, frmExtraSettings)
}


// todo ??????????????????????????????????
private object RolePermSetPropertyId


private case class RolePermSet(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
  override def toString = role.getName
}


/**
 * Role permission type table column generator for properties ids of type DocumentPermissionSetTypeDomainObject.
 * Vaddin (bug?) warning:
 *   For some reasons when adding items to table, values are not available (assigned) immediately, hence
 *   Property.getValue returns null.
 */
private class RolesPermSetsTableColumnGenerator(setType: DocumentPermissionSetTypeDomainObject) extends Table.ColumnGenerator {
  def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = new Label with UndefinedSize |>> {
    val rolePermSetType = source.getItem(itemId)
      .getItemProperty(RolePermSetPropertyId)
      .getValue.asInstanceOf[RolePermSet]

    _.value = if (rolePermSetType != null && rolePermSetType.setType == setType) "X" else ""
  }
}





private class AddRolePermSetDialogMainUI extends FormLayout with UndefinedSize {
  val cbRole = new ComboBox("Role") with SingleSelect[RoleDomainObject] with NoNullSelection with Immediate
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect[DocumentPermissionSetTypeDomainObject]

  List(READ, RESTRICTED_1, RESTRICTED_2, FULL) foreach { setType =>
    ogPermsSetType.addItem(setType, setType.toString.i)
  }

  addComponentsTo(this, cbRole, ogPermsSetType)
}

/**
 * Changes permission set type for a role.
 */
private class ChangeRolePermSetDialogMainUI extends FormLayout with UndefinedSize {
  val lblRole = new Label with UndefinedSize |>> {_ setCaption "Role"}
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect[DocumentPermissionSetTypeDomainObject]

  List(READ, RESTRICTED_1, RESTRICTED_2, FULL) foreach { setType =>
    ogPermsSetType.addItem(setType, setType.toString.i)
  }

  addComponentsTo(this, lblRole, ogPermsSetType)
}


/**
 * State of this dialog is represented by {@link TextDocumentPermissionSetDomainObject}
 */
abstract class PermSetEditor(
      protected val permSet: TextDocumentPermissionSetDomainObject,
      protected val doc: DocumentDomainObject,
      protected val user: UserDomainObject
    ) extends Editor with ImcmsServicesSupport {

  type Data = TextDocumentPermissionSetDomainObject
}


/**
 * Any but text-doc restricted permission set editor.
 */
trait NonTextDocPermSetEditor { this: PermSetEditor =>
  val ui = new NonTextDocPermSetEditorUI |>> { ui =>
    ui.chkEditMeta.checked = permSet.getEditDocumentInformation
    ui.chkEditPermissions.checked = permSet.getEditPermissions
    ui.chkEditContent.checked = permSet.getEdit
  }

  def resetValues() {}

  def collectValues(): ErrorsOrData = new TextDocumentPermissionSetDomainObject(permSet.getType) |>> { ps =>
    ps.setEditDocumentInformation(ui.chkEditMeta.checked)
    ps.setEditPermissions(ui.chkEditPermissions.checked)
    ps.setEdit(ui.chkEditContent.checked)
  } |> Right.apply
}


/**
 * Text doc restricted permission set editor.
 */
trait TextDocPermSetEditor { this: PermSetEditor =>
  val ui = new TextDocPermSetEditorUI |>> { ui =>
    // Authorized document types
    val selectedTypeIds = permSet.getAllowedDocumentTypeIds
    for ((typeId, typeName) <- imcmsServices.getDocumentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user).asScala) {
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

  def resetValues() {}

  def collectValues(): ErrorsOrData = new TextDocumentPermissionSetDomainObject(permSet.getType) |>> { ps =>
    ps.setEditDocumentInformation(ui.chkEditMeta.checked)
    ps.setEditPermissions(ui.chkEditPermissions.checked)
    ps.setEdit(ui.chkEditContent.checked)

    ps.setEditTemplates(ui.chkEditTemplates.checked)
    ps.setEditImages(ui.chkEditImages.checked)
    ps.setEditMenus(ui.chkEditMenus.checked)
    ps.setEditIncludes(ui.chkEditIncludes.checked)

    ps.setAllowedDocumentTypeIds(new java.util.HashSet(ui.tcsCreateDocsOfTypes.itemIds))
    ps.setAllowedTemplateGroupIds(ui.tcsUseTemplatesFromTemplateGroups.itemIds.asScala.map(Int box _.getId).toSet.asJava)
  } |> Right.apply
}


/**
 * Doc's common restricted permission set
 */
trait PermSetEditorUI extends VerticalLayout with UndefinedSize {

  val content = new VerticalLayout with Spacing with Margin with UndefinedSize

  // Decoration; always checked and read only
  val chkViewContent = new CheckBox("View content") with Checked with ReadOnly
  val chkEditContent = new CheckBox("Edit content")
  val chkEditMeta = new CheckBox("Edit properties")
  val chkEditPermissions = new CheckBox("Edit permissions")
}


class TextDocPermSetEditorUI extends PermSetEditorUI {

  val chkEditImages = new CheckBox("Edit images")
  val chkEditIncludes = new CheckBox("Edit includes")
  val chkEditMenus = new CheckBox("Edit menues")
  val chkEditTemplates = new CheckBox("Change templates")

  // item caption is a type name in a user language
  val tcsCreateDocsOfTypes = new TwinColSelect("Create documents of type") with MultiSelect[DocTypeId] { setRows(5) }
  val tcsUseTemplatesFromTemplateGroups = new TwinColSelect("Use templates from groups") with MultiSelect[TemplateGroupDomainObject] { setRows(5) }

  chkEditContent.setCaption("Edit texts")

  addComponent(content)
  setComponentAlignment(content, Alignment.MIDDLE_CENTER)
  addComponentsTo(content, chkViewContent, chkEditMeta, chkEditPermissions, chkEditContent, chkEditIncludes, chkEditMenus, chkEditTemplates, tcsCreateDocsOfTypes, tcsUseTemplatesFromTemplateGroups)
}


/**
 * Non text doc limited permissions.
 */
class NonTextDocPermSetEditorUI extends PermSetEditorUI {

  addComponentsTo(this, chkViewContent, chkEditContent, chkEditMeta, chkEditPermissions)
}


class PermSetsEditor(doc: DocumentDomainObject, user: UserDomainObject) extends Editor with ImcmsServicesSupport {
  case class Data()

  private object sets {
    val full = DocumentPermissionSetDomainObject.FULL.asInstanceOf[TextDocumentPermissionSetDomainObject]
    val read = DocumentPermissionSetDomainObject.READ.asInstanceOf[TextDocumentPermissionSetDomainObject]
    val lim1 = doc.getMeta.getPermissionSets.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject]
    val lim2 = doc.getMeta.getPermissionSets.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject]
  }

  private object editors {
    val full = doc match {
      case _: TextDocumentDomainObject =>
        new PermSetEditor(sets.full, doc, user) with TextDocPermSetEditor
      case _ =>
        new PermSetEditor(sets.full, doc, user) with NonTextDocPermSetEditor
    }

    val lim2 = doc match {
      case _: TextDocumentDomainObject =>
        new PermSetEditor(sets.lim1, doc, user) with TextDocPermSetEditor
      case _ =>
        new PermSetEditor(sets.lim2, doc, user) with NonTextDocPermSetEditor
    }

    val lim1 = doc match {
      case _: TextDocumentDomainObject =>
        new PermSetEditor(sets.lim1, doc, user) with TextDocPermSetEditor
      case _ =>
        new PermSetEditor(sets.lim2, doc, user) with NonTextDocPermSetEditor
    }


    val read = doc match {
      case _: TextDocumentDomainObject =>
        new PermSetEditor(sets.read, doc, user) with TextDocPermSetEditor
      case _ =>
        new PermSetEditor(sets.read, doc, user) with NonTextDocPermSetEditor
    }
  }

  val ui = new PermSetsEditorUI |>> { ui =>
    ui.tsSets.addTab(editors.read.ui, "View [predefined])")
    ui.tsSets.addTab(editors.lim1.ui, "Custom-One")
    ui.tsSets.addTab(editors.lim2.ui, "Custom-Two")
    ui.tsSets.addTab(editors.full.ui, "All [predefined]")

    editors.read.ui.setEnabled(false)
    editors.full.ui.setEnabled(false)
  }

  resetValues()

  def resetValues() {
    doto(editors.full, editors.lim2, editors.lim1, editors.read) { _.resetValues() }
    ui.tsSets.setSelectedTab(editors.lim1.ui)
  }

  def collectValues(): ErrorsOrData = Right(Data())
}


class PermSetsEditorUI extends VerticalLayout with UndefinedSize with Spacing {
  val tsSets = new TabSheet with UndefinedSize
  val chkLim1IsMorePrivilegedThanLim2 = new CheckBox("Custom-One is more privileged that Custom-Two")

  addComponentsTo(this, tsSets, chkLim1IsMorePrivilegedThanLim2)
}