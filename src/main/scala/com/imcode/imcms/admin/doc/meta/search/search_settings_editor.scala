package com.imcode
package imcms
package admin.doc.meta.search

import scala.collection.JavaConverters._
import com.imcode.imcms.api.Meta
import com.imcode.imcms.vaadin._
import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._

/**
 * Doc's search settings editor.
 */
class SearchSettingsEditor(meta: Meta) extends Editor {
  case class Data(keywords: Set[Keyword], isExcludeFromInnerSearch: Boolean)

  private val initialValues = Data(meta.getKeywords.asScala.map(_.toLowerCase).toSet, false)

  val ui = new SearchSettingsEditorUI |>> { ui =>
    import ui.keywords.{btnAdd, btnRemove, txtKeyword, lstKeywords}

    btnAdd.addClickHandler {
      txtKeyword.trim.toLowerCase match {
        case value if value.length > 0 && lstKeywords.getItem(value) == null =>
          setKeywords(lstKeywords.itemIds.asScala.toSet + value)

        case _ =>
      }

      txtKeyword.value = ""
    }

    btnRemove.addClickHandler {
      whenSelected(lstKeywords) { keywords => keywords.asScala.foreach(lstKeywords removeItem _) }
    }

    lstKeywords.addValueChangeHandler {
      lstKeywords.value.asScala.toSeq match {
        case Seq(value) => txtKeyword.value = value
        case Seq(_, _, _*) => txtKeyword.value = ""
        case _ =>
      }
    }
  } // ui

  resetValues()


  private def setKeywords(keywords: Set[Keyword]) {
    ui.keywords.lstKeywords.itemIds = keywords.map(_.toLowerCase).toSeq.sorted.asJava
  }


  def resetValues() {
    setKeywords(initialValues.keywords)
    ui.misc.chkExcludeFromInternalSearch.checked = initialValues.isExcludeFromInnerSearch
  }

  def collectValues(): ErrorsOrData = Data(
    ui.keywords.lstKeywords.itemIds.asScala.toSet,
    ui.misc.chkExcludeFromInternalSearch.isChecked
  ) |> Right.apply

  //def isModified = state != initialData
}


class SearchSettingsEditorUI extends VerticalLayout with Spacing with FullWidth {

  object keywords {
    val lstKeywords = new ListSelect with MultiSelect[Keyword] with Immediate {
      setRows(10)
      setColumns(10)
    }
    val btnAdd = new Button("+")
    val btnRemove = new Button("-")
    val txtKeyword = new TextField {
      setInputPrompt("New keyword")
    }
  }

  object misc {
    val chkExcludeFromInternalSearch = new CheckBox("Exclude this page from internal search")
  }

  private val pnlKeywords = new Panel("Keywords") with FullWidth {
    val content = new VerticalLayout with FullWidth with Margin
    val lytBar = new HorizontalLayout with UndefinedSize

    addComponentsTo(lytBar, keywords.txtKeyword, keywords.btnAdd, keywords.btnRemove)
    addComponentsTo(content, lytBar, keywords.lstKeywords)
    setContent(content)
  }

  private val pnlMisc = new Panel("Misc") with FullWidth {
    val content = new VerticalLayout with FullWidth with Margin
    content.addComponent(misc.chkExcludeFromInternalSearch)
    setContent(content)
  }

  addComponentsTo(this, pnlKeywords, pnlMisc)
}