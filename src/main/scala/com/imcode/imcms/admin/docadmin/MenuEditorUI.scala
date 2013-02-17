package com.imcode
package imcms
package admin.docadmin

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.admin.doc.projection.DocIdSelectWithLifeCycleIcon
import imcode.server.document.textdocument.MenuDomainObject

class MenuEditorUI extends VerticalLayout with FullSize {
  val mb = new MenuBar with FullWidth
  val miIncludeDocs = mb.addItem("Add")
  val miExcludeSelectedDoc = mb.addItem("Remove")
  val miShowSelectedDoc = mb.addItem("Show")
  val miEditSelectedDoc = mb.addItem("Properties")
  val miHelp = mb.addItem("Help")
  val ttMenu = new TreeTable with AlwaysFireValueChange[AnyRef] with DocIdSelectWithLifeCycleIcon with SingleSelect[DocId]
                             with Selectable with Immediate with FullSize |>> { tt =>
    tt.setRowHeaderMode(Table.RowHeaderMode.HIDDEN)
  }

  val cbSortOrder = new ComboBox("Sort order") with AlwaysFireValueChange[AnyRef] with SingleSelect[JInteger] with Immediate with NoNullSelection |>> { cb =>
    Seq(
      MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE -> "Title",
      MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED -> "Modified date/time",
      MenuDomainObject.MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED -> "Published date/time",
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED -> "Manual",
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER -> "Manual - multilevel"
    ).foreach {
      case (id, caption) => cb.addItem(id: JInteger, caption)
    }
  }

  private val lytSort = new FormLayout |>> { lyt =>
    lyt.addComponent(cbSortOrder)
  }

  this.addComponents(mb, lytSort, ttMenu)
  setExpandRatio(ttMenu, 1f)
}
