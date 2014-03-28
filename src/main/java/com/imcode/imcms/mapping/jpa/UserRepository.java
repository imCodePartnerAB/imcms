package com.imcode.imcms.mapping.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {

    @Transactional(Transactional.TxType.SUPPORTS)
    User findByLogin(String login);

    @Transactional(Transactional.TxType.SUPPORTS)
    User findByLoginIgnoreCase(String login);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER (?1)")
    @Transactional(Transactional.TxType.SUPPORTS)
    List<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(?1)")
    @Transactional(Transactional.TxType.SUPPORTS)
    User findByEmailUnique(String email);

    @Transactional(Transactional.TxType.SUPPORTS)
    User findById(int id);

    @Transactional(Transactional.TxType.SUPPORTS)
    User findByPasswordResetId(String resetId);

//    @Query("SELECT u FROM User u WHERE i.id != 2 AND u.active = ?2 AND " +
//            "(u.login LIKE ?1 OR u.email LIKE ?1 OR firstName LIKE ?1 OR u.lastName LIKE ?1 OR u.title LIKE ?1 OR u.company LIKE ?1)" +
//            "ORDER BY u.lastName, u.firstName")
//    @Transactional(Transactional.TxType.SUPPORTS)
//    List<User> findUsersByNamePrefix(String prefix, boolean includeInactive);
}


interface UserRepositoryCustom {

    @Transactional(Transactional.TxType.SUPPORTS)
    List<User> findAll(boolean includeExternal, boolean includeInactive);

    @Transactional(Transactional.TxType.SUPPORTS)
    List<User> findByNamePrefix(String prefix, boolean includeInactive);
}

class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<User> findAll(boolean includeExternal, boolean includeInactive) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> c = cb.createQuery(User.class);
        Root<User> user = c.from(User.class);

        Predicate criteria = cb.conjunction();

        if (!includeExternal) {
            criteria = cb.and(criteria, cb.notEqual(user.get("external"), 2));
        }

        if (!includeInactive) {
            criteria = cb.and(criteria, cb.isTrue(user.get("active")));
        }

        c.select(user).where(criteria);

        return entityManager.createQuery(c).getResultList();
    }


    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<User> findByNamePrefix(String prefix, boolean includeInactive) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> c = cb.createQuery(User.class);
        Root<User> user = c.from(User.class);

        Predicate criteria = cb.notEqual(user.get("external"), 2);

        if (!includeInactive) {
            criteria = cb.and(criteria, cb.isTrue(user.get("active")));
        }

        criteria = cb.and(
                criteria,
                cb.or(
                        cb.like(user.get("login"), prefix),
                        cb.like(user.get("email"), prefix),
                        cb.like(user.get("firstName"), prefix),
                        cb.like(user.get("lastName"), prefix),
                        cb.like(user.get("title"), prefix),
                        cb.like(user.get("company"), prefix)
                )
        );

        c.select(user).where(criteria).orderBy(cb.asc(user.get("lastName")), cb.asc(user.get("firstName")));

        return entityManager.createQuery(c).getResultList();
    }
}