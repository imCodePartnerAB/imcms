package com.imcode
package imcms
package admin.instance.settings.language

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import imcode.server.Imcms
import imcms.security.{PermissionGranted, PermissionDenied}
import imcms.api.DocumentLanguage
import imcms.dao.{SystemDao, LanguageDao}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._

//todo delete in use message
class LanguageManager {
  private val languageDao = Imcms.getServices.getManagedBean(classOf[LanguageDao])
  private val systemDao = Imcms.getServices.getManagedBean(classOf[SystemDao])

  val view = new LanguageManagerView |>> { w =>
    w.miReload.setCommandHandler { _ => reload() }
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
            Current.ui.privileged(permission) {
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
            Current.ui.privileged(permission) {
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

  def canManage = Current.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage languages")

  /** Edit in modal dialog. */
  private def editAndSave(vo: DocumentLanguage) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = if (isNew) "Create new language" else "edit Language"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainComponent = new LanguageEditorView |>> { c =>
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

            Current.ui.privileged(permission) {
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
    view.tblLanguages.removeAllItems

    val default: JInteger = systemDao.getProperty("DefaultLanguageId").getValue.toInt
    for {
      vo <- languageDao.getAllLanguages.asScala
      id = vo.getId
      isDefault = default == id.intValue
    } view.tblLanguages.addItem(
      Array[AnyRef](id, vo.getCode, vo.getName, vo.getNativeName, vo.isEnabled: JBoolean, isDefault: JBoolean),
      id)

    canManage |> { value =>
      view.tblLanguages.setSelectable(value)
      Seq(view.miNew, view.miEdit, view.miDelete).foreach(_.setEnabled(value))
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && view.tblLanguages.isSelected) |> { enabled =>
      Seq(view.miEdit, view.miDelete).foreach(_.setEnabled(enabled))
    }
  }
} // class LanguageManager