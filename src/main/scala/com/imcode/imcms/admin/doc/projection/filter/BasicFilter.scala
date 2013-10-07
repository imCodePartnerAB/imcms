package com.imcode.imcms.admin.doc.projection.filter

import scala.collection.JavaConverters._

import com.vaadin.ui.{CheckBox, Label}

import imcode.server.document.{LifeCyclePhase, DocumentTypeDomainObject}
import scala.PartialFunction._

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.api.DocumentLanguage
import scala.util.Try

class BasicFilter extends ImcmsServicesSupport {

  val ui: BasicFilterUI = new BasicFilterUI |>> { ui =>
    ui.chkIdRange.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.range", ui.chkIdRange, ui.lytIdRange,
        new Label("%s - %s".format(ui.lytIdRange.txtStart.getInputPrompt.trimToEmpty, ui.lytIdRange.txtEnd.getInputPrompt.trimToEmpty))
      )
    }

    ui.chkText.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.text", ui.chkText, ui.txtText)
    }

    ui.chkType.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.types", ui.chkType, ui.lytTypes)
    }

    ui.chkPhase.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.phases", ui.chkPhase, ui.lytPhases)
    }

    ui.chkLanguage.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(ui, "docs_projection.basic_filter.languages", ui.chkLanguage, ui.lytLanguages)
    }

    ui.chkAdvanced.addValueChangeHandler { _ =>
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


  def reset(): Unit = setParameters(BasicFilterParameters())

  // todo: handle language differently - no lang selected, select default!!
  def setParameters(parameters: BasicFilterParameters) {
    Seq(ui.chkIdRange, ui.chkText, ui.chkType, ui.chkPhase, ui.chkAdvanced).foreach { chk =>
      chk.check()
    }

    Seq(ui.lytPhases.chkNew, ui.lytPhases.chkPublished, ui.lytPhases.chkUnpublished, ui.lytPhases.chkApproved,
        ui.lytPhases.chkDisapproved, ui.lytPhases.chkArchived).foreach { chk =>
      chk.uncheck()
    }

    ui.chkIdRange.checked = parameters.idRangeOpt.isDefined
    ui.chkText.checked = parameters.textOpt.isDefined
    ui.chkType.checked = parameters.docTypesOpt.isDefined
    ui.chkLanguage.checked = parameters.languagesOpt.isDefined
    ui.chkAdvanced.checked = parameters.advancedOpt.isDefined
    ui.chkPhase.checked = parameters.phasesOpt.isDefined

    ui.txtText.value = parameters.textOpt.getOrElse("")

    parameters.idRangeOpt.collect {
      case IdRange(start, end) => (start.map(_.toString).getOrElse(""), end.map(_.toString).getOrElse(""))
    } getOrElse("", "") match {
      case (start, end) =>
        ui.lytIdRange.txtStart.value = start
        ui.lytIdRange.txtEnd.value = end
    }

    ui.lytTypes.chkText.checked = parameters.docTypesOpt.exists(set => set.contains(DocumentTypeDomainObject.TEXT))
    ui.lytTypes.chkFile.checked = parameters.docTypesOpt.exists(set => set.contains(DocumentTypeDomainObject.FILE))
    ui.lytTypes.chkHtml.checked = parameters.docTypesOpt.exists(set => set.contains(DocumentTypeDomainObject.HTML))


    ui.lytAdvanced.cbTypes.removeAllItems()
    Seq("docs_projection.basic_filter.cb_advanced_type.custom", "docs_projection.basic_filter.cb_advanced_type.last_xxx", "docs_projection.basic_filter.cb_advanced_type.last_zzz").foreach(itemId => ui.lytAdvanced.cbTypes.addItem(itemId, itemId.i))
    ui.lytAdvanced.cbTypes.value = parameters.advancedOpt.getOrElse("docs_projection.basic_filter.cb_advanced_type.custom")

    ui.lytLanguages.removeAllComponents()
    for (language <- imcmsServices.getDocumentI18nSupport.getLanguages.asScala) {
      val chkLanguage = new CheckBox(language.getNativeName) with TypedData[DocumentLanguage] |>> { chk =>
        chk.setIcon(Theme.Icon.Language.flag(language))
        chk.data = language
        chk.checked = language |> imcmsServices.getDocumentI18nSupport.isDefault
      }

      ui.lytLanguages.addComponent(chkLanguage)
    }
  }

  def getParameters(): Try[BasicFilterParameters] = Try {
    val idRangeOpt = when(ui.chkIdRange.isChecked) {
      IdRange(
        condOpt(ui.lytIdRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case NonNegInt(start) => start
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        },
        condOpt(ui.lytIdRange.txtEnd.trim) {
          case value if value.nonEmpty => value match {
            case NonNegInt(end) => end
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        }
      )
    }

    val textOpt = when(ui.chkText.isChecked)(ui.txtText.trim)

    val typesOpt = when(ui.chkType.isChecked) {
      Set(
        when(ui.lytTypes.chkText.isChecked) { DocumentTypeDomainObject.TEXT },
        when(ui.lytTypes.chkFile.isChecked) { DocumentTypeDomainObject.FILE },
        when(ui.lytTypes.chkHtml.isChecked) { DocumentTypeDomainObject.HTML },
        when(ui.lytTypes.chkUrl.isChecked) { DocumentTypeDomainObject.URL }
      ).flatten
    }

    val phasesOpt: Option[Set[LifeCyclePhase]] = when(ui.chkPhase.isChecked) {
      import ui.lytPhases._

      Map(chkNew -> LifeCyclePhase.NEW,
        chkPublished -> LifeCyclePhase.PUBLISHED,
        chkUnpublished -> LifeCyclePhase.UNPUBLISHED,
        chkApproved -> LifeCyclePhase.APPROVED,
        chkDisapproved -> LifeCyclePhase.DISAPPROVED,
        chkArchived -> LifeCyclePhase.ARCHIVED
      ).filterKeys(_.isChecked).values.to[Set]
    }


    val languagesOpt = when(ui.chkLanguage.isChecked) {
      (
        for {
          _chk@(chkLanguage: CheckBox with TypedData[DocumentLanguage]) <- ui.lytLanguages.iterator.asScala
          if chkLanguage.isChecked
        } yield
          chkLanguage.data
      ).to[Set]
    }

    BasicFilterParameters(idRangeOpt, textOpt, typesOpt, languagesOpt, phasesOpt)
  }
}



// svn: E160013: Commit failed (details follow):
// svn: E160013: '/!svn/bc/28859/src/main/scala/com/imcode/imcms/admin/doc/projection/filter/BasicFilterValues.scala' path not found: 404 Not Found (https://svn.imcode.com)
// svn: E160013: Commit failed (details follow): svn: E160013: '/!svn/bc/28859/src/main/scala/com/imcode/imcms/admin/doc/projection/filter/BasicFilterValues.scala' path not found: 404 Not Found (https://svn.imcode.com)