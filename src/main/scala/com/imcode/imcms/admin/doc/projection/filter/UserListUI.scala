package com.imcode
package imcms
package admin.doc.projection.filter

import com.vaadin.ui._

import com.imcode.imcms.vaadin.ui._


/**
 * Component for managing listByNamedParams of users.
 */
class UserListUI(caption: String = "") extends GridLayout(2, 2) {
  val chkEnabled = new CheckBox(caption) with ExposeValueChange[JBoolean] with Immediate
  val lstUsers = new ListSelect with MultiSelectBehavior[UserId] with NoNullSelection {
    setColumns(20)
  }
  val btnAdd = new Button("+") with SmallStyle
  val btnRemove = new Button("-") with SmallStyle
  val lytButtons = new VerticalLayout with UndefinedSize

  lytButtons.addComponents(btnRemove, btnAdd)
  addComponent(chkEnabled, 0, 0, 1, 0)
  this.addComponents(lstUsers, lytButtons)

  setComponentAlignment(lytButtons, Alignment.BOTTOM_LEFT)
}
