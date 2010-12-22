package com.imcode
package imcms.admin.document.category.`type`

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin._
import imcode.server.document.{CategoryTypeDomainObject}
import com.imcode.imcms.admin.document.category.{CategoryTypeId}

class CategoryTypeManager(app: VaadinApplication) {
  val ui = letret(new CategoryTypeManagerUI) { ui =>
    val categoryMapper = Imcms.getServices.getCategoryMapper

    ui.tblTypes.itemsProvider = () => {
      for {
        tdo <- categoryMapper.getAllCategoryTypes
        id = Int box tdo.getId
      } yield {
        id -> Seq(id, tdo.getName, Boolean box (tdo.getMaxChoices > 0), Boolean box tdo.isInherited, Boolean box tdo.isImageArchive)
      }
    }

    ui.rc.btnReload addListener block { ui.tblTypes.reload() }

    ui.miNew setCommand block {
      app.initAndShow(new OkCancelDialog("New categor type")) { dlg =>
        val txtId = new TextField("Id")
        val txtName = new TextField("Name")
        val chkMultiSelect = new CheckBox("Multiselect")
        val chkInherited = new CheckBox("Inherited to new documents")
        val chkImageArchive = new CheckBox("Used by image archive")

        dlg.mainContent = new FormLayout {
          addComponents(this, txtId, txtName, chkMultiSelect, chkInherited, chkImageArchive)

          dlg.addOkButtonClickListener {
            let(new CategoryTypeDomainObject(0,
                txtName.getValue.asInstanceOf[String],
                if (chkMultiSelect.booleanValue) 1 else 0,
                chkInherited.booleanValue)) {
              categoryType =>
              categoryType.setImageArchive(chkImageArchive.booleanValue)

              categoryMapper addCategoryTypeToDb categoryType
            }

            ui.tblTypes.reload()
          }
        }
      }
    }

    ui.miDelete setCommand block {
      whenSelected(ui.tblTypes) { id =>
        app.initAndShow(new ConfirmationDialog("Delete category type")) { dlg =>
          dlg.addOkButtonClickListener {
            val categoryType = categoryMapper getCategoryTypeById id.intValue

            categoryMapper deleteCategoryTypeFromDb  categoryType
            ui.tblTypes.reload()
          }
        }
      }
    }
  }
}

class CategoryTypeManagerUI extends VerticalLayout {
  val mb = new MenuBar
  val miNew = mb.addItem("New", null)
  val miEdit = mb.addItem("Edit", null)
  val miDelete = mb.addItem("Delete", null)
  val tblTypes = new Table with Reloadable with SingleSelect2[CategoryTypeId] with Selectable with Immediate
  val rc = new ReloadableContentUI(tblTypes)

  addContainerProperties(tblTypes,
    ContainerProperty[JInteger]("Id"),
    ContainerProperty[String]("Name"),
    ContainerProperty[JBoolean]("Multi select?"),
    ContainerProperty[JBoolean]("Inherited to new documents?"),
    ContainerProperty[JBoolean]("Used by image archive?"))

  addComponents(this, mb, rc)
}

