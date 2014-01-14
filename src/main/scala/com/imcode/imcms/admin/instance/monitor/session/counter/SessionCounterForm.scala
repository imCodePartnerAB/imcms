package com.imcode
package imcms
package admin.instance.monitor.session.counter

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class SessionCounterForm extends FormLayout with UndefinedSize {
  val txtValue = new TextField("Value")
  val calStart = new DateField("Start date") with DayResolution

  this.addComponent(txtValue)
  this.addComponent(calStart)
}