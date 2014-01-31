package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.orm.{MenuHistory, DocRef}
import scala.collection.JavaConverters._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite, BeforeAndAfterAll}
import com.imcode.imcms.test.fixtures.{DocItemFX, DocFX, VersionFX}
import com.imcode.imcms.test.fixtures.UserFX.mkSuperAdmin
import com.imcode.imcms.test.TestSetup
import com.imcode.imcms.test.config.HibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import org.junit.Assert._
import _root_.imcode.server.document.textdocument._

@RunWith(classOf[JUnitRunner])
class MenuDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfterEach {

	var textDocDao: TextDocDao = _

  val menuNos = 1 to 4

  override def beforeAll() = TestSetup.db.recreate()

  override def beforeEach() {
    val ctx = TestSetup.spring.createCtx(classOf[MenuDaoSuiteConfig])

    textDocDao = ctx.getBean(classOf[TextDocDao])

    TestSetup.db.runScripts("src/test/resources/sql/text_doc_menu_dao.sql")
  }

  def menu(docId: Int = DocFX.DefaultId, docVersionNo: Int = VersionFX.DefaultNo, no: JInteger = DocItemFX.DefaultNo, assertExists: Boolean = true) =
    textDocDao.getMenu(DocRef.of(docId, docVersionNo), no) |>> { menu =>
      if (assertExists) {
        assertNotNull("menu exists", menu)
        assertEquals("docRef", DocRef.of(docId, docVersionNo), menu.getDocRef)
        assertEquals("no", docVersionNo, menu.getNo)
      }
    }


  def menus(docId: JInteger = DocFX.DefaultId, docVersionNo: JInteger = VersionFX.DefaultNo, assertNotEmpty: Boolean = true) =
    textDocDao.getMenus(DocRef.of(docId, docVersionNo)) |>> { _.asScala |> { menus =>
      if (assertNotEmpty) {
        assertTrue("menus exist", menus.nonEmpty)
        menus.foreach { menu =>
          assertEquals("docRef", DocRef.of(docId, docVersionNo), menu.getDocRef)
        }
      }}
    }

  def defaultMenu() = menu()
  def defaultMenus() = menus().asScala


  test("get all menus") {
    val menus = defaultMenus()
    assertEquals("menus count", 4, menuNos.size)

    for (menu <- menus) {
      expectResult(menu.getNo.intValue, "Items count in a menu") {
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
    menu.setDocRef(DocRef.of(DocFX.DefaultId, VersionFX.DefaultNo))
    menu.setNo(menuNos.max + 1)
    menu.setSortOrder(MenuDomainObject.MENU_SORT_ORDER__BY_HEADLINE)

    val mi = new MenuItemDomainObject
    mi.setSortKey(2)
    mi.setTreeSortKey(new TreeSortKeyDomainObject("3"))

    menu.getItemsMap().put(DocFX.DefaultId, mi)

    textDocDao.saveMenu(menu)

    defaultMenus() |> { menus => assertEquals("menus count", menuNos.size + 1, menus.size) }

    val menuHistory = new MenuHistory(menu, mkSuperAdmin)
    textDocDao.saveMenuHistory(menuHistory)
  }

  test("delete existing menu") {
    defaultMenu() |> { menu =>
      textDocDao.deleteMenu(menu)
    }

    menu(assertExists = false) |> assertNull
  }


  test("delete all menus") {
    textDocDao.deleteMenus(DocRef.of(DocFX.DefaultId, VersionFX.DefaultNo))

    menus(assertNotEmpty = false) |> { menus =>
      assertTrue("menus do not exist",  menus.isEmpty)
    }
  }
}


@Import(Array(classOf[HibernateConfig]))
class MenuDaoSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def dao = new TextDocDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Function.chain(Seq(
      TestSetup.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      TestSetup.hibernate.configurators.BasicWithSql,
      TestSetup.hibernate.configurators.addAnnotatedClasses(classOf[MenuDomainObject], classOf[MenuHistory]),
      TestSetup.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/Menu.hbm.xml")
    ))
}