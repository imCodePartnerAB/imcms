package com.imcode.imcms.dao;

import static com.imcode.imcms.dao.Utils.META_ID;
import static com.imcode.imcms.dao.Utils.languageDao;
import static com.imcode.imcms.dao.Utils.metaDao;
import static com.imcode.imcms.dao.Utils.textDao;
import imcode.server.document.textdocument.TextDomainObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.testng.annotations.Test;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.I18nLanguage;

public class TextDaoTest extends DaoTestG {
	
	/**
	 * Queries all texts for the same document.
	 */
	@Test
	public void getTextsForAllVersions() {
		I18nLanguage language = languageDao.getDefaultLanguage();
		
		List<DocumentVersion> versions = metaDao.getDocumentVersions(META_ID);
		List<TextDomainObject> texts = textDao.getTextsForAllVersions(META_ID, language);
				
		Set<Integer> documentVersions = (HashSet<Integer>)CollectionUtils.collect(versions, new Transformer() {
			public Object transform(Object object) {
				return ((DocumentVersion)object).getNumber();
			}
		}, new HashSet<Integer>());
		
		Set<Integer> textVersions = (HashSet<Integer>)CollectionUtils.collect(texts, new Transformer() {
			public Object transform(Object object) {
				return ((TextDomainObject)object).getMetaVersion();
			}
		}, new HashSet<Integer>());
		
		
	}
	
	
	/**
	 * Queries all texts for the same document.
	 */
	@Test
	public void getTextsForVersions() {
		I18nLanguage language = languageDao.getDefaultLanguage();
		
		List<DocumentVersion> versions = metaDao.getDocumentVersions(META_ID);
				
		Set<Integer> documentVersions = (HashSet<Integer>)CollectionUtils.collect(versions, new Transformer() {
			public Object transform(Object object) {
				return ((DocumentVersion)object).getNumber();
			}
		}, new HashSet<Integer>());
		
		List<TextDomainObject> texts = textDao.getTextsForVersions(META_ID, language,
				documentVersions);
		
		Set<Integer> textVersions = (HashSet<Integer>)CollectionUtils.collect(texts, new Transformer() {
			public Object transform(Object object) {
				return ((TextDomainObject)object).getMetaVersion();
			}
		}, new HashSet<Integer>());
		
		
	}	
	
	/**
	 * Queries all texts for the same document.
	 */
	@Test
	public void getTextsForVersionsInRange() {
		I18nLanguage language = languageDao.getDefaultLanguage();
		
		List<DocumentVersion> versions = metaDao.getDocumentVersions(META_ID);
				
		Set<Integer> documentVersions = (HashSet<Integer>)CollectionUtils.collect(versions, new Transformer() {
			public Object transform(Object object) {
				return ((DocumentVersion)object).getNumber();
			}
		}, new HashSet<Integer>());
		
		List<TextDomainObject> texts = textDao.getTextsForVersionsInRange(META_ID, 
				language, 
				versions.get(0).getNumber(),
				versions.get(versions.size() - 1).getNumber());

		Set<Integer> textVersions = (HashSet<Integer>)CollectionUtils.collect(texts, new Transformer() {
			public Object transform(Object object) {
				return ((TextDomainObject)object).getMetaVersion();
			}
		}, new HashSet<Integer>());
	}
	
	@Override
	protected String getDataSetFileName() {
		return "dbunit-texts-data.xml";
	}
}