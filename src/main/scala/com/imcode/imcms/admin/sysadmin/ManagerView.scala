package com.imcode
package imcms
package admin
package sysadmin

import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._

class ManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with MenuBarInTabStyle with FullWidth
  val miLanguage = mb.addItem("", Theme.Icon.Language.flag("eng"))
  val miLanguageEng = miLanguage.addItem("English", Theme.Icon.Language.flag("eng"))
  val miLanguageSwe = miLanguage.addItem("Svenska", Theme.Icon.Language.flag("swe"))
  val miChangePassword = mb.addItem("admin_mi.change_password".i)

  val miRestart = mb.addItem("admin_mi.restart".i)
  val miLogOut = mb.addItem("admin_mi.logout".i)
  val miHelp = mb.addItem("mi.help".i)
  val imgSplash = new Image()

  addComponents(mb, imgSplash)
  setExpandRatio(imgSplash, 1.0f)
  setComponentAlignment(imgSplash, Alignment.MIDDLE_CENTER)
}
