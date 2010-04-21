package com.imcode.imcms.dao;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.DocumentProperty;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.api.DocumentLabels;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.mapping.orm.FileReference;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.Include;
import com.imcode.imcms.mapping.orm.TemplateNames;
import com.imcode.imcms.mapping.orm.UrlReference;

public class MetaDao extends HibernateTemplate {

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

	/**
	 * @return Meta.
	 */
	@Transactional
	public Meta getMeta(Integer docId) {
		return (Meta)get(Meta.class, docId);
	}

	/**
	 * @return Labels.
	 */
	@Transactional
	public DocumentLabels getLabels(Integer docId, Integer docVersionNo, I18nLanguage language) {
		DocumentLabels labels = (DocumentLabels)getSession().createQuery("SELECT l FROM DocumentLabels l WHERE l.docId = :docId AND docVersionNo = :docVersionNo AND l.language.id = :languageId")
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .setParameter("languageId", language.getId())
                .uniqueResult();

        if (labels == null) {
            labels = new DocumentLabels();
            labels.setDocId(docId);
            labels.setDocVersionNo(docVersionNo);
            labels.setLanguage(language);
            labels.setHeadline("");
            labels.setMenuText("");
            labels.setMenuImageURL("");
        }

        return labels;
	}


    @Transactional
    public boolean insertPropertyIfNotExists(Integer docId, String name, String value) {
        DocumentProperty property = (DocumentProperty)getSession()
                .getNamedQuery("DocumentProperty.getProperty")
                .setParameter("docId", docId)
                .setParameter("name", name)
                .uniqueResult();

        if (property != null) {
            if (StringUtils.isNotBlank(property.getValue())) {
                return false;
            }
        } else {
            property = new DocumentProperty();
            property.setDocId(docId);
            property.setName(name);
        }

        property.setValue(value);

        saveOrUpdate(property);
        
        return true;
    }


	/**
	 * @return Labels.
	 */
	@Transactional
	public List<DocumentLabels> getLabels(Integer docId, Integer docVersionNo) {
		return (List<DocumentLabels>)getSession().createQuery("SELECT l FROM DocumentLabels l WHERE l.docId = :docId AND docVersionNo = :docVersionNo")
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .list();
	}


	@Transactional
	public void deleteLabels(Integer docId, Integer docVersionNo, Integer LanguageId) {
		getSession().createQuery("DELETE FROM DocumentLabels l WHERE l.docId = :docId AND l.docVersionNo = :docVersionNo and l.language.id = :languageId")
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .setParameter("languageId", LanguageId)
                .executeUpdate();
	}


    @Transactional
	public void saveMeta(Meta meta) {
		saveOrUpdate(meta);
	}


    @Transactional
    public DocumentLabels saveLabels(DocumentLabels labels) {
        String headline = labels.getHeadline();
        String text = labels.getMenuText();
        
        String headlineThatFitsInDB = headline.substring(0, Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
        String textThatFitsInDB = text.substring(0, Math.min(text.length(), META_TEXT_MAX_LENGTH - 1));

        labels.setHeadline(headlineThatFitsInDB);
        labels.setMenuText(textThatFitsInDB);

        saveOrUpdate(labels);

        return labels;
    }


    @Transactional
	public void deleteIncludes(Integer docId) {
		bulkUpdate("delete from Include i where i.metaId = ?", docId);
	}

 	@Transactional
	public void saveInclude(Include include) {
		saveOrUpdate(include);
	}


 	@Transactional
	public void deleteHtmlReference(Integer docId, Integer docVersionNo) {
		bulkUpdate("delete from HtmlReference r where r.docId = ? AND r.docVersionNo = ?", new Object [] {docId, docVersionNo});
	}

 	@Transactional
	public void deleteUrlReference(Integer docId, Integer docVersionNo) {
		bulkUpdate("delete from UrlReference r where r.docId = ? AND r.docVersionNo = ?", new Object [] {docId, docVersionNo});
	}

	@Transactional
	public void saveTemplateNames(TemplateNames templateNames) {
		merge(templateNames);
	}

	@Transactional
	public Collection<Include> getIncludes(Integer documentId) {
		return (Collection<Include>) find("select i from Include i where i.metaId = ?", documentId);
	}

	@Transactional
	public TemplateNames getTemplateNames(Integer docId) {
        return (TemplateNames)get(TemplateNames.class, docId);
	}

    @Transactional
    public int deleteTemplateNames(Integer docId) {
        return getSession().createQuery("DELETE FROM TemplateNames n WHERE n.metaId = :docId")
                .setParameter("docId", docId)
                .executeUpdate();
    }

	@Transactional
	public Collection<FileReference> getFileReferences(Integer docId, Integer docVersionNo) {
		return findByNamedQueryAndNamedParam("FileDoc.getReferences", new String [] {"docId", "docVersionNo"},
                new Object [] {docId, docVersionNo});
	}

	@Transactional
	public FileReference saveFileReference(FileReference fileRef) {
		saveOrUpdate(fileRef);
		
		return fileRef;
	}

	@Transactional
	public int deleteFileReferences(Integer docId, Integer docVersionNo) {
        return getSession().getNamedQuery("FileDoc.deleteAllReferences")
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .executeUpdate();
	}

	@Transactional
	public HtmlReference getHtmlReference(Integer docId, Integer docVersionNo) {
		return (HtmlReference)getSession().getNamedQuery("HtmlDoc.getReference")
		    .setParameter("docId", docId)
            .setParameter("docVersionNo", docVersionNo)
		    .uniqueResult();
	}	
	
	@Transactional
	public HtmlReference saveHtmlReference(HtmlReference reference) {
		saveOrUpdate(reference);
		
		return reference;
	}
	
	@Transactional
	public UrlReference getUrlReference(Integer docId, Integer docVersionNo) {
		return (UrlReference)getSession().getNamedQuery("UrlDoc.getReference")
		    .setParameter("docId", docId)
            .setParameter("docVersionNo", docVersionNo)
		    .uniqueResult();
	}

	@Transactional
	public UrlReference saveUrlReference(UrlReference reference) {
		merge(reference);
		
		return reference;
	}	
	
	
	@Transactional
	public Integer getDocumentIdByAlias(String alias) {
		return (Integer)getSession().getNamedQuery("DocumentProperty.getDocumentIdByAlias")
			.setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
			.setParameter("value", alias.toLowerCase())
			.uniqueResult();
	}
	
	@Transactional
	public List<String> getAllAliases() {
		return findByNamedQueryAndNamedParam(
				"DocumentProperty.getAllAliases", "name", 
				DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
	}	

    
	@Transactional
	public DocumentProperty getAliasProperty(String alias) {
		return (DocumentProperty)getSession().getNamedQuery("DocumentProperty.getAliasProperty")
			.setParameter("name", DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS)
			.setParameter("value", alias)
			.uniqueResult();
	}
    
	
	@Transactional
	public void deleteDocument(final Integer metaId) {
		String[] sqls = {
			"DELETE FROM document_categories WHERE meta_id = ?",
			"DELETE FROM imcms_text_doc_menu_items WHERE to_doc_id = ?",
			"DELETE FROM imcms_text_doc_menu_items WHERE menu_id IN (SELECT doc_id FROM imcms_text_doc_menus WHERE doc_id = ?)",
			"DELETE FROM imcms_text_doc_menus WHERE doc_id = ?", 
			"DELETE FROM text_docs WHERE meta_id = ?", 
			"DELETE FROM imcms_text_doc_texts WHERE doc_id = ?", 
			"DELETE FROM imcms_text_doc_images WHERE doc_id = ?", 
			"DELETE FROM roles_rights WHERE meta_id = ?", 
			"DELETE FROM user_rights WHERE meta_id = ?", 
			"DELETE FROM imcms_url_docs WHERE doc_id = ?",
			"DELETE FROM fileupload_docs WHERE meta_id = ?", 
			"DELETE FROM imcms_html_docs WHERE doc_id = ?", 
			"DELETE FROM new_doc_permission_sets_ex WHERE meta_id = ?", 
			"DELETE FROM new_doc_permission_sets WHERE meta_id = ?", 
			"DELETE FROM doc_permission_sets_ex WHERE meta_id = ?", 
			"DELETE FROM doc_permission_sets WHERE meta_id = ?", 
			"DELETE FROM includes WHERE meta_id = ?", 
			"DELETE FROM includes WHERE included_meta_id = ?", 
			"DELETE FROM imcms_text_doc_texts_history WHERE doc_id = ?",
			"DELETE FROM imcms_text_doc_images_history WHERE doc_id = ?",
			"DELETE FROM imcms_text_doc_menu_items_history WHERE to_doc_id = ?", 
			"DELETE FROM imcms_text_doc_menu_items_history WHERE menu_id IN (SELECT menu_id FROM imcms_text_doc_menus_history WHERE doc_id = ?)",
			"DELETE FROM imcms_text_doc_menus_history WHERE doc_id = ?", 
			"DELETE FROM document_properties WHERE meta_id = ?", 	
			"DELETE FROM imcms_doc_labels WHERE doc_id = ?",
            "DELETE FROM imcms_text_doc_contents WHERE doc_id = ?",
            "DELETE FROM imcms_text_doc_content_loops WHERE doc_id = ?",
            "DELETE FROM imcms_doc_default_version WHERE doc_id = ?",
            "DELETE FROM imcms_doc_default_languages WHERE doc_id = ?",
            "DELETE FROM imcms_doc_keywords WHERE doc_id = ?",
            "DELETE FROM imcms_doc_versions WHERE doc_id = ?",
			"DELETE FROM meta WHERE meta_id = ?",
		};
		
		Session session = getSession();
		
		for (String sql: sqls) {
			int i = session.createSQLQuery(sql).setParameter(0, metaId).executeUpdate();
		}								
	}
	
	@Transactional
	public List<Integer> getAllDocumentIds() {
		return (List<Integer>)getSession().getNamedQuery("Meta.getAllDocumentIds")
			.list();
	}
	
	@Transactional
	public List<Integer> getDocumentIdsInRange(Integer min, Integer max) {
		return (List<Integer>)getSession().getNamedQuery("Meta.getDocumentIdsInRange")
			.setParameter("min", min)
			.setParameter("max", max)
			.list();
	}
	
	@Transactional
	public Integer getMaxDocumentId() {
		return (Integer)getSession().getNamedQuery("Meta.getMaxDocumentId")
			.uniqueResult();
	}
	
	@Transactional
	public Integer getMinDocumentId() {
		return (Integer)getSession().getNamedQuery("Meta.getMinDocumentId")
			.uniqueResult();
	}
	
	@Transactional
	public Integer[] getMinMaxDocumentIds() {
	    Object[] tuple = (Object[]) getSession().getNamedQuery("Meta.getMinMaxDocumentIds")
	        .uniqueResult();
	    
	    return new Integer[] {
	            (Integer) tuple[0], 
	            (Integer) tuple[1]
	    };
	}

    // TODO: REMOVE!!!
    // TEMP!!!
    @Override
    public Session getSession() {
        return super.getSession();
    }

    @Transactional
    public List<I18nLanguage> getEnabledLanguages(Integer docId) {
        //getSession().createQuery("SELECT FROM ")
        return null;
    }
}