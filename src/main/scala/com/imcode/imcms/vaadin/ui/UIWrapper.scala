package com.imcode
package imcms.vaadin.ui

import javax.servlet.http.HttpSession
import javax.servlet.ServletContext
import java.net.URL
import com.vaadin.server.{Page, VaadinServlet, VaadinService, WrappedHttpSession}
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils


/* implicit */
class UIWrapper(ui: com.vaadin.ui.UI) {

//  def context: WebApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)

  def session: HttpSession = ui.getSession.getSession.asInstanceOf[WrappedHttpSession].getHttpSession

  def servletContext: ServletContext = session.getServletContext

  def resourceUrl(resourcePath: String): URL = ui.getPage.getLocation |> { appUrl =>
    new URL(appUrl.getScheme, appUrl.getHost, appUrl.getPort, s"${servletContext.getContextPath}/$resourcePath")
  }

  def withLock(body: => Unit) {
    ui.getSession.lock()
    try {
      body
    } finally {
      ui.getSession.unlock()
    }
  }
}