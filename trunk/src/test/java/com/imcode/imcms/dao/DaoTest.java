package com.imcode.imcms.dao;

import java.io.FileReader;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public abstract class DaoTest {
	
	static final String dataSetRootPath = "src/test/resources/";
	
	DataSourceDatabaseTester tester;
		
	@BeforeClass public void initTester() throws Exception {
        DataSource dataSource = (DataSource)Utils.getBean("dataSourceWithAutoCommit");
        String dataSetFilePath = dataSetRootPath + getDataSetFileName();
        FlatXmlDataSet dataSet = new FlatXmlDataSet(new FileReader(dataSetFilePath));
        
        tester = new DataSourceDatabaseTester(dataSource);
        //tester.setSetUpOperation DatabaseOperation.REFRESH
        tester.setDataSet(dataSet);
	}
		
	
    @BeforeMethod public final void refreshDatabase() throws Exception {
    	tester.onSetup();
    }
	
		
	@AfterMethod public final void afterMethod() throws Exception {
		tester.onTearDown();
	}
	
	protected abstract String getDataSetFileName();
}