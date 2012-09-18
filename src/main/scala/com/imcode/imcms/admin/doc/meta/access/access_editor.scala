package com.imcode
package imcms
package admin.doc.meta.access

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
  // if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document ))
  // This can be defined perd doc, so - change must be single select!!
  // TODO: Check!! ??? Mapped roles might contain: if ( DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) || RoleId.SUPERADMIN.equals(roleId)
  // TODO: !!!
    // security
    // ui.lytRestrictedPermSets.btnEditRestrictedOnePermSet setReadOnly !user.canDefineRestrictedOneFor(doc)
    // ui.lytRestrictedPermSets.btnEditRestrictedTwoPermSet setReadOnly !user.canDefineRestrictedTwoFor(doc)
    // ui.chkLim1IsMorePrivilegedThanLim2 setReadOnly !user.isSuperAdminOrHasFullPermissionOn(doc)
  val ui = new AccessEditorUI |>> { ui =>
    ui.perms.miRoleAdd.setCommandHandler {
      val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
      val assignedRoles = ui.perms.tblRolesPermSets.itemIds.asScala.toSet
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
              types.foreach { typeSet =>
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


    ui.perms.miRoleChangePermSet.setCommandHandler {
      whenSingle(ui.perms.tblRolesPermSets.value.asScala.toSeq) { role =>
        types.filter(setType => user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(setType, role.getId, doc)) match {
          case Nil => ui.topWindow.showWarningNotification("You are not allowed to edit this role")
          case availableSetTypes =>
            ui.topWindow.initAndShow(new OkCancelDialog("Change Role Permissions")) { dlg =>
              dlg.mainUI = new ChangeRolePermSetDialogMainUI |>> { c =>
                c.lblRole.value = role.getName

                c.ogPermsSetType.value = ui.perms.tblRolesPermSets
                  .item(role)
                  .getItemProperty(RolePermSetPropertyId).getValue.asInstanceOf[RolePermSet].setType

                types.foreach { setType =>
                  c.ogPermsSetType.setItemEnabled(setType, availableSetTypes contains setType)
                }

                dlg.wrapOkHandler {
                  setRolePermSetType(role, c.ogPermsSetType.value)
                }
              }
            }
        }
      }
    }

    ui.perms.miRoleRemove.setCommandHandler {
      whenSelected(ui.perms.tblRolesPermSets) { roles =>
        roles.asScala.foreach(ui.perms.tblRolesPermSets.removeItem)
      }
    }

    ui.perms.miEditPermSets.setCommandHandler {
      ui.topWindow.initAndShow(new OkCancelDialog("Permissions")) { dlg =>
        dlg.mainUI = permSetsEditor.ui
      }
    }
  }

  resetValues()


  def resetValues() {
    val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
    val allButSuperadminRole = roleMapper.getAllRoles
      .filterNot(_.getId == RoleId.SUPERADMIN)
      .map(role => (role.getId, role))(breakOut) : Map[RoleId, RoleDomainObject]

    ui.perms.tblRolesPermSets.removeAllItems()

    for {
      mapping <- initialValues.rolesPermissions.getMappings
      roleId = mapping.getRoleId
      role <- allButSuperadminRole.get(roleId)
      setType = mapping.getDocumentPermissionSetType
      if setType != NONE
    } {
      addRolePermSetType(role, setType)
    }

    ui.misc.chkShareWithOtherAdmins.checked = initialValues.isLinkableByOtherUsers
    ui.misc.chkShowToUnauthorizedUser.checked = initialValues.isLinkedForUnauthorizedUsers

    permSetsEditor.resetValues()

    doc match {
      case textDoc: TextDocumentDomainObject =>
      case _ =>
    }
  }


  private def addRolePermSetType(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
    ui.perms.tblRolesPermSets.addItem(Array[AnyRef](RolePermSet(role, setType)), role)
  }

  private def setRolePermSetType(role: RoleDomainObject, setType: DocumentPermissionSetTypeDomainObject) {
    ui.perms.tblRolesPermSets.item(role)
      .getItemProperty(RolePermSetPropertyId)
      .setValue(RolePermSet(role, setType))
  }


  def collectValues(): ErrorsOrData = {
    val permSetsValues = permSetsEditor.collectValues().right.get

    Right(
      Data(
        new RoleIdToDocumentPermissionSetTypeMappings |>> { rolesPermissions =>
          import ui.perms.tblRolesPermSets
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
        ui.misc.chkShowToUnauthorizedUser.checked,
        ui.misc.chkShareWithOtherAdmins.checked
      )
    )
  }
}


class AccessEditorUI extends VerticalLayout with Spacing with FullWidth {
  object perms {
    val mb = new MenuBar
    val miRole = mb.addItem("Role")
    val miEditPermSets = mb.addItem("Permissions")
    val miRoleAdd = miRole.addItem("Add")
    val miRoleRemove = miRole.addItem("Remove")
    val miRoleChangePermSet = miRole.addItem("Change Permissions")

    val tblRolesPermSets = new Table with MultiSelect[RoleDomainObject] with Immediate with FullWidth with Selectable |>> { tbl =>
      tbl.setPageLength(7)

      addContainerProperties(tbl,
        CP[RolePermSet](RolePermSetPropertyId))

      tbl.setColumnHeader(RolePermSetPropertyId, "Role")

      for (setType <- Seq(READ, RESTRICTED_1, RESTRICTED_2, FULL)) {
        tbl.addGeneratedColumn(setType, new RolesPermSetsTableColumnGenerator(setType))
        tbl.setColumnHeader(setType, PermSetTypeName(setType))
      }
    }
  }

  object misc {
    val chkShowToUnauthorizedUser = new CheckBox("Unauthorized user can see link(s) to this document")
    val chkShareWithOtherAdmins = new CheckBox("Share the document with other administrators")
  }

  private val pnlRights = new Panel("Rights") with FullWidth {
    val content = new VerticalLayout with Spacing with Margin with FullWidth

    addComponentsTo(content, perms.mb, perms.tblRolesPermSets)

    setContent(content)
  }

  private val pnlMisc = new Panel("Misc") with FullWidth {
    val content = new VerticalLayout with Spacing with Margin

    addComponentsTo(content, misc.chkShowToUnauthorizedUser, misc.chkShareWithOtherAdmins)
    setContent(content)
  }

  addComponentsTo(this, pnlRights, pnlMisc)
}


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


private class AddRolePermSetDialogMainUI extends FormLayout with UndefinedSize {
  val cbRole = new ComboBox("Role") with SingleSelect[RoleDomainObject] with NoNullSelection with Immediate
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect[DocumentPermissionSetTypeDomainObject]

  doto(READ, RESTRICTED_1, RESTRICTED_2, FULL) { setType =>
    ogPermsSetType.addItem(setType, PermSetTypeName(setType))
  }

  addComponentsTo(this, cbRole, ogPermsSetType)
}

/**
 * Changes permission set type for a role.
 */
private class ChangeRolePermSetDialogMainUI extends FormLayout with UndefinedSize {
  val lblRole = new Label with UndefinedSize |>> {_ setCaption "Role"}
  val ogPermsSetType = new OptionGroup("Permissions") with SingleSelect[DocumentPermissionSetTypeDomainObject]

  doto(READ, RESTRICTED_1, RESTRICTED_2, FULL) { setType =>
    ogPermsSetType.addItem(setType, PermSetTypeName(setType))
  }

  addComponentsTo(this, lblRole, ogPermsSetType)
}


trait DocPermSetEditor extends Editor {
  type Data = TextDocumentPermissionSetDomainObject
}


class NonTextDocPermSetEditor(permSet: TextDocumentPermissionSetDomainObject) extends DocPermSetEditor {

  val ui = new NonTextDocPermSetEditorUI

  resetValues()

  def resetValues() {
    ui.chkEditMeta.checked = permSet.getEditDocumentInformation
    ui.chkEditPermissions.checked = permSet.getEditPermissions
    ui.chkEditContent.checked = permSet.getEdit
  }

  def collectValues(): ErrorsOrData = new TextDocumentPermissionSetDomainObject(permSet.getType) |>> { ps =>
    ps.setEditDocumentInformation(ui.chkEditMeta.checked)
    ps.setEditPermissions(ui.chkEditPermissions.checked)
    ps.setEdit(ui.chkEditContent.checked)
  } |> Right.apply
}


class TextDocPermSetEditor(
    permSet: TextDocumentPermissionSetDomainObject,
    doc: DocumentDomainObject,
    user: UserDomainObject) extends DocPermSetEditor with ImcmsServicesSupport {

  val ui = new TextDocPermSetEditorUI

  resetValues()

  def resetValues() {
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
    ui.chkEditTexts.checked = permSet.getEdit

    ui.chkEditTemplates.checked = permSet.getEditTemplates
    ui.chkEditImages.checked = permSet.getEditImages
    ui.chkEditMenus.checked = permSet.getEditMenus
    ui.chkEditIncludes.checked = permSet.getEditIncludes
  }

  def collectValues(): ErrorsOrData = new TextDocumentPermissionSetDomainObject(permSet.getType) |>> { ps =>
    ps.setEditDocumentInformation(ui.chkEditMeta.checked)
    ps.setEditPermissions(ui.chkEditPermissions.checked)
    ps.setEdit(ui.chkEditTexts.checked)

    ps.setEditTemplates(ui.chkEditTemplates.checked)
    ps.setEditImages(ui.chkEditImages.checked)
    ps.setEditMenus(ui.chkEditMenus.checked)
    ps.setEditIncludes(ui.chkEditIncludes.checked)

    ps.setAllowedDocumentTypeIds(new java.util.HashSet(ui.tcsCreateDocsOfTypes.itemIds))
    ps.setAllowedTemplateGroupIds(ui.tcsUseTemplatesFromTemplateGroups.itemIds.asScala.map(Int box _.getId).toSet.asJava)
  } |> Right.apply
}


/**
 * Doc's restricted permission set
 */
class NonTextDocPermSetEditorUI extends VerticalLayout with UndefinedSize {

  private val content = new VerticalLayout with Spacing with Margin with UndefinedSize

  // Decoration; always checked and read only
  val chkViewContent = new CheckBox("View content") with Checked with ReadOnly
  val chkEditContent = new CheckBox("Edit content")
  val chkEditMeta = new CheckBox("Edit properties")
  val chkEditPermissions = new CheckBox("Edit permissions")

  addComponent(content)
  setComponentAlignment(content, Alignment.MIDDLE_CENTER)
  addComponentsTo(content, chkViewContent, chkEditContent, chkEditMeta, chkEditPermissions)
}


class TextDocPermSetEditorUI extends VerticalLayout with UndefinedSize {

  private val content = new VerticalLayout with Spacing with Margin with UndefinedSize

  // Decoration; always checked and read only
  val chkViewContent = new CheckBox("View content") with Checked with ReadOnly
  val chkEditMeta = new CheckBox("Edit properties")
  val chkEditPermissions = new CheckBox("Edit permissions")

  val chkEditTexts = new CheckBox("Edit texts")
  val chkEditImages = new CheckBox("Edit images")
  val chkEditIncludes = new CheckBox("Edit includes")
  val chkEditMenus = new CheckBox("Edit menues")
  val chkEditTemplates = new CheckBox("Change templates")

  // item caption is a type name in a user language
  val tcsCreateDocsOfTypes = new TwinColSelect("Create documents of type") with MultiSelect[DocTypeId] { setRows(5) }
  val tcsUseTemplatesFromTemplateGroups = new TwinColSelect("Use templates from groups") with MultiSelect[TemplateGroupDomainObject] { setRows(5) }

  addComponent(content)
  setComponentAlignment(content, Alignment.MIDDLE_CENTER)
  addComponentsTo(content, chkViewContent, chkEditMeta, chkEditPermissions, chkEditTexts, chkEditIncludes, chkEditMenus, chkEditTemplates, tcsCreateDocsOfTypes, tcsUseTemplatesFromTemplateGroups)
}


class DocPermSetsEditor(doc: DocumentDomainObject, user: UserDomainObject) extends Editor with ImcmsServicesSupport {
  case class Data(
    restrictedOnePermSet: TextDocumentPermissionSetDomainObject = doc.getMeta.getPermissionSets.getRestricted1.asInstanceOf[TextDocumentPermissionSetDomainObject],
    restrictedTwoPermSet: TextDocumentPermissionSetDomainObject = doc.getMeta.getPermissionSets.getRestricted2.asInstanceOf[TextDocumentPermissionSetDomainObject],
    isRestrictedOneMorePrivilegedThanRestrictedTwo: Boolean = doc.getMeta.getRestrictedOneMorePrivilegedThanRestrictedTwo
  )

  private val initialValues = new Data()

  private object editors {
    val fullSet = DocumentPermissionSetDomainObject.FULL.asInstanceOf[TextDocumentPermissionSetDomainObject]
    val readSet = DocumentPermissionSetDomainObject.READ.asInstanceOf[TextDocumentPermissionSetDomainObject]

    val (full, read, restrictedOne, restrictedTwo) = doc match {
      case _: TextDocumentDomainObject => (
        new TextDocPermSetEditor(fullSet, doc, user),
        new TextDocPermSetEditor(readSet, doc, user),
        new TextDocPermSetEditor(initialValues.restrictedOnePermSet, doc, user),
        new TextDocPermSetEditor(initialValues.restrictedTwoPermSet, doc, user)
      )

      case _ => (
        new NonTextDocPermSetEditor(fullSet),
        new NonTextDocPermSetEditor(readSet),
        new NonTextDocPermSetEditor(initialValues.restrictedOnePermSet),
        new NonTextDocPermSetEditor(initialValues.restrictedTwoPermSet)
      )
    }
  }

  val ui = new DocPermSetsEditorUI |>> { ui =>
    ui.tsSets.addTab(editors.read.ui, PermSetTypeName(READ))
    ui.tsSets.addTab(editors.restrictedOne.ui, PermSetTypeName(RESTRICTED_1))
    ui.tsSets.addTab(editors.restrictedTwo.ui, PermSetTypeName(RESTRICTED_2))
    ui.tsSets.addTab(editors.full.ui, PermSetTypeName(FULL))

    editors.read.ui.setEnabled(false)
    editors.full.ui.setEnabled(false)
  }

  resetValues()

  def resetValues() {
    doto(editors.full, editors.restrictedOne, editors.restrictedTwo, editors.read) { _.resetValues() }
    ui.tsSets.setSelectedTab(editors.restrictedOne.ui)
    ui.chkRestrictedOneIsMorePrivilegedThanRestrictedTwo.checked = doc.getMeta.getRestrictedOneMorePrivilegedThanRestrictedTwo
  }

  def collectValues(): ErrorsOrData = Data(
    restrictedOnePermSet = editors.restrictedOne.collectValues().right.get,
    restrictedTwoPermSet = editors.restrictedTwo.collectValues().right.get,
    isRestrictedOneMorePrivilegedThanRestrictedTwo = ui.chkRestrictedOneIsMorePrivilegedThanRestrictedTwo.checked
  ) |> Right.apply
}


class DocPermSetsEditorUI extends VerticalLayout with UndefinedSize with Spacing {
  val tsSets = new TabSheet with UndefinedSize
  val chkRestrictedOneIsMorePrivilegedThanRestrictedTwo = new CheckBox("Custom-One is more privileged that Custom-Two")

  addComponentsTo(this, tsSets, chkRestrictedOneIsMorePrivilegedThanRestrictedTwo)
}


private object PermSetTypeName extends (DocumentPermissionSetTypeDomainObject => String) {
  private val permSetTypesNames = Map(FULL -> "All [sealed]", RESTRICTED_1 -> "Custom-One", RESTRICTED_2 -> "Custom-Two", READ -> "View [sealed]")

  def apply(permSetType: DocumentPermissionSetTypeDomainObject) = permSetTypesNames(permSetType)
}