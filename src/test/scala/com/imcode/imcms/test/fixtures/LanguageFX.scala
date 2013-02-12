package com.imcode
package imcms.test
package fixtures

import scala.collection.JavaConverters._
import com.imcode.imcms.api.{ContentLanguage, I18nContentSupport}

object LanguageFX {
  val HostNameEn = "imcode.com"
  val HostNameSe = "imcode.se"

  def mkEnglish: ContentLanguage = ContentLanguage.builder().id(1).code("en").name("English").nativeName("English").enabled(true).build
  def mkSwedish: ContentLanguage = ContentLanguage.builder().id(2).code("sv").name("Swedish").nativeName("Svenska").enabled(true).build

  def mkLanguages: Seq[ContentLanguage] = Seq(mkEnglish, mkSwedish)

  def mkI18nSupport(defaultLanguage: ContentLanguage = mkEnglish): I18nContentSupport = new I18nContentSupport(
    mkLanguages.map(l => l.getCode -> l).toMap.asJava,
    Map(HostNameEn -> mkEnglish, HostNameSe -> mkSwedish).asJava,
    defaultLanguage
  )
}
