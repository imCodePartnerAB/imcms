package com.imcode
package imcms.api

import com.imcode.imcms.mapping.DocumentMapper
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import javax.servlet.http.HttpServletRequest
import imcode.server.{Imcms, ImcmsConstants}


object DocGetterCallbackUtil {

  /** Creates callback and associates it with a current user. */
  def createAndSetDocGetterCallback(request: HttpServletRequest, user: UserDomainObject) {
    val currentDocGetterCallback = user.getDocGetterCallback
    val defaultLanguage = Imcms.getI18nSupport.getDefaultLanguage
    val language = ?(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE))
                   .map(Imcms.getI18nSupport.getByCode)
                   .orElse(?(currentDocGetterCallback) map (_.params.language))
                   .orElse(?(Imcms.getI18nSupport.getForHost(request.getServerName)))
                   .getOrElse(defaultLanguage)

    val params = Params(user, language, defaultLanguage)
    val docGetterCallback =
      (for {
        docIdentity <- ?(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID))
        docVersionNoStr <- ?(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION))
        if !user.isDefaultUser
      } yield {
        val docId = ?(Imcms.getServices.getDocumentMapper.toDocumentId(docIdentity)).getOrElse {
          error("Document with identity %s does not exists." format docIdentity)
        }

        Integer.parseInt(docVersionNoStr) match {
          case DocumentVersion.WORKING_VERSION_NO => WorkingDocGetterCallback(params, docId)
          case docVersionNo => CustomDocGetterCallback(params, docId, docVersionNo)
        }
      }) getOrElse {
        currentDocGetterCallback match {
          case docGetterCallback: CustomDocGetterCallback => docGetterCallback.copy(params)
          case docGetterCallback: WorkingDocGetterCallback => docGetterCallback.copy(params)
          case _ => DefaultDocGetterCallback(params)
        }
      }

    user.setDocGetterCallback(docGetterCallback)
  }
}


/**
 * Parametrized callback for DocumentMapper#getDocument method.
 * A callback is (re)created on each request and (re)assigned to a user session object.
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
  val params: Params
  def getDoc(docMapper: DocumentMapper, docId: JInteger): DocumentDomainObject

  // legacy code support
  def getParams = params
  def getUser = params.user
  def getLanguage = params.language
  def getDefaultLanguage = params.defaultLanguage
}

case class DefaultDocGetterCallback(params: Params) extends DocGetterCallback {
  def getDoc(docMapper: DocumentMapper, docId: JInteger) =
    docMapper.getDefaultDocument(docId, params.language) match {
      case doc if doc != null && !params.languageIsDefault && !params.user.isSuperAdmin =>
        val meta = doc.getMeta

        if (!meta.getLanguages.contains(params.language)) {
          if (meta.getDisabledLanguageShowSetting == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE)
            docMapper.getDefaultDocument(docId)
          else
            null
        } else doc

      case doc => doc
    }
}

case class WorkingDocGetterCallback(params: Params, docId: JInteger) extends DocGetterCallback {
  def getDoc(docMapper: DocumentMapper, docId: JInteger) =
    if (this.docId == docId) docMapper.getWorkingDocument(docId, params.language)
    else DefaultDocGetterCallback(params).getDoc(docMapper, docId)
}

case class CustomDocGetterCallback(params: Params, docId: JInteger, docVersionNo: JInteger) extends DocGetterCallback {
  def getDoc(docMapper: DocumentMapper, docId: JInteger) =
    if (this.docId == docId) docMapper.getCustomDocument(docId, docVersionNo, params.language)
    else DefaultDocGetterCallback(params).getDoc(docMapper, docId)
}

case class Params(user: UserDomainObject, language: I18nLanguage, defaultLanguage: I18nLanguage) {
  val languageIsDefault = language == defaultLanguage
}