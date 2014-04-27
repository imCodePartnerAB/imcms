package com.imcode.imcms.vaadin.component.dialog

import com.imcode.imcms.vaadin.component.Spacing
import com.vaadin.ui.{Alignment, GridLayout}

/** OKCancel dialog window. */
class OkCancelDialog(caption: String = "") extends Dialog(caption) with OKButton with CancelButton {

  val lytButtons = new GridLayout(2, 1) with Spacing |>> { lyt =>
    lyt.addComponents( btnOk, btnCancel)

    lyt.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
    lyt.setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)

    lyt.addStyleName("dialog-buttons")
  }

  footerComponent = lytButtons
}
