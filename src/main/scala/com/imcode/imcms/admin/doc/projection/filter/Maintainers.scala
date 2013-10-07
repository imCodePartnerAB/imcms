package com.imcode
package imcms
package admin.doc.projection.filter

case class Maintainers(creatorsOpt: Option[Set[UserId]], publishersOpt: Option[Set[UserId]])
