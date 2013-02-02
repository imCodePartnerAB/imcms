package com.imcode
package imcms
package admin.docadmin

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._


class FullScreenEditorUI(title: String = null) extends CustomComponent with FullSize {
  private val lytContent = new VerticalLayout with FullSize
  private val lytComponents = new GridLayout(1, 3) with Spacing with Margin with UndefinedSize
  private val pnlTitle = new Panel(title) with FullHeight
  private val lytButtons = new HorizontalLayout with Spacing with UndefinedSize

  lytContent.addComponent(lytComponents)
  lytContent.setComponentAlignment(lytComponents, Alignment.MIDDLE_CENTER)
  setCompositionRoot(lytContent)

  object buttons {
    val btnSave = new Button("Save")
    val btnSaveAndClose = new Button("Save & Close")
    val btnClose = new Button("Close")
  }

  lytButtons.addComponents(buttons.btnSave, buttons.btnSaveAndClose, buttons.btnClose)

  lytComponents.addComponent(pnlTitle, 0, 0)
  lytComponents.addComponent(lytButtons, 0, 2)
  lytComponents.setComponentAlignment(lytButtons, Alignment.TOP_CENTER)

  def mainUI: Component = lytComponents.getComponent(0, 1)
  def mainUI_=(component: Component) {
    lytComponents.removeComponent(0, 1)
    lytComponents.addComponent(component, 0, 1)

    lytComponents.setComponentAlignment(component, Alignment.TOP_CENTER)
  }

  def setTitle(title: String) {
    pnlTitle.setCaption(title)
  }
}
