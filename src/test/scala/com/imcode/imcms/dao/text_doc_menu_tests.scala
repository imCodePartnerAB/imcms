package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
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
import imcode.server.document.textdocument._

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

  def menu(docId: Int = DocFX.DefaultId, docVersionNo: Int = VersionFX.DefaultNo, no: JInteger = DocItemFX.DefaultNo, assertExists: Boolean = true) =
    menuDao.getMenu(new DocIdentity(docId, docVersionNo), no) |>> { menu =>
      if (assertExists) {
        assertNotNull("menu exists", menu)
        assertEquals("docIdentity", new DocIdentity(docId, docVersionNo), menu.getDocIdentity)
        assertEquals("no", docVersionNo, menu.getNo)
      }
    }


  def menus(docId: JInteger = DocFX.DefaultId, docVersionNo: JInteger = VersionFX.DefaultNo, assertNotEmpty: Boolean = true) =
    menuDao.getMenus(new DocIdentity(docId, docVersionNo)) |>> { _.asScala |> { menus =>
      if (assertNotEmpty) {
        assertTrue("menus exist", menus.nonEmpty)
        menus foreach { menu =>
          assertEquals("docIdentity", new DocIdentity(docId, docVersionNo), menu.getDocIdentity)
        }
      }}
    }

  def defaultMenu() = menu()
  def defaultMenus() = menus().asScala


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
    menu(no = DocItemFX.VacantNo, assertExists = false) |> assertNull
  }

  test("get non-existing menus") {
    menus(docId = DocFX.VacantId, assertNotEmpty = false) |> { menus => assertTrue("menus do not exist", menus.isEmpty) }
  }

  test("save new menu") {
    val menu = new MenuDomainObject
    menu.setDocIdentity(new DocIdentity(DocFX.DefaultId, VersionFX.DefaultNo))
    menu.setNo(menuNos.max + 1)
    menu.setSortOrder(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE)

    val mi = new MenuItemDomainObject
    mi.setSortKey(2)
    mi.setTreeSortKey(new TreeSortKeyDomainObject("3"))

    menu.getItemsMap().put(DocFX.DefaultId, mi)

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
    menuDao.deleteMenus(new DocIdentity(DocFX.DefaultId, VersionFX.DefaultNo))

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