package com.imcode.imcms.admin.doc.projection.filter

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import imcode.server.document.LifeCyclePhase


class BasicFilterUI extends CustomLayout("admin/doc/projection/basic_filter") with FullWidth {

  val chkIdRange = new CheckBox("docs_projection.basic_filter.chk_range".i) with ExposeValueChange[JBoolean] with Immediate
  val lytIdRange = new HorizontalLayout with Spacing with UndefinedSize {
    val txtStart = new TextField { setColumns(5) }
    val txtEnd = new TextField { setColumns(5) }
    val lblSeparator = new Label("-")

    this.addComponents(txtStart, lblSeparator, txtEnd)
  }

  val chkText = new CheckBox("docs_projection.basic_filter.chk_text".i) with ExposeValueChange[JBoolean] with Immediate
  val txtText = new TextField |>> { _.setInputPrompt("docs_projection.basic_filter.txt_text.prompt".i) }

  val chkType = new CheckBox("docs_projection.basic_filter.chk_type".i) with ExposeValueChange[JBoolean] with Immediate
  val lytTypes = new HorizontalLayout with UndefinedSize with Spacing {
    val chkText = new CheckBox("docs_projection.basic_filter.chk_type_text".i)
    val chkFile = new CheckBox("docs_projection.basic_filter.chk_type_file".i)
    val chkHtml = new CheckBox("docs_projection.basic_filter.chk_type_html".i)
    val chkUrl = new CheckBox("docs_projection.basic_filter.chk_type_url".i)
    //val chkProfile = new CheckBox("docs_projection.basic_filter.chk_type_profile".i)
    //this.addComponents(chkText, chkFile, chkHtml, chkUrl, new Label(" | ") with UndefinedSize,  chkProfile)
    this.addComponents(chkText, chkFile, chkHtml, chkUrl)
  }

  val chkPhase = new CheckBox("docs_projection.basic_filter.chk_phase".i) with ExposeValueChange[JBoolean] with Immediate
  val lytPhases = new HorizontalLayout with Spacing with UndefinedSize {
    val chkNew = new CheckBox("docs_projection.basic_filter.chk_phase_new".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.NEW)) }
    val chkDisapproved = new CheckBox("docs_projection.basic_filter.chk_phase_disapproved".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.DISAPPROVED)) }
    val chkApproved = new CheckBox("docs_projection.basic_filter.chk_phase_approved".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.APPROVED)) }
    val chkPublished = new CheckBox("docs_projection.basic_filter.chk_phase_published".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.PUBLISHED)) }
    val chkArchived = new CheckBox("docs_projection.basic_filter.chk_phase_archived".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.ARCHIVED)) }
    val chkUnpublished = new CheckBox("docs_projection.basic_filter.chk_phase_unpublished".i) |>> { _.setIcon(Theme.Icon.Doc.phase(LifeCyclePhase.UNPUBLISHED)) }

    this.addComponents(chkNew, chkDisapproved, chkApproved, chkPublished, chkArchived, chkUnpublished)
  }

  val chkAdvanced = new CheckBox("docs_projection.basic_filter.chk_advanced".i) with ExposeValueChange[JBoolean] with Immediate

  val lytAdvanced = new HorizontalLayout with UndefinedSize with Spacing {
    val cbTypes = new ComboBox with NoNullSelection with SingleSelect[String] with Immediate
    val btnCustomize = new Button("...") with SmallStyle
    val btnSaveAs = new Button("docs_projection.basic_filter.btn_advanced_save_as".i) with SmallStyle with Disabled
    val btnDelete = new Button("docs_projection.basic_filter.btn_advanced_delete".i) with SmallStyle with Disabled

    Seq(cbTypes, btnCustomize/*, btnSaveAs, btnDelete*/).foreach { component =>
      addComponent(component)
      setComponentAlignment(component, Alignment.MIDDLE_LEFT)
    }
  }

  val chkLanguage = new CheckBox("docs_projection.basic_filter.chk_language".i) with Immediate
  val lytLanguages = new HorizontalLayout with UndefinedSize with Spacing {}

  val lytButtons = new HorizontalLayout with UndefinedSize with Spacing {
    val btnReset = new Button("btn_reset".i) with SmallStyle
    val btnFilter = new Button("btn_search".i) with SmallStyle
    val btnBack = new Button with SmallStyle |>> { b => b.setIcon(Theme.Icon.HistoryBack, "Go back") }
    val btnForward = new Button with SmallStyle |>> { b => b.setIcon(Theme.Icon.HistoryForward, "Go forward") }

    this.addComponents(btnReset, btnFilter, btnBack, btnForward)
  }

  this.addNamedComponents(
    "docs_projection.basic_filter.chk_range" -> chkIdRange,
    "docs_projection.basic_filter.range" -> lytIdRange,
    "docs_projection.basic_filter.chk_text" -> chkText,
    "docs_projection.basic_filter.text" -> txtText,
    "docs_projection.basic_filter.chk_type" -> chkType,
    "docs_projection.basic_filter.types" -> lytTypes,
    "docs_projection.basic_filter.chk_phase" -> chkPhase,
    "docs_projection.basic_filter.phases" -> lytPhases,

    "docs_projection.basic_filter.chk_language" -> chkLanguage,
    "docs_projection.basic_filter.languages" -> lytLanguages,

    "docs_projection.basic_filter.chk_advanced" -> chkAdvanced,
    "docs_projection.basic_filter.advanced" -> lytAdvanced,
    "docs_projection.basic_filter.buttons" -> lytButtons
  )
}