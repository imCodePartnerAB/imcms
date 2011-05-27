package com.imcode
package imcms
package admin.doc

import com.imcode.imcms.vaadin._

import scala.collection.JavaConversions._
import vaadin.{ImcmsApplication, FullSize}
import com.vaadin.event.Action
import com.vaadin.ui._


object Actions {
  val IncludeToSelection = new Action("doc.action.include_to_selection".i)
  val ExcludeFromSelection = new Action("doc.action.exclude_from_selection".i)
  val Edit = new Action("doc.action.edit".i)
  //val View = new Action("doc.action.view".i)
  val Delete = new Action("doc.action.delete".i)
}


class DocManager(app: ImcmsApplication) {
  val customDocs = new CustomDocs
  val search = new DocSearch(new AllDocsContainer)

  val docSelectionDlg = letret(new OKDialog("doc.dlg_selection.caption".i) with CustomSizeDialog) { dlg =>
    dlg.mainUI = customDocs.ui
    dlg.setSize(500, 500)
  }

  val ui = letret(new DocManagerUI(search.ui)) { ui =>
    ui.miShowSelection.setCommandHandler {
      app.show(docSelectionDlg, modal = false, resizable = true)
    }

    search.docsUI.addActionHandler(new Action.Handler {
      import Actions._

      def getActions(target: AnyRef, sender: AnyRef) = Array(IncludeToSelection, Edit, Delete)

      def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
        action match {
          case IncludeToSelection =>
            customDocs.search.docsContainer.addItem(target)
            customDocs.search.update()
            customDocs.search.search()
          case _ =>
        }
    })
  }
}

class DocManagerUI(searchUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.mgr.mi.doc".i)
  val miDocNew = miDoc.addItem("doc.action.new".i)
  val miDocEdit = miDoc.addItem("doc.action.edit".i)
  val miDocDelete = miDoc.addItem("doc.action.delete".i)
  val miView = mb.addItem("doc.mgr.mi.view".i)
  val miShowSelection = miView.addItem("doc.action.show_selection".i)

  addComponents(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}



class CustomDocs {
  val search = new DocSearch(new CustomDocsContainer)
  val ui = new CustomDocsUI(search.ui)

  search.docsUI.addActionHandler(new Action.Handler {
    import Actions._

    def getActions(target: AnyRef, sender: AnyRef) = Array(ExcludeFromSelection, Edit, Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case ExcludeFromSelection => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}


class CustomDocsUI(searchUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.selection.mi.doc".i)

  addComponents(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}