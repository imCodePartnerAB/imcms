package com.imcode
package imcms.vaadin.component

import com.imcode.imcms.api.DocumentLanguage
import com.vaadin.server.{ThemeResource => TR}
import imcode.server.document.{LifeCyclePhase, DocumentDomainObject}

// todo: refactor, change package ???
object Theme {
  implicit val stringToThemeResource = new TR(_:String)

  object Icon {
    val About16: TR = "icons/16/globe.png"
    val Help16: TR = "icons/16/help.png"
    val New16: TR = "icons/16/document-add.png"
    val Edit16: TR = "icons/16/settings.png"
    val Delete16: TR = "icons/16/document-delete.png"
    val Reload16: TR = "icons/16/reload.png"
    val EditContent16: TR = "icons/16/document-txt.png"
    val Documents16: TR = "icons/16/note.png"
    val Tab32: TR = "icons/32/folder.png"
    val Tab16: TR = "icons/16/folder.png"
    val Lock32: TR = "icons/32/lock.png"
    val Ok16: TR = "icons/16/ok.png"
    val Ok32: TR = "icons/32/ok.png"
    val Done16: TR = Ok16
    val Done32: TR = Ok32
    val File16: TR = "icons/16/document.png"
    val Folder16: TR = "icons/16/folder.png"
    val TextFormatPlain16: TR = "icons/16/document-txt.png"
    val TextFormatHtml16: TR = "icons/16/document-web.png"
    val HistoryBack16: TR = "icons/16/arrow-left.png"
    val MoreOptions16: TR = "icons/16/arrow-down.png"
    val HistoryForward16: TR = "icons/16/arrow-right.png"

    object Browser {
      val TabHome32: TR = "icons/16/document-web.png"
      val TabImages32: TR = "icons/16/document-image.png"
      val TabTemplates32: TR = "icons/16/document-ppt.png"
      val TabConf32: TR = "icons/16/document-txt.png"
      val TabLogs32: TR = "icons/16/document.png"
    }

    object Doc {
      def phase(lifeCyclePhase: LifeCyclePhase): TR = s"icons/doc_status/${lifeCyclePhase}.gif"
      def phase(doc: DocumentDomainObject): TR = if (doc == null) null else phase(doc.getLifeCyclePhase)
    }

    object Language {
      def flag(language: DocumentLanguage): TR = flag(language.getCode)
      def flag(languageCode: String): TR = s"icons/language_flag/${languageCode}.gif"
    }
  }
}