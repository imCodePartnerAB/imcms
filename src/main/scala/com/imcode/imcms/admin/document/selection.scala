package com.imcode
package imcms
package admin.document

import scala.collection.JavaConversions._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}

import vaadin.{ImcmsApplication, FullSize}
import com.vaadin.ui.Table.CellStyleGenerator
import com.vaadin.ui._
import imcode.server.Imcms
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.vaadin.terminal.{ExternalResource, Resource}
import imcode.server.document.{LifeCyclePhase, DocumentDomainObject}
import com.vaadin.event.Action


class DocFilteredView {
  val basicFilter = new DocBasicFilter
  val advancedFilter = new DocAdvancedFilter
  val advancedFilterPanel = new Panel with Scrollable with UndefinedSize with FullHeight {
    setStyleName(Panel.STYLE_LIGHT)
    setContent(advancedFilter.ui)
  }

  val docTableUI = DocTableUI(true)

  val ui = letret(new GridLayout(1, 2) with Spacing with FullSize) { ui =>
    ui.addComponent(basicFilter.ui)
    ui.addComponent(docTableUI)
    ui.setRowExpandRatio(1, 1f)
  }

  basicFilter.ui.btnAdvanced.addClickHandler {
    val component = ui.getComponent(0, 1) match {
      case `docTableUI` => advancedFilterPanel
      case _ => docTableUI
    }

    ui.removeComponent(0, 1)
    ui.addComponent(component, 0, 1)
  }
}


/**
 * Custom docs collection.
 */
class DocSelection(app: ImcmsApplication) {
  val docFilteredView = new DocFilteredView
  val ui = new DocSelectionUI(docFilteredView.ui)

  docFilteredView.docTableUI.addActionHandler(new Action.Handler {
    import Actions._

    def getActions(target: AnyRef, sender: AnyRef) = Array(Exclude, View, Edit, Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case Exclude => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}


class DocSelectionUI(docViewUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("Document")
  val miView = mb.addItem("Filter") // -> search in selection

  addComponents(this, mb, docViewUI)
  setExpandRatio(docViewUI, 1.0f)
}


class DocBasicFilter {
  val ui = letret(new DocBasicFilterUI) { ui =>
    ui.lytButtons.btnClear.addClickHandler { reset() }

    ui.chkText.addClickHandler { ui.txtText setEnabled ui.chkText.booleanValue }

    ui.chkRange.addClickHandler { ui.lytRange setEnabled ui.chkRange.booleanValue }

    ui.chkStatus.addClickHandler { ui.lytStatus setEnabled ui.chkStatus.booleanValue }

    ui.chkAdvanced.addClickHandler { ui.btnAdvanced setEnabled ui.chkAdvanced.booleanValue }
  }

  reset()

  def reset() {
    ui.chkAdvanced.value = false
    ui.chkRange.value = false
    ui.chkStatus.value = false
    ui.chkText.value = true
  }
}

class DocBasicFilterUI extends CustomLayout("admin/doc/filter/basic") {
  setWidth("700px")

  // prompt - real numbers: lower, upper
  val chkRange = new CheckBox("doc.filter.basic.frm.lbl.range".i) with Immediate with UndefinedSize
  val lytRange = new HorizontalLayout with Spacing with UndefinedSize {
    val txtFrom = new TextField { setInputPrompt("doc.filter.basic.frm.txt.range.from.prompt".i); setColumns(5) }
    val txtTo = new TextField { setInputPrompt("doc.filter.basic.frm.txt.range.to.prompt".i); setColumns(5) }

    addComponents(this, txtFrom, txtTo)
  }
  // prompt - any text
  val chkText = new CheckBox("doc.filter.basic.frm.lbl.text".i) with Immediate with UndefinedSize
  val txtText = new TextField { setInputPrompt("doc.filter.basic.frm.txt.text.prompt".i) }

  val chkStatus = new CheckBox("doc.filter.basic.frm.lbl.status".i) with Immediate with UndefinedSize
  val lytStatus = new HorizontalLayout with Spacing with UndefinedSize {
    val chkNew = new CheckBox("doc.filter.basic.frm.ckh.status.new".i)
    val chkPublished = new CheckBox("doc.filter.basic.frm.chk.status.published".i)
    val chkExpired = new CheckBox("doc.filter.basic.frm.chk.status.expired".i)

    addComponents(this, chkNew, chkPublished, chkExpired)
  }

  val chkAdvanced = new CheckBox("doc.filter.basic.frm.chk.advanced".i) with Immediate with UndefinedSize
  val btnAdvanced = new Button("doc.filter.basic.frm.btn.advanced".i) with LinkStyle


  val lytButtons = new HorizontalLayout with UndefinedSize with Spacing {
    val btnClear = new Button("doc.filter.basic.frm.btn.clear".i) { setStyleName("small") }
    val btnSearch = new Button("doc.filter.basic.frm.btn.search".i) { setStyleName("small") }

    addComponents(this, btnClear, btnSearch)
  }

  addNamedComponents(this,
    "doc.filter.basic.frm.chk.range" -> chkRange,
    "doc.filter.basic.frm.range" -> lytRange,
    "doc.filter.basic.frm.chk.text" -> chkText,
    "doc.filter.basic.frm.txt.text" -> txtText,
    "doc.filter.basic.frm.chk.status" -> chkStatus,
    "doc.filter.basic.frm.status" -> lytStatus,
    "doc.filter.basic.frm.chk.advanced" -> chkAdvanced,
    "doc.filter.basic.frm.btn.advanced" -> btnAdvanced,
    "doc.filter.basic.frm.buttons" -> lytButtons
  )
}


class DocAdvancedFilter {
  val ui = new DocAdvancedFilterUI
}

class DocAdvancedFilterUI extends CustomLayout("admin/doc/filter/advanced") {
  setWidth("700px")

  val lblPredefined = new Label("doc.filter.advanced.frm.lbl.predefined".i) with UndefinedSize
  val cbPredefined = new ComboBox

  val chkType = new CheckBox("doc.filter.advanced.frm.chk.type".i)
  val lytType = new HorizontalLayout with UndefinedSize with Spacing {
    val chkText = new CheckBox("doc.filter.advanced.frm.chk.type.text".i)
    val chkFile = new CheckBox("doc.filter.advanced.frm.chk.type.file".i)
    val chkHtml = new CheckBox("doc.filter.advanced.frm.chk.type.html".i)

    addComponents(this, chkText, chkFile, chkHtml)
  }

  val chkDates = new CheckBox("doc.filter.advanced.frm.chk.dates".i)
  val lytDates = new VerticalLayout with UndefinedSize with Spacing

  val chkCategories = new CheckBox("doc.filter.advanced.frm.chk.categories".i)
  val tcsCategories = new TwinColSelect

  val chkRelationship = new CheckBox("doc.filter.advanced.frm.chk.relationship".i)
  val lytRelationship = new HorizontalLayout with Spacing with UndefinedSize {
    val cbParents = new ComboBox("doc.filter.advanced.frm.chk.relationship.parents".i)
    val cbChildren = new ComboBox("doc.filter.advanced.frm.chk.relationship.children".i)

    cbParents.addItem("-not selected-")
    cbParents.addItem("With parents")
    cbParents.addItem("Without parents")

    cbChildren.addItem("-not selected-")
    cbChildren.addItem("With children")
    cbChildren.addItem("Without children")

    cbParents.setNullSelectionItemId("-not selected-")
    cbChildren.setNullSelectionItemId("-not selected-")

    addComponents(this, cbParents, cbChildren)
  }

  val chkMaintainers = new CheckBox("doc.filter.advanced.frm.chk.maintainers".i)
  val lytMaintainers = new HorizontalLayout with UndefinedSize {
    val lstCreators = new ListSelect("doc.filter.advanced.frm.chk.maintainers.creators".i)

    addComponents(this, lstCreators)
  }

  addNamedComponents(this,
    "doc.filter.advanced.frm.chk.type" -> chkType,
    "doc.filter.advanced.frm.lyt.type" -> lytType,
    "doc.filter.advanced.frm.lbl.predefined" -> lblPredefined,
    "doc.filter.advanced.frm.cb.predefined" -> cbPredefined,
    "doc.filter.advanced.frm.chk.dates" -> chkDates,
    "doc.filter.advanced.frm.lyt.dates" -> lytDates,
    "doc.filter.advanced.frm.chk.relationship" -> chkRelationship,
    "doc.filter.advanced.frm.lyt.relationship" -> lytRelationship,
    "doc.filter.advanced.frm.chk.categories" -> chkCategories,
    "doc.filter.advanced.frm.tcs.categories" -> tcsCategories,
    "doc.filter.advanced.frm.chk.maintainers" -> chkMaintainers,
    "doc.filter.advanced.frm.lyt.maintainers" -> lytMaintainers
  )
}
