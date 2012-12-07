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
import com.imcode.imcms.api.Document
import com.vaadin.terminal.{FileResource, ExternalResource, Resource}
import java.io.File
import imcode.server.document.textdocument._
import com.vaadin.event.dd.acceptcriteria.{Not, AcceptAll, AcceptCriterion}
import com.vaadin.data.util.{HierarchicalContainer}
import scala.annotation.tailrec
import admin.doc.search.{DocIdSelectWithLifeCycleIcon, DocsProjectionDialog}
import admin.doc.{DocViewer, DocEditor}
import com.vaadin.ui.AbstractSelect.{VerticalLocationIs, ItemDescriptionGenerator}
import java.util.concurrent.atomic.AtomicBoolean
import dao.TextDao
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
        case PathHandlers.Text(docId, docVersionNo, textNo) => mkTextEditorWindow(DocRef.of(docId, docVersionNo), textNo)
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

    lytButtons.btnClose.addClickHandler {

    }
  }


  def mkMenuEditorWindow(docRef: DocRef, menuNo: Int): Window = new Window with ImcmsServicesSupport {// wnd =>
    val doc = app.imcmsServices.getDocumentMapper.getCustomDocument(docRef).asInstanceOf[TextDocumentDomainObject]
    val menu = doc.getMenu(menuNo)
    val editor = new MenuEditor(doc, menu) |>> { me => me.ui.setSizeFull() }
    val pnlEditor = new Panel("Edit document %d menu no %d".format(docRef.docId, docRef.docVersionNo)) with FullSize |>> { _.setWidth("800px") }
    val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
      val btnClose = new Button("Close")
      val btnSaveAndClose = new Button("Save & Close")

      addComponentsTo(this, btnClose, btnSaveAndClose)
    }

    pnlEditor.setContent(editor.ui)

    val wndContent = new VerticalLayout with MiddleCenterAlignment with Spacing with Margin with FullSize

    addComponentsTo(wndContent, pnlEditor, lytButtons)
    wndContent.setExpandRatio(pnlEditor, 1f)

    /*wnd.*/setContent(wndContent)

    lytButtons.btnSaveAndClose.addClickHandler {
      editor.collectValues().right.get |> { menu =>
        imcmsServices.getDocumentMapper.saveTextDocMenu(menu, /*wnd.*/getApplication.imcmsUser)
      }
    }

    lytButtons.btnClose.addClickHandler {
      new ExternalResource(/*wnd.*/getApplication.imcmsDocUrl(doc.getId)) |> { resource =>
        /*wnd.*/open(resource)
        //app.removeWindow(wnd)
        //app.removeWindow(this)
      }
    }
  }

  def mkTextEditorWindow(docRef: DocRef, textNo: Int): Window = new Window with ImcmsServicesSupport {// wnd =>
    val doc = app.imcmsServices.getDocumentMapper.getCustomDocument(docRef).asInstanceOf[TextDocumentDomainObject]
    val textDao = app.imcmsServices.getSpringBean(classOf[TextDao])
    val texts = textDao.getTexts(docRef, textNo, None, true)
    val editor = new TextEditor(doc, texts.asScala)
    val pnlEditor = new Panel("Edit document %d text no %d".format(docRef.docId, docRef.docVersionNo)) with FullSize |>> { _.setWidth("800px") }

    val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
      val btnClose = new Button("Close")
      val btnSaveAndClose = new Button("Save & Close")

      addComponentsTo(this, btnClose, btnSaveAndClose)
    }

    pnlEditor.setContent(editor.ui)

    val wndContent = new VerticalLayout with MiddleCenterAlignment with Spacing with Margin with FullSize

    addComponentsTo(wndContent, pnlEditor, lytButtons)
    wndContent.setExpandRatio(pnlEditor, 1f)

    /*wnd.*/setContent(wndContent)

    lytButtons.btnSaveAndClose.addClickHandler {
      editor.collectValues().right.get |> { texts =>
        imcmsServices.getDocumentMapper.saveTextDocTexts(texts.asJava, /*wnd.*/getApplication.imcmsUser)
        closeEditor()
      }
    }

    lytButtons.btnClose.addClickHandler {
      closeEditor()
    }

    private def closeEditor() {
      new ExternalResource(/*wnd.*/getApplication.imcmsDocUrl(doc.getId)) |> { resource =>
        /*wnd.*/open(resource)
        //app.removeWindow(wnd)
        //app.removeWindow(this)
      }
    }
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

  val ui = new MenuEditorUI { ui =>
    override def attach() {
      initEditor()
    }

    ui.ttMenu.setItemDescriptionGenerator(new ItemDescriptionGenerator {
      def generateDescription(source: Component, itemId: AnyRef, propertyId: AnyRef) = "menu item tooltip"
    })

    ui.ttMenu.addValueChangeHandler {
      doto(ui.miEditSelectedDoc, ui.miExcludeSelectedDoc, ui.miShowSelectedDoc) {
        _.setEnabled(ui.ttMenu.isSelected)
      }
    }

    addContainerProperties(ui.ttMenu,
      PropertyDescriptor[DocId]("doc.tbl.col.id".i),
      PropertyDescriptor[String]("doc.tbl.col.headline".i),
      PropertyDescriptor[String]("doc.tbl.col.alias".i),
      PropertyDescriptor[String]("doc.tbl.col.type".i),
      PropertyDescriptor[String]("doc.tbl.col.status".i)
    )

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

          updateMenuUI()
        }

        dlg.setSize(500, 600)
      }
    }

    ui.miExcludeSelectedDoc.setCommandHandler {
      for (docId <- ui.ttMenu.selectionOpt) {
        state.removeMenuItemByDocumentId(docId)
        updateMenuUI()
      }
    }

    ui.miEditSelectedDoc.setCommandHandler {
      for (docId <- ui.ttMenu.selectionOpt) {
        imcmsServices.getDocumentMapper.getDocument(docId) match {
          case null =>
            ui.rootWindow.showWarningNotification("Document does not exist")
            state.removeMenuItemByDocumentId(docId)
            updateMenuUI()

          case doc =>
            DocEditor.mkDocEditorDialog(doc, "Edit document properties") |>> { dlg =>
              dlg.setOkButtonHandler {
                dlg.docEditor.collectValues() match {
                  case Left(errors) => ui.rootWindow.showErrorNotification(errors.mkString(", "))
                  case Right((modifiedDoc, i18nMetas)) =>
                    try {
                      imcmsServices.getDocumentMapper.saveDocument(modifiedDoc, i18nMetas.asJava, ui.getApplication.imcmsUser)
                      updateMenuUI()
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
      for (docId <- ui.ttMenu.selectionOpt) {
        DocViewer.showDocViewDialog(ui, docId)
      }
    }

    ui.cbSortOrder.addValueChangeHandler {
      state.setSortOrder(ui.cbSortOrder.selection)
      updateMenuUI()
    }
  }


  private object MenuDropHandlers {
    private val container = ui.ttMenu.getContainerDataSource.asInstanceOf[HierarchicalContainer]

    private abstract class AbstractDropHandler extends DropHandler {
      def drop(event: DragAndDropEvent) {
        val transferable = event.getTransferable.asInstanceOf[Table#TableTransferable]
        val target = event.getTargetDetails.asInstanceOf[AbstractSelect#AbstractSelectTargetDetails]

        val targetItemId = target.getItemIdOver
        val sourceItemId = transferable.getItemId

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

          case VerticalDropLocation.MIDDLE =>
            container.setParent(sourceItemId, targetItemId)
        }

        updateItemsSortIndex()
      }

      protected def updateItemsSortIndex()
    }

    val singleLevel: DropHandler = new AbstractDropHandler {
      val getAcceptCriterion: AcceptCriterion = new Not(VerticalLocationIs.MIDDLE)

      protected def updateItemsSortIndex() {
        val menuItems = state.getItemsMap

        for {
          nodes <- container.rootItemIds() |> opt
          (docId, index) <- nodes.asInstanceOf[JCollection[DocId]].asScala.zipWithIndex
        } {
          menuItems.get(docId) |> { _.setSortKey(index + 1) }
        }
      }
    }

    val multilevel: DropHandler = new AbstractDropHandler {
      val getAcceptCriterion: AcceptCriterion = AcceptAll.get()

      protected def updateItemsSortIndex() {
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
    }
  }


  private val initEditor: (() => Unit) = {
    val initialized = new AtomicBoolean(false)

    () => if (initialized.compareAndSet(false, true)) resetValues()
  }

  private def updateMenuUI() {
    val sortOrder = state.getSortOrder
    val isMultilevel = sortOrder == MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER
    val isManualSort = Set(
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER,
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED
    ).contains(sortOrder)

    import ui.ttMenu

    ttMenu.removeAllItems()
    ttMenu.setDragMode(if (isManualSort) Table.TableDragMode.ROW else Table.TableDragMode.NONE)
    ttMenu.setDropHandler(sortOrder match {
      case MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER => MenuDropHandlers.multilevel
      case MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED => MenuDropHandlers.singleLevel
      case _ => null
    })


    val menuItems = state.getMenuItems
    for (menuItem <- menuItems) {
      val doc = menuItem.getDocument
      val docId = doc.getId

      ttMenu.addRow(docId, docId: JInteger, doc.getHeadline, doc.getAlias, doc.getDocumentType.getName.toLocalizedString(ui.getApplication.imcmsUser), doc.getLifeCyclePhase.toString)
      ttMenu.setChildrenAllowed(docId, isMultilevel)
      ttMenu.setCollapsed(docId, false)
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
        ttMenu.setParent(menuItem.getDocumentId, parentMenuItem.getDocumentId)
      }
    }
  }

  def resetValues() {
    state = menu.clone()
    ui.cbSortOrder.selection = state.getSortOrder
    ui.ttMenu.selection = null
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
  val ttMenu = new TreeTable with AlwaysFireValueChange with DocIdSelectWithLifeCycleIcon with SingleSelect[DocId]
                             with Selectable with Immediate with FullSize |>> { tt =>
    tt.setRowHeaderMode(Table.ROW_HEADER_MODE_HIDDEN)
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
  addComponentsTo(this, mb, lytSort, ttMenu)
  setExpandRatio(ttMenu, 1f)
}


// todo: mk text if not exists
// todo: check perms
// todo: pick user lang at start
// todo: pick a format + rows no
class TextEditor(doc: TextDocumentDomainObject, texts: Seq[TextDomainObject]) extends Editor {

  type Data = Seq[TextDomainObject]

  private var state: Seq[TextDomainObject] = _
  private var textsUis: Seq[RichTextArea with GenericProperty[String]] = _

  val ui = new TextEditorUI

  resetValues()

  def resetValues() {
    state = texts.map(_.clone)
    textsUis = state.map { text =>
      new RichTextArea with GenericProperty[String] with FullSize |>> { rt =>
        rt.value = text.getText
      }
    }

    ui.tsTexts.removeAllComponents()

    (state, textsUis).zipped.foreach { (text, textUi) =>
      ui.tsTexts.addTab(textUi) |> { tab =>
        tab.setCaption(text.getLanguage.getName)
      }
    }
  }

  def collectValues(): ErrorsOrData = {
    (state, textsUis).zipped.foreach((text, textUi) => text.setText(textUi.value))
    Right(state.map(_.clone))
  }
}


class TextEditorUI extends VerticalLayout with Margin with FullSize {
  val mb = new MenuBar with FullWidth
  val miShowHistory = mb.addItem("Show history")
  val miHelp = mb.addItem("Help")
  val tsTexts = new TabSheet with FullSize

  private val lytFormat = new FormLayout

  addComponentsTo(this, mb, lytFormat, tsTexts)
  setExpandRatio(tsTexts, 1f)
}