package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import imcode.server.document.textdocument.{TreeSortKeyDomainObject, MenuItemDomainObject, MenuDomainObject}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfter, FunSuite, BeforeAndAfterAll}
import imcms.test._
import fixtures.{DocItemFX, DocFX, VersionFX}
import imcms.test.fixtures.UserFX.{mkSuperAdmin}
import imcms.test.Test.{db}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.api.{MenuHistory}
import org.junit.Assert._

@RunWith(classOf[JUnitRunner])
class MenuDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

	var menuDao: MenuDao = _

  val menuNos = 1 to 4

  override def beforeAll() = db.recreate()

  before {
    val ctx = Test.spring.createCtx(classOf[MenuDaoSuiteConfig])

    menuDao = ctx.getBean(classOf[MenuDao])

    db.runScripts("src/test/resources/sql/text_doc_menu_dao.sql")
  }

  def menu(docId: JInteger = DocFX.defaultId, docVersionNo: JInteger = VersionFX.defaultNo, no: JInteger = DocItemFX.defaultNo, assertExists: Boolean = true) =
    menuDao.getMenu(docId, docVersionNo, no) |>> { menu =>
      if (assertExists) {
        assertNotNull("menu exists", menu)
        assertEquals("docId", docId, menu.getDocId)
        assertEquals("getDocVersionNo", docVersionNo, menu.getDocVersionNo)
        assertEquals("no", docVersionNo, menu.getNo)
      }
    }


  def menus(docId: JInteger = DocFX.defaultId, docVersionNo: JInteger = VersionFX.defaultNo, assertNotEmpty: Boolean = true) =
    menuDao.getMenus(docId, docVersionNo) |>> { menus =>
      if (assertNotEmpty) {
        assertTrue("menus exist", menus.nonEmpty)
        menus foreach { menu =>
          assertEquals("docId", docId, menu.getDocId)
          assertEquals("getDocVersionNo", docVersionNo, menu.getDocVersionNo)
        }
      }
    }

  def defaultMenu() = menu()
  def defaultMenus() = menus()


  test("get all [4] menus") {
    val menus = defaultMenus()
    assertEquals("menus count", 4, menuNos.size)

    for (menu <- menus) {
      expect(menu.getNo.intValue, "Items count in a menu") {
        menu.getItemsMap.size
      }
    }
  }

  test("get existing menu") {
    defaultMenu()
  }

  test("get non-existing menu") {
    menu(no = DocItemFX.vacantNo, assertExists = false) |> assertNull
  }

  test("get non-existing menus") {
    menus(docId = DocFX.vacantId, assertNotEmpty = false) |> { menus => assertTrue("menus do not exist", menus.isEmpty) }
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

    defaultMenus() |> { menus => assertEquals("menus count", menuNos.size + 1, menus.size) }

    val menuHistory = new MenuHistory(menu, mkSuperAdmin)
    menuDao.saveMenuHistory(menuHistory)
  }

  test("delete existing menu") {
    defaultMenu() |> { menu =>
      menuDao.deleteMenu(menu)
    }

    menu(assertExists = false) |> assertNull
  }


  test("delete all menus") {
    menuDao.deleteMenus(DocFX.defaultId, VersionFX.defaultNo)

    menus(assertNotEmpty = false) |> { menus =>
      assertTrue("menus do not exist",  menus.isEmpty)
    }
  }
}


@Import(Array(classOf[AbstractHibernateConfig]))
class MenuDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def menuDao = new MenuDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      Test.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Test.hibernate.configurators.BasicWithSql,
      Test.hibernate.configurators.addAnnotatedClasses(classOf[MenuDomainObject], classOf[MenuHistory]),
      Test.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/Menu.hbm.xml")
    ))
}