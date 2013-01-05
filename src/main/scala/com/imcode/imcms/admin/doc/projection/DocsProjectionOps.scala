package com.imcode
package imcms
package admin.doc.projection

import _root_.imcode.server.document.{UrlDocumentDomainObject, FileDocumentDomainObject, DocumentTypeDomainObject, DocumentDomainObject}
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.admin.doc.{DocEditorDialog, DocViewer, DocEditor}
import com.imcode.imcms.vaadin.ui.dialog.ConfirmationDialog
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._
import scala.collection.JavaConverters._
import scala.reflect.ClassTag

// todo: add callbacks???
class DocsProjectionOps(projection: DocsProjection) extends ImcmsServicesSupport with Log4jLoggerSupport {

  def mkDocOfType[T <: DocumentDomainObject : ClassTag] {
    PartialFunction.condOpt(projection.selection) {
      case Seq(selectedDoc: TextDocumentDomainObject) =>
        val (newDocType, dlgCaption) = scala.reflect.classTag[T].runtimeClass match {
          case c if c == classOf[TextDocumentDomainObject] => DocumentTypeDomainObject.TEXT_ID -> "New text document"
          case c if c == classOf[FileDocumentDomainObject] => DocumentTypeDomainObject.FILE_ID -> "New file document"
          case c if c == classOf[UrlDocumentDomainObject] => DocumentTypeDomainObject.URL_ID -> "New url document"
        }

        val newDoc = imcmsServices.getDocumentMapper.createDocumentOfTypeFromParent(newDocType, selectedDoc, projection.ui.getApplication.imcmsUser)

        import projection.ui

        new DocEditorDialog(dlgCaption, newDoc) |>> { dlg =>
          dlg.setOkButtonHandler {
            dlg.docEditor.collectValues() match {
              case Left(errors) => ui.rootWindow.showErrorNotification(errors.mkString(","))
              case Right((editedDoc, i18nMetas)) =>
                try {
                  imcmsServices.getDocumentMapper.saveNewDocument(editedDoc, i18nMetas.asJava, ui.getApplication.imcmsUser)
                  ui.rootWindow.showInfoNotification("New document has been created")
                  projection.reload()
                } catch {
                  case e => ui.rootWindow.showErrorNotification("Failed to create new document", e.getStackTraceString)
                }
            }
          }
        } |> ui.rootWindow.addWindow
    }
  }


  def deleteSelectedDocs() {
    whenNotEmpty(projection.selection) { docs =>
      new ConfirmationDialog("Delete selected document(s)?") |>> { dlg =>
        dlg.setOkButtonHandler {
          try {
            docs.foreach(doc => imcmsServices.getDocumentMapper.deleteDocument(doc, projection.ui.getApplication.imcmsUser))
          } catch {
            case e =>
              logger.error("Document delete error", e)
              projection.ui.rootWindow.showErrorNotification("Error deleging document(s)", e.getStackTraceString)
          } finally {
            // todo: update ranges ???
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
      imcmsServices.getDocumentMapper.copyDocument(doc, projection.ui.getApplication.imcmsUser)
      projection.reload()
      projection.ui.rootWindow.showInfoNotification("Document has been copied")
    }
  }

  // todo: allow change several at once???
  // todo: permissions
  def editSelectedDoc() {
    whenSingle(projection.selection) { doc =>
      val rootWindow = projection.ui.rootWindow

      new DocEditorDialog("Edit document", doc) { dlg =>
        dlg.setOkButtonHandler {
          dlg.docEditor.collectValues() match {
            case Left(errors) => rootWindow.showErrorNotification(errors.mkString(","))
            case Right((editedDoc, i18nMetas)) =>
              try {
                imcmsServices.getDocumentMapper.saveDocument(editedDoc, i18nMetas.asJava, rootWindow.getApplication.imcmsUser)
                rootWindow.showInfoNotification("Document has been saved")
                projection.reload()
              } catch {
                case e => rootWindow.showErrorNotification("Failed to save document", e.getStackTraceString)
              }
          }
        }
      } |> rootWindow.addWindow
    }
  }
}
