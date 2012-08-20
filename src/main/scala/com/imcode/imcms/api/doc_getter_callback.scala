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
  def createAndSetDocGetterCallback(request: HttpServletRequest, services: ImcmsServices, user: UserDomainObject) {
    val currentDocGetterCallback = user.getDocGetterCallback
    val i18nSupport = services.getI18nSupport
    val defaultLanguage = i18nSupport.getDefaultLanguage
    val language = Option(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE))
                   .map(i18nSupport.getByCode)
                   .orElse(currentDocGetterCallback |> opt map (_.state.selectedLanguage))
                   .orElse(i18nSupport.getForHost(request.getServerName) |> opt)
                   .getOrElse(defaultLanguage)

    val state = State(language, defaultLanguage)
    val docGetterCallback =
      (for {
        docRef <- Option(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID))
        docVersionNoStr <- Option(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION))
        if !user.isDefaultUser
      } yield {
        val docId: Int = docRef match {
          case IntNum(n) => n
          case _ =>
            Imcms.getServices.getDocumentMapper.toDocumentId(docRef) |> opt getOrElse {
              sys.error("Document with identity %s does not exists." format docRef)
            }
        }

        Integer.valueOf(docVersionNoStr) match {
          case DocumentVersion.WORKING_VERSION_NO => WorkingDocGetterCallback(state, docId)
          case docVersionNo => CustomDocGetterCallback(state, docId, docVersionNo)
        }
      }) getOrElse {
        currentDocGetterCallback match {
          case docGetterCallback: CustomDocGetterCallback => docGetterCallback.copy(state)
          case docGetterCallback: WorkingDocGetterCallback => docGetterCallback.copy(state)
          case _ => DefaultDocGetterCallback(state)
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
  def state: State
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper): DocumentDomainObject

  // legacy code support
  def selectedLanguage = state.selectedLanguage
}

case class DefaultDocGetterCallback(state: State) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    docMapper.getDefaultDocument(docId, state.selectedLanguage) match {
      case doc if doc != null && !state.selectedLanguageIsDefault && user.isSuperAdmin =>
        val meta = doc.getMeta

        if (!meta.getLanguages.contains(state.selectedLanguage)) {
          if (meta.getDisabledLanguageShowSetting == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE)
            docMapper.getDefaultDocument(docId)
          else
            null
        } else doc

      case doc => doc
    }
}

case class WorkingDocGetterCallback(state: State, selectedDocId: Int) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    if (selectedDocId == docId) docMapper.getWorkingDocument(docId, state.selectedLanguage)
    else DefaultDocGetterCallback(state).getDoc(docId, user, docMapper)
}

case class CustomDocGetterCallback(state: State, selectedDocId: Int, selectedDocVersionNo: Int) extends DocGetterCallback {
  def getDoc(docId: Int, user: UserDomainObject, docMapper: DocumentMapper) =
    if (selectedDocId == docId) docMapper.getCustomDocument(DocRef.of(selectedDocId, selectedDocVersionNo), state.selectedLanguage)
    else DefaultDocGetterCallback(state).getDoc(docId, user, docMapper)
}

case class State(selectedLanguage: I18nLanguage, defaultLanguage: I18nLanguage) {
  val selectedLanguageIsDefault = selectedLanguage == defaultLanguage
}