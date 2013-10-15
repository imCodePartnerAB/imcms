package com.imcode
package imcms
package admin.doc.projection.filter

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.event._

import _root_.imcode.server.document.LifeCyclePhase


class BasicFilterUI extends CustomLayout("admin/doc/projection/basic_filter") with FullWidth {

  object idRange {
    val chkEnabled = new CheckBox("docs_projection.basic_filter.chk_range".i) with ExposeValueChange[JBoolean] with Immediate
    
    val txtStart = new TextField { setColumns(5) }
    val txtEnd = new TextField { setColumns(5) }
  }

  object text {
    val chkEnabled = new CheckBox("docs_projection.basic_filter.chk_text".i) with ExposeValueChange[JBoolean] with Immediate
    
    val txtText = new TextField with FullWidth |>> { _.setInputPrompt("docs_projection.basic_filter.txt_text.prompt".i) }
  }

  object types {
    val chkEnabled = new CheckBox("docs_projection.basic_filter.chk_type".i) with ExposeValueChange[JBoolean] with Immediate

    val chkText = new CheckBox("docs_projection.basic_filter.chk_type_text".i)
    val chkFile = new CheckBox("docs_projection.basic_filter.chk_type_file".i)
    val chkHtml = new CheckBox("docs_projection.basic_filter.chk_type_html".i)
    val chkUrl = new CheckBox("docs_projection.basic_filter.chk_type_url".i)
    //val chkProfile = new CheckBox("docs_projection.basic_filter.chk_type_profile".i)
  }

  object phases {
    val chkEnabled = new CheckBox("docs_projection.basic_filter.chk_phase".i) with ExposeValueChange[JBoolean] with Immediate

    val chkNew = new CheckBox("docs_projection.basic_filter.chk_phase_new".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.NEW)) }
    val chkDisapproved = new CheckBox("docs_projection.basic_filter.chk_phase_disapproved".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.DISAPPROVED)) }
    val chkApproved = new CheckBox("docs_projection.basic_filter.chk_phase_approved".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.APPROVED)) }
    val chkPublished = new CheckBox("docs_projection.basic_filter.chk_phase_published".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.PUBLISHED)) }
    val chkArchived = new CheckBox("docs_projection.basic_filter.chk_phase_archived".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.ARCHIVED)) }
    val chkUnpublished = new CheckBox("docs_projection.basic_filter.chk_phase_unpublished".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.UNPUBLISHED)) }
  }

  object languages {
    val chkEnabled = new CheckBox("docs_projection.basic_filter.chk_language".i) with Immediate
    val layout = new HorizontalLayout with UndefinedSize with Spacing
  }

  object extended {
    val chkEnabled = new CheckBox("docs_projection.basic_filter.chk_extended".i) with FullWidth with ExposeValueChange[JBoolean] with Immediate
    val btnCustomize = new Button("...") with SmallStyle
  }
  
  object filterButtons {
    val btnReset = new Button("btn_reset".i) with SmallStyle
    val btnApplyFilter = new Button("btn_search".i) with SmallStyle
    val btnApplyPredefinedFilter = new Button("...") with SmallStyle
    val btnBack = new Button with SmallStyle |>> { b => b.setIcon(Theme.Icon.HistoryBack) }
    //val btnForward = new Button with SmallStyle |>> { b => b.setIcon(Theme.Icon.HistoryForward, "Go forward") }
  }

  private val lytIdRange = new HorizontalLayout with Spacing with UndefinedSize |>> { lyt =>
    import idRange._
    lyt.addComponents(txtStart, new Label("-"), txtEnd)
  }

  private val lytTypes = new HorizontalLayout with Spacing with UndefinedSize |>> { lyt =>
    import types._
    lyt.addComponents(chkText, chkFile, chkHtml, chkUrl)
  }

  private val lytPhases = new HorizontalLayout with Spacing with UndefinedSize |>> { lyt =>
    import phases._
    lyt.addComponents(chkNew, chkDisapproved, chkApproved, chkPublished, chkArchived, chkUnpublished)
  }

  private val lytSearchButtons = new HorizontalLayout with UndefinedSize with Spacing |>> { lyt =>
    import filterButtons._
    val lytFilter = new HorizontalLayout with UndefinedSize
    lytFilter.addComponents(btnApplyFilter, btnApplyPredefinedFilter)
    lyt.addComponents(btnReset, lytFilter, btnBack/*, btnForward*/)
  }

  private val lytExtended = new HorizontalLayout with Spacing with UndefinedSize |>> { lyt =>
    lyt.addComponents(extended.chkEnabled, extended.btnCustomize)
  }

  this.addNamedComponents(
    "docs_projection.basic_filter.chk_range" -> idRange.chkEnabled,
    "docs_projection.basic_filter.range" -> lytIdRange,

    "docs_projection.basic_filter.chk_text" -> text.chkEnabled,
    "docs_projection.basic_filter.text" -> text.txtText,

    "docs_projection.basic_filter.chk_type" -> types.chkEnabled,
    "docs_projection.basic_filter.types" -> lytTypes,

    "docs_projection.basic_filter.chk_phase" -> phases.chkEnabled,
    "docs_projection.basic_filter.phases" -> lytPhases,

    "docs_projection.basic_filter.chk_language" -> languages.chkEnabled,
    "docs_projection.basic_filter.languages" -> languages.layout,

    "docs_projection.basic_filter.chk_extended" -> lytExtended,
    //"docs_projection.basic_filter.extended" -> lytExtended,

    "docs_projection.basic_filter.buttons" -> lytSearchButtons
  )

  idRange.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.basic_filter.range", idRange.chkEnabled, lytIdRange,
      new Label("%s - %s".format(idRange.txtStart.getInputPrompt.trimToEmpty, idRange.txtEnd.getInputPrompt.trimToEmpty))
    )
  }

  text.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.basic_filter.text", text.chkEnabled, text.txtText)
  }

  types.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.basic_filter.types", types.chkEnabled, lytTypes)
  }

  phases.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.basic_filter.phases", phases.chkEnabled, lytPhases)
  }

  languages.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.basic_filter.languages", languages.chkEnabled, languages.layout)
  }
}