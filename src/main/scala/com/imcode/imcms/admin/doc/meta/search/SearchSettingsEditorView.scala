package com.imcode
package imcms
package admin.doc.meta.search


import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class SearchSettingsEditorView extends TabSheet with TabSheetSmallStyle with FullSize {

  object keywords {
//    val mb = new MenuBar with MenuBarInTabStyle with FullWidth
//    val miAdd = mb.addItem("mi.add".i)
//    val miRemove = mb.addItem("mi.remove".i)
//    val miHelp = mb.addItem("mi.help".i)
//    val tblKeywords = new Table with BorderlessStyle with FullWidth |>> { tbl =>
//    }

    val lstKeywords = new ListSelect with MultiSelect[Keyword] with Immediate  with FullSize
    val btnAdd = new Button("+") with SmallStyle
    val btnRemove = new Button("-") with SmallStyle
    val txtKeyword = new TextField with FullWidth |>> { _.setInputPrompt("New keyword") }

    private val lytButtons = new HorizontalLayout(btnAdd, btnRemove) with UndefinedSize
    private val lytToolBar = new HorizontalLayout(txtKeyword, lytButtons) with FullWidth with Spacing with MiddleLeftAlignment |>> {
      _.setExpandRatio(txtKeyword, 1.0f)
    }

    val content = new VerticalLayout(lytToolBar, lstKeywords) with Spacing with Margin |>> { lyt =>
      lyt.setExpandRatio(lstKeywords, 1.0f)
      lyt.setSize(300, 200)
    }
  }

  object misc {
    val chkExcludeFromInternalSearch = new CheckBox("Exclude this page from internal search")
    val content = new FormLayout(chkExcludeFromInternalSearch)
  }

  addTab(keywords.content, "Keywords")
  addTab(misc.content, "Misc")
}