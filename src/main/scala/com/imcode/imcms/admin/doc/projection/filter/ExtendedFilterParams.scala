package com.imcode.imcms.admin.doc.projection.filter

case class ExtendedFilterParams(
  datesOpt: Option[Map[String, DateRange]] = Some(Map.empty),
  categoriesOpt: Option[Set[String]] = Some(Set.empty),
  relationshipOpt: Option[Relationship] = Some(Relationship(Relationship.Unspecified, Relationship.Unspecified)),
  maintainersOpt: Option[Maintainers] = Some(Maintainers(Some(Set.empty), Some(Set.empty)))
)