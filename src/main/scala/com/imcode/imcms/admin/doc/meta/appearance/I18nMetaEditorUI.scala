package com.imcode
package imcms
package admin.doc.meta.appearance

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.api.I18nLanguage
import com.imcode.imcms.vaadin.Theme

class I18nMetaEditorUI(val language: I18nLanguage, caption: String) extends CustomLayout("admin/doc/meta/appearance/i18n_meta") with FullWidth {
  private val lytContent = new FormLayout |>> { lyt =>
    lyt.setEnabled(false)
  }

  val chkEnabled = new CheckBox(caption) with Immediate with AlwaysFireValueChange[JBoolean] |>> { chk =>
    chk.setIcon(Theme.Icon.Language.flag(language))
    chk.addValueChangeHandler {
      lytContent.setEnabled(chk.booleanValue)
    }
  }

  val txtTitle = new TextField("Title") with FullWidth
  val txaMenuText = new TextArea("Menu text") with FullWidth {
    setRows(3)
  }

  val embLinkImage = new TextField("Link image") with FullWidth

  lytContent.addComponents(txtTitle, txaMenuText, embLinkImage)

  this.addNamedComponents("legend" -> chkEnabled, "content" -> lytContent)
}
