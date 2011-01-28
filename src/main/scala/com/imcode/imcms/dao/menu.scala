package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.MenuHistory
import org.hibernate.{ScrollMode, CacheMode}

import imcode.server.document.textdocument.MenuDomainObject

class MenuDao extends SpringHibernateTemplate {

  @Transactional
  def getMenus(docId: JInteger, docVersionNo: JInteger) =
    hibernateTemplate.findByNamedQueryAndNamedParam("Menu.getMenus",
      Array("docId", "docVersionNo"),
      Array[AnyRef](docId, docVersionNo)).asInstanceOf[JList[MenuDomainObject]]

  @Transactional
  def saveMenu(menu: MenuDomainObject) = {
    for ((_, menuItem) <- menu.getItemsMap)
      menuItem.setTreeSortIndex(menuItem.getTreeSortKey.toString)

    letret(menu) { hibernateTemplate.saveOrUpdate }
  }


  @Transactional
  def saveMenuHistory(menuHistory: MenuHistory) = letret(menuHistory) { hibernateTemplate.save }

  @Transactional
  def deleteMenus(docId: JInteger, docVersionNo: JInteger) = withSession { session =>
    val scroll = session.getNamedQuery("Menu.getMenus")
      .setParameter("docId", docId)
      .setParameter("docVersionNo", docVersionNo)
      .setCacheMode(CacheMode.IGNORE)
      .scroll(ScrollMode.FORWARD_ONLY)

    var count = 0
    while (scroll.next) {
      session.delete(scroll.get(0).asInstanceOf[MenuDomainObject])
      count += 1
      if (count % 25 == 0) {
        session.flush
        session.clear
      }
    }

    count
  }


  @Transactional
  def deleteMenu(menu: MenuDomainObject) = hibernateTemplate.delete(menu)
}
