package com.imcode.imcms.admin.doc.projection.filter

case class Maintainers(creatorsOpt: Option[Set[UserId]], publishersOpt: Option[Set[UserId]])
