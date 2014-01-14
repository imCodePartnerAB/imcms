package com.imcode
package imcms
package admin.doc.meta.access

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

class DocPermSetsEditorView extends VerticalLayout with UndefinedSize with Spacing {
  val tsSets = new TabSheet with UndefinedSize
  val chkRestrictedOneIsMorePrivilegedThanRestrictedTwo = new CheckBox("Custom-One is more privileged that Custom-Two")

  this.addComponents(tsSets, chkRestrictedOneIsMorePrivilegedThanRestrictedTwo)
}