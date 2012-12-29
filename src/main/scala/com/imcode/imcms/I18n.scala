package com.imcode.imcms

import java.util.{ListResourceBundle, ResourceBundle, Locale}
import org.apache.log4j.Logger
import java.text.MessageFormat
import scala.util.control.{Exception => Ex}
import _root_.imcode.server.Imcms


object I18n {
  def i(key: String): String = key.i
  def f(arg: Any, args: Any*)(key: String): String = key.f(arg, args)
}


// implicit
class I18n(key: String) {
  private val localeAndValue: (Locale, String) = {
    val locale = Imcms.getUser |> opt match {
      case Some(user) => new Locale(user.getLanguageIso639_2)
      case _ => Locale.getDefault
    }

    val bundle = try {
      ResourceBundle.getBundle("ui", locale)
    } catch {
      case e =>
        Logger.getLogger(getClass).error("Can't retrieve resource bundle for locale %s".format(locale.getDisplayName), e)
        new ListResourceBundle {
          def getContents = Array(Array())
        }
    }

    (locale, Ex.allCatch.opt(bundle.getString(key)).getOrElse("<#%s#>".format(key.split('.').last)))
  }


  /**
   * @return resource bundle i18n value corresponding to the key
   */
  def i: String = localeAndValue._2


  /**
   * @param arg first format arg
   * @param args rest param args
   * @return formatted resource bundle i18n value corresponding to the
   */
  def f(arg: Any, args: Any*): String = localeAndValue match {
    case (locale, value) => new MessageFormat(value, locale).format((arg +: args.toArray).map(_.asInstanceOf[AnyRef]))
  }
}
