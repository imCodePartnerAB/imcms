package com.imcode
package imcms.vaadin

import com.vaadin.terminal.{ThemeResource => TR}

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
    val Lock32: TR = "icons/32/lock.png"
    val Ok16: TR = "icons/16/ok.png"
    val Ok32: TR = "icons/32/ok.png"
    val Done16: TR = Ok16
    val Done32: TR = Ok32
    val File16: TR = "icons/16/document.png"
    val Folder16: TR = "icons/16/folder.png"

    object Browser {
      val TabHome32: TR = "icons/16/document-web.png"
      val TabImages32: TR = "icons/16/document-image.png"
      val TabTemplates32: TR = "icons/16/document-ppt.png"
      val TabConf32: TR = "icons/16/document-txt.png"
      val TabLogs32: TR = "icons/16/document.png"
    }
  }
}