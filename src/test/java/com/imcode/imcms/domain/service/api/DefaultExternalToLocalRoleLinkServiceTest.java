package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ExternalRole;
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
    }

    @Test
    void removeLinks() {
    }
}
