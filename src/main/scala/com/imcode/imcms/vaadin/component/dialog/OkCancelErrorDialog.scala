package com.imcode.imcms.vaadin.component.dialog

/** Error dialog window. */
class OkCancelErrorDialog(msg: String = "") extends OkCancelDialog("dlg_title.error".i) with MsgLabel {
  lblMessage.value = msg
}
