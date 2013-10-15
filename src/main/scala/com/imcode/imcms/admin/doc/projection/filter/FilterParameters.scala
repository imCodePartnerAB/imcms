package com.imcode.imcms.admin.doc.projection.filter

case class FilterParameters(basic: BasicFilterParameters = BasicFilterParameters(),
                            extendedOpt: Option[ExtendedFilterParameters] = None)
