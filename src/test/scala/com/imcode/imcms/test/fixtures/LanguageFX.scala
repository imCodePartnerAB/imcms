package com.imcode
package imcms.test
package fixtures

import com.imcode.imcms.api.DocumentLanguage
import scala.collection.JavaConverters._
import com.imcode.imcms.api.DocumentLanguages

object LanguageFX {
  val HostNameEn = "imcode.com"
  val HostNameSe = "imcode.se"

  def mkEnglish: DocumentLanguage = DocumentLanguage.builder().code("en").name("English").nativeName("English").build
  def mkSwedish: DocumentLanguage = DocumentLanguage.builder().code("sv").name("Swedish").nativeName("Svenska").build

  def mkLanguages: Seq[DocumentLanguage] = Seq(mkEnglish, mkSwedish)

  def mkI18nSupport(defaultLanguage: DocumentLanguage): DocumentLanguages =
    new DocumentLanguages(
      mkLanguages.asJava,
      Map(HostNameEn -> mkEnglish, HostNameSe -> mkSwedish).asJava,
      defaultLanguage
    )

  def mkI18nSupport(): DocumentLanguages = mkI18nSupport(mkEnglish)
}
