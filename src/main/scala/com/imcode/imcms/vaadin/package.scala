package com.imcode
package imcms

import com.vaadin.Application
import com.vaadin.terminal.UserError

package object vaadin {

  implicit def stringToUserError(string: String): UserError = new UserError(string)

  implicit def applicationToImcmsApplication(app: Application): ImcmsApplication = app.asInstanceOf[ImcmsApplication]

  implicit def wrapApplication(app: Application) = new ApplicationWrapper(app)
}