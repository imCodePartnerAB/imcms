package com.imcode
package imcms
package admin.doc.projection.filter

import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._
import com.vaadin.ui._


class BasicFilterUI extends CustomLayout("admin/doc/search/basic_form") with FullWidth {

  val chkIdRange = new CheckBox("doc.search.basic.frm.fld.chk_range".i) with Immediate with ExposeValueChange
  val lytIdRange = new HorizontalLayout with Spacing with UndefinedSize {
    val txtStart = new TextField { setColumns(5) }
    val txtEnd = new TextField { setColumns(5) }

    this.addComponents(txtStart, txtEnd)
  }

  val chkText = new CheckBox("doc.search.basic.frm.fld.chk_text".i) with Immediate with ExposeValueChange
  val txtText = new TextField { setInputPrompt("doc.search.basic.frm.fld.txt_text.prompt".i) }

  val chkType = new CheckBox("doc.search.basic.frm.fld.chk_type".i) with Immediate with ExposeValueChange
  val lytType = new HorizontalLayout with UndefinedSize with Spacing {
    val chkText = new CheckBox("doc.search.basic.frm.fld.chk_type_text".i)
    val chkFile = new CheckBox("doc.search.basic.frm.fld.chk_type_file".i)
    val chkHtml = new CheckBox("doc.search.basic.frm.fld.chk_type_html".i)
    val chkURL = new CheckBox("doc.search.basic.frm.fld.chk_type_url".i)
    val chkProfile = new CheckBox("doc.search.basic.frm.fld.chk_type_profile".i)

    this.addComponents(chkText, chkFile, chkHtml, chkURL, new Label(" | ") with UndefinedSize,  chkProfile)
  }

  val chkAdvanced = new CheckBox("doc.search.basic.frm.fld.chk_advanced".i) with Immediate with ExposeValueChange

  val lytAdvanced = new HorizontalLayout with UndefinedSize with Spacing {
    val cbTypes = new ComboBox with NoNullSelection with SingleSelect[String] with Immediate
    val btnCustomize = new Button("...") with SmallStyle
    val btnSaveAs = new Button("doc.search.basic.frm.fld.btn_advanced_save_as".i) with SmallStyle with Disabled
    val btnDelete = new Button("doc.search.basic.frm.fld.btn_advanced_delete".i) with SmallStyle with Disabled

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
    "doc.search.basic.frm.fld.chk_range" -> chkIdRange,
    "doc.search.basic.frm.fld.range" -> lytIdRange,
    "doc.search.basic.frm.fld.chk_text" -> chkText,
    "doc.search.basic.frm.fld.text" -> txtText,
    "doc.search.basic.frm.fld.chk_type" -> chkType,
    "doc.search.basic.frm.fld.type" -> lytType,
    "doc.search.basic.frm.fld.chk_advanced" -> chkAdvanced,
    "doc.search.basic.frm.fld.advanced" -> lytAdvanced,
    "doc.search.basic.frm.fld.buttons" -> lytButtons
  )
}

