package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.MenuHistory
import org.hibernate.{ScrollMode, CacheMode}

import imcode.server.document.textdocument.MenuDomainObject

@Transactional(rollbackFor = Array(classOf[Throwable]))
class MenuDao extends HibernateSupport {

  def getMenu(docId: Int, docVersionNo: Int, no: Int): MenuDomainObject = hibernate.getByNamedQueryAndNamedParams(
    "Menu.getMenu", "docId" -> docId, "docVersionNo" -> docVersionNo, "no" -> no
  )

  
  def getMenus(docId: Int, docVersionNo: Int): JList[MenuDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Menu.getMenus", "docId" -> docId, "docVersionNo" -> docVersionNo
    )

  
  def saveMenu(menu: MenuDomainObject): MenuDomainObject = {
    for ((_, menuItem) <- menu.getItemsMap)
      menuItem.setTreeSortIndex(menuItem.getTreeSortKey.toString)

    hibernate.saveOrUpdate(menu)
  }

  
  def saveMenuHistory(menuHistory: MenuHistory) = hibernate.save(menuHistory)

  
  def deleteMenus(docId: Int, docVersionNo: Int) = hibernate.withCurrentSession { session =>
    val scroll = session.getNamedQuery("Menu.getMenus")
      .setParameter("docId", docId)
      .setParameter("docVersionNo", docVersionNo)
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
