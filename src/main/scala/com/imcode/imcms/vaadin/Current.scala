package com.imcode
package imcms.vaadin

import com.vaadin.server._
import com.vaadin.ui.UI
import javax.servlet.http.HttpSession
import javax.servlet.ServletContext

object Current {

  def vaadinServlet: VaadinServlet = VaadinServlet.getCurrent
  def vaadinSession: VaadinSession = VaadinSession.getCurrent
  def vaadinService: VaadinService = VaadinService.getCurrent

  def ui: UI = Current.ui
  def page: Page = Current.page

  def httpSession: HttpSession = vaadinSession.getSession.asInstanceOf[WrappedHttpSession].getHttpSession
  def servletContext: ServletContext = vaadinServlet.getServletContext
  def contextPath: String = servletContext.getContextPath
}
