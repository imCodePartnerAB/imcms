package com.imcode.imcms.vaadin

import com.vaadin.server.Page

package object server {

  implicit def wrapPage(page: Page) = new PageWrapper(page)
}
