package com.imcode.imcms.admin.doc.content.textdoc

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class NewTextDocContentEditorView extends VerticalLayout with FullSize with Spacing with Margin {

  val chkCopyDocCommonContentIntoTextFields = new CheckBox("Copy link heading & subheading to text 1 & text 2")
                                           with Immediate

  private val lytContent = new VerticalLayout with Margin
  private val pnlOptions = new Panel("Options", lytContent) with FullSize

  lytContent.addComponents(chkCopyDocCommonContentIntoTextFields)

  addComponent(pnlOptions)
}
