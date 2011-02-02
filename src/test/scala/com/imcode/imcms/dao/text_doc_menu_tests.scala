package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import imcms.api.MenuHistory
import imcode.server.document.textdocument.{TreeSortKeyDomainObject, MenuItemDomainObject, MenuDomainObject}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import imcms.test._
import fixtures.{DocItemFX, DocFX, VersionFX}
import imcms.test.fixtures.UserFX.{admin}
import imcms.test.Base.{db}
import org.springframework.orm.hibernate3.HibernateTemplate

@RunWith(classOf[JUnitRunner])
class MenuDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfterEach {

	var menuDao: MenuDao = _

  val menuNos = 1 to 4

  override def beforeAll() = withLogFailure { db.recreate() }

  override def beforeEach() = withLogFailure {
    val sf = db.createHibernateSessionFactory(Seq(classOf[MenuDomainObject], classOf[MenuHistory]),
               "src/main/resources/com/imcode/imcms/hbm/Menu.hbm.xml")

    menuDao = new MenuDao
    menuDao.hibernateTemplate = new HibernateTemplate(sf)

    db.runScripts("src/test/resources/sql/text_doc_menu_dao.sql")
  }

  def menu(docId: JInteger = DocFX.defaultId, docVersionNo: JInteger = VersionFX.defaultNo, no: JInteger = DocItemFX.defaultNo, assertExists: Boolean = true) =
    letret(menuDao.getMenu(docId, docVersionNo, no)) { menu =>
      if (assertExists) {
        menu must not be (null)
        menu must have (
          'docId (docId),
          'docVersionNo (docVersionNo),
          'no (no)
        )
      }
    }


  def menus(docId: JInteger = DocFX.defaultId, docVersionNo: JInteger = VersionFX.defaultNo, assertNotEmpty: Boolean = true) =
    letret(menuDao.getMenus(docId, docVersionNo)) { menus =>
      if (assertNotEmpty) {
        menus must not be ('empty)
        menus foreach { menu =>
          menu must have (
            'docId (docId),
            'docVersionNo (docVersionNo)
          )
        }
      }
    }

  def defaultMenu() = menu()
  def defaultMenus() = menus()


  test("get all [4] menus") {
    val menus = defaultMenus()
    menus must have size (menuNos.size)

    for (menu <- menus) {
      expect(menu.getNo.intValue, "Items count in a menu") {
        menu.getItemsMap.size
      }
    }
  }

  test("get existing menu") {
    defaultMenu()
  }

  test("get missing menu") {
    menu(no = DocItemFX.missingNo, assertExists = false) must be ('null)
  }

  test("get missing menus") {
    menus(docId = DocFX.missingId, assertNotEmpty = false) must be ('empty)
  }

  test("save new menu") {
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

    defaultMenus() must have size (menuNos.size + 1)

    val menuHistory = new MenuHistory(menu, admin)
    menuDao.saveMenuHistory(menuHistory)
  }

  test("delete existing menu") {
    let(defaultMenu()) { menu =>
      menuDao.deleteMenu(menu)
    }

    menu(assertExists = false) must be (null)
  }


  test("delete all menus") {
    menuDao.deleteMenus(DocFX.defaultId, VersionFX.defaultNo)

    let(menus(assertNotEmpty = false)) { menus =>
      menus must be ('empty)
    }
  }
}