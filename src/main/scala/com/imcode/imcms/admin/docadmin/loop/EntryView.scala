package com.imcode
package imcms
package admin.docadmin.loop

import com.imcode.imcms.vaadin.component.{Spacing, FullWidth, FullSize}
import com.vaadin.ui._

class EntryView extends CustomComponent with FullSize {

  val btnMoveUp = new Button("Move up")
  val btnMoveDown = new Button("Move down")
  val btnDelete = new Button("Delete")
  val lblText = new Label with FullWidth

  private val lytContent = new VerticalLayout(lblText, btnMoveUp, btnMoveDown, btnDelete) with FullWidth with Spacing
  private val pnlContent = new Panel(lytContent) with FullWidth

  setCompositionRoot(pnlContent)


}
