package com.imcode
package imcms
package admin.doc.meta.access

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

/**
 * Doc's restricted permission set
 */
class NonTextDocPermSetEditorView extends VerticalLayout with UndefinedSize {

  private val content = new VerticalLayout with Spacing with Margin with UndefinedSize

  // Decoration; always checked and read only
  val chkViewContent = new CheckBox("View content") with Checked with ReadOnly
  val chkEditContent = new CheckBox("Edit content")
  val chkEditMeta = new CheckBox("Edit properties")
  val chkEditPermissions = new CheckBox("Edit permissions")

  addComponent(content)
  setComponentAlignment(content, Alignment.MIDDLE_CENTER)
  content.addComponents(chkViewContent, chkEditContent, chkEditMeta, chkEditPermissions)
}