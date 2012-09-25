package com.imcode
package imcms.admin

import javax.servlet._
import imcode.util.Utility
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

class SysAdminFilter extends Filter {

  def init(filterConfig: FilterConfig) {}

  def destroy() {}

  def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val session = request.asInstanceOf[HttpServletRequest].getSession(false)
    val user = if (session == null) null else Utility.getLoggedOnUser(session)

    if (user != null && user.canAccessAdminPages) chain.doFilter(request, response)
    else Utility.forwardToLogin(request.asInstanceOf[HttpServletRequest], response.asInstanceOf[HttpServletResponse])
  }
}