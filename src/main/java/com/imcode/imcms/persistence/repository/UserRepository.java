package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
@Transactional(Transactional.TxType.SUPPORTS)
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByLogin(String login);

    User findByLoginAndActiveIsTrue(String login);

    User findByLoginIgnoreCase(String login);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER (?1)")
    List<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(?1)")
    User findByEmailUnique(String email);

    User findById(int id);

    List<User> findByIdIn(Set<Integer> id);

    User findByPasswordResetId(String resetId);

    @Query(value = "SELECT u.* FROM users u, user_roles_crossref u_roles " +
            "WHERE u_roles.user_id = u.user_id AND (u_roles.role_id IN (:roleIds)) " +
            "GROUP BY u.user_id",
            nativeQuery = true)
    List<User> findUsersWithRoleIds(@Param("roleIds") Integer... roleIds);

    @Transactional
    @Query("UPDATE User u SET u.sessionId = ?1 WHERE u.id = ?2")
    void updateSessionId(int userId, String sessionId);

    @Transactional
    @Query("SELECT u.sessionId FROM User u WHERE u.id = ?1")
    String findSessionId(int userId);
}


