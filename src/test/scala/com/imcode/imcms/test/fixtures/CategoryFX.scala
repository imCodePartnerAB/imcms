package com.imcode
package imcms.test
package fixtures

import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}


object CategoryFX {
  def mkCategories(starId: Int = 0, count: Int = 10): Seq[CategoryDomainObject] =
    for (id <- starId until (starId + count))
    yield new CategoryDomainObject |>> { c =>
      c.setId(id)
      c.setName(s"category_$id")
      c.setType(new CategoryTypeDomainObject(id, s"category_type_$id", id + 1, id % 2 == 0))
    }
}
