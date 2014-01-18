package com.imcode
package imcms
package admin
package access.user

import imcode.server.user.{PhoneNumber, PhoneNumberType}

case class UserContacts(title: String, company: String, address: String, city: String, zip: String, country: String,
                        province: String, phones: Map[PhoneNumberType, PhoneNumber])

