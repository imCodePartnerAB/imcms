package com.imcode
package imcms
package admin.docadmin.menu

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.admin.doc.projection.DocIdSelectWithLifeCycleIcon
import imcode.server.document.textdocument.MenuDomainObject
import com.imcode.imcms._
import com.imcode._

class MenuEditorUI extends VerticalLayout with FullSize {
  val mb = new MenuBar with FullWidth
  val miAddExistingDocs = mb.addItem("mi.add".i)
  val miNewDoc = mb.addItem("mi.new".i)
  val miNewDocNA = miNewDoc.addItem("Not implemented".i)
  val miCopySelectedDoc = mb.addItem("mi.copy".i)
  val miRemoveSelectedDocs = mb.addItem("mi.remove".i)
  val miEditSelectedDoc = mb.addItem("mi.edit".i)
  val miShowSelectedDoc = mb.addItem("mi.show_in_a_new_window".i)
  val miHelp = mb.addItem("mi.help".i)
  val ttMenu = new TreeTable with AlwaysFireValueChange[AnyRef] with DocIdSelectWithLifeCycleIcon with SingleSelect[MetaId]
                             with Selectable with Immediate with FullSize |>> { tt =>
    tt.setRowHeaderMode(Table.RowHeaderMode.HIDDEN)
  }

  val cbSortOrder = new ComboBox("menu_editor.cb_sort".i) with AlwaysFireValueChange[AnyRef] with SingleSelect[JInteger] with Immediate with NoNullSelection |>> { cb =>
    Seq(
      MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE -> "menu_editor.cb_sort.item.headline".i,
      MenuDomainObject.MENU_SORT_ORDER__BY_MODIFIED_DATETIME_REVERSED -> "menu_editor.cb_sort.item.modified_dt".i,
      MenuDomainObject.MENU_SORT_ORDER__BY_PUBLISHED_DATETIME_REVERSED -> "menu_editor.cb_sort.item.published_dt".i,
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_ORDER_REVERSED -> "menu_editor.cb_sort.item.manual".i,
      MenuDomainObject.MENU_SORT_ORDER__BY_MANUAL_TREE_ORDER -> "menu_editor.cb_sort.item.manual_tree".i
    ).foreach {
      case (id, caption) => cb.addItem(id, caption)
    }
  }

  private val lytSort = new FormLayout |>> { lyt =>
    lyt.addComponent(cbSortOrder)
  }

  this.addComponents(mb, lytSort, ttMenu)
  setExpandRatio(ttMenu, 1f)
}
