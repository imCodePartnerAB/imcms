package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.ExternalToLocalRoleLink;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.ExternalToLocalRoleLinkRepository;
import com.imcode.imcms.persistence.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultExternalToLocalRoleLinkServiceTest {

    @Mock
    private ExternalToLocalRoleLinkRepository repository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultExternalToLocalRoleLinkService externalToLocalRoleLinkService;

    @Test
    void addLink() {
        final Integer localRoleId = 42;
        final String providerId = "providerId";
        final String externalRoleId = "external-role-id";
        final ExternalRole externalRole = mock(ExternalRole.class);
        final RoleJPA localRole = mock(RoleJPA.class);

        given(localRole.getId()).willReturn(localRoleId);
        given(externalRole.getProviderId()).willReturn(providerId);
        given(externalRole.getId()).willReturn(externalRoleId);
        given(roleRepository.findOne(localRoleId)).willReturn(localRole);

        externalToLocalRoleLinkService.addLink(externalRole, localRoleId);

        then(roleRepository).should().findOne(localRoleId);

        final ArgumentCaptor<ExternalToLocalRoleLink> captor = ArgumentCaptor.forClass(ExternalToLocalRoleLink.class);
        then(repository).should().save(captor.capture());
        final ExternalToLocalRoleLink savedLink = captor.getValue();

        assertNotNull(savedLink);
        assertEquals(providerId, savedLink.getProviderId());
        assertEquals(externalRoleId, savedLink.getExternalRoleId());
        assertEquals(localRoleId, savedLink.getLocalRoleId());
        assertEquals(localRole, savedLink.getRole());
    }

    @Test
    void removeLink() {
        final int localRoleId = 42;
        final String providerId = "provider-id";
        final String externalRoleId = "external-role-id";
        final ExternalRole externalRole = mock(ExternalRole.class);

        given(externalRole.getProviderId()).willReturn(providerId);
        given(externalRole.getId()).willReturn(externalRoleId);

        externalToLocalRoleLinkService.removeLink(externalRole, localRoleId);

        then(repository).should().deleteByProviderIdAndExternalRoleIdAndLocalRoleId(
                providerId, externalRoleId, localRoleId
        );
    }

    @Test
    void removeLinks() {
        final String providerId = "provider-id";
        final String externalRoleId = "external-role-id";
        final ExternalRole externalRole = mock(ExternalRole.class);

        given(externalRole.getProviderId()).willReturn(providerId);
        given(externalRole.getId()).willReturn(externalRoleId);

        externalToLocalRoleLinkService.removeLinks(externalRole);

        then(repository).should().deleteByProviderIdAndExternalRoleId(providerId, externalRoleId);
    }

    @Test
    void getLinkedLocalRoles_When_NoLinkedRolesExist_Expect_EmptyList() {
        final String providerId = "provider-id";
        final String externalRoleId = "external-role-id";
        final ExternalRole externalRole = mock(ExternalRole.class);

        given(externalRole.getProviderId()).willReturn(providerId);
        given(externalRole.getId()).willReturn(externalRoleId);
        given(repository.findByProviderIdAndExternalRoleId(providerId, externalRoleId))
                .willReturn(Collections.emptySet());

        final Set<Role> linkedLocalRoles = externalToLocalRoleLinkService.getLinkedLocalRoles(externalRole);

        assertNotNull(linkedLocalRoles);
        assertTrue(linkedLocalRoles.isEmpty());
    }

    @Test
    void getLinkedLocalRoles_When_LinkedRolesExist_Expect_ListWithCorrectRoles() {
        final String providerId = "provider-id";
        final String externalRoleId = "external-role-id";
        final ExternalRole externalRole = mock(ExternalRole.class);

        final RoleJPA role1 = new RoleJPA(1, "first");
        final RoleJPA role2 = new RoleJPA(2, "second");

        final List<Role> expected = Arrays.asList(new RoleDTO(role1), new RoleDTO(role2));

        final List<ExternalToLocalRoleLink> links = Arrays.asList(
                new ExternalToLocalRoleLink(providerId, externalRoleId, role1),
                new ExternalToLocalRoleLink(providerId, externalRoleId, role2)
        );

        given(externalRole.getProviderId()).willReturn(providerId);
        given(externalRole.getId()).willReturn(externalRoleId);
        given(repository.findByProviderIdAndExternalRoleId(providerId, externalRoleId))
                .willReturn(new HashSet<>(links));

        final Set<Role> actual = externalToLocalRoleLinkService.getLinkedLocalRoles(externalRole);

        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }
}
