package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.IPAccess;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
public class IPAccessDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<IPAccess> getAll() {
        return entityManager.createQuery("SELECT i FROM IPAccess i", IPAccess.class).getResultList();
    }

    public int delete(int id) {
        return entityManager.createQuery("DELETE FROM IPAccess i WHERE i.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }


    public IPAccess save(IPAccess ipAccess) {
        return entityManager.merge(ipAccess);
    }

    public IPAccess get(int id) {
        return entityManager.find(IPAccess.class, id);
    }
}
