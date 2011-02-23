package com.imcode
package imcms.admin.document.category.`type`

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcode.server.document.{CategoryTypeDomainObject}
import com.imcode.imcms.admin.document.category.{CategoryTypeId}
import com.vaadin.ui.Window.Notification
import imcms.security.{PermissionDenied, PermissionGranted}

//todo:
//fix: edit - multiselect - always on
class CategoryTypeManager(app: ImcmsApplication) {
  private val categoryMapper = Imcms.getServices.getCategoryMapper

  val ui = letret(new CategoryTypeManagerUI) { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.tblTypes addValueChangeHandler { handleSelection() }

    ui.miNew setCommand block { editAndSave(new CategoryTypeDomainObject) }
    ui.miEdit setCommand block {
      whenSelected(ui.tblTypes) { id =>
        categoryMapper.getCategoryTypeById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete setCommand block {
      whenSelected(ui.tblTypes) { id =>
        app.initAndShow(new ConfirmationDialog("Delete selected category type?")) { dlg =>
          dlg wrapOkHandler {
            app.privileged(permission) {
              EX.allCatch.either(?(categoryMapper getCategoryTypeById id.intValue) foreach categoryMapper.deleteCategoryTypeFromDb) match {
                case Right(_) =>
                  app.showInfoNotification("Category type has been deleted")
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
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage category types")

  /** Edit in a modal dialog. */
  private def editAndSave(vo: CategoryTypeDomainObject) {
    val id = vo.getId
    val isNew = id == 0
    val dialogTitle = if(isNew) "Create new category type" else "Edit category type"

    app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
      dlg.mainUI = letret(new CategoryTypeEditorUI) { c =>
        c.txtId.value = if (isNew) "" else id.toString
        c.txtName.value = ?(vo.getName) getOrElse ""
        c.chkImageArchive.value = Boolean box vo.isImageArchive
        c.chkInherited.value = Boolean box vo.isInherited
        c.chkMultiSelect.value = Boolean box vo.isMultiselect

        dlg wrapOkHandler {
          let(vo.clone()) { voc =>
            voc setName c.txtName.value.trim
            voc setInherited c.chkInherited.booleanValue
            voc setImageArchive c.chkImageArchive.booleanValue
            voc setMultiselect c.chkMultiSelect.booleanValue

            // todo: move validate into separate fn
            val validationError: Option[String] = voc.getName match {
              case "" => ?("Category type name is not set")
              case name => ?(categoryMapper.getCategoryTypeByName(name)) collect {
                case categoryType if categoryType.getId != voc.getId =>
                  "Category type with such name already exists"
              }
            }

            validationError foreach { msg =>
              app.getMainWindow.showNotification(msg, Notification.TYPE_WARNING_MESSAGE)
              error(msg)
            }

            app.privileged(permission) {
              EX.allCatch.either(categoryMapper saveCategoryType voc) match {
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
    }
  } // editAndSave

  def reload() {
    ui.tblTypes.removeAllItems
    for {
      vo <- categoryMapper.getAllCategoryTypes
      id = Int box vo.getId
    } ui.tblTypes.addItem(Array[AnyRef](id, vo.getName, Boolean box vo.isMultiselect, Boolean box vo.isInherited, Boolean box vo.isImageArchive), id)

    let(canManage) { canManage =>
      ui.tblTypes.setSelectable(canManage)
      forlet[{def setEnabled(e: Boolean)}](ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled canManage } //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    let(canManage && ui.tblTypes.isSelected) { enabled =>
      forlet(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
}

class CategoryTypeManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblTypes = new Table with SingleSelect2[CategoryTypeId] with Immediate
  val rc = new ReloadableContentUI(tblTypes)

  addContainerProperties(tblTypes,
    CP[JInteger]("Id"),
    CP[String]("Name"),
    CP[JBoolean]("Multi select?"),
    CP[JBoolean]("Inherited to new documents?"),
    CP[JBoolean]("Used by image archive?"))

  addComponents(this, mb, rc)
}


class CategoryTypeEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name") with Required
  val chkMultiSelect = new CheckBox("Multiselect")
  val chkInherited = new CheckBox("Inherited to new documents")
  val chkImageArchive = new CheckBox("Used by image archive")

  addComponents(this, txtId, txtName, chkMultiSelect, chkInherited, chkImageArchive)
}