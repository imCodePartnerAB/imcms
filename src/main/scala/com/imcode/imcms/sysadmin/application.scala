package com.imcode.imcms.sysadmin

import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger}
import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.api._
import imcms.docadmin.{DocFlowFactory, MetaMVC}
import imcms.mapping.CategoryMapper
import imcms.servlet.superadmin.AdminSearchTerms

import imcms.sysadmin.chat.{MessageView, Chat}
import imcms.sysadmin.filemanager.{FileBrowser, FileBrowserWithImagePreview, IconImagePicker}
import imcms.sysadmin.permissions.UsersView
import imcms.sysadmin.template.{TemplateGroupDialogContent, EditTemplateContentDialogContent, EditTemplateDialogContent, TemplateDialogContent}
import imcode.util.Utility
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.vaadin.ui.Layout.MarginInfo
import scala.actors.Actor._
import scala.actors._
import imcode.server.document.textdocument.TextDocumentDomainObject
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}
import imcode.server.document._
import com.imcode.imcms.vaadin._;
import com.imcode.imcms.vaadin.AbstractFieldWrapper._;


object ChatTopic extends Actor {

  case class Subscribe(subscriber: Actor)
  case class Message(text: String)

  var subscribers: Set[Actor] = Set.empty

  def act {
    loop {
      react {
        case Subscribe(subscriber) =>
          subscribers += subscriber // send 10 last messages?
        case msg : Message => subscribers foreach (_ ! msg) 
        case other => println("Unknown message: " + other)
      }
    }
  }

  start()
}


class Application extends com.vaadin.Application with VaadinApplication { application =>

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
      object Structure extends MenuItem(this)
      object New extends MenuItem(this)
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
    object System extends MenuItem(this) {
      object Cache extends MenuItem(this)  
    }
  } 

  val languageDao = Imcms.getSpringBean("languageDao").asInstanceOf[LanguageDao]
  val systemDao = Imcms.getSpringBean("systemDao").asInstanceOf[SystemDao]
  val ipAccessDao = Imcms.getSpringBean("ipAccessDao").asInstanceOf[IPAccessDao]  

  
  abstract class TableViewTemplate extends VerticalLayout {
    val tblItems = new Table {
      setSelectable(true)
      setImmediate(true)
      setPageLength(10)

      addListener(unit { resetComponents })

      tableProperties foreach (addContainerProperties(this, _))
    }

    val btnReload = new Button("Reload") {
      addListener(unit { reloadTableItems })
      setStyleName(Button.STYLE_LINK)
      setIcon(new ThemeResource("icons/16/reload.png"))
    }
    
    // todo - refactor into lytMenu
    val pnlHeader = new HorizontalLayout {
      setWidth("100%")
      setSpacing(true)
    }

    val lytHeader = new HorizontalLayout {
      setWidth("100%")
      setSpacing(true)
    }
    
    val lytMenu = pnlHeader

    addComponents(lytHeader, lytMenu, btnReload)
    lytHeader.setExpandRatio(lytMenu, 1.0f)

    setSpacing(true)
    addComponents(this, lytHeader, tblItems)

    reloadTableItems
    resetComponents
    
    // Investigate: List[(AnyRef, Array[AnyRef])]
    def tableItems(): Seq[(AnyRef, Seq[AnyRef])] = List.empty

    def tableProperties: Seq[(AnyRef, JClass[_], AnyRef)] = List.empty

    def reloadTableItems {
      tblItems.removeAllItems

      for((id, cells) <- tableItems()) tblItems.addItem(cells.toArray, id)
      //for ((id:, cells:) <- tableItems()) tblItems.addItem(cells, id)
    }

    def resetComponents = {}
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
    val content = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL) {
      setSplitPosition(15)
      setSizeFull
    }
    
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
            case Menu.Documents.Structure => docStructure
            case Menu.Documents.New => newDocument
            case Menu.System.Cache => systemCacheView 

            case other => NA(other)
          })
      }
    })

    content setFirstComponent treeMenu
    //this setContent content

    val splitView = new SplitPanel {
      setFirstComponent(content)
      setSecondComponent(chat)
      setSplitPosition(85)
    }

    this setContent splitView
  }

  def init {
    wndMain initMenu Menu
    Menu.items foreach { wndMain.treeMenu expandItemsRecursively _ }
    wndMain.treeMenu select Menu.About
    this setMainWindow wndMain    
  }

  def newDocument = {
    import com.imcode.imcms.docadmin.{MetaMVC, FlowUI, DocFlowFactory}

    val flowFactory = new DocFlowFactory(this)
    val parentDoc = Imcms.getServices.getDocumentMapper.getDocument(1001)
    val flow = flowFactory.newDocFlow(DocumentTypeDomainObject.TEXT, parentDoc)

    flow
//
//    val mvc = new MetaMVC(
//      application,
//      doc,
//      Imcms.getI18nSupport.getLanguages map ((_, true)) toMap,
//      Imcms.getSpringBean("metaDao").asInstanceOf[MetaDao].getLabels(1001) map (l => (l.getLanguage, l)) toMap)

//    mvc.view
  }

//  def systemCacheView = {
//    import com.imcode.imcms.sysadmin.cache.View
//
//    new cache.View(Imcms.getDocumentMapper.getCahcingDocumentGetter)
//  }
  def systemCacheView = new cache.View(Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy)

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

      mainContent = new FormLayout {
        addComponents(this, txtId, txtCode, txtName, txtNativeName, chkEnabled)  
      }
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

    table addListener unit { resetControls }


    val pnlReloadBar = new Panel(new GridLayout(1,1))
    val btnReload = new Button("Reload")
    pnlReloadBar.addComponent(btnReload)

    btnReload addListener unit { reloadTable }

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

      mainContent = lytMainContent
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

      btnAdd addListener unit {
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

      btnEdit addListener unit {
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

      btnDelete addListener unit {
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

      mainContent = lytForm
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

      btnAdd addListener unit {
        initAndShow(new RoleDataWindow("New role")) { w =>
          w.addOkButtonClickListener {
            val role = new RoleDomainObject(w.txtName.getValue.asInstanceOf[String])
            w.checkedPermissions foreach { p => role.addPermission(p) }

            roleMapper saveRole role

            reloadTableItems
          }
        }
      }

      btnEdit addListener unit {
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

      btnDelete addListener unit {
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

    lytButtons.btnRevert addListener unit {
      reload() 
    }

    lytButtons.btnSave addListener unit {
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

      lytButtons.btnReload addListener unit { reload() }
      lytButtons.btnClear addListener unit {
        initAndShow(new ConfirmationDialog("Confirmation", "Clear counter statistics?")) { w =>
          w.addOkButtonClickListener {
            Imcms.getServices setSessionCounter 0
            Imcms.getServices setSessionCounterDate new Date

            reload()
          }          
        }
      }

      lytButtons.btnEdit addListener unit {
        initAndShow(new OkCancelDialog("Edit session counter")) { w =>
          val txtValue = new TextField("Value")
          val calStart = new DateField("Start date")

          calStart.setResolution(DateField.RESOLUTION_DAY)
          txtValue setValue Imcms.getServices.getSessionCounter.toString
          calStart setValue Imcms.getServices.getSessionCounterDate

          w.mainContent = new FormLayout {
            addComponents(this, txtValue, calStart)
          }

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

      lytBar.btnReload addListener unit { reload() }

      reload()
    })
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
    tabSheet.setSizeFull
    setSizeFull
    addTab(new VerticalLayoutView("File manager") {
      setSizeFull
      val lytButtons = new HorizontalLayout {
        setSpacing(true)
 
        val btnReload = new Button("Reload")
        val btnView = new Button("View")
        val btnEdit = new Button("Edit")
        val btnCopy = new Button("Copy to..")
        val btnMove = new Button("Move to..")
        val btnDelete = new Button("Delete")

        val btnDownload = new Button("Download")
        val btnUpload = new Button("Upload")

        addComponents(this, btnReload, new Label("|"), btnCopy, btnMove, btnDelete, new Label("|"), btnView, btnEdit, new Label("|"), btnDownload, btnUpload)
      }

      val fileBrowser = new FileBrowser {
        addDirectoryTree("Home", Imcms.getPath)
        addDirectoryTree("Templates", new File(Imcms.getPath, "WEB-INF/templates/text"))
        addDirectoryTree("Images", new File(Imcms.getPath, "images"))
        addDirectoryTree("Conf", new File(Imcms.getPath, "WEB-INF/conf"))
        addDirectoryTree("Logs", new File(Imcms.getPath, "WEB-INF/logs"))

        tblDirContent.setSelectable(true)
        tblDirContent.setMultiSelect(true)
      }

      addComponents(this, lytButtons, fileBrowser)

      setExpandRatio(fileBrowser, 1.0f)

      lytButtons.btnReload addListener unit { fileBrowser.reload()}
      lytButtons.btnCopy addListener unit {
        initAndShow(new OkCancelDialog("Copy to - choose destination directory")
            with CustomSizeDialog with BottomMarginOnlyDialog, resizable = true) { w =>
          let(w.mainContent = new FileBrowser) { b =>
            b setSplitPosition 30
            b addDirectoryTree("Home", Imcms.getPath)
            b addDirectoryTree("Templates", new File(Imcms.getPath, "WEB-INF/templates/text"))
            b addDirectoryTree("Images", new File(Imcms.getPath, "images"))
            b addDirectoryTree("Conf", new File(Imcms.getPath, "WEB-INF/conf"))
            b addDirectoryTree("Logs", new File(Imcms.getPath, "WEB-INF/logs"))
          }

          w setWidth "600px"
          w setHeight "400px"
        }
      }
    })
  }

  def templates = new TabSheetView {
    val templateMapper = Imcms.getServices.getTemplateMapper

    // templates tab
    addTab(new VerticalLayoutView("Templates") {
      addComponent(new TableViewTemplate {
        override def tableProperties() =
          ("Name", classOf[String], null) ::
          ("Kind", classOf[String], null) ::
          ("Documents count using template", classOf[JInteger], null) ::
          Nil

        override def tableItems = templateMapper.getAllTemplates map { t =>
          val kind = let(t.getFileName) { filename =>
            filename.lastIndexOf(".") match {
              case -1 => ""
              case n if (n + 1) == filename.length => ""
              case n => filename.substring(n + 1)
            }
          }

          (t.getName, List(t.getName, kind, Int box templateMapper.getCountOfDocumentsUsingTemplate(t)))
        }

//        val btnNew = new Button("Add new")
//        val btnRename = new Button("Rename")
//        val btnDelete = new Button("Delete")
//        val btnEditContent = new Button("Edit content")

        val menuBar = new MenuBar
        val miAddNew = menuBar.addItem("Add new", new ThemeResource("icons/16/document-add.png"), null)
        val miRename = menuBar.addItem("Edit", new ThemeResource("icons/16/settings.png"), null)
        val miDelete = menuBar.addItem("Delete", new ThemeResource("icons/16/document-delete.png"), null)
        val miEditContent = menuBar.addItem("Edit content", new ThemeResource("icons/16/document-txt.png"), null)

        //addComponents(pnlHeader, btnNew, btnRename, btnEditContent, new Label(" "), btnDelete)
        //pnlHeader.addComponent(menuBar)
        lytMenu.addComponent(menuBar)

        //btnNew addListener {
        miAddNew setCommand unit {
          initAndShow(new OkCancelDialog("Add new template")) { w =>
            let(w.mainContent = new TemplateDialogContent) { c =>
              w addOkButtonClickListener {
                c.uploadReceiver.uploadRef.get match {
                  case Some(upload) =>
                    val in = new ByteArrayInputStream(upload.content)
                    val result = templateMapper.saveTemplate(c.txtName.stringValue,
                        upload.filename, in, c.chkOverwriteExisting.booleanValue)

                    result match {
                      case 0 => reloadTableItems // ok
                      case -1 => error("File exists") // file exists
                      case -2 => error("IO error")  // io error
                      case n => error("Unknown error: " + n)
                    }

                  case _ =>
                }
              }
            }
          }
        } // btnNew

        //btnRename addListener {
        miRename setCommand unit {
          tblItems getValue match {
            case name: String =>
              initAndShow(new OkCancelDialog("Edit template")) { w =>
                let(w.mainContent = new EditTemplateDialogContent) { c =>
                  c.txtName setValue name      
                  w addOkButtonClickListener {
                    templateMapper.renameTemplate(name, c.txtName.stringValue)
                    reloadTableItems
                  }
                }
              }

            case _ =>
          }
        } // btnRename

        //btnDelete addListener {
        miDelete setCommand unit {
          tblItems getValue match {
            case name: String =>
              initAndShow(new ConfirmationDialog("Delete selected template?")) { w =>
                w addOkButtonClickListener {
                  templateMapper deleteTemplate templateMapper.getTemplateByName(name)
                  reloadTableItems
                }
              }

            case _ =>
          }          
        } // btnDelete

        //btnEditContent addListener {
        miEditContent setCommand unit {
          tblItems getValue match {
            case name: String =>
              initAndShow(new OkCancelDialog("Edit template content")
                      with CustomSizeDialog with BottomMarginOnlyDialog) { w =>
                let(w.mainContent = new EditTemplateContentDialogContent) { c =>
                  val file = new File(Imcms.getServices.getConfig.getTemplatePath,
                                      "text/" + templateMapper.getTemplateByName(name).getFileName)
                  
                  c.txtContent setValue scala.io.Source.fromFile(file).mkString
                  w addOkButtonClickListener {
                    // save content
                  }
                }

                w setWidth "600px"
                w setHeight "800px"
              }

            case _ =>
          }
        } // btnEditContent

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
        
//        lazy val btnNew = new Button("New")
//        lazy val btnEdit = new Button("Edit")
//        lazy val btnDelete = new Button("Delete")

        lazy val menuBar = new MenuBar
        lazy val miAddNew = menuBar.addItem("Add new", new ThemeResource("icons/16/document-add.png"), null)
        lazy val miEdit = menuBar.addItem("Edit", new ThemeResource("icons/16/settings.png"), null)
        lazy val miDelete = menuBar.addItem("Delete", new ThemeResource("icons/16/document-delete.png"), null)        

        override def resetComponents() {
          forlet(miEdit, miDelete) { b =>
            b.setEnabled(tblItems.getValue != null)
          }
        }

        lytMenu.addComponent(menuBar)
        //addComponents(pnlHeader, btnNew, btnEdit, btnDelete)
//        replaceComponent(pnlHeader, menuBar)

        //btnNew addListener unit {
        miAddNew setCommand unit {
          initAndShow(new OkCancelDialog("New group")) { w =>
            let(w.setMainContent(new TemplateGroupDialogContent)) { c =>
              templateMapper.getAllTemplates foreach (c.twsTemplates addAvailableItem _.getName)

              w.addOkButtonClickListener {
                templateMapper.createTemplateGroup(c.txtName.stringValue)
                val group = templateMapper.getTemplateGroupByName(c.txtName.stringValue)
                c.twsTemplates.chosenItemIds foreach { name =>
                  templateMapper.getTemplateByName(name) match {
                    case null =>
                    case t => templateMapper.addTemplateToGroup(t, group)
                  }
                }
                
                reloadTableItems
              }
            }
          }
        } // btnNew handler

        //btnEdit addListener unit {
        miEdit setCommand unit {
          initAndShow(new OkCancelDialog("Edit group")) { w =>
            let(w.setMainContent(new TemplateGroupDialogContent)) { c =>
              let(tblItems.getValue) {
                case null =>
                case id: JInteger =>
                  let(templateMapper getTemplateGroupById id.intValue) { g =>
                    templateMapper.getTemplatesInGroup(g) foreach (c.twsTemplates addChosenItem _.getName)
                    templateMapper.getTemplatesNotInGroup(g) foreach (c.twsTemplates addAvailableItem _.getName)

                    c.txtId setValue id
                    c.txtName setValue templateMapper.getTemplateGroupById(id.intValue).getName

                    w.addOkButtonClickListener {
                      templateMapper.renameTemplateGroup(g, c.txtName.stringValue)
                      templateMapper.getTemplatesInGroup(g) foreach { t =>
                        templateMapper.removeTemplateFromGroup(t, g)
                      }
                                          
                      c.twsTemplates.chosenItemIds foreach { name =>
                        templateMapper.getTemplateByName(name) match {
                          case null =>
                          case t => templateMapper.addTemplateToGroup(t, g)
                        }
                      }

                      reloadTableItems
                    }
                  }
              } // let
            }
          }
        } // btnEdit handler
        
        //btnDelete addListener unit {
        miDelete setCommand unit {
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



  
  // CategoryDialogContent
  trait CategoryDialog { this: OkCancelDialog =>
    val txtId = new TextField("Id") {
      setEnabled(false)
      setColumns(11)
    }
    val txtName = new TextField("Name")
    val txtDescription = new TextField("Description") {
      setRows(5)
      setColumns(11)
    }
    val sltType = new Select("Type") {
      setNullSelectionAllowed(false)
    }
    val embIcon = new IconImagePicker(50, 50) {
      setCaption("Icon")

      btnChoose addListener unit {
        initAndShow(new OkCancelDialog("Select icon image - .gif  .png  .jpg  .jpeg")
                with CustomSizeDialog with BottomMarginOnlyDialog, resizable = true) { w =>
                
          let(w.mainContent = new FileBrowserWithImagePreview(100, 100)) { b =>
            b.browser setSplitPosition 30
            b.browser addDirectoryTree("Images", new File(Imcms.getPath, "images"))
            b.browser.tblDirContent setSelectable true

            w.addOkButtonClickListener {
              b.preview.image match {
                case Some(source) => showImage(source)
                case _ => showStub
              }
            }
          }

          w setWidth "650px"
          w setHeight "350px"
        }
      }
    }

    mainContent = new HorizontalLayout {
      addComponents(this, new FormLayout { addComponents(this, txtId, txtName, sltType, embIcon, txtDescription) })
      setSizeUndefined
    }
  }


  def categories = new TabSheetView {
    val categoryMapper = Imcms.getServices.getCategoryMapper

    addTab(new VerticalLayoutView("Category") {
      addComponent(new TableViewTemplate {

        override def tableProperties =
          ("Id", classOf[JInteger],  null) ::
          ("Name", classOf[String],  null) ::
          ("Description", classOf[String],  null) ::
          ("Icon", classOf[String],  null) ::
          ("Type", classOf[String],  null) ::
          Nil

        override def tableItems(): Seq[(AnyRef, Seq[AnyRef])] = categoryMapper.getAllCategories map { c =>
          (Int box c.getId, Seq(Int box c.getId, c.getName, c.getDescription, c.getImageUrl, c.getType.getName))
        }

        val btnAdd = new Button("Add")
        val btnEdit = new Button("Edit")
        val btnDelete = new Button("Delete")

        addComponents(pnlHeader, btnAdd, btnEdit, btnDelete)

        btnAdd addListener unit {
          initAndShow(new OkCancelDialog("New category") with CategoryDialog) { w =>
            categoryMapper.getAllCategoryTypes foreach { c =>
              w.sltType addItem c.getName
            }

            w addOkButtonClickListener {
              let(new CategoryDomainObject) { c =>
                c setName w.txtName.getValue.asInstanceOf[String]
                c setDescription w.txtDescription.getValue.asInstanceOf[String]
                c setImageUrl ""//embIcon
                c setType categoryMapper.getCategoryTypeByName(w.sltType.getValue.asInstanceOf[String])

                categoryMapper saveCategory c
                reloadTableItems
              }
            }
          }
        }

        btnEdit addListener unit {
          tblItems.getValue match {
            case id: JInteger =>
              categoryMapper.getCategoryById(id.intValue) match {
                case null => error("No such category")
                case category =>
                  initAndShow(new OkCancelDialog("Edit category") with CategoryDialog) { w =>
                    categoryMapper.getAllCategoryTypes foreach { c =>
                      w.sltType addItem c.getName
                    }

                    w.txtId setValue id
                    w.txtName setValue category.getName
                    w.txtDescription setValue category.getDescription

                    w addOkButtonClickListener {
                      category setName w.txtName.stringValue
                      category setDescription w.txtDescription.stringValue
                      category setType categoryMapper.getCategoryTypeByName(w.sltType.stringValue)

                      categoryMapper saveCategory category
                      reloadTableItems
                    }
                  } // initAndShow
              }

            case _ =>
          }
        }

        btnDelete addListener unit {
          initAndShow(new ConfirmationDialog("Delete category")) { w =>
            w addOkButtonClickListener {
              tblItems.getValue match {
                case null =>
                case id: JInteger =>
                  let(categoryMapper getCategoryById id.intValue) { c =>
                    categoryMapper deleteCategoryFromDb c   
                  }
                  reloadTableItems
              }
            }
          }
        }
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
//        override def tableProperties =
//          ("Id", classOf[JInteger],  null) ::
//          ("Name", classOf[String],  null) ::
//          ("Multi select?", classOf[JBoolean],  null) ::
//          ("Inherited to new documents?", classOf[JBoolean],  null) ::
//          ("Used by image archive?", classOf[JBoolean],  null) ::
//          Nil

        override def tableItems(): Seq[(AnyRef, Seq[AnyRef])] = categoryMapper.getAllCategoryTypes map { t =>
          (Int box t.getId, Seq(Int box t.getId, t.getName, Boolean box (t.getMaxChoices > 0), Boolean box t.isInherited, Boolean box t.isImageArchive))
        }

        val btnNew = new Button("New")
        val btnEdit = new Button("Edit")
        val btnDelete = new Button("Delete")

        addComponents(pnlHeader, btnNew, btnEdit, btnDelete)

        btnNew addListener unit {
          initAndShow(new OkCancelDialog("New categor type")) { w =>
            val txtId = new TextField("Id")
            val txtName = new TextField("Name")
            val chkMultiSelect = new CheckBox("Multiselect")
            val chkInherited = new CheckBox("Inherited to new documents")
            val chkImageArchive = new CheckBox("Used by image archive")
            
            w.mainContent = new FormLayout {
              addComponents(this, txtId, txtName, chkMultiSelect, chkInherited, chkImageArchive)

              w.addOkButtonClickListener {
                let(new CategoryTypeDomainObject(0,
                    txtName.getValue.asInstanceOf[String],
                    if (chkMultiSelect.booleanValue) 1 else 0,
                    chkInherited.booleanValue)) {
                  categoryType =>
                  categoryType.setImageArchive(chkImageArchive.booleanValue)

                  categoryMapper addCategoryTypeToDb categoryType
                }

                reloadTableItems
              }
            }
          }
        }

        btnDelete addListener unit {
          tblItems.getValue match {
            case null =>
            case id: JInteger =>
              initAndShow(new ConfirmationDialog("Delete category type")) { w =>
                w.addOkButtonClickListener {
                  val categoryType = categoryMapper getCategoryTypeById id.intValue

                  categoryMapper deleteCategoryTypeFromDb  categoryType
                  reloadTableItems
                }
              }
          }
        }
      })
    })
  } // category

  //
  // Chat
  //
  lazy val chat = new Chat {
    setCaption("Chat messages")
      setMargin(true)
      val subscriber = actor {
        loop {
          react {
            case ChatTopic.Message(text) =>
              pnlMessages addMessage new MessageView("#user#", text)
              pnlMessages.requestRepaint
            case _ =>
          }
        }
      }

      btnSend addListener unit {
        ChatTopic ! ChatTopic.Message(txtText.getValue.asInstanceOf[String])
        txtText setValue ""
      }
      ChatTopic ! ChatTopic.Subscribe(subscriber)
    } //chat


  //
  // Users
  //
  lazy val users = {
    new TabSheetView {
      addTab(new VerticalLayoutView("Users and their permissions.") {
        addComponent(new UsersView(application))
      })
    }
  }

  //
  //
  //
  def docStructure = new TabSheetView {
    addTab(new VerticalLayoutView("Document structure outline") {
      val lytMenu = new HorizontalLayout {
        setSpacing(true)
        val txtId = new TextField("Text doc (meta) id")
        val btnShow = new Button("Show")

        addComponents(this, txtId, btnShow)
      }
      val lytStructure = new VerticalLayout {
        setSpacing(true)
      }

      lytMenu.btnShow addListener unit {
        lytMenu.txtId.getValue match {
          case IntNumber(id) =>
            Imcms.getServices.getDocumentMapper.getDocument(id) match {
              case null =>
                show(new MsgDialog("Information", "No document with id ["+id+"]."))
              case doc: TextDocumentDomainObject =>
                lytStructure.removeAllComponents
                lytStructure.addComponent(new Form(new GridLayout(2,1)) {
                  setSpacing(true)
                  setCaption("Texts")
                  let(getLayout.asInstanceOf[GridLayout]) { l =>
                    for ((textId, text) <- doc.getTexts) {
                      addComponents(l, new Label(textId.toString), new Label(text.getText))
                    }
                  }
                })

               lytStructure.addComponent(new Form(new GridLayout(2,1)) {
                  setSpacing(true)
                  setCaption("Images")
                  let(getLayout.asInstanceOf[GridLayout]) { l =>
                    for ((imageId, image) <- doc.getImages) {
                      addComponents(l, new Label(imageId.toString), new Label(image.getImageUrl))
                    }
                  }
                })

               lytStructure.addComponent(new Form(new GridLayout(2,1)) {
                  setSpacing(true)
                  setCaption("Menus")
                  let(getLayout.asInstanceOf[GridLayout]) { l =>
                    for ((menuId, menu) <- doc.getMenus) {
                      addComponents(l, new Label(menuId.toString), new Label(menu.getMenuItems.map(_.getDocumentId).mkString(", ")))
                    }
                  }
                })              

              case _ =>
                show(new MsgDialog("Information", "Not a text document."))
                
            }
          case _: String =>
            show(new MsgDialog("Information", "Document id must be integer."))
        }
      }

      addComponents(this, lytMenu, lytStructure)
    })
  }
}




//    btnChooseFile addListener {
//      initAndShow(new OkCancelDialog("Select template file - .htm .html .xhtml .jsp .jspx")
//              with CustomSizeDialog with BottomMarginOnlyDialog, resizable = true) { w =>
//
//        let(w.mainContent = new FileBrowser) { b =>
//          b setSplitPosition 30
//          b addDirectoryTree("Templates", new File(Imcms.getPath, "WEB-INF/templates"))
//          b.tblDirContent setSelectable true
//
//          w.addOkButtonClickListener {
//            b.tblDirContent.getValue match {
//              case file: File /*if canPreview(file)*/=>
//                txtFilename.setReadOnly(false)
//                txtFilename.setValue(file.getName)
//                txtFilename.setData(file)
//                txtFilename.setReadOnly(true)
//
//              case _ =>
//                txtFilename.setReadOnly(false)
//                txtFilename.setValue("")
//                txtFilename.setData(null)
//                txtFilename.setReadOnly(true)
//            }
//          }
//        }
//
//        w setWidth "650px"
//        w setHeight "350px"
//      }
//    }


