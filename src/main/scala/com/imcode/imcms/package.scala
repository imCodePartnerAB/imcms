package com.imcode

import java.util.{Locale, ResourceBundle}
import java.text.MessageFormat
import imcode.server.{ImcmsServices, Imcms}

package object imcms {

  type UserId = JInteger
  type DocId = JInteger
  type DocTypeId = JInteger
  type DocVersionNo = JInteger
  type LanguageId = JInteger
  type Keyword = String
  type CategoryId = JInteger
  type MenuItemId = String


  class ResourceBundleValue(key: String) {
    private val localeAndValue: (Locale, String) = {
      val locale = ?(Imcms.getUser) match {
        case Some(user) => new Locale(user.getLanguageIso639_2)
        case _ => Locale.getDefault
      }

      val bundle = ResourceBundle.getBundle("ui", locale)
      (locale, EX.allCatch.opt(bundle.getString(key)) getOrElse "<#%s#>".format(key.split('.').last))
    }


    /** @return resource bundle i18n value corresponding to the key */
    def i = localeAndValue._2


    /**
     * @param arg first format arg
     * @param @args rest param args
     * @return formatted resource bundle i18n value corresponding to the
     */
    def f(arg: Any, args: Any*) = localeAndValue match {
      case (locale, value) => new MessageFormat(value, locale).format((arg +: args.toArray).map(_.asInstanceOf[AnyRef]))
    }
  }


  implicit def stringToResourceBundleValue(string: String) = new ResourceBundleValue(string)


  trait ImcmsServicesSupport {
    def imcmsServices(implicit implicitImcmsServices: ImcmsServices = Imcms.getServices) = implicitImcmsServices
  }
}