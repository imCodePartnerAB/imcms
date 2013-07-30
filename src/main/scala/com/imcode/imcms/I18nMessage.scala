package com.imcode
package imcms

import java.util.{ListResourceBundle, ResourceBundle, Locale}
import org.apache.log4j.Logger
import java.text.MessageFormat
import scala.util.control.{Exception => Ex}
import _root_.imcode.server.Imcms
import scala.util.Try

/**
 * For use without implicit conversion.
 * <p>Examples:
 * <code>
 *   "ok.button"     |> I18nMessage.i
 *   "edit.doc.text" |> I18nMessage.f(1001, 0)
 * </code>
 */
object I18nMessage {
  def i(key: String): String = key.i
  def f(arg: Any, args: Any*)(key: String): String = key.f(arg, args)
}


// implicit
class I18nMessage(key: String) {

  private case class Value(locale: Locale, text: String)

  private val value: Value = {
    val locale = Imcms.getUser.asOption match {
      case Some(user) => new Locale(user.getLanguageIso639_2)
      case _ => Locale.getDefault
    }

    val bundle = try {
      ResourceBundle.getBundle("admin_ui", locale)
    } catch {
      case e: Exception =>
        Logger.getLogger(getClass).error(s"Can't retrieve resource bundle for locale ${locale.getDisplayName}.", e)
        new ListResourceBundle {
          override def getContents: Array[Array[AnyRef]] = Array.ofDim(2)
        }
    }

    Value(
      locale,
      Try(bundle.getString(key)).getOrElse(s"<#${key.split('.').last}#>")
    )
  }


  /**
   * @return resource bundle value corresponding to the key
   */
  def i: String = value.text


  /**
   * @param arg first format arg
   * @param args rest param args
   * @return formatted resource value corresponding to the key
   */
  def f(arg: Any, args: Any*): String =
    new MessageFormat(value.text, value.locale).format((arg +: args.toArray).map(_.asInstanceOf[AnyRef]))
}
