package com.imcode
package imcms.admin.document.template.group

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcode.server.document.{TemplateGroupDomainObject}
import com.vaadin.ui.Window.Notification


//todo: canManage => wrap with privileged() {} ...
//form check
class TemplateGroupManager(app: ImcmsApplication) {
  val templateMapper = Imcms.getServices.getTemplateMapper

  val ui = letret(new TemplateGroupManagerUI) { ui =>
    ui.rc.btnReload addListener block { reload() }
    ui.tblGroups addListener block { handleSelection() }

    ui.miGroupNew setCommand block { editAndSave(new TemplateGroupDomainObject(0, null)) }
    ui.miGroupEdit setCommand block {
      whenSelected(ui.tblGroups) { id =>
        templateMapper.getTemplateGroupById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miGroupDelete setCommand block {
      whenSelected(ui.tblGroups) { id =>
        app.initAndShow(new ConfirmationDialog("Delete template group")) { dlg =>
          dlg addOkHandler {
            if (canManage) templateMapper deleteTemplateGroup id.intValue
            else error("NO PERMISSIONS")
            reload()
          }
        }
      }
    }
  } // ui

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin

  /** Edit in a modal dialog. */
  private def editAndSave(vo: TemplateGroupDomainObject) {
    val id = vo.getId
    val isNew = id == 0
    val dialogTitle = if(isNew) "Create new template group" else "Edit template group"

    app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
      dlg.mainUI = letret(new TemplateGroupDialogMainUI) { c =>
        c.txtId.value = if (isNew) "" else id.toString
        c.txtName.value = ?(vo.getName) getOrElse ""
        templateMapper.getTemplatesInGroup(vo) foreach (c.twsTemplates addChosenItem _.getName)
        templateMapper.getTemplatesNotInGroup(vo) foreach (c.twsTemplates addAvailableItem _.getName)

        dlg.addOkHandler {
//                templateMapper.createTemplateGroup(c.txtName.value)
//                val group = templateMapper.getTemplateGroupByName(c.txtName.value)
//                c.twsTemplates.chosenItemIds foreach { name =>
//                  templateMapper.getTemplateByName(name) match {
//                    case null =>
//                    case t => templateMapper.addTemplateToGroup(t, group)
//                  }
//                }

          let(vo.clone()) { voc =>
            templateMapper.renameTemplateGroup(voc, c.txtName.value)
//            templateMapper.getTemplatesInGroup(voc) foreach { t =>    // test
//              templateMapper.removeTemplateFromGroup(t, voc)
//            }
            templateMapper.getTemplatesInGroup(voc) foreach { templateMapper.removeTemplateFromGroup(_, voc) }

            for {
              name <- c.twsTemplates.chosenItemIds
              template <- ?(templateMapper.getTemplateByName(name))
            } templateMapper.addTemplateToGroup(template, voc)

            reload()
          }
        }
      }
    }
  }

  // todo: add can manage check
  def reload() {
    ui.tblGroups.removeAllItems
    templateMapper.getAllTemplateGroups foreach { vo =>
      let(Int box vo.getId) { id =>
        ui.tblGroups.addItem(Array[AnyRef](id, vo.getName, Int box templateMapper.getTemplatesInGroup(vo).length), id)
      }
    }

    handleSelection()
  }

  private def handleSelection() {
    let(canManage && ui.tblGroups.isSelected) { isSelected =>
      ui.miGroupEdit.setEnabled(isSelected)
      ui.miGroupDelete.setEnabled(isSelected)
    }
  }
}


class TemplateGroupManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miGroup = mb.addItem("Group")
  val miHelp = mb.addItem("Help", Help16)
  val miGroupNew = miGroup.addItem("Add new", New16)
  val miGroupEdit = miGroup.addItem("Edit", Edit16)
  val miGroupDelete = miGroup.addItem("Delete", Delete16)
  val tblGroups = new Table with SingleSelect2[JInteger] with Selectable with Immediate
  val rc = new ReloadableContentUI(tblGroups)

  addContainerProperties(tblGroups,
    CP[JInteger]("Id"),
    CP[String]("Name"),
    CP[JInteger]("Templates count"))

  addComponents(this, mb, rc)
}

class TemplateGroupDialogMainUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name") with Required
  val twsTemplates = new TwinSelect[String]("Templates")

  addComponents(this, txtId, txtName, twsTemplates)
}