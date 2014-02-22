package com.imcode.imcms.admin.access.user.projection

case class UserProjectionSettings(
                                   multiSelect: Boolean = true,
                                   collapsedProperties: Seq[AnyRef] = Nil
                                   )
