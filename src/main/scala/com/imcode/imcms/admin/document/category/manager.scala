package com.imcode
package imcms.admin.document.category

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin._
import com.imcode.imcms.admin.filesystem.{IconImagePicker, FileBrowserWithImagePreview}
import imcode.server.document.{CategoryDomainObject}
import java.io.File

object Types {
  type CategoryId = JInteger
  type CategoryTypeId = JInteger
}

import Types.{CategoryId}


class CategoryManager(app: VaadinApplication) {
  val ui = letret(new CategoryManagerUI) { ui =>
    val categoryMapper = Imcms.getServices.getCategoryMapper

    ui.tblCategories.itemsProvider = () => {
      for {
        cdo <- categoryMapper.getAllCategories
        id = Int box cdo.getId
      } yield {
        id -> Seq(id, cdo.getName, cdo.getDescription, cdo.getImageUrl, cdo.getType.getName)
      }
    }

    ui.rcCategories.btnReload addListener block { ui.tblCategories.reload() }

    ui.miAdd setCommand block {
      app.initAndShow(new OkCancelDialog("New category")) { dlg =>
        let(dlg.setMainContent(new CategoryDialogContentUI(app))) { c =>
          categoryMapper.getAllCategoryTypes foreach { ct =>
            c.sltType addItem ct.getName
          }

          dlg addOkButtonClickListener {
            let(new CategoryDomainObject) { cdo =>
              cdo setName c.txtName.getValue.asInstanceOf[String]
              cdo setDescription c.txtDescription.getValue.asInstanceOf[String]
              cdo setImageUrl ""//embIcon
              cdo setType categoryMapper.getCategoryTypeByName(c.sltType.getValue.asInstanceOf[String])

              categoryMapper saveCategory cdo
              ui.tblCategories.reload()
            }
          }
        }
      }
    }

//    ui.miEdit setCommand block {
//        tblItems.getValue match {
//          case id: JInteger =>
//            categoryMapper.getCategoryById(id.intValue) match {
//              case null => error("No such category")
//              case category =>
//                initAndShow(new OkCancelDialog("Edit category") with CategoryDialog) { w =>
//                  categoryMapper.getAllCategoryTypes foreach { c =>
//                    w.sltType addItem c.getName
//                  }
//
//                  w.txtId setValue id
//                  w.txtName setValue category.getName
//                  w.txtDescription setValue category.getDescription
//
//                  w addOkButtonClickListener {
//                    category setName w.txtName.value
//                    category setDescription w.txtDescription.value
//                    category setType categoryMapper.getCategoryTypeByName(w.sltType.value)
//
//                    categoryMapper saveCategory category
//                    reloadTableItems
//                  }
//                } // initAndShow
//            }
//
//          case _ =>
//        }
//    }

    ui.miDelete setCommand block {
      whenSelected(ui.tblCategories) { id =>
        app.initAndShow(new ConfirmationDialog("Delete category")) { dlg =>
          dlg addOkButtonClickListener {
            let(categoryMapper getCategoryById id.intValue) { cdo =>
              categoryMapper deleteCategoryFromDb cdo
            }
            ui.tblCategories.reload()
          }
        }
      }
    }
  }
}

class CategoryManagerUI extends VerticalLayout with Spacing {
  val mbCategory = new MenuBar
  val miAdd = mbCategory.addItem("Add", null)
  val miEdit = mbCategory.addItem("Edit", null)
  val miDelete = mbCategory.addItem("Delete", null)
  val tblCategories = new Table with Reloadable with SingleSelect2[CategoryId] with Selectable with Immediate
  val rcCategories = new ReloadableContentUI(tblCategories)

  addContainerProperties(tblCategories,
    ContainerProperty[JInteger]("Id"),
    ContainerProperty[String]("Name"),
    ContainerProperty[String]("Description"),
    ContainerProperty[String]("Icon"),
    ContainerProperty[String]("Type"))

  addComponents(this, mbCategory, rcCategories)
}

//todo: dialog, add param - undefined size=true?

class CategoryDialogContentUI(app: VaadinApplication) extends FormLayout {
  val txtId = new TextField("Id") with Disabled {
    setColumns(11)
  }
  val txtName = new TextField("Name")
  val txtDescription = new TextField("Description") {
    setRows(5)
    setColumns(11)
  }

  val sltType = new Select("Type") with ValueType[String] with NoNullSelection

  val embIcon = new IconImagePicker(50, 50) {
    setCaption("Icon")

    btnChoose addListener block {
      app.initAndShow(new OkCancelDialog("Select icon image - .gif  .png  .jpg  .jpeg")
              with CustomSizeDialog with BottomMarginOnlyDialog, resizable = true) { w =>

        let(w.mainContent = new FileBrowserWithImagePreview(100, 100)) { b =>
          b.browser setSplitPosition 30
          b.browser addDirectoryTree("Images", new File(Imcms.getPath, "images"))
          b.browser.tblDirContent setSelectable true

          w.addOkButtonClickListener {
            b.preview.image match {
              case Some(source) => showImage(source)
              case _ => showStub
            }
          }
        }

        w setWidth "650px"
        w setHeight "350px"
      }
    }
  }

  addComponents(this, txtId, txtName, sltType, embIcon, txtDescription)
}

//def categories = new TabSheetView {
//  addTab(new VerticalLayoutUI("Category type") {
//    addComponent(new TableViewTemplate {
//      override def tableProperties = List(
//        ("Id", classOf[JInteger],  null),
//        ("Name", classOf[String],  null),
//        ("Multi select?", classOf[JBoolean],  null),
//        ("Inherited to new documents?", classOf[JBoolean],  null),
//        ("Used by image archive?", classOf[JBoolean],  null))
////        override def tableProperties =
////          ("Id", classOf[JInteger],  null) ::
////          ("Name", classOf[String],  null) ::
////          ("Multi select?", classOf[JBoolean],  null) ::
////          ("Inherited to new documents?", classOf[JBoolean],  null) ::
////          ("Used by image archive?", classOf[JBoolean],  null) ::
////          Nil
//
//      override def tableItems(): Seq[(AnyRef, Seq[AnyRef])] = categoryMapper.getAllCategoryTypes map { t =>
//        (Int box t.getId, Seq(Int box t.getId, t.getName, Boolean box (t.getMaxChoices > 0), Boolean box t.isInherited, Boolean box t.isImageArchive))
//      }
//
//      val btnNew = new Button("New")
//      val btnEdit = new Button("Edit")
//      val btnDelete = new Button("Delete")
//
//      addComponents(pnlHeader, btnNew, btnEdit, btnDelete)
//
//      btnNew addListener block {
//        initAndShow(new OkCancelDialog("New categor type")) { w =>
//          val txtId = new TextField("Id")
//          val txtName = new TextField("Name")
//          val chkMultiSelect = new CheckBox("Multiselect")
//          val chkInherited = new CheckBox("Inherited to new documents")
//          val chkImageArchive = new CheckBox("Used by image archive")
//
//          w.mainContent = new FormLayout {
//            addComponents(this, txtId, txtName, chkMultiSelect, chkInherited, chkImageArchive)
//
//            w.addOkButtonClickListener {
//              let(new CategoryTypeDomainObject(0,
//                  txtName.getValue.asInstanceOf[String],
//                  if (chkMultiSelect.booleanValue) 1 else 0,
//                  chkInherited.booleanValue)) {
//                categoryType =>
//                categoryType.setImageArchive(chkImageArchive.booleanValue)
//
//                categoryMapper addCategoryTypeToDb categoryType
//              }
//
//              reloadTableItems
//            }
//          }
//        }
//      }
//
//      btnDelete addListener block {
//        tblItems.getValue match {
//          case null =>
//          case id: JInteger =>
//            initAndShow(new ConfirmationDialog("Delete category type")) { w =>
//              w.addOkButtonClickListener {
//                val categoryType = categoryMapper getCategoryTypeById id.intValue
//
//                categoryMapper deleteCategoryTypeFromDb  categoryType
//                reloadTableItems
//              }
//            }
//        }
//      }
//    })
//  })
//} // category
