package com.imcode.imcms.db

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.imcode.imcms.test.Test
import org.scalatest.{WordSpec}

@RunWith(classOf[JUnitRunner])
class DBTestSuite extends WordSpec {

  "new db" should {
    Test.db.recreate()

    val db = new DB(Test.db.createDataSource())

    "be empty" in {
      assert(db.isEmpty(), "empty")
      assert(db.tables().isEmpty, "no tables")
    }

    "throw an exception on attempt to get a version" in {
      intercept[Exception] {
        db.version()
      }
    }

    "throw an exception on attempt to update a version" in {
      intercept[Exception] {
        db.updateVersion(Version(1, 0))
      }
    }
  }


  "DB.prepare(schema) on 'new db'" should {
    Test.db.recreate()

    val scriptsDir = Test.path("src/main/web/WEB-INF/sql")
    val schema = Schema.load(Test.file("src/main/resources/schema.xml")).changeScriptsDir(scriptsDir)
    val db = new DB(Test.db.createDataSource());

    "run all update scritps" in {
      db.prepare(schema)
    }

    "create all necessary tables" in {
      assert(!db.isEmpty, "not empty")
      assert(db.tables().nonEmpty, "has tables")
    }

    "change database version to most recent schema version" in {
      assert(schema.version === db.version())
    }
  }
}