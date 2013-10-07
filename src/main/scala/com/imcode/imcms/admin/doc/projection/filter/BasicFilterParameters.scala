package com.imcode.imcms.admin.doc.projection.filter

import _root_.imcode.server.document.{LifeCyclePhase, DocumentTypeDomainObject}
import com.imcode.imcms.api.DocumentLanguage

/**
 * The checkbox associated with a parameter is checked only if this parameter is defined.
 *
 * @param idRangeOpt
 * @param textOpt
 * @param docTypesOpt
 * @param languagesOpt
 * @param phasesOpt
 */
case class BasicFilterParameters(
  idRangeOpt: Option[IdRange] = Some(IdRange(None, None)),
  textOpt: Option[String] = Some(""),
  docTypesOpt: Option[Set[DocumentTypeDomainObject]] = Some(Set.empty),
  languagesOpt: Option[Set[DocumentLanguage]] = Some(Set.empty),
  phasesOpt: Option[Set[LifeCyclePhase]] = Some(Set.empty),
  advancedOpt: Option[String] = Some("")
)
