package com.imcode
package imcms
package admin.doc.projection.filter

import java.util.{Calendar, Date}
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._

trait DateRangeUISetup { this: DateRangeUI =>
  import DateRangeType._

  cbRangeType.addValueChangeHandler {
    doto(dtFrom, dtTo) { _.setEnabled(false) }
    val now = new Date
    val calendar = Calendar.getInstance

    cbRangeType.value match {
      case Undefined =>
        dtFrom.setValue(null)
        dtTo.setValue(null)

      case Custom =>
        doto(dtFrom, dtTo) { dt => dt.setEnabled(true); dt.value = dt.valueOpt.getOrElse(now) }

      case Day =>
        dtFrom.value = calendar.getTime
        dtTo.value = now

      case Week =>
        calendar.add(Calendar.DAY_OF_MONTH, -6)
        dtFrom.value = calendar.getTime
        dtTo.value = now

      case Month =>
        calendar.add(Calendar.MONTH, -1)
        dtFrom.value = calendar.getTime
        dtTo.value = now

      case Quarter =>
        calendar.add(Calendar.MONTH, -3)
        dtFrom.value = calendar.getTime
        dtTo.value = now

      case Year =>
        calendar.add(Calendar.YEAR, -1)
        dtFrom.value = calendar.getTime
        dtTo.value = now
    }
  }

  DateRangeType.values.foreach(cbRangeType addItem _)
  cbRangeType.value = Undefined
}
