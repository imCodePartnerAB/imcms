package com.imcode.imcms.db

import javax.sql.DataSource
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate

import scala.collection.JavaConversions._
import java.sql.{ResultSet, Connection}
import com.ibatis.common.jdbc.ScriptRunner
import org.springframework.jdbc.core.{ConnectionCallback, RowMapper}
import java.io.FileReader
import com.imcode.Logger
import com.imcode._

class DB(ds: DataSource) extends Logger {
  
  val template = new SimpleJdbcTemplate(ds)

  def tables = template.query("SHOW TABLES", new RowMapper[String] {
    def mapRow(rs: ResultSet, rowNum: Int) = rs getString 1
  }).toList


  def isEmpty = tables.isEmpty
  

  def version(): Version = template.queryForObject("""SELECT concat(major, '.', minor) FROM database_version""",
                                                   classOf[String])


  def updateVersion(newVersion: Version) {
    logger.info("Updating database version from %s to %s.".format(version(), newVersion))
    template.update("UPDATE database_version SET major=?, minor=?", Int.box(newVersion.major),
                                                                    Int.box(newVersion.minor))
  }


  /**
   * Throws an RuntimeException if database version is greater than required.
   */
  def prepare(schema: Schema): Version = {
    def scriptFullPath(script: String) = "%s/%s".format(schema.scriptsDir, script)

    logger.info("Preparing databse.")

    if (isEmpty) {
      logger.info("Database is empty and need to be initialized.")
      logger.info("The following init will be applied: %s." format schema.init)

      runScripts(schema.init.scripts map scriptFullPath)
      updateVersion(schema.init.version)

      logger.info("Database has been initialized.")
    }

    version() match {
      case schema.version =>
        logger.info("Database is up-to-date.");
        schema.version
      
      case dbVersion if dbVersion < schema.version =>
        logger.info("Database have to be updated. Required version: %s, database version: %s."
                    .format(schema.version, dbVersion))

        for (diff <- schema diffsChain dbVersion) {
          logger.info("The following diff will be applied: %s." format diff)

          runScripts(diff.scripts map scriptFullPath)
          updateVersion(diff.to)
        }

        val updatedDbVersion = version()
        logger.info("Database has been updated. Database version: %s." format updatedDbVersion)
        updatedDbVersion      

      case unexpectedDbVersion =>
        val errorMsg = "Unexpected database version. Database version: %s is greater than required version: %s."
                       .format(unexpectedDbVersion, schema.version)

        logger error errorMsg
        error(errorMsg)
    }
  }

  
  def runScripts(scripts: List[String]) {
    template.getJdbcOperations.execute(new ConnectionCallback[Unit] {
      def doInConnection(connection: Connection) {
        val scriptRunner = new ScriptRunner(ds.getConnection, false, true)

        for (script <- scripts) {
          logger.debug("Running script %s." format script)

          using(new FileReader(script)) { reader =>
            scriptRunner runScript reader
          }
        }
      }
    })    
  }
}