package com.imcode
package imcms
package admin.access.user

import _root_.imcode.server.user._

import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._
import com.vaadin.ui.themes.Reindeer


class UserEditorView extends TabSheet with UndefinedSize {

  private val lytAccount = new FormLayout with UndefinedSize
  private val lytContacts = new VerticalLayout with UndefinedSize

  addTab(lytAccount, "account".i)
  addTab(lytContacts, "contacts".i)
  addStyleName(Reindeer.TABSHEET_MINIMAL)
  addStyleName("user-editor")

  object account {
    val txtLogin = new TextField("user_editor.frm_fld.txt_login".i)
    val txtPassword = new PasswordField("user_editor.frm_fld.pwd_password".i)
    val txtPasswordCheck = new PasswordField("user_editor.frm_fld.pwd_password_check".i)
    val txtFirstName = new TextField("user_editor.frm_fld.txt_first_name".i)
    val txtLastName = new TextField("user_editor.frm_fld.txt_last_name".i)
    val chkEnabled = new CheckBox("user_editor.frm_fld.chk_enabled".i)
    val tcsRoles = new TwinColSelect("user_editor.frm_fld.tcs_roles".i) with MultiSelect[RoleId] with TCSDefaultI18n
    val sltUILanguage = new ComboBox("user_editor.frm_fld.language".i) with SingleSelect[String] with NoNullSelection
    val txtEmail = new TextField("user_editor.frm_fld.email".i)

    val lytPassword = new HorizontalLayout(txtPassword, txtPasswordCheck) with UndefinedSize with Spacing |>> { lyt =>
      lyt.setCaption("user_editor.frm_fld.password".i)
    }

    val lytName = new HorizontalLayout(txtFirstName, txtLastName) with UndefinedSize with Spacing |>> { lyt =>
      lyt.setCaption("user_editor.frm_fld.name".i)
    }

    val lytLogin = new HorizontalLayout(txtLogin, chkEnabled) with UndefinedSize with Spacing |>> { lyt =>
      lyt.setCaption("user_editor.frm_fld.account".i)
      lyt.setComponentAlignment(chkEnabled, Alignment.BOTTOM_LEFT)
    }

    Seq(txtLogin, txtPassword, txtPasswordCheck, txtEmail).foreach(_.setRequired(true))
  }

  object contacts {
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
  }

  lytAccount |> { lyt =>
    import account._
    lyt.addComponents(lytLogin, lytPassword, lytName, txtEmail, sltUILanguage, tcsRoles)
  }

  lytContacts |> { lyt =>
    import contacts._

    val lytMain = new FormLayout(txtTitle, txtCompany, txtCity, txtZip, txtCountry, txtProvince)
    val lytPhones = new FormLayout(phones.txtWork, phones.txtMobile, phones.txtHome, phones.txtOther, phones.txtFax)

    lyt.addComponents(lytMain, new Panel("Phones".i, lytPhones) with UndefinedSize)
  }
}