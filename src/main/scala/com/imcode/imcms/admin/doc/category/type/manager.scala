package com.imcode
package imcms.admin.doc.category.`type`

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcode.server.{Imcms}

import imcode.server.document.{CategoryTypeDomainObject}
import com.imcode.imcms.admin.doc.category.{CategoryTypeId}
import imcms.security.{PermissionDenied, PermissionGranted}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

//todo:
//fix: edit - multiselect - always on
class CategoryTypeManager(app: UI) {
  private val categoryMapper = Imcms.getServices.getCategoryMapper

  val widget = new CategoryTypeManagerWidget |>> { w =>
    w.rc.btnReload.addClickHandler { _ => reload() }
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
            app.privileged(permission) {
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

  def canManage = Current.ui.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage category types")

  /** Edit in a modal dialog. */
  private def editAndSave(vo: CategoryTypeDomainObject) {
    val id = vo.getId
    val isNew = id == 0
    val dialogTitle = if(isNew) "Create new category type" else "Edit category type"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainWidget = new CategoryTypeEditorWidget |>> { w =>
        w.txtId.value = if (isNew) "" else id.toString
        w.txtName.value = vo.getName.trimToEmpty
        w.chkImageArchive.value = vo.isImageArchive// : JBoolean
        w.chkInherited.value = vo.isInherited //: JBoolean
        w.chkMultiSelect.value = vo.isMultiselect //: JBoolean

        dlg.setOkButtonHandler {
          vo.clone() |> { voc =>
            voc setName w.txtName.value.trim
            voc setInherited w.chkInherited.value
            voc setImageArchive w.chkImageArchive.value
            voc setMultiselect w.chkMultiSelect.value

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

            app.privileged(permission) {
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
    widget.tblTypes.removeAllItems
    for {
      vo <- categoryMapper.getAllCategoryTypes
      id = vo.getId :JInteger
    } widget.tblTypes.addItem(Array[AnyRef](id, vo.getName, vo.isMultiselect : JBoolean, vo.isInherited : JBoolean, vo.isImageArchive : JBoolean), id)

    canManage |> { value =>
      widget.tblTypes.setSelectable(value)
      Seq[{def setEnabled(e: Boolean)}](widget.miNew, widget.miEdit, widget.miDelete).foreach(_.setEnabled(value)) //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && widget.tblTypes.isSelected) |> { enabled =>
      Seq(widget.miEdit, widget.miDelete).foreach(_.setEnabled(enabled))
    }
  }
}

class CategoryTypeManagerWidget extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblTypes = new Table with SingleSelect[CategoryTypeId] with Immediate
  val rc = new ReloadableContentWidget(tblTypes)

  addContainerProperties(tblTypes,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[JBoolean]("Multi select?"),
    PropertyDescriptor[JBoolean]("Inherited to new documents?"),
    PropertyDescriptor[JBoolean]("Used by image archive?"))

  this.addComponents(mb, rc)
}


class CategoryTypeEditorWidget extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name") with Required
  val chkMultiSelect = new CheckBox("Multiselect")
  val chkInherited = new CheckBox("Inherited to new documents")
  val chkImageArchive = new CheckBox("Used by image archive")

  this.addComponents(txtId, txtName, chkMultiSelect, chkInherited, chkImageArchive)
}