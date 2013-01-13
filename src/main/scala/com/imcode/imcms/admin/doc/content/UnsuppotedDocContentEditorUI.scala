package com.imcode.imcms.admin.doc.content

import com.vaadin.ui.{Alignment, Label, VerticalLayout}
import com.imcode.imcms.vaadin.ui._

class UnsuppotedDocContentEditorUI  extends VerticalLayout with FullSize with Spacing with Margin {
  val lblInfo = new Label("Not Supported") with UndefinedSize

  addComponent(lblInfo)
  setComponentAlignment(lblInfo, Alignment.MIDDLE_CENTER)
}
