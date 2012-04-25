package com.imcode
package imcms.dao

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import scala.collection.JavaConversions._
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.{Project, withLogFailure}
import org.springframework.context.annotation.{Bean, Import}
import com.imcode.imcms.test.config.{AbstractHibernateConfig}


@RunWith(classOf[JUnitRunner])
class NativeQueriesSuite extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  val MimeTypeCountInEveryLanguage = 17

  var nativeQueriesDao: NativeQueriesDao = _

  override def beforeAll() = withLogFailure {
    Project.testDB.recreate()
    Project.testDB.runScripts("src/test/resources/sql/native_queries_dao.sql")

    nativeQueriesDao = Project.spring.createCtx(classOf[NativeQueriesSuiteConfig]) |> {
      _.getBean(classOf[NativeQueriesDao])
    }
  }


  test("getAllMimeTypes") {
    val mimeTypes = nativeQueriesDao.getAllMimeTypes()

    expect(MimeTypeCountInEveryLanguage * 2, "Mime type count") {
      mimeTypes.size()
    }
  }


  test("getAllMimeTypesWithDescriptions") {
    for (language <- Seq("eng", "swe")) {
      val names = for (Array(name, description) <- nativeQueriesDao.getAllMimeTypesWithDescriptions(language)) yield name
      assert(names.size === MimeTypeCountInEveryLanguage, "Mime type count in %s".format(language))
    }
  }


  test("getParentDocumentAndMenuIdsForDocument") {
    val menusItems = nativeQueriesDao.getParentDocumentAndMenuIdsForDocument(1001)
    val docMenus = menusItems.foldLeft(Map.empty[Int, Seq[Int]].withDefaultValue(Seq.empty)) {
      case (map, Array(docId, menuId)) => docId.toInt |> { id =>
        map.updated(id, map(id) :+ menuId.toInt)
      }
    }

    expect(3, "Menu items count")(docMenus.size)
  }


  test("getDocumentsWithPermissionsForRole") {
    expect(1001) {
      nativeQueriesDao.getDocumentsWithPermissionsForRole(2)
    }
  }


  test("getAllDocumentTypeIdsAndNamesInUsersLanguage") {
    nativeQueriesDao.getAllDocumentTypeIdsAndNamesInUsersLanguage("eng")
  }


  test("getDocumentMenuPairsContainingDocument") {
    nativeQueriesDao.getDocumentMenuPairsContainingDocument(1001)
  }
}


@Import(Array(classOf[AbstractHibernateConfig]))
class NativeQueriesSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def nativeQueriesDao = new NativeQueriesDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    Project.hibernate.configurators.BasicWithSql
}