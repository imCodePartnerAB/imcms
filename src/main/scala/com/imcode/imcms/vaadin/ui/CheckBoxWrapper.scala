package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.CheckBox

/**/
// todo: remove ambigose properties (isChecjed <-> checked)
class CheckBoxWrapper(checkBox: CheckBox) {
  def isChecked: Boolean = checked
  def isUnchecked: Boolean = !isChecked

  def checked: Boolean = checkBox.getValue
  def checked_=(value: Boolean): Unit = checkBox.setValue(value)

  def check() { checked = true }
  def uncheck() { checked = false }
}
