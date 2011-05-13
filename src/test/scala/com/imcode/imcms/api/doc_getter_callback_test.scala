package com.imcode
package imcms.api

import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test.Base.{db}
import imcode.server.user.UserDomainObject
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import javax.servlet.http.HttpServletRequest
import imcode.server.{Imcms, ImcmsConstants}
import imcms.test.fixtures.{LanguagesFX, UserFX}

@RunWith(classOf[JUnitRunner])
class DocGetterCallbackSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

  Imcms.setI18nSupport(LanguagesFX.i18nSupport)

  test("default user - no params") {
    val request = mock(classOf[HttpServletRequest])
    val user = UserFX.user

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)) thenReturn null
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)) thenReturn null
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)) thenReturn null

    DocGetterCallbackUtil.createAndSetDocGetterCallback(request, user)
    assertDGC[DefaultDocGetterCallback](user.getDocGetterCallback)
  }

  test("default user changing language through request param") {
    val request = mock(classOf[HttpServletRequest])
    val user = UserFX.user

    Imcms.getI18nSupport.setDefaultLanguage(LanguagesFX.english)

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)) thenReturn LanguagesFX.swedish.getCode
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)) thenReturn null
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)) thenReturn null

    DocGetterCallbackUtil.createAndSetDocGetterCallback(request, user)
    assertDGC[DefaultDocGetterCallback](user.getDocGetterCallback)

    assertEquals(LanguagesFX.swedish, user.getDocGetterCallback.getLanguage)
  }

  test("default user requesting working version") {
    val request = mock(classOf[HttpServletRequest])
    val user = UserFX.user

    Imcms.getI18nSupport.setDefaultLanguage(LanguagesFX.english)

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)) thenReturn null
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)) thenReturn "1001"
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)) thenReturn "0"

    DocGetterCallbackUtil.createAndSetDocGetterCallback(request, user)
    assertDGC[DefaultDocGetterCallback](user.getDocGetterCallback)
  }

  test("default user requesting custom version") {
    val request = mock(classOf[HttpServletRequest])
    val user = UserFX.user

    Imcms.getI18nSupport.setDefaultLanguage(LanguagesFX.english)

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)) thenReturn null
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)) thenReturn "1001"
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)) thenReturn "2"

    DocGetterCallbackUtil.createAndSetDocGetterCallback(request, user)
    assertDGC[DefaultDocGetterCallback](user.getDocGetterCallback)
  }

  test("power user requesting working version") {
    val request = mock(classOf[HttpServletRequest])
    val user = UserFX.admin

    Imcms.getI18nSupport.setDefaultLanguage(LanguagesFX.english)

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)) thenReturn null
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)) thenReturn "1001"
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)) thenReturn "0"

    DocGetterCallbackUtil.createAndSetDocGetterCallback(request, user)

    assertDGC[WorkingDocGetterCallback](user.getDocGetterCallback)
    val gdc = user.getDocGetterCallback.asInstanceOf[WorkingDocGetterCallback]

    assertEquals(1001, gdc.docId)
  }

  test("power user requesting custom version") {
    val request = mock(classOf[HttpServletRequest])
    val user = UserFX.admin

    Imcms.getI18nSupport.setDefaultLanguage(LanguagesFX.english)

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)) thenReturn null
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)) thenReturn "1001"
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)) thenReturn "2"

    DocGetterCallbackUtil.createAndSetDocGetterCallback(request, user)

    assertDGC[CustomDocGetterCallback](user.getDocGetterCallback)
    val gdc = user.getDocGetterCallback.asInstanceOf[CustomDocGetterCallback]

    assertEquals(1001, gdc.docId)
    assertEquals(2, gdc.docVersionNo)
  }

  def assertDGC[A <: DocGetterCallback](dgc: DocGetterCallback)(implicit mf: Manifest[A]) {
    assertNotNull(dgc)
    assertEquals(mf.erasure, dgc.getClass)
  }
}