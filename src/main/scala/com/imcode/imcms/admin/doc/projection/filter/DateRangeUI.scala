package com.imcode.imcms.admin.doc.projection.filter

import com.vaadin.ui.{PopupDateField, ComboBox, HorizontalLayout}
import com.imcode.imcms.vaadin.ui._


class DateRangeUI(caption: String = "") extends HorizontalLayout with Spacing with UndefinedSize {
  val cbRangeType = new ComboBox with SingleSelect[DateRangeType.Value] with NoNullSelection with Immediate
  val dtFrom = new PopupDateField with DayResolution
  val dtTo = new PopupDateField with DayResolution

  dtFrom.setInputPrompt("date_range.dt_from.prompt".i)
  dtTo.setInputPrompt("date_range.dt_to.prompt".i)

  setCaption(caption)

  this.addComponents(cbRangeType, dtFrom, dtTo)
}
