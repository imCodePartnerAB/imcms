package com.imcode
package imcms
package admin
package access.user


import com.imcode.imcms.vaadin.component.{UndefinedSize, Spacing}
import com.vaadin.ui.{Panel, VerticalLayout, TextField, FormLayout}

class UserContactsEditorView extends VerticalLayout with Spacing with UndefinedSize {

  val txtTitle = new TextField("user.contact.title.".i)
  val txtCompany = new TextField("user.contact.company.".i)
  val txtCity = new TextField("user.contact.city.".i)
  val txtAddress = new TextField("user.contact.address.".i)
  val txtZip = new TextField("user.contact.zip.".i)
  val txtCountry = new TextField("user.contact.country.".i)
  val txtProvince = new TextField("user.contact.province.".i)

  object phones {
    val txtOther = new TextField("user.contact.phone.other.".i)
    val txtHome = new TextField("user.contact.phone.home.".i)
    val txtWork = new TextField("user.contact.phone.work.".i)
    val txtMobile = new TextField("user.contact.phone.mobile.".i)
    val txtFax = new TextField("user.contact.phone.fax.".i)
  }


  private val lytMain = new FormLayout(txtTitle, txtCompany, txtCity, txtZip, txtCountry, txtProvince)
  private val lytPhones = new FormLayout(phones.txtWork, phones.txtMobile, phones.txtHome, phones.txtOther, phones.txtFax)

  addComponents(lytMain, new Panel("Phones", lytPhones) with UndefinedSize)
}
