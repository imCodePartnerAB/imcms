package com.imcode
package imcms
package admin.doc.meta.access

import imcode.server.document._
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

class TextDocPermSetEditorView extends VerticalLayout with UndefinedSize {

  private val content = new VerticalLayout with Spacing with Margin with UndefinedSize

  // Decoration; always checked and read only
  val chkViewContent = new CheckBox("View content") with Checked with ReadOnly
  val chkEditMeta = new CheckBox("Edit properties")
  val chkEditPermissions = new CheckBox("Edit permissions")

  val chkEditTexts = new CheckBox("Edit texts")
  val chkEditImages = new CheckBox("Edit images")
  val chkEditIncludes = new CheckBox("Edit includes")
  val chkEditMenus = new CheckBox("Edit menues")
  val chkEditTemplates = new CheckBox("Change templates")

  // item caption is a type name in a user language
  val tcsCreateDocsOfTypes = new TwinColSelect("Create documents of type") with MultiSelect[DocTypeId] { setRows(5) }
  val tcsUseTemplatesFromTemplateGroups = new TwinColSelect("Use templates from groups") with MultiSelect[TemplateGroupDomainObject] { setRows(5) }

  addComponent(content)
  setComponentAlignment(content, Alignment.MIDDLE_CENTER)
  content.addComponents(chkViewContent, chkEditMeta, chkEditPermissions, chkEditTexts, chkEditIncludes, chkEditMenus, chkEditTemplates, tcsCreateDocsOfTypes, tcsUseTemplatesFromTemplateGroups)
}
