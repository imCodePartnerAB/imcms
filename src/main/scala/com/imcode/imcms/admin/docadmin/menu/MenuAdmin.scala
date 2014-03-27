package com.imcode
package imcms
package admin.docadmin.menu


import com.imcode.imcms.admin.docadmin.EditorContainerView
import com.imcode.imcms.mapping.container.TextDocMenuContainer
import java.util.Locale
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.component.dialog.ConfirmationDialog
import com.imcode.imcms.ImcmsServicesSupport

import com.imcode.imcms.vaadin.Current

@com.vaadin.annotations.Theme("imcms")
class MenuAdmin extends UI with Log4jLoggerSupport with ImcmsServicesSupport {

  override def init(request: VaadinRequest) {
    setLocale(new Locale(Current.imcmsUser.getLanguageIso639_2))
    getLoadingIndicatorConfiguration.setFirstDelay(1)



  }

  //  private def mkContent(request: VaadinRequest): Component = {
  //    import PartialFunction.condOpt
  //
  //    val contextPath = Current.contextPath
  //    val pathInfo = request.getPathInfo
  //    val docOpt =
  //      for {
  //        docId <- request.getParameter("meta_id") |> NonNegInt.unapply
  //        doc <- imcmsServices.getDocumentMapper.getDocument[DocumentDomainObject](docId).asOption
  //      } yield doc
  //
  //    val titleOpt = request.getParameter("label").trimToOption
  //    val returnUrlOpt = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption
  //
  //    docOpt.flatMap {
  //      doc =>
  //        val docId = doc.getId
  //
  //        condOpt(pathInfo) {
  //          case null | "" | "/" => wrapDocEditor(request, doc)
  //        } orElse {
  //          condOpt(pathInfo, doc, request.getParameter("menu_no")) {
  //            case ("/menu", textDoc: TextDocumentDomainObject, NonNegInt(menuNo)) =>
  //              val title = titleOpt.getOrElse("menu_editor.title".f(docId, menuNo))
  //              val returnUrl = returnUrlOpt.getOrElse(
  //                s"$contextPath/servlet/AdminDoc?meta_id=$docId&flags=${ImcmsConstants.DISPATCH_FLAG__EDIT_MENU}&editmenu=$menuNo"
  //              )
  //
  //              wrapTextDocMenuEditor(MenuEditorParameters(textDoc, menuNo, title, returnUrl))
  //          }
  //        } orElse {
  //          condOpt(pathInfo, doc, request.getParameter("img")) {
  //            case ("/image", textDoc: TextDocumentDomainObject, NonNegInt(imageNo)) =>
  //              wrapTextDocImageEditor(request, textDoc, imageNo)
  //          }
  //        }
  //    } getOrElse {
  //      new Label("N/A")
  //    }
  //  }

  def wrapTextDocMenuEditor(params: MenuEditorParameters) = new EditorContainerView(params.title) |>> {
    w =>
      val doc = params.doc
      val docId = doc.getId
      val menuNo = params.menuNo
      val menu = params.doc.getMenu(menuNo)

      val editor = new MenuEditor(doc, menu) |>> {
        _.view.setSize(900, 600)
      }

      w.mainComponent = editor.view

      w.buttons.btnReset.addClickHandler {
        _ =>
          editor.resetValues()
      }

      w.buttons.btnSaveAndClose.addClickHandler {
        _ =>
          save(close = true)
      }

      w.buttons.btnClose.addClickHandler {
        _ =>
          val editedMenu = editor.collectValues().right.get
          if (editedMenu.getSortOrder == menu.getSortOrder && editedMenu.getMenuItems.deep == menu.getMenuItems.deep) {
            closeEditor()
          } else {
            new ConfirmationDialog("menu_editor_dlg.confirmation.close_without_saving.title".i,
              "menu_editor_dlg.confirmation.close_without_saving.message".i) |>> {
              dlg =>
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
        editor.collectValues().right.get |> {
          menu =>
            imcmsServices.getDocumentMapper.saveTextDocMenu(TextDocMenuContainer.of(doc.getVersionRef, menuNo, menu), Current.imcmsUser)
            Current.page.showInfoNotification("menu_editor.notification.saved".i)

            if (close) {
              Current.page.open(params.returnUrl, "_self")
            }
        }
      }
  }
}