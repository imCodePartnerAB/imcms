package com.imcode
package imcms
package admin.doc.projection

import com.imcode.imcms.vaadin.Current
import _root_.imcode.server.document._
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.admin.doc.{DocEditorDialog, DocOpener}
import com.imcode.imcms.vaadin.component.dialog.{InformationDialog, ConfirmationDialog}

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.server._
import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import com.imcode.imcms.mapping.DocumentMapper
import com.imcode.imcms.admin.doc.content.textdoc.NewTextDocContentEditor
import com.vaadin.ui.UI
import com.vaadin.server.Page

/**
 * Common operations associated with selected document(s) such as edit, view, delete etc.
 *
 * @param projection
 */
// todo: allow edit several at once
// fixme: check op permissions
// fixme: file doc - cant add files
// fixme: copy should be rewritten - predefined copy dialog should be shown to a user.
class DocsProjectionOps(projection: DocsProjection) extends ImcmsServicesSupport with Log4jLoggerSupport {

  private def showMissingDocNotification() {
    Current.page.showWarningNotification(
      "Selected document can not be found".i,
      "Please re-run search.".i
    )
  }

  def mkDocOfType[T <: DocumentDomainObject : ClassTag] {
    projection.selection match {
      case selection if selection.isEmpty || selection.size > 1 =>
        new InformationDialog("Please select a text document you want to use as a template".i) |>> Current.ui.addWindow

      case selection => selection.head |> { ref =>
        (imcmsServices.getDocumentMapper.getDefaultDocument(ref.metaId(), ref.language()) : DocumentDomainObject) match {
          case null => showMissingDocNotification()

          case selectedDoc if !selectedDoc.isInstanceOf[TextDocumentDomainObject] =>
            new InformationDialog("Please select a text document".i) |>> Current.ui.addWindow

          case selectedDoc: TextDocumentDomainObject =>
            val (newDocType, dlgCaption) = scala.reflect.classTag[T].runtimeClass match {
              case c if c == classOf[TextDocumentDomainObject] => DocumentTypeDomainObject.TEXT_ID -> "new_text_doc.dlg.title".i
              case c if c == classOf[FileDocumentDomainObject] => DocumentTypeDomainObject.FILE_ID -> "new_file_doc.dlg.title".i
              case c if c == classOf[UrlDocumentDomainObject] => DocumentTypeDomainObject.URL_ID -> "new_url_doc.dlg.title".i
              case c if c == classOf[HtmlDocumentDomainObject] => DocumentTypeDomainObject.HTML_ID -> "new_html_doc.dlg.title".i
            }

            val newDoc = imcmsServices.getDocumentMapper.createDocumentOfTypeFromParent(newDocType, selectedDoc, projection.user)

            new DocEditorDialog(dlgCaption, newDoc) |>> { dlg =>
              dlg.setOkButtonHandler {
                dlg.docEditor.collectValues() match {
                  case Left(errors) =>
                    Current.page.showErrorNotification(errors.mkString(", "))

                  case Right((editedDoc, i18nMetas)) =>
                    val saveOpts = dlg.docEditor.contentEditor match {
                      case contentEditor: NewTextDocContentEditor if contentEditor.view.chkCopyI18nMetaTextsToTextFields.checked =>
                        java.util.EnumSet.of(DocumentMapper.SaveOpts.CopyI18nMetaTextsIntoTextFields)

                      case _ =>
                        java.util.EnumSet.noneOf(classOf[DocumentMapper.SaveOpts])
                    }

                    imcmsServices.getDocumentMapper.saveNewDocument(
                      editedDoc,
                      i18nMetas.values.to[Set].asJava,
                      saveOpts,
                      projection.user
                    )
                    Current.page.showInfoNotification("New document has been created".i)
                    projection.reload()
                    dlg.close()
                }
              }
            } |> Current.ui.addWindow
        }
      }
    }
  }


  def deleteSelectedDocs() {
    whenNotEmpty(projection.selection) { refs =>
      new ConfirmationDialog("Delete selected document(s)?".i) |>> { dlg =>
        dlg.setOkButtonHandler {
          try {
            refs.foreach(ref => imcmsServices.getDocumentMapper.deleteDocument(ref.metaId(), projection.user))
            Current.page.showInfoNotification("Documents has been deleted".i)
            dlg.close()
          } finally {
            projection.reload()
          }
        }
      } |> Current.ui.addWindow
    }
  }


  def showSelectedDoc() {
    whenSingleton(projection.selection) { ref =>
      DocOpener.openDoc(ref.metaId())
    }
  }


  def copySelectedDoc() {
    whenSingleton(projection.selection) { ref =>
      (imcmsServices.getDocumentMapper.getDefaultDocument(ref.metaId(), ref.language()) : DocumentDomainObject) match {
        case null => showMissingDocNotification()
        case doc =>
          imcmsServices.getDocumentMapper.copyDocument(ref.docRef(), projection.user)
          projection.reload()
          Current.page.showInfoNotification("Document has been copied".i)
      }
    }
  }


  def editSelectedDoc() {
    whenSingleton(projection.selection) { ref =>
      (imcmsServices.getDocumentMapper.getWorkingDocument(ref.metaId(), ref.language()) : DocumentDomainObject) match {
        case null => showMissingDocNotification()
        case doc =>
          val page = Current.page
          new DocEditorDialog(s"Edit document ${doc.getMetaId}".i, doc) |>> { dlg =>
            dlg.setOkButtonHandler {
              dlg.docEditor.collectValues() match {
                case Left(errors) =>
                  page.showErrorNotification("Unable to save document".i, errors.mkString(", "))

                case Right((editedDoc, i18nMetas)) =>
                  imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.values.to[Set].asJava, projection.user)
                  page.showInfoNotification("Document has been saved".i)
                  projection.reload()
                  dlg.close()
              }
            }
          } |> Current.ui.addWindow
      }
    }
  }
}
