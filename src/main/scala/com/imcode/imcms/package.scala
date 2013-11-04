package com.imcode

import scala.language.implicitConversions

package object imcms {

  type UserId = JInteger
  type DocId = JInteger
  type MetaId = JInteger
  type DocTypeId = JInteger
  type DocVersionNo = JInteger
  type LanguageId = JInteger
  type Keyword = String
  type CategoryId = JInteger
  type MenuItemId = String
  type FileId = String
  type ErrorMsg = String
  /** Zero based index */
  type Index = JInteger

  implicit def stringAsI18nMessageKey(string: String) = new I18nMessage(string)
}