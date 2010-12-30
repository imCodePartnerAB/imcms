package com.imcode
package imcms.admin.access.ip

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcode.server.document.{CategoryDomainObject}
import com.vaadin.ui.Window.Notification
import imcms.admin.filesystem._
import com.vaadin.terminal.FileResource
import java.io.File
import imcms.security.{PermissionGranted, PermissionDenied}
import imcode.server.user.{RoleId, RoleDomainObject}