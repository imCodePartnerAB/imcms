package com.imcode.imcms.admin.doc.projection.filter

import imcode.server.document.DocumentTypeDomainObject

case class BasicFilterValues(
  idRange: Option[IdRange] = Some(IdRange(None, None)),
  text: Option[String] = Some(""),
  docType: Option[Set[DocumentTypeDomainObject]] = Some(Set.empty),
  profile: Boolean = false, // Set[String]
  advanced: Option[String] = None // value in drop-down
)
