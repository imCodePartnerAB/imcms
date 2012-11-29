package com.imcode
package imcms.vaadin

import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.Application
import javax.servlet.http.HttpSession
import javax.servlet.ServletContext
import java.net.URL


/* implicit */
class ApplicationWrapper(app: Application) {

  def context: WebApplicationContext = app.getContext.asInstanceOf[WebApplicationContext]

  def session: HttpSession = context.getHttpSession

  def servletContext: ServletContext = session.getServletContext

  def resourceUrl(resourcePath: String): URL = app.getURL |> { appUrl =>
    new URL(appUrl.getProtocol, appUrl.getHost, appUrl.getPort, "%s/%s".format(servletContext.getContextPath, resourcePath))
  }
}