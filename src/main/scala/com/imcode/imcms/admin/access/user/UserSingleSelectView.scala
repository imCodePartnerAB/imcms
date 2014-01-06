package com.imcode
package imcms
package admin.access.user

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

class UserSingleSelectView extends HorizontalLayout with Spacing with UndefinedSize {
  val lblName = new Label with UndefinedSize |>> { _.setContentMode(Label.CONTENT_XHTML) }
  val btnSelect = new Button("select") with SmallStyle
  val btnClear = new Button("clear") with SmallStyle

  this.addComponents(lblName, btnSelect, btnClear)
}