package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.persistence.entity.DocumentRole;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.entity.User;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Meta.Permission.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class DocumentRoleRepositoryTest {

    @Autowired
    private DocumentRolesRepository documentRolesRepository;

    @Autowired
    private TextDocumentDataInitializer textDocumentDataInitializer;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeClass
    public static void setUser() {
        final UserDomainObject user = new UserDomainObject(0);
        Imcms.setUser(user);
    }

    @Test
    public void getDocumentRolesByDocIdAndUserId_When_OneExist_Expect_Returned() {
        final int roleId = 0;
        final Meta.Permission permission = VIEW;

        final User user = userDataInitializer.createData(1, roleId).get(0);
        final int userId = user.getId();

        final TextDocumentDTO textDocument = textDocumentDataInitializer.createTextDocument();
        final Integer docId = textDocument.getId();

        final Meta meta = metaRepository.findOne(docId);
        final RoleJPA roleJPA = roleRepository.findOne(roleId);

        documentRolesRepository.save(new DocumentRole(meta, roleJPA, permission));

        final List<DocumentRole> documentRoleList = documentRolesRepository
                .getDocumentRolesByUserIdAndDocId(userId, docId);

        assertThat(documentRoleList, hasSize(1));

        final DocumentRole actualDocumentRole = documentRoleList.get(0);

        assertThat(actualDocumentRole.getDocument(), is(meta));
        assertThat(actualDocumentRole.getRole(), is(roleJPA));
        assertThat(actualDocumentRole.getPermission(), is(permission));
    }

    @Test
    public void getDocumentRolesByDocIdAndUserId_When_TwoExist_Expect_Returned() {
        final int roleId1 = 0;
        final int roleId2 = 1;

        final Meta.Permission permission1 = VIEW;
        final Meta.Permission permission2 = EDIT;

        final User user = userDataInitializer.createData(1, roleId1, roleId2).get(0);
        final int userId = user.getId();

        final TextDocumentDTO textDocument = textDocumentDataInitializer.createTextDocument();
        final Integer docId = textDocument.getId();

        final Meta meta = metaRepository.findOne(docId);

        final RoleJPA roleJPA1 = roleRepository.findOne(roleId1);
        final RoleJPA roleJPA2 = roleRepository.findOne(roleId2);

        documentRolesRepository.save(new DocumentRole(meta, roleJPA1, permission1));
        documentRolesRepository.save(new DocumentRole(meta, roleJPA2, permission2));

        final List<DocumentRole> documentRoleList = documentRolesRepository
                .getDocumentRolesByUserIdAndDocId(userId, docId);

        assertThat(documentRoleList, hasSize(2));

        final List<Meta> metaList = documentRoleList.stream()
                .map(DocumentRole::getDocument)
                .collect(Collectors.toList());

        final List<RoleJPA> roleList = documentRoleList.stream()
                .map(DocumentRole::getRole)
                .collect(Collectors.toList());

        final List<Meta.Permission> permissionList = documentRoleList.stream()
                .map(DocumentRole::getPermission)
                .collect(Collectors.toList());

        assertThat(metaList, containsInAnyOrder(meta, meta));
        assertThat(roleList, containsInAnyOrder(roleJPA1, roleJPA2));
        assertThat(permissionList, containsInAnyOrder(permission1, permission2));
    }

    @Test
    public void getByDocumentId() {
        final int roleId1 = 0;
        final int roleId2 = 1;

        final Meta.Permission permission1 = VIEW;
        final Meta.Permission permission2 = EDIT;

        final TextDocumentDTO textDocument1 = textDocumentDataInitializer.createTextDocument();
        final Integer docId1 = textDocument1.getId();

        final Meta meta1 = metaRepository.findOne(docId1);

        final TextDocumentDTO textDocument2 = textDocumentDataInitializer.createTextDocument();
        final Integer docId2 = textDocument2.getId();

        final Meta meta2 = metaRepository.findOne(docId2);

        final RoleJPA roleJPA1 = roleRepository.findOne(roleId1);
        final RoleJPA roleJPA2 = roleRepository.findOne(roleId2);

        final DocumentRole docRole1 = documentRolesRepository.save(new DocumentRole(meta1, roleJPA1, permission1));
        final DocumentRole docRole2 = documentRolesRepository.save(new DocumentRole(meta1, roleJPA2, permission2));
        documentRolesRepository.save(new DocumentRole(meta2, roleJPA2, permission2));

        final List<DocumentRole> expectedRoles = Arrays.asList(docRole1, docRole2);

        final Set<DocumentRole> docRoles1 = documentRolesRepository.findByDocument_Id(docId1);

        assertTrue(docRoles1.containsAll(expectedRoles));
        assertTrue(expectedRoles.containsAll(docRoles1));
    }
}