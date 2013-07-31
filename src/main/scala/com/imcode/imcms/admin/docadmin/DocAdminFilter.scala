package com.imcode.imcms.admin.docadmin

import javax.servlet._
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import _root_.imcode.util.Utility

class DocAdminFilter extends Filter {

  override def init(filterConfig: FilterConfig) {}

  override def destroy() {}

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val session = request.asInstanceOf[HttpServletRequest].getSession(false)
    val user = if (session == null) null else Utility.getLoggedOnUser(session)

    if (user != null && !user.isDefaultUser) chain.doFilter(request, response)
    else Utility.forwardToLogin(request.asInstanceOf[HttpServletRequest], response.asInstanceOf[HttpServletResponse])
  }
}