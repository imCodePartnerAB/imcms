package com.imcode.imcms.admin.doc.projection.filter

case class AdvancedFilterParameters(
  datesOpt: Option[Map[String, DateRange]] = None,
  categoriesOpt: Option[Set[String]] = None,
  relationshipOpt: Option[Relationship] = None,
  maintainersOpt: Option[Maintainers] = None
)