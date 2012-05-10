package com.imcode
package imcms
package admin.access.user

import scala.collection.breakOut
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

import imcode.server.user._
import com.imcode.util.event.Publisher
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.ui._
import java.util.concurrent.atomic.AtomicReference


trait UserSingleSelectDialog { this: OkCancelDialog =>
  val search = new UserSearch(multiSelect = false)

  mainUI = search.ui

  search.listen { btnOk setEnabled _.nonEmpty }
  search.notifyListeners()
}


trait UserSelectDialog { this: OkCancelDialog =>
  val search = new UserSearch

  mainUI = search.ui

  search.listen { btnOk setEnabled _.nonEmpty }
  search.notifyListeners()
}


class UserSingleSelect {
  private val selectionRef = new AtomicReference(Option.empty[UserDomainObject])
  val ui = new UserSingleSelectUI |>> { ui =>
    ui.btnSelect.addClickHandler {
      ui.getApplication.initAndShow(new OkCancelDialog("Select user") with UserSingleSelectDialog) { dlg =>
        dlg.wrapOkHandler {
          selection = dlg.search.selection.headOption
        }
      }
    }

    ui.btnClear.addClickHandler { selection = None }
  }

  def selection = selectionRef.get
  def selection_=(userOpt: Option[UserDomainObject]) {
    ui.btnClear.setEnabled(userOpt.isDefined)
    ui.lblName.value = userOpt match {
      case Some(user) => "[ %s ]" format user.getLoginName
      case _ => "[ not selected ]"
    }

    selectionRef.set(userOpt)
  }

  selection = None
}

class UserSingleSelectUI extends HorizontalLayout with Spacing with UndefinedSize{
  val lblName = new Label with UndefinedSize
  val btnSelect = new Button("select") with SmallStyle
  val btnClear = new Button("clear") with SmallStyle

  addComponents(this, lblName, btnSelect, btnClear)
}


class UserSearch(multiSelect: Boolean = true) extends Publisher[Seq[UserDomainObject]] with ImcmsServicesSupport {
  private val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val selectionRef = new AtomicReference(Seq.empty[UserDomainObject])

  private val searchForm = new UserSearchForm
  private val searchResult = new Table with MultiSelectBehavior[UserId] with Immediate with Selectable {
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

  val ui = new GridLayout(1, 2) |>> { ui =>
    addComponents(ui, searchForm.ui, searchResult)
  }

  searchResult.addValueChangeHandler {
    selectionRef.set(searchResult.value.map(roleMapper getUser _.intValue)(breakOut))
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

    searchResult.removeAllItems
    for {
      user <- roleMapper.getAllUsers.toList if !user.isDefaultUser && matchesAll(user)
      userId = Int box user.getId
    } searchResult.addItem(Array[AnyRef](
                               userId,
                               user.getLoginName,
                               user.getFirstName,
                               user.getLastName,
                               Boolean box user.isSuperAdmin,
                               Boolean box user.isActive),
                             userId)
  }

  /** Search result selected users */
  def selection: Seq[UserDomainObject] = selectionRef.get

  override def notifyListeners() = notifyListeners(selection)
}


case class UserSearchFormState(
  text: Option[String] = Some(""),
  roles: Option[Set[RoleId]] = None,
  isShowInactive: Boolean = false
)

class UserSearchForm extends ImcmsServicesSupport {
  val ui: UserSearchFormUI = new UserSearchFormUI |>> { ui =>
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

    doto(ui.chkText, ui.chkRoles, ui.chkShowInactive)(_ fireValueChange true)

    ui.txtText.value = state.text.getOrElse("")
    ui.tcsRoles.removeAllItems
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRolesExceptUsersRole) {
      ui.tcsRoles.addItem(role.getId)
      ui.tcsRoles.setItemCaption(role.getId, role.getName)
    }
    ui.tcsRoles.value = state.roles.getOrElse(Set.empty[RoleId]).asJavaCollection
  }
  
  def getState() = UserSearchFormState(
    when(ui.chkText.checked)(ui.txtText.trim),
    when(ui.chkRoles.checked)(ui.tcsRoles.value.toSet),
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
  val tcsRoles = new TwinColSelect with MultiSelect[RoleId] with TCSDefaultI18n
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