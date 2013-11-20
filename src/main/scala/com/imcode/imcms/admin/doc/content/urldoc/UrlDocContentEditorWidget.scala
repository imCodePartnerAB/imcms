package com.imcode
package imcms
package admin.doc.content.urldoc

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._


class UrlDocContentEditorWidget extends Panel("URL/Link".i) with FullSize {
  private val content = new FormLayout with Margin with FullSize
  private val lytTarget = new HorizontalLayout with Spacing with FullWidth |>> { _.setCaption("Open In") }

  val txtURL = new TextField("URL/Link".i) with FullWidth
  val cbTarget = new ComboBox with SingleSelect[String] with NoTextInput with NoNullSelection with Immediate
  val txtCustomTarget = new TextField with FullWidth |>> { _.setInputPrompt("not specified") }
  setContent(content)

  lytTarget.addComponents(cbTarget, txtCustomTarget)
  content.addComponents(txtURL, lytTarget)

  lytTarget.setExpandRatio(txtCustomTarget, 1f)

  cbTarget.addItem("_self", "Same Frame") // _self install/htdocs/sv/jsp/docadmin/url_document.jsp/1003
  cbTarget.addItem("_blank", "New Window") // _blank install/htdocs/sv/jsp/docadmin/url_document.jsp/1004
  cbTarget.addItem("_top", "Replace All") // _top install/htdocs/sv/jsp/docadmin/url_document.jsp/1005
  cbTarget.addItem("_custom", "Other Frame") // ??? install/htdocs/sv/jsp/docadmin/url_document.jsp/1006

  cbTarget.addValueChangeHandler { _ =>
    cbTarget.value match {
      case "_custom" =>
        txtCustomTarget.setEnabled(true)
        txtCustomTarget.value = ""

      case itemId =>
        txtCustomTarget.setEnabled(false)
        txtCustomTarget.value = itemId
    }
  }

  cbTarget.select("_self")
}
