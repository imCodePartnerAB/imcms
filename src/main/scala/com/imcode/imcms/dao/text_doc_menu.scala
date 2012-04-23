package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.MenuHistory
import org.hibernate.{ScrollMode, CacheMode}

import imcode.server.document.textdocument.MenuDomainObject

@Transactional(rollbackFor = Array(classOf[Throwable]))
class MenuDao extends HibernateSupport {

  //@Transactional
  def getMenu(docId: JInteger, docVersionNo: JInteger, no: JInteger) = hibernate.getByNamedQuery[MenuDomainObject](
    "Menu.getMenu", "docId" -> docId, "docVersionNo" -> docVersionNo, "no", no
  )

  //@Transactional
  def getMenus(docId: JInteger, docVersionNo: JInteger) = hibernate.listByNamedQueryAndNamedParams[MenuDomainObject](
    "Menu.getMenus", "docId" -> docId, "docVersionNo" -> docVersionNo
  )

  //@Transactional
  def saveMenu(menu: MenuDomainObject): MenuDomainObject = {
    for ((_, menuItem) <- menu.getItemsMap)
      menuItem.setTreeSortIndex(menuItem.getTreeSortKey.toString)

    hibernate.saveOrUpdate(menu)
  }


  //@Transactional
  def saveMenuHistory(menuHistory: MenuHistory) = hibernate.save(menuHistory)

  //@Transactional
  def deleteMenus(docId: JInteger, docVersionNo: JInteger) = hibernate.withSession { session =>
    val scroll = session.getNamedQuery("Menu.getMenus")
      .setParameter("docId", docId)
      .setParameter("docVersionNo", docVersionNo)
      .setCacheMode(CacheMode.IGNORE)
      .scroll(ScrollMode.FORWARD_ONLY)

    var count = 0
    while (scroll.next) {
      session.delete(scroll.get(0)) //get(0).asInstanceOf[MenuDomainObject])
      count += 1
      if (count % 25 == 0) {
        session.flush
        session.clear
      }
    }

    session.flush

    count
  }


  //@Transactional
  def deleteMenu(menu: MenuDomainObject) = hibernate.delete(menu)
}
