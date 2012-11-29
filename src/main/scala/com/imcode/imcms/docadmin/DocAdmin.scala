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
import com.vaadin.event.dd.{DragAndDropEvent, DropHandler}
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation
import com.vaadin.event.DataBoundTransferable
import com.vaadin.ui._
import com.imcode.imcms.api.Document
import com.vaadin.terminal.{FileResource, ExternalResource, Resource}
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import imcode.server.document.textdocument._
import com.vaadin.ui.AbstractSelect.{VerticalLocationIs, ItemDescriptionGenerator}
import com.vaadin.event.dd.acceptcriteria.{Not, AcceptAll, AcceptCriterion}
import com.vaadin.data.util.{HierarchicalContainer}
import scala.annotation.tailrec
import admin.doc.search.{DocIdSelectWithLifeCycleIcon, DocsProjectionDialog}
import admin.doc.{DocViewer, DocEditor}

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
            imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.asJava, app.imcmsUser)
            wnd.showInfoNotification("Document has been saved")
          } catch {
            case e => wnd.showErrorNotification("Failed to save document", e.getStackTraceString)
          }
      }
    }
  }


  def mkMenuEditorWindow(docRef: DocRef, menuNo: Int): Window = new Window with ImcmsServicesSupport |>> { wnd =>
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

    lytButtons.btnSave.addClickHandler {
      menuEditor.collectValues().right.get |> { menu =>
        imcmsServices.getDocumentMapper.saveTextDocMenu(menu, wnd.getApplication.imcmsUser())
      }
    }
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


// todo: ???revert btn (in save or in menu)???
class MenuEditor(doc: TextDocumentDomainObject, menu: MenuDomainObject) extends Editor with ImcmsServicesSupport {

  type Data = MenuDomainObject

  private var state = menu.clone()

  val ui = new MenuEditorUI |>> { ui =>
    ui.treeMenu.setItemDescriptionGenerator(new ItemDescriptionGenerator {
      def generateDescription(source: Component, itemId: AnyRef, propertyId: AnyRef) = "menu item tooltip"
    })

    ui.treeMenu.addValueChangeHandler {
      doto(ui.miEditSelectedDoc, ui.miExcludeSelectedDoc, ui.miShowSelectedDoc) {
        _.setEnabled(ui.treeMenu.selectionOpt.isDefined)
      }
    }

    ui.miIncludeDocs.setCommandHandler {
      ui.rootWindow.initAndShow(new OkCancelDialog("Choose documents") with DocsProjectionDialog, resizable = true) { dlg =>
        dlg.setOkButtonHandler {
          for {
            docId <- dlg.projection.selection
            if !state.getItemsMap.containsKey(docId)
            doc <- imcmsServices.getDocumentMapper.getDefaultDocument(docId) |> opt
          } {
            val docRef = imcmsServices.getDocumentMapper.getDocumentReference(doc)
            val menuItem = new MenuItemDomainObject(docRef)
            state.addMenuItemUnchecked(menuItem)
          }

          updateTreeMenuUI()
        }

        dlg.setSize(500, 600)
      }
    }

    ui.miExcludeSelectedDoc.setCommandHandler {
      for (docId <- ui.treeMenu.selectionOpt) {
        state.removeMenuItemByDocumentId(docId)
        updateTreeMenuUI()
      }
    }

    ui.miEditSelectedDoc.setCommandHandler {
      for (docId <- ui.treeMenu.selectionOpt) {
        imcmsServices.getDocumentMapper.getDocument(docId) match {
          case null =>
            ui.rootWindow.showWarningNotification("Document does not exist")
            state.removeMenuItemByDocumentId(docId)
            updateTreeMenuUI()

          case doc =>
            DocEditor.mkDocEditorDialog(doc, "Edit document properties") |>> { dlg =>
              dlg.setOkButtonHandler {
                dlg.docEditor.collectValues() match {
                  case Left(errors) => ui.rootWindow.showErrorNotification(errors.mkString(", "))
                  case Right((modifiedDoc, i18nMetas)) =>
                    try {
                      imcmsServices.getDocumentMapper.saveDocument(modifiedDoc, i18nMetas.asJava, ui.getApplication.imcmsUser())
                      updateTreeMenuUI()
                    } catch {
                      case e =>
                        ui.rootWindow.showErrorNotification("Can't save document", e.getMessage)
                        throw e
                    }
                }
              }
            } |> ui.rootWindow.addWindow
        }
      }
    }

    ui.miShowSelectedDoc.setCommandHandler {
      for (docId <- ui.treeMenu.selectionOpt) {
        DocViewer.showDocViewDialog(ui, docId)
      }
    }

    ui.cbSortOrder.addValueChangeHandler {
      state.setSortOrder(ui.cbSortOrder.selection)
      updateTreeMenuUI()
    }
  }

  private object TreeMenuDropHandlers {
    private val tree = ui.treeMenu
    private val container = tree.getContainerDataSource.asInstanceOf[HierarchicalContainer]

    val singleLevel = new DropHandler {
      private def updateItemsSortIndex() {
        val menuItems = state.getItemsMap

        for {
          nodes <- container.rootItemIds() |> opt
          (docId, index) <- nodes.asInstanceOf[JCollection[DocId]].asScala.zipWithIndex
        } {
          menuItems.get(docId) |> { _.setSortKey(index + 1) }
        }
      }

      def getAcceptCriterion(): AcceptCriterion = new Not(VerticalLocationIs.MIDDLE)

      def drop(event: DragAndDropEvent) {
        event.getTransferable match {
          case transferable if transferable.getSourceComponent != tree =>
          case transferable =>
            val target = event.getTargetDetails.asInstanceOf[Tree#TreeTargetDetails]
            val sourceItemId = transferable.asInstanceOf[DataBoundTransferable].getItemId
            val targetItemId = target.getItemIdOver

            target.getDropLocation match {
              case VerticalDropLocation.TOP =>
                val parentId = container.getParent(targetItemId)
                container.setParent(sourceItemId, parentId)
                container.moveAfterSibling(sourceItemId, targetItemId)
                container.moveAfterSibling(targetItemId, sourceItemId)

              case VerticalDropLocation.BOTTOM =>
                val parentId = container.getParent(targetItemId)
                container.setParent(sourceItemId, parentId)
                container.moveAfterSibling(sourceItemId, targetItemId)
            }

            updateItemsSortIndex()
        }
      }
    }

    val multilevel = new DropHandler {
      private def updateItemsTreeSortIndex() {
        def updateItemsTreeSortIndex(parentSortIndex: Option[String], nodes: JCollection[_]) {
          if (nodes != null) {
            for ((docId, index) <- nodes.asInstanceOf[JCollection[DocId]].asScala.zipWithIndex) {
              state.getItemsMap.get(docId) |> { menuItem =>
                val sortIndex = parentSortIndex.map(_ + ".").mkString + (index + 1)

                menuItem.setTreeSortIndex(sortIndex)
                updateItemsTreeSortIndex(Some(sortIndex), container.getChildren(docId))
              }
            }
          }
        }

        updateItemsTreeSortIndex(None, container.rootItemIds())
      }

      def getAcceptCriterion(): AcceptCriterion = AcceptAll.get()

      def drop(event: DragAndDropEvent) {
        event.getTransferable match {
          case transferable if transferable.getSourceComponent != tree =>
          case transferable =>
            val target = event.getTargetDetails.asInstanceOf[Tree#TreeTargetDetails]
            val sourceItemId = transferable.asInstanceOf[DataBoundTransferable].getItemId
            val targetItemId = target.getItemIdOver

            target.getDropLocation match {
              case VerticalDropLocation.MIDDLE =>
                tree.setParent(sourceItemId, targetItemId)

              case VerticalDropLocation.TOP =>
                val parentId = container.getParent(targetItemId)
                container.setParent(sourceItemId, parentId)
                container.moveAfterSibling(sourceItemId, targetItemId)
                container.moveAfterSibling(targetItemId, sourceItemId)

              case VerticalDropLocation.BOTTOM =>
                val parentId = container.getParent(targetItemId)
                container.setParent(sourceItemId, parentId)
                container.moveAfterSibling(sourceItemId, targetItemId)
            }

            updateItemsTreeSortIndex()
        }
      }
    }
  }


  resetValues()

  private def updateTreeMenuUI() {
    val sortOrder = state.getSortOrder
    val isMultilevel = sortOrder == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER
    val isManualSort = Set(
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER,
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED
    ).contains(sortOrder)

    import ui.treeMenu

    treeMenu.removeAllItems()
    treeMenu.setDragMode(if (isManualSort) Tree.TreeDragMode.NODE else Tree.TreeDragMode.NONE)
    treeMenu.setDropHandler(sortOrder match {
      case MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER => TreeMenuDropHandlers.multilevel
      case MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED => TreeMenuDropHandlers.singleLevel
      case _ => null
    })

    val menuItems = state.getMenuItems
    for (menuItem <- menuItems) {
      val doc = menuItem.getDocument
      val docId = doc.getId
      val caption = docId+": "+doc.getHeadline

      treeMenu.addItem(docId, caption)
      treeMenu.setChildrenAllowed(docId, isMultilevel)
      treeMenu.expandItem(docId)
    }

    if (isMultilevel) {
      @tailrec
      def findParentMenuItem(menuIndex: String): Option[MenuItemDomainObject] = {
        menuIndex.lastIndexOf('.') match {
          case -1 => None
          case  n =>
            val parentMenuIndex = menuIndex.substring(0, n)
            val parentMenuItemOpt = menuItems.find(_.getTreeSortIndex == parentMenuIndex)

            if (parentMenuItemOpt.isDefined) parentMenuItemOpt else findParentMenuItem(parentMenuIndex)
        }
      }

      for {
        menuItem <- menuItems
        parentMenuItem <- findParentMenuItem(menuItem.getTreeSortIndex)
      } {
        treeMenu.setParent(menuItem.getDocumentId, parentMenuItem.getDocumentId)
      }
    }
  }

  def resetValues() {
    state = menu.clone()
    ui.cbSortOrder.selection = state.getSortOrder
    ui.treeMenu.selection = null
  }

  def collectValues(): ErrorsOrData = Right(state.clone())
}


class MenuEditorUI extends VerticalLayout with Margin with FullSize {
  val mb = new MenuBar with FullWidth
  val miIncludeDocs = mb.addItem("Add")
  val miExcludeSelectedDoc = mb.addItem("Remove")
  val miShowSelectedDoc = mb.addItem("Show")
  val miEditSelectedDoc = mb.addItem("Properties")
  val miHelp = mb.addItem("Help")

  val treeMenu = new Tree with AlwaysFireValueChange with DocIdSelectWithLifeCycleIcon with SingleSelect[DocId] with Selectable with Immediate with FullSize |>> {
    _.setContainerDataSource(new HierarchicalContainer)
  }

  private val lytSort = new FormLayout

  val cbSortOrder = new ComboBox("Sort order") with SingleSelect[JInteger] with Immediate with NoNullSelection |>> { cb =>
    List(
      MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE -> "Title",
      MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED -> "Modified date/time",
      MenuDomainObject.MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED -> "Published date/time",
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED -> "Manual",
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER -> "Manual - multilevel"
    ).foreach {
      case (id, caption) => cb.addItem(id: JInteger, caption)
    }
  }

  lytSort.addComponent(cbSortOrder)
  addComponentsTo(this, mb, lytSort, treeMenu)
  setExpandRatio(treeMenu, 1f)
}