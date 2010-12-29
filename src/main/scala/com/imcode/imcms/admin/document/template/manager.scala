package com.imcode
package imcms.admin.document.template

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.ui.Window.Notification
import imcode.server.document.{TemplateDomainObject, TemplateGroupDomainObject}

class TemplateManager(app: ImcmsApplication) {
  val fileExtRE = """(?i).*\.(jsp|jspx|html)""".r
  val templateMapper = Imcms.getServices.getTemplateMapper

  val ui = letret(new TemplateManagerUI) { ui =>
    ui.rc.btnReload addListener block { reload() }
    ui.tblTemplates addListener block { handleSelection() }

    ui.miNew setCommand block { editAndSave(new TemplateDomainObject(null, null)) }
    ui.miEdit setCommand block {
      whenSelected(ui.tblTemplates) { name =>
        templateMapper.getTemplateByName(name) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete setCommand block {
      whenSelected(ui.tblTemplates) { name =>
        app.initAndShow(new ConfirmationDialog("Delete selected template?")) { dlg =>
          dlg addOkHandler {
            if (canManage) templateMapper deleteTemplate templateMapper.getTemplateByName(name)
            else error("NO PERMISSIONS")
            reload()
          }
        }
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin

  /** Edit in a modal dialog. */
  private def editAndSave(vo: TemplateDomainObject) {
    val name = vo.getName
    val isNew = name == null
    val dialogTitle = if(isNew) "Create new template" else "Edit template"

    app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
      dlg.mainUI = letret(new TemplateEditorUI) { c =>
      }
    }
  }

  //todo add sec check
  def reload() {
    ui.tblTemplates.removeAllItems
    for {
      vo <- templateMapper.getAllTemplates
      name = vo.getName
      kind = fileExtRE.unapplySeq(vo.getFileName) map (_.head) get
    } ui.tblTemplates.addItem(Array[AnyRef](name, kind, Int box templateMapper.getCountOfDocumentsUsingTemplate(vo)), name)

    handleSelection()
  }

  private def handleSelection() {
    let(canManage && ui.tblTemplates.isSelected) { isSelected =>
      ui.miEdit.setEnabled(isSelected)
      ui.miEditContent.setEnabled(isSelected)
      ui.miDelete.setEnabled(isSelected)
    }
  }
}

class TemplateManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miEditContent = mb.addItem("Edit content", EditContent16)
  val miHelp = mb.addItem("Help", Help16)
  val tblTemplates = new Table with SingleSelect2[String] with Selectable with Immediate
  val rc = new ReloadableContentUI(tblTemplates)

  addContainerProperties(tblGroups,
    CP[String]("Name"),
    CP[String]("Kind"),
    CP[JInteger]("Documents count using template"))

  addComponents(this, mb, rc)
}

class TemplateEditorUI extends FormLayout with UndefinedSize {
  val uploadReceiver = new MemoryUploadReceiver
  val txtName = new TextField
  val chkUseFilenameAsName = new CheckBox("Use filename") { setImmediate(true) }
  val chkOverwriteExisting = new CheckBox("Replace existing")
}

