package com.imcode
package imcms.admin.document.category

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcode.server.document.{CategoryDomainObject}
import java.io.File
import com.vaadin.ui.Window.Notification
import imcms.admin.filesystem._

// Only Superadmin can manage categories
// todo: separate object with methods such as canManageXXX

/**
 * Category is identified by its name and type.
 */
class CategoryManager(app: VaadinApplication) {
  private val categoryMapper = Imcms.getServices.getCategoryMapper

  val ui: CategoryManagerUI = letret(new CategoryManagerUI) { ui =>
    ui.tblCategories.itemsProvider = () => {
      for {
        vo <- categoryMapper.getAllCategories
        id = Int box vo.getId
      } yield {
        id -> Seq(id, vo.getName, vo.getDescription, vo.getImageUrl, vo.getType.getName)
      }
    }

    ui.rc.btnReload addListener block { reload() }
    ui.tblCategories addListener block { handleSelection() }

    ui.miNew setCommand block { editInPopUp(new CategoryDomainObject) }
    ui.miEdit setCommand block {
      whenSelected(ui.tblCategories) { id =>
        categoryMapper.getCategoryById(id.intValue) match {
          case null => reload()
          case vo => editInPopUp(vo)
        }
      }
    }

    ui.miDelete setCommand block {
      whenSelected(ui.tblCategories) { id =>
        app.initAndShow(new ConfirmationDialog("Delete category")) { dlg =>
          dlg addOkButtonClickListener {
            ?(categoryMapper getCategoryById id.intValue) foreach { vo =>
              if (canManage) categoryMapper deleteCategoryFromDb vo
              else error("NO PERMISSIONS")
            }
            reload()
          }
        }
      }
    }
  } // val ui

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin

  /** Edit in modal dialog. */
  private def editInPopUp(vo: CategoryDomainObject) {
    val typesNames = categoryMapper.getAllCategoryTypes map (_.getName)

    if (typesNames.isEmpty) {
      app.getMainWindow.showNotification("Please create at least one category type.", Notification.TYPE_WARNING_MESSAGE)
    } else {
      val id = vo.getId
      val isNew = id == 0
      val dialogTitle = if(isNew) "Create new category" else "Edit category"
      val imagePicker = new ImagePicker(app)
      imagePicker.browser.addLocation("Images", new File(Imcms.getPath, "images"))

      app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
        let(dlg.setMainContent(new CategoryDialogContentUI(imagePicker.ui))) { c =>
          typesNames foreach { c.sltType addItem _ }

          c.txtId.value = if (isNew) "" else id.toString
          c.txtName.value = ?(vo.getName) getOrElse ""
          c.txtDescription.value = ?(vo.getDescription) getOrElse ""
          c.sltType.value = if (isNew) typesNames.head else vo.getType.getName

          dlg addOkButtonClickListener {
            let(vo.clone()) { voc =>
              voc setName c.txtName.value.trim
              voc setDescription c.txtDescription.value.trim
              voc setImageUrl ""//embIcon
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

              if (!canManage) {
                app.getMainWindow.showNotification("You are not allowed to manage categories", Notification.TYPE_ERROR_MESSAGE)
              } else {
                EX.allCatch.either(categoryMapper saveCategory voc) match {
                  case Left(ex) =>
                    // todo: log ex, provide custom dialog with details -> show stack
                    app.getMainWindow.showNotification("Internal error, please contact your administrator", Notification.TYPE_ERROR_MESSAGE)
                    throw ex
                  case _ =>
                    let(if (isNew) "New category has been added" else "Category has been updated") { msg =>
                      app.getMainWindow.showNotification(msg, Notification.TYPE_HUMANIZED_MESSAGE)
                    }

//                    let (Int box voc.getId) { id =>
//                      ui.tblCategories.addItem(Array[AnyRef](id, voc.getDescription, voc.getImageUrl, voc.getType.getName), id)
//                      ui.tblCategories.requestRepaint()
//                    }
                    reload()
                }
              }
            }
          }
        }
      }
    }
  } // editInPopUp

  private def reload() {
    ui.tblCategories.reload()

    let(canManage) { canManage =>
      ui.tblCategories.setSelectable(canManage)
      forlet[{def setEnabled(e: Boolean)}](ui.mb, ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled canManage }
    }

    handleSelection()
  }

  private def handleSelection() {
    let(canManage && ui.tblCategories.isSelected) { isSelected =>
      ui.miEdit.setEnabled(isSelected)
      ui.miDelete.setEnabled(isSelected)
    }
  }
} // class CategoryManager

class CategoryManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  val mb = new MenuBar
  val miNew = mb.addItem("New", null)
  val miEdit = mb.addItem("Edit", null)
  val miDelete = mb.addItem("Delete", null)
  val tblCategories = new Table with Reloadable with SingleSelect2[CategoryId] with Selectable with Immediate
  val rc = new ReloadableContentUI(tblCategories)

  addContainerProperties(tblCategories,
    CP[JInteger]("Id"),
    CP[String]("Name"),
    CP[String]("Description"),
    CP[String]("Icon"),
    CP[String]("Type"))

  addComponents(this, mb, rc)
}


class CategoryDialogContentUI(imagePcikerUI: ImagePickerUI) extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled {
    setColumns(11)
  }
  val txtName = new TextField("Name") with Required
  val txtDescription = new TextField("Description") {
    setRows(5)
    setColumns(11)
  }

  val sltType = new Select("Type") with ValueType[String] with Required with NoNullSelection

  val embIcon = imagePcikerUI
//  val embIcon = new IconImagePickerUI(50, 50) {
//    setCaption("Icon")
//
//    btnChoose addListener block {
//      app.initAndShow(new OkCancelDialog("Select icon image - .gif  .png  .jpg  .jpeg")
//            with FileSelectDialog, resizable = true) { dlg =>
//        dlg.browser.ui setSplitPosition 30
//        dlg.browser.addLocation("Images", new File(Imcms.getPath, "images"))
//
//        dlg.addOkButtonClickListener {
//          app.getMainWindow.showNotification("selected:" + dlg.browser.dirContentSelection.get.toString)
//        }
//
//        dlg setWidth "650px"
//        dlg setHeight "350px"
//      }
//
////      app.initAndShow(new OkCancelDialog("Select icon image - .gif  .png  .jpg  .jpeg")
////              with CustomSizeDialog with BottomMarginDialog, resizable = true) { w =>
////
////        let(w.mainContent = new FileBrowserWithImagePreview(100, 100)) { b =>
////          b.browser.ui setSplitPosition 30
////          b.browser.addLocation("Images", new File(Imcms.getPath, "images"))
////          //b.browser.tblDirContent setSelectable true
////
////          w.addOkButtonClickListener {
////            b.preview.image match {
////              case Some(source) => showImage(source)
////              case _ => showStub
////            }
////          }
////        }
////
////        w setWidth "650px"
////        w setHeight "350px"
////      }
//    }
//  }

  addComponents(this, txtId, txtName, sltType, embIcon, txtDescription)
}