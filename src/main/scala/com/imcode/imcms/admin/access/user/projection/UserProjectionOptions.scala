package com.imcode.imcms.admin.access.user.projection

case class UserProjectionOptions(
  multiSelect: Boolean = true,
  collapsedProperties: Seq[AnyRef] = Nil
)
