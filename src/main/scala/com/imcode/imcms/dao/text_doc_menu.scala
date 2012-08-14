package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.MenuHistory
import org.hibernate.{ScrollMode, CacheMode}
import imcode.server.document.textdocument.{DocIdentity, MenuDomainObject}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class MenuDao extends HibernateSupport {

  def getMenu(docIdentity: DocIdentity, no: Int): MenuDomainObject = hibernate.getByNamedQueryAndNamedParams(
    "Menu.getMenu", "docIdentity" -> docIdentity, "no" -> no
  )

  
  def getMenus(docIdentity: DocIdentity): JList[MenuDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Menu.getMenus", "docIdentity" -> docIdentity
    )

  
  def saveMenu(menu: MenuDomainObject): MenuDomainObject = {
    for ((_, menuItem) <- menu.getItemsMap.asScala)
      menuItem.setTreeSortIndex(menuItem.getTreeSortKey.toString)

    hibernate.saveOrUpdate(menu)
  }

  
  def saveMenuHistory(menuHistory: MenuHistory) = hibernate.save(menuHistory)

  
  def deleteMenus(docIdentity: DocIdentity) = hibernate.withCurrentSession { session =>
    val scroll = session.getNamedQuery("Menu.getMenus")
      .setParameter("docIdentity", docIdentity)
      .setCacheMode(CacheMode.IGNORE)
      .scroll(ScrollMode.FORWARD_ONLY)

    var count = 0
    while (scroll.next) {
      session.delete(scroll.get(0))
      count += 1
      if (count % 25 == 0) {
        session.flush()
        session.clear()
      }
    }

    session.flush()

    count
  }

  
  def deleteMenu(menu: MenuDomainObject) = hibernate.delete(menu)
}
