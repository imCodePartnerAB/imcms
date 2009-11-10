package com.imcode.imcms.dao;

import static com.imcode.imcms.dao.Utils.META_ID;
import static com.imcode.imcms.dao.Utils.databaseDocumentGetter;
import static com.imcode.imcms.dao.Utils.documentSaver;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import imcode.server.DefaultImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.List;

import org.testng.annotations.Test;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionTag;
import com.imcode.imcms.mapping.CachingDocumentGetter;
import com.imcode.imcms.mapping.DocumentMapper;

public class MetaDaoTest extends DaoTestG {
	
	/**
	 * Supports only limited set of API. 
	 */
	DocumentMapper documentMapper;
	
	/**
	 * User with saving rights. 
	 */
	UserDomainObject user;
	
	{
		user = new UserDomainObject();
		user.setId(0);
		
		documentMapper = new DocumentMapper();
		
		documentMapper.setDocumentSaver(documentSaver);
		documentMapper.setCachingDocumentGetter(
			new CachingDocumentGetter(databaseDocumentGetter, 100)				
		);
		documentMapper.setDatabaseDocumentGetter(databaseDocumentGetter);
		
		DefaultImcmsServices services = new DefaultImcmsServices();
		services.setDocumentMapper(documentMapper);
	}

	/**
	 * Queries document's versions. 
	 */
	@Test
	public void getDocumentVersions() {
		List<DocumentVersion> versions = documentMapper.getDocumentVersions(META_ID);
		
		assertEquals(4, versions.size());
		
		assertEquals(versions.get(0).getVersionTag(), DocumentVersionTag.ARCHIVED);
		assertEquals(versions.get(1).getVersionTag(), DocumentVersionTag.PUBLISHED);
		assertEquals(versions.get(2).getVersionTag(), DocumentVersionTag.POSTPONED);
		assertEquals(versions.get(3).getVersionTag(), DocumentVersionTag.WORKING);
	}
	
	/**
	 * Queries working document. 
	 */
    /*
	@Test
	public void getWorkingDocument() {
		DocumentDomainObject document = databaseDocumentGetter.getWorkingDocument(META_ID);
		
		assertNotNull(document);
		assertTrue(document.getMeta().getVersion().getVersionTag() == DocumentVersionTag.WORKING);
	}
	*/
	
	/**
	 * Creates working version from existing published document and publishes it.
	 */
	@Test
	public void createWorkingVersionAndPublishIt() 
	throws Exception {
		// there should NOT be working version
		DocumentDomainObject workingDocument = documentMapper.getWorkingDocument(META_ID);		
		assertNull(workingDocument);
		
		// there should be published version
		DocumentDomainObject publishedDocument = documentMapper.getPublishedDocument(META_ID);		
		assertNotNull(publishedDocument);
		
		// create working version
		documentMapper.createWorkingDocument(META_ID, publishedDocument.getVersion().getNumber(), user);
		
		// now there should be a working version		
		workingDocument = documentMapper.getWorkingDocument(META_ID);		
		assertNotNull(workingDocument);		
		
		// Test version number change
		DocumentVersion publishedVersion = publishedDocument.getVersion();
		DocumentVersion workingVersion = workingDocument.getVersion();
		
		assertTrue(workingVersion.getNumber() == publishedVersion.getNumber() - 1);
				
		documentMapper.publishWorkingDocument(workingDocument, user);		
		// check if it was published and published version archived
		
		DocumentDomainObject archivedDocument = documentMapper.getDocument(META_ID, publishedVersion.getNumber());
		DocumentDomainObject publishedWorkingDocument = documentMapper.getPublishedDocument(META_ID);
		
		assertNotNull(archivedDocument);
		assertNotNull(publishedWorkingDocument);
		
		// Test version number change
		DocumentVersion archivedVersion = archivedDocument.getVersion();
		DocumentVersion publishedWorkingVersion = publishedWorkingDocument.getVersion();
		
		assertTrue(archivedVersion.getNumber() == publishedWorkingVersion.getNumber() - 1);
	}

	@Test
	public void getLatestDocumentVersion() {
		List<DocumentVersion> versions = documentMapper.getDocumentVersions(META_ID);
		DocumentVersion latestVersion = versions.get(versions.size() - 1);
		
		DocumentDomainObject latestDocumentVersion = documentMapper.getDocument(META_ID);		
		
		assertEquals(latestVersion.getNumber(), latestDocumentVersion.getVersion().getNumber());
				
	} 
	
	@Override
	protected String getDataSetFileName() {
		return "dbunit-meta-data.xml";
	}
}