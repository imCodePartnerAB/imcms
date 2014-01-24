package com.imcode
package imcms
package admin.access.user.projection.filter

import imcode.server.user.RoleId

case class UserFilterParameters(
  text: Option[String] = Some(""),
  roles: Option[Set[RoleId]] = None,
  isShowInactive: Boolean = false
)
