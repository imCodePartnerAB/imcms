package com.imcode
package imcms
package admin.access.user

import com.imcode.imcms.vaadin.Editor
import imcode.server.user.{PhoneNumber, PhoneNumberType, UserDomainObject}
import com.imcode.imcms.vaadin.data._


class UserContactsEditor(user: UserDomainObject) extends Editor {

  override type Data = UserContacts

  override val view = new UserContactsEditorView

  resetValues()

  override def resetValues() {}

  override def collectValues(): UserContactsEditor#ErrorsOrData = {
    Right(UserContacts(
      title = view.txtTitle.trimmedValue,
      company = view.txtCompany.trimmedValue,
      address = view.txtAddress.trimmedValue,
      city = view.txtCity.trimmedValue,
      zip = view.txtZip.trimmedValue,
      country = view.txtCountry.trimmedValue,
      province = view.txtProvince.trimmedValue,

      Map(
        PhoneNumberType.FAX -> view.phones.txtFax.trimmedValueOpt,
        PhoneNumberType.HOME -> view.phones.txtHome.trimmedValueOpt,
        PhoneNumberType.MOBILE -> view.phones.txtMobile.trimmedValueOpt,
        PhoneNumberType.OTHER -> view.phones.txtOther.trimmedValueOpt,
        PhoneNumberType.WORK -> view.phones.txtWork.trimmedValueOpt
      ).collect { case (nrType, Some(nr)) => nrType -> new PhoneNumber(nr, nrType) }
    ))
  }
}
