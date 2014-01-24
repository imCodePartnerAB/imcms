package com.imcode
package imcms
package admin.docadmin

import java.util.Locale
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.api.{DocumentVersion, DocRef}
import com.imcode.imcms.admin.doc.DocEditor
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.component.dialog.ConfirmationDialog
import com.imcode.imcms.mapping.DocumentSaveException
import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.dao.TextDocDao
import org.apache.commons.lang3.StringEscapeUtils

import _root_.imcode.server.document.textdocument._
import _root_.imcode.server.{ImcmsConstants, Imcms}
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.util.{ShouldNotBeThrownException, ShouldHaveCheckedPermissionsEarlierException}
import _root_.imcode.server.document.{DocumentDomainObject, NoPermissionToEditDocumentException}

import com.imcode.imcms.admin.docadmin.menu.{MenuEditorParameters, MenuEditor}
import com.imcode.imcms.admin.docadmin.text.{TextEditor, TextEditorParameters}
import com.imcode.imcms.admin.docadmin.image.ImagesEditor
import com.imcode.imcms.vaadin.Current

// todo: validate params in filter, create params wrapper, pass params into DocAdmin (no need to examine path in init)?
// todo: template/group
// todo: add [im]cms path element: /[im]cms/sysadmin/...; [im]cms/docadmin/...
@com.vaadin.annotations.Theme("imcms")
class DocAdmin extends UI with Log4jLoggerSupport with ImcmsServicesSupport { ui =>

  // todo: move logic into filter
  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))

    setContent(mkContent(request))

    getLoadingIndicatorConfiguration.setFirstDelay(1)
  }


  private def mkContent(request: VaadinRequest): Component = {
    import PartialFunction.condOpt

    val contextPath = Current.contextPath
    val pathInfo = request.getPathInfo
    val docOpt =
      for {
        metaId <- request.getParameter("meta_id") |> NonNegInt.unapply
        doc <- imcmsServices.getDocumentMapper.getDocument[DocumentDomainObject](metaId).asOption
      } yield doc

    val titleOpt = request.getParameter("label").trimToOption
    val returnUrlOpt = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption

    docOpt.flatMap { doc =>
      val docId = doc.getId

      condOpt(pathInfo) {
        case null | "" | "/" => wrapDocEditor(request, doc)
      } orElse {
        condOpt(pathInfo, doc, request.getParameter("menu_no")) {
          case ("/menu", textDoc: TextDocumentDomainObject, NonNegInt(menuNo)) =>
            val title = titleOpt.getOrElse("menu_editor.title".f(docId, menuNo))
            val returnUrl = returnUrlOpt.getOrElse(
              s"$contextPath/servlet/AdminDoc?meta_id=$docId&flags=${ImcmsConstants.DISPATCH_FLAG__EDIT_MENU}&editmenu=$menuNo"
            )

            wrapTextDocMenuEditor(MenuEditorParameters(textDoc, menuNo, title, returnUrl))
        }
      } orElse {
        condOpt(pathInfo, doc, request.getParameter("txt")) {
          case ("/text", textDoc: TextDocumentDomainObject, NonNegInt(textNo)) =>
            wrapTextDocTextEditor(request, textDoc, textNo)
        }
      } orElse {
        condOpt(pathInfo, doc, request.getParameter("img")) {
          case ("/image", textDoc: TextDocumentDomainObject, NonNegInt(imageNo)) =>
            wrapTextDocImageEditor(request, textDoc, imageNo)
        }
      }
    } getOrElse {
      new Label("N/A")
    }
  }


  def wrapTextDocImageEditor(request: VaadinRequest, doc: TextDocumentDomainObject, imageNo: Int): EditorContainerView = {
    val imageEditor = new ImagesEditor(doc.getRef, imageNo)
    val editorContainerView =  new EditorContainerView("doc.edit_image.title".i)

    editorContainerView.mainComponent = imageEditor.view
    editorContainerView.buttons.btnSave.addClickHandler { _ => }
    editorContainerView.buttons.btnReset.addClickHandler { _ => imageEditor.resetValues() }
    editorContainerView.buttons.btnSaveAndClose.addClickHandler { _ => }
    editorContainerView.buttons.btnClose.addClickHandler { _ => }

    imageEditor.view.setSize(900, 600)
    imageEditor.resetValues()

    editorContainerView
  }


  def wrapDocEditor(request: VaadinRequest, doc: DocumentDomainObject): EditorContainerView = {
    new EditorContainerView("doc.edit_properties.title".f(doc.getId)) |>> { w =>
      val editor = new DocEditor(doc)

      w.mainComponent = editor.view

      editor.view.setSize(900, 600)

      w.buttons.btnSave.addClickHandler { _ =>
        editor.collectValues() match {
          case Left(errors) => Current.page.showConstraintViolationNotification(errors)
          case Right((editedDoc, i18nMetas)) =>
            try {
              imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.values.to[Set].asJava, Current.imcmsUser)
              Current.page.showInfoNotification("notification.doc.saved".i)
              Current.page.open(Current.contextPath, "_self")
            } catch {
              case e: Exception => Current.page.showUnhandledExceptionNotification(e)
            }
        }
      }

      w.buttons.btnClose.addClickHandler { _ =>
        Current.page.open(Current.contextPath, "_self")
      }

      Current.page.getUriFragment.asOption.map(_.toLowerCase).foreach {
        case "info" => editor.metaEditor.view.treeEditors.selection = "doc_meta_editor.menu_item.life_cycle"
        case "access" => editor.metaEditor.view.treeEditors.selection = "doc_meta_editor.menu_item.access"
        case "appearance" => editor.metaEditor.view.treeEditors.selection = "doc_meta_editor.menu_item.appearance"
        case "content" => editor.view.setSelectedTab(1)
        case _ =>
      }
    }
  }


  def wrapTextDocMenuEditor(params: MenuEditorParameters) = new EditorContainerView(params.title) |>> { w =>
    val doc = params.doc
    val docId = doc.getId
    val menuNo = params.menuNo
    val menu = params.doc.getMenu(menuNo)

    val editor = new MenuEditor(doc, menu) |>> { _.view.setSize(900, 600) }

    w.mainComponent = editor.view

    w.buttons.btnReset.addClickHandler { _ =>
      editor.resetValues()
    }

    w.buttons.btnSaveAndClose.addClickHandler { _ =>
      save(close = true)
    }

    w.buttons.btnClose.addClickHandler { _ =>
      val editedMenu = editor.collectValues().right.get
      if (editedMenu.getSortOrder == menu.getSortOrder && editedMenu.getMenuItems.deep == menu.getMenuItems.deep) {
        closeEditor()
      } else {
        new ConfirmationDialog("menu_editor_dlg.confirmation.close_without_saving.title".i,
                               "menu_editor_dlg.confirmation.close_without_saving.message".i) |>> { dlg =>
          dlg.setOkButtonHandler {
            closeEditor()
            dlg.close()
          }
        } |> Current.ui.addWindow
      }
    }

    def closeEditor() {
      Current.page.open(params.returnUrl, "_self")
    }

    def save(close: Boolean) {
      editor.collectValues().right.get |> { menu =>
        imcmsServices.getDocumentMapper.saveTextDocMenu(menu, Current.imcmsUser)
        Current.page.showInfoNotification("menu_editor.notification.saved".i)

        if (close) {
          Current.page.open(params.returnUrl, "_self")
        }
      }
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
  // check ImcmsConstants.REQUEST_PARAM__RETURN_URL
  //if (returnURL != null) {
  //res.sendRedirect(returnURL);
  //} else {
  //res.sendRedirect("AdminDoc?meta_id=" + meta_id + "&flags="
  //+ imcode.server.ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS);
  //}

  // [-] <%= showModeEditor ? "Editor/" : "" %>HTML
  def wrapTextDocTextEditor(request: VaadinRequest, doc: TextDocumentDomainObject, textNo: Int): EditorContainerView = new EditorContainerView |>> { w =>
    val title = request.getParameter("label").trimToNull match {
      case null => s"Document ${doc.getId} text no $textNo"
      case label => label |> StringEscapeUtils.escapeHtml4
    }

    w.setTitle(title)

    val formats = request.getParameterMap.get("format") match {
      case null => Set.empty[String]
      case array => array.toSet
    }

    val rowsCountOpt = request.getParameter("rows") |> {
      case  NonNegInt(rows) => Some(rows)
      case _ => None
    }

    val showModeText = formats.isEmpty || formats.contains("text")
    val showModeHtml = formats.isEmpty || formats.contains("html") || formats.contains("none")
    val showModeEditor = formats.isEmpty && rowsCountOpt.isEmpty

    val ContentRefExt = """(\d+)_(\d+)""".r
    val contentRefOpt = request.getParameter("contentRef") match {
      case ContentRefExt(loopNo, contentNo) => ContentRef.of(loopNo.toInt, contentNo.toInt).asOption
      case _ => None
    }

    val textDao = imcmsServices.getManagedBean(classOf[TextDocDao])
    val texts = textDao.getTexts(DocRef.of(doc.getId, DocumentVersion.WORKING_VERSION_NO), textNo, contentRefOpt, createIfNotExists = true)

    for (text <- texts.asScala if text.getI18nDocRef == null) {
      text.setType(TextDomainObject.TEXT_TYPE_HTML)
    }

    // Current language
    val preferredLanguage = Imcms.getUser.getDocGetterCallback.documentLanguages.preferred

    val (format, canChangeFormat) = (showModeText, showModeHtml) match {
      case (true, false) => (TextDomainObject.Format.PLAIN_TEXT, false)
      case (false, true) => (TextDomainObject.Format.HTML, false)
      case _ => (TextDomainObject.Format.values()(texts.asScala.head.getType), true)
    }

    val editor = new TextEditor(texts.asScala, TextEditorParameters(format, rowsCountOpt, canChangeFormat, showModeEditor))

    w.mainComponent = editor.view
    editor.view.setSize(900, 600)

    w.buttons.btnSave.addClickHandler { _ =>
      save(closeAfterSave = false)
    }
    w.buttons.btnSaveAndClose.addClickHandler { _ =>
      save(closeAfterSave = true)
    }

    w.buttons.btnClose.addClickHandler { _ =>
      closeEditor()
    }

    def closeEditor() {
      Current.page.open(Current.contextPath, "_self")
    }

    def save(closeAfterSave: Boolean) {
      editor.collectValues().right.get |> { texts =>
        // -check permissionSet.getEditTexts()
        try {
          imcmsServices.getDocumentMapper.saveTextDocTexts(texts.asJava, Current.imcmsUser)
          val user = new UserDomainObject() // todo: fixme
          imcmsServices.updateMainLog(s"Text $textNo in [${doc.getId}] modified by user: [${user.getFullName}]");
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