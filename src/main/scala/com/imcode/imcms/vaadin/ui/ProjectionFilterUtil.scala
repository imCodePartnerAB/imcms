package com.imcode
package imcms
package vaadin.ui

import com.vaadin.ui.{Label, Component, CheckBox, CustomLayout}

object ProjectionFilterUtil {

  def toggle(layout: CustomLayout, name: String, checkBox: CheckBox, component: Component,
             stub: => Component = { new Label("projection_filter.lbl_any_value".i) with UndefinedSize }) {

    layout.addComponent(checkBox.checked ? component | stub, name)
  }
}