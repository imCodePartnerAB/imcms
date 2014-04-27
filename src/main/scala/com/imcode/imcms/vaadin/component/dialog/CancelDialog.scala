package com.imcode.imcms.vaadin.component.dialog

/** Empty dialog window. */
class CancelDialog(caption: String = "") extends Dialog(caption) with CancelButton {
  footerComponent = btnCancel

  setCancelButtonHandler { close() }
}
