package com.imcode.imcms.servlet.superadmin.vaadin;

import scala.collection.JavaConversions._
import clojure.lang.RT
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property._
import imcode.server.Imcms
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao}
import imcode.server.document.DocumentDomainObject
import com.imcode.imcms.api.{Document}
import com.imcode.imcms.api.Document.PublicationStatus
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper

class App extends com.vaadin.Application {

  case class Node(nodes: Node*)

  val labelNA = new Label("Not Available");

  val labelAbout = new Label("""|Welcome to the imCMS new admin UI prototype -
                                | please pick a task from the menu.
                                |""".stripMargin)


  def init {
    val window = new Window
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

    window setContent splitPanel
    
    this setMainWindow window
  }

  
  def languagesTable = {
    val table = new Table("Languages")
    table.addContainerProperty("Id", classOf[java.lang.Integer],  null)
    table.addContainerProperty("Code", classOf[String],  null)
    table.addContainerProperty("Name", classOf[String],  null)
    table.addContainerProperty("Native name", classOf[String],  null)
    table.addContainerProperty("Enabled", classOf[java.lang.Boolean],  null)

    val languageDao = Imcms.getSpringBean("languageDao").asInstanceOf[LanguageDao]

    languageDao.getAllLanguages.toList foreach { language =>
      table.addItem(Array(language.getId, language.getCode, language.getName,
                          language.getNativeName, language.isEnabled),
                    language.getId)      
    }

    table
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