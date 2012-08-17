package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.MenuHistory
import org.hibernate.{ScrollMode, CacheMode}
import imcode.server.document.textdocument.{DocRef, MenuDomainObject}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class MenuDao extends HibernateSupport {

  def getMenu(docRef: DocRef, no: Int): MenuDomainObject = hibernate.getByNamedQueryAndNamedParams(
    "Menu.getMenuByDocRefAndNo", "docRef" -> docRef, "no" -> no
  )

  
  def getMenus(docRef: DocRef): JList[MenuDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Menu.getMenusByDocRef", "docRef" -> docRef
    )

  
  def saveMenu(menu: MenuDomainObject): MenuDomainObject = {
    for ((_, menuItem) <- menu.getItemsMap.asScala)
      menuItem.setTreeSortIndex(menuItem.getTreeSortKey.toString)

    hibernate.saveOrUpdate(menu)
  }

  
  def saveMenuHistory(menuHistory: MenuHistory) = hibernate.save(menuHistory)

  
  def deleteMenus(docRef: DocRef) = hibernate.withCurrentSession { session =>
    val scroll = session.getNamedQuery("Menu.getMenusByDocRef")
      .setParameter("docRef", docRef)
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