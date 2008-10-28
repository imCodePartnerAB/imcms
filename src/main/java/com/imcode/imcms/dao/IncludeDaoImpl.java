package com.imcode.imcms.dao;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.Include;

public class IncludeDaoImpl extends HibernateTemplate implements IncludeDao {

	public List<Include> getDocumentIncludes(int metaId) {
		
		return (List<Include>)findByNamedQueryAndNamedParam("Include.getByMetaId", "metaId", metaId);
	}

	@Transactional
	public Include getDocumentInclude(int metaId, int includeIndex) {
		return (Include)getSession().getNamedQuery("Include.getByMetaIdAndIndex")
			.setParameter("metaId", metaId)
			.setParameter("index", includeIndex)
			.uniqueResult();
	}

	@Transactional
	public Include saveInclude(Include include) {
		saveOrUpdate(include);
		
		return include;
	}

	@Transactional
	public int deleteDocumentIncludes(int metaId) {
		return getSession().createQuery("DELETE FROM Include i WHERE i.metaId = :metaId")
			.setParameter("metaId", metaId).executeUpdate();
	}
}