package com.imcode
package imcms
package admin.doc.meta.appearance

import com.imcode.imcms.mapping.DocumentMeta
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.Current


class AppearanceEditorView extends TabSheet with TabSheetSmallStyle with FullSize {

  object languages {
    val cbShowMode = new ComboBox with SingleSelect[DocumentMeta.DisabledLanguageShowMode] with FullWidth with NoNullSelection
    val lblShowMode = new Label("When language is disabled") with UndefinedSize
    val lytShowMode = new HorizontalLayout() with FullWidth with MiddleLeftAlignment with Spacing with Margin |>> { lyt =>
      lyt.addComponents(lblShowMode, cbShowMode)
      lyt.setExpandRatio(cbShowMode, 1.0f)
    }

    val lytI18nMetas = new VerticalLayout with Spacing with FullWidth
    private val pnlI18nMetas = new Panel(lytI18nMetas) with FullSize with LightStyle

    val content = new VerticalLayout(lytShowMode, pnlI18nMetas) with FullSize |>> { lyt =>
      lyt.setExpandRatio(pnlI18nMetas, 1.0f)
    }
  }

  object linkTarget {
    val lblTarget = new Label("Show in") with UndefinedSize
    val cbTarget = new ComboBox with FullWidth with SingleSelect[String] with NoNullSelection
    val content = new HorizontalLayout with MiddleLeftAlignment with Margin with Spacing with FullWidth |>> { lyt =>
      lyt.addComponents(lblTarget, cbTarget)
      lyt.setExpandRatio(cbTarget, 1.0f)
    }
  }

  object alias {
    private val contextPathPrefix = Current.contextPath match {
      case "" | "/" => "/ "
      case path => s"$path/ "
    }

    val lblAlias = new Label(s"${contextPathPrefix}", ContentMode.HTML) with UndefinedSize
    val txtAlias = new TextField with FullWidth
    val content = new HorizontalLayout with MiddleLeftAlignment with Margin with FullWidth |>> { lyt =>
      lyt.addComponents(lblAlias, txtAlias)
      lyt.setExpandRatio(txtAlias, 1.0f)
    }
  }

  addTab(languages.content, "Languages")
  addTab(linkTarget.content, "Link action")
  addTab(alias.content, "Alias")
}