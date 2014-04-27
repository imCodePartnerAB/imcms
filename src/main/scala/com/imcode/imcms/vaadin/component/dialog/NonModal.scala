package com.imcode.imcms.vaadin.component.dialog

import com.vaadin.ui.Window

trait NonModal { this: Window =>
  setModal(false)
}
