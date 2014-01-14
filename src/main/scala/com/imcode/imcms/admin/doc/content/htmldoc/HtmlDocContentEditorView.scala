package com.imcode.imcms.admin.doc.content.htmldoc

import com.imcode.imcms.vaadin.component.{Margin, Spacing, FullSize}
import com.vaadin.ui.{TextArea, VerticalLayout}


class HtmlDocContentEditorView extends  VerticalLayout with Spacing with Margin with FullSize {

  val editor = new TextArea with FullSize

  addComponent(editor)
  setExpandRatio(editor, 1f)
}
