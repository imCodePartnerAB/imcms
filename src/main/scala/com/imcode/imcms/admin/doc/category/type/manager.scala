package com.imcode
package imcms.admin.doc.category.`type`

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcode.server.{Imcms}

import imcode.server.document.{CategoryTypeDomainObject}
import com.imcode.imcms.admin.doc.category.{CategoryTypeId}
import imcms.security.{PermissionDenied, PermissionGranted}
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

//todo:
//fix: edit - multiselect - always on
class CategoryTypeManager(app: UI) {
  private val categoryMapper = Imcms.getServices.getCategoryMapper

  val ui = new CategoryTypeManagerUI |>> { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.tblTypes addValueChangeHandler { handleSelection() }

    ui.miNew setCommandHandler { editAndSave(new CategoryTypeDomainObject) }
    ui.miEdit setCommandHandler {
      whenSelected(ui.tblTypes) { id =>
        categoryMapper.getCategoryTypeById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete setCommandHandler {
      whenSelected(ui.tblTypes) { id =>
        new ConfirmationDialog("Delete selected category type?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(categoryMapper.getCategoryTypeById(id.intValue).asOption.foreach(categoryMapper.deleteCategoryTypeFromDb)) match {
                case Right(_) =>
                  Page.getCurrent.showInfoNotification("Category type has been deleted")
                case Left(ex) =>
                  Page.getCurrent.showErrorNotification("Internal error")
                  throw ex
              }

              reload()
            }
          }
        } |> UI.getCurrent.addWindow
      }
    }
  } // val ui

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = UI.getCurrent.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage category types")

  /** Edit in a modal dialog. */
  private def editAndSave(vo: CategoryTypeDomainObject) {
    val id = vo.getId
    val isNew = id == 0
    val dialogTitle = if(isNew) "Create new category type" else "Edit category type"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainUI = new CategoryTypeEditorUI |>> { c =>
        c.txtId.value = if (isNew) "" else id.toString
        c.txtName.value = vo.getName.trimToEmpty
        c.chkImageArchive.value = vo.isImageArchive// : JBoolean
        c.chkInherited.value = vo.isInherited //: JBoolean
        c.chkMultiSelect.value = vo.isMultiselect //: JBoolean

        dlg.setOkButtonHandler {
          vo.clone() |> { voc =>
            voc setName c.txtName.value.trim
            voc setInherited c.chkInherited.booleanValue
            voc setImageArchive c.chkImageArchive.booleanValue
            voc setMultiselect c.chkMultiSelect.booleanValue

            // todo: move validate into separate fn
            val validationError: Option[String] = voc.getName match {
              case "" => "Category type name is not set".asOption
              case name => categoryMapper.getCategoryTypeByName(name).asOption.collect {
                case categoryType if categoryType.getId != voc.getId =>
                  "Category type with such name already exists"
              }
            }

            validationError.foreach { msg =>
              Page.getCurrent.showWarningNotification(msg)
              sys.error(msg)
            }

            app.privileged(permission) {
              Ex.allCatch.either(categoryMapper saveCategoryType voc) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  Page.getCurrent.showErrorNotification("Internal error, please contact your administrator")
                  throw ex
                case _ =>
                  (isNew ? "New category type has been created" | "Category type has been updated") |> { msg =>
                    Page.getCurrent.showInfoNotification(msg)
                  }

                  reload()
              }
            }
          }
        }
      }
    } |> UI.getCurrent.addWindow
  } // editAndSave

  def reload() {
    ui.tblTypes.removeAllItems
    for {
      vo <- categoryMapper.getAllCategoryTypes
      id = vo.getId :JInteger
    } ui.tblTypes.addItem(Array[AnyRef](id, vo.getName, vo.isMultiselect : JBoolean, vo.isInherited : JBoolean, vo.isImageArchive : JBoolean), id)

    canManage |> { value =>
      ui.tblTypes.setSelectable(value)
      doto[{def setEnabled(e: Boolean)}](ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled value } //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && ui.tblTypes.isSelected) |> { enabled =>
      doto(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
}

class CategoryTypeManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblTypes = new Table with SingleSelect[CategoryTypeId] with Immediate
  val rc = new ReloadableContentUI(tblTypes)

  addContainerProperties(tblTypes,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[JBoolean]("Multi select?"),
    PropertyDescriptor[JBoolean]("Inherited to new documents?"),
    PropertyDescriptor[JBoolean]("Used by image archive?"))

  this.addComponents(mb, rc)
}


class CategoryTypeEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name") with Required
  val chkMultiSelect = new CheckBox("Multiselect")
  val chkInherited = new CheckBox("Inherited to new documents")
  val chkImageArchive = new CheckBox("Used by image archive")

  this.addComponents(txtId, txtName, chkMultiSelect, chkInherited, chkImageArchive)
}