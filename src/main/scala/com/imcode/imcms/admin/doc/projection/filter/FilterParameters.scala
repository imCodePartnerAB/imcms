package com.imcode.imcms.admin.doc.projection.filter

case class FilterParameters(basic: BasicFilterParams = BasicFilterParams(),
                            extendedOpt: Option[ExtendedFilterParams] = None)
