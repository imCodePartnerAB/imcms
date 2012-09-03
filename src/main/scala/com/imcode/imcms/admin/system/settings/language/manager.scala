package com.imcode
package imcms.admin.system.settings.language

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.ui.Window.Notification
import imcms.security.{PermissionGranted, PermissionDenied}
import imcms.api.I18nLanguage
import imcms.dao.{SystemDao, LanguageDao}

//todo delete in use message
class LanguageManager(app: ImcmsApplication) {
  private val languageDao = Imcms.getServices.getSpringBean(classOf[LanguageDao])
  private val systemDao = Imcms.getServices.getSpringBean(classOf[SystemDao])

  val ui = new LanguageManagerUI |>> { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.tblLanguages addValueChangeHandler { handleSelection() }

    ui.miNew setCommandHandler { editAndSave(I18nLanguage.builder().build()) }
    ui.miEdit setCommandHandler {
      whenSelected(ui.tblLanguages) { id =>
        languageDao.getById(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete setCommandHandler {
      whenSelected(ui.tblLanguages) { id =>
        app.initAndShow(new ConfirmationDialog("Delete selected language?")) { dlg =>
          dlg wrapOkHandler {
            app.privileged(permission) {
              Ex.allCatch.either(languageDao.deleteLanguage(id)) match {
                case Right(_) =>
                  app.showInfoNotification("Language has been deleted")
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
    ui.miSetDefault setCommandHandler {
      whenSelected(ui.tblLanguages) { id =>
        app.initAndShow(new ConfirmationDialog("Change default language?")) { dlg =>
          dlg wrapOkHandler {
            app.privileged(permission) {
              val property = systemDao.getProperty("DefaultLanguageId")
              property.setValue(id.toString)

              Ex.allCatch.either(systemDao saveProperty property) match {
                case Right(_) =>
                  app.showInfoNotification("Default language has been changed")
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
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage languages")

  /** Edit in modal dialog. */
  private def editAndSave(vo: I18nLanguage) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = if(isNew) "Create new language" else "edit Language"

    app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
      dlg.mainUI = new LanguageEditorUI |>> { c =>
        c.txtId.value = if (isNew) "" else id.toString
        c.txtCode.value = vo.getCode |> opt getOrElse ""
        c.txtName.value = vo.getName |> opt getOrElse ""
        c.txtNativeName.value = vo.getNativeName |> opt getOrElse ""
        c.chkEnabled.value = vo.isEnabled

        dlg wrapOkHandler {
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
                  app.getMainWindow.showNotification("Internal error, please contact your administrator", Notification.TYPE_ERROR_MESSAGE)
                  throw ex
                case _ =>
                  (if (isNew) "New language access has been created" else "Language access has been updated") |> { msg =>
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
    ui.tblLanguages.removeAllItems

    val default = Int box systemDao.getProperty("DefaultLanguageId").getValue.toInt
    for {
      vo <- languageDao.getAllLanguages
      id = vo.getId
      isDefault = Boolean box (default == id.intValue)
    } ui.tblLanguages.addItem(Array[AnyRef](id, vo.getCode, vo.getName, vo.getNativeName, Boolean box vo.isEnabled, isDefault), id)

    canManage |> { value =>
      ui.tblLanguages.setSelectable(value)
      doto(ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled value }
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && ui.tblLanguages.isSelected) |> { enabled =>
      doto(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
} // class LanguageManager

class LanguageManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miSetDefault = mb.addItem("Set default", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblLanguages = new Table with SingleSelect[JInteger] with Immediate
  val rc = new ReloadableContentUI(tblLanguages)

  addContainerProperties(tblLanguages,
    CP[JInteger]("Id"),
    CP[String]("Code"),
    CP[String]("Name"),
    CP[String]("Native name"),
    CP[String]("Enabled"),
    CP[String]("Default"))

  addComponents(this, mb, rc)
}

class LanguageEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtCode = new TextField("Code")
  val txtName = new TextField("Name")
  val txtNativeName = new TextField("Native name")
  val chkEnabled = new CheckBox("Enabled")

  addComponents(this, txtId, txtCode, txtName, txtNativeName, chkEnabled)
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
//  addNamedComponents(this,
//    "txtId" -> txtId,
//    "txtCode" -> txtCode,
//    "txtName" -> txtName,
//    "txtNativeName" -> txtNativeName,
//    "chkEnabled" -> chkEnabled)
//}