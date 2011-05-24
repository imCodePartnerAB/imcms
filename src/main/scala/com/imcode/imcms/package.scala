package com.imcode

import java.util.{Locale, ResourceBundle}
import java.text.MessageFormat
import imcode.server.{ImcmsServices, Imcms}

package object imcms {

  type DocId = JInteger
  type UserId = JInteger
  type LanguageId = JInteger

  implicit def stringAsBundleResourceId(string: String) = new {

    private def getLocale() = ?(Imcms.getUser) match {
      case Some(user) => new Locale(user.getLanguageIso639_2)
      case _ => Locale.getDefault
    }

    private def localize() = {
      val locale = getLocale()
      val bundle = ResourceBundle.getBundle("ui", locale)
      (EX.allCatch.opt(bundle.getString(string)) getOrElse "<#%s#>".format(string.split('.').last), locale)
    }


    def i = localize()._1

    def f(arg: Any, args: Any*) = localize() match {
      case (message, locale) => new MessageFormat(message, locale).format((arg +: args toArray).map(_.asInstanceOf[AnyRef]))
    }
  }

  trait ImcmsServicesSupport {
    def imcmsServices(implicit implicitImcmsServices: ImcmsServices = Imcms.getServices) = implicitImcmsServices
  }
}