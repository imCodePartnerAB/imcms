package com.imcode
package imcms.dao

import imcode.server.user.UserDomainObject
import imcms.api.MenuHistory
import imcode.server.document.textdocument.{TreeSortKeyDomainObject, MenuItemDomainObject, MenuDomainObject}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test.Base.{db}

@RunWith(classOf[JUnitRunner])
//todo: Test named queries
class MenuDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

	var menuDao: MenuDao = _

  val ADMIN = new UserDomainObject(0)

  override def beforeAll() = db.recreate()

  override def beforeEach() {
    val sf = db.createHibernateSessionFactory(Seq(classOf[MenuDomainObject], classOf[MenuHistory]),
               "src/main/resources/com/imcode/imcms/hbm/Menu.hbm.xml")

    db.runScripts("src/test/resources/sql/menu_dao.sql")

    menuDao = new MenuDao
    menuDao.setSessionFactory(sf)
  }


  test("get all text doc's menus") {
    val menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 3)
  }


  test("create and save new text doc's menu") {
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


  test("delete existing text doc's menu") {
    var menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 3)

    menuDao.deleteMenu(menus.get(0))

    menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 2)
  }


  test("delete all text doc's menus") {
    var menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 3)

    menuDao.deleteMenus(1001, 0)

    menus = menuDao.getMenus(1001, 0)
    assertEquals(menus.size, 0)
  }
}