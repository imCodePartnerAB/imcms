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
import imcms.test.fixtures.UserFX.{admin}
import imcms.test.Project.{testDB}
import com.imcode.imcms.test.config.AbstractHibernateConfig
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.api.{MenuHistory}

@RunWith(classOf[JUnitRunner])
class MenuDaoSuite extends FunSuite with MustMatchers with BeforeAndAfterAll with BeforeAndAfter {

	var menuDao: MenuDao = _

  val menuNos = 1 to 4

  override def beforeAll() = testDB.recreate()

  before {
    val ctx = Project.spring.createCtx(classOf[MenuDaoSuiteConfig])

    menuDao = ctx.getBean(classOf[MenuDao])

    testDB.runScripts("src/test/resources/sql/text_doc_menu_dao.sql")
  }

  def menu(docId: JInteger = DocFX.defaultId, docVersionNo: JInteger = VersionFX.defaultNo, no: JInteger = DocItemFX.defaultNo, assertExists: Boolean = true) =
    doto(menuDao.getMenu(docId, docVersionNo, no)) { menu =>
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
    doto(menuDao.getMenus(docId, docVersionNo)) { menus =>
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
    defaultMenu() |> { menu =>
      menuDao.deleteMenu(menu)
    }

    menu(assertExists = false) must be (null)
  }


  test("delete all menus") {
    menuDao.deleteMenus(DocFX.defaultId, VersionFX.defaultNo)

    menus(assertNotEmpty = false) |> { menus =>
      menus must be ('empty)
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
      Project.hibernate.configurators.Hbm2ddlAutoCreateDrop,
      Project.hibernate.configurators.BasicWithSql,
      Project.hibernate.configurators.addAnnotatedClasses(classOf[MenuDomainObject], classOf[MenuHistory]),
      Project.hibernate.configurators.addXmlFiles("com/imcode/imcms/hbm/Menu.hbm.xml")
    ))
}