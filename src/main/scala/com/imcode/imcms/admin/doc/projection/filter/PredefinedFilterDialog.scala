package com.imcode
package imcms
package admin.doc.projection.filter

import com.imcode.imcms.vaadin.component.dialog.OkCancelDialog
import com.vaadin.ui.{ComboBox, VerticalLayout, CheckBox}
import com.imcode.imcms.vaadin.component.{SingleSelect, NoNullSelection, Spacing, UndefinedSize}

// todo: i18n
// todo: implement
class PredefinedFilterDialog extends OkCancelDialog("Quick search".i) {

  mainComponent = new VerticalLayout with Spacing with UndefinedSize {
    val cbFilterType = new ComboBox("Documents".i) with SingleSelect[String] with NoNullSelection

    val cbCreatedByCurrentUser = new CheckBox("Created by me".i)
    val cbApprovedByCurrentUser = new CheckBox("Approved by me".i)

    addComponents(cbFilterType, cbCreatedByCurrentUser, cbApprovedByCurrentUser)

    Seq(
      "Created in the past week".i,
      "Modified, but not created in the past week".i,
      "Approved and published documents to be archived within one week".i,
      "Approved, published and archived documents to be unpublished within one week".i,
      "New, approved, published and archived documents not modified in the last six months".i
    ).foreach(cbFilterType.addItem)
  }

  btnOk.setCaption("Search".i)
}
