package com.imcode
package imcms
package admin.doc.projection

import com.imcode.imcms.vaadin.Current
import _root_.imcode.server.document._
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.admin.doc.{DocEditorDialog, DocOpener}
import com.imcode.imcms.vaadin.component.dialog.{Dialog, InformationDialog, ConfirmationDialog}

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
        new InformationDialog("Please select a text document you want to use as a template".i).show()

      case selection => selection.head |> { ref =>
        (imcmsServices.getDocumentMapper.getDefaultDocument(ref.getDocId(), ref.getDocLanguage()) : DocumentDomainObject) match {
          case null => showMissingDocNotification()

          case selectedDoc if !selectedDoc.isInstanceOf[TextDocumentDomainObject] =>
            new InformationDialog("Please select a text document".i).show()

          case selectedDoc: TextDocumentDomainObject =>
            val (newDocType, dlgCaption) = scala.reflect.classTag[T].runtimeClass match {
              case c if c == classOf[TextDocumentDomainObject] => DocumentTypeDomainObject.TEXT_ID -> "new_doc_dlg_title.text".i
              case c if c == classOf[FileDocumentDomainObject] => DocumentTypeDomainObject.FILE_ID -> "new_doc_dlg_title.file".i
              case c if c == classOf[UrlDocumentDomainObject] => DocumentTypeDomainObject.URL_ID -> "new_doc_dlg_title.url".i
              case c if c == classOf[HtmlDocumentDomainObject] => DocumentTypeDomainObject.HTML_ID -> "new_doc_dlg_title.html".i
            }

            val newDoc = imcmsServices.getDocumentMapper.createDocumentOfTypeFromParent(newDocType, selectedDoc, projection.user)

            val dialog = new DocEditorDialog(dlgCaption, newDoc)
            Dialog.bind(dialog) { case (editedDoc, i18nMetas) =>
              val saveOpts = dialog.editor.contentEditor match {
                case contentEditor: NewTextDocContentEditor if contentEditor.view.chkCopyI18nMetaTextsToTextFields.checked =>
                  java.util.EnumSet.of(DocumentMapper.SaveOpts.CopyDocAppearenceIntoTextFields)

                case _ =>
                  java.util.EnumSet.noneOf(classOf[DocumentMapper.SaveOpts])
              }

              imcmsServices.getDocumentMapper.saveNewDocument(
                editedDoc,
                i18nMetas.values.to[Set].asJava,
                saveOpts,
                projection.user
              )
              Current.page.showInfoNotification("New document has been saved".i)
              projection.reload()
            }
            dialog.show()
        }
      }
    }
  }


  def deleteSelectedDocs() {
    whenNotEmpty(projection.selection) { refs =>
      new ConfirmationDialog("Delete selected document(s)?".i) |>> { dlg =>
        dlg.setOkButtonHandler {
          try {
            refs.foreach(ref => imcmsServices.getDocumentMapper.deleteDocument(ref.getDocId(), projection.user))
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
      DocOpener.openDoc(ref.getDocId())
    }
  }


  def copySelectedDoc() {
    whenSingleton(projection.selection) { ref =>
      (imcmsServices.getDocumentMapper.getDefaultDocument(ref.getDocId(), ref.getDocLanguage()) : DocumentDomainObject) match {
        case null => showMissingDocNotification()
        case doc =>
          imcmsServices.getDocumentMapper.copyDocument(ref, projection.user)
          projection.reload()
          Current.page.showInfoNotification("Document has been copied".i)
      }
    }
  }


  def editSelectedDoc() {
    whenSingleton(projection.selection) { ref =>
      (imcmsServices.getDocumentMapper.getWorkingDocument(ref.getDocId(), ref.getDocLanguage()) : DocumentDomainObject) match {
        case null => showMissingDocNotification()
        case doc =>
          val dialog = new DocEditorDialog(s"Edit document ${doc.getId}".i, doc)

          Dialog.bind(dialog) { case (editedDoc, i18nMetas) =>
            imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.values.to[Set].asJava, projection.user)
            Current.page.showInfoNotification("Document has been saved".i)
            projection.reload()
          }

          dialog.show()
      }
    }
  }
}
