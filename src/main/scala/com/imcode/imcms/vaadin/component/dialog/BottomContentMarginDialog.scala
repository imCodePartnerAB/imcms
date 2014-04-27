package com.imcode.imcms.vaadin.component.dialog

import com.vaadin.shared.ui.MarginInfo

trait BottomContentMarginDialog { this: Dialog =>
  content.setMargin(new MarginInfo(false, false, true, false))
}