package com.imcode
package imcms
package admin.docadmin

import com.vaadin.server.Sizeable
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class EditorContainerView(title: String = null) extends CustomComponent with FullSize {
  private val lytContent = new VerticalLayout with FullSize
  private val lytComponents = new GridLayout(1, 3) with Spacing with FullSize
  private val lblTitle = new Label with UndefinedSize |>> { _.setContentMode(ContentMode.HTML) }
  private val lytButtons = new HorizontalLayout with Spacing with UndefinedSize

  lytContent.addComponent(lytComponents)
  lytContent.setComponentAlignment(lytComponents, Alignment.MIDDLE_CENTER)
  setCompositionRoot(lytContent)

  object buttons {
    val btnSave = new Button("btn_caption.save".i)
    val btnSaveAndClose = new Button("btn_caption.save_and_close".i)
    val btnClose = new Button("btn_caption.close".i)
    val btnReset = new Button("btn_caption.reset".i)
  }

  lytButtons.addComponents(buttons.btnSave, buttons.btnSaveAndClose, buttons.btnClose, buttons.btnReset)

  lytComponents.addComponent(lblTitle, 0, 0)
  lytComponents.addComponent(lytButtons, 0, 2)
  lytComponents.setComponentAlignment(lytButtons, Alignment.TOP_CENTER)
  lytComponents.setRowExpandRatio(1, 1.0f)

  def mainComponent: Component = lytComponents.getComponent(0, 1)
  def mainComponent_=(component: Component) {
    lytComponents.removeComponent(0, 1)
    lytComponents.addComponent(component, 0, 1)

    lytComponents.setComponentAlignment(component, Alignment.TOP_CENTER)
  }

  def setTitle(title: String) {
    lblTitle.setValue(s"<h2>$title</h2>")
  }

  def setContentSize(width: Float, height: Float, units: Sizeable.Unit = Sizeable.Unit.PIXELS) {
    lytComponents.setSize(width, height, units)
  }

  setTitle(title)
  setContentSize(800, 700)
}
