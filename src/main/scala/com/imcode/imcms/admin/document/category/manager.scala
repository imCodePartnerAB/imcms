package com.imcode
package imcms.admin.document.category

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcode.server.document.{CategoryDomainObject}
import com.vaadin.ui.Window.Notification
import imcms.admin.system.file._
import com.vaadin.terminal.FileResource
import java.io.File
import imcms.security.{PermissionGranted, PermissionDenied}

/**
 * Category manager.
 *
 * A category is identified by its name and type.
 */
//todo: edit - image can not be null
//todo: delete in use message
class CategoryManager(app: ImcmsApplication) {
  private val categoryMapper = Imcms.getServices.getCategoryMapper

  val ui: CategoryManagerUI = letret(new CategoryManagerUI) { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.tblCategories addValueChangeHandler { handleSelection() }

    ui.miNew setCommand block { editAndSave(new CategoryDomainObject) }
    ui.miEdit setCommand block {
      whenSelected(ui.tblCategories) { id =>
        categoryMapper.getCategoryById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }

    ui.miDelete setCommand block {
      whenSelected(ui.tblCategories) { id =>
        app.initAndShow(new ConfirmationDialog("Delete selected category?")) { dlg =>
          dlg setOkHandler {
            app.privileged(permission) {
              EX.allCatch.either(?(categoryMapper getCategoryById id.intValue) foreach categoryMapper.deleteCategoryFromDb) match {
                case Right(_) =>
                  app.showInfoNotification("Category has been deleted")
                case Left(ex) =>
                  app.showErrorNotification("Internal error")
                  throw ex
              }

              reload()
            }
          }
        }
      }
    }
  } // val ui

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage categories")

  /** Edit in modal dialog. */
  private def editAndSave(vo: CategoryDomainObject) {
    val typesNames = categoryMapper.getAllCategoryTypes map (_.getName)

    if (typesNames.isEmpty) {
      app.getMainWindow.showNotification("Please create at least one category type.", Notification.TYPE_WARNING_MESSAGE)
    } else {
      val id = vo.getId
      val isNew = id == 0
      val dialogTitle = if(isNew) "Create new category" else "Edit category"
      val browser = letret(new FileBrowser) { browser =>
        browser.addLocation("Images", LocationConf(new File(Imcms.getPath, "images"), LocationItemsFilter.imageFile))
      }
      val imagePicker = new ImagePicker(app, browser)
      val imageFile = for {
        url <- ?(vo.getImageUrl)
        file = new File(Imcms.getPath, "WEB-INF/" + url) if file.isFile
      } imagePicker.preview.set(new Embedded("", new FileResource(file, app)))

      app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
        dlg.mainContent = letret(new CategoryEditorUI(imagePicker.ui)) { c =>
          typesNames foreach { c.sltType addItem _ }

          c.txtId.value = if (isNew) "" else id.toString
          c.txtName.value = ?(vo.getName) getOrElse ""
          c.txtDescription.value = ?(vo.getDescription) getOrElse ""
          c.sltType.value = if (isNew) typesNames.head else vo.getType.getName

          dlg setOkHandler {
            let(vo.clone()) { voc =>
              voc setName c.txtName.value.trim
              voc setDescription c.txtDescription.value.trim
              voc setImageUrl (if (imagePicker.preview.isEmpty) null else "../images/" + imagePicker.preview.get.get.getSource.asInstanceOf[FileResource].getFilename)
              voc setType categoryMapper.getCategoryTypeByName(c.sltType.value)
              // todo: move validate into separate fn
              val validationError: Option[String] = voc.getName match {
                case "" => ?("Category name is not set")
                case name => ?(categoryMapper.getCategoryByTypeAndName(voc.getType, name)) collect {
                  case category if category.getId != voc.getId =>
                    "Category with such name and type already exists"
                }
              }

              validationError foreach { msg =>
                app.getMainWindow.showNotification(msg, Notification.TYPE_WARNING_MESSAGE)
                error(msg)
              }

              app.privileged(permission) {
                EX.allCatch.either(categoryMapper saveCategory voc) match {
                  case Left(ex) =>
                    // todo: log ex, provide custom dialog with details -> show stack
                    app.getMainWindow.showNotification("Internal error, please contact your administrator", Notification.TYPE_ERROR_MESSAGE)
                    throw ex
                  case _ =>
                    let(if (isNew) "New category type has been created" else "Category type has been updated") { msg =>
                      app.getMainWindow.showNotification(msg, Notification.TYPE_HUMANIZED_MESSAGE)
                    }

                    reload()
                }
              }
            }
          }
        }
      } // initAndShow
    }
  } // editAndSave

  def reload() {
    ui.tblCategories.removeAllItems
    for {
      vo <- categoryMapper.getAllCategories
      id = Int box vo.getId
    } ui.tblCategories.addItem(Array[AnyRef](id, vo.getName, vo.getDescription, vo.getImageUrl, vo.getType.getName), id)

    let(canManage) { canManage =>
      ui.tblCategories.setSelectable(canManage)
      forlet[{def setEnabled(e: Boolean)}](ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled canManage } //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    let(canManage && ui.tblCategories.isSelected) { enabled =>
      forlet(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
} // class CategoryManager


class CategoryManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblCategories = new Table with SingleSelect2[CategoryId] with Immediate
  val rc = new ReloadableContentUI(tblCategories)

  addContainerProperties(tblCategories,
    CP[JInteger]("Id"),
    CP[String]("Name"),
    CP[String]("Description"),
    CP[String]("Icon"),
    CP[String]("Type"))

  addComponents(this, mb, rc)
}


class CategoryEditorUI(val imagePickerUI: ImagePickerUI) extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled {
    setColumns(11)
  }
  val txtName = new TextField("Name") with Required
  val txtDescription = new TextField("Description") {
    setRows(5)
    setColumns(11)
  }

  val sltType = new Select("Type") with ValueType[String] with Required with NoNullSelection

  addComponents(this, txtId, txtName, sltType, imagePickerUI, txtDescription)
  imagePickerUI.setCaption("Icon")
}