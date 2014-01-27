package com.imcode
package imcms
package admin.access.user.projection

import com.imcode.imcms.vaadin.data.util.converter.TableCellStringToBooleanConverter
import com.vaadin.ui.themes.Reindeer
import scala.collection.breakOut
import scala.collection.JavaConverters._

import com.imcode.util.event.Publisher
import java.util.concurrent.atomic.AtomicReference
import com.imcode.imcms.admin.access.user.projection.filter.UserFilter
import com.vaadin.ui.{VerticalLayout, Table}

import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.component._

import _root_.imcode.server.user.UserDomainObject


class UsersProjection(multiSelect: Boolean = true) extends Publisher[Seq[UserDomainObject]] with ImcmsServicesSupport {

  private val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val selectionRef = new AtomicReference(Seq.empty[UserDomainObject])

  private val filter = new UserFilter
  val usersView = new Table with SelectWithTypedItemId[UserId] with Immediate with Selectable with FullSize |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[UserId]("users_projection.container_property.id"),
      PropertyDescriptor[String]("users_projection.container_property.login"),
      PropertyDescriptor[String]("users_projection.container_property.first_name"),
      PropertyDescriptor[String]("users_projection.container_property.last_name"),
      PropertyDescriptor[String]("users_projection.container_property.email"),
      PropertyDescriptor[String]("users_projection.container_property.language"),
      PropertyDescriptor[JBoolean]("users_projection.container_property.is_superadmin"),
      PropertyDescriptor[JBoolean]("users_projection.container_property.is_inactive"),
      PropertyDescriptor[Void]("")
    )

    tbl.setMultiSelect(multiSelect)
    tbl.setColumnHeaders(tbl.getContainerPropertyIds.asScala.map(_.toString.i).toArray: _*)
    tbl.addStyleName(Reindeer.TABLE_BORDERLESS)
    tbl.setColumnExpandRatio("", 1f)
    tbl.setColumnAlignment("users_projection.container_property.id", Table.Align.RIGHT)
    tbl.setColumnAlignment("users_projection.container_property.is_superadmin", Table.Align.CENTER)
    tbl.setColumnAlignment("users_projection.container_property.is_inactive", Table.Align.CENTER)

    val converter = TableCellStringToBooleanConverter.falseAsEmptyString

    tbl.setConverter("users_projection.container_property.is_superadmin", converter)
    tbl.setConverter("users_projection.container_property.is_inactive", converter)
  }

  val view = new VerticalLayout with FullSize |>> { w =>
    w.addComponents(filter.view, usersView)
    w.setExpandRatio(usersView, 1f)
  }

  usersView.addValueChangeHandler { _ =>
    selectionRef.set(usersView.selection.map(userId => roleMapper.getUser(userId))(breakOut))
    notifyListeners()
  }

  filter.view.btnFilter.addClickHandler { _ => reload() }
  filter.view.btnReset.addClickHandler { _ => reset() }

  reset()

  def reset() {
    filter.reset()
    reload()
  }


  def reload() {
    val state = filter.getFilterParameters

    val loginPredicateOpt: Option[(UserDomainObject => Boolean)] = PartialFunction.condOpt(state.text) {
      case Some(text) if text.nonEmpty => { _.getLoginName.toLowerCase.contains(text.toLowerCase) }
    }

    val rolesPredicateOpt: Option[(UserDomainObject => Boolean)] = PartialFunction.condOpt(state.roles) {
      case Some(roles) if roles.nonEmpty => { _.getRoleIds.intersect(filter.view.tcsRoles.selection).nonEmpty }
    }

    val inactivePredicateOpt: Option[(UserDomainObject => Boolean)] = PartialFunction.condOpt(state.isShowInactive) {
      case false => { _.isActive }
    }

    val predicate: (UserDomainObject => Boolean) =
      Seq(loginPredicateOpt, rolesPredicateOpt, inactivePredicateOpt).collect { case Some(p) => p } match {
        case Nil => { _ => true }
        case ps => { u => ps.forall(p => p(u)) }
      }

    usersView.removeAllItems()
    for {
      user <- roleMapper.getAllUsers.toList if !user.isDefaultUser && predicate(user)
      userId = user.getId : JInteger
    } {
      usersView.addRow(
        userId,
        userId,
        user.getLoginName,
        user.getFirstName,
        user.getLastName,
        user.getEmailAddress,
        user.getLanguageIso639_2,
        user.isSuperAdmin : JBoolean,
        !user.isActive : JBoolean,
        null
      )
    }
  }


  def selection: Seq[UserDomainObject] = selectionRef.get

  override def notifyListeners(): Unit = notifyListeners(selection)
}
