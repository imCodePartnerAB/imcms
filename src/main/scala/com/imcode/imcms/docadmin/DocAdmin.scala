package com.imcode
package imcms
package docadmin

import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ImcmsApplication
import java.util.Locale
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import imcode.server.{ImcmsConstants}
import com.vaadin.terminal.gwt.server.HttpServletRequestListener
import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.admin.doc.content.filedoc.FileDocContentEditor
import scala.collection.JavaConverters._
import com.imcode.imcms.admin.doc.content._
import com.imcode.imcms.vaadin.ui._
import imcode.server.document.textdocument.{DocRef, MenuDomainObject, TextDocumentDomainObject}
import com.imcode.imcms.admin.access.user.UserSelectDialog
import com.vaadin.ui._
import imcode.server.document._
import com.imcode.imcms.vaadin.ui.dialog.{NoMarginDialog, OKDialog, CustomSizeDialog, OkCancelDialog}
import com.imcode.imcms.admin.doc.search.{DocsProjectionDialog}
import com.imcode.imcms.admin.doc.DocEditor

class DocAdmin extends com.vaadin.Application with HttpServletRequestListener with ImcmsApplication with ImcmsServicesSupport { app =>

  // extractors
  private object PathHandlers {
    private val DefaultDocRe = """0*(\d+)""".r
    private val CustomDocRe = """0*(\d+)-0*(\d+)""".r
    private val MenuRe = """menu-0*(\d+)-0*(\d+)-0*(\d+)""".r
    private val TextRe = """text-0*(\d+)-0*(\d+)-0*(\d+)""".r

    object DefaultDoc {
      def unapply(value: String): Option[Int] = PartialFunction.condOpt(value) {
        case DefaultDocRe(IntNum(docId)) => docId
      }
    }

    object CustomDoc {
      def unapply(value: String): Option[(Int, Int)] = PartialFunction.condOpt(value) {
        case CustomDocRe(IntNum(docId), IntNum(docVersionNo)) => (docId, docVersionNo)
      }
    }


    object Menu {
      def unapply(value: String): Option[(Int, Int, Int)] = PartialFunction.condOpt(value) {
        case MenuRe(IntNum(docId), IntNum(docVersion), IntNum(menuNo)) => (docId, docVersion, menuNo)
      }
    }

    object Text {
      def unapply(value: String): Option[(Int, Int, Int)] = PartialFunction.condOpt(value) {
        case TextRe(IntNum(docId), IntNum(docVersion), IntNum(textNo)) => (docId, docVersion, textNo)
      }
    }
  }

  val mainWindow = new Window {
  }

  override def getWindow(name: String): Window = super.getWindow(name) match {
    case window if window != null => window
    case _ =>
      name |> {
        case PathHandlers.DefaultDoc(docId) => mkDocEditorWindow(docId)
        case PathHandlers.CustomDoc(docId, docVersionNo) => mkDocEditorWindow(docId)
        case PathHandlers.Menu(docId, docVersionNo, menuNo) => mkMenuEditorWindow(DocRef.of(docId, docVersionNo), menuNo)
        case PathHandlers.Text(docId, docVersionNo, textNo) => mkTextEditorWindow(docId)
        case _ => null
      } |>> { window =>
        if (window != null) {
          window.setName(name)
          addWindow(window)
        }
      }
  }


  def mkDocEditorWindow(docId: Int): Window = new Window |>> { wnd =>
    val doc = app.imcmsServices.getDocumentMapper.getDocument(docId)
    val lytEditor = new VerticalLayout with Spacing with Margin with FullHeight |>> { lyt =>
      lyt.setWidth("800px")
    }

    val lblTitle = new Label("Document " + docId) with UndefinedSize
    val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
      val btnClose = new Button("Close")
      val btnSave = new Button("Save")

      addComponentsTo(this, btnClose, btnSave)
    }

    val docEditor = new DocEditor(doc)

    lytEditor.addComponent(lblTitle)
    lytEditor.addComponent(docEditor.ui)
    lytEditor.addComponent(lytButtons)
    lytEditor.setExpandRatio(docEditor.ui, 1.0f)
    lytEditor.setComponentAlignment(lytButtons, Alignment.MIDDLE_CENTER)

    val windowContent = new VerticalLayout with FullSize
    windowContent.addComponent(lytEditor)
    windowContent.setComponentAlignment(lytEditor, Alignment.MIDDLE_CENTER)

    wnd.setContent(windowContent)

    lytButtons.btnSave.addClickHandler {
      (docEditor.metaEditor.collectValues(), docEditor.contentEditor.collectValues()) match {
        case (Left(errorMsgs), _) =>
          wnd.showErrorNotification(errorMsgs.mkString(","))

        case (_, Left(errorMsgs)) =>
          wnd.showErrorNotification(errorMsgs.mkString(","))

        case (Right((metaDoc, i18nMetas)), Right(doc)) =>
          doc.setMeta(metaDoc.getMeta)

          try {
            imcmsServices.getDocumentMapper.saveDocument(doc, i18nMetas.asJava, app.user())
            wnd.showInfoNotification("Document has been saved")
          } catch {
            case e => wnd.showErrorNotification("Failed to save document", e.getStackTraceString)
          }
      }
    }
  }


  def mkMenuEditorWindow(docRef: DocRef, menuNo: Int): Window = new Window |>> { wnd =>
    val doc = app.imcmsServices.getDocumentMapper.getCustomDocument(docRef).asInstanceOf[TextDocumentDomainObject]
    val menu = doc.getMenu(menuNo)
    val menuEditor = new MenuEditor(doc, menu) |>> { me => me.ui.setSizeFull() }
    val pnlEditor = new Panel("Edit document %d menu no %d") with FullSize |>> { _.setWidth("800px") }
    val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
      val btnClose = new Button("Close")
      val btnSave = new Button("Save")

      addComponentsTo(this, btnClose, btnSave)
    }

    pnlEditor.setContent(menuEditor.ui)

    val wndContent = new VerticalLayout with MiddleCenterAlignment with Spacing with Margin with FullSize

    addComponentsTo(wndContent, pnlEditor, lytButtons)
    wndContent.setExpandRatio(pnlEditor, 1f)

    wnd.setContent(wndContent)

  }

  def mkTextEditorWindow(docId: Int): Window = new Window |>> { wnd =>
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
    menu.getMenuItems.foreach { ui.treeMenu.addItem(_) }

    ui.miAdd.setCommandHandler {
      ui.topWindow.initAndShow(new OkCancelDialog("Choose documents") with DocsProjectionDialog, resizable = true) { dlg =>
        dlg.setOkHandler {
           dlg.projection.selection.foreach(ui.treeMenu.addItem)
        }

        dlg.setSize(500, 600)
      }
    }
  }
}

class MenuEditorUI extends VerticalLayout with Spacing with Margin with FullSize {
  val mb = new MenuBar
  val miAdd = mb.addItem("Add docs to menu")
  val miRemove = mb.addItem("Remove docs from menu")

  val treeMenu = new Tree("Menu")

  addComponentsTo(this, mb, treeMenu)
  setExpandRatio(treeMenu, 1f)
}