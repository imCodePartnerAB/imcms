package com.imcode
package imcms.api

import com.imcode.imcms.mapping.DocumentMapper
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import javax.servlet.http.HttpServletRequest
import imcode.server.{ImcmsServices, ImcmsConstants}


object DocGetterCallbacks {

  private sealed trait DocVersionType
  private case object WorkingVersion extends DocVersionType
  private case object DefaultVersion extends DocVersionType
  private case class CustomVersion(no: Int) extends DocVersionType

  private object DocVersionType {
    def unapply(string: String): Option[DocVersionType] =
      PartialFunction.condOpt(string.trimToEmpty.toLowerCase) {
        case DocumentVersion.DEFAULT_VERSION_NAME => DefaultVersion
        case DocumentVersion.WORKING_VERSION_NAME => WorkingVersion
        case PosInt(no) if no == DocumentVersion.WORKING_VERSION_NO => WorkingVersion
        case PosInt(no) => CustomVersion(no)
      }
  }

  /** Creates callback and sets it to a user. */
  def updateUserDocGetterCallback(request: HttpServletRequest, services: ImcmsServices, user: UserDomainObject) {
    val currentDocGetterCallback = user.getDocGetterCallback
    val i18nContentSupport = services.getI18nContentSupport
    val defaultLanguage = i18nContentSupport.getDefaultLanguage
    val preferredLanguage = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE).asOption
                   .flatMap(code => i18nContentSupport.getByCode(code).asOption)
                   .orElse(currentDocGetterCallback.asOption.map(_.documentLanguages.preferred))
                   .orElse(i18nContentSupport.getForHost(request.getServerName).asOption)
                   .getOrElse(defaultLanguage)

    val documentLanguages = DocumentLanguages(preferredLanguage, defaultLanguage)
    val docGetterCallback =
      (for {
        PosInt(docId) <- request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID).asOption
        DocVersionType(docVersionType) <- request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION).asOption
        if !user.isDefaultUser
      } yield {
        docVersionType match {
          case WorkingVersion => WorkingDocGetterCallback(documentLanguages, docId)
          case DefaultVersion => DefaultDocGetterCallback(documentLanguages)
          case CustomVersion(docVersionNo) => CustomDocGetterCallback(documentLanguages, docId, docVersionNo)
        }
      }) getOrElse {
        currentDocGetterCallback match {
          case docGetterCallback: CustomDocGetterCallback => docGetterCallback.copy(documentLanguages)
          case docGetterCallback: WorkingDocGetterCallback => docGetterCallback.copy(documentLanguages)
          case _ => DefaultDocGetterCallback(documentLanguages)
        }
      }

    user.setDocGetterCallback(docGetterCallback)
  }
}


/**
 * Parametrized callback for DocumentMapper#getDocument method.
 * A callback is (re)created on each request and (re)assigned to a user.
 *
 * Default doc callback always returns default version of any doc if it is present and a user has at least 'view' rights on it.
 *
 * Working and Custom doc callback return working and custom version of a document with particular id;
 * for other doc ids they behave exactly as default doc callback.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(Integer)
 */
trait DocGetterCallback {
  def documentLanguages: DocumentLanguages
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper): DocumentDomainObject
}

case class DefaultDocGetterCallback(documentLanguages: DocumentLanguages) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    docMapper.getDefaultDocument(docId, documentLanguages.preferred) match {
      case doc if doc != null && !documentLanguages.preferredIsDefault && user.isSuperAdmin =>
        val meta = doc.getMeta

        if (!meta.getEnabledLanguages.contains(documentLanguages.preferred)) {
          if (meta.getDisabledLanguageShowSetting == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE)
            docMapper.getDefaultDocument(docId, documentLanguages.default)
          else
            null
        } else doc

      case doc => doc
    }
}

case class WorkingDocGetterCallback(documentLanguages: DocumentLanguages, selectedDocId: Int) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    if (selectedDocId == docId) docMapper.getWorkingDocument(docId, documentLanguages.preferred)
    else DefaultDocGetterCallback(documentLanguages).getDoc(docId, user, docMapper)
}

case class CustomDocGetterCallback(documentLanguages: DocumentLanguages, selectedDocId: Int, selectedDocVersionNo: Int) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    if (selectedDocId == docId) docMapper.getCustomDocument(DocRef.of(selectedDocId, selectedDocVersionNo), documentLanguages.preferred)
    else DefaultDocGetterCallback(documentLanguages).getDoc(docId, user, docMapper)
}

case class DocumentLanguages(preferred: DocumentLanguage, default: DocumentLanguage) {
  val preferredIsDefault = preferred == default
}