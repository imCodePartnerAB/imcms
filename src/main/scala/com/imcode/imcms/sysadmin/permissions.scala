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
import com.imcode.imcms.vaadin._
import com.vaadin.data.util.IndexedContainer;
import imcode.server.document.{TemplateDomainObject, CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}
import com.imcode.imcms.vaadin._;
import com.imcode.imcms.vaadin.AbstractFieldWrapper._;


// user-admin-roles???

// user search UI / user picker?



/**
 * 
 */
class UserListFilterUI extends VerticalLayout { //CustomLayout
  val lytParams = new FormLayout
  
  val txtText = new TextField("Ex: username, email, first name, last name...") {
    setColumns(20)
  }
  val lytText = new VerticalLayout {
    setCaption("Free text")
    addComponent(txtText)
  }
  val btnApply = new Button("Search") with LinkStyle
  val btnClear = new Button("Clear") with LinkStyle
  val chkShowInactive = new CheckBox("Show inactive")
  val lstRoles = new ListSelect("Only with role(s)") with ValueType[JCollection[RoleId]] with ItemIdType[RoleId] with MultiSelect {
    setColumns(21)
    setRows(5)
  }

  val lytControls = new HorizontalLayout {
    setSpacing(true)
    addComponents(this, chkShowInactive, btnClear, btnApply)
  }

  addComponents(lytParams, lytText, lstRoles, lytControls)
  addComponents(this, lytParams)
  setSpacing(true)
}


class UserListUI extends VerticalLayout {
  val filterUI = new UserListFilterUI
  val tblUsers = new Table with ValueType[JInteger] with ItemIdType[JInteger] with Reloadable with Immediate with Selectable {
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


// todo: prototype, db access, filtering should be optimized, + lazy loading
class UserList(onSelect: Option[UserDomainObject] => Unit = { _ => }) {
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

  val ui = letret(new UserListUI) { ui =>          
    roleMapper.getAllRolesExceptUsersRole foreach { role =>
      ui.filterUI.lstRoles.addItem(role.getId)
      ui.filterUI.lstRoles.setItemCaption(role.getId, role.getName)
    }     

    ui.tblUsers.itemsProvider = () => {
      val matchesText: UserDomainObject => Boolean = ui.filterUI.txtText.value.trim match {
        case text if text.isEmpty => { _ => true }
        case text => { _.getLoginName.contains(text) }
      }

      val matchesRole: UserDomainObject => Boolean = ui.filterUI.lstRoles.value match {
        case rolesIds if rolesIds.isEmpty => { _ => true }
        case rolesIds => { _.getRoleIds.intersect(rolesIds.toSeq).nonEmpty }
      }

      for {
        user <- roleMapper.getAllUsers.toList if matchesText(user) && matchesRole(user)
        userId = Int box user.getId
      } yield userId -> List(userId,
                             user.getLoginName,
                             user.getFirstName,
                             user.getLastName,
                             Boolean box user.isSuperAdmin,
                             Boolean box user.isActive)
    }
      
    ui.filterUI.btnApply addListener block { ui.tblUsers.reload }
    ui.filterUI.btnClear addListener block {
      ui.filterUI.txtText.value = ""
      ui.filterUI.lstRoles.select(null)
      ui.tblUsers.reload
    }
    ui.tblUsers addListener block {
      onSelect(Option(ui.tblUsers.value) map { roleMapper getUser _.intValue })
    }
  }
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
  val sltUILanguage = new Select("Interface language") with ValueType[String] with NoNullSelection
    

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
  val filter = new UserListFilterUI {
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
            u setFirstName c.txtFirstName.value
            u setLastName c.txtLastName.value
            u setLoginName c.txtLogin.value
            u setPassword c.txtPassword.value
            u setRoleIds c.tslRoles.chosenItemIds.toArray
            u setLanguageIso639_2 c.sltUILanguage.value

            roleMapper.addUser(u)
            reloadTable
          }
        }
      }
    }
  }

  miEdit setCommand block {
    whenSelected(table) { userId =>
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
            user setFirstName c.txtFirstName.value
            user setLastName c.txtLastName.value
            user setLoginName c.txtLogin.value
            user setPassword c.txtPassword.value
            user setRoleIds c.tslRoles.chosenItemIds.toArray
            user setLanguageIso639_2 c.sltUILanguage.value

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

class UserUI extends VerticalLayout {
  val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  val filterUI = new UserListFilterUI
  val tblUsers = new Table with ValueType[Integer] with Immediate with Selectable {
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
  // ?????
  tblUsers addListener block {
    whenSelected(tblUsers) { id =>
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