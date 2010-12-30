package com.imcode
package imcms.admin


import document.PermissionsEditor
import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.api._
import imcms.servlet.superadmin.AdminSearchTerms

//import imcms.admin.chat.{MessageView, Chat}

import imcms.admin.access.user.{UserManager}
import imcode.util.Utility
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date}
import scala.actors.Actor._
import scala.actors._
import imcode.server.document.textdocument.TextDocumentDomainObject
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}
import imcode.server.document._
import com.imcode.imcms.vaadin._
import com.vaadin.ui.Window.Notification
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.imcode.imcms.vaadin.Theme.Icons._




// Controller VS HANDLER?
/*
   A manager is associated with a set of tasks
   For every task a user must have a permission...

 * Administration is performed using managers and editors.
 * -or-??
 *  Administration is performed using managers, editors and flows.
 *
 * A manager controls one or more editor.
 * Editor is intended to change and validate edited object (entity) model but not to perform physical changes - that a
 * manager's task (commits).
 *  
 * An editor can be viewed as a MVC where model is typically an administered object (such as document, text or image)
 * itself or in more sophisticated cases an instance of a separate class containing additional parameters and attributes.
 * A view or UI of an editor is represented by one or several standard vaadin components such as Layout, Panel or Window.
 * A controller is just set of routines and callbacks located inside or outside of UI and/or model classes. 
 * In some (mostly trivial) cases model and/or controller are embedded in a view component.
 *
 * A flow is also an editor which contains one or more editors which may have (partially) shared model and controller.
 * - commit???
 *
 * EDITOR UI, but NO EDITOR - explain
 */


//object ChatTopic extends Actor {
//
//  case class Subscribe(subscriber: Actor)
//  case class Message(text: String)
//
//  var subscribers: Set[Actor] = Set.empty
//
//  def act {
//    loop {
//      react {
//        case Subscribe(subscriber) =>
//          subscribers += subscriber // send 10 last messages?
//        case msg : Message => subscribers foreach (_ ! msg)
//        case other => println("Unknown message: " + other)
//      }
//    }
//  }
//
//  start()
//}


class Application extends com.vaadin.Application with ImcmsApplication { app =>

  def canAccess {
//            if ( !user.isSuperAdmin() && !user.isUserAdminAndCanEditAtLeastOneRole() ) {
//                Utility.forwardToLogin( request, response );
//                return;
//            }
//
//        if ( !user.canAccessAdminPages() ) {
//            Utility.forwardToLogin( request, response );
//            return;
//        }
  }

  // Main menu items IDS
  object NewMenu {
    object About
    object Settings {
      object Languages
      object Properties
    }
    object Documents {
      object Categories
      object Templates
      object Profiles
      object Links
      object Structure
      object Edit
    }
    object Permissions {
      object Users
      object Roles
      object IP_Access
    }
    object Statistics {
      object SearchTerms
      object SessionCounter
    }
    object Filesystem
    object System {
      object Cache
    }
  }
  
  object Menu extends MenuItem {
    object About extends MenuItem(this)
    object Settings extends MenuItem(this) {
      object Languages extends MenuItem(this)
      object Properties extends MenuItem(this)
    }
    object Documents extends MenuItem(this) {
      object Categories extends MenuItem(this, Some(Done16))
      object Templates extends MenuItem(this, Some(Done16))
      object Profiles extends MenuItem(this)
      object Links extends MenuItem(this)
      object Structure extends MenuItem(this)
      object Edit extends MenuItem(this)
    }
    object Permissions extends MenuItem(this) {
      object Users extends MenuItem(this)
      object Roles extends MenuItem(this, Some(Done16))
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

      addListener(block { resetComponents })

      tableProperties foreach (addContainerProperties(this, _))
    }

    val btnReload = new Button("Reload") {
      addListener(block { reloadTableItems })
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
    addTab(new VerticalLayoutUI(id.toString) {
      addComponent(new Label("NOT AVAILABLE"))
    })
  }

  val labelAbout = new VerticalLayoutUI {
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
          menu.icon foreach (treeMenu.setItemIcon(menu, _))

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
            case Menu.Documents.Edit => docadmin
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
    setTheme("imcms")
    wndMain initMenu Menu
    Menu.items foreach { wndMain.treeMenu expandItemsRecursively _ }
    wndMain.treeMenu select Menu.About
    this setMainWindow wndMain    
  }


  // doadmin prototype
  def docadmin = {
    import com.imcode.imcms.admin.document.{MetaEditor, EditorsFactory, MetaModel}

    val dm = Imcms.getServices.getDocumentMapper
    val btnNewTextDoc = new Button("New text doc")
    val btnNewFileDoc = new Button("New file doc")
    val btnNewUrlDoc = new Button("New url doc")
    val btnDocInfo = new Button("Doc info")
    val btnDocPermissions = new Button("Doc Permissions")
    val btnEdit = new Button("Edit doc") // edit content
    val btnReload = new Button("RELOAD") with LinkStyle

    val docAdmin = new EditorsFactory(app, Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(1))

    val tblDocs = new Table with ValueType[JInteger] with Selectable with Immediate {
      addContainerProperties(this,
        ContainerProperty[JInteger]("Id"),
        ContainerProperty[String]("Type"),
        ContainerProperty[Date]("Created date"),
        ContainerProperty[Date]("Modified date"),
        ContainerProperty[String]("Default version no"))
    }

    val lytBar = new HorizontalLayout with Spacing {
      addComponents(this, btnNewTextDoc, btnNewFileDoc, btnNewUrlDoc, btnDocInfo, btnEdit, btnDocPermissions, btnReload)
    }

    btnReload addListener { reload _ }

    btnNewTextDoc addListener block {
      app.initAndShow(new Dialog("New text document") with BottomMarginDialog) { dlg =>
        val parentDoc = dm.getDocument(1001)
        val onCommit = { doc: TextDocumentDomainObject =>
          getMainWindow.showNotification("Text document [id = %d] has been created" format doc.getId, Notification.TYPE_HUMANIZED_MESSAGE)
          dlg.close
          reload()
        }

        val flow = docAdmin.newTextDocFlow(parentDoc)
        val flowUI = flow.ui

        flow.commitListeners += onCommit

        dlg.setMainContent(flowUI)

        flowUI.setWidth("600px")
        flowUI.setHeight("800px")
      }
    }

    btnDocPermissions addListener block {

      whenSelected(tblDocs) { id =>
        val model = MetaModel(id)
        val editor = new PermissionsEditor(app, model.meta, Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(1))



        app.initAndShow(new OkCancelDialog) { dlg =>
          dlg.setMainContent(editor.ui)
        }
      }
    }
    
    btnDocInfo addListener block {
      whenSelected(tblDocs) { id =>
        val model = MetaModel(id)
        val editor = new MetaEditor(app, model)

        app.initAndShow(new OkCancelDialog) { d =>
          d.setMainContent(editor.ui)
        }        
      }
    }

    btnNewUrlDoc addListener block {
      app.initAndShow(new Dialog("New url document")) { dlg =>
        val parentDoc = dm.getDocument(1001)
        val flow = docAdmin.newURLDocFlow(parentDoc)
        dlg.setMainContent(flow.ui)
        dlg.setWidth("600px")
        dlg.setHeight("800px")
      }
    }

    btnNewFileDoc addListener block {
      app.initAndShow(new Dialog("New file document")) { dlg =>
        val parentDoc = dm.getDocument(1001)
        val onCommit = { doc: FileDocumentDomainObject =>
          getMainWindow.showNotification("File document [id = %d] has been created" format doc.getId, Notification.TYPE_HUMANIZED_MESSAGE)
          dlg.close
          reload()
        }

        val flow = docAdmin.newFileDocFlow(parentDoc)
        val flowUI = flow.ui

        flow.commitListeners += onCommit

        flowUI.bar.btnCancel addListener block {
          dlg.close
        }
        
        dlg.setMainContent(flowUI)

        dlg.setWidth("600px")
        dlg.setHeight("800px")
      }
    }

    btnEdit addListener block {
      whenSelected(tblDocs) { id =>
        // show edit meta dialog
      }
    }

    def reload() {
      tblDocs.removeAllItems
      for {
        id <- dm.getAllDocumentIds
        meta = dm.getDocumentLoaderCachingProxy.getMeta(id)
      } {
        addItem(tblDocs, id, id, meta.getDocumentTypeId, meta.getCreatedDatetime, meta.getModifiedDatetime, meta.getDefaultVersionNo.toString)
      }
    }

    new VerticalLayout with Margin with Spacing {
      addComponents(this, lytBar, tblDocs)
      reload()
    }
  }

//  def systemCacheView = {
//    import com.imcode.imcms.sysadmin.cache.View
//
//    new cache.View(Imcms.getDocumentMapper.getCahcingDocumentGetter)
//  }
  def systemCacheView = new com.imcode.imcms.admin.monitor.cache.View(Imcms.getServices.getDocumentMapper.getDocumentLoaderCachingProxy)

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

            app.initAndShow(new LanguageWindow("New language")) { wndEditLanguage =>
              val language = new com.imcode.imcms.api.I18nLanguage

              wndEditLanguage setOkHandler {
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

            app.initAndShow(new LanguageWindow("Edit language")) { wndEditLanguage =>
              wndEditLanguage.txtId.setValue(language.getId)
              wndEditLanguage.txtId.setEnabled(false)
              wndEditLanguage.txtCode.setValue(language.getCode)
              wndEditLanguage.txtName.setValue(language.getName)
              wndEditLanguage.txtNativeName.setValue(language.getNativeName)
              wndEditLanguage.chkEnabled.setValue(language.isEnabled)

              wndEditLanguage setOkHandler {
                language.setCode(wndEditLanguage.txtCode.getValue.asInstanceOf[String])
                language.setName(wndEditLanguage.txtName.getValue.asInstanceOf[String])
                language.setNativeName(wndEditLanguage.txtNativeName.getValue.asInstanceOf[String])
                language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[JBoolean])

                languageDao.saveLanguage(language)
                reloadTable
              }
            }

          case `btnSetDefault` =>
            app.initAndShow(new ConfirmationDialog("Confirmation", "Change default language?")) { wndConfirmation =>
              wndConfirmation setOkHandler {
                val languageId = table.getValue.asInstanceOf[JInteger]
                val property = systemDao.getProperty("DefaultLanguageId")

                property.setValue(languageId.toString)
                systemDao.saveProperty(property)
                reloadTable
              }
            }

          case `btnDelete` =>
            app.initAndShow(new ConfirmationDialog("Confirmation", "Delete language from the system?")) { wndConfirmation =>
              wndConfirmation setOkHandler {
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

    table addListener block { resetControls }


    val pnlReloadBar = new Panel(new GridLayout(1,1))
    val btnReload = new Button("Reload")
    pnlReloadBar.addComponent(btnReload)

    btnReload addListener block { reloadTable }

    pnlReloadBar.getContent.setSizeFull
    pnlReloadBar.getContent.asInstanceOf[GridLayout].setComponentAlignment(btnReload, Alignment.MIDDLE_RIGHT)

    reloadTable    
    resetControls

    new TabSheetView {
      addTab(new VerticalLayoutUI("Languages") {
        addComponent(new GridLayout(1,3) {
          addComponents(this, pnlControls, table, pnlReloadBar)
        })
      })
    }
  }
  

  def documentsTable = new TabSheetView {
    addTab(new VerticalLayoutUI("Documents") {
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

      btnAdd addListener block {
        app.initAndShow(new IPAccessWindow("Add new IP Access")) { w =>
          Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllUsers foreach { u =>
            w.sltUser addItem u.getId
            w.sltUser setItemCaption (u.getId, u.getLoginName)
          }

          w.setOkHandler {
            val ipAccess = new IPAccess
            ipAccess setUserId w.sltUser.getValue.asInstanceOf[Integer]
            ipAccess setStart fromDDN(w.txtFrom.getValue.asInstanceOf[String])
            ipAccess setEnd fromDDN(w.txtTo.getValue.asInstanceOf[String])

            ipAccessDao.save(ipAccess)

            reloadTableItems
          }
        }
      }

      btnEdit addListener block {
        app.initAndShow(new IPAccessWindow("Edit IP Access")) { w =>
          Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllUsers foreach { u =>
            w.sltUser addItem u.getId
            w.sltUser setItemCaption (u.getId, u.getLoginName)
          }

          val ipAccessId = tblItems.getValue.asInstanceOf[JInteger]
          val ipAccess = ipAccessDao get ipAccessId

          w.sltUser select ipAccess.getUserId
          w.txtFrom setValue toDDN(ipAccess.getStart)
          w.txtTo setValue toDDN(ipAccess.getEnd)

          w.setOkHandler {
            val ipAccess = new IPAccess
            ipAccess setUserId w.sltUser.getValue.asInstanceOf[Integer]
            ipAccess setStart fromDDN(w.txtFrom.getValue.asInstanceOf[String])
            ipAccess setEnd fromDDN(w.txtTo.getValue.asInstanceOf[String])

            ipAccessDao.save(ipAccess)

            reloadTableItems
          }
        }
      }

      btnDelete addListener block {
        app.initAndShow(new ConfirmationDialog("Confirmation", "Delete IP Access?")) { w =>
          w.setOkHandler {
            ipAccessDao delete tblItems.getValue.asInstanceOf[JInteger]
            reloadTableItems
          }
        }
      }
    }

    new TabSheetView {
      addTab(new VerticalLayoutUI("IP Access") {
        addComponents(this,
          new Label("Users from a specific IP number or an intervall of numbers are given direct access to the system (so that the user does not have to log in)."),
          new IPAccessView)
      })
    }
  }


  lazy val roles = new VerticalLayout with Margin {
    val roleManager = new com.imcode.imcms.admin.access.role.RoleManager(app)

    val tabSheet = new TabSheet
    tabSheet.addTab(roleManager.ui, "Roles and their permissions", Tab32)

    roleManager.ui.setMargin(true)

    addComponent(tabSheet)
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

    lytButtons.btnRevert addListener block {
      reload() 
    }

    lytButtons.btnSave addListener block {
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
      addTab(new VerticalLayoutUI("System Properties") {
        addComponent(lytContent)
      })
    }   
  }

  def settingSessionCounter = new TabSheetView {
    addTab(new VerticalLayoutUI("Session counter") { self =>
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

      lytButtons.btnReload addListener block { reload() }
      lytButtons.btnClear addListener block {
        app.initAndShow(new ConfirmationDialog("Confirmation", "Clear counter statistics?")) { w =>
          w.setOkHandler {
            Imcms.getServices setSessionCounter 0
            Imcms.getServices setSessionCounterDate new Date

            reload()
          }          
        }
      }

      lytButtons.btnEdit addListener block {
        app.initAndShow(new OkCancelDialog("Edit session counter")) { w =>
          val txtValue = new TextField("Value")
          val calStart = new DateField("Start date")

          calStart.setResolution(DateField.RESOLUTION_DAY)
          txtValue setValue Imcms.getServices.getSessionCounter.toString
          calStart setValue Imcms.getServices.getSessionCounterDate

          w.mainContent = new FormLayout {
            addComponents(this, txtValue, calStart)
          }

          w.setOkHandler {
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
    addTab(new VerticalLayoutUI("Popular search terms") {
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

      lytBar.btnReload addListener block { reload() }

      reload()
    })
  }


  lazy val filesystem = new TabSheetView {
    tabSheet.setSizeFull
    setSizeFull
    addTab(new VerticalLayoutUI("File manager") {
      setSizeFull

      addComponent(new com.imcode.imcms.admin.filesystem.FileManager ui)
    })
  }

  lazy val templates = new VerticalLayout with Margin {
    lazy val templateManager = new com.imcode.imcms.admin.document.template.TemplateManager(app)
    lazy val templateGroupManager = new com.imcode.imcms.admin.document.template.group.TemplateGroupManager(app)

    val tabSheet = new TabSheet
    tabSheet.addTab(templateManager.ui, "Templates", Tab32)
    tabSheet.addTab(templateGroupManager.ui, "Template Groups", Tab32)

    templateManager.ui.setMargin(true)
    templateGroupManager.ui.setMargin(true)

    addComponent(tabSheet)
  }


  lazy val categories = new VerticalLayout with Margin {
    val categoryManager = new com.imcode.imcms.admin.document.category.CategoryManager(app)
    val categoryTypeManager = new com.imcode.imcms.admin.document.category.`type`.CategoryTypeManager(app)

    val tabSheet = new TabSheet
    tabSheet.addTab(categoryManager.ui, "Categories ", Tab32)
    tabSheet.addTab(categoryTypeManager.ui, "Category types", Tab32)

    categoryManager.ui.setMargin(true)
    categoryTypeManager.ui.setMargin(true)

    addComponent(tabSheet)
  } // category

  //
  // Chat
  //
  lazy val chat =  new VerticalLayout
//    new Chat {
//    setCaption("Chat messages")
//      setMargin(true)
//      val subscriber = actor {
//        loop {
//          react {
//            case ChatTopic.Message(text) =>
//              pnlMessages addMessage new MessageView("#user#", text)
//              pnlMessages.requestRepaint
//            case _ =>
//          }
//        }
//      }
//
//      btnSend addListener block {
//        ChatTopic ! ChatTopic.Message(txtText.getValue.asInstanceOf[String])
//        txtText setValue ""
//      }
//      ChatTopic ! ChatTopic.Subscribe(subscriber)
//    } //chat


  //
  // User manager
  //
  lazy val users = {
    new TabSheetView {
      addTab(new VerticalLayoutUI("Users and their permissions.") {
        addComponent(new UserManager(app) ui)
      })
    }
  }

  //
  //
  //
  def docStructure = new TabSheetView {
    addTab(new VerticalLayoutUI("Document structure outline") {
      val lytMenu = new HorizontalLayout {
        setSpacing(true)
        val txtId = new TextField("Text doc (meta) id")
        val btnShow = new Button("Show")

        addComponents(this, txtId, btnShow)
      }
      val lytStructure = new VerticalLayout {
        setSpacing(true)
      }

      lytMenu.btnShow addListener block {
        lytMenu.txtId.getValue match {
          case IntNumber(id) =>
            Imcms.getServices.getDocumentMapper.getDocument(id) match {
              case null =>
                app.show(new MsgDialog("Information", "No document with id ["+id+"]."))
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
                app.show(new MsgDialog("Information", "Not a text document."))
                
            }
          case _: String =>
            app.show(new MsgDialog("Information", "Document id must be integer."))
        }
      }

      addComponents(this, lytMenu, lytStructure)
    })
  }
}




//    btnChooseFile addListener {
//      app.initAndShow(new OkCancelDialog("Select template file - .htm .html .xhtml .jsp .jspx")
//              with CustomSizeDialog with BottomMarginDialog, resizable = true) { w =>
//
//        let(w.mainContent = new FileBrowser) { b =>
//          b setSplitPosition 30
//          b addDirectoryTree("Templates", new File(Imcms.getPath, "WEB-INF/templates"))
//          b.tblDirContent setSelectable true
//
//          w.setOkHandler {
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