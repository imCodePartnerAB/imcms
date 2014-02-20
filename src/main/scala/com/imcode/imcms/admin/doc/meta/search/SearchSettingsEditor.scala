package com.imcode
package imcms
package admin.doc.meta.search

import com.imcode.imcms.mapping.Meta
import scala.collection.JavaConverters._

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.Editor

/**
 * Doc's search settings editor.
 */
class SearchSettingsEditor(meta: Meta) extends Editor {
  case class Data(keywords: Set[Keyword], isExcludeFromInnerSearch: Boolean)

  private val initialValues = Data(meta.getKeywords.asScala.map(_.toLowerCase).toSet, false)

  override val view = new SearchSettingsEditorView |>> { w =>
    import w.keywords.{btnAdd, btnRemove, txtKeyword, lstKeywords}

    btnAdd.addClickHandler { _ =>
      txtKeyword.trimmedValue.toLowerCase match {
        case value if value.length > 0 && lstKeywords.getItem(value) == null =>
          setKeywords(lstKeywords.itemIds.asScala.toSet + value)

        case _ =>
      }

      txtKeyword.value = ""
    }

    btnRemove.addClickHandler { _ =>
      whenSelected(lstKeywords) { keywords => keywords.foreach(lstKeywords removeItem _) }
    }

    lstKeywords.addValueChangeHandler { _ =>
      lstKeywords.selection match {
        case Seq(value) => txtKeyword.value = value
        case Seq(_, _, _*) => txtKeyword.value = ""
        case _ =>
      }
    }
  } // widget

  resetValues()


  private def setKeywords(keywords: Set[Keyword]) {
    view.keywords.lstKeywords.setItems(keywords.map(_.toLowerCase).toSeq.sorted)
  }


  override def resetValues() {
    setKeywords(initialValues.keywords)
    view.misc.chkExcludeFromInternalSearch.checked = initialValues.isExcludeFromInnerSearch
  }

  override def collectValues(): ErrorsOrData = Data(
    view.keywords.lstKeywords.itemIds.asScala.toSet,
    view.misc.chkExcludeFromInternalSearch.checked
  ) |> Right.apply

  //def isModified = state != initialData
}