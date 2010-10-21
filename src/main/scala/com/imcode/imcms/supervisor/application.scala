package com.imcode.imcms.supervisor

import com.imcode.imcms.servlet.superadmin.vaadin.permissions._
import com.imcode.imcms.servlet.superadmin.vaadin.filemanager._
import com.imcode.imcms.servlet.superadmin.vaadin.template._
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
import imcms.api.TextDocument.MenuItem
import imcms.mapping.CategoryMapper
import imcms.servlet.superadmin.AdminSearchTerms
import com.imcode.imcms.api.Document.PublicationStatus
import imcms.servlet.superadmin.vaadin.ChatTopic.Message
import imcode.util.Utility
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.vaadin.ui.Layout.MarginInfo
import com.imcode.imcms.servlet.superadmin.vaadin.ui._
import com.imcode.imcms.servlet.superadmin.vaadin.ui.AbstractFieldWrapper._
import java.util.concurrent.atomic.AtomicReference
import scala.actors.Actor._
import scala.actors._
import imcode.server.document.textdocument.TextDocumentDomainObject
import imcode.server.document.{TemplateDomainObject, CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import java.io.{ByteArrayInputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}


// todo: borwser, repl server, swank server

//    if (repl != null) {
//        ClojureUtils.startReplServer(Integer.parseInt(repl));
//    }
//
//
//    String swank = request.getParameter("swank");
//
//    if (swank != null) {
//        ClojureUtils.startSwankServer(Integer.parseInt(swank));
//    }

class Application extends VaadinApplication {

  lazy val pnlStatus = new Panel("Status") {
    val lytContent = new FormLayout
    
    val lblMode = new Label("Mode")
    val lblRunning = new Label("Running")
    val lblStartupError = new Label("Startup error")

    addComponents(lytContent, lblMode, lblRunning, lblStartupError)
    setContent(lytContent)
  }

  lazy val mbMain = new MenuBar {
    val miStartImcms = addItem("Start imCMS", null)
    val miStopImcms = addItem("Start imCMS", null)
    val miNoramlMode = addItem("Normal mode", null)
    val miMaintenaceMode = addItem("Maintenace mode", null)
    val miReloadStatus = addItem("Reload status", null)
  }

  lazy val wndMain = new Window("imCMS Supervisor") {
    addComponents(this, mbMain, pnlStatus)
  }

  def init {
    setMainWindow(wndMain)

    val command = menuCommand({ mi =>
      mi match {
        case mbMain.miStartImcms => Imcms.start
        case mbMain.miStopImcms => Imcms.stop
        case mbMain.miNoramlMode => Imcms.setNormalMode
        case mbMain.miMaintenaceMode => Imcms.setMaintenanceMode
        case _ =>
      }

      reloadStatus()
    })

    mbMain.getItems foreach (_ setCommand command)
    reloadStatus()
  }

  def reloadStatus() {
    let(pnlStatus) { s =>
      s.lblMode setValue Imcms.getMode
      s.lblRunning.setValue(if (Imcms.getServices() == null) "NO" else "YES")
      s.lblStartupError.setValue(if (Imcms.getStartEx == null) "NO ERROR" else Imcms.getStartEx) 
    }
  }
}