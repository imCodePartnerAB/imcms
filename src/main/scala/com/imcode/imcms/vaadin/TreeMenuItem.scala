package com.imcode
package imcms.vaadin

import java.lang.reflect.{Method, Modifier}
import com.vaadin.server.Resource

/** Tree item descriptor */
class TreeMenuItem(val id: String = null, val icon: Resource = null) {
  val children: Seq[TreeMenuItem] = {
    val treeMenuItemClass = classOf[TreeMenuItem]
    def treeMenuItemMethod(method: Method) =
      treeMenuItemClass.isAssignableFrom(method.getReturnType) && Modifier.isPublic(method.getModifiers)

    getClass.getDeclaredMethods
      .filter(treeMenuItemMethod)
      .sortBy(_.getAnnotation(classOf[MenuItemOrder]).asOption.map(_.value()).getOrElse(0))
      .map(_.invoke(this).asInstanceOf[TreeMenuItem])
  }
}