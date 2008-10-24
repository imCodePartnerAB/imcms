package com.imcode.imcms.dao

import org.dbunit.DataSourceDatabaseTesterimport org.dbunit.database.DatabaseConnectionimport org.dbunit.DefaultDatabaseTesterimport org.dbunit.operation.DatabaseOperationimport org.dbunit.dataset.xml.FlatXmlDataSetimport org.testng.annotations.BeforeClassimport org.testng.annotations.AfterMethodimport org.testng.annotations.BeforeMethodimport org.testng.annotations.BeforeTest
import static org.junit.Assert.*
public abstract class DaoTest {
	
	static final dataSetRootPath = "src/test/resources/"
	
	def tester
		
	@BeforeClass void initTester() {
        def dataSource = Context.getBean("dataSourceWithAutoCommit")
        def dataSetFilePath = dataSetRootPath +  dataSetFileName
        def dataSet = new FlatXmlDataSet(new FileReader(dataSetFilePath))
        
        tester = new DataSourceDatabaseTester(dataSource)
        tester.setSetUpOperation DatabaseOperation.REFRESH
        tester.setDataSet dataSet
	}
		
	
    @BeforeMethod final void refreshDatabase() {
    	tester.onSetup()
    }
	
		
	@AfterMethod final void afterMethod() {
		tester.onTearDown()
	}
	
	abstract getDataSetFileName();
}