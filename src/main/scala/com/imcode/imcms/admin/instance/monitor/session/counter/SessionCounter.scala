package com.imcode
package imcms.admin.instance.monitor.session.counter

import imcode.server.Imcms

case class SessionCounter(value: Int, date: java.util.Date)

object SessionCounter {
  val services = Imcms.getServices
  def get() = new SessionCounter(services.getSessionCounter, services.getSessionCounterDate)
  def save(sc: SessionCounter) {
    services.setSessionCounter(sc.value)
    services.setSessionCounterDate(sc.date)
  }
}
