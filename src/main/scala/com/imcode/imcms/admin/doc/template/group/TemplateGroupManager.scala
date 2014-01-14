package com.imcode
package imcms
package admin.doc.template
package group

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import _root_.imcode.server.Imcms
import _root_.imcode.server.document.TemplateGroupDomainObject
import com.imcode.imcms.security.{PermissionDenied, PermissionGranted}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._

//todo: form check
//todo: duplicate save check!
//todo: internal error check
class TemplateGroupManager {
  private val templateMapper = Imcms.getServices.getTemplateMapper

  val view = new TemplateGroupManagerView |>> { w =>
    w.miReload.setCommandHandler { _ => reload() }
    w.tblGroups.addValueChangeHandler { _ => handleSelection() }

    w.miNew.setCommandHandler { _ => editAndSave(new TemplateGroupDomainObject(0, null)) }
    w.miEdit.setCommandHandler { _ =>
      whenSelected(w.tblGroups) { id =>
        templateMapper.getTemplateGroupById(id.intValue) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    w.miDelete.setCommandHandler { _ =>
      whenSelected(w.tblGroups) { id =>
        new ConfirmationDialog("Delete selected template group?") |>> { dlg =>
          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              Ex.allCatch.either(templateMapper.deleteTemplateGroup(id.intValue)) match {
                case Right(_) =>
                  Current.page.showInfoNotification("Template group has been deleted")
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
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage template groups")

  /** Edit in a modal dialog. */
  private def editAndSave(vo: TemplateGroupDomainObject) {
    val id = vo.getId
    val isNew = id == 0
    val dialogTitle = if (isNew) "Create new template group" else "Edit template group"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainComponent = new TemplateGroupEditorView |>> { c =>
        c.txtId.value = if (isNew) "" else id.toString
        c.txtName.value = vo.getName.trimToEmpty

        val templatesInGroups = templateMapper.getTemplatesInGroup(vo).asScala.map(_.getName)
        val templatesNotInGroups = templateMapper.getTemplatesNotInGroup(vo).asScala.map(_.getName)

        c.twsTemplates.removeAllItems()
        c.twsTemplates.addItems(templatesInGroups)
        c.twsTemplates.addItems(templatesNotInGroups)
        c.twsTemplates.selection = templatesInGroups

        //templateMapper.getTemplatesInGroup(vo).asScala.foreach(template => c.twsTemplates.addChosenItem(template.getName))
        //templateMapper.getTemplatesNotInGroup(vo).asScala.foreach(template => c.twsTemplates.addAvailableItem(template.getName))

        dlg.setOkButtonHandler {
          Current.ui.privileged(permission) {
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
              name <- c.twsTemplates.selection
              template <- templateMapper.getTemplateByName(name).asOption
            } templateMapper.addTemplateToGroup(template, voc)

            reload()
            dlg.close()
          }
        }
      }
    } |> Current.ui.addWindow
  }

  def reload() {
    view.tblGroups.removeAllItems
    for {
      vo <- templateMapper.getAllTemplateGroups
      id = vo.getId : JInteger
    } view.tblGroups.addItem(Array[AnyRef](id, vo.getName, templateMapper.getTemplatesInGroup(vo).size : JInteger), id)

    canManage |> { value =>
      view.tblGroups.setSelectable(value)
      Seq[{def setEnabled(e: Boolean)}](view.miNew, view.miEdit, view.miDelete).foreach(_.setEnabled(value))   //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && view.tblGroups.isSelected) |> { enabled =>
      Seq(view.miEdit, view.miDelete).foreach(_.setEnabled(enabled))
    }
  }
}