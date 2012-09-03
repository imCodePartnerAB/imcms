package com.imcode
package imcms.api

import com.imcode.imcms.mapping.DocumentMapper
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import javax.servlet.http.HttpServletRequest
import imcode.server.{Imcms, ImcmsServices, ImcmsConstants}
import imcode.server.document.textdocument.DocRef


object DocGetterCallbackUtil {

  /** Creates callback and sets it to a user. */
  def updateUserDocGetterCallback(request: HttpServletRequest, services: ImcmsServices, user: UserDomainObject) {
    val currentDocGetterCallback = user.getDocGetterCallback
    val i18nSupport = services.getI18nSupport
    val defaultLanguage = i18nSupport.getDefaultLanguage
    val language = Option(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE))
                   .map(i18nSupport.getByCode)
                   .orElse(currentDocGetterCallback |> opt map (_.languages.selected))
                   .orElse(i18nSupport.getForHost(request.getServerName) |> opt)
                   .getOrElse(defaultLanguage)

    val callbackLanguages = CallbackLanguages(language, defaultLanguage)
    val docGetterCallback =
      (for {
        docIdentity <- Option(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID))
        docVersionNoStr <- Option(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION))
        if !user.isDefaultUser
      } yield {
        val docId: Int = docIdentity match {
          case IntNum(n) => n
          case _ =>
            Imcms.getServices.getDocumentMapper.toDocumentId(docIdentity) |> opt getOrElse {
              sys.error("Document with identity %s does not exists." format docIdentity)
            }
        }

        docVersionNoStr match {
          case IntNum(DocumentVersion.WORKING_VERSION_NO) => WorkingDocGetterCallback(callbackLanguages, docId)
          case PosInt(docVersionNo) => CustomDocGetterCallback(callbackLanguages, docId, docVersionNo)
          case _ =>
            sys.error("Invalid document version value: %s." format docIdentity)
        }
      }) getOrElse {
        currentDocGetterCallback match {
          case docGetterCallback: CustomDocGetterCallback => docGetterCallback.copy(callbackLanguages)
          case docGetterCallback: WorkingDocGetterCallback => docGetterCallback.copy(callbackLanguages)
          case _ => DefaultDocGetterCallback(callbackLanguages)
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
  def languages: CallbackLanguages
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper): DocumentDomainObject
}

case class DefaultDocGetterCallback(languages: CallbackLanguages) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    docMapper.getDefaultDocument(docId, languages.selected) match {
      case doc if doc != null && !languages.selectedIsDefault && user.isSuperAdmin =>
        val meta = doc.getMeta

        if (!meta.getEnabledLanguages.contains(languages.selected)) {
          if (meta.getDisabledLanguageShowSetting == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE)
            docMapper.getDefaultDocument(docId, languages.default)
          else
            null
        } else doc

      case doc => doc
    }
}

case class WorkingDocGetterCallback(languages: CallbackLanguages, selectedDocId: Int) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    if (selectedDocId == docId) docMapper.getWorkingDocument(docId, languages.selected)
    else DefaultDocGetterCallback(languages).getDoc(docId, user, docMapper)
}

case class CustomDocGetterCallback(languages: CallbackLanguages, selectedDocId: Int, selectedDocVersionNo: Int) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    if (selectedDocId == docId) docMapper.getCustomDocument(DocRef.of(selectedDocId, selectedDocVersionNo), languages.selected)
    else DefaultDocGetterCallback(languages).getDoc(docId, user, docMapper)
}

case class CallbackLanguages(selected: I18nLanguage, default: I18nLanguage) {
  val selectedIsDefault = selected == default
}