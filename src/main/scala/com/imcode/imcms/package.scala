package com.imcode

import java.text.MessageFormat
import imcode.server.{ImcmsServices, Imcms}
import org.apache.log4j.Logger
import java.util.{ListResourceBundle, Locale, ResourceBundle}

package object imcms {

  type UserId = JInteger
  type DocId = JInteger
  type DocTypeId = JInteger
  type DocVersionNo = JInteger
  type LanguageId = JInteger
  type Keyword = String
  type CategoryId = JInteger
  type MenuItemId = String
  type FileId = String

  type ErrorMsg = String



  class ResourceBundleValue(key: String) {
    private val localeAndValue: (Locale, String) = {
      val locale = Imcms.getUser |> option match {
        case Some(user) => new Locale(user.getLanguageIso639_2)
        case _ => Locale.getDefault
      }

      val bundle = try {
        ResourceBundle.getBundle("ui", locale)
      } catch {
        case e =>
          Logger.getLogger(getClass).error("Can't retrieve resource bundle for locale %s" format locale.getDisplayName, e)
          new ListResourceBundle {
            def getContents = Array(Array())
          }
      }

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