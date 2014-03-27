package com.imcode
package imcms
package admin.docadmin

import javax.servlet._
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import _root_.imcode.util.Utility
import _root_.imcode.server.ImcmsConstants

class DocAdminFilter extends Filter with Slf4jLoggerSupport {

  override def init(filterConfig: FilterConfig) {}

  override def destroy() {}

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    doFilter(request.asInstanceOf[HttpServletRequest], response.asInstanceOf[HttpServletResponse], chain)
  }

  def doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
    val session = request.asInstanceOf[HttpServletRequest].getSession(false)
    val user = if (session == null) null else Utility.getLoggedOnUser(session)

    if (user == null || user.isDefaultUser) {
      Utility.forwardToLogin(request, response)
    } else {
      val pathInfo = request.getPathInfo
      val returnUrlOpt = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL).trimToOption
      val titleOpt = request.getParameter("label").trimToOption

      chain.doFilter(request, response)
    }
  }
}