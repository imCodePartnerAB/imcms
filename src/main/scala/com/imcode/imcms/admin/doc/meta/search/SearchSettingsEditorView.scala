package com.imcode
package imcms
package admin.doc.meta.search


import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class SearchSettingsEditorView extends VerticalLayout with Spacing with FullWidth {

  object keywords {
    val lstKeywords = new ListSelect with MultiSelect[Keyword] with Immediate {
      setRows(10)
      setColumns(10)
    }
    val btnAdd = new Button("+") with SmallStyle
    val btnRemove = new Button("-") with SmallStyle
    val txtKeyword = new TextField {
      setInputPrompt("New keyword")
    }
  }

  object misc {
    val chkExcludeFromInternalSearch = new CheckBox("Exclude this page from internal search")
  }

  private val pnlKeywords = new Panel("Keywords") with FullWidth {
    val content = new VerticalLayout with FullWidth with Margin
    val lytBar = new HorizontalLayout with MiddleLeftAlignment with UndefinedSize

    lytBar.addComponents(keywords.txtKeyword, keywords.btnAdd, keywords.btnRemove)
    content.addComponents(lytBar, keywords.lstKeywords)
    setContent(content)
  }

  private val pnlMisc = new Panel("Misc") with FullWidth {
    val content = new VerticalLayout with FullWidth with Margin
    content.addComponent(misc.chkExcludeFromInternalSearch)
    setContent(content)
  }

  addComponents(pnlKeywords, pnlMisc)
}