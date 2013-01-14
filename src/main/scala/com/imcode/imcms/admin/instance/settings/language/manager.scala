package com.imcode
package imcms.admin.instance.settings.language

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{_}
import com.vaadin.ui.Window.Notification
import imcms.security.{PermissionGranted, PermissionDenied}
import imcms.api.I18nLanguage
import imcms.dao.{SystemDao, LanguageDao}
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

//todo delete in use message
class LanguageManager(app: ImcmsUI) {
  private val languageDao = Imcms.getServices.getSpringBean(classOf[LanguageDao])
  private val systemDao = Imcms.getServices.getSpringBean(classOf[SystemDao])

  val ui = new LanguageManagerUI |>> { ui =>
    ui.rc.btnReload.addClickHandler { reload() }
    ui.tblLanguages.addValueChangeHandler { handleSelection() }

    ui.miNew.setCommandHandler { editAndSave(I18nLanguage.builder().build()) }
    ui.miEdit.setCommandHandler {
      whenSelected(ui.tblLanguages) { id =>
        languageDao.getById(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete.setCommandHandler {
      whenSelected(ui.tblLanguages) { id =>
        new ConfirmationDialog("Delete selected language?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(languageDao.deleteLanguage(id)) match {
                case Right(_) =>
                  Page.getCurrent.showInfoNotification("Language has been deleted")
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
    ui.miSetDefault.setCommandHandler {
      whenSelected(ui.tblLanguages) { id =>
        new ConfirmationDialog("Change default language?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              val property = systemDao.getProperty("DefaultLanguageId")
              property.setValue(id.toString)

              Ex.allCatch.either(systemDao saveProperty property) match {
                case Right(_) =>
                  Page.getCurrent.showInfoNotification("Default language has been changed")
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
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = UI.getCurrent.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage languages")

  /** Edit in modal dialog. */
  private def editAndSave(vo: I18nLanguage) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = isNew ? "Create new language" | "edit Language"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainUI = new LanguageEditorUI |>> { c =>
        c.txtId.value = isNew ? "" | id.toString
        c.txtCode.value = vo.getCode |> opt getOrElse ""
        c.txtName.value = vo.getName |> opt getOrElse ""
        c.txtNativeName.value = vo.getNativeName |> opt getOrElse ""
        c.chkEnabled.value = vo.isEnabled

        dlg.setOkButtonHandler {
          I18nLanguage.builder() |> { voc =>
            // todo: validate
            voc.code(c.txtCode.value)
            voc.name(c.txtName.value)
            voc.nativeName(c.txtNativeName.value)
            voc.enabled(c.chkEnabled.value)

            app.privileged(permission) {
              Ex.allCatch.either(languageDao saveLanguage voc.build()) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  Page.getCurrent.showErrorNotification("Internal error, please contact your administrator")
                  throw ex
                case _ =>
                  (isNew ? "New language access has been created" | "Language access has been updated") |> { msg =>
                    Page.getCurrent.showIngoNotification(msg)
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
    ui.tblLanguages.removeAllItems

    val default: JInteger = systemDao.getProperty("DefaultLanguageId").getValue.toInt
    for {
      vo <- languageDao.getAllLanguages.asScala
      id = vo.getId
      isDefault: JBoolean = default == id.intValue
    } ui.tblLanguages.addItem(Array[AnyRef](id, vo.getCode, vo.getName, vo.getNativeName, vo.isEnabled: JBoolean, isDefault), id)

    canManage |> { value =>
      ui.tblLanguages.setSelectable(value)
      doto(ui.miNew, ui.miEdit, ui.miDelete) { _.setEnabled(value) }
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && ui.tblLanguages.isSelected) |> { enabled =>
      doto(ui.miEdit, ui.miDelete) { _.setEnabled(enabled) }
    }
  }
} // class LanguageManager

class LanguageManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miSetDefault = mb.addItem("Set default", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblLanguages = new Table with SingleSelect[JInteger] with Immediate
  val rc = new ReloadableContentUI(tblLanguages)

  addContainerProperties(tblLanguages,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Code"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Native name"),
    PropertyDescriptor[String]("Enabled"),
    PropertyDescriptor[String]("Default"))

  this.addComponents(mb, rc)
}

class LanguageEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtCode = new TextField("Code")
  val txtName = new TextField("Name")
  val txtNativeName = new TextField("Native name")
  val chkEnabled = new CheckBox("Enabled")

  this.addComponents(txtId, txtCode, txtName, txtNativeName, chkEnabled)
}


////
//// Custom UI demo
////
//class LanguageEditorUI extends CustomLayout("LanguageEditorUI") with UndefinedSize{
//  val txtId = new TextField("Id") with Disabled
//  val txtCode = new TextField("Code")
//  val txtName = new TextField("Name")
//  val txtNativeName = new TextField("Native name")
//  val chkEnabled = new CheckBox("Enabled")
//
//  addNamedComponentsTo(this,
//    "txtId" -> txtId,
//    "txtCode" -> txtCode,
//    "txtName" -> txtName,
//    "txtNativeName" -> txtNativeName,
//    "chkEnabled" -> chkEnabled)
//}