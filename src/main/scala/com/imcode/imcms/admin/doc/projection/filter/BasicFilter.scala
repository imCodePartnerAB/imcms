package com.imcode
package imcms
package admin.doc.projection.filter

import scala.collection.JavaConverters._

import com.vaadin.ui.{CheckBox, Label}

import _root_.imcode.server.document.DocumentTypeDomainObject
import scala.PartialFunction._

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.Theme
import com.imcode.imcms.api.I18nLanguage

class BasicFilter extends ImcmsServicesSupport {

  val ui: BasicFilterUI = new BasicFilterUI |>> { ui =>
    ui.chkIdRange.addValueChangeHandler {
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.range", ui.chkIdRange, ui.lytIdRange,
        new Label("%s - %s".format(Option(ui.lytIdRange.txtStart.getInputPrompt).getOrElse(""), Option(ui.lytIdRange.txtEnd.getInputPrompt).getOrElse(""))))
    }

    ui.chkText.addValueChangeHandler {
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.text", ui.chkText, ui.txtText)
    }

    ui.chkType.addValueChangeHandler {
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.types", ui.chkType, ui.lytTypes)
    }

    ui.chkPhase.addValueChangeHandler  {
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.phases", ui.chkPhase, ui.lytPhases)
    }

    ui.chkLanguage.addValueChangeHandler  {
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.languages", ui.chkLanguage, ui.lytLanguages)
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
    ui.chkLanguage.checked = true
    doto(ui.chkIdRange, ui.chkText, ui.chkType, ui.chkPhase, ui.chkAdvanced) {
      //_.fireValueChange(true)
      _.check()
    }

    doto(ui.lytPhases.chkNew, ui.lytPhases.chkPublished, ui.lytPhases.chkUnpublished, ui.lytPhases.chkApproved, ui.lytPhases.chkDisapproved, ui.lytPhases.chkArchived) {
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

    ui.lytTypes.chkText.checked = values.docType.map(_(DocumentTypeDomainObject.TEXT)).getOrElse(false)
    ui.lytTypes.chkFile.checked = values.docType.map(_(DocumentTypeDomainObject.FILE)).getOrElse(false)
    ui.lytTypes.chkHtml.checked = values.docType.map(_(DocumentTypeDomainObject.HTML)).getOrElse(false)

    // todo: DEMO, replace with real values when spec is complete
    ui.lytAdvanced.cbTypes.removeAllItems()
    Seq("docs_projection.basic_filter.cb_advanced_type.custom", "docs_projection.basic_filter.cb_advanced_type.last_xxx", "docs_projection.basic_filter.cb_advanced_type.last_zzz").foreach(itemId => ui.lytAdvanced.cbTypes.addItem(itemId, itemId.i))
    ui.lytAdvanced.cbTypes.value = values.advanced.getOrElse("docs_projection.basic_filter.cb_advanced_type.custom")

    ui.lytLanguages.removeAllComponents()
    for (language <- imcmsServices.getI18nSupport.getLanguages.asScala) {
      val chkLanguage = new CheckBox(language.getNativeName) with GenericData[I18nLanguage] |>> { chk =>
        chk.setIcon(Theme.Icon.Language.flag(language))
        chk.data = language
        chk.checked = language |> imcmsServices.getI18nSupport.isDefault
      }

      ui.lytLanguages.addComponent(chkLanguage)
    }
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
        whenOpt(ui.lytTypes.chkText.isChecked) { DocumentTypeDomainObject.TEXT },
        whenOpt(ui.lytTypes.chkFile.isChecked) { DocumentTypeDomainObject.FILE },
        whenOpt(ui.lytTypes.chkHtml.isChecked) { DocumentTypeDomainObject.HTML },
        whenOpt(ui.lytTypes.chkUrl.isChecked) { DocumentTypeDomainObject.URL }
      ).flatten
    },

    advanced = whenOpt(ui.chkAdvanced.isChecked)(ui.lytAdvanced.cbTypes.value)
  )
}

