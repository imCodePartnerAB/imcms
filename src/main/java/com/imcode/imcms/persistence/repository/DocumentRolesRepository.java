package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.DocumentRole;
import com.imcode.imcms.persistence.entity.DocumentRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DocumentRolesRepository extends JpaRepository<DocumentRole, DocumentRoleId> {

    @Query("SELECT docRoles FROM DocumentRole docRoles, UserRoles userRoles " +
            "WHERE userRoles.id.userId = ?1 AND docRoles.id.documentId = ?2 AND " +
            "docRoles.id.roleId = userRoles.id.roleId")
    List<DocumentRole> getDocumentRolesByUserIdAndDocId(int userId, int documentId);

    Set<DocumentRole> findByDocument_Id(int documentId);
}
