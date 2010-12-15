package com.imcode.imcms.sysadmin.permissions

import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger}
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
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
import com.vaadin.data.util.IndexedContainer
import java.util.concurrent.atomic.AtomicReference;
import imcode.server.document.{TemplateDomainObject, CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}
import com.imcode.imcms.vaadin._;


// user-admin-roles???

// user search UI / user picker?



/**
 * 
 */
class UserSelectFilterUI extends VerticalLayout { //CustomLayout
  val lytParams = new FormLayout
  
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

  val lytControls = new HorizontalLayout {
    setSpacing(true)
    addComponents(this, chkShowInactive, btnReset, btnApply)
  }

  addComponents(lytParams, lytText, lstRoles, lytControls)
  addComponents(this, lytParams)
  setSpacing(true)
}


class UserSelectUI extends VerticalLayout {
  val filterUI = new UserSelectFilterUI
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


// todo: db access, filtering should be optimized, + lazy loading/limit/pagination
class UserSelect {
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val selectionRef = new AtomicReference[Option[UserDomainObject]](None)

  val selectionListeners = ListBuffer.empty[Option[UserDomainObject] => Unit]

  val ui: UserSelectUI = letret(new UserSelectUI) { ui =>
    roleMapper.getAllRolesExceptUsersRole foreach { role =>
      ui.filterUI.lstRoles.addItem(role.getId)
      ui.filterUI.lstRoles.setItemCaption(role.getId, role.getName)
    }     

    ui.tblUsers.itemsProvider = () => {
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

      for {
        user <- roleMapper.getAllUsers.toList if matchesAll(user)
        userId = Int box user.getId
      } yield userId -> List(userId,
                             user.getLoginName,
                             user.getFirstName,
                             user.getLastName,
                             Boolean box user.isSuperAdmin,
                             Boolean box user.isActive)
    }
      
    ui.filterUI.btnApply addListener block { reload() }
    ui.filterUI.btnReset addListener block {
      ui.filterUI.txtText.value = ""
      ui.filterUI.chkShowInactive.value = false
      ui.filterUI.lstRoles.value foreach ui.filterUI.lstRoles.unselect
      reload()
    }
    ui.tblUsers addListener block {
      selectionRef.set(Option(ui.tblUsers.value) map { roleMapper getUser _.intValue })
      notifyListeners()
    }
  }

  reload()

  def selection = selectionRef.get

  def reload() = ui.tblUsers.reload()

  def notifyListeners() = let(selection) { optionUser => for (listener <- selectionListeners) listener(optionUser) }
}


/**
 * Adds user select ui as a Ok-Cancel dialog content.
 * Adds listener - dialog's Ok button is enabled/disabled if a user is selected/unselected. 
 */
trait UserSelectDialog { this: OkCancelDialog =>  
  val userSelect = new UserSelect

  mainContent = userSelect.ui

  userSelect.selectionListeners += { btnOk setEnabled _.isDefined }
  userSelect.notifyListeners()
}


/**
 * Add/Edit user dialog content.
 */
class UserDialogContent extends FormLayout {
  val txtLogin = new TextField("Username")
  val txtPassword = new TextField("4-16 characters") with Secret
  val txtVerifyPassword = new TextField("4-16 characters (retype)") with Secret
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

  val btnContacts = new Button("Edit...") with LinkStyle with Disabled

  val lytContacts = new HorizontalLayout {
    setCaption("Contacts")
    addComponent(btnContacts)
  }

  forlet(txtLogin, txtPassword, txtVerifyPassword, txtEmail) { _ setRequired true }

  addComponents(this, lytLogin, lytPassword, lytName, txtEmail, sltUILanguage, tslRoles, lytContacts)
}


class UserManager(app: VaadinApplication) {
  val userSelect = new UserSelect
  
  val ui = letret(new UserManagerUI(userSelect.ui)) { ui =>
    val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
    
    ui.miNew setCommand block {
      app.initAndShow(new OkCancelDialog("New user")) { dlg =>
        let(dlg setMainContent new UserDialogContent) { c =>
          for (role <- roleMapper.getAllRoles if role.getId != RoleId.USERS) {
            c.tslRoles.addAvailableItem(role.getId, role.getName)
          }

          let(Imcms.getServices.getLanguageMapper.getDefaultLanguage) { l =>
            c.sltUILanguage.addItem(l)
            c.sltUILanguage.select(l)
          }

          c.chkActivated.setValue(true)

          dlg addOkButtonClickListener {
            let(new UserDomainObject) { u =>
              u setActive c.chkActivated.booleanValue
              u setFirstName c.txtFirstName.value
              u setLastName c.txtLastName.value
              u setLoginName c.txtLogin.value
              u setPassword c.txtPassword.value
              u setRoleIds c.tslRoles.chosenItemIds.toArray
              u setLanguageIso639_2 c.sltUILanguage.value

              roleMapper.addUser(u)
              userSelect.reload()
            }
          }
        }
      }
    }

   ui.miEdit setCommand block {
      whenSelected(ui.userSelectUI.tblUsers) { userId =>
        app.initAndShow(new OkCancelDialog("Edit user")) { dlg =>
          let(dlg setMainContent new UserDialogContent) { c =>
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

            dlg addOkButtonClickListener {
              user setActive c.chkActivated.booleanValue
              user setFirstName c.txtFirstName.value
              user setLastName c.txtLastName.value
              user setLoginName c.txtLogin.value
              user setPassword c.txtPassword.value
              user setRoleIds c.tslRoles.chosenItemIds.toArray
              user setLanguageIso639_2 c.sltUILanguage.value

              roleMapper.saveUser(user)
              userSelect.reload()
            }
          }
        }
      }
    }

    userSelect.selectionListeners += { ui.miEdit setEnabled _.isDefined }
    userSelect.notifyListeners()
  }
}


class UserManagerUI(val userSelectUI: UserSelectUI) extends VerticalLayout with Spacing {
  val menuBar = new MenuBar
  val miNew = menuBar.addItem("Add new", null) // new ThemeResource("icons/16/document-add.png")
  val miEdit = menuBar.addItem("Edit", null)   // new ThemeResource("icons/16/settings.png")

  addComponents(this, menuBar, userSelectUI)
}