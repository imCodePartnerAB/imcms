package com.imcode
package imcms
package admin.access.user

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

import imcode.server.user._
import java.util.concurrent.atomic.AtomicReference
import com.imcode.util.event.Publisher
import com.vaadin.ui._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}


trait UserSingleSelectDialog { this: OkCancelDialog =>
  val select = new UserSelect(multiSelect = false)

  mainUI = select.ui

  select.listen { btnOk setEnabled _.nonEmpty }
  select.notifyListeners()
}


trait UserSelectDialog { this: OkCancelDialog =>
  val select = new UserSelect

  mainUI = select.ui

  select.listen { btnOk setEnabled _.nonEmpty }
  select.notifyListeners()
}


class UserSelect(multiSelect: Boolean = true) extends Publisher[Seq[UserDomainObject]] with ImcmsServicesSupport {
  private val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val selectionRef = new AtomicReference[Seq[UserDomainObject]](Seq.empty)

  private val searchForm = new UserSearchForm
  private val usersUI = new Table with MultiSelectBehavior[UserId] with Immediate with Selectable {
    addContainerProperties(this,
      CP[UserId]("user.tbl.col.id"),
      CP[String]("user.tbl.col.login"),
      CP[String]("user.tbl.col.first_name"),
      CP[String]("user.tbl.col.last_name"),
      CP[JBoolean]("user.tbl.col.is_superadmin"),
      CP[JBoolean]("user.tbl.col.is_active"))

    setMultiSelect(multiSelect)

    setColumnHeaders(getContainerPropertyIds.map(_.toString.i).toArray)
  }

  val ui = letret(new GridLayout(1, 2)) { ui =>
    addComponents(ui, searchForm.ui, usersUI)
  }

  usersUI.addValueChangeHandler {
    selectionRef.set(usersUI.value map { roleMapper getUser _.intValue })
    notifyListeners()
  }

  searchForm.ui.lytButtons.btnSearch.addClickHandler { search() }
  searchForm.ui.lytButtons.btnReset.addClickHandler { reset() }
  // todo: move to parent?
  reset()

  def reset() {
    searchForm.reset()
    search()
  }


  def search() {
    val matchesAlways = Function.const(true)_
    val state = searchForm.getState()

    val matchesText: UserDomainObject => Boolean =
      state.text match {
        case Some(text) if text.nonEmpty => { _.getLoginName.contains(text) }
        case _ => matchesAlways
      }

    val matchesRoles: UserDomainObject => Boolean =
      state.roles match {
        case Some(roles) if roles.nonEmpty => { _.getRoleIds.intersect(searchForm.ui.tcsRoles.value.toSeq).nonEmpty }
        case _ => matchesAlways
      }

    val matchesShowInactive: UserDomainObject => Boolean =
      if (state.isShowInactive) matchesAlways else { _.isActive }

    val matchesAll = (user: UserDomainObject) => List(matchesText, matchesRoles, matchesShowInactive) forall (_ apply user)

    usersUI.removeAllItems
    for {
      user <- roleMapper.getAllUsers.toList if !user.isDefaultUser && matchesAll(user)
      userId = Int box user.getId
    } usersUI.addItem(Array[AnyRef](
                               userId,
                               user.getLoginName,
                               user.getFirstName,
                               user.getLastName,
                               Boolean box user.isSuperAdmin,
                               Boolean box user.isActive),
                             userId)
  }

  /**
   * Selected users.
   */
  def selection: Seq[UserDomainObject] = selectionRef.get

  override def notifyListeners() = notifyListeners(selection)
}


case class UserSearchFormState(
  text: Option[String] = Some(""),
  roles: Option[Set[RoleId]] = None,
  isShowInactive: Boolean = false
)

class UserSearchForm extends ImcmsServicesSupport {
  val ui: UserSearchFormUI = letret(new UserSearchFormUI) { ui =>
    ui.chkText.addValueChangeHandler {
      SearchFormUtil.toggle(ui, "admin.access.user.search.frm.fld.text", ui.chkText, ui.txtText)
    }

    ui.chkRoles.addValueChangeHandler {
      SearchFormUtil.toggle(ui, "admin.access.user.search.frm.fld.roles", ui.chkRoles, ui.tcsRoles)
    }
  }

  def reset() = setState(UserSearchFormState())

  def setState(state: UserSearchFormState) {
    ui.chkText.checked = state.text.isDefined
    ui.chkRoles.checked = state.roles.isDefined
    ui.chkShowInactive.checked = state.isShowInactive

    forlet(ui.chkText, ui.chkRoles, ui.chkShowInactive)(_ fireValueChange true)

    ui.txtText.value = state.text.getOrElse("")
    ui.tcsRoles.removeAllItems
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRolesExceptUsersRole) {
      ui.tcsRoles.addItem(role.getId)
      ui.tcsRoles.setItemCaption(role.getId, role.getName)
    }
    ui.tcsRoles.value = state.roles.getOrElse(Set.empty[RoleId]).asJavaCollection
  }
  
  def getState() = UserSearchFormState(
    whenOpt(ui.chkText.checked)(ui.txtText.trim),
    whenOpt(ui.chkRoles.checked)(ui.tcsRoles.value.toSet),
    ui.chkShowInactive.checked
  )  
}


class UserSearchFormUI extends CustomLayout("admin/access/user/search/form") with UndefinedSize {
  val chkText = new CheckBox("user.search.frm.fld.chk_text".i) with ExposeValueChange with Immediate
  val txtText = new TextField {
    setInputPrompt("user.search.frm.fld.txt_text.prompt".i)
    setDescription("user.search.frm.fld.txt_text.tooltip".i)
  }

  val chkRoles = new CheckBox("user.search.frm.fld.chk_roles".i) with ExposeValueChange with Immediate
  val tcsRoles = new TwinColSelect with MultiSelect2[RoleId] with TCSDefaultI18n
  val chkShowInactive = new CheckBox("user.search.frm.fld.chk_show_inactive".i) with ExposeValueChange with Immediate
  val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
    val btnReset = new Button("btn_reset".i) with SmallStyle
    val btnSearch = new Button("btn_search".i) with SmallStyle

    addComponents(this, btnReset, btnSearch)
  }

  addNamedComponents(this,
    "admin.access.user.search.frm.fld.chk_text" -> chkText,
    "admin.access.user.search.frm.fld.text" -> txtText,
    "admin.access.user.search.frm.fld.chk_roles" -> chkRoles,
    "admin.access.user.search.frm.fld.roles" -> tcsRoles,
    "admin.access.user.search.frm.fld.misc" -> chkShowInactive,
    "admin.access.user.search.frm.fld.buttons" -> lytButtons
  )
}