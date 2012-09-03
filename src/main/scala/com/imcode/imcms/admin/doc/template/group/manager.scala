package com.imcode
package imcms.admin.doc.template
package group

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcode.server.document.{TemplateGroupDomainObject}
import com.vaadin.ui.Window.Notification
import imcms.security.{PermissionDenied, PermissionGranted}

//todo: form check
//todo: duplicate save check!
//todo: internal error check
class TemplateGroupManager(app: ImcmsApplication) {
  private val templateMapper = Imcms.getServices.getTemplateMapper

  val ui = new TemplateGroupManagerUI |>> { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.tblGroups addValueChangeHandler { handleSelection() }

    ui.miNew setCommandHandler { editAndSave(new TemplateGroupDomainObject(0, null)) }
    ui.miEdit setCommandHandler {
      whenSelected(ui.tblGroups) { id =>
        templateMapper.getTemplateGroupById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete setCommandHandler {
      whenSelected(ui.tblGroups) { id =>
        app.initAndShow(new ConfirmationDialog("Delete selected template group?")) { dlg =>
          dlg wrapOkHandler {
            app.privileged(permission) {
              Ex.allCatch.either(templateMapper deleteTemplateGroup id.intValue) match {
                case Right(_) =>
                  app.showInfoNotification("Template group has been deleted")
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
  } // ui

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage template groups")

  /** Edit in a modal dialog. */
  private def editAndSave(vo: TemplateGroupDomainObject) {
    val id = vo.getId
    val isNew = id == 0
    val dialogTitle = if(isNew) "Create new template group" else "Edit template group"

    app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
      dlg.mainUI = new TemplateGroupEditorUI |>> { c =>
        c.txtId.value = if (isNew) "" else id.toString
        c.txtName.value = vo.getName |> opt getOrElse ""
        templateMapper.getTemplatesInGroup(vo) foreach (c.twsTemplates addChosenItem _.getName)
        templateMapper.getTemplatesNotInGroup(vo) foreach (c.twsTemplates addAvailableItem _.getName)

        dlg.wrapOkHandler {
          app.privileged(permission) {
            val voc = if (isNew) {
              templateMapper.createTemplateGroup(c.txtName.value)
              templateMapper.getTemplateGroupByName(c.txtName.value)
            } else vo.clone() |>> { voc =>
              templateMapper.renameTemplateGroup(voc, c.txtName.value)
            }

            templateMapper.getTemplatesInGroup(voc) foreach { templateMapper.removeTemplateFromGroup(_, voc) }

            for {
              name <- c.twsTemplates.chosenItemIds
              template <- Option(templateMapper.getTemplateByName(name))
            } templateMapper.addTemplateToGroup(template, voc)

            reload()
          }
        }
      }
    }
  }

  def reload() {
    ui.tblGroups.removeAllItems
    for {
      vo <- templateMapper.getAllTemplateGroups
      id = Int box vo.getId
    } ui.tblGroups.addItem(Array[AnyRef](id, vo.getName, Int box templateMapper.getTemplatesInGroup(vo).length), id)

    canManage |> { value =>
      ui.tblGroups.setSelectable(value)
      doto[{def setEnabled(e: Boolean)}](ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled value }   //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && ui.tblGroups.isSelected) |> { enabled =>
      doto(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
}


class TemplateGroupManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblGroups = new Table with SingleSelect[TemplateGroupId] with Selectable with Immediate
  val rc = new ReloadableContentUI(tblGroups)

  addContainerProperties(tblGroups,
    CP[JInteger]("Id"),
    CP[String]("Name"),
    CP[JInteger]("Templates count"))

  addComponents(this, mb, rc)
}

class TemplateGroupEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name") with Required
  val twsTemplates = new TwinSelect[String]("Templates")

  addComponents(this, txtId, txtName, twsTemplates)
}