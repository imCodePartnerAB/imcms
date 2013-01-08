package com.imcode
package imcms

import java.util.{ListResourceBundle, ResourceBundle, Locale}
import org.apache.log4j.Logger
import java.text.MessageFormat
import scala.util.control.{Exception => Ex}
import _root_.imcode.server.Imcms


object I18nResource {
  def i(key: String): String = key.i
  def f(arg: Any, args: Any*)(key: String): String = key.f(arg, args)
}


// implicit
class I18nResource(key: String) {
  private val localeAndResource: (Locale, String) = {
    val locale = Imcms.getUser |> opt match {
      case Some(user) => new Locale(user.getLanguageIso639_2)
      case _ => Locale.getDefault
    }

    val bundle = try {
      ResourceBundle.getBundle("ui", locale)
    } catch {
      case e: Throwable =>
        Logger.getLogger(getClass).error(s"Can't retrieve resource bundle for locale ${locale.getDisplayName}.", e)
        new ListResourceBundle {
          def getContents = Array(Array())
        }
    }

    (locale, Ex.allCatch.opt(bundle.getString(key)).getOrElse(s"<#${key.split('.').last}#>"))
  }


  /**
   * @return resource bundle value corresponding to the key
   */
  def i: String = localeAndResource._2


  /**
   * @param arg first format arg
   * @param args rest param args
   * @return formatted resource value corresponding to the key
   */
  def f(arg: Any, args: Any*): String = localeAndResource match {
    case (locale, value) => new MessageFormat(value, locale).format((arg +: args.toArray).map(_.asInstanceOf[AnyRef]))
  }
}
