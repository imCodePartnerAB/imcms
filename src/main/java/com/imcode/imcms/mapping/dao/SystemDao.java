package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.SystemProperty;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Transactional
public class SystemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<SystemProperty> getProperties() {
        return entityManager.createQuery("SELECT p FROM SystemProperty p", SystemProperty.class).getResultList();
    }

    public SystemProperty getProperty(String name) {
        return entityManager.createQuery("SELECT p FROM SystemProperty p WHERE p.name = ?1", SystemProperty.class)
                .setParameter(1, name)
                .getSingleResult();
    }

    public SystemProperty saveProperty(SystemProperty property) {
        return entityManager.merge(property);
    }
}





