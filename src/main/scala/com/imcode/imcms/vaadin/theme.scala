package com.imcode
package imcms.vaadin

import com.vaadin.terminal.{ThemeResource => TR}

object Theme {
  implicit val stringToThemeResource = new TR(_:String)

  object Icons {
    val Help16 = "icons/16/help.png" : TR
    val New16 = "icons/16/document-add.png" : TR
    val Edit16 = "icons/16/settings.png" : TR
    val Delete16 = "icons/16/document-delete.png" : TR
    val Reload16 = "icons/16/reload.png" : TR
    val EditContent16 = "icons/16/document-txt.png" : TR
    val Tab32 = "icons/32/folder.png" : TR
  }
}