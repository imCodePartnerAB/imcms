package com.imcode
package imcms
package admin.doc.projection.filter

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._


class BasicFilterUI extends CustomLayout("admin/doc/projection/basic_filter") with FullWidth {

  val chkIdRange = new CheckBox("docs_projection.basic_filter_lyt.chk_range".i) with Immediate with ExposeValueChange
  val lytIdRange = new HorizontalLayout with Spacing with UndefinedSize {
    val txtStart = new TextField { setColumns(5) }
    val txtEnd = new TextField { setColumns(5) }
    val lblSeparator = new Label("-")

    this.addComponents(txtStart, lblSeparator, txtEnd)
  }

  val chkText = new CheckBox("docs_projection.basic_filter_lyt.chk_text".i) with Immediate with ExposeValueChange
  val txtText = new TextField |>> { _.setInputPrompt("docs_projection.basic_filter_lyt.txt_text.prompt".i) }

  val chkType = new CheckBox("docs_projection.basic_filter_lyt.chk_type".i) with Immediate with ExposeValueChange
  val lytType = new HorizontalLayout with UndefinedSize with Spacing {
    val chkText = new CheckBox("docs_projection.basic_filter_lyt.chk_type_text".i)
    val chkFile = new CheckBox("docs_projection.basic_filter_lyt.chk_type_file".i)
    val chkHtml = new CheckBox("docs_projection.basic_filter_lyt.chk_type_html".i)
    val chkUrl = new CheckBox("docs_projection.basic_filter_lyt.chk_type_url".i)
    //val chkProfile = new CheckBox("docs_projection.basic_filter_lyt.chk_type_profile".i)
    //this.addComponents(chkText, chkFile, chkHtml, chkUrl, new Label(" | ") with UndefinedSize,  chkProfile)
    this.addComponents(chkText, chkFile, chkHtml, chkUrl)
  }

  val chkPhase = new CheckBox("docs_projection.basic_filter_lyt.chk_phase".i) with Immediate with ExposeValueChange
  val lytPhases = new HorizontalLayout with Spacing with UndefinedSize {
    val chkNew = new CheckBox("docs_projection.basic_filter_lyt.chk_phase_new".i)
    val chkDisapproved = new CheckBox("docs_projection.basic_filter_lyt.chk_phase_disapproved".i)
    val chkApproved = new CheckBox("docs_projection.basic_filter_lyt.chk_phase_approved".i)
    val chkPublished = new CheckBox("docs_projection.basic_filter_lyt.chk_phase_published".i)
    val chkArchived = new CheckBox("docs_projection.basic_filter_lyt.chk_phase_archived".i)
    val chkUnpublished = new CheckBox("docs_projection.basic_filter_lyt.chk_phase_unpublished".i)

    this.addComponents(chkNew, chkDisapproved, chkApproved, chkPublished, chkArchived, chkUnpublished)
  }

  val chkAdvanced = new CheckBox("docs_projection.basic_filter_lyt.chk_advanced".i) with Immediate with ExposeValueChange

  val lytAdvanced = new HorizontalLayout with UndefinedSize with Spacing {
    val cbTypes = new ComboBox with NoNullSelection with SingleSelect[String] with Immediate
    val btnCustomize = new Button("...") with SmallStyle
    val btnSaveAs = new Button("docs_projection.basic_filter_lyt.btn_advanced_save_as".i) with SmallStyle with Disabled
    val btnDelete = new Button("docs_projection.basic_filter_lyt.btn_advanced_delete".i) with SmallStyle with Disabled

    doto(cbTypes, btnCustomize, btnSaveAs, btnDelete) { component =>
      addComponent(component)
      setComponentAlignment(component, Alignment.MIDDLE_LEFT)
    }
  }


  val lytButtons = new HorizontalLayout with UndefinedSize with Spacing {
    val btnReset = new Button("btn_reset".i) with SmallStyle
    val btnFilter = new Button("btn_search".i) with SmallStyle

    this.addComponents(btnReset, btnFilter)
  }

  this.addNamedComponents(
    "docs_projection.basic_filter_lyt.chk_range" -> chkIdRange,
    "docs_projection.basic_filter_lyt.range" -> lytIdRange,
    "docs_projection.basic_filter_lyt.chk_text" -> chkText,
    "docs_projection.basic_filter_lyt.text" -> txtText,
    "docs_projection.basic_filter_lyt.chk_type" -> chkType,
    "docs_projection.basic_filter_lyt.type" -> lytType,
    "docs_projection.basic_filter_lyt.chk_phase" -> chkPhase,
    "docs_projection.basic_filter_lyt.status" -> lytPhases,
    "docs_projection.basic_filter_lyt.chk_advanced" -> chkAdvanced,
    "docs_projection.basic_filter_lyt.advanced" -> lytAdvanced,
    "docs_projection.basic_filter_lyt.buttons" -> lytButtons
  )
}