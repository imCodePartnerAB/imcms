package com.imcode
package imcms.vaadin

import com.vaadin.terminal.Resource

/** Tree item descriptor */
class TreeMenuItem(val id: String = null, val icon: Resource = null) {
  val children: Seq[TreeMenuItem] = {
    val isMenuItemType: Class[_] => Boolean = classOf[TreeMenuItem].isAssignableFrom

    getClass.getDeclaredMethods
      .filter(_.getReturnType |> isMenuItemType)
      .sortBy(_.getAnnotation(classOf[OrderedMethod]) |> opt map(_.value()) getOrElse 0)
      .map(_.invoke(this).asInstanceOf[TreeMenuItem])
  }
}