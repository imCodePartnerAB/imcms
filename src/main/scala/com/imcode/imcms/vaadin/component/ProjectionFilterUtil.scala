package com.imcode
package imcms
package vaadin.component

import com.vaadin.ui.{Label, Component, CheckBox, CustomLayout}

object ProjectionFilterUtil {

  /**
   * Toggles between two components by hiding one and displaying another depending on a checkbox checked status.
   *
   * @param layout
   * @param name
   * @param checkBox
   * @param component
   * @param stub
   */
  def toggle(layout: CustomLayout,
             name: String,
             checkBox: CheckBox,
             component: Component,
             stub: => Component = {
               new Label("projection_filter.lbl_any_value".i) with UndefinedSize
             }
            ) {

    layout.addComponent(if (checkBox.checked) component else stub, name)
  }
}