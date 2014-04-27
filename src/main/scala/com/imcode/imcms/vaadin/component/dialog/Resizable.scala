package com.imcode.imcms.vaadin.component.dialog

import com.vaadin.ui.Window

trait Resizable { this: Window =>
  setResizable(true)
}
