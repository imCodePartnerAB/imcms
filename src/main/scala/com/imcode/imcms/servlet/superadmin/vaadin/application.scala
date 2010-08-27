package com.imcode.imcms.servlet.superadmin.vaadin;

import scala.collection.JavaConversions._
import com.imcode.Controls._
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import imcode.server.Imcms
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcode.server.document.DocumentDomainObject
import com.imcode.imcms.api.Document.PublicationStatus
import com.vaadin.terminal.UserError
import com.imcode.imcms.api.{IPAccess, Document}
import imcode.util.Utility
import imcode.server.user._

class App extends com.vaadin.Application {

  type ButtonClickHandler = Button#ClickEvent => Unit
  type PropertyValueChangeHandler = ValueChangeEvent => Unit 

  val languageDao = Imcms.getSpringBean("languageDao").asInstanceOf[LanguageDao]
  val systemDao = Imcms.getSpringBean("systemDao").asInstanceOf[SystemDao]
  val ipAccessDao = Imcms.getSpringBean("ipAccessDao").asInstanceOf[IPAccessDao]

  implicit def BlockToButtonClickListener(handler: => Unit): Button.ClickListener =
    new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent) = handler
    }  

//  def addButtonClickListener(button: Button)(handler: ButtonClickHandler) {
//    button addListener new Button.ClickListener {
//      def buttonClick(event: Button#ClickEvent) = handler(event)
//    }
//  }

  implicit def BlockToPropertyValueChangeListener(block: => Unit): Property.ValueChangeListener =
    new Property.ValueChangeListener {
      def valueChange(event: ValueChangeEvent) = block
    }

//  def addValueChangeHandler(target: AbstractField)(handler: ValueChangeEvent => Unit) {
//    target addListener new Property.ValueChangeListener {
//      def valueChange(event: ValueChangeEvent) = handler(event)
//    }
//  }

  def initAndShow[W <: Window](window: W, modal: Boolean = true)(init: W => Unit) {
    init(window)
    window setModal modal
    wndMain addWindow window
  }

  def addComponents(container: AbstractComponentContainer, components: Component*) = {
    components foreach { c => container addComponent c }
    container
  }

  def addContainerProperties(table: Table, properties: (AnyRef, java.lang.Class[_], AnyRef)*) =
    for ((propertyId, propertyType, defaultValue) <- properties)
      table.addContainerProperty(propertyId, propertyType, defaultValue)

  class DialogWindow(caption: String) extends Window(caption) {
    val lytContent = new GridLayout(1, 2)

    lytContent setMargin true
    lytContent setSpacing true
    lytContent setSizeFull

    setContent(lytContent)

    def setMainContent(c: Component) {
      lytContent.addComponent(c, 0, 0)
      lytContent.setComponentAlignment(c, Alignment.BOTTOM_CENTER)
    }
    
    def setButtonsContent(c: Component) {
      lytContent.addComponent(c, 0, 1)
      lytContent.setComponentAlignment(c, Alignment.TOP_CENTER)
    }
  }

  /** Message dialog window. */
  class MsgDialog(caption: String, msg: String) extends DialogWindow(caption) {
    val btnOk = new Button("Ok")
    val lblMessage = new Label(msg)

    setMainContent(lblMessage)
    setButtonsContent(btnOk)

    btnOk addListener close
  }

  /** OKCancel dialog window. */
  class OkCancelDialog(caption: String) extends DialogWindow(caption) {
    val btnOk = new Button("Ok")
    val btnCancel = new Button("Cancel")
    val lytButtons = new GridLayout(2, 1) {
      setSpacing(true)
      addComponent(btnOk)
      addComponent(btnCancel)
      setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
      setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)
    }
    
    setButtonsContent(lytButtons)

    btnCancel addListener close

    def addOkButtonClickListener(block: => Unit) {
      btnOk addListener {
        try {
          block
          close
        } catch {
          case ex: Exception => using(new java.io.StringWriter) { w =>
            ex.printStackTrace(new java.io.PrintWriter(w))
            show(new MsgDialog("ERROR", "%s  ##  ##  ##  ## ## %s" format (ex.getMessage, w.getBuffer)))
            throw ex
          }
        }
      }
    }
  }

  /** Confirmation dialog window. */
  class ConfirmationDialog(caption: String, msg: String) extends OkCancelDialog(caption) {
    val lblMessage = new Label(msg)

    setMainContent(lblMessage)
  }

  abstract class TableViewTemplate extends GridLayout(1,3) {
    val tblItems = new Table {
      setSelectable(true)
      setImmediate(true)
      setPageLength(10)

      setSizeFull

      addListener { resetComponents }

      getTableProperties foreach { p => addContainerProperties(this, p) }
    }

    val pnlHeader = new Panel {
      val layout = new HorizontalLayout {
        setSpacing(true)
      }

      setContent(layout)
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

      setContent(layout)
    }

    addComponents(this, pnlHeader, tblItems, pnlFooter)

    // Investigate: List[(AnyRef, Array[AnyRef])]
    def getTableItems: List[(AnyRef, List[AnyRef])]

    def getTableProperties: List[(AnyRef, java.lang.Class[_], AnyRef)]

    def reloadTableItems {
      tblItems.removeAllItems

      for((id, cells) <- getTableItems) tblItems.addItem(cells.toArray, id)
      //for ((id:, cells:) <- getTableItems) tblItems.addItem(cells, id)
    }

    def resetComponents = {}
    
    reloadTableItems
    resetComponents
  }

  case class Node(nodes: Node*)

  val labelNA = new Label("Not Available");

  val labelAbout = new Label("""|Welcome to the imCMS new admin UI prototype -
                                | please pick a task from the menu.
                                |""".stripMargin)

  var wndMain: Window = _

  def show(wndChild: Window, modal: Boolean = true) {
    wndChild setModal modal
    wndMain addWindow wndChild
  }

  def init {
    wndMain = new Window
    val splitPanel = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL)
    val tree = new Tree

    val treeItems = List(
      "About" -> Nil,
      "Documents" -> List("Categories", "Templates"),    
      "Settings" -> List("Languages", "Properties"),
      "Permissions" -> List("Users", "Roles", "IP Access"),
      "Statistics" -> List("Session counter", "Search terms"),
      "Filesystem" -> Nil)

    treeItems foreach {
      case (item, subitems) =>
        tree addItem item
        if (subitems.isEmpty) {
          tree setChildrenAllowed (item, false)
        } else {
          subitems foreach {subitem =>
            tree addItem subitem
            tree setParent (subitem, item)
            tree setChildrenAllowed (subitem, false)
          }
        }

        tree expandItemsRecursively item
    }

    tree addListener (new ValueChangeListener {
      def valueChange(e: ValueChangeEvent) {
        e.getProperty.getValue.asInstanceOf[String] match {
          case "Languages" => splitPanel setSecondComponent languagesPanel
          case "Properties" => splitPanel setSecondComponent propertiesTable
          case "About" => splitPanel setSecondComponent labelAbout
          case "Documents" => splitPanel setSecondComponent documentsTable
          case "Roles" => splitPanel setSecondComponent roles
          case "Users" => splitPanel setSecondComponent users
          case "IP Access" => splitPanel setSecondComponent ipAccess

          case _ => splitPanel setSecondComponent labelNA
        }
      }
    })

    tree setImmediate true
    tree select "About"

    splitPanel setFirstComponent tree

    wndMain setContent splitPanel
    
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

    val pnlLanguages = new Panel

    
    val table = new Table

    table setPageLength 10
    table setSelectable true
    table setImmediate true

    table.addContainerProperty("Id", classOf[java.lang.Integer],  null)
    table.addContainerProperty("Code", classOf[String],  null)
    table.addContainerProperty("Name", classOf[String],  null)
    table.addContainerProperty("Native name", classOf[String],  null)
    table.addContainerProperty("Enabled", classOf[java.lang.Boolean],  null)
    table.addContainerProperty("Default", classOf[java.lang.Boolean],  null)

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
                  language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[java.lang.Boolean])

                  languageDao.saveLanguage(language)
                  reloadTable
                }
              }
            }

          case `btnEdit` =>
            val languageId = table.getValue.asInstanceOf[java.lang.Integer]
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
                language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[java.lang.Boolean])

                languageDao.saveLanguage(language)
                reloadTable
              }
            }

          case `btnSetDefault` =>
            initAndShow(new ConfirmationDialog("Confirmation", "Change default language?")) { wndConfirmation =>
              wndConfirmation addOkButtonClickListener {
                val languageId = table.getValue.asInstanceOf[java.lang.Integer]
                val property = systemDao.getProperty("DefaultLanguageId")

                property.setValue(languageId.toString)
                systemDao.saveProperty(property)
                reloadTable
              }
            }

          case `btnDelete` =>
            initAndShow(new ConfirmationDialog("Confirmation", "Delete language from the system?")) { wndConfirmation =>
              wndConfirmation addOkButtonClickListener {
                val languageId = table.getValue.asInstanceOf[java.lang.Integer]
                languageDao.deleteLanguage(languageId)
                reloadTable
              }
            }
        }
      }
    }    

    def resetControls = {
      val languageId = table.getValue.asInstanceOf[java.lang.Integer]

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

    reloadTable

    val pnlReloadBar = new Panel(new GridLayout(1,1))
    val btnReload = new Button("Reload")
    pnlReloadBar.addComponent(btnReload)

    btnReload addListener reloadTable

    pnlReloadBar.getContent.setSizeFull
    pnlReloadBar.getContent.asInstanceOf[GridLayout].setComponentAlignment(btnReload, Alignment.MIDDLE_RIGHT)

    val lytLanguages = new GridLayout(1,3)
    pnlLanguages.setContent(lytLanguages)
    lytLanguages.setMargin(true)

    pnlLanguages addComponent pnlControls
    pnlLanguages addComponent table    
    pnlLanguages addComponent pnlReloadBar

    resetControls
    pnlLanguages
  }
  

  def propertiesTable = {
    val table = new Table("Properties")
    table.addContainerProperty("Id", classOf[java.lang.Integer],  null)
    table.addContainerProperty("Name", classOf[String],  null)
    table.addContainerProperty("Value", classOf[String],  null)

    val systemDao = Imcms.getSpringBean("systemDao").asInstanceOf[SystemDao]

    systemDao.getProperties.toList foreach { property =>
      table.addItem(Array(property.getId, property.getName, property.getValue), property.getId)
    }

    table
  }

  
  def documentsTable = {
    val content = new VerticalLayout
    content.setMargin(true)

    val table = new Table()
    table.addContainerProperty("Page alias", classOf[String],  null)
    table.addContainerProperty("Status", classOf[String],  null)
    table.addContainerProperty("Type", classOf[java.lang.Integer],  null)
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

    content.addComponent(controls)
    content.addComponent(table)

    content
  }

  
  def users = {
    def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

    val table = new Table

    table.addContainerProperty("Id", classOf[java.lang.Integer],  null)
    table.addContainerProperty("Login name", classOf[String],  null)
    table.addContainerProperty("Password", classOf[String],  null)
    table.addContainerProperty("Default user?", classOf[java.lang.Boolean],  null)
    table.addContainerProperty("Superadmin?", classOf[java.lang.Boolean],  null)
    table.addContainerProperty("Useradmin?", classOf[java.lang.Boolean],  null)

    roleMapper.getAllUsers.foreach { user =>
      table.addItem(Array(Int box user.getId, user.getLoginName, user.getPassword,
                          Boolean box user.isDefaultUser,
                          Boolean box user.isSuperAdmin,
                          Boolean box user.isUserAdmin), user)
    }

    table
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

      def getTableProperties = List(
        ("User", classOf[String],  null),
        ("IP range from", classOf[String],  null),
        ("IP range to", classOf[String],  null))

      def getTableItems = ipAccessDao.getAll.toList map { ipAccess =>
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

          val ipAccessId = tblItems.getValue.asInstanceOf[java.lang.Integer]
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
            ipAccessDao delete tblItems.getValue.asInstanceOf[java.lang.Integer]
            reloadTableItems
          }
        }
      }
      
      // Bug in vaadin? - items are not displayed
      //reloadTableItems
    }

    val lytContent = new VerticalLayout
    lytContent setMargin true
    lytContent setSpacing true
    
    addComponents(lytContent,
      new Label("Users from a specific IP number or an intervall of numbers are given direct access to the system (so that the user does not have to log in)."),
      new IPAccessView)
  }


  def roles = {
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

      def getTableProperties = List(
        ("Id", classOf[java.lang.Integer],  null),
        ("Name", classOf[String],  null))

      def getTableItems = 
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

      
      //reloadTableItems
    }

    val lytContent = new VerticalLayout
    lytContent setMargin true
    lytContent setSpacing true

    addComponents(lytContent,
      new Label("Roles and their permissions."),
      new RolesView)    
  }  
}