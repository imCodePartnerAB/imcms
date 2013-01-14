package com.imcode
package imcms.admin.doc

import com.imcode.imcms._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.vaadin.ui._
import com.vaadin.server.ExternalResource

object DocViewer extends ImcmsServicesSupport {
  // todo: ??? loading indicator
  // todo: ??? handle document can not be found
  // todo: ??? show languages + disabled/enabled/available
  // todo: ??? show versions
  def showDocViewDialog(ui: Component, docId: DocId) {
    val docUrl = UI.getCurrent.imcmsDocUrl(docId)

    new OKDialog("Document") with CustomSizeDialog with BottomContentMarginDialog /*with Resizable*/ |>> { dlg =>
      dlg.mainUI = new VerticalLayout with FullSize |>> { lyt =>
        val mb = new MenuBar
        val mi = mb.addItem("Menu")
        1 to 10 foreach { mi addItem _.toString }

        val emb = new Embedded with FullSize |>> { browser =>
          browser.setType(Embedded.TYPE_BROWSER)
          browser.setSource(new ExternalResource(docUrl))

          browser.setEnabled(false)
        }

        lyt.addComponents(mb, emb)
        lyt.setExpandRatio(emb, 1.0f)
      }

      dlg.setSize(600, 600)
      dlg.setOkButtonHandler({})

    } |> UI.getCurrent.addWindow
  }

  // def openDoc(ui: Component, doc: DocumentDomainObject) {}
  // def openDoc(ui: Component, docId: DocId) {} // document can not be found
}