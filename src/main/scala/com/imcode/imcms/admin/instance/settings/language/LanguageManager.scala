package com.imcode
package imcms
package admin.instance.settings.language

import com.imcode.imcms.api.DocumentLanguage
import com.imcode.imcms.mapping.{DocumentLanguageMapper}
import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import _root_.imcode.server.Imcms
import com.imcode.imcms.security.{PermissionGranted, PermissionDenied}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.server._

//todo delete in use message
class LanguageManager {
  private val languageMapper = Imcms.getServices.getManagedBean(classOf[DocumentLanguageMapper])

  val view = new LanguageManagerView |>> { v =>
    v.miReload.setCommandHandler { _ => reload() }
    v.tblLanguages.addValueChangeHandler { _ => handleSelection() }

    v.miNew.setCommandHandler { _ => editAndSave(DocumentLanguage.builder().build()) }
    v.miEdit.setCommandHandler { _ =>
      whenSelected(v.tblLanguages) { code =>
        languageMapper.findByCode(code) match {
          case null => reload()
          case language => editAndSave(language)
        }
      }
    }
    v.miDelete.setCommandHandler { _ =>
      whenSelected(v.tblLanguages) { code =>
        new ConfirmationDialog("Delete selected language?") |>> { dlg =>
          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              Ex.allCatch.either(languageMapper.deleteByCode(code)) match {
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
    v.miSetAsDefault.setCommandHandler { _ =>
      whenSelected(v.tblLanguages) { code =>
        new ConfirmationDialog("Change default language?") |>> { dlg =>
          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              Ex.allCatch.either(languageMapper.setDefault(code)) match {
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
    val code = vo.getCode
    val isNew = code == null
    val dialogTitle = if (isNew) "doc_language_editor_dlg_title.new".i else "doc_language_editor_dlg_title.edit".f(vo.getName)

    val dlg = new OkCancelDialog(dialogTitle) with OKCaptionIsSave
    dlg.mainComponent = new LanguageEditorView |>> { c =>
      c.txtId.value = if (isNew) "" else ""
      c.txtCode.value = vo.getCode.trimToEmpty
      c.txtName.value = vo.getName.trimToEmpty
      c.txtNativeName.value = vo.getNativeName.trimToEmpty
      c.chkEnabled.value = true

      dlg.setOkButtonHandler {
        // todo: validate
        val language = DocumentLanguage.builder()
          .code(c.txtCode.value)
          .name(c.txtName.value)
          .nativeName(c.txtNativeName.value)
          .build()

        Current.ui.privileged(permission) {
          Ex.allCatch.either(languageMapper.save(language)) match {
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

    dlg.show()
  } // editAndSave

  def reload() {
    view.tblLanguages.removeAllItems()

    val defaultLanguage = languageMapper.getDefault
    for {
      language <- languageMapper.getAll
      code = vo.getCode
      isDefault = language == defaultLanguage
    } view.tblLanguages.addRow(code,
      code, code, language.getName, language.getNativeName, true: JBoolean, isDefault: JBoolean, null
    )

    canManage |> { value =>
      view.tblLanguages.setSelectable(value)
      Seq(view.miNew, view.miEdit, view.miDelete).foreach(_.setEnabled(value))
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && view.tblLanguages.isSelected) |> { enabled =>
      Seq(view.miEdit, view.miDelete, view.miSetAsDefault).foreach(_.setEnabled(enabled))
    }
  }
} // class LanguageManager