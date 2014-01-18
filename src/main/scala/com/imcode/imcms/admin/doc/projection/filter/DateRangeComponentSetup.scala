package com.imcode
package imcms
package admin.doc.projection.filter

import java.util.{Calendar, Date}

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._

trait DateRangeComponentSetup { this: DateRangeComponent =>
  import DateRangeType._

  cbRangeType.addValueChangeHandler { _ =>
    Seq(dtFrom, dtTo).foreach(_.setEnabled(false))
    val now = new Date
    val calendar = Calendar.getInstance

    cbRangeType.selection match {
      case Undefined =>
        dtFrom.setValue(null)
        dtTo.setValue(null)

      case Custom =>
        Seq(dtFrom, dtTo).foreach { dt =>
          dt.setEnabled(true)
          dt.value = dt.valueOpt.getOrElse(now)
        }

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
  cbRangeType.selection = Undefined
}
