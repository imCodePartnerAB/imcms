package com.imcode
package imcms.vaadin

import _root_.javax.servlet.http.HttpSession
import javax.servlet.ServletContext

import com.vaadin.server._
import com.vaadin.ui.{JavaScript, UI}
import imcode.server.user.UserDomainObject
import imcode.util.Utility

object Current {

  def vaadinServlet: VaadinServlet = VaadinServlet.getCurrent
  def vaadinSession: VaadinSession = VaadinSession.getCurrent
  def vaadinService: VaadinService = VaadinService.getCurrent

  def ui: UI = UI.getCurrent
  def page: Page = Page.getCurrent
  def javaScript: JavaScript = JavaScript.getCurrent
  def webBrowser: WebBrowser = page.getWebBrowser

  def httpSession: HttpSession = vaadinSession.getSession.asInstanceOf[WrappedHttpSession].getHttpSession
  def servletContext: ServletContext = vaadinServlet.getServletContext
  def contextPath: String = servletContext.getContextPath

  def imcmsUser: UserDomainObject = Utility.getLoggedOnUser(httpSession)
}
