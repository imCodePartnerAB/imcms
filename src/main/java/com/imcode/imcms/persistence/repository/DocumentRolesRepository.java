package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.DocumentRoleId;
import com.imcode.imcms.persistence.entity.DocumentRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRolesRepository extends JpaRepository<DocumentRoles, DocumentRoleId> {

    @Query("SELECT docRoles FROM DocumentRoles docRoles, UserRoles userRoles " +
            "WHERE userRoles.id.userId = ?1 AND docRoles.id.documentId = ?2 AND " +
            "docRoles.id.roleId = userRoles.id.roleId")
    List<DocumentRoles> getDocumentRolesByDocIdAndUserId(int userId, int documentId);
}
