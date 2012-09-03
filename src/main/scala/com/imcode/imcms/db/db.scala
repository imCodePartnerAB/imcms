package com.imcode
package imcms.db

import javax.sql.DataSource
import org.springframework.jdbc.core.JdbcTemplate

import scala.collection.JavaConverters._
import java.sql.{ResultSet, Connection}
import org.apache.ibatis.jdbc.ScriptRunner
import org.springframework.jdbc.core.{ConnectionCallback, RowMapper}
import java.io.FileReader

class DB(ds: DataSource) extends Slf4jLoggerSupport {
  
  val jdbcTemplate = new JdbcTemplate(ds)

  def tables(): List[String] = jdbcTemplate.query("SHOW TABLES", new RowMapper[String] {
    def mapRow(rs: ResultSet, rowNum: Int) = rs getString 1
  }).asScala.toList

  def isNew(): Boolean = tables().isEmpty

  def version(): Version = jdbcTemplate.queryForObject("""SELECT concat(major, '.', minor) FROM database_version""", classOf[String])

  def updateVersion(newVersion: Version): Unit = synchronized {
    logger.info("Updating database version from %s to %s.".format(version(), newVersion))
    jdbcTemplate.update("UPDATE database_version SET major=?, minor=?", Int.box(newVersion.major), Int.box(newVersion.minor))
  }

  def prepare(schema: Schema): Version = synchronized {
    def scriptFullPath(script: String) = "%s/%s".format(schema.scriptsDir, script)

    def update(): Either[String, Version] = {
      version() match {
        case schema.version =>
          logger.info("Database is up-to-date.");
          Right(schema.version)

        case dbVersion if dbVersion < schema.version =>
          logger.info("Database have to be updated. Required version: %s, database version: %s."
                      .format(schema.version, dbVersion))

          schema diffsChain dbVersion match {
            case Nil =>
              Left("No diff is available for version %s." format dbVersion)

            case diffsChain =>
              for (diff <- diffsChain) {
                logger.info("The following diff will be applied: %s." format diff)

                runScripts(diff.scripts map scriptFullPath)
                updateVersion(diff.to)
              }

              val updatedDbVersion = version()
              logger.info("Database has been updated. Database version: %s." format updatedDbVersion)
              Right(updatedDbVersion)
          }

        case unexpectedDbVersion =>
          Left("Unexpected database version. Database version: %s is greater than required version: %s.".format(unexpectedDbVersion, schema.version))
      }
    } // update

    logger.info("Preparing databse.")
    if (isNew()) {
      logger.info("Database is empty and need to be initialized.")
      logger.info("The following init will be applied: %s." format schema.init)

      runScripts(schema.init.scripts map scriptFullPath)
      updateVersion(schema.init.version)

      logger.info("Database has been initialized.")
    }

    update() match {
      case Left(errorMsg) =>
        logger.error(errorMsg)
        sys.error(errorMsg)

      case Right(version) =>
        version
    }
  }

  def runScripts(scripts: Seq[String]): Unit = synchronized {
    jdbcTemplate.execute(new ConnectionCallback[Unit] {
      def doInConnection(connection: Connection) {
        val scriptRunner = new ScriptRunner(connection) |>> { sr =>
          sr.setAutoCommit(false)
          sr.setStopOnError(true)
        }

        for (script <- scripts) {
          logger.debug("Running script %s." format script)

          using(new FileReader(script)) { scriptRunner runScript _ }
        }
      }
    })
  }
}