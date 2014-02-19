package com.imcode
package imcms.api

import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import _root_.com.imcode.imcms.test.fixtures.{LanguageFX, UserFX}
import _root_.imcode.server.{ImcmsServices, ImcmsConstants}

import org.mockito.Mockito._
import javax.servlet.http.HttpServletRequest
import org.scalatest.mock.MockitoSugar
import scala.reflect.ClassTag

@RunWith(classOf[JUnitRunner])
class DocGetterCallbackSuite extends FunSuite {

  val i18nContentSupport = LanguageFX.mkI18nSupport()
  val services = mock(classOf[ImcmsServices])

  when(services.getDocumentLanguageSupport).thenReturn(i18nContentSupport)

  test("default user - no params") {
    val request = MockitoSugar.mock[HttpServletRequest]
    val user = UserFX.mkDefaultUser

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null)
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn(null)
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn(null)
    when(request.getServerName).thenReturn("127.0.0.1")

    DocGetterCallbacks.updateUserDocGetterCallback(request, services, user)
    assertDGC[DefaultDocGetterCallback](user.getDocGetterCallback)
  }

  test("default user changing language through request param") {
    val request = MockitoSugar.mock[HttpServletRequest]
    val user = UserFX.mkDefaultUser

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(LanguageFX.mkSwedish.getCode)
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn(null)
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn(null)

    DocGetterCallbacks.updateUserDocGetterCallback(request, services, user)
    assertDGC[DefaultDocGetterCallback](user.getDocGetterCallback)

    assertEquals(LanguageFX.mkSwedish, user.getDocGetterCallback.documentLanguages.preferred)
  }

  test("default user requesting working version") {
    val request = MockitoSugar.mock[HttpServletRequest]
    val user = UserFX.mkDefaultUser

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null)
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001")
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("0")
    when(request.getServerName).thenReturn("127.0.0.1")

    DocGetterCallbacks.updateUserDocGetterCallback(request, services, user)
    assertDGC[DefaultDocGetterCallback](user.getDocGetterCallback)
  }

  test("default user requesting custom version") {
    val request = MockitoSugar.mock[HttpServletRequest]
    val user = UserFX.mkDefaultUser

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null)
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001")
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("2")
    when(request.getServerName).thenReturn("127.0.0.1")

    DocGetterCallbacks.updateUserDocGetterCallback(request, services, user)
    assertDGC[DefaultDocGetterCallback](user.getDocGetterCallback)
  }

  test("power user requesting working version") {
    val request = MockitoSugar.mock[HttpServletRequest]
    val user = UserFX.mkSuperAdmin

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null)
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001")
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("0")
    when(request.getServerName).thenReturn("127.0.0.1")

    DocGetterCallbacks.updateUserDocGetterCallback(request, services, user)

    assertDGC[WorkingDocGetterCallback](user.getDocGetterCallback)
    val gdc = user.getDocGetterCallback.asInstanceOf[WorkingDocGetterCallback]

    assertEquals(1001, gdc.selectedDocId)
  }

  test("power user requesting custom version") {
    val request = MockitoSugar.mock[HttpServletRequest]
    val user = UserFX.mkSuperAdmin

    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE)).thenReturn(null)
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)).thenReturn("1001")
    when(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION)).thenReturn("2")
    when(request.getServerName).thenReturn("127.0.0.1")

    DocGetterCallbacks.updateUserDocGetterCallback(request, services, user)

    assertDGC[CustomDocGetterCallback](user.getDocGetterCallback)
    val gdc = user.getDocGetterCallback.asInstanceOf[CustomDocGetterCallback]

    assertEquals(1001, gdc.selectedDocId)
    assertEquals(2, gdc.selectedDocVersionNo)
  }

  def assertDGC[A <: DocGetterCallback](dgc: DocGetterCallback)(implicit mf: ClassTag[A]) {
    assertNotNull(dgc)
    assertEquals(mf.runtimeClass, dgc.getClass)
  }
}