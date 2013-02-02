package com.imcode
package imcms
package admin.docadmin


import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui._
import com.vaadin.event.dd.{DragAndDropEvent, DropHandler}
import com.imcode.imcms.api.{ContentLanguage, DocumentVersion, DocRef, Document}
import dialog.{ConfirmationDialog, OkCancelDialog}
import java.io.File
import imcode.server.document.textdocument._
import com.vaadin.event.dd.acceptcriteria.{Not, AcceptAll, AcceptCriterion}
import com.vaadin.data.util.HierarchicalContainer
import scala.annotation.tailrec
import com.imcode.imcms.admin.doc.{DocSelectDialog, DocEditorDialog, DocViewer, DocEditor}
import com.vaadin.ui.AbstractSelect.{VerticalLocationIs, ItemDescriptionGenerator}
import com.vaadin.ui._
import java.util.{Locale, Arrays, Collections}
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.{VaadinService, Page, VaadinRequest, ExternalResource}
import com.vaadin.shared.ui.dd.VerticalDropLocation
import imcode.server.Imcms
import imcode.server.user.UserDomainObject
import imcode.util.{ShouldNotBeThrownException, ShouldHaveCheckedPermissionsEarlierException}
import imcode.server.document.NoPermissionToEditDocumentException
import com.imcode.imcms.mapping.DocumentSaveException
import java.util.EnumSet
import org.apache.commons.lang3.{StringEscapeUtils, StringUtils}
import com.imcode.imcms.ImcmsServicesSupport
import com.imcode._
import scala.Some
import com.imcode.imcms.admin.docadmin.TextEditorSettings
import com.imcode.imcms.vaadin.data.PropertyDescriptor
import scala.Some
import com.imcode.imcms.admin.docadmin.TextEditorSettings
import com.imcode.imcms.vaadin.data.PropertyDescriptor
import com.imcode.imcms.dao.TextDao
import com.imcode.imcms.admin.doc.projection.DocIdSelectWithLifeCycleIcon


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

  // [+] Select or create text in current language, set editor label
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
// [-]
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

  // [-]
  // History / RESTORE
  // [-]
  // if rows == 1 show text field, else text area if rows not defined default to 25
  // NB! showModeEditor = false if rows > 0
  //
  // ?????? editorHidden - show original i.e. w/o editoc controls ??? ?????

  // [+] Save text
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
  //
  // [-] Detect type using text format
  // [-] Escape HTML
/*
        <div id="editor"><%
	        if (rows == 1) { %>
	          <input type="text" name="text" id="text_1row" tabindex="1" value="<%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %>" style="width:100%;" /><%
	        } else { %>
            <textarea name="text" tabindex="1" id="text" cols="125" rows="<%= (rows > 1) ? rows : 25 %>" style="overflow: auto; width: 100%;"><%= StringEscapeUtils.escapeHtml( textEditPage.getTextString() ) %></textarea><%
	        } %>
        </div>
   */
  // [+] label
  // [-] validate
  // [!] Return URL
  // [-] <%= showModeEditor ? "Editor/" : "" %>HTML
  def mkWorkingDocTextEditorComponent(mappedRequest: EditWorkingDocText) = new FullScreenEditorUI() |>> { ui =>
    val title = mappedRequest.vaadinRequest.getParameter("label").trimToNull match {
      case null => s"Document ${mappedRequest.docId} text no ${mappedRequest.textNo}"
      case label => label |> StringEscapeUtils.escapeHtml4
    }

    ui.setTitle(title)

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

    val ContentRefExt = """(\d+)_(\d+)""".r
    val contentRefOpt = mappedRequest.vaadinRequest.getParameter("contentRef") match {
      case ContentRefExt(loopNo, contentNo) => ContentRef.of(loopNo.toInt, contentNo.toInt).asOption
      case _ => None
    }




    val docIdOpt = mappedRequest.vaadinRequest.getParameter("docId") |> PosInt.unapply
    val textNoOpt = mappedRequest.vaadinRequest.getParameter("textNo") |> PosInt.unapply

    val textDao = imcmsServices.getSpringBean(classOf[TextDao])
    val texts = textDao.getTexts(DocRef.of(docIdOpt.get, DocumentVersion.WORKING_VERSION_NO), textNoOpt.get, contentRefOpt, createIfNotExists = true)

    for (text <- texts.asScala if text.getDocRef == null) {
      text.setType(TextDomainObject.TEXT_TYPE_HTML)
    }

    // Current language
    val preferredLanguage = Imcms.getUser.getDocGetterCallback.contentLanguages.preferred

    val doc = app.imcmsServices.getDocumentMapper.getWorkingDocument(mappedRequest.docId).asInstanceOf[TextDocumentDomainObject]
    val (format, canChangeFormat) = (showModeText, showModeHtml) match {
      case (true, false) => (TextDomainObject.Format.PLAIN, false)
      case (false, true) => (TextDomainObject.Format.HTML, false)
      case _ => (TextDomainObject.Format.values()(texts.asScala.head.getType), true)
    }

    val editor = new TextEditor(texts.asScala, TextEditorSettings(format, rowsCountOpt, canChangeFormat, showModeEditor))

    ui.mainUI = editor.ui
    editor.ui.setSize(900, 600)

    ui.buttons.btnSave.addClickHandler {
      save(closeAfterSave = false)
    }
    ui.buttons.btnSaveAndClose.addClickHandler {
      save(closeAfterSave = true)
    }

    ui.buttons.btnClose.addClickHandler {
      closeEditor()
    }

    def closeEditor() {
      Page.getCurrent.open(UI.getCurrent.servletContext.getContextPath, "_self")
    }

    def save(closeAfterSave: Boolean) {
      editor.collectValues().right.get |> { texts =>
        // -check permissionSet.getEditTexts()
        try {
          imcmsServices.getDocumentMapper.saveTextDocTexts(texts.asJava, UI.getCurrent.imcmsUser)
          val user = new UserDomainObject() // todo: fixme
          imcmsServices.updateMainLog(s"Text ${textNoOpt.get} in [${docIdOpt.get}] modified by user: [${user.getFullName}]");
          if (closeAfterSave) closeEditor()
        } catch {
          case e: NoPermissionToEditDocumentException => throw new ShouldHaveCheckedPermissionsEarlierException(e)
          case e: NoPermissionToAddDocumentToMenuException => throw new ShouldHaveCheckedPermissionsEarlierException(e)
          case e: DocumentSaveException => throw new ShouldNotBeThrownException(e)
        }
      }
    }
  }
}










