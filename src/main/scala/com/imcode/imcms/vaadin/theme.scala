package com.imcode
package imcms.vaadin

import com.vaadin.terminal.{ThemeResource => TR}

object Theme {
  implicit val stringToThemeResource = new TR(_:String)

  object Icons {
    val About16 = "icons/16/globe.png" : TR
    val Help16 = "icons/16/help.png" : TR
    val New16 = "icons/16/document-add.png" : TR
    val Edit16 = "icons/16/settings.png" : TR
    val Delete16 = "icons/16/document-delete.png" : TR
    val Reload16 = "icons/16/reload.png" : TR
    val EditContent16 = "icons/16/document-txt.png" : TR
    val Documents16 = "icons/16/note.png" : TR
    val Tab32 = "icons/32/folder.png" : TR
    val Lock32 = "icons/32/lock.png" : TR
    val Ok16 = "icons/16/ok.png" : TR
    val Ok32 = "icons/32/ok.png" : TR
    val Done16 = Ok16
    val Done32 = Ok32
    val File16 = "icons/16/document.png" : TR
    val Folder16 = "icons/16/folder.png" : TR
  }
}