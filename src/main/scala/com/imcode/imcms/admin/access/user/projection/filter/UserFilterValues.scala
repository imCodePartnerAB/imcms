package com.imcode
package imcms
package admin.access.user.projection.filter

import imcode.server.user.RoleId

case class UserFilterValues(
  text: Option[String] = Some(""),
  roles: Option[Set[RoleId]] = None,
  isShowInactive: Boolean = false
)
