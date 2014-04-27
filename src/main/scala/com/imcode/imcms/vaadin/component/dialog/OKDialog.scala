package com.imcode.imcms.vaadin.component.dialog

/** Empty dialog window. */
class OKDialog(caption: String = "") extends Dialog(caption) with OKButton {
  footerComponent = btnOk
}
