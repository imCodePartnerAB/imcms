package com.imcode
package imcms
package admin.doc.projection.filter

import com.vaadin.ui.Label

import _root_.imcode.server.document.DocumentTypeDomainObject
import scala.PartialFunction._

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._

class BasicFilter {

  val ui: BasicFilterUI = new BasicFilterUI |>> { ui =>
    ui.chkIdRange.addValueChangeHandler {
      FilterFormUtil.toggle(ui, "docs_projection.basic_filter_lyt.range", ui.chkIdRange, ui.lytIdRange,
        new Label("%s - %s".format(Option(ui.lytIdRange.txtStart.getInputPrompt).getOrElse(""), Option(ui.lytIdRange.txtEnd.getInputPrompt).getOrElse(""))))
    }

    ui.chkText.addValueChangeHandler {
      FilterFormUtil.toggle(ui, "docs_projection.basic_filter_lyt.text", ui.chkText, ui.txtText)
    }

    ui.chkType.addValueChangeHandler {
      FilterFormUtil.toggle(ui, "docs_projection.basic_filter_lyt.type", ui.chkType, ui.lytType)
    }

    ui.chkStatus.addValueChangeHandler  {
      FilterFormUtil.toggle(ui, "docs_projection.basic_filter_lyt.status", ui.chkStatus, ui.lytStatus)
    }

    ui.chkAdvanced.addValueChangeHandler {
      ui.lytAdvanced.setEnabled(ui.chkAdvanced.isChecked)
    }
  }

  def setVisibleDocsRangeInputPrompt(range: Option[(DocId, DocId)]) {
    range.map { case (start, end) => (start.toString, end.toString) }.getOrElse ("", "") |> {
      case (start, end) =>
        ui.lytIdRange.txtStart.setInputPrompt(start)
        ui.lytIdRange.txtEnd.setInputPrompt(end)
    }
  }


  def reset(): Unit = setValues(BasicFilterValues())

  def setValues(values: BasicFilterValues) {
    ui.chkIdRange.checked = values.idRange.isDefined
    ui.chkText.checked = values.text.isDefined
    ui.chkType.checked = values.docType.isDefined
    ui.chkAdvanced.checked = values.advanced.isDefined
    doto(ui.chkIdRange, ui.chkText, ui.chkType, ui.chkStatus, ui.chkAdvanced) {
      //_.fireValueChange(true)
      _.uncheck()
    }

    doto(ui.lytStatus.chkNew, ui.lytStatus.chkPublished, ui.lytStatus.chkUnpublished, ui.lytStatus.chkApproved, ui.lytStatus.chkDisapproved, ui.lytStatus.chkExpired) {
      _.uncheck()
    }

    ui.txtText.value = values.text.getOrElse("")

    values.idRange.collect {
      case IdRange(start, end) => (start.map(_.toString).getOrElse(""), end.map(_.toString).getOrElse(""))
    } getOrElse ("", "") match {
      case (start, end) =>
        ui.lytIdRange.txtStart.value = start
        ui.lytIdRange.txtEnd.value = end
    }

    ui.lytType.chkText.checked = values.docType.map(_(DocumentTypeDomainObject.TEXT)).getOrElse(false)
    ui.lytType.chkFile.checked = values.docType.map(_(DocumentTypeDomainObject.FILE)).getOrElse(false)
    ui.lytType.chkHtml.checked = values.docType.map(_(DocumentTypeDomainObject.HTML)).getOrElse(false)

    // todo: DEMO, replace with real values when spec is complete
    ui.lytAdvanced.cbTypes.removeAllItems()
    Seq("docs_projection.basic_filter_lyt.cb_advanced_type.custom", "docs_projection.basic_filter_lyt.cb_advanced_type.last_xxx", "docs_projection.basic_filter_lyt.cb_advanced_type.last_zzz").foreach(itemId => ui.lytAdvanced.cbTypes.addItem(itemId, itemId.i))
    ui.lytAdvanced.cbTypes.value = values.advanced.getOrElse("docs_projection.basic_filter_lyt.cb_advanced_type.custom")
  }

  // todo: return Error Either State
  def getState() = BasicFilterValues(
    idRange = whenOpt(ui.chkIdRange.isChecked) {
      IdRange(
        condOpt(ui.lytIdRange.txtStart.trim) { case value if value.nonEmpty => value.toInt },
        condOpt(ui.lytIdRange.txtEnd.trim) { case value if value.nonEmpty => value.toInt }
      )
    },

    text = whenOpt(ui.chkText.isChecked)(ui.txtText.trim),

    docType = whenOpt(ui.chkType.isChecked) {
      Set(
        whenOpt(ui.lytType.chkText.isChecked) { DocumentTypeDomainObject.TEXT },
        whenOpt(ui.lytType.chkFile.isChecked) { DocumentTypeDomainObject.FILE },
        whenOpt(ui.lytType.chkHtml.isChecked) { DocumentTypeDomainObject.HTML }
      ).flatten
    },

    advanced = whenOpt(ui.chkAdvanced.isChecked)(ui.lytAdvanced.cbTypes.value)
  )
}

