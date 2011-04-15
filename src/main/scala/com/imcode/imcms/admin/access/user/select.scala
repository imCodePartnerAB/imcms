package com.imcode
package imcms
package admin.access.user

import scala.collection.JavaConversions._

import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin._

import java.util.concurrent.atomic.AtomicReference
import com.imcode.util.event.Publisher
import scala.PartialFunction.{condOpt}
import java.util.Collections

// todo: db access, filtering should be optimized, + lazy loading/limit/pagination
class UserSelect extends Publisher[Option[UserDomainObject]] {
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val selectionRef = new AtomicReference[Option[UserDomainObject]](None)

  val ui: UserSelectUI = letret(new UserSelectUI) { ui =>
    roleMapper.getAllRolesExceptUsersRole foreach { role =>
      ui.filterUI.lstRoles.addItem(role.getId)
      ui.filterUI.lstRoles.setItemCaption(role.getId, role.getName)
    }

    ui.filterUI.btnApply addClickHandler { reload() }
    ui.filterUI.btnReset addClickHandler {
      ui.filterUI.txtText.value = ""
      ui.filterUI.chkShowInactive.value = false
      ui.filterUI.lstRoles.value foreach ui.filterUI.lstRoles.unselect
      reload()
    }
    ui.tblUsers addValueChangeHandler {
      selectionRef.set(Option(ui.tblUsers.value) map { roleMapper getUser _.intValue })
      notifyListeners()
    }
  }

  reload()

  def selection = selectionRef.get

  def reload() {
    val matchesAlways = Function.const(true)_

    val matchesText: UserDomainObject => Boolean = ui.filterUI.txtText.value.trim match {
      case text if text.isEmpty => matchesAlways
      case text => { _.getLoginName.contains(text) }
    }

    val matchesRole: UserDomainObject => Boolean = ui.filterUI.lstRoles.value match {
      case rolesIds if rolesIds.isEmpty => matchesAlways
      case rolesIds => { _.getRoleIds.intersect(rolesIds.toSeq).nonEmpty }
    }

    val matchesActive: UserDomainObject => Boolean = ui.filterUI.chkShowInactive.booleanValue match {
      case true => matchesAlways
      case false => { _.isActive }
    }

    val matchesAll = (user: UserDomainObject) => List(matchesText, matchesRole, matchesActive) forall (_ apply user)

    ui.tblUsers.removeAllItems
    for {
      user <- roleMapper.getAllUsers.toList if matchesAll(user)
      userId = Int box user.getId
    } ui.tblUsers.addItem(Array[AnyRef](userId,
                            user.getLoginName,
                            user.getFirstName,
                            user.getLastName,
                            Boolean box user.isSuperAdmin,
                            Boolean box user.isActive),
                          userId)
  }

  override def notifyListeners() = notifyListeners(selection)
}


/**
 * Adds user select ui as a Ok-Cancel dialog content.
 * Adds listener - dialog's Ok button is enabled/disabled if a user is selected/unselected.
 */
trait UserSelectDialog { this: OkCancelDialog =>
  val userSelect = new UserSelect

  mainUI = userSelect.ui

  userSelect.listen { btnOk setEnabled _.isDefined }
  userSelect.notifyListeners()
}


class UserSelectUI extends VerticalLayout with UndefinedSize {
  val filterUI = new UserSelectFilterUI
  val tblUsers = new Table with ValueType[JInteger] with ItemIdType[JInteger] with Immediate with Selectable {
    addContainerProperties(this,
      ContainerProperty[JInteger]("Id"),
      ContainerProperty[String]("Login"),
      ContainerProperty[String]("First name"),
      ContainerProperty[String]("Last name"),
      ContainerProperty[JBoolean]("Superadmin?"),
      ContainerProperty[JBoolean]("Active?"))

    setPageLength(5)
  }

  addComponents(this, filterUI, tblUsers)
}


/**
 *
 */
class UserSelectFilterUI extends VerticalLayout with UndefinedSize { //CustomLayout
  val lytParams = new FormLayout with UndefinedSize

  val txtText = new TextField("Ex: username, email, first name, last name...") {
    setColumns(20)
  }
  val lytText = new VerticalLayout {
    setCaption("Free text")
    addComponent(txtText)
  }
  val btnApply = new Button("Search") with LinkStyle
  val btnReset = new Button("Reset") with LinkStyle
  val chkShowInactive = new CheckBox("Show inactive")
  val lstRoles = new ListSelect("Only with role(s)") with ValueType[JCollection[RoleId]] with ItemIdType[RoleId] with MultiSelect {
    setColumns(21)
    setRows(5)
  }

  val lytControls = new HorizontalLayout with UndefinedSize {
    setSpacing(true)
    addComponents(this, chkShowInactive, btnReset, btnApply)
  }

  addComponents(lytParams, lytText, lstRoles, lytControls)
  addComponents(this, lytParams)
  setSpacing(true)
}








trait UserSearchDialog { this: OkCancelDialog =>
  val search = new UserSearch

  mainUI = search.ui

  search.listen { btnOk setEnabled _.nonEmpty }
  search.notifyListeners()
}



class UserSearch extends Publisher[Seq[UserDomainObject]] {
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val selectionRef = new AtomicReference(Seq.empty[UserDomainObject])

  val form = new UserSearchForm
  val resultUI = new Table with MultiSelectBehavior[UserId] with Immediate with Selectable {
    addContainerProperties(this,
      ContainerProperty[UserId]("admin.access.user.search.tbl.col.id".i),
      ContainerProperty[String]("admin.access.user.search.tbl.col.login".i),
      ContainerProperty[String]("admin.access.user.search.tbl.col.first_name".i),
      ContainerProperty[String]("admin.access.user.search.tbl.col.last_name".i),
      ContainerProperty[JBoolean]("admin.access.user.search.tbl.col.superadmin?".i),
      ContainerProperty[JBoolean]("admin.access.user.search.tbl.col.active?".i))
  }

  val ui = letret(new GridLayout(1, 2) with Spacing) { ui =>
    addComponents(ui, form.ui, resultUI)
  } // ui

  resultUI.addValueChangeHandler {
    selectionRef.set(resultUI.value map { roleMapper getUser _.intValue })
    notifyListeners()
  }

  form.ui.lytButtons.btnSearch.addClickHandler {
    search()
  }

  form.ui.lytButtons.btnClear.addClickHandler {
    reload()
  }

  reload()


  def reload() {
    form.ui.tcsRoles.removeAllItems

    roleMapper.getAllRolesExceptUsersRole foreach { role =>
      form.ui.tcsRoles.addItem(role.getId)
      form.ui.tcsRoles.setItemCaption(role.getId, role.getName)
    }

    form.reset()
    search()
  }


  def search() {
    val matchesAlways = Function.const(true)_

    val matchesText: UserDomainObject => Boolean = form.ui.txtText.value.trim match {
      case text if text.isEmpty => matchesAlways
      case text => { _.getLoginName.contains(text) }
    }

    val matchesRole: UserDomainObject => Boolean = form.ui.tcsRoles.value match {
      case rolesIds if rolesIds.isEmpty => matchesAlways
      case rolesIds => { _.getRoleIds.intersect(rolesIds.toSeq).nonEmpty }
    }

    val matchesActive: UserDomainObject => Boolean = form.ui.chkIncludeInactive.booleanValue match {
      case true => matchesAlways
      case false => { _.isActive }
    }

    val matchesAll = (user: UserDomainObject) => List(matchesText, matchesRole, matchesActive) forall (_ apply user)

    resultUI.removeAllItems
    for {
      user <- roleMapper.getAllUsers.toList if matchesAll(user)
      userId = Int box user.getId
    } resultUI.addItem(Array[AnyRef](
                               userId,
                               user.getLoginName,
                               user.getFirstName,
                               user.getLastName,
                               Boolean box user.isSuperAdmin,
                               Boolean box user.isActive),
                             userId)
  }

  def selection = selectionRef.get

  override def notifyListeners() = notifyListeners(selection)
}


case class UserSearchFormState(
  text: Option[String] = None,
  roleIds: Option[Seq[RoleId]] = None
)

class UserSearchForm {
  private var state = UserSearchFormState()

  val ui = new UserSearchFormUI

  ui.chkText.addClickHandler { state = alterText(state) }
  ui.chkRole.addClickHandler { state = alterRoles(state) }

  def reset() {
    ui.chkText.check
    ui.chkRole.uncheck
    ui.chkIncludeInactive.uncheck

    setState(UserSearchFormState())
  }

  def setState(newState: UserSearchFormState) {
    alterText(newState)
    alterRoles(newState)

    state = newState
  }

  def alterText(currentState: UserSearchFormState) = {
    if (ui.chkText.isChecked) {
      ui.txtText.setEnabled(true)
      ui.txtText.value = currentState.text.getOrElse("")
      currentState
    } else {
      val newState = currentState.copy(
        text = condOpt(ui.txtText.value.trim) { case text if text.nonEmpty => text }
      )

      ui.txtText.setEnabled(true)
      ui.txtText.value = ""
      ui.txtText.setEnabled(false)
      newState
    }
  }

  def alterRoles(currentState: UserSearchFormState) = {
    if (ui.chkRole.isChecked) {
      ui.tcsRoles.setEnabled(true)
      ui.tcsRoles.value = Seq.empty[RoleId]
      for (roleIds <- currentState.roleIds) ui.tcsRoles.value = roleIds
      currentState
    } else {
      val newState = currentState.copy(roleIds = condOpt(ui.tcsRoles.value) { case ids if ids.nonEmpty => ids })

      ui.tcsRoles.setEnabled(true)
      ui.tcsRoles.value = ui.tcsRoles.itemIds.toSeq
      ui.tcsRoles.setEnabled(false)
      newState
    }
  }
}


class UserSearchFormUI extends CustomLayout("admin/access/user/search/form") with UndefinedSize {
  val chkText = new CheckBox("admin.access.user.search.frm.chk.text".i) with Immediate
  val txtText = new TextField("admin.access.user.search.frm.text.caption".i) {
    setInputPrompt("admin.access.user.search.frm.text.prompt".i)
  }

  val chkRole = new CheckBox("admin.access.user.search.frm.chk.roles".i) with Immediate
  val tcsRoles = new TwinColSelect with MultiSelectBehavior[RoleId]
  //val chkOther = new Label("Misc") with UndefinedSize //new CheckBox("admin.access.user.search.frm.chk.other".i) with Immediate
  val chkIncludeInactive = new CheckBox("admin.access.user.search.frm.other.chk.include_inactive".i)
  val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
    val btnClear = new Button("admin.access.user.search.frm.buttons.btn.clear".i) { setStyleName("small") }
    val btnSearch = new Button("admin.access.user.search.frm.buttons.btn.search".i) { setStyleName("small") }

    addComponents(this, btnClear, btnSearch)
  }

  tcsRoles.setLeftColumnCaption("admin.access.user.search.frm.roles.tcs.lbl.available".i)
  tcsRoles.setRightColumnCaption("admin.access.user.search.frm.roles.tcs.lbl.selected".i)

  addNamedComponents(this,
    "admin.access.user.search.frm.chk.text" -> chkText,
    "admin.access.user.search.frm.text" -> txtText,
    "admin.access.user.search.frm.chk.roles" -> chkRole,
    "admin.access.user.search.frm.roles" -> tcsRoles,
    //"admin.access.user.search.frm.chk.other" -> chkOther,
    "admin.access.user.search.frm.misc" -> chkIncludeInactive,
    "admin.access.user.search.frm.buttons" -> lytButtons
  )
}