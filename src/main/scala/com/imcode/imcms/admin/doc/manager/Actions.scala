package com.imcode
package imcms
package admin.doc.manager

import com.vaadin.event.Action

object Actions {
  val IncludeToSelection = new Action("doc_mgr.action.include_to_selection".i)
  val ExcludeFromSelection = new Action("doc_mgr.action.exclude_from_selection".i)
  val Delete = new Action("doc_mgr.action.delete".i)
  val EditMeta = new Action("doc_mgr.action.edit_meta".i)
}
