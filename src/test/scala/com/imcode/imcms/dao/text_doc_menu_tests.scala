package com.imcode
package imcms.dao

import imcms.api.MenuHistory
import imcode.server.document.textdocument.{TreeSortKeyDomainObject, MenuItemDomainObject, MenuDomainObject}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test._
import imcms.test.fixtures.UserFX.{admin}
import imcms.test.fixtures.{DocFX, VersionFX}
import imcms.test.Base.{db}
import org.springframework.orm.hibernate3.HibernateTemplate

@RunWith(classOf[JUnitRunner])
class MenuDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

	var menuDao: MenuDao = _

  val menuNos = 0 to 4

  override def beforeAll() = withLogFailure { db.recreate() }

  override def beforeEach() = withLogFailure {
    val sf = db.createHibernateSessionFactory(Seq(classOf[MenuDomainObject], classOf[MenuHistory]),
               "src/main/resources/com/imcode/imcms/hbm/Menu.hbm.xml")

    menuDao = new MenuDao
    menuDao.hibernateTemplate = new HibernateTemplate(sf)

    db.runScripts("src/test/resources/sql/menu_dao.sql")
  }

  def getMenu(docId: JInteger = DocFX.defaultId, docVersionNo: JInteger = VersionFX.defaultNo, no: JInteger = 0, assertExists: Boolean = true) =
    letret(menuDao.getMenu) { menu =>
      if (assertExists) menu must not be (null)
    }


  def getMenus(docId: JInteger = DocFX.defaultId, docVersionNo: JInteger = VersionFX.defaultNo, assertExists: Boolean = true) =
    letret(menuDao.getMenus(docId, docVersionNo)) { menus =>
      if (assertExists) menus must not be ('empty)
    }


  test("get all [4] text doc's menus") {
    val menus = getMenus()
    menus must have size (4)

    for (menu <- menus) {
      menu.getItemsMap must have size (menu.getNo)
    }
  }


  test("create and save new text doc's menu") {
    val menu = new MenuDomainObject
    menu.setDocId(DocFX.defaultId)
    menu.setDocVersionNo(VersionFX.defaultNo)
    menu.setNo(menuNos.max + 1)
    menu.setSortOrder(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE)

    val mi = new MenuItemDomainObject
    mi.setSortKey(2)
    mi.setTreeSortKey(new TreeSortKeyDomainObject("3"))

    menu.getItemsMap().put(DocFX.defaultId, mi)

    menuDao.saveMenu(menu)

    let(menuDao.getMenus(DocFX.defaultId, VersionFX.defaultNo)) { menus =>
      menus must have size (menuNos.size + 1)
    }

    val menuHistory = new MenuHistory(menu, admin)
    menuDao.saveMenuHistory(menuHistory)
  }


  test("delete one existing menu") {
    val no = 0

    let(getMenu()) { menu =>
      menuDao.deleteMenu(menus.get(0))
    }

    let(menuDao.getMenus(DocFX.defaultId, VersionFX.defaultNo)) { menus =>
      menus mast have size (menuNos.size - 1)
    }
  }


  test("delete all text doc's menus") {
    menuDao.deleteMenus(DocFX.defaultId, VersionFX.defaultNo)

    let(menuDao.getMenus(DocFX.defaultId, VersionFX.defaultNo)) { menus =>
      menus mast be ('empty)
    }
  }
}