package com.imcode
package imcms
package admin.doc.category

import com.imcode.imcms.security.{PermissionDenied, PermissionGranted}
import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}

import _root_.imcode.server.Imcms
import _root_.imcode.server.document.CategoryTypeDomainObject

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._

//todo:
//fix: edit - multiselect - always on
class CategoryTypeManager {
  private val categoryMapper = Imcms.getServices.getCategoryMapper

  val view = new CategoryTypeManagerView |>> { w =>
    w.miReload.setCommandHandler{ _ => reload() }
    w.tblTypes.addValueChangeHandler { _ => handleSelection() }

    w.miNew.setCommandHandler { _ => editAndSave(new CategoryTypeDomainObject) }
    w.miEdit.setCommandHandler { _ =>
      whenSelected(w.tblTypes) { id =>
        categoryMapper.getCategoryTypeById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }

    w.miDelete.setCommandHandler { _ =>
      whenSelected(w.tblTypes) { id =>
        new ConfirmationDialog("Delete selected category type?") |>> { dlg =>
          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              Ex.allCatch.either(categoryMapper.getCategoryTypeById(id.intValue).asOption.foreach(categoryMapper.deleteCategoryTypeFromDb)) match {
                case Right(_) =>
                  Current.page.showInfoNotification("Category type has been deleted")
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
  } // val widget

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = Current.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage category types")

  /** Edit in a modal dialog. */
  private def editAndSave(vo: CategoryTypeDomainObject) {
    val id = vo.getId
    val isNew = id == 0
    val dialogTitle = if (isNew) "Create new category type" else "Edit category type"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainComponent = new CategoryTypeEditorView |>> { w =>
        w.txtId.value = if (isNew) "" else id.toString
        w.txtName.value = vo.getName.trimToEmpty
        w.chkImageArchive.value = vo.isImageArchive// : JBoolean
        w.chkInherited.value = vo.isInherited //: JBoolean
        w.chkMultiSelect.value = vo.isMultiselect //: JBoolean

        dlg.setOkButtonHandler {
          vo.clone() |> { voc =>
            voc.setName(w.txtName.value.trim)
            voc.setInherited(w.chkInherited.value)
            voc.setImageArchive(w.chkImageArchive.value)
            voc.setMultiselect(w.chkMultiSelect.value)

            // todo: move validate into separate fn
            val validationError: Option[String] = voc.getName match {
              case "" => "Category type name is not set".asOption
              case name => categoryMapper.getCategoryTypeByName(name).asOption.collect {
                case categoryType if categoryType.getId != voc.getId =>
                  "Category type with such name already exists"
              }
            }

            validationError.foreach { msg =>
              Current.page.showWarningNotification(msg)
              sys.error(msg)
            }

            Current.ui.privileged(permission) {
              Ex.allCatch.either(categoryMapper saveCategoryType voc) match {
                case Left(ex) =>
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
  } // editAndSave

  def reload() {
    view.tblTypes.removeAllItems
    for {
      vo <- categoryMapper.getAllCategoryTypes
      id = vo.getId :JInteger
    } view.tblTypes.addRow(id, id, vo.getName, vo.isMultiselect : JBoolean, vo.isInherited : JBoolean, vo.isImageArchive : JBoolean, null)

    canManage |> { value =>
      view.tblTypes.setSelectable(value)
      Seq[{def setEnabled(e: Boolean)}](view.miNew, view.miEdit, view.miDelete).foreach(_.setEnabled(value)) //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && view.tblTypes.isSelected) |> { enabled =>
      Seq(view.miEdit, view.miDelete).foreach(_.setEnabled(enabled))
    }
  }
}