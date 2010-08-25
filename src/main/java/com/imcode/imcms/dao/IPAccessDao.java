package com.imcode.imcms.dao;

import com.imcode.imcms.api.IPAccess;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public class IPAccessDao {

    private HibernateTemplate hibernateTemplate;

    public IPAccessDao() {}

    public IPAccessDao(SessionFactory sf) {
        hibernateTemplate = new HibernateTemplate(sf);
    }

    @Transactional
    public List<IPAccess> getAll() {
        return hibernateTemplate.loadAll(IPAccess.class);
    }

    @Transactional
    public void delete(final Integer id) {
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("DELETE FROM IPAccess i WHERE i.id = :id").setParameter("id", id).executeUpdate();
            }
        });
    }

    @Transactional
    public void save(IPAccess ipAccess) {
        hibernateTemplate.saveOrUpdate(ipAccess);
    }

    @Transactional
    public IPAccess get(Integer id) {
        return hibernateTemplate.get(IPAccess.class, id);
    }
}
