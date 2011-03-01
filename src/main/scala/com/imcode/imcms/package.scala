package com.imcode

import imcode.server.Imcms
import java.util.{Locale, ResourceBundle}

package object imcms {

  type DocId = JInteger

  implicit def stringAsI18nResource(s: String) = new {
    def i = (for {
      user <- ?(Imcms.getUser)
      language = user.getLanguageIso639_2
      bundle = ResourceBundle.getBundle("ui", new Locale(language))
      value <- EX.allCatch.opt(bundle.getString(s))
    } yield value) getOrElse "<#%s#>".format(s)
  }
}