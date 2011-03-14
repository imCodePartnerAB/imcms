package com.imcode
package imcms
package admin.document

import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}

import com.vaadin.ui.{MenuBar, Table, VerticalLayout}
import vaadin.{ImcmsApplication, FullSize}


class DocSelection(app: ImcmsApplication) {
  val ui = new DocSelectionUI
}


class DocSelectionUI extends VerticalLayout with FullSize {
  val tblDocs = DocTableUI()
  val mb = new MenuBar
  val miDoc = mb.addItem("Document")
  val miView = mb.addItem("Filter") // -> search in selection

  addComponents(this, mb, tblDocs)
  setExpandRatio(tblDocs, 1.0f)
}


object DocTableUI {
  def apply() = letret(new Table) { table =>
    addContainerProperties(table,
      CP[String]("doc.list.alias".i),
      CP[String]("doc.list.status".i),
      CP[JInteger]("doc.list.type".i),
      CP[String]("doc.list.admin".i),
      CP[String]("doc.list.ref".i),
      CP[String]("doc.list.children".i))
  }
}
