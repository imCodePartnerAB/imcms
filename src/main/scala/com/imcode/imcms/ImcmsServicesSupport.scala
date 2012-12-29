package com.imcode.imcms

import imcode.server.{Imcms, ImcmsServices}

trait ImcmsServicesSupport {
  def imcmsServices(implicit implicitImcmsServices: ImcmsServices = Imcms.getServices) = implicitImcmsServices
}
