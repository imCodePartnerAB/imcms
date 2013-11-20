package com.imcode
package imcms.admin.doc.category

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcode.server.{Imcms}

import imcode.server.document.{CategoryDomainObject}
import imcms.admin.instance.file._
import java.io.File
import imcms.security.{PermissionGranted, PermissionDenied}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.{Page, FileResource}
import scala.util.{Failure, Try}

/**
 * Category manager.
 *
 * A category is identified by its name and type.
 */
//todo: edit - image can not be null
//todo: delete in use message
class CategoryManager(app: UI) {
  private val categoryMapper = Imcms.getServices.getCategoryMapper

  val widget: CategoryManagerWidget = new CategoryManagerWidget |>> { w =>
    w.rc.btnReload.addClickHandler { _ => reload() }
    w.tblCategories.addValueChangeHandler { _ =>  handleSelection() }

    w.miNew.setCommandHandler { _ => editAndSave(new CategoryDomainObject) }
    w.miEdit.setCommandHandler { _ =>
      whenSelected(w.tblCategories) { id =>
        categoryMapper.getCategoryById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }

    w.miDelete.setCommandHandler { _ =>
      whenSelected(w.tblCategories) { id =>
        new ConfirmationDialog("Delete selected category?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(categoryMapper.getCategoryById(id.intValue).asOption.foreach(categoryMapper.deleteCategoryFromDb)) match {
                case Right(_) =>
                  Current.page.showInfoNotification("Category has been deleted")
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
  } // val ui

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = Current.ui.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage categories")

  /** Edit in modal dialog. */
  private def editAndSave(vo: CategoryDomainObject) {
    val typesNames = categoryMapper.getAllCategoryTypes.map(_.getName)

    if (typesNames.isEmpty) {
      Current.page.showWarningNotification("Please create at least one category type.")
    } else {
      val id = vo.getId
      val isNew = id == 0
      val dialogTitle = if(isNew) "Create new category" else "Edit category"
      val browser = ImcmsFileBrowser.addImagesLocation(new FileBrowser)
      val imagePicker = new ImagePicker(browser)
      val imageFile = for {
        url <- vo.getImageUrl.asOption
        file = new File(Imcms.getPath, "WEB-INF/" + url) if file.isFile
      } imagePicker.preview.set(new Embedded("", new FileResource(file)))

      new OkCancelDialog(dialogTitle) |>> { dlg =>
        dlg.mainWidget = new CategoryEditorWidget(imagePicker.widget) |>> { c =>
          typesNames.foreach { c.sltType addItem _ }

          c.txtId.value = if (isNew) "" else id.toString
          c.txtName.value = vo.getName.trimToEmpty
          c.txaDescription.value = vo.getDescription.trimToEmpty
          c.sltType.value = if (isNew) typesNames.head else vo.getType.getName

          dlg.setOkButtonHandler {
            vo.clone |> { voc =>
              voc setName c.txtName.value.trim
              voc setDescription c.txaDescription.value.trim
              voc setImageUrl (if (imagePicker.preview.isEmpty) null else "../images/" + imagePicker.preview.get.get.getSource.asInstanceOf[FileResource].getFilename)
              voc setType categoryMapper.getCategoryTypeByName(c.sltType.value)
              // todo: move validate into separate fn
              val validationError: Option[String] = voc.getName match {
                case "" => Some("Category name is not set")
                case name => categoryMapper.getCategoryByTypeAndName(voc.getType, name).asOption.collect {
                  case category if category.getId != voc.getId =>
                    "Category with such name and type already exists"
                }
              }

              validationError.foreach { msg =>
                Current.page.showWarningNotification(msg)
                sys.error(msg)
              }

              app.privileged(permission) {
                Try(categoryMapper.saveCategory(voc)) match {
                  case Failure(ex) =>
                    // todo: log ex, provide custom dialog with details -> show stack
                    Current.page.showErrorNotification("Internal error, please contact your administrator")
                    throw ex
                  case _ =>
                    (if (isNew) "New category type has been created" else "Category type has been updated") |> { msg =>
                      Current.page.showInfoNotification(msg)
                    }

                    reload()
                }
              }
            }
          }
        }
      } |> Current.ui.addWindow
    }
  } // editAndSave

  def reload() {
    widget.tblCategories.removeAllItems
    for {
      vo <- categoryMapper.getAllCategories.asScala
      id = Int box vo.getId
    } widget.tblCategories.addItem(Array[AnyRef](id, vo.getName, vo.getDescription, vo.getImageUrl, vo.getType.getName), id)

    canManage |> { value =>
      widget.tblCategories.setSelectable(value)
      Seq[{def setEnabled(e: Boolean)}](widget.miNew, widget.miEdit, widget.miDelete).foreach(_.setEnabled(value)) //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && widget.tblCategories.isSelected) |> { enabled =>
      Seq(widget.miEdit, widget.miDelete).foreach(_.setEnabled(enabled))
    }
  }
} // class CategoryManager


class CategoryManagerWidget extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblCategories = new Table with SingleSelect[CategoryId] with Immediate
  val rc = new ReloadableContentWidget(tblCategories)

  addContainerProperties(tblCategories,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Description"),
    PropertyDescriptor[String]("Icon"),
    PropertyDescriptor[String]("Type"))

  this.addComponents(mb, rc)
}


class CategoryEditorWidget(val imagePickerWidget: ImagePickerWidget) extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled {
    setColumns(11)
  }
  val txtName = new TextField("Name") with Required
  val txaDescription = new TextArea("Description") |>> { t =>
    t.setRows(5)
    t.setColumns(11)
  }

  val sltType = new ComboBox("Type") with SingleSelect[String] with Required with NoNullSelection

  this.addComponents(txtId, txtName, sltType, imagePickerWidget, txaDescription)
  imagePickerWidget.setCaption("Icon")
}