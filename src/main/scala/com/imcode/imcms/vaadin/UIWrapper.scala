package com.imcode
package imcms.vaadin

import javax.servlet.http.HttpSession
import javax.servlet.ServletContext
import java.net.URL
import com.vaadin.server.WrappedHttpSession


/* implicit */
class UIWrapper(ui: com.vaadin.ui.UI) {

  //def context: WebApplicationContext = app.getContext.asInstanceOf[WebApplicationContext]

  def session: HttpSession = ui.getSession.getSession.asInstanceOf[WrappedHttpSession].getHttpSession

  def servletContext: ServletContext = session.getServletContext

  // todo: fix
  def resourceUrl(resourcePath: String): URL = ???
//    (null : URL) |> { appUrl =>
//    new URL(appUrl.getProtocol, appUrl.getHost, appUrl.getPort, s"{$servletContext.getContextPath}/$resourcePath")
//  }
}