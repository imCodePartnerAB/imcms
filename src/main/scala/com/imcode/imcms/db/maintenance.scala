package com.imcode.imcms.db

import javax.sql.DataSource
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate

import scala.collection.JavaConversions._
import java.sql.{ResultSet, Connection}
import com.ibatis.common.jdbc.ScriptRunner
import org.springframework.jdbc.core.{ConnectionCallback, RowMapper}
import java.io.FileReader
import com.imcode.imcms.logger.Logger

class DB(ds: DataSource) extends Logger {
  
  val template = new SimpleJdbcTemplate(ds)

  def tables = template.query("SHOW TABLES",
    new RowMapper[String] {
      def mapRow(rs: ResultSet, rowNum: Int) = rs.getString(1)
    }).toList


  def isEmpty = tables.isEmpty
  

  def version(): Version = template.queryForObject(
                                """SELECT concat(major, '.', minor) FROM database_version""",
                                classOf[String])


  def updateVersion(newVersion: Version) {
    logger.info("Updating database version from %s to %s." format (version(), newVersion))
    template.update("UPDATE database_version SET major=?, minor=?", newVersion.major.asInstanceOf[AnyRef],
                                                                    newVersion.minor.asInstanceOf[AnyRef])
  }

  
  def prepare(schema: Schema): Version = {
    def scriptFullPath(script: String) = "%s/%s" format (schema.scriptsDir, script)

    logger.info("Preparing databse.")

    if (isEmpty) {
      logger.info("Database is empty and need to be initialized.")
      logger.info("The following init will be applied: %s." format schema.init)

      runScripts(schema.init.scripts map scriptFullPath)
      updateVersion(schema.init.version)

      logger.info("Database has been initialized.")
    }

    
    version() match {
      case schema.version => logger.info("Database is up-to-date.")
      case currentVersion => logger.info("Database have to be updated. Required version: %s, current version: %s."
              format (schema.version, currentVersion))

        for (diff <- schema.diffsChain(currentVersion)) {
          logger.info("The following diff will be applied: %s." format diff)
          
          runScripts(diff.scripts map scriptFullPath)
          updateVersion(diff.to)
        }
    }


    val currentVersion = version()
    logger.info("Database has been prepared. Current version: %s." format currentVersion)
    currentVersion    
  }

  
  def runScripts(scripts: List[String]) {
    template.getJdbcOperations.execute(new ConnectionCallback[Unit] {
      def doInConnection(connection: Connection) {
        val scriptRunner = new ScriptRunner(ds.getConnection, false, true)

        for (script <- scripts) {
          logger.debug("Running script %s." format script)

          val reader = new FileReader(script)

          try
            scriptRunner.runScript(reader)
          finally
            reader.close
        }
      }
    })    
  }
}