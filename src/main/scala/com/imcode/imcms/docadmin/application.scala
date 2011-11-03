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
import com.vaadin.ui._

class Application extends com.vaadin.Application with HttpServletRequestListener with ImcmsApplication with ImcmsServicesSupport { app =>

  val wndMain = new Window {
  }

  def init {
    setTheme("imcms")
    setLocale(new Locale(user.getLanguageIso639_2))
    setMainWindow(wndMain)
  }

  def onRequestStart(request: HttpServletRequest, response: HttpServletResponse) {
    println("[Start of request");
    println(" Query string: " + request.getQueryString)
    println(" Path: " + request.getPathInfo)
    println(" URI: " + request.getRequestURI)
    println(" URL: " + request.getRequestURL.toString)

    for {
      (IntNumber(docId), IntNumber(menuNo)) <- Option(request.getParameter("doc_id"), request.getParameter("menu_no"))
      doc @ (si_900 : TextDocumentDomainObject) <- ?(imcmsServices.getDocumentMapper.getDocument(docId))
      menu <- ?(doc.getMenu(menuNo))
    } {
      val pnlAdmin = new Panel
      pnlAdmin.setSize(600, 600)

      val menuEditor = new MenuEditor(doc, menu)
      let(wndMain.getContent.asInstanceOf[VerticalLayout]) { lyt =>
        lyt.removeAllComponents()
        lyt.addComponent(menuEditor.ui)
        lyt.asInstanceOf[VerticalLayout].setComponentAlignment(menuEditor.ui, Alignment.MIDDLE_CENTER)
      }

      //setLogoutURL("/test.jsp")
      //"AdminDoc?meta_id=" + parentDocument.getId + "&flags=" + ImcmsConstants.DISPATCH_FLAG__EDIT_MENU + "&editmenu=" + parentMenuIndex
    }
  }

  def onRequestEnd(request: HttpServletRequest, response: HttpServletResponse) {
    println(" End of request]")
  }
}


class MenuEditor(doc: TextDocumentDomainObject, menu: MenuDomainObject) {
  val ui = letret(new MenuEditorUI) { ui =>
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