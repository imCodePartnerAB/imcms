// DOCADMIN APP

package com.imcode.imcms.servlet.superadmin.vaadin

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
import imcms.api.{CategoryType, SystemProperty, IPAccess, Document}
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
import com.vaadin.terminal.{ExternalResource, ThemeResource, UserError}
import java.net.URL

// RAW PROTOTYPE, everything is HARDCODED
class Docadmin extends com.vaadin.Application {

  lazy val toolbar = new VerticalLayout { tb =>
    val btnTexts = new Button("Texts")
    val btnImages = new Button("Images")
    val btnMenus = new Button("Menus")
    val btnContentLoops = new Button("Content loops")

    val lytTextDocBar = new HorizontalLayout {
      setSpacing(true)
      addComponents(this, btnTexts, btnImages, btnMenus, btnContentLoops)
    }

    addComponents(this,
      lytTextDocBar,
      letret(new Button("more...")) { btnMore =>
        btnMore addListener unit {
          tb addComponent new HorizontalLayout { bar =>
            setSpacing(true)
            1 to 10 foreach { i=>
              bar addComponent letret(new Button("Some action " + i)) { btnAction =>
                btnAction addListener unit {
                  tb removeComponent bar
                }
              }
            }
          }
        }
      })

    setMargin(true)
    setSpacing(true)

    btnTexts addListener unit {
      initAndShow(new OkCancelDialog("Lets edit some text...")) { w => }
    }

    btnImages addListener unit {
      initAndShow(new OkCancelDialog("Lets edit some image...")) { w => }
    }    
  }

  lazy val embDocument = new Embedded {
    let(getURL) { url: URL =>
      val urlEmbedded = new URL(url.getProtocol, url.getHost, url.getPort, "/")
      setSource(new ExternalResource(urlEmbedded))
    }

    setType(Embedded.TYPE_BROWSER)
    setSizeFull
  }

  lazy val wndMain = new Window {
    val lytContent = new GridLayout(1,2) {
      setRowExpandRatio(1, 1.0f)
      setSizeFull
    }

    addComponents(lytContent, toolbar, embDocument)

    setContent(lytContent)
  }

  def init = setMainWindow(wndMain)

  def initAndShow[W <: Window](window: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true)(init: W => Unit) {
    init(window)
    window setModal modal
    window setResizable resizable
    window setDraggable draggable
    wndMain addWindow window
  }  
}

