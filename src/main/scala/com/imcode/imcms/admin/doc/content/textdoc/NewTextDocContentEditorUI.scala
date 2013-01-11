package com.imcode.imcms.admin.doc.content.textdoc

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._


class NewTextDocContentEditorUI extends VerticalLayout with FullSize with Spacing with Margin {
  class TextsUI extends FormLayout with FullSize {
    val txtText1 = new TextField("No 1")
    val txtText2 = new TextField("No 2")

    this.addComponents(txtText1, txtText2)
  }

  val chkCopyI18nMetaTextsToTextFields = new CheckBox("Copy link heading & subheading to text 1 & text 2 in page")
                                           with Immediate
  val tsTexts = new TabSheet with UndefinedSize with FullSize

  this.addComponents(chkCopyI18nMetaTextsToTextFields, tsTexts)
  setExpandRatio(tsTexts, 1.0f)
}
