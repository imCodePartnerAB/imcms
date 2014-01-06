package com.imcode
package imcms
package admin.access.user.projection

import scala.collection.breakOut
import scala.collection.JavaConverters._

import com.imcode.util.event.Publisher
import java.util.concurrent.atomic.AtomicReference
import com.imcode.imcms.admin.access.user.projection.filter.UserFilter
import com.vaadin.ui.{GridLayout, Table}

import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.component._

import _root_.imcode.server.user.UserDomainObject


class UsersProjection(multiSelect: Boolean = true) extends Publisher[Seq[UserDomainObject]] with ImcmsServicesSupport {
  private val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val selectionRef = new AtomicReference(Seq.empty[UserDomainObject])

  private val filter = new UserFilter
  private val filteredUsersView = new Table with MultiSelectBehavior[UserId] with Immediate with Selectable |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[UserId]("users_projection.container_property.id"),
      PropertyDescriptor[String]("users_projection.container_property.login"),
      PropertyDescriptor[String]("users_projection.container_property.first_name"),
      PropertyDescriptor[String]("users_projection.container_property.last_name"),
      PropertyDescriptor[JBoolean]("users_projection.container_property.is_superadmin"),
      PropertyDescriptor[JBoolean]("users_projection.container_property.is_active"))

    tbl.setMultiSelect(multiSelect)

    tbl.setColumnHeaders(tbl.getContainerPropertyIds.asScala.map(_.toString.i).toArray: _*)
    tbl.addStyleName("striped")
  }

  val view = new GridLayout(1, 2) |>> { w =>
    w.addComponents(filter.view, filteredUsersView)
  }

  filteredUsersView.addValueChangeHandler { _ =>
    selectionRef.set(filteredUsersView.value.asScala.map(userId => roleMapper.getUser(userId))(breakOut))
    notifyListeners()
  }

  filter.view.lytButtons.btnFilter.addClickHandler { _ => reload() }
  filter.view.lytButtons.btnReset.addClickHandler { _ => reset() }

  reset()

  def reset() {
    filter.reset()
    reload()
  }


  def reload() {
    val matchesAlways = Function.const(true)_
    val state = filter.getValues

    val matchesText: (UserDomainObject => Boolean) =
      state.text match {
        case Some(text) if text.nonEmpty => { _.getLoginName.contains(text) }
        case _ => matchesAlways
      }

    val matchesRoles: (UserDomainObject => Boolean) =
      state.roles match {
        case Some(roles) if roles.nonEmpty => { _.getRoleIds.intersect(filter.view.tcsRoles.value.asScala.toSeq).nonEmpty }
        case _ => matchesAlways
      }

    val matchesShowInactive: (UserDomainObject => Boolean) =
      if (state.isShowInactive) matchesAlways else { _.isActive }

    val matchesAll = (user: UserDomainObject) => List(matchesText, matchesRoles, matchesShowInactive) forall (_ apply user)

    filteredUsersView.removeAllItems
    for {
      user <- roleMapper.getAllUsers.toList if !user.isDefaultUser && matchesAll(user)
      userId = Int box user.getId
    } {
      filteredUsersView.addItem(
        Array[AnyRef](
          userId,
          user.getLoginName,
          user.getFirstName,
          user.getLastName,
          user.isSuperAdmin : JBoolean,
          user.isActive : JBoolean
        ),
        userId)
    }
  }


  def selection: Seq[UserDomainObject] = selectionRef.get

  override def notifyListeners(): Unit = notifyListeners(selection)
}
