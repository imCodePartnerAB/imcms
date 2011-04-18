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

// "Ex: username, email, first name, last name..."
//


trait UserSearchDialog { this: OkCancelDialog =>
  val search = new UserSearch

  mainUI = search.ui

  search.listen { btnOk setEnabled _.nonEmpty }
  search.notifyListeners()
}


class UserSearch(multiSelect: Boolean = false) extends Publisher[Seq[UserDomainObject]] {
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val selectionRef = new AtomicReference[Seq[UserDomainObject]](Seq.empty)

  private val form = new UserSearchForm
  private val resultUI = new Table with MultiSelectBehavior[UserId] with Immediate with Selectable {
    addContainerProperties(this,
      ContainerProperty[UserId]("admin.access.user.search.tbl.col.id".i),
      ContainerProperty[String]("admin.access.user.search.tbl.col.login".i),
      ContainerProperty[String]("admin.access.user.search.tbl.col.first_name".i),
      ContainerProperty[String]("admin.access.user.search.tbl.col.last_name".i),
      ContainerProperty[JBoolean]("admin.access.user.search.tbl.col.superadmin?".i),
      ContainerProperty[JBoolean]("admin.access.user.search.tbl.col.active?".i))

    setMultiSelect(multiSelect)
  }

  val ui = letret(new GridLayout(1, 2) with Spacing) { ui =>
    addComponents(ui, form.ui, resultUI)
  } // ui

  resultUI.addValueChangeHandler {
    selectionRef.set(resultUI.value map { roleMapper getUser _.intValue })
    notifyListeners()
  }

  form.ui.lytButtons.btnSearch.addClickHandler { search() }
  form.ui.lytButtons.btnReset.addClickHandler { reload() }

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

    val matchesRole: UserDomainObject => Boolean = _.getRoleIds.intersect(form.ui.tcsRoles.value).nonEmpty

    val matchesActive: UserDomainObject => Boolean = form.ui.chkInactive.booleanValue match {
      case true => matchesAlways
      case false => { _.isActive }
    }

    val matchesAll = (user: UserDomainObject) => List(matchesText, matchesRole, matchesActive) forall (_ apply user)

    resultUI.removeAllItems
    for {
      user <- roleMapper.getAllUsers.toList if !user.isDefaultUser && matchesAll(user)
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

  val ui: UserSearchFormUI = letret(new UserSearchFormUI) { ui =>
    ui.chkText.addClickHandler { state = alterText(state) }
    ui.chkRole.addClickHandler { state = alterRoles(state) }
  }

  def reset() {
    ui.chkText.check
    ui.chkRole.uncheck
    ui.chkInactive.uncheck

    setState(UserSearchFormState())
  }

  def setState(newState: UserSearchFormState) {
    alterText(newState)
    alterRoles(newState)

    state = newState
  }

  private def alterText(currentState: UserSearchFormState) = {
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

  private def alterRoles(currentState: UserSearchFormState) = {
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
  val chkInactive = new CheckBox("admin.access.user.search.frm.other.chk.inactive".i)
  val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
    val btnReset = new Button("admin.access.user.search.frm.buttons.btn.reset".i) { setStyleName("small") }
    val btnSearch = new Button("admin.access.user.search.frm.buttons.btn.search".i) { setStyleName("small") }

    addComponents(this, btnReset, btnSearch)
  }

  tcsRoles.setLeftColumnCaption("admin.access.user.search.frm.roles.tcs.lbl.available".i)
  tcsRoles.setRightColumnCaption("admin.access.user.search.frm.roles.tcs.lbl.selected".i)

  addNamedComponents(this,
    "admin.access.user.search.frm.chk.text" -> chkText,
    "admin.access.user.search.frm.text" -> txtText,
    "admin.access.user.search.frm.chk.roles" -> chkRole,
    "admin.access.user.search.frm.roles" -> tcsRoles,
    "admin.access.user.search.frm.misc" -> chkInactive,
    "admin.access.user.search.frm.buttons" -> lytButtons
  )
}