package com.imcode.imcms.vaadin.component.dialog

import com.vaadin.ui.Window

trait Modal { this: Window =>
  setModal(true)
}
