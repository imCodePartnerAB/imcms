package com.imcode
package imcms
package vaadin.component.dialog

import com.imcode.imcms.vaadin.data._

/** Error dialog window. */
class OkCancelErrorDialog(msg: String = "") extends OkCancelDialog("dlg_title.error".i) with MsgLabel {
  lblMessage.value = msg
}
