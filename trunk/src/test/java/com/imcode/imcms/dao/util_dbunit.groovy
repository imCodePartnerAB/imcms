/* 
 * Used to load data sets from databse.
 */
package com.imcode.imcms.dao

import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.database.QueryDataSet
import java.sql.Connection
import java.sql.DriverManager
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection

def dataSource = Context.getBean("dataSource")
def jdbcConnection = dataSource.connection
def resourcesPath = "src/test/resources/"
def outFilePath = resourcesPath + "dbunit-test-data.xml"
def tables = ["i18n_meta"]

try {
	def connection = new DatabaseConnection(jdbcConnection)
	def dataSet = new QueryDataSet(connection)
	
	tables.each {
		dataSet.addTable it
	}
	
	FlatXmlDataSet.write dataSet, new FileOutputStream(outFilePath)
	
	IDataSet fullDataSet = connection.createDataSet();
	FlatXmlDataSet.write(fullDataSet, new FileOutputStream(resourcesPath + "full.xml"));	
} finally {
	jdbcConnection.close()
}