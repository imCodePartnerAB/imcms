package com.imcode
package imcms
package admin.doc.meta.category
import com.imcode.imcms.vaadin.ui._
import scala.collection.JavaConverters._

import com.imcode.imcms.api._
import com.imcode.imcms.vaadin._

import com.vaadin.ui._
import java.util.Collections
import com.imcode.imcms.vaadin.ui._

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

  private val typeCategoriesUIs: Seq[(CheckBox with ExposeValueChange, MultiSelectBehavior[CategoryId])] =
    for {
      cType <- imcmsServices.getCategoryMapper.getAllCategoryTypes.toSeq
      categories = imcmsServices.getCategoryMapper.getAllCategoriesOfType(cType)
      if categories.nonEmpty
    } yield {
      val chkCType = new CheckBox(cType.getName) with ExposeValueChange with Immediate
      val sltCategories =
        if (cType.isMultiselect) new TwinColSelect with MultiSelectBehavior[CategoryId]
        else new ComboBox with MultiSelectBehavior[CategoryId] with NoNullSelection

      categories.foreach { category =>
        sltCategories.addItem(category.getId, category.getName)
      }

      chkCType.addValueChangeHandler {
        sltCategories.setVisible(chkCType.isChecked)
      }

      chkCType -> sltCategories
    }

  val ui = new GridLayout(2, 1) with Spacing |>> { ui =>
    for ((chkCType, sltCategories) <- typeCategoriesUIs) {
      addComponentsTo(ui, chkCType, sltCategories)
    }
  }

  def resetValues() {
    for ((chkCType, sltCategories) <- typeCategoriesUIs) {
      chkCType.uncheck()
      sltCategories.value = Collections.emptyList[CategoryId]

      for (categoryId <- sltCategories.itemIds.asScala if initialValues.categoriesIds(categoryId)) {
        sltCategories.select(categoryId)
        chkCType.check()
      }

      chkCType.fireValueChange()
    }
  }

  def collectValues(): ErrorsOrData = Right(
    Data(
      typeCategoriesUIs.collect {
        case (chkCType, sltCategories) if chkCType.isChecked => sltCategories.value.asScala
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