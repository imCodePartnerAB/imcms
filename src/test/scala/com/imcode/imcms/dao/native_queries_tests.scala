package com.imcode
package imcms.dao

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import imcms.test.withLogFailure
import imcms.test.Base.{db}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import scala.collection.JavaConversions._

@RunWith(classOf[JUnitRunner])
class NativeQueriesSuite extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  var nativeQueriesDao: NativeQueriesDao = _

  //override def beforeAll() = withLogFailure { db.prepare(true) }

  before {
    val sf = db.createHibernateSessionFactory()
    nativeQueriesDao = new NativeQueriesDao |< { _.sessionFactory = sf }
    nativeQueriesDao.hibernate.withSession(_.beginTransaction())
  }

  after {
    nativeQueriesDao.hibernate.withSession(_.getTransaction.commit())
  }

  test("get All Mime Types") {
    val mimeTypes = nativeQueriesDao.getAllMimeTypes()
  }

  test("get All Document Type Ids And Names In Users Language") {
    nativeQueriesDao.getAllDocumentTypeIdsAndNamesInUsersLanguage("eng")
  }

  test("get All Mime Types With Descriptions") {
    for (Array(name, description) <- nativeQueriesDao.getAllMimeTypesWithDescriptions("eng")) {
      println(">>> %s -> %s".format(name, description))
    }
  }

  test("a test") {
    val x: JList[Array[String]] = nativeQueriesDao.hibernate.listBySqlQuery(
      "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id", "eng"
    )
  }
}