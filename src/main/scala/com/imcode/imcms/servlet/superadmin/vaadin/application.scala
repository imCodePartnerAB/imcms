package com.imcode.imcms.servlet.superadmin.vaadin;

import scala.collection.JavaConversions._
import clojure.lang.RT
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import imcode.server.Imcms
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcode.server.document.DocumentDomainObject
import com.imcode.imcms.api.{Document}
import com.imcode.imcms.api.Document.PublicationStatus
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper
import com.vaadin.terminal.UserError

class App extends com.vaadin.Application {

  type ButtonClickHandler = Button#ClickEvent => Unit

  val languageDao = Imcms.getSpringBean("languageDao").asInstanceOf[LanguageDao]
  val systemDao = Imcms.getSpringBean("systemDao").asInstanceOf[SystemDao]
  val ipAccessDao = Imcms.getSpringBean("ipAccessDao").asInstanceOf[IPAccessDao]

  def addClickHandler(button: Button)(handler: ButtonClickHandler) {
    button addListener new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent) = handler(event)
    }
  }

  def addClickHandler(button: Button, handler: => Unit): Unit =
    addClickHandler(button) { _ => handler }

  def addClickHandlers(buttonHandlers: (Button, ButtonClickHandler)*) {
    for ((button, handler) <- buttonHandlers)
      addClickHandler(button)(handler)
  }
  

  def addValueChangeHandler(target: AbstractField)(handler: ValueChangeEvent => Unit) {
    target addListener new Property.ValueChangeListener {
      def valueChange(event: ValueChangeEvent) = handler(event);
    }
  }

  def addValueChangeHandler(target: AbstractField, handler: => Unit): Unit =
    addValueChangeHandler(target) { _ => handler }

  def initAndShow[W <: Window](window: W, modal: Boolean = true)(init: W => Unit) {
    init(window)
    window setModal modal
    wndMain addWindow window
  }

  def addComponents(container: AbstractComponentContainer, components: Component*) =
    components foreach {c => container addComponent c}

  /** Message window */
  class MsgWindow(caption: String, msg: String) extends Window(caption) {
    val btnOk = new Button("Ok")
    val lblMessage = new Label(msg)

    val lytContent = new GridLayout(1, 2)
    
    lytContent setMargin true
    lytContent setSpacing true
    lytContent addComponent lblMessage
    lytContent addComponent btnOk

    lytContent.setComponentAlignment(lblMessage, Alignment.BOTTOM_CENTER)
    lytContent.setComponentAlignment(btnOk, Alignment.TOP_CENTER)

    lytContent setSizeFull

    setContent(lytContent)

    addClickHandler(btnOk, close)
  }

  /** OKCancel window. */
  class OkCancelWindow(caption: String) extends Window(caption) {
    val btnOk = new Button("Ok")
    val btnCancel = new Button("Cancel")

    val lytButtons = new GridLayout(2, 1)
    val lytContent = new VerticalLayout

    setContent(lytContent)
    lytContent addComponent lytButtons
    lytContent setMargin true

    lytButtons setSpacing true
    lytButtons addComponent btnOk
    lytButtons addComponent btnCancel
    
    lytButtons.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
    lytButtons.setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)

    addClickHandler(btnCancel, close)

    def setMainContent(c: Component) = lytContent addComponentAsFirst c
  }

  /** Confirmation window. */
  class ConfirmationWindow(caption: String, msg: String) extends OkCancelWindow(caption) {
    val lblMessage = new Label(msg)

    setMainContent(lblMessage)
  }

  case class Node(nodes: Node*)

  val labelNA = new Label("Not Available");

  val labelAbout = new Label("""|Welcome to the imCMS new admin UI prototype -
                                | please pick a task from the menu.
                                |""".stripMargin)

  var wndMain: Window = _

  def show(w: Window) = wndMain addWindow w
  
  def init {
    wndMain = new Window
    val splitPanel = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL)
    val tree = new Tree

    val treeItems = List(
      "About" -> Nil,
      "System" -> List("Languages", "Properties"),
      "Documents" -> List("New", "Search"),
      "Permissions" -> List("Users", "Roles", "IP Access"))

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
    class LanguageWindow(caption: String) extends OkCancelWindow(caption) {
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

              addClickHandler(wndEditLanguage.btnOk) { _ =>
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
                  wndMain removeWindow wndEditLanguage
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

              addClickHandler(wndEditLanguage.btnOk) { _ =>
                language.setCode(wndEditLanguage.txtCode.getValue.asInstanceOf[String])
                language.setName(wndEditLanguage.txtName.getValue.asInstanceOf[String])
                language.setNativeName(wndEditLanguage.txtNativeName.getValue.asInstanceOf[String])
                language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[java.lang.Boolean])

                languageDao.saveLanguage(language)
                reloadTable
                wndMain removeWindow wndEditLanguage
              }
            }

          case `btnSetDefault` =>
            initAndShow(new ConfirmationWindow("Confirmation", "Change default language?")) { wndConfirmation =>
              addClickHandler(wndConfirmation.btnOk, {
                val languageId = table.getValue.asInstanceOf[java.lang.Integer]
                val property = systemDao.getProperty("DefaultLanguageId")

                property.setValue(languageId.toString)
                systemDao.saveProperty(property)
                reloadTable
                wndMain removeWindow wndConfirmation
              })
            }

          case `btnDelete` =>
            initAndShow(new ConfirmationWindow("Confirmation", "Delete language from the system?")) { wndConfirmation =>
              addClickHandler(wndConfirmation.btnOk, {
                val languageId = table.getValue.asInstanceOf[java.lang.Integer]
                languageDao.deleteLanguage(languageId)
                reloadTable
                wndMain removeWindow wndConfirmation
              })
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

    addValueChangeHandler(table, resetControls)

    reloadTable

    val pnlReloadBar = new Panel(new GridLayout(1,1))
    val btnReload = new Button("Reload")
    pnlReloadBar.addComponent(btnReload)

    addClickHandler(btnReload, reloadTable)

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


  def roles = {
    def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

    val table = new Table
    table.addContainerProperty("Id", classOf[java.lang.Integer],  null)
    table.addContainerProperty("Name", classOf[String],  null)

    roleMapper.getAllRoles.foreach { role =>
      table.addItem(Array(Int box role.getId.intValue, role.getName), role)
    }

    table
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
    class IPAccessWindow(caption: String) extends OkCancelWindow(caption) {
      val sltUser = new Select("Users")
      val txtFrom = new TextField("Code")
      val txtTo = new TextField("Name")

      val lytMainContent = new FormLayout

      lytMainContent.addComponent(sltUser)
      lytMainContent.addComponent(txtFrom)
      lytMainContent.addComponent(txtTo)

      setMainContent(lytMainContent)
    }

    val table = new Table
    val btnReload = new Button("Reload")
    val btnAdd = new Button("Add")
    val btnEdit = new Button("Edit")
    val btnDelete = new Button("Delete")

    table.addContainerProperty("User", classOf[java.lang.Integer],  null)
    table.addContainerProperty("IP range from", classOf[String],  null)
    table.addContainerProperty("IP range to", classOf[String],  null)

    val pnlMenuBar = new Panel
    val pnlReloadBar = new Panel
    val pnlContent = new Panel

    val lytContent = new VerticalLayout
    lytContent.setMargin(true)
    pnlContent.setContent(lytContent)

    pnlContent addComponent pnlMenuBar
    pnlContent addComponent table
    pnlContent addComponent pnlReloadBar

    val lytMenuBar = new HorizontalLayout
    lytMenuBar.setSpacing(true)
    pnlMenuBar.setContent(lytMenuBar)

    addClickHandlers(
      btnAdd -> { _ =>
        initAndShow(new IPAccessWindow("Add new IP Access")) { w =>
          addClickHandler(w.btnOk, wndMain removeWindow w)
        }
      },

      btnEdit -> { _ =>
        initAndShow(new IPAccessWindow("Edit IP Access")) { w =>
          addClickHandler(w.btnOk, wndMain removeWindow w)
        }
      },
    
      btnDelete -> { _ =>
        initAndShow(new MsgWindow("Delete", "IP Access entry delete is not yet implemented.")) { w =>
          //addClickHandler(w.btnOk, wndMain removeWindow w)
        }
      }
    )

    addComponents(pnlMenuBar, btnAdd, btnEdit, btnDelete)

    pnlContent
  }
}