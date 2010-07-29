package com.imcode.imcms.servlet.superadmin.vaadin;

import scala.collection.JavaConversions._
import clojure.lang.RT
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property._
import imcode.server.Imcms
import com.imcode.imcms.dao.{SystemDao, LanguageDao}

class App extends com.vaadin.Application {

  def init {
    val window = new Window
    val splitPanel = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL)
    val tree = new Tree

    val treeItems = List(
      "About" -> Nil,
      "System" -> List("Languages", "Properties"),
      "Document" -> List("New", "Search"),
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
    }

    window setContent splitPanel

    splitPanel setFirstComponent tree

    tree addListener (new ValueChangeListener {
      def valueChange(e: ValueChangeEvent) {
        println(">>> ", e.getProperty.getValue)

        e.getProperty.getValue.asInstanceOf[String] match {
          case "Languages" => splitPanel setSecondComponent languagesTable
          case "Properties" => splitPanel setSecondComponent propertiesTable

          case _ => splitPanel setSecondComponent (new Label("N/A"))
        }
      }
    })

    tree setImmediate true

    this setMainWindow window

    splitPanel setSecondComponent languagesTable
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
}