package com.imcode
package imcms
package admin

import com.imcode.imcms.vaadin._
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui._
import com.vaadin.event.dd.{DragAndDropEvent, DropHandler}
import com.imcode.imcms.api.{ContentLanguage, DocumentVersion, DocRef, Document}
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
import com.vaadin.server.{VaadinService, Page, VaadinRequest, ExternalResource}
import com.vaadin.shared.ui.dd.VerticalDropLocation
import scala.reflect.internal.Trees.Try
import scala.util.Try
import imcode.server.Imcms


// Todo: check permissions
// Doc edit, etc
@com.vaadin.annotations.Theme("imcms")
class DocAdmin extends UI with ImcmsServicesSupport { app =>

  sealed trait MappedRequest { def vaadinRequest: VaadinRequest }
  case class EditWorkingDoc(vaadinRequest: VaadinRequest, docId: Int) extends MappedRequest
  case class EditWorkingDocMenu(vaadinRequest: VaadinRequest, docId: Int, menuNo: Int) extends MappedRequest
  case class EditWorkingDocText(vaadinRequest: VaadinRequest, docId: Int, textNo: Int) extends MappedRequest

  object MappedRequest {
    def apply(vaadinRequest: VaadinRequest): Option[MappedRequest] = {
      import PartialFunction.condOpt

      val pathInfo = vaadinRequest.getPathInfo

      condOpt((pathInfo, vaadinRequest.getParameter("docId"))) {
        case (null, AnyInt(docId)) => EditWorkingDoc(vaadinRequest, docId)
      } orElse {
        condOpt((pathInfo, vaadinRequest.getParameter("docId"), vaadinRequest.getParameter("menuNo"))) {
          case ("/menu", AnyInt(docId), AnyInt(menuNo)) => EditWorkingDocMenu(vaadinRequest, docId, menuNo)
        }
      } orElse {
        condOpt((pathInfo, vaadinRequest.getParameter("docId"), vaadinRequest.getParameter("textNo"))) {
          case ("/text", AnyInt(docId), AnyInt(menuNo)) => EditWorkingDocText(vaadinRequest, docId, menuNo)
        }
      }
    }
  }


  // todo: doc - unapply @ bounds to matched object, not the result
  // todo: ??? pass requests from filter ???
  override def init(request: VaadinRequest) {
    setLocale(new Locale(UI.getCurrent.imcmsUser.getLanguageIso639_2))

    MappedRequest(request).map {
      case mappedRequest: EditWorkingDoc => mkWorkingDocEditorComponent(mappedRequest)
      case mappedRequest: EditWorkingDocMenu => mkWorkingDocMenuEditorComponent(mappedRequest)
      case mappedRequest: EditWorkingDocText => mkWorkingDocTextEditorComponent(mappedRequest)
    } match {
      case Some(component) => setContent(component)
      case _ => setContent(new Label("N/A"))
    }

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


  //
  //
  def mkWorkingDocEditorComponent(mappedRequest: EditWorkingDoc) = new FullScreenEditorUI(s"Document ${mappedRequest.docId}") |>> { ui =>
    val docId = mappedRequest.docId
    val doc = imcmsServices.getDocumentMapper.getDocument(docId)
    val editor = new DocEditor(doc)

    ui.mainUI = editor.ui

    editor.ui.setSize(900, 600)

    ui.buttons.btnSave.addClickHandler {
      editor.collectValues() match {
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

    ui.buttons.btnClose.addClickHandler {
      Page.getCurrent.open(UI.getCurrent.servletContext.getContextPath, "_self")
    }

    Page.getCurrent.getUriFragment.|>(opt).map(_.toLowerCase).foreach {
      case "info" => editor.metaEditor.ui.treeEditors.selection = "doc_meta_editor.menu_item.life_cycle"
      case "access" => editor.metaEditor.ui.treeEditors.selection = "doc_meta_editor.menu_item.access"
      case "appearance" => editor.metaEditor.ui.treeEditors.selection = "doc_meta_editor.menu_item.appearance"
      case "content" => editor.ui.setSelectedTab(1)
      case _ =>
    }
  }


  def mkWorkingDocMenuEditorComponent(mappedRequest: EditWorkingDocMenu) = new FullScreenEditorUI(s"Document ${mappedRequest.docId} menu no ${mappedRequest.menuNo}") |>> { ui =>
    val doc = imcmsServices.getDocumentMapper.getDefaultDocument(mappedRequest.docId).asInstanceOf[TextDocumentDomainObject]
    val menu = doc.getMenu(mappedRequest.menuNo)
    val editor = new MenuEditor(doc, menu) |>> { me => me.ui.setSizeFull() }

    ui.mainUI = editor.ui
    editor.ui.setSize(900, 600)

    ui.buttons.btnSaveAndClose.addClickHandler {
      editor.collectValues().right.get |> { menu =>
        imcmsServices.getDocumentMapper.saveTextDocMenu(menu, UI.getCurrent.imcmsUser)
        Page.getCurrent.showInfoNotification("Menu has been saved")
        closeEditor()
      }
    }

    ui.buttons.btnClose.addClickHandler {
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

    def closeEditor() {
      Page.getCurrent.open(UI.getCurrent.servletContext.getContextPath, "_self")
    }
  }



  // [+] Text can be inside or outside of a content loop
  // [+] Request params: rows, mode, formats, optionally content loop, filter, label
  // [-] Load doc and check permissions
  // TextDocumentDomainObject textDocument = (TextDocumentDomainObject) documentMapper.getDocument(documentId);
  // TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(textDocument);
  //
  // if (!textDocumentPermissionSet.getEditTexts()) {    // Checking to see if user may edit this
  //    AdminDoc.adminDoc(documentId, user, request, res, getServletContext());
  //
  //    return;
  // }

  // [~] Select or create text in current language, set editor label
  //  int textIndex = Integer.parseInt(request.getParameter("txt"));
  //  String label = null == request.getParameter("label") ? "" : request.getParameter("label");
  //
  //  I18nLanguage language = Imcms.getUser().getDocGetterCallback().languages().preferred();
  //  TextDomainObject text = contentRef == null
  //  ? textDocument.getText(textIndex)
  //    : textDocument.getText(textIndex, contentRef);
  //
  //  Integer metaId = textDocument.getId();
  //
  //  if (text == null) {
  //    text = new TextDomainObject();
  //    text.setDocRef(DocRef.of(metaId, textDocument.getVersionNo()));
  //    text.setNo(textIndex);
  //    text.setLanguage(language);
  //    text.setType(TextDomainObject.TEXT_TYPE_HTML);
  //    text.setContentRef(contentRef);
  //  }

  // [+] editor/text formats
//  boolean showModeEditor = formats.isEmpty();
//  boolean showModeText   = formats.contains("text") || showModeEditor;
//  boolean showModeHtml   = formats.contains("html") || formats.contains("none") || showModeEditor ;
//  boolean editorHidden   = getCookie("imcms_hide_editor", request).equals("true") ;
//  int rows = (request.getParameter("rows") != null && request.getParameter("rows").matches("^\\d+$")) ? Integer.parseInt(request.getParameter("rows")) : 0 ;
//
//  if (rows > 0) {
//    showModeEditor = false;
//  }
//
//  [-]
//  Cookie?
  // <title><? templates/sv/change_text.html/1 ?></title>
  //         <script src="<%= request.getContextPath() %>/imcms/$language/scripts/imcms_admin.js.jsp"
  //
  //     <% if (showModeEditor && !editorHidden) { %>
  //            JS XINA

  // if TEXT_TYPE == text type html && !editor hidden
//  String returnUrl = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL);
//  if (returnUrl != null) {
//    %>
//    <input type="hidden" name="<%=ImcmsConstants.REQUEST_PARAM__RETURN_URL%>" value="<%=returnUrl%>">
//      <%
//        }
//        %>

  // History / RESTORE
  // if rows == 1 show text field, else text area if rows not defined default to 25
  // NB! showModeEditor = false if rows > 0
  //
  // ?????? editorHidden - show originail i.e. w/o editoc controls ??? ?????

  // [-] Save text
  // -check permissionSet.getEditTexts()
  // -save text
  // -imcref.updateMainLog("Text " + txt_no + " in [" + meta_id + "] modified by user: [" + user.getFullName()+ "]");
  // - handle save exs:
  //  try {
  //    documentMapper.saveTextDocText(text, user);
  //  } catch (NoPermissionToEditDocumentException e) {
  //    throw new ShouldHaveCheckedPermissionsEarlierException(e);
  //  } catch (NoPermissionToAddDocumentToMenuException e) {
  //    throw new ConcurrentDocumentModificationException(e);
  //  } catch (DocumentSaveException e) {
  //    throw new ShouldNotBeThrownException(e);
  //  }

  // [-] Fix edit_text.jsp - location, language, loop attrs.
  // [-] Delete ChangeText servet
  // [-] Delete SaveText servet
  // [-] Delete change_text.jsp + resources
  // [-] Remove Xina, install CKEditor
  // [-]
  //
  //
  //
  //
  //
  //
  //
  def mkWorkingDocTextEditorComponent(mappedRequest: EditWorkingDocText) = new FullScreenEditorUI(s"Document ${mappedRequest.docId} text no ${mappedRequest.textNo}") |>> { ui =>
    val formats = mappedRequest.vaadinRequest.getParameterMap.get("format") match {
      case null => Set.empty[String]
      case array => array.toSet
    }

    val rowsCountOpt = mappedRequest.vaadinRequest.getParameter("rows") |> {
      case  PosInt(rows) => Some(rows)
      case _ => None
    }

    val showModeText = formats.isEmpty || formats.contains("text")
    val showModeHtml = formats.isEmpty || formats.contains("html") || formats.contains("none")
    val showModeEditor = formats.isEmpty && rowsCountOpt.isEmpty

    val ContentRefEx = """(\d+)_(\d+)""".r
    val contentRefOpt = mappedRequest.vaadinRequest.getParameter("contentRef") match {
      case ContentRefEx(loopNo, contentNo) => ContentRef.of(loopNo.toInt, contentNo.toInt) |> opt
      case _ => None
    }

    val labelOpt = mappedRequest.vaadinRequest.getParameter("label") |> opt map (_.trim) filter (_.length > 0)


    val docIdOpt = mappedRequest.vaadinRequest.getParameter("docId") |> Try(_.toInt).toOption
    val textNoOpt = mappedRequest.vaadinRequest.getParameter("textNo") |> Try(_.toInt).toOption

    val textDao = imcmsServices.getSpringBean(classOf[TextDao])
    val texts = textDao.getTexts(DocRef.of(docIdOpt, DocumentVersion.WORKING_VERSION_NO), contentRefOpt, createIfNotExists = true)

    for (text <- texts.asScala if text.getId == null) {
      text.setType(TextDomainObject.TEXT_TYPE_HTML)
    }

    // Current language
    val userLanguage = Imcms.getUser.getDocGetterCallback.contentLanguages.preferred

    val doc = app.imcmsServices.getDocumentMapper.getWorkingDocument(mappedRequest.docId).asInstanceOf[TextDocumentDomainObject]
    val editor = new TextEditor(doc, texts.asScala)

    ui.mainUI = editor.ui
    editor.ui.setSize(900, 600)

    ui.buttons.btnSaveAndClose.addClickHandler {
      editor.collectValues().right.get |> { texts =>
        imcmsServices.getDocumentMapper.saveTextDocTexts(texts.asJava, UI.getCurrent.imcmsUser)
        closeEditor()
      }
    }

    ui.buttons.btnClose.addClickHandler {
      closeEditor()
    }

    def closeEditor() {
      Page.getCurrent.open(UI.getCurrent.servletContext.getContextPath, "_self")
    }
  }
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


class MenuEditorUI extends VerticalLayout with FullSize {
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

case class TextEditorSettings(linesCountOpt: Option[Int], formatOpt: Option[String])

class TextEditor(doc: TextDocumentDomainObject, texts: Seq[TextDomainObject]) extends Editor with ImcmsServicesSupport {

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


class TextEditorUI extends VerticalLayout with FullSize {
  val mb = new MenuBar with FullWidth
  val miShowHistory = mb.addItem("Show history")
  val miRestore = mb.addItem("Restore")
  val miHelp = mb.addItem("Help")
  val tsTexts = new TabSheet with FullSize
  val

  private val lytFormat = new FormLayout

  this.addComponents( mb, lytFormat, tsTexts)
  setExpandRatio(tsTexts, 1f)
}



class FullScreenEditorUI(caption: String) extends CustomComponent with FullSize {
  private val lytContent = new VerticalLayout with FullSize
  private val lytComponents = new GridLayout(1, 3) with Spacing with Margin with UndefinedSize
  private val pnlCaption = new Panel(caption) with FullHeight
  private val lytButtons = new HorizontalLayout with Spacing with UndefinedSize

  lytContent.addComponent(lytComponents)
  lytContent.setComponentAlignment(lytComponents, Alignment.MIDDLE_CENTER)
  setCompositionRoot(lytContent)

  object buttons {
    val btnSave = new Button("Save")
    val btnSaveAndClose = new Button("Save & Close")
    val btnClose = new Button("Close")
  }

  lytButtons.addComponents(buttons.btnSave, buttons.btnSaveAndClose, buttons.btnClose)

  lytComponents.addComponent(pnlCaption, 0, 0)
  lytComponents.addComponent(lytButtons, 0, 2)
  lytComponents.setComponentAlignment(lytButtons, Alignment.TOP_CENTER)

  def mainUI: Component = lytComponents.getComponent(0, 1)
  def mainUI_=(component: Component) {
    lytComponents.removeComponent(0, 1)
    lytComponents.addComponent(component, 0, 1)

    lytComponents.setComponentAlignment(component, Alignment.TOP_CENTER)
  }
}
