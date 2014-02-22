package com.imcode
package imcms
package admin.instance.settings.language

import com.imcode.imcms.api.DocumentLanguage
import com.imcode.imcms.mapping.dao.{SystemPropertyDao, DocLanguageDao}
import com.imcode.imcms.mapping.orm.DocLanguage
import com.imcode.imcms.mapping.OrmToApi
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
  private val languageDao = Imcms.getServices.getManagedBean(classOf[DocLanguageDao])
  private val systemDao = Imcms.getServices.getManagedBean(classOf[SystemPropertyDao])

  val view = new LanguageManagerView |>> { v =>
    v.miReload.setCommandHandler { _ => reload() }
    v.tblLanguages.addValueChangeHandler { _ => handleSelection() }

    v.miNew.setCommandHandler { _ => editAndSave(DocumentLanguage.builder().build()) }
    v.miEdit.setCommandHandler { _ =>
      whenSelected(v.tblLanguages) { code =>
        // fixme: use service
        languageDao.getByCode(code) match {
          case null => reload()
          case vo => editAndSave(vo |> OrmToApi.toApi)
        }
      }
    }
    v.miDelete.setCommandHandler { _ =>
      whenSelected(v.tblLanguages) { code =>
        new ConfirmationDialog("Delete selected language?") |>> { dlg =>
          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              Ex.allCatch.either(languageDao.deleteByCode(code)) match {
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
              val id = languageDao.getByCode(code).getId
              val property = systemDao.findByName("DefaultLanguageId")
              property.setValue(id.toString)

              Ex.allCatch.either(systemDao.save(property)) match {
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
      c.chkEnabled.value = vo.isEnabled

      dlg.setOkButtonHandler {
        DocumentLanguage.builder() |> { voc =>
          // todo: validate
          voc.code(c.txtCode.value)
          voc.name(c.txtName.value)
          voc.nativeName(c.txtNativeName.value)
          voc.enabled(c.chkEnabled.value)

          Current.ui.privileged(permission) {
            val language = new DocLanguage
            // fixme: fil in all fields
            language.setCode(c.txtCode.trimmedValue)
            language.setName(c.txtName.trimmedValue)

            Ex.allCatch.either(languageDao.save(language)) match {
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

    dlg.show()
  } // editAndSave

  def reload() {
    view.tblLanguages.removeAllItems()

    val default: JInteger = systemDao.findByName("DefaultLanguageId").getValue.toInt
    for {
      vo <- languageDao.findAll.asScala
      code = vo.getCode
      isDefault = default == vo.getId.intValue
    } view.tblLanguages.addRow(code,
      code, vo.getCode, vo.getName, vo.getNativeName, !vo.isEnabled: JBoolean, isDefault: JBoolean, null
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