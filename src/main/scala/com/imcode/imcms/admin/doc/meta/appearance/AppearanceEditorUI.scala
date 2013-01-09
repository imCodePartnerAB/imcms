package com.imcode
package imcms
package admin.doc.meta.appearance

import com.imcode.imcms.api._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._


class AppearanceEditorUI extends VerticalLayout with Spacing with FullWidth {
  val pnlLanguages = new Panel("Languages") with FullWidth {
    val layout = new VerticalLayout with Spacing with Margin with FullWidth

    val lytI18nMetas = new VerticalLayout with Spacing with FullWidth
    val cbShowMode = new ComboBox("When language is disabled") with SingleSelect[Meta.DisabledLanguageShowSetting] with NoNullSelection

    private val lytShowMode = new FormLayout with FullWidth
    lytShowMode.addComponent(cbShowMode)

    layout.addComponents(lytI18nMetas, lytShowMode)
    setContent(layout)
  }

  val pnlLinkTarget = new Panel("Link action") with FullWidth {
    val layout = new FormLayout with Margin with FullWidth
    val cbTarget = new ComboBox("Show in") with SingleSelect[String] with NoNullSelection

    layout.addComponent(cbTarget)
    setContent(layout)
  }


  val pnlAlias = new Panel("Alias") with FullWidth {
    val layout = new HorizontalLayout with MiddleLeftAlignment with Margin with FullHeight
    val txtAlias = new TextField with FullWidth |>> {
      _.setInputPrompt("alternate page name")
    }

    val lblAlias = new Label with UndefinedSize {
      override def attach() {
        super.attach()
        getApplication.servletContext.getContextPath |> {
          case "" | "/" => "/"
          case path => s"$path/"
        } |> setCaption
      }
    }

    layout.addComponents(lblAlias, txtAlias)
    setContent(layout)
  }

  this.addComponents(pnlLanguages, pnlLinkTarget, pnlAlias)
}