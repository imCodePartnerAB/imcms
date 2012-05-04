package com.imcode.imcms.db

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.imcode.imcms.test.Project
import com.imcode.imcms.test.Project.{testDB}
import org.scalatest.{FunSpec, FunSuite}

@RunWith(classOf[JUnitRunner])
class DBTestSuite extends FunSpec {

  describe("new db") {
    testDB.recreate();

    val db = new DB(testDB.createDataSource())

    it("should be empty") {
      assert(db.isEmpty(), "empty")
      assert(db.tables().length === 0, "no tables")
    }

    it("should throw an exception on attempt to get a version") {
      intercept[Exception] {
        db.version()
      }
    }

    it("should throw an exception on attempt to update a version") {
      intercept[Exception] {
        db.updateVersion(Version(1, 0))
      }
    }
  }


  describe("prepare on 'new db'") {
      testDB.recreate()

      val scriptsDir = Project.path("src/main/web/WEB-INF/sql")
      val schema = Schema.load(Project.file("src/main/resources/schema.xml")).changeScriptsDir(scriptsDir)
      val db = new DB(testDB.createDataSource());

      it("should run all update scritps") {
        db.prepare(schema)
      }

      it ("should create all necessary tables") {
        assert(!db.isEmpty(), "not empty")
        assert(db.tables().size > 0, "has tables")
      }

      it ("should change database version to most recent schema version") {
        assert(schema.version === db.version())
      }
  }
}