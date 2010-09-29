package com.imcode.imcms.servlet.superadmin.vaadin.filemanager

import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger, Long => JLong}
import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.api.{CategoryType, SystemProperty, IPAccess, Document}
import imcms.mapping.CategoryMapper
import imcms.servlet.superadmin.AdminSearchTerms
import com.imcode.imcms.api.Document.PublicationStatus
import com.vaadin.terminal.UserError
import imcode.util.Utility
import imcode.server.user._
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.vaadin.ui.Layout.MarginInfo
import java.io.{OutputStream, FileOutputStream, File}
import com.imcode.imcms.servlet.superadmin.vaadin.ui._
import com.imcode.imcms.servlet.superadmin.vaadin.ui.AbstractFieldWrapper._
import java.util.concurrent.atomic.AtomicReference
import imcode.server.document.{CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import scala.actors.Actor._
import scala.actors._
import imcode.server.document.textdocument.TextDocumentDomainObject

class MessageView(sender: String, message: String) extends VerticalLayout {
  val lytHeader = new HorizontalLayout {
    val lblSender = new Label(sender) {setWidth("100%")}
    val lblDetails = new Label("Sent: " + (new Date).toString)

    addComponents(this, lblSender, lblDetails)
    //setExpandRatio(lblSender, 1.0f)

    setWidth("100%")
    setHeight(null)
    setSpacing(true)
  }

  val lblText = new Label(message) {setWidth("100%")}

  addComponents(this, lytHeader, lblText)
  setWidth("100%")
  setHeight(null)
}

class MessagesPanel extends Panel(new VerticalLayout{setSpacing(true)}) {
  //setStyle(Panel.STYLE_LIGHT)

  setScrollable(true)
  setSizeFull

  def addMessage(msg: MessageView) = synchronized {
    let(getComponentIterator.toList) {components =>
      if (components.length > 3) removeComponent(components.head)
    }

    addComponent(msg)
  }
  
}

class Chat extends VerticalLayout {
  val pnlMessages = new MessagesPanel
  val txtText = new TextField{setRows(3); setSizeFull}
  val btnSend = new Button("Send") {setHeight("100%")}

  val lytMessage = new HorizontalLayout {
    addComponents(this, txtText, btnSend)
    setExpandRatio(txtText, 1.0f)
    setWidth("100%")
    setHeight("50px")
  }

  setSpacing(true)
  addComponents(this, pnlMessages, lytMessage)
  setExpandRatio(pnlMessages, 1.0f)
  setSizeFull
}