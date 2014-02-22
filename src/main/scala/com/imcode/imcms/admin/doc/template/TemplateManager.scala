package com.imcode
package imcms.admin.doc.template

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import _root_.imcode.server.Imcms
import com.imcode.imcms.admin.instance.file._
import org.apache.commons.io.FileUtils
import com.imcode.imcms.security.{PermissionDenied, PermissionGranted}
import java.io.FileInputStream
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._

//todo: common internal ex handler???
//todo: add related docs handling
//-upload fialog and save as handling
class TemplateManager {
  private val templateMapper = Imcms.getServices.getTemplateMapper
  private val fileRE = """(?i)(.+?)(?:\.(\w+))?""".r // filename, (optional extension)

  val view = new TemplateManagerView |>> {
    w =>
      w.tblTemplates.addValueChangeHandler {
        _ => handleSelection()
      }
      w.miUpload.setCommandHandler {
        _ =>
          new FileUploaderDialog("Upload template file") |>> {
            dlg =>
            // strips filename extension, trims and replaces spaces with underscores
              dlg.uploader.fileNameToSaveAsName = fileRE.unapplySeq(_: String).map(_.head.trim.replaceAll( """\s""", "_")).get
              dlg.setOkButtonHandler {
                for {
                  uploadedFile <- dlg.uploader.uploadedFile
                  name = dlg.uploader.view.txtSaveAsName.value // todo: check not empty
                  in = new FileInputStream(uploadedFile.file)
                } {
                  Current.ui.privileged(permission) {
                    templateMapper.saveTemplate(name, uploadedFile.name, in, dlg.uploader.view.chkOverwrite.value) match {
                      case 0 =>
                        FileUtils.deleteQuietly(uploadedFile.file)
                        reload() // ok
                      case -1 =>
                        Current.page.showErrorNotification("Template with such name allready exists")
                        sys.error("File exists")
                      case -2 =>
                        Current.page.showErrorNotification("Internal error")
                        sys.error("IO error")
                      case n =>
                        Current.page.showErrorNotification("Internal error")
                        sys.error("Unknown error")
                    }
                  }
                }
              }
          } |> Current.ui.addWindow
      }
      w.miRename.setCommandHandler {
        _ =>
          whenSelected(w.tblTemplates) {
            name =>
              new OkCancelDialog("Rename template") |>> {
                dlg =>
                  val templateRenameView = new TemplateRenameView |>> {
                    c =>
                      c.txtName.value = name
                  }

                  dlg.mainComponent = templateRenameView
                  dlg.setOkButtonHandler {
                    Current.ui.privileged(permission) {
                      templateMapper.renameTemplate(name, templateRenameView.txtName.value)
                    }

                    reload()
                  }
              } |> Current.ui.addWindow
          }
      }
      w.miEditContent.setCommandHandler {
        _ =>
          whenSelected(w.tblTemplates) {
            name =>
              new Dialog("Template file content") with CustomSizeDialog with NoContentMarginDialog |>> {
                dlg =>
                  dlg.mainComponent = new TemplateContentEditorView |>> {
                    c =>
                      c.txaContent.value = templateMapper.getTemplateData(name)
                  }

                  dlg.setWidth("600px")
                  dlg.setHeight("800px")
              } |> Current.ui.addWindow
          }
      }
      w.miDelete.setCommandHandler {
        _ =>
          whenSelected(w.tblTemplates) {
            name =>
              new ConfirmationDialog("Delete selected template?") |>> {
                dlg =>
                  dlg.setOkButtonHandler {
                    Current.ui.privileged(permission) {
                      Ex.allCatch.either(templateMapper.getTemplateByName(name).asOption.foreach(templateMapper.deleteTemplate)) match {
                        case Right(_) =>
                          Current.page.showInfoNotification("Template has been deleted")
                        case Left(ex) =>
                          Current.page.showErrorNotification("Internal error")
                          throw ex
                      }

                      reload()
                    }
                  }
              } |> Current.ui.addWindow
          }
      }
  }

  reload()

  // END OF PRIMARY CONSTRUCTOR

  def canManage = Current.imcmsUser.isSuperAdmin

  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage templates")

  def reload() {
    view.tblTemplates.removeAllItems
    for {
      vo <- templateMapper.getAllTemplates.asScala
      name = vo.getName
      fileRE(_, ext) = vo.getFileName
    } view.tblTemplates.addItem(Array[AnyRef](name, ext, Int box templateMapper.getCountOfDocumentsUsingTemplate(vo), null), name)

    canManage |> {
      value =>
        import view._
        tblTemplates.setSelectable(value)
        Seq[ {def setEnabled(e: Boolean)}](miUpload, miDownload, miRename, miDelete, miEditContent).foreach {
          widget =>
            widget.setEnabled(value) //ui.mb,
        }
    }

    handleSelection()
  }

  private def handleSelection() {
    import view._
    (canManage && tblTemplates.isSelected) |> {
      enabled =>
        Seq(miDownload, miRename, miEditContent, miDelete).foreach(_.setEnabled(enabled))
    }

    miDocuments.setEnabled(tblTemplates.firstSelected.asOption.map {
      name =>
        templateMapper.getCountOfDocumentsUsingTemplate(templateMapper.getTemplateByName(name)) > 0
    } getOrElse false)
  }
}