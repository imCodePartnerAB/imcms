package com.imcode
package imcms.admin.access.role

import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._


class RoleEditorView extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name")
  val chkPermGetPasswordByEmail = new CheckBox("Permission to get password by email")
  val chkPermAccessMyPages = new CheckBox("""Permission to access "My pages" """)
  val chkPermUseImagesFromArchive = new CheckBox("Permission to use images from image archive")
  val chkPermChangeImagesInArchive = new CheckBox("Permission to change images in image archive")

  addComponents(txtName, chkPermGetPasswordByEmail, chkPermAccessMyPages, chkPermUseImagesFromArchive,
    chkPermChangeImagesInArchive)
}