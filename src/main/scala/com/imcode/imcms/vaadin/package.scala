package com.imcode
package imcms

import com.vaadin.ui.UI
import com.vaadin.server.UserError

package object vaadin {

  implicit def stringToUserError(string: String): UserError = new UserError(string)

  implicit def uiToImcmsUI(ui: UI): ImcmsUI = ui.asInstanceOf[ImcmsUI]

  implicit def wrapUI(ui: UI) = new UIWrapper(ui)
}