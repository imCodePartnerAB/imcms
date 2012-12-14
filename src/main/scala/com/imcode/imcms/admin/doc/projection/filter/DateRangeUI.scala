package com.imcode
package imcms
package admin.doc.projection.filter

import com.vaadin.ui.{PopupDateField, ComboBox, HorizontalLayout}
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data.GenericProperty


class DateRangeUI(caption: String = "") extends HorizontalLayout with Spacing with UndefinedSize {
  val cbRangeType = new ComboBox with GenericProperty[DateRangeType.Value] with NoNullSelection with Immediate
  val dtFrom = new PopupDateField with DayResolution
  val dtTo = new PopupDateField with DayResolution

  dtFrom.setInputPrompt("dr.dt_from.prompt".i)
  dtTo.setInputPrompt("dr.dt_to.prompt".i)

  setCaption(caption)

  addComponentsTo(this, cbRangeType, dtFrom, dtTo)
}
