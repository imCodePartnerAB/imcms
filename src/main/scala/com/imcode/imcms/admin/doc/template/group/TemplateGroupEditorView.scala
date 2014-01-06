package com.imcode
package imcms
package admin.doc.template
package group

import com.imcode.imcms.vaadin.component.{MultiSelect, Required, Disabled, UndefinedSize}
import com.vaadin.ui.{TwinColSelect, TextField, FormLayout}

class TemplateGroupEditorView extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name") with Required
  val twsTemplates = new TwinColSelect("Templates".i) with MultiSelect[String] |>> {tws =>
    tws.setLeftColumnCaption("available".i)
    tws.setRightColumnCaption("selected".i)
  }


  this.addComponents(txtId, txtName, twsTemplates)
}
