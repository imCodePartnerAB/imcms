package com.imcode
package imcms
package admin
package uber

import com.imcode.imcms.vaadin.Current
import com.vaadin.server.ExternalResource
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui.MenuBar
import com.vaadin.ui.MenuBar.Command

// fixme: change lang should update user
// fixme: change password should update user
class UberAdminManager {

  val view = new UberAdminManagerView |>> { v =>
    v.imgSplash.setIcon(new ExternalResource(Current.contextPath + "/images/imCMSpower.gif"))

    v.miLogOut.setCommandHandler { _ =>
      Current.page.setLocation(Current.contextPath + "/servlet/LogOut")
    }

    v.miRestart.setCommandHandler { _ =>
      Current.page.setLocation(Current.page.getLocation)
      Current.httpSession.invalidate()
    }

    v.miChangePassword.setEnabled(false)
    v.miLanguage.setIcon(Theme.Icon.Language.flag(Current.imcmsUser.getLanguageIso639_2))
    v.miLanguageEng.setEnabled(Current.imcmsUser.getLanguageIso639_2 != "eng")
    v.miLanguageSwe.setEnabled(Current.imcmsUser.getLanguageIso639_2 != "swe")

    val cmdChangeLanguage = new Command {
      override def menuSelected(selectedItem: MenuBar#MenuItem) {
        Current.imcmsUser.setLanguageIso639_2(if (selectedItem eq v.miLanguageEng) "eng" else "swe")
        Current.page.setLocation(Current.page.getLocation)
        Current.ui.close()
      }
    }
    
    Seq(v.miLanguageEng, v.miLanguageSwe).foreach(_.setCommand(cmdChangeLanguage))
  }
}
