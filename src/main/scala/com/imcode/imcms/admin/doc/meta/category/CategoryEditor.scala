package com.imcode
package imcms
package admin.doc.meta.category

import com.imcode.imcms.mapping.Meta
import java.util.Collections
import scala.collection.JavaConverters._

import com.vaadin.ui._
import com.imcode.imcms.api._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.Editor

/**
 * Doc's categories editor.
 *
 * Categories are added dynamically in editor and grouped by their type.
 * Single-choice categories appear in a Select component, multi-choice in TwinSelect component.
 * Components (Select and TwinSelect) captions is set to type name.
 */
class CategoryEditor(meta: Meta) extends Editor with ImcmsServicesSupport {
  case class Data(categoriesIds: Set[CategoryId])

  private val initialValues = Data(meta.getCategoryIds.asScala.toSet)

  private val typeCategoriesView: Seq[(CheckBox with ExposeValueChange[JBoolean], SingleSelect[CategoryId])] =
    for {
      cType <- imcmsServices.getCategoryMapper.getAllCategoryTypes.toSeq
      categories = imcmsServices.getCategoryMapper.getAllCategoriesOfType(cType)
      if categories.nonEmpty
    } yield {
      val chkCType = new CheckBox(cType.getName) with ExposeValueChange[JBoolean] with Immediate
      val sltCategories =
        if (cType.isMultiselect) new TwinColSelect with SingleSelect[CategoryId]
        else new ComboBox with SingleSelect[CategoryId] with NoNullSelection

      categories.foreach { category =>
        sltCategories.addItem(category.getId, category.getName)
      }

      chkCType.addValueChangeHandler { _ =>
        sltCategories.setVisible(chkCType.checked)
      }

      chkCType -> sltCategories
    }

  override val view = new GridLayout(2, 1) with Spacing |>> { w =>
    for ((chkCType, sltCategories) <- typeCategoriesView) {
      w.addComponents(chkCType, sltCategories)
    }
  }

  override def resetValues() {
    for ((chkCType, sltCategories) <- typeCategoriesView) {
      chkCType.uncheck()
      sltCategories.clearSelection()

      for (categoryId <- sltCategories.itemIds.asScala if initialValues.categoriesIds(categoryId)) {
        sltCategories.select(categoryId)
        chkCType.check()
      }

      chkCType.fireValueChange()
    }
  }

  override def collectValues(): ErrorsOrData = Right(
    Data(
      typeCategoriesView.collect {
        case (chkCType, sltCategories) if chkCType.checked => sltCategories.selection
      }.flatten.toSet
    )
  )

  resetValues()
}

//class CategoryEditorUI extends VerticalLayout with FullWidth {
//  private val pnlCategories = new Panel("Categories") with FullWidth {
//    val content = new VerticalLayout
//  }
//}