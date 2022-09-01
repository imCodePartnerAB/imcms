package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.ExternalToLocalRoleLinkJPA;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.repository.ExternalToLocalRoleLinkRepository;
import com.imcode.imcms.persistence.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalToLocalRoleLinkServiceTest {

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
	    given(roleRepository.getOne(localRoleId)).willReturn(localRole);

        externalToLocalRoleLinkService.addLink(externalRole, localRoleId);

	    then(roleRepository).should().getOne(localRoleId);

        final ArgumentCaptor<ExternalToLocalRoleLinkJPA> captor = ArgumentCaptor.forClass(ExternalToLocalRoleLinkJPA.class);
        then(repository).should().save(captor.capture());
        final ExternalToLocalRoleLinkJPA savedLink = captor.getValue();

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

        final List<ExternalToLocalRoleLinkJPA> links = Arrays.asList(
                new ExternalToLocalRoleLinkJPA(providerId, externalRoleId, role1),
                new ExternalToLocalRoleLinkJPA(providerId, externalRoleId, role2)
        );

        given(externalRole.getProviderId()).willReturn(providerId);
        given(externalRole.getId()).willReturn(externalRoleId);
        given(repository.findByProviderIdAndExternalRoleId(providerId, externalRoleId))
                .willReturn(new HashSet<>(links));

        final Set<Role> actual = externalToLocalRoleLinkService.getLinkedLocalRoles(externalRole);

        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test
    void setLinkedRoles_When_SomeRolesAlreadyLinkedAndSendingSomeExistingAndSomeNewAndSomeNotSent_Expect_NewSavedAndExistingSentNotTouchedAndNotSentDeleted() {
        final String providerId = "provider-id";
        final String externalRoleId1 = "external-role-1";

        final ExternalRole externalRole1 = mock(ExternalRole.class);
        given(externalRole1.getId()).willReturn(externalRoleId1);
        given(externalRole1.getProviderId()).willReturn(providerId);

        final Integer localRoleIdShouldBeDeleted = 1;
        final Integer localRoleIdShouldNotBeDeleted = 2;
        final Integer localRoleIdShouldBeLinked = 3;

        final Set<Integer> rolesSent = new HashSet<>(Arrays.asList(
                localRoleIdShouldNotBeDeleted, localRoleIdShouldBeLinked
        ));

        final RoleJPA localRoleWhichLinkShouldBeDeleted = new RoleJPA(localRoleIdShouldBeDeleted, "1");
        final ExternalToLocalRoleLinkJPA linkShouldBeDeleted = new ExternalToLocalRoleLinkJPA(
                13, providerId, externalRoleId1, localRoleWhichLinkShouldBeDeleted
        );
        final RoleJPA localRoleAlreadyLinkedShouldNotBeDeleted = new RoleJPA(localRoleIdShouldNotBeDeleted, "2");
        final Set<ExternalToLocalRoleLinkJPA> existingRoleLinks = new HashSet<>(Arrays.asList(
                linkShouldBeDeleted,
                new ExternalToLocalRoleLinkJPA(14, providerId, externalRoleId1, localRoleAlreadyLinkedShouldNotBeDeleted)
        ));

        given(repository.findByProviderIdAndExternalRoleId(providerId, externalRoleId1)).willReturn(existingRoleLinks);

        externalToLocalRoleLinkService.setLinkedRoles(externalRole1, rolesSent);

        then(repository).should().delete(linkShouldBeDeleted);

        final ArgumentCaptor<ExternalToLocalRoleLinkJPA> captor = ArgumentCaptor.forClass(ExternalToLocalRoleLinkJPA.class);

        then(repository).should().save(captor.capture());

        final ExternalToLocalRoleLinkJPA savedLink = captor.getValue();
        assertEquals(savedLink.getProviderId(), providerId);
        assertEquals(savedLink.getLocalRoleId(), localRoleIdShouldBeLinked);
        assertEquals(savedLink.getExternalRoleId(), externalRoleId1);
    }
}
