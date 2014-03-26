package com.imcode
package imcms
package admin.docadmin

import com.imcode.imcms.mapping.container.TextDocMenuContainer
import java.util.Locale
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.admin.doc.DocEditor
import com.vaadin.ui._
import com.vaadin.server._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.component.dialog.ConfirmationDialog
import com.imcode.imcms.ImcmsServicesSupport

import _root_.imcode.server.document.textdocument._
import _root_.imcode.server.ImcmsConstants
import imcode.server.document.DocumentDomainObject

import com.imcode.imcms.admin.docadmin.menu.{MenuEditorParameters, MenuEditor}
import com.imcode.imcms.admin.docadmin.image.ImagesEditor
import com.imcode.imcms.vaadin.Current
import scala.collection.JavaConverters._

// todo: validate params in filter, create params wrapper, pass params into DocAdmin (no need to examine path in init)?
// todo: template/group
// todo: add [im]cms path element: /[im]cms/sysadmin/...; [im]cms/docadmin/...
@com.vaadin.annotations.Theme("imcms")
class DocAdmin extends UI with Log4jLoggerSupport with ImcmsServicesSupport {
  ui =>

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
        docId <- request.getParameter("meta_id") |> NonNegInt.unapply
        doc <- imcmsServices.getDocumentMapper.getDocument[DocumentDomainObject](docId).asOption
      } yield doc

    val titleOpt = request.getParameter("label").trimToOption
    val returnUrlOpt = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption

    docOpt.flatMap {
      doc =>
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
    val editorContainerView = new EditorContainerView("doc.edit_image.title".i)

    editorContainerView.mainComponent = imageEditor.view
    editorContainerView.buttons.btnSave.addClickHandler {
      _ =>
    }
    editorContainerView.buttons.btnReset.addClickHandler {
      _ => imageEditor.resetValues()
    }
    editorContainerView.buttons.btnSaveAndClose.addClickHandler {
      _ =>
    }
    editorContainerView.buttons.btnClose.addClickHandler {
      _ =>
    }

    imageEditor.view.setSize(900, 600)
    imageEditor.resetValues()

    editorContainerView
  }


  def wrapDocEditor(request: VaadinRequest, doc: DocumentDomainObject): EditorContainerView = {
    new EditorContainerView("doc.edit_properties.title".f(doc.getId)) |>> {
      w =>
        val editor = new DocEditor(doc)

        w.mainComponent = editor.view

        editor.view.setSize(900, 600)

        w.buttons.btnSave.addClickHandler {
          _ =>
            editor.collectValues() match {
              case Left(errors) => Current.page.showConstraintViolationNotification(errors)
              case Right((editedDoc, i18nMetas)) =>
                try {
                  imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.asJava, Current.imcmsUser)
                  Current.page.showInfoNotification("notification.doc.saved".i)
                  Current.page.open(Current.contextPath, "_self")
                } catch {
                  case e: Exception => Current.page.showUnhandledExceptionNotification(e)
                }
            }
        }

        w.buttons.btnClose.addClickHandler {
          _ =>
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