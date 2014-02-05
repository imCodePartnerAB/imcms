package com.imcode
package imcms
package admin.doc.meta.appearance

import com.imcode.imcms.mapping.orm.DocLanguage
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

class I18nMetaEditorView(val language: DocLanguage, caption: String) extends CustomLayout("admin/doc/meta/appearance/i18n_meta") with FullWidth {
  private val lytContent = new FormLayout

  val chkEnabled = new CheckBox(caption) |>> { chk =>
    chk.setIcon(Theme.Icon.Language.flag(language))
  }

  val txtTitle = new TextField("Title") with FullWidth
  val txaMenuText = new TextArea("Menu text") with FullWidth {
    setRows(3)
  }

  val embLinkImage = new TextField("Link image") with FullWidth

  lytContent.addComponents(txtTitle, txaMenuText, embLinkImage)

  this.addNamedComponents("legend" -> chkEnabled, "content" -> lytContent)
}
