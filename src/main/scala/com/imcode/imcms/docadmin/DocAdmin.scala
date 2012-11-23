package com.imcode
package imcms
package docadmin

import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ImcmsApplication
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import com.vaadin.terminal.gwt.server.HttpServletRequestListener
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog.{OkCancelDialog}
import com.imcode.imcms.admin.doc.search.{DocsProjectionDialog}
import com.imcode.imcms.admin.doc.DocEditor
import com.vaadin.event.dd.{DragAndDropEvent, DropHandler}
import com.vaadin.event.dd.acceptcriteria.{AcceptAll, AcceptCriterion}
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation
import com.vaadin.ui.Tree.TreeDragMode
import com.vaadin.data.Container
import com.vaadin.event.DataBoundTransferable
import com.vaadin.data.util.{HierarchicalContainer, ContainerHierarchicalWrapper}
import com.vaadin.ui._
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator
import api.Document
import com.vaadin.terminal.{FileResource, ExternalResource, Resource}
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import imcode.server.document.textdocument._

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
      docEditor.collectValues() match {
        case Left(errors) => wnd.showErrorNotification(errors.mkString(","))
        case Right((editedDoc, i18nMetas)) =>
          try {
            imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.asJava, app.user)
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


class MenuEditor(doc: TextDocumentDomainObject, menu: MenuDomainObject) extends ImcmsServicesSupport {


//  menu.getSortOrder match {
//    case MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED =>
//      for (menuItem <- menu.getMenuItems) {
//        automaticallySortedMenuItems.addItem(menuItem.getDocumentId) |> { item =>
//          item.addProperty(PropertyId.Caption, FunctionProperty[String](() => item.getDocument.getHeadline))
//          item.addProperty(PropertyId.Icon, FunctionProperty[String](() => item.getDocument.getHeadline))
//        }
//      }
//
//    case MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER =>
//    case _ =>
//  }

  private def updateTreeMenuUI() {
    val menuSortOrder = menu.getSortOrder
    val childrenAllowed = menu.getSortOrder == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER
    val manualSortAllowed = Set(MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER,
        MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED
    ).contains(menuSortOrder)

    import ui.treeMenu
    treeMenu.removeAllItems()
    treeMenu.setDragMode(if (manualSortAllowed) Tree.TreeDragMode.NODE else Tree.TreeDragMode.NONE)

    for (menuItem <- menu.getMenuItems) {
      val doc = menuItem.getDocument
      val docId = doc.getId
      val caption = docId+": "+doc.getHeadline
      val icon = doc.getPublicationStatus |> {
        case Document.PublicationStatus.APPROVED => "approved.gif"
        case Document.PublicationStatus.DISAPPROVED => "disapproved.gif"
        case Document.PublicationStatus.NEW => "new.gif"
      } |> { filename =>
        val app = ui.getApplication
        val file = new File(app.context.getBaseDirectory, "imcms/eng/images/admin/status/" + filename)

        new FileResource(file, app)
      }

      treeMenu.addItem(docId, caption)
      treeMenu.setItemIcon(docId, icon)
      treeMenu.setChildrenAllowed(docId, childrenAllowed)
    }
  }

  val ui = new MenuEditorUI { ui =>
    ui.treeMenu.setItemDescriptionGenerator(new ItemDescriptionGenerator {
      def generateDescription(source: Component, itemId: AnyRef, propertyId: AnyRef) = "menu item tooltip"
    })

    ui.miAddDocs.setCommandHandler {
      ui.topWindow.initAndShow(new OkCancelDialog("Choose documents") with DocsProjectionDialog, resizable = true) { dlg =>
        dlg.setOkHandler {
           for {
             docId <- dlg.projection.selection
             doc <- imcmsServices.getDocumentMapper.getDefaultDocument(docId) |> opt
           } {
             val docRef = imcmsServices.getDocumentMapper.getDocumentReference(doc)
             val menuItem = new MenuItemDomainObject(docRef)
             menu.addMenuItemUnchecked(menuItem)

             updateTreeMenuUI()
           }
        }

        dlg.setSize(500, 600)
      }
    }

    ui.cbSortOrder.addValueChangeHandler {
      menu.setSortOrder(ui.cbSortOrder.value)
      updateTreeMenuUI()
    }

    override def attach() {
      init()
    }
  }

  private val init: () => Unit = {
    val initialized = new AtomicBoolean(false)

    () => { if (initialized.compareAndSet(false, true)) updateTreeMenuUI() }
  }
}

// Custom UI for sort - a;la doc editor
class MenuEditorUI extends VerticalLayout with Margin with FullSize {
  val mb = new MenuBar with FullWidth
  val miAddDocs = mb.addItem("Add docs")
  val miRemoveDocs = mb.addItem("Remove docs")
  val miHelp = mb.addItem("Help")

  val treeMenu = new Tree with SingleSelect[DocId] with Selectable with Immediate with FullSize |>> { tree =>
    tree.setContainerDataSource(new HierarchicalContainer)
    tree.setDragMode(TreeDragMode.NODE)
    tree.setDropHandler(new DropHandler {
      def getAcceptCriterion: AcceptCriterion = AcceptAll.get()

      def drop(event: DragAndDropEvent) {
        val transferable = event.getTransferable

        if (transferable.getSourceComponent != tree) return

        val target = event.getTargetDetails.asInstanceOf[Tree#TreeTargetDetails]

        val sourceItemId = transferable.asInstanceOf[DataBoundTransferable].getItemId //target.getData("itemId")
        val targetItemId = target.getItemIdOver
        val location = target.getDropLocation
        val container = tree.getContainerDataSource.asInstanceOf[HierarchicalContainer]

        if (location == VerticalDropLocation.MIDDLE) {
          tree.setParent(sourceItemId, targetItemId)
        } else if (location == VerticalDropLocation.TOP) {
          val parentId = container.getParent(targetItemId)
          container.setParent(sourceItemId, parentId)
          container.moveAfterSibling(sourceItemId, targetItemId)
          container.moveAfterSibling(targetItemId, sourceItemId)
        } else if (location == VerticalDropLocation.BOTTOM) {
          // Drop below another item -> make it next
          val parentId = container.getParent(targetItemId)
          container.setParent(sourceItemId, parentId)
          container.moveAfterSibling(sourceItemId, targetItemId)
        }
      }
    })
  }

  private val lytSort = new FormLayout

  val cbSortOrder = new ComboBox("Sort order") with SingleSelect[JInteger] with Immediate with NoNullSelection |>> { cb =>
    Set(
      MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE -> "Title",
      MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED -> "Modified date/time",
      MenuDomainObject.MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED -> "Published date/time",
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED -> "Manual [list]",
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER -> "Manual [tree]"
    ).foreach {
      case (id, caption) => cb.addItem(id: JInteger, caption)
    }
  }

  lytSort.addComponent(cbSortOrder)
  addComponentsTo(this, mb, lytSort, treeMenu)
  setExpandRatio(treeMenu, 1f)
}