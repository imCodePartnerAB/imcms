package com.imcode.imcms.dao;

import com.imcode.imcms.api.MenuHistory;
import com.imcode.imcms.api.SystemProperty;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public class SystemDao extends HibernateTemplate {

	@Transactional
	public List<SystemProperty> getProperties() {
        return find("SELECT p FROM SystemProperty p");
    }


	@Transactional
	public SystemProperty getProperty(String name) {
        return (SystemProperty)getSession().createQuery("SELECT p FROM SystemProperty p WHERE p.name = ?")
                .setParameter(0, name)
                .uniqueResult();
    }
    

	@Transactional
	public void saveProperty(SystemProperty property) {
        saveOrUpdate(property);
    }
}