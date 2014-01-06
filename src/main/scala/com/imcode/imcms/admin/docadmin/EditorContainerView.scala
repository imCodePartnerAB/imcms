package com.imcode
package imcms
package admin.docadmin

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class EditorContainerView(title: String = null) extends CustomComponent with FullSize {
  private val lytContent = new VerticalLayout with FullSize
  private val lytComponents = new GridLayout(1, 3) with Spacing with Margin with UndefinedSize
  private val pnlTitle = new Panel(title) with FullHeight
  private val lytButtons = new HorizontalLayout with Spacing with UndefinedSize

  lytContent.addComponent(lytComponents)
  lytContent.setComponentAlignment(lytComponents, Alignment.MIDDLE_CENTER)
  setCompositionRoot(lytContent)

  object buttons {
    val btnSave = new Button("btn_save".i)
    val btnSaveAndClose = new Button("btn_save_and_close".i)
    val btnClose = new Button("btn_close".i)
    val btnReset = new Button("btn_reset".i)
  }

  lytButtons.addComponents(buttons.btnSave, buttons.btnSaveAndClose, buttons.btnClose, buttons.btnReset)

  lytComponents.addComponent(pnlTitle, 0, 0)
  lytComponents.addComponent(lytButtons, 0, 2)
  lytComponents.setComponentAlignment(lytButtons, Alignment.TOP_CENTER)

  def mainComponent: Component = lytComponents.getComponent(0, 1)
  def mainComponent_=(component: Component) {
    lytComponents.removeComponent(0, 1)
    lytComponents.addComponent(component, 0, 1)

    lytComponents.setComponentAlignment(component, Alignment.TOP_CENTER)
  }

  def setTitle(title: String) {
    pnlTitle.setCaption(title)
  }
}
