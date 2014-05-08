package com.imcode
package imcms
package vaadin.component.dialog

import com.imcode.imcms.vaadin.component.UndefinedSize
import com.vaadin.ui.Label

/** Confirmation dialog window. */
class ConfirmationDialog(caption: String, msg: String) extends OkCancelDialog(caption) {
  def this(msg: String = "") = this("dlg_title.confirmaton".i, msg)

  val lblMessage = new Label(msg) with UndefinedSize

  mainComponent = lblMessage
}