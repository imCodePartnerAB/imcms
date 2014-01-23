package com.imcode.imcms.vaadin.component

import com.vaadin.ui.{UI, Window}

// implicit
class WindowWrapper(window: Window) {
  def show() = UI.getCurrent.addWindow(window)
}
