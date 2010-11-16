package com.imcode.imcms.sysadmin.permissions

import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger}
import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.api.{CategoryType, SystemProperty, IPAccess, Document}
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.imcode.imcms.vaadin._;
import imcode.server.document.{TemplateDomainObject, CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}
import com.imcode.imcms.vaadin._;
import com.imcode.imcms.vaadin.AbstractFieldWrapper._;


// user-admin-roles???

class UserViewFilter extends VerticalLayout { //CustomLayout
  val lytParams = new FormLayout
  
  val txtText = new TextField("Username, email, first name, last name, email") {
    setColumns(20)
  }
  val lytText = new VerticalLayout {
    setCaption("Free text")
    addComponent(txtText)
  }
  val btnApply = new Button("Search") with LinkStyle
  val btnClear = new Button("Clear") with LinkStyle
  val chkShowInactive = new CheckBox("Show inactive")
  val lstRoles = new ListSelect("Only with role(s)") {
    setColumns(21)
    setRows(5)
    setNullSelectionAllowed(false)
  }

  val lytControls = new HorizontalLayout {
    setSpacing(true)
    addComponents(this, chkShowInactive, btnClear, btnApply)
  }

  addComponents(lytParams, lytText, lstRoles, lytControls)
  addComponents(this, lytParams)
  setSpacing(true)
}

//

class UserDialogContent extends FormLayout {
  val txtLogin = new TextField("Username")
  val txtPassword = new TextField("4-16 characters") { setSecret(true) }
  val txtVerifyPassword = new TextField("4-16 characters (retype)") { setSecret(true) }
  val txtFirstName = new TextField("First")
  val txtLastName = new TextField("Last")
  val chkActivated = new CheckBox("Activated")
  val tslRoles = new TwinSelect[RoleId]("Roles")
  val sltUILanguage = new Select("Interface language") {
    setNullSelectionAllowed(false)
  }
  val txtEmail = new TextField("Email")
  
  val lytPassword = new HorizontalLayoutView("Password") {
      addComponent(txtPassword)
      addComponent(txtVerifyPassword)
  }

  val lytName = new HorizontalLayoutView("Name") {
      addComponent(txtFirstName)
      addComponent(txtLastName)
  }


  val lytLogin = new HorizontalLayoutView("Login") {
    addComponents(this, txtLogin, chkActivated)
    setComponentAlignment(chkActivated, Alignment.BOTTOM_LEFT)
  }

  val btnContacts = new Button("Edit...") {
    setStyleName(Button.STYLE_LINK)
    setEnabled(false)
  }

  val lytContacts = new HorizontalLayout {
    setCaption("Contacts")
    addComponent(btnContacts)
  }

  forlet(txtLogin, txtPassword, txtVerifyPassword, txtEmail) { _ setRequired true }

  addComponents(this, lytLogin, lytPassword, lytName, txtEmail, sltUILanguage, tslRoles, lytContacts)
}


class UsersView(application: VaadinApplication) extends {
  val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
} with TableView {
  
  val mbUser = new MenuBar
  val miNew = mbUser.addItem("Add new", new ThemeResource("icons/16/document-add.png"), null)
  val miEdit = mbUser.addItem("Edit", new ThemeResource("icons/16/settings.png"), null)
  val filter = new UserViewFilter {
    roleMapper.getAllRoleNames foreach { name =>
      lstRoles addItem name
    }
  }

  lytToolBar.addComponent(mbUser)
  lytTable.addComponent(filter, 0)

  miNew setCommand unit {
    application.initAndShow(new OkCancelDialog("New user")) { w =>
      let(w setMainContent new UserDialogContent) { c =>
        for (role <- roleMapper.getAllRoles if role.getId != RoleId.USERS) {
          c.tslRoles.addAvailableItem(role.getId, role.getName)
        }

        let(Imcms.getServices.getLanguageMapper.getDefaultLanguage) { l =>
          c.sltUILanguage.addItem(l)
          c.sltUILanguage.select(l)
        }

        c.chkActivated.setValue(true)

        w addOkButtonClickListener {
          let(new UserDomainObject) { u =>
            u setActive c.chkActivated.booleanValue
            u setFirstName c.txtFirstName.stringValue
            u setLastName c.txtLastName.stringValue
            u setLoginName c.txtLogin.stringValue
            u setPassword c.txtPassword.stringValue
            u setRoleIds c.tslRoles.chosenItemIds.toArray
            u setLanguageIso639_2 c.sltUILanguage.stringValue

            roleMapper.addUser(u)
            reloadTable
          }
        }
      }
    }
  }

  miEdit setCommand unit {
    whenSelected[JInteger](table) { userId =>
      application.initAndShow(new OkCancelDialog("Edit user")) { w =>
        let(w setMainContent new UserDialogContent) { c =>
          val user = roleMapper.getUser(userId.intValue)
          val userRoleIds = user.getRoleIds

          c.chkActivated setValue user.isActive
          c.txtFirstName setValue user.getFirstName
          c.txtLastName setValue user.getLastName
          c.txtLogin setValue user.getLoginName
          c.txtPassword setValue user.getPassword

          for {
            role <- roleMapper.getAllRoles
            roleId = role.getId
            if roleId != RoleId.USERS
          } {
            if (userRoleIds contains roleId) {
              c.tslRoles.addChosenItem(roleId, role.getName)
            } else {
              c.tslRoles.addAvailableItem(roleId, role.getName)
            }
          }

          let(Imcms.getServices.getLanguageMapper.getDefaultLanguage) { l =>
            c.sltUILanguage.addItem(l)
          }

          c.sltUILanguage.select(user.getLanguageIso639_2)

          w addOkButtonClickListener {
            user setActive c.chkActivated.booleanValue
            user setFirstName c.txtFirstName.stringValue
            user setLastName c.txtLastName.stringValue
            user setLoginName c.txtLogin.stringValue
            user setPassword c.txtPassword.stringValue
            user setRoleIds c.tslRoles.chosenItemIds.toArray
            user setLanguageIso639_2 c.sltUILanguage.stringValue

            roleMapper.saveUser(user)
            reloadTable
          }
        }
      }
    }
  }

  override def tableFields = List(
    ("Id", classOf[JInteger],  null),
    ("Login", classOf[String],  null),
    ("First name", classOf[String],  null),
    ("Last name", classOf[String],  null),
    ("Superadmin?", classOf[JBoolean],  null),
    ("Active?", classOf[JBoolean],  null))

  override def tableRows =
    roleMapper.getAllUsers.toList map { user =>
      val userId = Int box user.getId

      userId -> List(userId,
                     user.getLoginName,
                     user.getFirstName,
                     user.getLastName,
                     Boolean box user.isSuperAdmin,
                     Boolean box user.isActive)
    }  
}

case class TableProperty(id: AnyRef, clazz: JClass[_], defaultValue: AnyRef = null)

class UserUI extends VerticalLayout {
  val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  val filterUI = new UserViewFilter
  val tblUsers = new Table {
    val properties =
      TableProperty("Id", classOf[JInteger]) ::
      TableProperty ("Login", classOf[String]) ::
      TableProperty("First name", classOf[String]) ::
      TableProperty("Last name", classOf[String]) ::
      TableProperty("Superadmin?", classOf[JBoolean]) ::
      TableProperty("Active?", classOf[JBoolean]) :: Nil

    properties foreach { c => addContainerProperty(c.id, c.clazz, c.defaultValue) }
    
    setSelectable(true)
    setImmediate(true)
    setPageLength(5)
  }

  addComponents(this, filterUI, tblUsers)

  tblUsers addListener unit {
    whenSelected[Integer](tblUsers) { id =>
      tblUsers.removeAllItems
      
      roleMapper.getAllUsers.toList map { user =>
        val userId = Int box user.getId

        userId -> List(userId,
                       user.getLoginName,
                       user.getFirstName,
                       user.getLastName,
                       Boolean box user.isSuperAdmin,
                       Boolean box user.isActive)
      }
    }
  }
}