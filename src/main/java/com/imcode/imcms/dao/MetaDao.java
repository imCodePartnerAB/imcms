package com.imcode.imcms.dao;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

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

	/**
	 * @return Labels.
	 */
	@Transactional
	public void deleteLabels(Integer docId, Integer docVersionNo) {
		getSession().createQuery("DELETE FROM DocumentLabels l WHERE l.docId = :docId AND docVersionNo = :docVersionNo")
                .setParameter("docId", docId)
                .setParameter("docVersionNo", docVersionNo)
                .executeUpdate();
	}

    
	@Transactional
	public DocumentLabels saveLabels(DocumentLabels labels) {
		save(labels);

        return labels;
	}

	
	@Transactional
	public void saveMeta(Meta meta) {
		saveOrUpdate(meta);
	}
		
	
	@Transactional
	public void saveIncludes(Integer documentId, Collection<Include> includes) {
		bulkUpdate("delete from Include i where i.metaId = ?", documentId);
		
		//flush();
		//clear();
		for (Include include: includes) {
			saveOrUpdate(include);
		}
	}

	@Transactional
	public void saveTemplateNames(Integer documentId, TemplateNames templateNames) {
		// delete first?
				
		saveOrUpdate(templateNames);
	}

	@Transactional
	public Collection<Include> getIncludes(Integer documentId) {
		return (Collection<Include>) find("select i from Include i where i.metaId = ?", documentId);
	}

	@Transactional
	public TemplateNames getTemplateNames(Integer documentId) {
		return (TemplateNames)getSession().createQuery("select n from TemplateNames n where n.metaId = ?")
			.setParameter(0, documentId)
			.uniqueResult();
	}

	@Transactional
	public Collection<FileReference> getFileReferences(Integer documentId) {
		return find("select f from FileReference f where f.metaId = ? ORDER BY f.defaultFileId DESC, f.fileId", documentId);
	}

	@Transactional
	public FileReference saveFileReference(FileReference fileRef) {
		saveOrUpdate(fileRef);
		
		return fileRef;
	}

	@Transactional
	public int deleteFileReferences(Integer documentId) {
		return bulkUpdate("delete from FileReference f where f.metaId = ?", documentId);
	}

	@Transactional
	public HtmlReference getHtmlReference(Integer documentId) {
		return (HtmlReference)getSession().createQuery("select h from HtmlReference h where h.metaId = ?")
		.setParameter(0, documentId)
		.uniqueResult();
	}	
	
	@Transactional
	public HtmlReference saveHtmlReference(HtmlReference reference) {
		saveOrUpdate(reference);
		
		return reference;
	}
	
	@Transactional
	public UrlReference getUrlReference(Integer documentId) {
		return (UrlReference)getSession().createQuery("select u from UrlReference u where u.metaId = ?")
		.setParameter(0, documentId)
		.uniqueResult();
	}

	@Transactional
	public UrlReference saveUrlReference(UrlReference reference) {
		saveOrUpdate(reference);
		
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
			// delete form keywords ???
			"DELETE FROM childs WHERE to_meta_id = ?", 
			"DELETE FROM childs WHERE menu_id IN (SELECT menu_id FROM menus WHERE meta_id = ?)",	
			"DELETE FROM menus WHERE meta_id = ?", 
			"DELETE FROM text_docs WHERE meta_id = ?", 
			"DELETE FROM texts WHERE meta_id = ?", 
			"DELETE FROM images WHERE meta_id = ?", 
			"DELETE FROM roles_rights WHERE meta_id = ?", 
			"DELETE FROM user_rights WHERE meta_id = ?", 
			"DELETE FROM url_docs WHERE meta_id = ?", 
			"DELETE FROM fileupload_docs WHERE meta_id = ?", 
			"DELETE FROM frameset_docs WHERE meta_id = ?", 
			"DELETE FROM new_doc_permission_sets_ex WHERE meta_id = ?", 
			"DELETE FROM new_doc_permission_sets WHERE meta_id = ?", 
			"DELETE FROM doc_permission_sets_ex WHERE meta_id = ?", 
			"DELETE FROM doc_permission_sets WHERE meta_id = ?", 
			"DELETE FROM includes WHERE meta_id = ?", 
			"DELETE FROM includes WHERE included_meta_id = ?", 
			"DELETE FROM texts_history WHERE meta_id = ?", 
			"DELETE FROM images_history WHERE meta_id = ?", 
			"DELETE FROM childs_history WHERE to_meta_id = ?", 
			"DELETE FROM childs_history WHERE menu_id IN (SELECT menu_id FROM menus_history WHERE meta_id = ?)",
			"DELETE FROM menus_history WHERE meta_id = ?", 
			"DELETE FROM document_properties WHERE meta_id = ?", 	
			"DELETE FROM i18n_meta WHERE meta_id = ?", 	
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