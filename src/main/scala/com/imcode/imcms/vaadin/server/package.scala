package com.imcode.imcms.vaadin

import scala.language.implicitConversions

import com.vaadin.server.{UserError, Page}

package object server {

  implicit def wrapPage(page: Page) = new PageWrapper(page)

  implicit def stringToUserError(string: String): UserError = new UserError(string)
}
