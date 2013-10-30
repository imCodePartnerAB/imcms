package com.imcode
package imcms
package mapping

import _root_.net.sf.ehcache.{Cache, Element, Ehcache}
import _root_.net.sf.ehcache.config.CacheConfiguration

case class CacheWrapper[K >: Null, V >: Null](cache: Ehcache) {

  // Compiles, but Intellij can't infer
  // Option(cache.get(key)).map(_.getObjectValue).orNull.asInstanceOf[V]
  def get(key: K): V =
    cache.get(key) |> {
      case null => null
      case element => element.getObjectValue
    } |> { _.asInstanceOf[V] }

  def put(key: K, value: V): Unit = cache.put(new Element(key, value))

  def remove(key: K): Boolean = cache.remove(key)

  def getOrPut(key: K)(compute: => V): V =
    get(key) match {
      case value if value != null => value
      case _ => compute |>> { value => put(key, value) }
    }
}


object CacheWrapper {
  def apply[K >: Null, V >: Null](cacheConfiguration: CacheConfiguration) =
    new CacheWrapper[K, V](new Cache(cacheConfiguration))
}
