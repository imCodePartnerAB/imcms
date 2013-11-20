package com.imcode
package imcms.admin.instance.settings.language

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcode.server.{Imcms}
import imcms.security.{PermissionGranted, PermissionDenied}
import imcms.api.DocumentLanguage
import imcms.dao.{SystemDao, LanguageDao}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

//todo delete in use message
class LanguageManager(app: UI) {
  private val languageDao = Imcms.getServices.getManagedBean(classOf[LanguageDao])
  private val systemDao = Imcms.getServices.getManagedBean(classOf[SystemDao])

  val widget = new LanguageManagerWidget |>> { w =>
    w.rc.btnReload.addClickHandler { _ => reload() }
    w.tblLanguages.addValueChangeHandler { _ => handleSelection() }

    w.miNew.setCommandHandler { _ => editAndSave(DocumentLanguage.builder().build()) }
    w.miEdit.setCommandHandler { _ =>
      whenSelected(w.tblLanguages) { id =>
        languageDao.getById(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    w.miDelete.setCommandHandler { _ =>
      whenSelected(w.tblLanguages) { id =>
        new ConfirmationDialog("Delete selected language?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(languageDao.deleteLanguage(id)) match {
                case Right(_) =>
                  Current.page.showInfoNotification("Language has been deleted")
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
    w.miSetDefault.setCommandHandler { _ =>
      whenSelected(w.tblLanguages) { id =>
        new ConfirmationDialog("Change default language?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              val property = systemDao.getProperty("DefaultLanguageId")
              property.setValue(id.toString)

              Ex.allCatch.either(systemDao saveProperty property) match {
                case Right(_) =>
                  Current.page.showInfoNotification("Default language has been changed")
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
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = Current.ui.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage languages")

  /** Edit in modal dialog. */
  private def editAndSave(vo: DocumentLanguage) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = if (isNew) "Create new language" else "edit Language"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainWidget = new LanguageEditorWidget |>> { c =>
        c.txtId.value = if (isNew) "" else id.toString
        c.txtCode.value = vo.getCode.trimToEmpty
        c.txtName.value = vo.getName.trimToEmpty
        c.txtNativeName.value = vo.getNativeName.trimToEmpty
        c.chkEnabled.value = vo.isEnabled

        dlg.setOkButtonHandler {
          DocumentLanguage.builder() |> { voc =>
            // todo: validate
            voc.code(c.txtCode.value)
            voc.name(c.txtName.value)
            voc.nativeName(c.txtNativeName.value)
            voc.enabled(c.chkEnabled.value)

            app.privileged(permission) {
              Ex.allCatch.either(languageDao saveLanguage voc.build()) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  Current.page.showErrorNotification("Internal error, please contact your administrator")
                  throw ex
                case _ =>
                  (if (isNew) "New language access has been created" else "Language access has been updated") |> { msg =>
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
    widget.tblLanguages.removeAllItems

    val default: JInteger = systemDao.getProperty("DefaultLanguageId").getValue.toInt
    for {
      vo <- languageDao.getAllLanguages.asScala
      id = vo.getId
      isDefault = default == id.intValue
    } widget.tblLanguages.addItem(
      Array[AnyRef](id, vo.getCode, vo.getName, vo.getNativeName, vo.isEnabled: JBoolean, isDefault: JBoolean),
      id)

    canManage |> { value =>
      widget.tblLanguages.setSelectable(value)
      Seq(widget.miNew, widget.miEdit, widget.miDelete).foreach(_.setEnabled(value))
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && widget.tblLanguages.isSelected) |> { enabled =>
      Seq(widget.miEdit, widget.miDelete).foreach(_.setEnabled(enabled))
    }
  }
} // class LanguageManager

class LanguageManagerWidget extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miSetDefault = mb.addItem("Set default", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblLanguages = new Table with SingleSelect[JInteger] with Immediate
  val rc = new ReloadableContentWidget(tblLanguages)

  addContainerProperties(tblLanguages,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Code"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Native name"),
    PropertyDescriptor[JBoolean]("Enabled"),
    PropertyDescriptor[JBoolean]("Default"))

  this.addComponents(mb, rc)
}

class LanguageEditorWidget extends FormLayout with UndefinedSize {
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