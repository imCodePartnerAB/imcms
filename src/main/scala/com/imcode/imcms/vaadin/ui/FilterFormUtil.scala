package com.imcode
package imcms
package vaadin.ui

import com.vaadin.ui.{Label, Component, CheckBox, CustomLayout}

// projection filter form
object FilterFormUtil {
  def toggle(layout: CustomLayout, name: String, checkBox: CheckBox, component: Component,
             stub: => Component = { new Label("search.frm.fld.lbl_any_value".i) with UndefinedSize }) {

    layout.addComponent(if (checkBox.checked) component else stub, name)
  }
}