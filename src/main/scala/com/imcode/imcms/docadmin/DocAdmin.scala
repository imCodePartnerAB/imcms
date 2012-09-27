package com.imcode
package imcms
package docadmin

import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ImcmsApplication
import java.util.Locale
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import imcode.server.{ImcmsConstants}
import com.vaadin.terminal.gwt.server.HttpServletRequestListener
import java.util.concurrent.ConcurrentMap
import com.vaadin.terminal.ExternalResource
import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.admin.doc.content.filedoc.FileDocContentEditor
import imcode.server.document.{UrlDocumentDomainObject, HtmlDocumentDomainObject, FileDocumentDomainObject}
import scala.collection.JavaConverters._
import com.imcode.imcms.admin.doc.content._
import com.imcode.imcms.vaadin.ui._
import imcode.server.document.textdocument.{DocRef, MenuDomainObject, TextDocumentDomainObject}
import com.imcode.imcms.admin.access.user.UserSelectDialog
import com.imcode.imcms.vaadin.ui.dialog.{CustomSizeDialog, OkCancelDialog}
import com.imcode.imcms.admin.doc.search.{DocsProjectionUI, AllDocsContainer, DocsProjection}
import com.vaadin.ui._

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
    val lytProperties = new VerticalLayout with Spacing with Margin with FullHeight |>> { lyt =>
      lyt.setWidth("800px")
    }

    val lblCaption = new Label("Document " + docId) with UndefinedSize
    val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
      val btnClose = new Button("Close")
      val btnSave = new Button("Save")

      addComponentsTo(this, btnClose, btnSave)
    }

    val metaEditor = new MetaEditor(doc)
    val contentEditor = doc match {
      //case textDoc: TextDocumentDomainObject => //new TextDocContentEditor(textDoc)
      case fileDoc: FileDocumentDomainObject => new FileDocContentEditor(fileDoc)
      case urlDoc: UrlDocumentDomainObject => new UrlDocContentEditor(urlDoc)
      case _ => new UnsupportedDocContentEditor(doc)
    }

    val tsProperties = new TabSheet with FullSize |>> { ts =>
      ts.addTab(metaEditor.ui, "Properties", null)
      ts.addTab(contentEditor.ui, "Content", null)
    }

    lytProperties.addComponent(lblCaption)
    lytProperties.addComponent(tsProperties)
    lytProperties.addComponent(lytButtons)
    lytProperties.setExpandRatio(tsProperties, 1.0f)
    lytProperties.setComponentAlignment(lytButtons, Alignment.MIDDLE_CENTER)

    val lytWindow = new VerticalLayout with FullSize
    lytWindow.addComponent(lytProperties)
    lytWindow.setComponentAlignment(lytProperties, Alignment.MIDDLE_CENTER)

    wnd.setContent(lytWindow)

    lytButtons.btnSave.addClickHandler {
      (metaEditor.collectValues(), contentEditor.collectValues()) match {
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



trait DocsProjectionDialog extends CustomSizeDialog { this: OkCancelDialog =>
  val projection = new DocsProjection(new AllDocsContainer)

  mainUI = new DocsProjectionDialogMainUI(projection.ui) |>> { ui =>
    ui.miNewFileDoc.setCommandHandler {}
    ui.miNewTextDoc.setCommandHandler {}
    ui.miNewUrlDoc.setCommandHandler {}

    ui.miCopyDoc.setCommandHandler {}
    ui.miDeleteDoc.setCommandHandler {}
    ui.miViewDoc.setCommandHandler {}

    projection.listen { selection =>
      doto(ui.miNewFileDoc, ui.miNewTextDoc, ui.miNewUrlDoc, ui.miViewDoc, ui.miCopyDoc) { mi =>
        mi.setEnabled(selection.size == 1)
      }

      ui.miDeleteDoc.setEnabled(selection.nonEmpty)
    }
  }

  projection.listen { selection =>
    btnOk.setEnabled(selection.nonEmpty)
  }

  projection.notifyListeners()
}


class DocsProjectionDialogMainUI(docsProjectionUI: DocsProjectionUI) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miNew = mb.addItem("doc.mgr.mi.new".i)
  val miNewTextDoc = miNew.addItem("doc.mgr.mi.new.text_doc".i)
  val miNewFileDoc = miNew.addItem("doc.mgr.mi.new.file_doc".i)
  val miNewUrlDoc = miNew.addItem("doc.mgr.mi.new.url_doc".i)

  val miCopyDoc = mb.addItem("doc.mgr.mi.copy".i)
  val miDeleteDoc = mb.addItem("doc.mgr.action.delete".i)

  val miViewDoc = mb.addItem("doc.mgr.mi.view".i)

  addComponentsTo(this, mb, docsProjectionUI)
  setExpandRatio(docsProjectionUI, 1f)
}

