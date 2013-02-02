package com.imcode
package imcms.admin.doc.template
package group

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcode.server.{Imcms}
import imcode.server.document.{TemplateGroupDomainObject}
import imcms.security.{PermissionDenied, PermissionGranted}
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

//todo: form check
//todo: duplicate save check!
//todo: internal error check
class TemplateGroupManager(app: UI) {
  private val templateMapper = Imcms.getServices.getTemplateMapper

  val ui = new TemplateGroupManagerUI |>> { ui =>
    ui.rc.btnReload.addClickHandler { reload() }
    ui.tblGroups.addValueChangeHandler { handleSelection() }

    ui.miNew.setCommandHandler { editAndSave(new TemplateGroupDomainObject(0, null)) }
    ui.miEdit.setCommandHandler {
      whenSelected(ui.tblGroups) { id =>
        templateMapper.getTemplateGroupById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete.setCommandHandler {
      whenSelected(ui.tblGroups) { id =>
        new ConfirmationDialog("Delete selected template group?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(templateMapper deleteTemplateGroup id.intValue) match {
                case Right(_) =>
                  Page.getCurrent.showInfoNotification("Template group has been deleted")
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
  } // ui

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = UI.getCurrent.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage template groups")

  /** Edit in a modal dialog. */
  private def editAndSave(vo: TemplateGroupDomainObject) {
    val id = vo.getId
    val isNew = id == 0
    val dialogTitle = isNew ? "Create new template group" | "Edit template group"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainUI = new TemplateGroupEditorUI |>> { c =>
        c.txtId.value = isNew ? "" | id.toString
        c.txtName.value = vo.getName.trimToEmpty
        templateMapper.getTemplatesInGroup(vo).asScala.foreach(template => c.twsTemplates.addChosenItem(template.getName))
        templateMapper.getTemplatesNotInGroup(vo).asScala.foreach(template => c.twsTemplates.addAvailableItem(template.getName))

        dlg.setOkButtonHandler {
          app.privileged(permission) {
            val voc = if (isNew) {
              templateMapper.createTemplateGroup(c.txtName.value)
              templateMapper.getTemplateGroupByName(c.txtName.value)
            } else vo.clone() |>> { voc =>
              templateMapper.renameTemplateGroup(voc, c.txtName.value)
            }

            templateMapper.getTemplatesInGroup(voc).asScala.foreach { template =>
              templateMapper.removeTemplateFromGroup(template, voc)
            }

            for {
              name <- c.twsTemplates.chosenItemIds
              template <- templateMapper.getTemplateByName(name).asOption
            } templateMapper.addTemplateToGroup(template, voc)

            reload()
          }
        }
      }
    } |> UI.getCurrent.addWindow
  }

  def reload() {
    ui.tblGroups.removeAllItems
    for {
      vo <- templateMapper.getAllTemplateGroups
      id = vo.getId : JInteger
    } ui.tblGroups.addItem(Array[AnyRef](id, vo.getName, templateMapper.getTemplatesInGroup(vo).size : JInteger), id)

    canManage |> { value =>
      ui.tblGroups.setSelectable(value)
      doto[{def setEnabled(e: Boolean)}](ui.miNew, ui.miEdit, ui.miDelete) { _.setEnabled(value) }   //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && ui.tblGroups.isSelected) |> { enabled =>
      doto(ui.miEdit, ui.miDelete) { _.setEnabled(enabled) }
    }
  }
}


class TemplateGroupManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblGroups = new Table with SingleSelect[TemplateGroupId] with Selectable with Immediate
  val rc = new ReloadableContentUI(tblGroups)

  addContainerProperties(tblGroups,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[JInteger]("Templates count"))

  this.addComponents(mb, rc)
}

class TemplateGroupEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name") with Required
  val twsTemplates = new TwinSelect[String]("Templates")

  this.addComponents(txtId, txtName, twsTemplates)
}