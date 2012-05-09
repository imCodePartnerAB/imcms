package com.imcode
package imcms.admin.chat

import scala.collection.JavaConversions._

import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
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
import imcode.server.document.{CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import com.imcode.imcms.vaadin._
import com.vaadin.ui._
;

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
    getComponentIterator.toList |> { components =>
      if (components.length > 3) removeComponent(components.head)
    }

    addComponent(msg)
  }
  
}

class Chat extends VerticalLayout {
  val pnlMessages = new MessagesPanel
  val txaText = new TextArea() |>> { t => t.setRows(3); t.setSizeFull }
  val btnSend = new Button("Send") { setHeight("100%") }

  val lytMessage = new HorizontalLayout {
    addComponents(this, txaText, btnSend)
    setExpandRatio(txaText, 1.0f)
    setWidth("100%")
    setHeight("50px")
  }

  setSpacing(true)
  addComponents(this, pnlMessages, lytMessage)
  setExpandRatio(pnlMessages, 1.0f)
  setSizeFull
}

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