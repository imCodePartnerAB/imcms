package com.imcode.imcms.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class SchemaVersionChecker {
	
	public String SQL_SELECT_SCHEMA_VERSION = "SELECT concat(" +
			"cast(major as char), '.', cast(minor as char)) " +
			"FROM database_version";
	
	private  HibernateTemplate hibernateTemplate;
	
	/**
	 * Checks if schema a version stored in a file 
	 * matches to a schema version stored in a database.
	 * 
	 * Schema version stored in file has the format major.minor where major and
	 * minor are digits. 
	 * 
	 * @param schemaVersionFilePath schema version file.
	 * @throws SchemaVersionCheckerException in case of failure.
	 */
	public void checkSchemaVersion(String schemaVersionFilePath) {
		try {
			String expectedVersion = FileUtils.readFileToString(
					new File(schemaVersionFilePath)).trim();
			
			String realVersion = getSchemaVersion().trim();
			
			if (!expectedVersion.equals(realVersion)) { 
				throw new SchemaVersionCheckerException(String.format(
						"Versions mismatch. Expected version: [%s], real version: [%s].", 
						realVersion, expectedVersion));
			}
		} catch (SchemaVersionCheckerException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new SchemaVersionCheckerException(String.format(
					"Schema version file [%s] is not found.", schemaVersionFilePath)
					, e);
		} catch (Exception e) {
			throw new SchemaVersionCheckerException("Uncategorized error.", e);
		}
	}
		
	public String getSchemaVersion() {
		return (String)hibernateTemplate.execute(new HibernateCallback() {
			public Object doInHibernate(Session session) 
			throws HibernateException, SQLException {				
				return session.createSQLQuery(SQL_SELECT_SCHEMA_VERSION)
							  .uniqueResult();
			}			
		});
	}	

	
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
}
