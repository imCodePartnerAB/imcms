package com.imcode.imcms.servlet.superadmin.vaadin;

import scala.collection.JavaConversions._
import clojure.lang.RT
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import imcode.server.Imcms
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao}
import imcode.server.document.DocumentDomainObject
import com.imcode.imcms.api.{Document}
import com.imcode.imcms.api.Document.PublicationStatus
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper
import com.vaadin.terminal.UserError

class App extends com.vaadin.Application {

  case class Node(nodes: Node*)

  val labelNA = new Label("Not Available");

  val labelAbout = new Label("""|Welcome to the imCMS new admin UI prototype -
                                | please pick a task from the menu.
                                |""".stripMargin)

  var wndMain: Window = _

  def init {
    wndMain = new Window
    val splitPanel = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL)
    val tree = new Tree

    val treeItems = List(
      "About" -> Nil,
      "System" -> List("Languages", "Properties"),
      "Documents" -> List("New", "Search"),
      "Permissions" -> List("Users", "Roles"))

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
          case "Languages" => splitPanel setSecondComponent languagesTable
          case "Properties" => splitPanel setSecondComponent propertiesTable
          case "About" => splitPanel setSecondComponent labelAbout
          case "Documents" => splitPanel setSecondComponent documentsTable
          case "Roles" => splitPanel setSecondComponent roles
          case "Users" => splitPanel setSecondComponent users

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

  // Languages panel
  def languagesTable = {
    val languageDao = Imcms.getSpringBean("languageDao").asInstanceOf[LanguageDao]
    val systemDao = Imcms.getSpringBean("systemDao").asInstanceOf[SystemDao]
    
    class ModalWindow(caption: String) extends Window(caption, new FormLayout) {
      setModal(true)  
    }
    
    class LanguageModalWindow(caption: String) extends ModalWindow(caption) {
      val txtId = new TextField("Id")
      val txtCode = new TextField("Code")
      val txtName = new TextField("Name")
      val txtNativeName = new TextField("Native name")
      val chkEnabled = new CheckBox("Enabled")

      addComponent(txtId)
      addComponent(txtCode)
      addComponent(txtName)
      addComponent(txtNativeName)
      addComponent(chkEnabled)

      val lytControls = new HorizontalLayout
      val btnOk = new Button("Save")
      val btnCancel = new Button("Cancel")

      lytControls addComponent btnOk
      lytControls addComponent btnCancel

      addComponent(lytControls)

      btnCancel addListener new Button.ClickListener {
        def buttonClick(clickEvent: Button#ClickEvent) = close
      }

      getContent.asInstanceOf[FormLayout].setMargin(true)
    }

    class ConfirmationModalWindow(caption: String, msg: String) extends Window(caption) {
      val lblMsg = new Label(msg)
      val lytControls = new HorizontalLayout
      val btnOk = new Button("Ok")
      val btnCancel = new Button("Cancel")

      lytControls addComponent btnOk
      lytControls addComponent btnCancel

      addComponent(lblMsg)
      addComponent(lytControls)

      btnCancel addListener new Button.ClickListener {
        def buttonClick(clickEvent: Button#ClickEvent) = close
      }

      setModal(true)
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

    val pnlControls = new Panel with Button.ClickListener {
      val btnNew = new Button("New")
      val btnEdit = new Button("Edit")
      val btnSetDefault = new Button("Set default")
      val btnDelete = new Button("Delete")

      setContent(new HorizontalLayout)

      List(btnNew, btnEdit, btnSetDefault, btnDelete).foreach { btn =>
        this addComponent btn
        btn addListener this
      }

      def buttonClick(clickEvent: Button#ClickEvent) {
        val defaultLanguageId = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt

        clickEvent.getButton match {

          case `btnNew` =>
            val wndEditLanguage = new LanguageModalWindow("New language")
            val language = new com.imcode.imcms.api.I18nLanguage

            wndEditLanguage.btnOk.addListener(new Button.ClickListener {
              def isInt(x:Any) = x match {
                case n: Int => true
                case s: String => s.nonEmpty && s.forall(_.isDigit)
                case _ => false
              }

              def buttonClick(clickEvent: Button#ClickEvent) {
                if (!isInt(wndEditLanguage.txtId.getValue)) {
                  wndEditLanguage.txtId.setComponentError(new UserError("Id must be an Int"))  
                } else {
                  language.setId(Int box wndEditLanguage.txtId.getValue.asInstanceOf[String].toInt)
                  language.setCode(wndEditLanguage.txtCode.getValue.asInstanceOf[String])
                  language.setName(wndEditLanguage.txtName.getValue.asInstanceOf[String])
                  language.setNativeName(wndEditLanguage.txtNativeName.getValue.asInstanceOf[String])
                  language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[java.lang.Boolean])

                  languageDao.saveLanguage(language)
                  wndMain removeWindow wndEditLanguage
                }
              }
            })

            wndMain addWindow wndEditLanguage

          case `btnEdit` =>
            val languageId = table.getValue.asInstanceOf[java.lang.Integer]
            val language = languageDao.getById(languageId)

            val wndEditLanguage = new LanguageModalWindow("Edit language")

            wndEditLanguage.txtId.setValue(language.getId)
            wndEditLanguage.txtId.setEnabled(false)
            wndEditLanguage.txtCode.setValue(language.getCode)
            wndEditLanguage.txtName.setValue(language.getName)
            wndEditLanguage.txtNativeName.setValue(language.getNativeName)
            wndEditLanguage.chkEnabled.setValue(language.isEnabled)
            wndEditLanguage.btnOk.addListener(new Button.ClickListener {


              def buttonClick(clickEvent: Button#ClickEvent) {
                language.setCode(wndEditLanguage.txtCode.getValue.asInstanceOf[String])
                language.setName(wndEditLanguage.txtName.getValue.asInstanceOf[String])
                language.setNativeName(wndEditLanguage.txtNativeName.getValue.asInstanceOf[String])
                language.setEnabled(wndEditLanguage.chkEnabled.getValue.asInstanceOf[java.lang.Boolean])

                languageDao.saveLanguage(language)
                wndMain removeWindow wndEditLanguage
              }
            })

            wndMain addWindow wndEditLanguage

          case `btnSetDefault` =>
            val wndConfirmation = new ConfirmationModalWindow("Confirmation", "Change default language?")
            wndConfirmation.btnOk.addListener(new Button.ClickListener {
              def buttonClick(clickEvent: Button#ClickEvent) {
                val languageId = table.getValue.asInstanceOf[java.lang.Integer]
                val property = systemDao.getProperty("DefaultLanguageId")

                property.setValue(languageId.toString)
                systemDao.saveProperty(property)
              }
            })

            wndMain addWindow wndConfirmation

          case `btnDelete` =>
            val wndConfirmation = new ConfirmationModalWindow("Confirmation", "Delete language from the system?")
            wndConfirmation.btnOk.addListener(new Button.ClickListener {
              def buttonClick(clickEvent: Button#ClickEvent) {
                val languageId = table.getValue.asInstanceOf[java.lang.Integer]
                languageDao.deleteLanguage(languageId)
              }
            })
            wndMain addWindow wndConfirmation
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
    
    table.addListener(new Property.ValueChangeListener {
        def valueChange(e: ValueChangeEvent) {
          resetControls  
        }
    })

    val defaultLanguageId = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt

    languageDao.getAllLanguages.toList foreach { language =>
      table.addItem(Array(language.getId, language.getCode, language.getName,
                          language.getNativeName, language.isEnabled,
                          Boolean box (language.getId == defaultLanguageId)),
                    language.getId)      
    }

    pnlLanguages addComponent pnlControls
    pnlLanguages addComponent table

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


    val controls = new GridLayout(5,1)
    
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

    val table = new Table()
    table.addContainerProperty("Id", classOf[java.lang.Integer],  null)
    table.addContainerProperty("Name", classOf[String],  null)

    roleMapper.getAllRoles.foreach { role =>
      table.addItem(Array(Int box role.getId.intValue, role.getName), role)
    }

    table
  }


  def users = {
    def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

    val table = new Table()

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
}