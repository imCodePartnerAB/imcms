package com.imcode
package imcms
package admin.doc.projection

import imcode.server.document._
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.admin.doc.{DocEditorDialog, DocViewer}
import com.imcode.imcms.vaadin.ui.dialog.{InformationDialog, ConfirmationDialog}
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._
import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import com.imcode.imcms.mapping.DocumentMapper
import com.imcode.imcms.admin.doc.content.textdoc.NewTextDocContentEditor
import com.vaadin.ui.UI

/**
 * Common operations associated with docs projections such as edit, view, delete etc.
 *
 * @param projection
 */
// todo: allow edit several at once
// todo: check permissions
class DocsProjectionOps(projection: DocsProjection) extends ImcmsServicesSupport with Log4jLoggerSupport {

  def mkDocOfType[T <: DocumentDomainObject : ClassTag] {
    projection.selection |> {
      case Seq(selectedDoc: TextDocumentDomainObject) =>
        val (newDocType, dlgCaption) = scala.reflect.classTag[T].runtimeClass match {
          case c if c == classOf[TextDocumentDomainObject] => DocumentTypeDomainObject.TEXT_ID -> "New Text Document"
          case c if c == classOf[FileDocumentDomainObject] => DocumentTypeDomainObject.FILE_ID -> "New File Document"
          case c if c == classOf[UrlDocumentDomainObject] => DocumentTypeDomainObject.URL_ID -> "New Url Document"
          case c if c == classOf[HtmlDocumentDomainObject] => DocumentTypeDomainObject.HTML_ID -> "New Html Document"
        }

        val newDoc = imcmsServices.getDocumentMapper.createDocumentOfTypeFromParent(newDocType, selectedDoc, UI.getCurrent.imcmsUser)

        import projection.ui

        new DocEditorDialog(dlgCaption, newDoc) |>> { dlg =>
          dlg.setOkButtonHandler {
            dlg.docEditor.collectValues() match {
              case Left(errors) =>
                ui.rootWindow.showErrorNotification(errors.mkString(", "))

              case Right((editedDoc, i18nMetas)) =>
                val saveOpts = dlg.docEditor match {
                  case editor: NewTextDocContentEditor if editor.ui.chkCopyI18nMetaTextsToTextFields.checked =>
                    java.util.EnumSet.of(DocumentMapper.SaveOpts.CopyI18nMetaTextsIntoTextFields)

                  case _ =>
                    java.util.EnumSet.noneOf(classOf[DocumentMapper.SaveOpts])
                }

                imcmsServices.getDocumentMapper.saveNewDocument(
                  editedDoc,
                  i18nMetas.asJava,
                  saveOpts,
                  UI.getCurrent.imcmsUser
                )
                ui.rootWindow.showInfoNotification("New document has been saved")
                projection.reload()
                dlg.close()
            }
          }
        } |> ui.rootWindow.addWindow

      case _ =>
        new InformationDialog("Please select a text document/profile") |>> projection.ui.rootWindow.addWindow
    }
  }


  def deleteSelectedDocs() {
    whenNotEmpty(projection.selection) { docs =>
      new ConfirmationDialog("Delete selected document(s)?") |>> { dlg =>
        dlg.setOkButtonHandler {
          try {
            docs.foreach(doc => imcmsServices.getDocumentMapper.deleteDocument(doc, UI.getCurrent.imcmsUser))
            dlg.rootWindow.showInfoNotification("Documents has been deleted")
            dlg.close()
          } finally {
            projection.reload()
          }
        }
      } |> projection.ui.rootWindow.addWindow
    }
  }


  def showSelectedDoc() {
    whenSingle(projection.selection) { doc =>
      DocViewer.showDocViewDialog(projection.ui, doc.getId)
    }
  }


  def copySelectedDoc() {
    whenSingle(projection.selection) { doc =>
      imcmsServices.getDocumentMapper.copyDocument(doc, UI.getCurrent.imcmsUser)
      projection.reload()
      projection.ui.rootWindow.showInfoNotification("Document has been copied")
    }
  }


  def editSelectedDoc() {
    whenSingle(projection.selection) { doc =>
      val rootWindow = projection.ui.rootWindow

      new DocEditorDialog("Edit document", doc) |>> { dlg =>
        dlg.setOkButtonHandler {
          dlg.docEditor.collectValues() match {
            case Left(errors) =>
              rootWindow.showErrorNotification("Unable to save document", errors.mkString(", "))

            case Right((editedDoc, i18nMetas)) =>
              imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.asJava, UI.getCurrent.imcmsUser)
              rootWindow.showInfoNotification("Document has been saved")
              projection.reload()
              dlg.close()
          }
        }
      } |> rootWindow.addWindow
    }
  }
}
