package com.imcode
package imcms.dao

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.collection.JavaConverters._
import org.springframework.beans.factory.annotation.Autowire
import com.imcode.imcms.test.{TestSetup, withLogFailure}
import org.springframework.context.annotation.{Bean, Import}
import com.imcode.imcms.test.config.HibernateConfig
import org.scalatest._


@RunWith(classOf[JUnitRunner])
class NativeQueriesSuite extends WordSpec with BeforeAndAfterEach with BeforeAndAfterAll {

  val MimeTypeCountInEveryLanguage = 17

  var nativeQueriesDao: NativeQueriesDao = _

  override def beforeAll() = withLogFailure {
    TestSetup.db.recreate()
    TestSetup.db.runScripts("src/test/resources/sql/native_queries_dao.sql")

    nativeQueriesDao = TestSetup.spring.createCtx(classOf[NativeQueriesSuiteConfig]) |> {
      _.getBean(classOf[NativeQueriesDao])
    }
  }


  ".getAllMimeTypes" in {
    val mimeTypes = nativeQueriesDao.getAllMimeTypes()

    expectResult(MimeTypeCountInEveryLanguage * 2, "Mime type count") {
      mimeTypes.size()
    }
  }


  ".getAllMimeTypesWithDescriptions" in {
    for (language <- Seq("eng", "swe")) {
      val names = for (Array(name, description) <- nativeQueriesDao.getAllMimeTypesWithDescriptions(language).asScala) yield name
      assert(names.size === MimeTypeCountInEveryLanguage, "Mime type count in %s".format(language))
    }
  }


  ".getParentDocumentAndMenuIdsForDocument" in {
    val menusItems = nativeQueriesDao.getParentDocumentAndMenuIdsForDocument(1001).asScala
    val docMenus = menusItems.foldLeft(Map.empty[Int, Seq[Int]].withDefaultValue(Seq.empty)) {
      case (map, Array(docId, menuId)) => docId.toInt |> { id =>
        map.updated(id, map(id) :+ menuId.toInt)
      }
    }

    expectResult(3, "Menu items count")(docMenus.size)
  }


  ".getDocumentsWithPermissionsForRole" in {
    expectResult(1001) {
      nativeQueriesDao.getDocumentsWithPermissionsForRole(2).get(0)
    }
  }


  ".getAllDocumentTypeIdsAndNamesInUsersLanguage" in {
    nativeQueriesDao.getAllDocumentTypeIdsAndNamesInUsersLanguage("eng")
  }


  ".getDocumentMenuPairsContainingDocument" in {
    nativeQueriesDao.getDocumentMenuPairsContainingDocument(1001)
  }
}


@Import(Array(classOf[HibernateConfig]))
class NativeQueriesSuiteConfig {

  @Bean(autowire = Autowire.BY_TYPE)
  def nativeQueriesDao = new NativeQueriesDao

  @Bean
  def hibernatePropertiesConfigurator: org.hibernate.cfg.Configuration => org.hibernate.cfg.Configuration =
    TestSetup.hibernate.configurators.BasicWithSql
}