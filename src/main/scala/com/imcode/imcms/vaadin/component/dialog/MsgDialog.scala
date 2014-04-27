package com.imcode.imcms.vaadin.component.dialog

/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends OKDialog(caption) with MsgLabel {
  lblMessage.value = msg

  setOkButtonHandler { close() }
}
