package com.imcode
package imcms
package admin.doc.meta.profile


import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.admin.doc.meta.access.TextDocPermSetEditorView
import com.vaadin.ui._
import com.vaadin.ui.themes.Reindeer


class ProfileEditorView(
   defaultPermSetEditorView: TextDocPermSetEditorView,
   restrictedOnePermSetEditorView: TextDocPermSetEditorView,
   restrictedTwoPermSetEditorView: TextDocPermSetEditorView)
extends VerticalLayout with FullWidth {

  val cbDefaultTemplate = new ComboBox("Template") with SingleSelect[String] with NoNullSelection // ??? NullSelection ???
  val cbRestrictedOneDefaultTemplate = new ComboBox("Template") with SingleSelect[String] with NullSelection
  val cbRestrictedTwoDefaultTemplate = new ComboBox("Template") with SingleSelect[String] with NullSelection

  defaultPermSetEditorView.setCaption("Permissions")
  restrictedOnePermSetEditorView.setCaption("Permissions")
  restrictedTwoPermSetEditorView.setCaption("Permissions")

  private val pnlSettings = new Panel("Settings") with FullWidth {
    val content = new VerticalLayout with FullWidth with Margin

    val tsSettings = new TabSheet |>> { _.addStyleName(Reindeer.TABSHEET_MINIMAL) }
    val lytDefault = new FormLayout with Margin
    val lytRestrictedOne = new FormLayout with Margin
    val lytRestrictedTwo = new FormLayout with Margin

    tsSettings.addTab(lytDefault, "Default")
    tsSettings.addTab(lytRestrictedOne, "Custom-One")
    tsSettings.addTab(lytRestrictedTwo, "Custom-Two")

    lytDefault.addComponents(cbDefaultTemplate, defaultPermSetEditorView)
    lytRestrictedOne.addComponents(cbRestrictedOneDefaultTemplate, restrictedOnePermSetEditorView)
    lytRestrictedTwo.addComponents(cbRestrictedTwoDefaultTemplate, restrictedTwoPermSetEditorView)

    content.addComponent(tsSettings)

    setContent(content)
  }

  this.addComponents(pnlSettings)
}