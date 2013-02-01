package com.imcode
package imcms.api

import com.imcode.imcms.mapping.DocumentMapper
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import javax.servlet.http.HttpServletRequest
import imcode.server.{ImcmsServices, ImcmsConstants}
import org.apache.commons.lang.StringUtils


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
                   .orElse(currentDocGetterCallback.asOption.map(_.contentLanguages.preferred))
                   .orElse(i18nContentSupport.getForHost(request.getServerName).asOption)
                   .getOrElse(defaultLanguage)

    val contentLanguages = ContentLanguages(preferredLanguage, defaultLanguage)
    val docGetterCallback =
      (for {
        PosInt(docId) <- request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID).asOption
        DocVersionType(docVersionType) <- request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION).asOption
        if !user.isDefaultUser
      } yield {
        docVersionType match {
          case WorkingVersion => WorkingDocGetterCallback(contentLanguages, docId)
          case DefaultVersion => DefaultDocGetterCallback(contentLanguages)
          case CustomVersion(docVersionNo) => CustomDocGetterCallback(contentLanguages, docId, docVersionNo)
        }
      }) getOrElse {
        currentDocGetterCallback match {
          case docGetterCallback: CustomDocGetterCallback => docGetterCallback.copy(contentLanguages)
          case docGetterCallback: WorkingDocGetterCallback => docGetterCallback.copy(contentLanguages)
          case _ => DefaultDocGetterCallback(contentLanguages)
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
  def contentLanguages: ContentLanguages
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper): DocumentDomainObject
}

case class DefaultDocGetterCallback(contentLanguages: ContentLanguages) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    docMapper.getDefaultDocument(docId, contentLanguages.preferred) match {
      case doc if doc != null && !contentLanguages.preferredIsDefault && user.isSuperAdmin =>
        val meta = doc.getMeta

        if (!meta.getEnabledLanguages.contains(contentLanguages.preferred)) {
          if (meta.getDisabledLanguageShowSetting == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE)
            docMapper.getDefaultDocument(docId, contentLanguages.default)
          else
            null
        } else doc

      case doc => doc
    }
}

case class WorkingDocGetterCallback(contentLanguages: ContentLanguages, selectedDocId: Int) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    if (selectedDocId == docId) docMapper.getWorkingDocument(docId, contentLanguages.preferred)
    else DefaultDocGetterCallback(contentLanguages).getDoc(docId, user, docMapper)
}

case class CustomDocGetterCallback(contentLanguages: ContentLanguages, selectedDocId: Int, selectedDocVersionNo: Int) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    if (selectedDocId == docId) docMapper.getCustomDocument(DocRef.of(selectedDocId, selectedDocVersionNo), contentLanguages.preferred)
    else DefaultDocGetterCallback(contentLanguages).getDoc(docId, user, docMapper)
}

case class ContentLanguages(preferred: ContentLanguage, default: ContentLanguage) {
  val preferredIsDefault = preferred == default
}