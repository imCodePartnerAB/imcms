package com.imcode

import scala.util.control.{Exception => Ex}
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
  // Zero based index
  type Ix = JInteger

  implicit def stringAsI18nMessageKey(string: String) = new I18nMessage(string)
}