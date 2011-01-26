package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.MenuHistory;
import imcode.server.document.textdocument.MenuDomainObject;

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
	def deleteMenus(docId: JInteger, docVersionNo: JInteger) = {
        // bulk delete problem - see comments on "Menu.deleteMenus"
        //        return getSession().getNamedQuery("Menu.deleteMenus")
        //                .setParameter("docId", docId)
        //                .setParameter("docVersionNo", docVersionNo)
        //                .executeUpdate();
    val count = hibernateTemplate.findByNamedQueryAndNamedParam("Menu.getMenus",
      Array("docId", "docVersionNo"),
      Array[AnyRef](docId, docVersionNo)).asInstanceOf[JList[MenuDomainObject]].map(deleteMenu).size

    flush()
    count
	}


	@Transactional
	def deleteMenu(menu: MenuDomainObject) = hibernateTemplate.delete(menu)
}
