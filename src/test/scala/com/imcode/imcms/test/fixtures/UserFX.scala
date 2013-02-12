package com.imcode
package imcms.test
package fixtures

import imcode.server.user.{UserDomainObject, RoleId}

object UserFX {
  def mkSuperAdmin: UserDomainObject = mkUser(0, RoleId.SUPERADMIN)
  def mkDefaultUser: UserDomainObject = mkUser(2, RoleId.USERS)

  def mkUser(id: Int, roleIds: RoleId*): UserDomainObject = new UserDomainObject(id) |>> { user =>
    roleIds.foreach(user.addRoleId)
  }
}
