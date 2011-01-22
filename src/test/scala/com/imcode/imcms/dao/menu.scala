package com.imcode
package imcms.dao

import org.scalatest.junit.JUnitSuite
import org.scalatest.BeforeAndAfterAll
import org.junit.{Before, Test}

import com.imcode.imcms.test.DB
import com.imcode.imcms.test.Project

import org.junit.Assert._
import imcode.server.user.UserDomainObject
import imcms.api.MenuHistory
import imcode.server.document.textdocument.{TreeSortKeyDomainObject, MenuItemDomainObject, MenuDomainObject}

//todo: Test named queries
class MenuDaoSuite extends JUnitSuite with BeforeAndAfterAll {

	var menuDao: MenuDao = _

  val ADMIN = new UserDomainObject(0)

  override def beforeAll {
    val project = Project()
    val db = new DB(project)

    db.recreate()
  }


  @Before
  def resetDBData() {
    val project = Project()
    val db = new DB(project)

    val sf = db.createHibernateSessionFactory(Seq(classOf[MenuDomainObject], classOf[MenuHistory]),
              "src/main/resources/com/imcode/imcms/hbm/Menu.hbm.xml")

    db.runScripts("src/test/resources/sql/menu_dao.sql")

    menuDao = new MenuDao
    menuDao.setSessionFactory(sf)
  }


  @Test def getMenus() {
    val menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 3)
  }


  @Test def insertMenu() {
    var menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 3)

    var menu = new MenuDomainObject
    menu.setDocId(1001)
    menu.setDocVersionNo(0)
    menu.setNo(4)
    menu.setSortOrder(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE)

    val mi = new MenuItemDomainObject
    mi.setSortKey(2)
    mi.setTreeSortKey(new TreeSortKeyDomainObject("3"))

    menu.getItemsMap().put(1001, mi)

    menuDao.saveMenu(menu)

    menus = menuDao.getMenus(1001, 0)

    assertEquals(menus.size, 4)

    val menuHistory = new MenuHistory(menu, ADMIN)
    menuDao.saveMenuHistory(menuHistory)
  }


  @Test def deleteMenu() {
    var menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 3)

    menuDao.deleteMenu(menus.get(0))

    menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 2)
  }


  @Test def deleteMenus() {
    var menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 3)

    menuDao.deleteMenus(1001, 0)

    menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 0)
  }
}