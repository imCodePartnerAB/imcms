package com.imcode
package imcms
package admin.docadmin.text

import java.util.Locale
import com.imcode.imcms.admin.docadmin.EditorContainerView
import com.imcode.imcms.api.DocumentVersion
import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.mapping.container.{LoopEntryRef, VersionRef}
import com.imcode.imcms.mapping.DocumentSaveException
import com.imcode.imcms.vaadin.Current
import com.imcode.NonNegInt
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.UI
import imcode.server.document.{TextDocumentPermissionSetDomainObject, NoPermissionToEditDocumentException}
import _root_.imcode.server.ImcmsConstants
import imcode.server.document.textdocument.{TextDocumentDomainObject, NoPermissionToAddDocumentToMenuException, TextDomainObject}
import _root_.imcode.util.{ShouldNotBeThrownException, ShouldHaveCheckedPermissionsEarlierException}
import com.imcode.imcms.vaadin.component._

@com.vaadin.annotations.Theme("imcms")
class TextEditorUI extends UI with Log4jLogger with ImcmsServicesSupport {

  val LoopEntryRefRE = """(\d+)_(\d+)""".r

  override def init(request: VaadinRequest) {
    val user = Current.imcmsUser
    val docId = request.getParameter("meta_id").toInt
    val doc = imcmsServices.getDocumentMapper.getWorkingDocument(docId) : TextDocumentDomainObject

    val permissionSet: TextDocumentPermissionSetDomainObject =
      user.getPermissionSetFor(doc).asInstanceOf[TextDocumentPermissionSetDomainObject]

    // fixme: v4.
    if (!permissionSet.getEditTexts) {
      //AdminDoc.adminDoc(documentId, user, request, res, getServletContext)
      return
    }

    getLoadingIndicatorConfiguration.setFirstDelay(1)
    getLoadingIndicatorConfiguration.setSecondDelay(2)
    getLoadingIndicatorConfiguration.setThirdDelay(3)

    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration |> { lic =>
      lic.setFirstDelay(1)
      lic.setSecondDelay(2)
      lic.setThirdDelay(3)
    }

    val contextPath = Current.contextPath
    val pathInfo = request.getPathInfo

    val titleOpt = request.getParameter("label").trimToOption
    val returnUrlOpt = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption
    val textNo = request.getParameter("txt").toInt
    val versioRef = VersionRef.of(docId, DocumentVersion.WORKING_VERSION_NO)
    val loopEntryRefOpt = request.getParameter("loopEntryRef").trimToEmpty match {
      case LoopEntryRefRE(NonNegInt(loopNo), NonNegInt(entryNo)) => Some(LoopEntryRef.of(loopNo, entryNo))
      case _ => None
    }

    val formats = request.getParameterMap.get("format").asOption.getOrElse(Array.empty)
    val rowsCountOpt = request.getParameter("rows") |> {
      case NonNegInt(rows) => Some(rows)
      case _ => None
    }

    val showModeText = formats.isEmpty || formats.contains("text")
    val showModeHtml = formats.isEmpty || formats.contains("html") || formats.contains("none")
    val showModeEditor = formats.isEmpty && rowsCountOpt.isEmpty

    val (format, canChangeFormat) = (showModeText, showModeHtml) match {
      case (true, false) => (TextDomainObject.Format.PLAIN_TEXT, false)
      case (false, true) => (TextDomainObject.Format.HTML, false)
      case _ => (TextDomainObject.Format.HTML, true)
    }

    val opts = TextEditorOpts(format, rowsCountOpt, canChangeFormat, showModeEditor)

    val editor = new TextEditor(versioRef, loopEntryRefOpt, textNo, opts)

    setContent(wrapTextDocTextEditor(request, editor))
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
  // [-] Remove Xina, install CKEditor
  //
  // [-] Detect type using text format
  // [-] Escape HTML
  /*
          <div id="editor"><%
            if (rows == 1) { %>
              <input type="text" name="text" id="text_1row" tabindex="1" value="<%= StringEscapeUtils.escapeHtml4( textEditPage.getTextString() ) %>" style="width:100%;" /><%
            } else { %>
              <textarea name="text" tabindex="1" id="text" cols="125" rows="<%= (rows > 1) ? rows : 25 %>" style="overflow: auto; width: 100%;"><%= StringEscapeUtils.escapeHtml4( textEditPage.getTextString() ) %></textarea><%
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
  def wrapTextDocTextEditor(request: VaadinRequest, editor: TextEditor): EditorContainerView = {
    val w = new EditorContainerView("text_editor.title".i)

    //      val title = request.getParameter("label").trimToNull match {
    //        case null => s"Document ${doc.getId} text no $textNo"
    //        case label => label |> StringEscapeUtils.escapeHtml4
    //      }
    //
    //      w.setTitle(title)

    w.mainComponent = editor.view

    w.buttons.btnSave.addClickHandler {
      _ =>
        save(closeOnSuccess = false)
    }
    w.buttons.btnSaveAndClose.addClickHandler {
      _ =>
        save(closeOnSuccess = true)
    }

    w.buttons.btnClose.addClickHandler {
      _ =>
        closeEditor()
    }

    w.buttons.btnReset.addClickHandler {
      _ =>
        editor.resetValues()
    }

    def closeEditor() {
      val docId = request.getParameter("meta_id").toInt
      val contextPath = Current.contextPath
      val returnUrl = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption.getOrElse(
        s"$contextPath/servlet/AdminDoc?meta_id=$docId&flags=${ImcmsConstants.DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_TEXTS}"
      )

      Current.page.setLocation(returnUrl)
    }

    def save(closeOnSuccess: Boolean) {
      editor.collectValues().right.get |> {
        container =>
        // -check permissionSet.getEditTexts()
          try {
            val user = Current.imcmsUser
            imcmsServices.getDocumentMapper.saveTextDocTexts(container, Current.imcmsUser)
            //imcmsServices.updateMainLog(s"Text $textNo in [${doc.getId}] modified by user: [${user.getFullName}]");
            if (closeOnSuccess) closeEditor()
          } catch {
            case e: NoPermissionToEditDocumentException => throw new ShouldHaveCheckedPermissionsEarlierException(e)
            case e: NoPermissionToAddDocumentToMenuException => throw new ShouldHaveCheckedPermissionsEarlierException(e)
            case e: DocumentSaveException => throw new ShouldNotBeThrownException(e)
          }
      }
    }

    w
  }

}
