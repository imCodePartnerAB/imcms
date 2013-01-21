package com.imcode
package imcms
package admin

import com.imcode.imcms.vaadin._
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui._
import com.vaadin.event.dd.{DragAndDropEvent, DropHandler}
import com.imcode.imcms.api.{DocRef, Document}
import dialog.{ConfirmationDialog, OkCancelDialog}
import java.io.File
import imcode.server.document.textdocument._
import com.vaadin.event.dd.acceptcriteria.{Not, AcceptAll, AcceptCriterion}
import com.vaadin.data.util.{HierarchicalContainer}
import scala.annotation.tailrec
import admin.doc.projection.{DocIdSelectWithLifeCycleIcon}
import com.imcode.imcms.admin.doc.{DocSelectDialog, DocEditorDialog, DocViewer, DocEditor}
import com.vaadin.ui.AbstractSelect.{VerticalLocationIs, ItemDescriptionGenerator}
import dao.TextDao
import com.vaadin.ui._
import java.util.{Locale, Arrays, Collections}
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.{Page, VaadinRequest, ExternalResource}
import com.vaadin.shared.ui.dd.VerticalDropLocation


// Todo: check permissions
// Doc edit, etc
@com.vaadin.annotations.Theme("imcms")
class DocAdmin extends UI with ImcmsServicesSupport { app =>

  sealed trait MappedRequest { def request: VaadinRequest }
  case class EditWorkingDoc(request: VaadinRequest, docId: Int) extends MappedRequest
  case class EditWorkingDocMenu(request: VaadinRequest, docId: Int, menuNo: Int) extends MappedRequest
  case class EditWorkingDocText(request: VaadinRequest, docId: Int, textNo: Int) extends MappedRequest

  object MappedRequest {
    def apply(request: VaadinRequest): Option[MappedRequest] = {
      import PartialFunction.condOpt

      val pathInfo = request.getPathInfo

      condOpt((pathInfo, request.getParameter("docId"))) {
        case (null, AnyInt(docId)) => EditWorkingDoc(request, docId)
      } orElse {
        condOpt((pathInfo, request.getParameter("docId"), request.getParameter("menuNo"))) {
          case ("/menu", AnyInt(docId), AnyInt(menuNo)) => EditWorkingDocMenu(request, docId, menuNo)
        }
      } orElse {
        condOpt((pathInfo, request.getParameter("docId"), request.getParameter("textNo"))) {
          case ("/text", AnyInt(docId), AnyInt(menuNo)) => EditWorkingDocText(request, docId, menuNo)
        }
      }
    }
  }


  def mkWorkingDocEditorComponent(mappedRequest: EditWorkingDoc) = new Panel with FullSize |>> { pnl =>
    val docId = mappedRequest.docId
    val doc = imcmsServices.getDocumentMapper.getDocument(docId)
    val lytEditor = new VerticalLayout with Spacing with Margin |>> {
      _.setSize(900, 700)
    }

    val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
      val btnClose = new Button("Close")
      val btnSave = new Button("Save")

      this.addComponents( btnClose, btnSave)
    }

    val docEditor = new DocEditor(doc)

    lytEditor.addComponent(new Panel(s"Document ${mappedRequest.docId}"))
    lytEditor.addComponent(docEditor.ui)
    lytEditor.addComponent(lytButtons)
    lytEditor.setExpandRatio(docEditor.ui, 1.0f)
    lytEditor.setComponentAlignment(lytButtons, Alignment.MIDDLE_CENTER)

    val content = new VerticalLayout with Margin with FullSize
    content.addComponent(lytEditor)
    content.setComponentAlignment(lytEditor, Alignment.MIDDLE_CENTER)

    pnl.setContent(content)

    lytButtons.btnSave.addClickHandler {
      docEditor.collectValues() match {
        case Left(errors) => Page.getCurrent.showErrorNotification(errors.mkString(","))
        case Right((editedDoc, i18nMetas)) =>
          try {
            imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.asJava, app.imcmsUser)
            Page.getCurrent.showInfoNotification("Document has been saved")
            Page.getCurrent.open(UI.getCurrent.servletContext.getContextPath, "_self")
          } catch {
            case e => Page.getCurrent.showErrorNotification("Failed to save document", e.getStackTraceString)
          }
      }
    }

    lytButtons.btnClose.addClickHandler {
      Page.getCurrent.open(UI.getCurrent.servletContext.getContextPath, "_self")
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

      this.addComponents( btnClose, btnSaveAndClose)
    }

    pnlEditor.setContent(editor.ui)

    val wndContent = new VerticalLayout with MiddleCenterAlignment with Spacing with Margin with FullSize

    wndContent.addComponents(pnlEditor, lytButtons)
    wndContent.setExpandRatio(pnlEditor, 1f)

    /*wnd.*/setContent(wndContent)

    lytButtons.btnSaveAndClose.addClickHandler {
      editor.collectValues().right.get |> { menu =>
        imcmsServices.getDocumentMapper.saveTextDocMenu(menu, UI.getCurrent.imcmsUser)
        Page.getCurrent.showInfoNotification("Menu has been saved")
        closeEditor()
      }
    }

    lytButtons.btnClose.addClickHandler {
      val editedMenu = editor.collectValues().right.get
      if (editedMenu.getSortOrder == menu.getSortOrder && editedMenu.getMenuItems.deep == menu.getMenuItems.deep) {
        closeEditor()
      } else {
        new ConfirmationDialog("Menu has been modified", "Close without saving?") |>> { dlg =>
          dlg.setOkButtonHandler {
            closeEditor()
          }
        } |> UI.getCurrent.addWindow
      }
    }

    private def closeEditor() {
//      new ExternalResource(getApplication.imcmsDocUrl(doc.getId)) |> { resource =>
//        open(resource)
//        //app.removeWindow(wnd)
//        //app.removeWindow(this)
//      }
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

      this.addComponents( btnClose, btnSaveAndClose)
    }

    pnlEditor.setContent(editor.ui)

    val wndContent = new VerticalLayout with MiddleCenterAlignment with Spacing with Margin with FullSize

    wndContent.addComponents(pnlEditor, lytButtons)
    wndContent.setExpandRatio(pnlEditor, 1f)

    /*wnd.*/setContent(wndContent)

    lytButtons.btnSaveAndClose.addClickHandler {
      editor.collectValues().right.get |> { texts =>
        imcmsServices.getDocumentMapper.saveTextDocTexts(texts.asJava, UI.getCurrent.imcmsUser)
        closeEditor()
      }
    }

    lytButtons.btnClose.addClickHandler {
      closeEditor()
    }

    private def closeEditor() {
//      new ExternalResource(/*wnd.*/getApplication.imcmsDocUrl(doc.getId)) |> { resource =>
//        /*wnd.*/open(resource)
//        //app.removeWindow(wnd)
//        //app.removeWindow(this)
//      }
    }
  }

  // todo: doc - unapply @ bounds to matched object, not the result
  override def init(request: VaadinRequest) {
    setLocale(new Locale(UI.getCurrent.imcmsUser.getLanguageIso639_2))

    val mrOpt = MappedRequest(request)
    val cmp = mrOpt collect {
      case mappedRequest: EditWorkingDoc => mkWorkingDocEditorComponent(mappedRequest)
      //case mappedRequest: EditWorkingDocMenu =>
      //case mappedRequest: EditWorkingDocText =>
    } getOrElse new Label("N/A")

    setContent(cmp)

//    setContent(new FormLayout(
//        new Label(request.getPathInfo) |>> { _.setCaption("Request path info") },
//      new Label(request.getParameterMap.toString) |>> { _.setCaption("Request parameter map") },
//      new Label(request.getCharacterEncoding) |>> { _.setCaption("Request character encoding") },
//      new Label(request.getContextPath) |>> { _.setCaption("Request context path") },
//      new Label(request.getService.getServiceName) |>> { _.setCaption("Service name") },
//      new Label(request.getService.getBaseDirectory.getPath) |>> { _.setCaption("Base dir") },
//      // WTF?
//      new Label(request.getService.getStaticFileLocation(request)) |>> { _.setCaption("Static file location") }
//    ))
  }

//      //setLogoutURL("/test.jsp")
//      //"AdminDoc?meta_id=" + parentDocument.getId + "&flags=" + ImcmsConstants.DISPATCH_FLAG__EDIT_MENU + "&editmenu=" + parentMenuIndex
////    }
//  }
}


// todo: ???revert btn (in save or in menu)???
class MenuEditor(doc: TextDocumentDomainObject, menu: MenuDomainObject) extends Editor with ImcmsServicesSupport {

  type Data = MenuDomainObject

  private var state = menu.clone()

  val ui = new MenuEditorUI |>> { ui =>

    ui.ttMenu.setItemDescriptionGenerator(new ItemDescriptionGenerator {
      def generateDescription(source: Component, itemId: AnyRef, propertyId: AnyRef) = "menu item tooltip"
    })

    ui.ttMenu.addValueChangeHandler {
      doto(ui.miEditSelectedDoc, ui.miExcludeSelectedDoc, ui.miShowSelectedDoc) {
        _.setEnabled(ui.ttMenu.isSelected)
      }
    }

    addContainerProperties(ui.ttMenu,
      PropertyDescriptor[DocId]("docs_projection.container_property.meta_id".i),
      PropertyDescriptor[String]("docs_projection.container_property.headline".i),
      PropertyDescriptor[String]("docs_projection.container_property.alias".i),
      PropertyDescriptor[String]("docs_projection.container_property.type".i),
      PropertyDescriptor[String]("docs_projection.container_property.status".i)
    )

    // todo: ??? search for current language + default version ???
    ui.miIncludeDocs.setCommandHandler {
      new DocSelectDialog("Choose documents", UI.getCurrent.imcmsUser) |>> { dlg =>
        dlg.setOkButtonHandler {
          for {
            doc <- dlg.projection.selection
            docId = doc.getId
            if !state.getItemsMap.containsKey(docId)
            doc <- imcmsServices.getDocumentMapper.getDefaultDocument(docId) |> opt
          } {
            val docRef = imcmsServices.getDocumentMapper.getDocumentReference(doc)
            val menuItem = new MenuItemDomainObject(docRef)
            state.addMenuItemUnchecked(menuItem)
          }

          updateMenuUI()
        }
      } |> UI.getCurrent.addWindow
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
            Page.getCurrent.showWarningNotification("Document does not exist")
            state.removeMenuItemByDocumentId(docId)
            updateMenuUI()

          case doc =>
            new DocEditorDialog( "Edit document properties", doc) |>> { dlg =>
              dlg.setOkButtonHandler {
                dlg.docEditor.collectValues() match {
                  case Left(errors) => Page.getCurrent.showErrorNotification(errors.mkString(", "))
                  case Right((modifiedDoc, i18nMetas)) =>
                    try {
                      imcmsServices.getDocumentMapper.saveDocument(modifiedDoc, i18nMetas.asJava, UI.getCurrent.imcmsUser)
                      updateMenuUI()
                    } catch {
                      case e =>
                        Page.getCurrent.showErrorNotification("Can't save document", e.getMessage)
                        throw e
                    }
                }
              }
            } |> UI.getCurrent.addWindow
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

  resetValues()

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
      // doc.getDocumentType.getName.toLocalizedString(ui.getApplication.imcmsUser)
      ttMenu.addRow(docId, docId: JInteger, doc.getHeadline, doc.getAlias, doc.getDocumentType.getName.toLocalizedString("eng"), doc.getLifeCyclePhase.toString)
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
  val ttMenu = new TreeTable with AlwaysFireValueChange[AnyRef] with DocIdSelectWithLifeCycleIcon with SingleSelect[DocId]
                             with Selectable with Immediate with FullSize |>> { tt =>
    tt.setRowHeaderMode(Table.RowHeaderMode.HIDDEN)
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
  this.addComponents( mb, lytSort, ttMenu)
  setExpandRatio(ttMenu, 1f)
}


// todo: mk text if not exists
// todo: check perms
// todo: pick user lang at start
// todo: pick a format + rows no
class TextEditor(doc: TextDocumentDomainObject, texts: Seq[TextDomainObject]) extends Editor {

  type Data = Seq[TextDomainObject]

  private var state: Seq[TextDomainObject] = _
  private var stateUis: Seq[RichTextArea] = _

  val ui = new TextEditorUI

  resetValues()

  def resetValues() {
    state = texts.map(_.clone)
    stateUis = state.map { text =>
      new RichTextArea with FullSize |>> { rt =>
        rt.value = text.getText
      }
    }

    ui.tsTexts.removeAllComponents()

    (state, stateUis).zipped.foreach { (text, textUi) =>
      ui.tsTexts.addTab(textUi) |> { tab =>
        tab.setCaption(text.getLanguage.getName)
      }
    }
  }

  def collectValues(): ErrorsOrData = {
    (state, stateUis).zipped.foreach { (text, textUi) => text.setText(textUi.value) }
    Right(state.map(_.clone))
  }
}


class TextEditorUI extends VerticalLayout with Margin with FullSize {
  val mb = new MenuBar with FullWidth
  val miShowHistory = mb.addItem("Show history")
  val miHelp = mb.addItem("Help")
  val tsTexts = new TabSheet with FullSize

  private val lytFormat = new FormLayout

  this.addComponents( mb, lytFormat, tsTexts)
  setExpandRatio(tsTexts, 1f)
}

//case PathHandlers.DefaultDoc(docId) => mkDocEditorWindow(docId)
//case PathHandlers.CustomDoc(docId, docVersionNo) => mkDocEditorWindow(docId)
//case PathHandlers.Menu(docId, docVersionNo, menuNo) => mkMenuEditorWindow(DocRef.of(docId, docVersionNo), menuNo)
//case PathHandlers.Text(docId, docVersionNo, textNo) => mkTextEditorWindow(DocRef.of(docId, docVersionNo), textNo)
//case _ => null

