package com.imcode.imcms.servlet.superadmin.vaadin

import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger}
import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.servlet.superadmin.AdminSearchTerms
import imcode.server.document.DocumentDomainObject
import com.imcode.imcms.api.Document.PublicationStatus
import com.vaadin.terminal.UserError
import imcode.util.Utility
import imcode.server.user._
import com.imcode.imcms.api.{SystemProperty, IPAccess, Document}
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.vaadin.ui.Layout.MarginInfo
import java.io.{OutputStream, FileOutputStream, File}
import com.imcode.imcms.servlet.superadmin.vaadin.ui._
import com.imcode.imcms.servlet.superadmin.vaadin.ui.UI._
import java.util.concurrent.atomic.AtomicReference


class App extends com.vaadin.Application {

  setTheme("imcms")
  
  object Menu extends MenuItem {
    object About extends MenuItem(this)
    object Settings extends MenuItem(this) {
      object Languages extends MenuItem(this)
      object Properties extends MenuItem(this)
    }
    object Documents extends MenuItem(this) {
      object Categories extends MenuItem(this)
      object Templates extends MenuItem(this)
      object Profiles extends MenuItem(this)
      object Links extends MenuItem(this)
    }
    object Permissions extends MenuItem(this) {
      object Users extends MenuItem(this)
      object Roles extends MenuItem(this)
      object IP_Access extends MenuItem(this)
    }
    object Statistics extends MenuItem(this) {
      object SearchTerms extends MenuItem(this)
      object SessionCounter extends MenuItem(this)
    }
    object Filesystem extends MenuItem(this)
  } 

  val languageDao = Imcms.getSpringBean("languageDao").asInstanceOf[LanguageDao]
  val systemDao = Imcms.getSpringBean("systemDao").asInstanceOf[SystemDao]
  val ipAccessDao = Imcms.getSpringBean("ipAccessDao").asInstanceOf[IPAccessDao]  

  def initAndShow[W <: Window](window: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true)(init: W => Unit) {
    init(window)
    window setModal modal
    window setResizable resizable
    window setDraggable draggable
    wndMain addWindow window
  }


  abstract class TableViewTemplate extends GridLayout(1,3) {
    val tblItems = new Table {
      setSelectable(true)
      setImmediate(true)
      setPageLength(10)

      setSizeFull

      addListener { resetComponents }

      tableProperties foreach (addContainerProperties(this, _))
    }

    val pnlHeader = new Panel {
      val layout = new HorizontalLayout {
        setSpacing(true)
        setMargin(true, false, true, false)
      }

      setContent(layout)
      addStyleName("light")
    }

    val btnReload = new Button("Reload") {
      addListener { reloadTableItems }
    }
    
    val pnlFooter = new Panel {
      val layout = new GridLayout(1,1) {
        addComponent(btnReload)
        setComponentAlignment(btnReload, Alignment.MIDDLE_RIGHT)
        setSizeFull

      }
      setMargin(true, false, true, false)
      setContent(layout)
      addStyleName("light")
    }

    addComponents(this, pnlHeader, tblItems, pnlFooter)

    // Investigate: List[(AnyRef, Array[AnyRef])]
    def tableItems(): Seq[(AnyRef, Seq[AnyRef])] = List.empty

    def tableProperties: Seq[(AnyRef, JClass[_], AnyRef)] = List.empty

    def reloadTableItems {
      tblItems.removeAllItems

      for((id, cells) <- tableItems()) tblItems.addItem(cells.toArray, id)
      //for ((id:, cells:) <- tableItems()) tblItems.addItem(cells, id)
    }

    def resetComponents = {}
    
    reloadTableItems
    resetComponents
  }

  def NA(id: Any) = new TabSheetView {
    addTab(new VerticalLayoutView(id.toString) {
      addComponent(new Label("NOT AVAILABLE"))
    })
  }

  val labelAbout = new VerticalLayoutView {
    addComponent(new Panel("About") {
      let(getContent) {
        case c: VerticalLayout =>
          c.setMargin(true)
          c.setSpacing(true)
      }

      addComponent(new Label("""|Welcome to the imCMS new admin UI prototype -
                     | please pick a task from the menu. Note that some views are not (yet) available. 
                     |""".stripMargin))
    })
  }

  val wndMain = new Window {
    val content = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL)
    val treeMenu = new Tree {
      setImmediate(true)
    }

    def initMenu(menu: MenuItem, setParent: Boolean = false) {
      menu.parent match {
        case null =>
          menu.items foreach (initMenu(_))
        
        case parent =>
          treeMenu addItem menu
          if (setParent) treeMenu setParent (menu, parent)
          
          menu.items match {
            case Nil => treeMenu setChildrenAllowed (menu, false)
            case items => items foreach (initMenu(_, setParent=true))
          }
      }
    }

    treeMenu addListener (new ValueChangeListener {
      def valueChange(e: ValueChangeEvent) {
        content.setSecondComponent(
          e.getProperty.getValue match {
            case null | Menu.About => labelAbout
            
            case Menu.Statistics.SearchTerms => searchTerms
            case Menu.Documents.Categories => categories
            case Menu.Settings.Languages => languagesPanel
            case Menu.Settings.Properties => settingsProperties
            case Menu.Statistics.SessionCounter => settingSessionCounter
            case Menu.Documents => documentsTable
            case Menu.Permissions.Roles => roles
            case Menu.Permissions.Users => users
            case Menu.Permissions.IP_Access => ipAccess
            case Menu.Filesystem => filesystem
            case Menu.Documents.Templates => templates

            case other => NA(other)
          })
      }
    })

    content setFirstComponent treeMenu
    this setContent content
  }

  def show(wndChild: Window, modal: Boolean = true) {
    wndChild setModal modal
    wndMain addWindow wndChild
  }

  def init {
    wndMain initMenu Menu
    Menu.items foreach { wndMain.treeMenu expandItemsRecursively _ }
    wndMain.treeMenu select Menu.About
    this setMainWindow wndMain    
  }

  //
  // Languages panel
  // 
  def languagesPanel = {
    class LanguageWindow(caption: String) extends OkCancelDialog(caption) {
      val txtId = new TextField("Id")
      val txtCode = new TextField("Code")
      val txtName = new TextField("Name")
      val txtNativeName = new TextField("Native name")
      val chkEnabled = new CheckBox("Enabled")

      val lytMainContent = new FormLayout

      // lytMainContent setMargin true
      
      addComponents(lytMainContent, txtId, txtCode, txtName, txtNativeName, chkEnabled)

      setMainContent(lytMainContent)
    }

    val table = new Table

    table setPageLength 10
    table setSelectable true
    table setImmediate true

    table.addContainerProperty("Id", classOf[JInteger],  null)
    table.addContainerProperty("Code", classOf[String],  null)
    table.addContainerProperty("Name", classOf[String],  null)
    table.addContainerProperty("Native name", classOf[String],  null)
    table.addContainerProperty("Enabled", classOf[JBoolean],  null)
    table.addContainerProperty("Default", classOf[JBoolean],  null)

    def reloadTable {
      table.removeAllItems

      val defaultLanguageId = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt

      languageDao.getAllLanguages.toList foreach { language =>
        table.addItem(Array(language.getId, language.getCode, language.getName,
                            language.getNativeName, language.isEnabled,
                            Boolean box (language.getId == defaultLanguageId)),
                      language.getId)
      }
    }    

    val pnlControls = new Panel with Button.ClickListener {
      val btnNew = new Button("New")
      val btnEdit = new Button("Edit")
      val btnSetDefault = new Button("Set default")
      val btnDelete = new Button("Delete")

      val layout = new HorizontalLayout
      setContent(layout)
      layout.setSpacing(true)

      List(btnNew, btnEdit, btnSetDefault, btnDelete).foreach { btn =>
        this addComponent btn
        btn addListener this
      }

      def buttonClick(clickEvent: Button#ClickEvent) {
        val defaultLanguageId = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt

        clickEvent.getButton match {

          case `btnNew` =>
            def isInt(x:Any) = x match {
              case n: Int => true
              case s: String => s.nonEmpty && s.forall(_.isDigit)
              case _ => false
            }

            initAndShow(new LanguageWindow("New language")) { wndEditLanguage =>
              val language = new com.imcode.imcms.api.I18nLanguage

              wndEditLanguage addOkButtonClickListener {
                if (!isInt(wndEditLanguage.txtId.getValue)) {
                  wndEditLanguage.txtId.setComponentError(new UserError("Id must be an Int"))
                } else {
                  language.setId(Int box wndEditLanguage.txtId.getValue.asInstanceOf[String].toInt)
                  language.setCode(wndEditLanguage.txtCode.getValue.asInstanceOf[String])
                  language.setName(wndEditLanguage.txtName.getValue.asInstanceOf[String])
                  language.setNativeName(wndEditLanguage.txtNativeName.getValue.asInstanceOf[String])
                  language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[JBoolean])

                  languageDao.saveLanguage(language)
                  reloadTable
                }
              }
            }

          case `btnEdit` =>
            val languageId = table.getValue.asInstanceOf[JInteger]
            val language = languageDao.getById(languageId)

            initAndShow(new LanguageWindow("Edit language")) { wndEditLanguage =>
              wndEditLanguage.txtId.setValue(language.getId)
              wndEditLanguage.txtId.setEnabled(false)
              wndEditLanguage.txtCode.setValue(language.getCode)
              wndEditLanguage.txtName.setValue(language.getName)
              wndEditLanguage.txtNativeName.setValue(language.getNativeName)
              wndEditLanguage.chkEnabled.setValue(language.isEnabled)

              wndEditLanguage addOkButtonClickListener {
                language.setCode(wndEditLanguage.txtCode.getValue.asInstanceOf[String])
                language.setName(wndEditLanguage.txtName.getValue.asInstanceOf[String])
                language.setNativeName(wndEditLanguage.txtNativeName.getValue.asInstanceOf[String])
                language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[JBoolean])

                languageDao.saveLanguage(language)
                reloadTable
              }
            }

          case `btnSetDefault` =>
            initAndShow(new ConfirmationDialog("Confirmation", "Change default language?")) { wndConfirmation =>
              wndConfirmation addOkButtonClickListener {
                val languageId = table.getValue.asInstanceOf[JInteger]
                val property = systemDao.getProperty("DefaultLanguageId")

                property.setValue(languageId.toString)
                systemDao.saveProperty(property)
                reloadTable
              }
            }

          case `btnDelete` =>
            initAndShow(new ConfirmationDialog("Confirmation", "Delete language from the system?")) { wndConfirmation =>
              wndConfirmation addOkButtonClickListener {
                val languageId = table.getValue.asInstanceOf[JInteger]
                languageDao.deleteLanguage(languageId)
                reloadTable
              }
            }
        }
      }
    }    

    def resetControls = {
      val languageId = table.getValue.asInstanceOf[JInteger]

      if (languageId == null) {
        pnlControls.btnDelete.setEnabled(false)
        pnlControls.btnEdit.setEnabled(false)
        pnlControls.btnSetDefault.setEnabled(false)
      } else {
        val defaultLanguageId = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt

        pnlControls.btnEdit.setEnabled(true)
        pnlControls.btnDelete.setEnabled(languageId != defaultLanguageId)
        pnlControls.btnSetDefault.setEnabled(languageId != defaultLanguageId)
      }
    }

    table addListener resetControls


    val pnlReloadBar = new Panel(new GridLayout(1,1))
    val btnReload = new Button("Reload")
    pnlReloadBar.addComponent(btnReload)

    btnReload addListener reloadTable

    pnlReloadBar.getContent.setSizeFull
    pnlReloadBar.getContent.asInstanceOf[GridLayout].setComponentAlignment(btnReload, Alignment.MIDDLE_RIGHT)

    reloadTable    
    resetControls

    new TabSheetView {
      addTab(new VerticalLayoutView("Languages") {
        addComponent(new GridLayout(1,3) {
          addComponents(this, pnlControls, table, pnlReloadBar)
        })
      })
    }
  }
  

  def documentsTable = new TabSheetView {
    addTab(new VerticalLayoutView("Documents") {
      val table = new Table()
      table.addContainerProperty("Page alias", classOf[String],  null)
      table.addContainerProperty("Status", classOf[String],  null)
      table.addContainerProperty("Type", classOf[JInteger],  null)
      table.addContainerProperty("Admin", classOf[String],  null)
      table.addContainerProperty("Ref.", classOf[String],  null)
      table.addContainerProperty("Child documents", classOf[String],  null)

      val metaDao = Imcms.getSpringBean("metaDao").asInstanceOf[MetaDao]

      metaDao.getAllDocumentIds.toList.foreach { id =>
        val meta = metaDao getMeta id
        val alias = meta.getProperties.get(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS) match {
          case null => ""
          case value => value
        }

        val status = meta.getPublicationStatus match {
          case Document.PublicationStatus.NEW => "New"
          case Document.PublicationStatus.APPROVED => "Approved"
          case Document.PublicationStatus.DISAPPROVED => "Disapproved"
        }

        table.addItem(Array(alias, status, meta.getDocumentType, id.toString, Int box 0, Int box 0), id)
      }

      val controls = new HorizontalLayout

      controls.addComponent(new Label("List between:"))
      controls.addComponent(new TextField)
      controls.addComponent(new Label("-"))
      controls.addComponent(new TextField)
      controls.addComponent(new Button("List"))

      addComponents(this, controls, table)
    })
  }
 
  def ipAccess = {
    def toDDN(internalFormat: String) = Utility.ipLongToString(internalFormat.toLong)
    def fromDDN(humanFormat: String) = Utility.ipStringToLong(humanFormat).toString

    class IPAccessWindow(caption: String) extends OkCancelDialog(caption) {
      val sltUser = new Select("Users")
      val txtFrom = new TextField("From")
      val txtTo = new TextField("To")

      val lytMainContent = new FormLayout

      lytMainContent.addComponent(sltUser)
      lytMainContent.addComponent(txtFrom)
      lytMainContent.addComponent(txtTo)

      setMainContent(lytMainContent)
    }
    
    class IPAccessView extends TableViewTemplate {
      lazy val btnAdd = new Button("Add")
      lazy val btnEdit = new Button("Edit")
      lazy val btnDelete = new Button("Delete")

      addComponents(pnlHeader, btnAdd, btnEdit, btnDelete)

      override def tableProperties = List(
        ("User", classOf[String],  null),
        ("IP range from", classOf[String],  null),
        ("IP range to", classOf[String],  null))

      override def tableItems() = ipAccessDao.getAll.toList map { ipAccess =>
        val user = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper getUser (Int unbox ipAccess.getUserId)
        ipAccess.getUserId -> List(user.getLoginName, toDDN(ipAccess.getStart), toDDN(ipAccess.getEnd))
      }

      override def resetComponents =
        if (tblItems.getValue == null) {
          btnDelete setEnabled false
          btnEdit setEnabled false
        } else {
          btnEdit setEnabled true
          btnDelete setEnabled true
        }

      btnAdd addListener {
        initAndShow(new IPAccessWindow("Add new IP Access")) { w =>
          Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllUsers foreach { u =>
            w.sltUser addItem u.getId
            w.sltUser setItemCaption (u.getId, u.getLoginName)
          }

          w.addOkButtonClickListener {
            val ipAccess = new IPAccess
            ipAccess setUserId w.sltUser.getValue.asInstanceOf[Integer]
            ipAccess setStart fromDDN(w.txtFrom.getValue.asInstanceOf[String])
            ipAccess setEnd fromDDN(w.txtTo.getValue.asInstanceOf[String])

            ipAccessDao.save(ipAccess)

            reloadTableItems
          }
        }
      }

      btnEdit addListener {
        initAndShow(new IPAccessWindow("Edit IP Access")) { w =>
          Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllUsers foreach { u =>
            w.sltUser addItem u.getId
            w.sltUser setItemCaption (u.getId, u.getLoginName)
          }

          val ipAccessId = tblItems.getValue.asInstanceOf[JInteger]
          val ipAccess = ipAccessDao get ipAccessId

          w.sltUser select ipAccess.getUserId
          w.txtFrom setValue toDDN(ipAccess.getStart)
          w.txtTo setValue toDDN(ipAccess.getEnd)

          w.addOkButtonClickListener {
            val ipAccess = new IPAccess
            ipAccess setUserId w.sltUser.getValue.asInstanceOf[Integer]
            ipAccess setStart fromDDN(w.txtFrom.getValue.asInstanceOf[String])
            ipAccess setEnd fromDDN(w.txtTo.getValue.asInstanceOf[String])

            ipAccessDao.save(ipAccess)

            reloadTableItems
          }
        }
      }

      btnDelete addListener {
        initAndShow(new ConfirmationDialog("Confirmation", "Delete IP Access?")) { w =>
          w.addOkButtonClickListener {
            ipAccessDao delete tblItems.getValue.asInstanceOf[JInteger]
            reloadTableItems
          }
        }
      }
    }

    new TabSheetView {
      addTab(new VerticalLayoutView("IP Access") {    
        addComponents(this,
          new Label("Users from a specific IP number or an intervall of numbers are given direct access to the system (so that the user does not have to log in)."),
          new IPAccessView)
      })
    }
  }


  lazy val roles = {
    def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

    class RoleDataWindow(caption: String) extends OkCancelDialog(caption) {
      val txtName = new TextField("Name")
      val chkPermGetPasswordByEmail = new CheckBox("Permission to get password by email")
      val chkPermAccessMyPages = new CheckBox("""Permission to access "My pages" """)
      val chkPermUseImagesFromArchive = new CheckBox("Permission to use images from image archive")
      val chkPermChangeImagesInArchive = new CheckBox("Permission to change images in image archive")

      val lytForm = new FormLayout {
        addComponents(this, txtName, chkPermGetPasswordByEmail, chkPermAccessMyPages, chkPermUseImagesFromArchive,
          chkPermChangeImagesInArchive)
      }

      val permsToChkBoxes = Map(
        RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION -> chkPermChangeImagesInArchive,
        RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION -> chkPermUseImagesFromArchive,
        RoleDomainObject.PASSWORD_MAIL_PERMISSION -> chkPermGetPasswordByEmail,
        RoleDomainObject.ADMIN_PAGES_PERMISSION -> chkPermAccessMyPages 
      )
      
      def checkedPermissions =
        for ((permission, chkBox) <- permsToChkBoxes if chkBox.getValue.asInstanceOf[Boolean]) yield permission

      def checkPermissions(permissions: Set[RolePermissionDomainObject]) =
        permissions foreach { p => permsToChkBoxes(p).setValue(true) }

      setMainContent(lytForm)
    }

    class RolesView extends TableViewTemplate {
      lazy val btnAdd = new Button("Add")
      lazy val btnEdit = new Button("Edit")
      lazy val btnDelete = new Button("Delete")

      addComponents(pnlHeader, btnAdd, btnEdit, btnDelete)

      override def tableProperties = List(
        ("Id", classOf[JInteger],  null),
        ("Name", classOf[String],  null))

      override def tableItems() =
        roleMapper.getAllRoles.toList map { role =>
          role.getId -> List(Int box role.getId.intValue, role.getName)
        }

      btnAdd addListener {
        initAndShow(new RoleDataWindow("New role")) { w =>
          w.addOkButtonClickListener {
            val role = new RoleDomainObject(w.txtName.getValue.asInstanceOf[String])
            w.checkedPermissions foreach { p => role.addPermission(p) }

            roleMapper saveRole role

            reloadTableItems
          }
        }
      }

      btnEdit addListener {
        initAndShow(new RoleDataWindow("Edit role")) { w =>
          val roleId = tblItems.getValue.asInstanceOf[RoleId]
          val role = roleMapper.getRole(roleId)

          w.txtName setValue role.getName
          w checkPermissions role.getPermissions.toSet

          w.addOkButtonClickListener {
            role.removeAllPermissions
            w.checkedPermissions foreach { p => role.addPermission(p) }
            roleMapper saveRole role
            reloadTableItems
          }
        }        
      }

      btnDelete addListener {
        initAndShow(new ConfirmationDialog("Confirmation", "Delete role?")) { w =>
          val roleId = tblItems.getValue.asInstanceOf[RoleId]
          val role = roleMapper.getRole(roleId)
          
          w.addOkButtonClickListener {
            roleMapper deleteRole role
            reloadTableItems
          }
        }
      }

      override def resetComponents =
        if (tblItems.getValue == null) {
          btnDelete setEnabled false
          btnEdit setEnabled false
        } else {
          btnEdit setEnabled true
          btnDelete setEnabled true
        }
    }

    new TabSheetView {
      addTab(new VerticalLayoutView("Roles and their permissions.") {
        addComponent(new RolesView)
      })
    }
  }

  //
  //
  //
  lazy val users = {
    def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

    class UsersView extends TableViewTemplate {
      override def tableProperties = List(
        ("Id", classOf[JInteger],  null),
        ("Login name", classOf[String],  null),
        ("Password", classOf[String],  null),
        ("Default user?", classOf[JBoolean],  null),
        ("Superadmin?", classOf[JBoolean],  null),
        ("Useradmin?", classOf[JBoolean],  null))

      override def tableItems() =
        roleMapper.getAllUsers.toList map { user =>
          val userId = Int box user.getId
          
          userId -> List(userId,
                         user.getLoginName,
                         user.getPassword,
                         Boolean box user.isDefaultUser,
                         Boolean box user.isSuperAdmin,
                         Boolean box user.isUserAdmin)          
        }

      val frmFilter = new Form {
        setCaption("Filter")
        val layout = new VerticalLayout
        setLayout(layout)        

        val txtFilter = new TextField("Login, first name, last name, title, email, company")
        val sltRoles = new ListSelect("Role(s)")
        val chkInactive = new CheckBox("Include inactive users")
        val btnClear = new Button("Clear")
        val lytFooter = new GridLayout(2, 1)

        setFooter(lytFooter)

        lytFooter addComponent chkInactive
        lytFooter addComponent btnClear

        lytFooter.setComponentAlignment(chkInactive, Alignment.MIDDLE_LEFT)
        lytFooter.setComponentAlignment(btnClear, Alignment.MIDDLE_RIGHT)

        layout addComponent txtFilter
        layout addComponent sltRoles
      }

      //val 

      //pnlHeader setContent lytFilter
      pnlFooter setContent new VerticalLayout { addComponent(frmFilter) }
    }

    new TabSheetView {
      addTab(new VerticalLayoutView("Users and their permissions.") {
        addComponent(new UsersView)
      })
    }
  }

  def settingsProperties = {
    val pnlStartPage = new Panel("Start page") {
      val txtNumber = new TextField("Number")

      addComponent(txtNumber)
    }

    val pnlSystemMessage = new Panel("System message") {
      val txtMessage = new TextField("Text")

      txtMessage.setRows(5)

      addComponent(txtMessage)
    }

    val pnlServerMaster = new Panel("Server master") {
      val txtName = new TextField("Name")
      val txtEmail = new TextField("Email")

      addComponents(this, txtName, txtEmail)
    }

    val pnlWebMaster = new Panel("Web master") {
      val txtName = new TextField("Name")
      val txtEmail = new TextField("Email")

      addComponents(this, txtName, txtEmail)
    }

    val lytButtons = new HorizontalLayout {
      val btnRevert = new Button("Revert")
      val btnSave = new Button("Save")

      setSpacing(true)

      addComponents(this, btnRevert, btnSave)
    }
    
    val lytContent = new VerticalLayout {
      setSpacing(true)
      setMargin(true)
    }

    addComponents(lytContent, pnlStartPage, pnlSystemMessage, pnlServerMaster, pnlWebMaster, lytButtons)

    def reload() {
      let(Imcms.getServices.getSystemData) { d =>
        pnlStartPage.txtNumber setValue d.getStartDocument.toString
        pnlSystemMessage.txtMessage setValue d.getSystemMessage
        pnlWebMaster.txtName setValue d.getWebMaster
        pnlWebMaster.txtEmail setValue d.getWebMasterAddress
        pnlServerMaster.txtName setValue d.getServerMaster
        pnlServerMaster.txtEmail setValue d.getServerMasterAddress        
      }
    }

    lytButtons.btnRevert addListener {
      reload() 
    }

    lytButtons.btnSave addListener {
      let(new SystemData) { d =>
        d setStartDocument pnlStartPage.txtNumber.getValue.asInstanceOf[String].toInt
        d setSystemMessage pnlSystemMessage.txtMessage.getValue.asInstanceOf[String]
        d setServerMaster pnlServerMaster.txtName.getValue.asInstanceOf[String]
        d setServerMasterAddress pnlServerMaster.txtEmail.getValue.asInstanceOf[String]
        d setWebMaster pnlWebMaster.txtName.getValue.asInstanceOf[String]
        d setWebMasterAddress pnlWebMaster.txtEmail.getValue.asInstanceOf[String]

        Imcms.getServices.setSystemData(d)
      }
    }

    reload()

    new TabSheetView {
      addTab(new VerticalLayoutView("System Properties") {
        addComponent(lytContent)
      })
    }   
  }

  def settingSessionCounter = new TabSheetView {
    addTab(new VerticalLayoutView("Session counter") { self =>
      setSpacing(false)
      
      val lytData = new FormLayout {
        val txtValue = new TextField("Value:")
        val calStart = new DateField("Start date:")
        calStart.setResolution(DateField.RESOLUTION_DAY)

        txtValue.setReadOnly(true)
        calStart.setReadOnly(true)

        addComponents(this, txtValue, calStart)
      }

      val lytButtons = new HorizontalLayout {
        val btnReload = new Button("Reload")
        val btnClear = new Button ("Clear")
        val btnEdit = new Button("Edit")
        setSpacing(true)

        addComponents(this, btnEdit, btnClear, btnReload)
      }

      addComponents(this, lytData, lytButtons)

      def reload() {
        // ?!?! when read only throws exception ?!?!
        lytData.txtValue setReadOnly false
        lytData.calStart setReadOnly false

        lytData.txtValue setValue Imcms.getServices.getSessionCounter.toString
        lytData.calStart setValue Imcms.getServices.getSessionCounterDate

        lytData.txtValue setReadOnly true
        lytData.calStart setReadOnly true         
      }

      lytButtons.btnReload addListener reload()
      lytButtons.btnClear addListener {
        initAndShow(new ConfirmationDialog("Confirmation", "Clear counter statistics?")) { w =>
          w.addOkButtonClickListener {
            Imcms.getServices setSessionCounter 0
            Imcms.getServices setSessionCounterDate new Date

            reload()
          }          
        }
      }

      lytButtons.btnEdit addListener {
        initAndShow(new OkCancelDialog("Edit session counter")) { w =>
          val txtValue = new TextField("Value")
          val calStart = new DateField("Start date")

          calStart.setResolution(DateField.RESOLUTION_DAY)
          txtValue setValue Imcms.getServices.getSessionCounter.toString
          calStart setValue Imcms.getServices.getSessionCounterDate

          w.setMainContent(new FormLayout {
            addComponents(this, txtValue, calStart)
          })

          w.addOkButtonClickListener {
            Imcms.getServices setSessionCounter txtValue.getValue.asInstanceOf[String].toInt
            Imcms.getServices setSessionCounterDate calStart.getValue.asInstanceOf[Date]

            reload()
          }

          reload() // enshure form and dialog values are same
        }
      }

      reload()
    })
  }

  def categories = new TabSheetView {
    addTab(new VerticalLayoutView("Category") {
      addComponent(new TableViewTemplate {

        override def tableProperties =
          ("Id", classOf[JInteger],  null) ::
          ("Name", classOf[String],  null) ::
          ("Description", classOf[String],  null) ::
          ("Icon", classOf[String],  null) ::
          ("Type", classOf[String],  null) ::
          Nil
      })
    })

    addTab(new VerticalLayoutView("Category type") {
      addComponent(new TableViewTemplate {
        override def tableProperties = List(
          ("Id", classOf[JInteger],  null),
          ("Name", classOf[String],  null),
          ("Multi select?", classOf[JBoolean],  null),
          ("Inherited to new documents?", classOf[JBoolean],  null),
          ("Used by image archive?", classOf[JBoolean],  null))

  //            ("Id", classOf[JInteger],  null) ::
  //            ("Name", classOf[String],  null) ::
  //            ("Multi select?", classOf[JBoolean],  null) ::
  //            ("Inherited to new documents?", classOf[JBoolean],  null) ::
  //            ("Used by image archive?", classOf[JBoolean],  null) ::
  //            Nil
  //
  //          override def tableProperties =
  //            ("Id", classOf[JInteger],  null) ::
  //            ("Name", classOf[String],  null) ::
  //            ("Multi select?", classOf[JBoolean],  null) ::
  //            ("Inherited to new documents?", classOf[JBoolean],  null) ::
  //            ("Used by image archive?", classOf[JBoolean],  null) ::
  //            Nil
      })
    })
  }



  lazy val searchTerms = new TabSheetView {
    addTab(new VerticalLayoutView("Popular search terms") {
      val tblTerms = new Table {
        addContainerProperties(this, ("Term", classOf[String], null), ("Count", classOf[String], null))
        setPageLength(10)
      }

      val lytBar = new HorizontalLayout {
        setSpacing(true)
        setCaption("Date range")
        val calFrom = new DateField()
        val calTo = new DateField()
        val btnReload = new Button("Reload")

        calFrom.setValue(new Date)
        //calFrom.setStyle("calendar")
        calFrom.setResolution(DateField.RESOLUTION_DAY)

        calTo.setValue(new Date)
        //calTo.setStyle("calendar")
        calTo.setResolution(DateField.RESOLUTION_DAY)

        addComponents(this, calFrom, calTo, btnReload)
      }

      addComponents(this, tblTerms, lytBar)

      def reload() {
        val terms = AdminSearchTerms.getTermCounts(lytBar.calFrom.getValue.asInstanceOf[Date],
          lytBar.calTo.getValue.asInstanceOf[Date])

        tblTerms.removeAllItems
        terms foreach { t =>
          val item = Array[AnyRef](t.getTerm, t.getCount.toString)
          tblTerms.addItem(item, item)
        }
      }

      lytBar.btnReload addListener reload()

      reload()
    })
  }

  class FileBrowser(val root: File) extends Tree {
    setImmediate(true)
    setSelectable(true)

    addListener(new Tree.ExpandListener {
      def nodeExpand(e: Tree#ExpandEvent) = e.getItemId match {
        case dir: File => dir.listFiles foreach (addItem(_, dir))
      }
    })

    addListener(new Tree.CollapseListener {
      def nodeCollapse(e: Tree#CollapseEvent) = getChildren(e.getItemId) match {
        case null =>
        case children => children foreach (removeItem(_))
      }
    })

    def addItem(fsNode: File) {
      super.addItem(fsNode)
      setItemCaption(fsNode, (if (fsNode.isDirectory) "/" else "") + fsNode.getName)
      setChildrenAllowed(fsNode, fsNode.isDirectory)
    }

    def addItem(fsNode: File, parentDir: File) {
      addItem(fsNode)
      setParent(fsNode, parentDir)
    }

    addItem(root)
    expandItem(root)    
  }


//  lazy val documentsLinks = new TabSheetView {
//    addTab(new VerticalLayoutView("Validate links"))
//  }
//
//  lazy val documentsProfiles = new TabSheetView {
//    addTab(new VerticalLayoutView("Profiles"))
//  }
//
//  lazy val documentsTemplates = new TabSheetView {
//    addTab(new VerticalLayoutView("Templates"))
//  }
  
  lazy val filesystem = new TabSheetView {
    addTab(new VerticalLayoutView("File manager") {
      val lytButtons = new HorizontalLayout {
        setSpacing(true)

        val btnReload = new Button("Reload")
        val btnView = new Button("View")
        val btnEdit = new Button("Edit")
        val btnCopy = new Button("Copy")
        val btnMove = new Button("Move")
        val btnDelete = new Button("Delete")

        val btnDownload = new Button("Download")
        val btnUpload = new Button("Upload")

        addComponents(this, btnReload, new Label("|"), btnView, btnEdit, new Label("|"), btnCopy, btnMove, btnDelete, new Label("|"), btnDownload, btnUpload)
      }

      addComponents(this, lytButtons, new FileBrowser(Imcms.getPath))
    })
  }

  class TemplateGroupWindow(caption: String) extends OkCancelDialog(caption) {
    val txtId = new TextField("Id")
    val txtName = new TextField("Name")
    txtId.setEnabled(false)

    val twsTemplates = new TwinSelect("Templates")

    setMainContent(new FormLayout {
      addComponents(this, txtId, txtName, twsTemplates)
    })
  }




  class TemplateView extends FormLayout {
    //val txtName = new
  }


  lazy val templates = new TabSheetView {
    val templateMapper = Imcms.getServices.getTemplateMapper

    // templates tab
    addTab(new VerticalLayoutView("Templates") {
      addComponent(new TableViewTemplate {
        override def tableProperties() =
          ("Name", classOf[String], null) ::
          ("Filename", classOf[String], null) ::
          ("Documents count", classOf[JInteger], null) ::
          Nil

        override def tableItems() = templateMapper.getAllTemplates map { t =>
          (t.getName, List(t.getName, t.getFileName, Int box templateMapper.getCountOfDocumentsUsingTemplate(t)))
        }

        val btnNew = new Button("New")
        val btnEdit = new Button("Edit")
        val btnDelete = new Button("Delete")
        val btnEditContent = new Button("Edit content")
        val btnDownload = new Button("Donwload")
        val btnUploadDemo = new Button("!?! Upload demo template !?!")

        addComponents(pnlHeader, btnNew, btnEdit, btnDelete, new Label(" "), btnEditContent, btnDownload)        

        btnNew addListener {
          initAndShow(new OkCancelDialog("Add new template")) { w =>
            w.btnOk.setEnabled(false)      

            val txtName = new TextField("Name")
            val lstGroups = new ListSelect("Groups") {
              setMultiSelect(true)
            }
            val uplFile = new Upload("Template file", new FileUploadReceiver("/tmp/upload")) with UploadEventHandler {
              def handleEvent(e: com.vaadin.ui.Component.Event) = e match {
                case e: Upload#SucceededEvent =>
                  w.btnOk.setEnabled(true)
                  txtName setValue e.getFilename
                case e: Upload#FailedEvent =>
                  w.btnOk.setEnabled(false)
                  
                case _ => // not interested
              }
            }


            w setMainContent new FormLayout {
              addComponents(this, txtName, lstGroups, uplFile)
            }

            templateMapper.getAllTemplateGroups foreach (lstGroups.addItem(_.getName))

            w.addOkButtonClickListener {
              println("SAving...")
            }
          }
        }
      }) // templates table view
    }) // templates tab


    // templates groups
    addTab(new VerticalLayoutView("Template group") {
      addComponent(new TableViewTemplate {
        override def tableProperties() =
          ("Id", classOf[JInteger], null) ::
          ("Name", classOf[String], null) ::
          ("Templates count", classOf[JInteger], null) ::
          Nil
        
        override def tableItems() = templateMapper.getAllTemplateGroups map { g =>
          (Int box g.getId, List(Int box g.getId, g.getName, Int box templateMapper.getTemplatesInGroup(g).length))
        }
        
        lazy val btnNew = new Button("New")
        lazy val btnEdit = new Button("Edit")
        lazy val btnDelete = new Button("Delete")

        override def resetComponents() {
          forlet(btnEdit, btnDelete) { b =>
            b.setEnabled(tblItems.getValue != null)
          }
        }

        addComponents(pnlHeader, btnNew, btnEdit, btnDelete)

        btnNew addListener {
          initAndShow(new TemplateGroupWindow("New group")) { w =>
            templateMapper.getAllTemplates foreach (w.twsTemplates.lstAvailable addItem _.getName)
            
            w.addOkButtonClickListener {
              templateMapper.createTemplateGroup(w.txtName.getValue.asInstanceOf[String])
              // addTemplates
              reloadTableItems 
            }
          }
        } // btnNew handler

        btnEdit addListener  {
          initAndShow(new TemplateGroupWindow("Edit group")) { w =>
            let(tblItems.getValue) {
              case null =>
              case id: JInteger =>
                let(templateMapper getTemplateGroupById id.intValue) { g =>
                  templateMapper.getTemplatesInGroup(g) foreach (w.twsTemplates.lstChosen addItem _.getName)
                  templateMapper.getTemplatesNotInGroup(g) foreach (w.twsTemplates.lstAvailable addItem _.getName)

                  w.txtId setValue id
                  w.txtName setValue templateMapper.getTemplateGroupById(id.intValue).getName  

                  w.addOkButtonClickListener {
                    templateMapper.renameTemplateGroup(g, w.txtName.getValue.asInstanceOf[String])
                    reloadTableItems
                  }
                }
            } // let
          }
        } // btnEdit handler
        
        btnDelete addListener {
          initAndShow(new ConfirmationDialog("Confirmation", "Detelete template group?")) { w =>
            w.addOkButtonClickListener {
              templateMapper deleteTemplateGroup tblItems.getValue.asInstanceOf[Int]
              reloadTableItems
            }
          }          
        } // btnDelete handler
      })  
    })
  }
}


