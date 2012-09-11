package com.imcode
package imcms
package docadmin

import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ImcmsApplication
import java.util.Locale
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import imcode.server.document.textdocument.{MenuDomainObject, TextDocumentDomainObject}
import imcode.server.{ImcmsConstants}
import com.vaadin.terminal.gwt.server.HttpServletRequestListener
import java.util.concurrent.ConcurrentMap
import com.vaadin.ui._
import com.vaadin.terminal.ExternalResource
import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.admin.doc.content.filedoc.FileDocContentEditor
import imcode.server.document.{UrlDocumentDomainObject, HtmlDocumentDomainObject, FileDocumentDomainObject}
import com.imcode.imcms.admin.doc.content.{UnsupportedDocContentEditor, URLDocContentEditor, HTMLDocContentEditor, TextDocContentEditor}

class DocAdminApplication extends com.vaadin.Application with HttpServletRequestListener with ImcmsApplication with ImcmsServicesSupport { app =>

  // extractors
  private object PathHandlers {
    private val InfoRe = """meta-0*(\d+)""".r
    private val MenuRe = """menu-0*(\d+)-0*(\d+)-0*(\d+)""".r
    private val TextRe = """text-0*(\d+)-0*(\d+)-0*(\d+)""".r

    object Properties {
      def unapply(value: String): Option[Int] = value match {
        case InfoRe(IntNum(docId)) => Some(docId)
        case _ => None
      }
    }

    object Menu {
      def unapply(value: String): Option[(Int, Int, Int)] = value match {
        case MenuRe(IntNum(docId), IntNum(docVersion), IntNum(menuNo)) => Some(docId, docVersion, menuNo)
        case _ => None
      }
    }

    object Text {
      def unapply(value: String): Option[(Int, Int, Int)] = value match {
        case TextRe(IntNum(docId), IntNum(docVersion), IntNum(textNo)) => Some(docId, docVersion, textNo)
        case _ => None
      }
    }
  }

  val mainWindow = new Window {
  }

  override def getWindow(name: String): Window = super.getWindow(name) match {
    case window if window != null => window
    case _ =>
      name |> {
        case PathHandlers.Properties(docId) => mkPropertiesWindow(docId)
        case PathHandlers.Menu(docId, docVersionNo, menuNo) => mkMenuWindow(docId)
        case PathHandlers.Text(docId, docVersionNo, textNo) => mkTextWindow(docId)
        case _ => null
      } |>> {
        case null =>
        case window =>
          window.setName(name)
          addWindow(window)
      }
  }



  def mkPropertiesWindow(docId: Int): Window = new Window |>> { wnd =>
    val doc = app.imcmsServices.getDocumentMapper.getDocument(docId)
    val pnlProperties = new Panel with FullSize {
      val metaEditor = new MetaEditor(doc)
      val contentEditor = doc match {
        case textDoc: TextDocumentDomainObject => new TextDocContentEditor(textDoc)
        case fileDoc: FileDocumentDomainObject => new FileDocContentEditor(fileDoc)
        case htmlDoc: HtmlDocumentDomainObject => new HTMLDocContentEditor(htmlDoc)
        case urlDoc: UrlDocumentDomainObject => new URLDocContentEditor(urlDoc)
        case _ => new UnsupportedDocContentEditor(doc)
      }

      val tsProperties = new TabSheet with FullSize |>> { ts =>
        ts.addTab(metaEditor.ui, "Properties", null)
        ts.addTab(contentEditor.ui, "Content", null)
      }

      setContent(tsProperties)
    }

    // ? add close listener - if closed outside
    wnd.setContent(pnlProperties)
  }

  def mkMenuWindow(docId: Int): Window = new Window |>> { wnd =>
    wnd.addComponent(new Button("menu"))
    wnd.addComponent(new Button("save"))
  }

  def mkTextWindow(docId: Int): Window = new Window |>> { wnd =>
    wnd.addComponent(new Button("text"))
    wnd.addComponent(new Button("save"))
  }

  def init() {
    setTheme("imcms")
//    setLocale(new Locale(user.getLanguageIso639_2))
    setMainWindow(mainWindow)
  }

  def onRequestStart(request: HttpServletRequest, response: HttpServletResponse) {
    println("[Start of request");
    println(" Query string: " + request.getQueryString)
    println(" Path: " + request.getPathInfo)
    println(" URI: " + request.getRequestURI)
    println(" URL: " + request.getRequestURL.toString)

//    for {
//      (IntNum(docId), IntNum(menuNo)) <- Option(request.getParameter("doc_id"), request.getParameter("menu_no"))
//      doc @ (si_900 : TextDocumentDomainObject) <- Option(imcmsServices.getDocumentMapper.getDocument(docId))
//      menu <- Option(doc.getMenu(menuNo))
//    } {
//      val pnlAdmin = new Panel
//      pnlAdmin.setSize(600, 600)
//
//      val menuEditor = new MenuEditor(doc, menu)
//      mainWindow.getContent.asInstanceOf[VerticalLayout] |> { lyt =>
//        lyt.removeAllComponents()
//        lyt.addComponent(menuEditor.ui)
//        lyt.asInstanceOf[VerticalLayout].setComponentAlignment(menuEditor.ui, Alignment.MIDDLE_CENTER)
//      }

      //setLogoutURL("/test.jsp")
      //"AdminDoc?meta_id=" + parentDocument.getId + "&flags=" + ImcmsConstants.DISPATCH_FLAG__EDIT_MENU + "&editmenu=" + parentMenuIndex
//    }
  }

  def onRequestEnd(request: HttpServletRequest, response: HttpServletResponse) {
    println(" End of request]")
  }
}


class MenuEditor(doc: TextDocumentDomainObject, menu: MenuDomainObject) {
  val ui = new MenuEditorUI |>> { ui =>
    menu.getMenuItems foreach { ui.treeMenu.addItem(_) }
  }
}

class MenuEditorUI extends VerticalLayout with Spacing with Margin with UndefinedSize {
  val mb = new MenuBar
  val miAdd = mb.addItem("Add item")
  val miCopy = mb.addItem("Copy item(s)")
  val miDelete = mb.addItem("Remove item")

  val miAddDoc = miAdd.addItem("Document")
  val miAddNewDoc = miAdd.addItem("New document")

  val treeMenu = new Tree

  addComponents(this, mb, treeMenu)
}