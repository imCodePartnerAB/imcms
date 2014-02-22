package com.imcode.imcms.vaadin.component

import com.vaadin.ui.CheckBox

/**/
class CheckBoxWrapper(checkBox: CheckBox) {

  def checked: Boolean = checkBox.getValue

  def checked_=(value: Boolean): Unit = checkBox.setValue(value)

  def unchecked: Boolean = !checkBox.getValue

  def unchecked_=(value: Boolean): Unit = checkBox.setValue(!value)

  def check() {
    checked = true
  }

  def uncheck() {
    checked = false
  }
}
