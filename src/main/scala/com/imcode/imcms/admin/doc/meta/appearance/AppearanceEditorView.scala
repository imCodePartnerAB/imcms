package com.imcode
package imcms
package admin.doc.meta.appearance

import com.imcode.imcms.api._
import com.imcode.imcms.mapping.Meta
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.Current


class AppearanceEditorView extends TabSheet with TabSheetSmallStyle with FullSize {

  object languages {
    val cbShowMode = new ComboBox with SingleSelect[Meta.DisabledLanguageShowSetting] with FullWidth with NoNullSelection
    val lblShowMode = new Label("When language is disabled") with UndefinedSize
    val lytShowMode = new HorizontalLayout(lblShowMode, cbShowMode) with FullWidth with MiddleLeftAlignment with Spacing with Margin |>> { lyt =>
      lyt.setExpandRatio(cbShowMode, 1.0f)
    }

    val lytI18nMetas = new VerticalLayout with Spacing with FullWidth
    private val pnlI18nMetas = new Panel(lytI18nMetas) with FullSize with LightStyle

    val content = new VerticalLayout(lytShowMode, pnlI18nMetas) with FullSize |>> { lyt =>
      lyt.setExpandRatio(pnlI18nMetas, 1.0f)
    }
  }

  object linkTarget {
    val cbTarget = new ComboBox("Show in") with FullWidth with SingleSelect[String] with NoNullSelection
    val content = new FormLayout(cbTarget)
  }

  object alias {
    private val contextPathPrefix = Current.contextPath match {
      case "" | "/" => "/"
      case path => s"$path/"
    }

    val lblAlias = new Label(contextPathPrefix) with UndefinedSize
    val txtAlias = new TextField with FullWidth
    val content = new HorizontalLayout(lblAlias, txtAlias) with Margin with FullSize |>> { lyt =>
      lyt.setExpandRatio(txtAlias, 1.0f)
    }
  }

  addTab(languages.content, "Languages")
  addTab(linkTarget.content, "Link action")
  addTab(alias.content, "Alias")
}