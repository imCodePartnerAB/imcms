package com.imcode
package imcms.test
package fixtures

import com.imcode.imcms.api.DocumentLanguage
import scala.collection.JavaConverters._
import com.imcode.imcms.api.DocumentLanguageSupport

object LanguageFX {
  val HostNameEn = "imcode.com"
  val HostNameSe = "imcode.se"

  def mkEnglish: DocumentLanguage = DocumentLanguage.builder().id(1).code("en").name("English").nativeName("English").enabled(true).build
  def mkSwedish: DocumentLanguage = DocumentLanguage.builder().id(2).code("sv").name("Swedish").nativeName("Svenska").enabled(true).build

  def mkLanguages: Seq[DocumentLanguage] = Seq(mkEnglish, mkSwedish)

  def mkI18nSupport(defaultLanguage: DocumentLanguage = mkEnglish): DocumentLanguageSupport = new DocumentLanguageSupport(
    mkLanguages.map(l => l.getCode -> l).toMap.asJava,
    Map(HostNameEn -> mkEnglish, HostNameSe -> mkSwedish).asJava,
    defaultLanguage
  )
}
