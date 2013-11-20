package com.imcode
package imcms.admin.doc.template

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import imcms.admin.instance.file._
import org.apache.commons.io.FileUtils
import imcms.security.{PermissionDenied, PermissionGranted}
import java.io.{FileInputStream, ByteArrayInputStream, File}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

//todo: common internal ex handler???
//todo: add related docs handling
//-upload fialog and save as handling
class TemplateManager(app: UI) {
  private val templateMapper = Imcms.getServices.getTemplateMapper
  private val fileRE = """(?i)(.+?)(?:\.(\w+))?""".r // filename, (optional extension)

  val widget = new TemplateManagerWidget |>> { w =>
    w.tblTemplates.addValueChangeHandler { _ => handleSelection() }
    w.miUpload.setCommandHandler { _ =>
      new FileUploaderDialog("Upload template file") |>> { dlg =>
        // strips filename extension, trims and replaces spaces with underscores
        dlg.uploader.fileNameToSaveAsName = fileRE.unapplySeq(_:String).map(_.head.trim.replaceAll("""\s""", "_")).get
        dlg.setOkButtonHandler {
          for {
            uploadedFile <- dlg.uploader.uploadedFile
            name = dlg.uploader.widget.txtSaveAsName.value // todo: check not empty
            in = new FileInputStream(uploadedFile.file)
          } {
            app.privileged(permission) {
              templateMapper.saveTemplate(name, uploadedFile.name, in, dlg.uploader.widget.chkOverwrite.value) match {
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
    w.miRename.setCommandHandler { _ =>
      whenSelected(w.tblTemplates) { name =>
        new OkCancelDialog("Rename template") |>> { dlg =>
          val fileRenameWidget = new TemplateRenameWidget |>> { c =>
            c.txtName.value = name
          }

          dlg.mainWidget = fileRenameWidget
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              templateMapper.renameTemplate(name, fileRenameWidget.txtName.value)
            }

            reload()
          }
        } |> Current.ui.addWindow
      }
    }
    w.miEditContent.setCommandHandler { _ =>
      whenSelected(w.tblTemplates) { name =>
        new Dialog("Template file content") with CustomSizeDialog with NoContentMarginDialog |>> { dlg =>
          dlg.mainWidget = new TemplateContentEditorWidget |>> { c =>
            c.txaContent.value = templateMapper.getTemplateData(name)
          }

          dlg.setWidth("600px")
          dlg.setHeight("800px")
        } |> Current.ui.addWindow
      }
    }
    w.miDelete.setCommandHandler { _ =>
      whenSelected(w.tblTemplates) { name =>
        new ConfirmationDialog("Delete selected template?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
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

  def canManage = Current.ui.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage templates")

  def reload() {
    widget.tblTemplates.removeAllItems
    for {
      vo <- templateMapper.getAllTemplates.asScala
      name = vo.getName
      fileRE(_, ext) = vo.getFileName
    } widget.tblTemplates.addItem(Array[AnyRef](name, ext, Int box templateMapper.getCountOfDocumentsUsingTemplate(vo)), name)

    canManage |> { value =>
      import widget._
      tblTemplates.setSelectable(value)
      Seq[{def setEnabled(e: Boolean)}](miUpload, miDownload, miRename, miDelete, miEditContent).foreach { widget =>
        widget.setEnabled(value)   //ui.mb,
      }
    }

    handleSelection()
  }

  private def handleSelection() {
    import widget._
    (canManage && tblTemplates.isSelected) |> { enabled =>
      Seq(miDownload, miRename, miEditContent, miDelete).foreach(_.setEnabled(enabled))
    }

    miDocuments.setEnabled(tblTemplates.value.asOption.map { name =>
      templateMapper.getCountOfDocumentsUsingTemplate(templateMapper.getTemplateByName(name)) > 0
    } getOrElse false)
  }
}

class TemplateManagerWidget extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miUpload = mb.addItem("Upload", New16, null)
  val miDownload = mb.addItem("Download", New16, null)
  val miRename = mb.addItem("Rename", Edit16, null)
  val miDelete = mb.addItem("Delete", Delete16, null)
  val miEditContent = mb.addItem("Edit content", EditContent16, null)
  val miDocuments = mb.addItem("Related documents", Documents16, null)
  val miHelp = mb.addItem("Help", Help16, null)
  val tblTemplates = new Table with SingleSelect[TemplateName] with Selectable with Immediate
  val rc = new ReloadableContentWidget(tblTemplates)

  addContainerProperties(tblTemplates,
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Type"),
    PropertyDescriptor[JInteger]("Document count using this template"))

  this.addComponents(mb, rc)
}


class TemplateRenameWidget extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}

class TemplateContentEditorWidget extends VerticalLayout with FullSize {
  val txaContent = new TextArea with FullSize |>> {
    _.setRows(20)
  }

  addComponent(txaContent)
}