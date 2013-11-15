package com.imcode.imcms.vaadin

import com.imcode.imcms._
import com.vaadin.ui.Component

trait Editor {

  type Data
  final type ErrorsOrData = Seq[ErrorMsg] Either Data

  def ui: Component

  def resetValues()
  def collectValues(): ErrorsOrData
}