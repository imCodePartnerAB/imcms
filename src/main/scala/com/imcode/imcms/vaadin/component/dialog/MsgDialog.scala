package com.imcode
package imcms
package vaadin.component.dialog

import com.imcode.imcms.vaadin.data._


/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends OKDialog(caption) with MsgLabel {
  lblMessage.value = msg

  setOkButtonHandler { close() }
}
