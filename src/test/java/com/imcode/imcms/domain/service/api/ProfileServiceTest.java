package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.ProfileDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.ProfileService;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.model.Profile;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ProfileServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private LanguageService languageService;

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        DocGetterCallback docGetterCallback = user.getDocGetterCallback();

        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        docGetterCallback.setLanguage(languageService.findByCode(ImcmsConstants.ENG_CODE));
        Imcms.setUser(user);
    }

    @Test
    public void findAll_When_ProfilesExist_Expected_CorrectEntities() {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        assertFalse(profileService.getAll().isEmpty());
        assertEquals(profiles, profileService.getAll());
    }

    @Test
    public void findAll_When_ProfilesNotExist_Expected_EmptyList() {
        assertTrue(profileService.getAll().isEmpty());
    }

    @Test
    public void createProfile_When_ProfileNameEmpty_Expected_CorrectException() {
        assertTrue(profileService.getAll().isEmpty());
        ProfileDTO profile = new ProfileDTO("1001", "", 1);

        assertThrows(IllegalArgumentException.class, () -> profileService.create(profile));
        assertTrue(profileService.getAll().isEmpty());
    }

    @Test
    public void createProfile_When_ProfileNotExist_Expected_CreatedProfile() {
        assertTrue(profileService.getAll().isEmpty());
        ProfileDTO profile = new ProfileDTO("1001", "name1", 1);
        profileService.create(profile);

        assertNotNull(profileService.getAll());
        assertEquals(1, profileService.getAll().size());
    }

    @Test
    public void getById_When_ProfileExist_Expected_CorrectEntity() {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        int idFirstProfile = profiles.get(0).getId();

        assertTrue(profileService.getById(idFirstProfile).isPresent());
    }

    @Test
    public void createProfile_When_DocumentIdNotExist_Expected_CorrrectException() {
        assertTrue(profileService.getAll().isEmpty());

        ProfileDTO profile = new ProfileDTO("99999", "name1", 1);
        assertThrows(IllegalArgumentException.class, () -> profileService.create(profile));
    }

    @Test
    public void createProfile_When_DocumentAliasNotExist_Expected_CorrrectException() {
        assertTrue(profileService.getAll().isEmpty());

        ProfileDTO profile = new ProfileDTO("alias", "name1", 1);
        assertThrows(IllegalArgumentException.class, () -> profileService.create(profile));
    }

    @Test
    public void getById_When_ProfileNotExist_Expected_EmptyOptional() {
        int fakeId = -1;
        Optional<Profile> profileDTO = profileService.getById(fakeId);
        assertFalse(profileDTO.isPresent());
    }

    @Test
    public void update_When_ProfileExist_Expected_UpdateEntity() {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        ProfileDTO profileDTO = profiles.get(0);
        profileDTO.setName("1003");

        assertEquals(profileDTO, profileService.update(profileDTO));
    }

    @Test
    public void update_When_ProfileNameEmpty_Expected_CorrectException() {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        ProfileDTO profile = profiles.get(0);
        profile.setName("");
        profile.setDocumentName("1001");

        assertThrows(IllegalArgumentException.class, () -> profileService.update(profile));
    }

    @Test
    public void update_When_DocumentNameEmpty_Expected_CorrectException() {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        ProfileDTO profile = profiles.get(0);
        profile.setName("name1");
        profile.setDocumentName("");

        assertThrows(IllegalArgumentException.class, () -> profileService.update(profile));
    }

    @Test
    public void update_When_DocumentIdNotExist_Expected_CorrrectException() {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        ProfileDTO profile = profiles.get(0);
        profile.setName("name1");
        profile.setDocumentName("999");

        assertThrows(IllegalArgumentException.class, () -> profileService.update(profile));
    }

    @Test
    public void update_When_DocumentAliasNotExist_Expected_CorrrectException() {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        ProfileDTO profile = profiles.get(0);
        profile.setName("name1");
        profile.setDocumentName("alias");

        assertThrows(IllegalArgumentException.class, () -> profileService.update(profile));
    }

    @Test
    public void deleteById_When_ProfileExist_Expected_EntityDeleted() {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        int id = profiles.get(0).getId();
        profileService.deleteById(id);

        assertEquals(profiles.size() - 1, profileService.getAll().size());
        assertFalse(profileService.getById(id).isPresent());
    }

    @Test
    public void deleteById_When_ProfilesNotExist_Expected_CorrectException() {
        int fakeId = -1;
        assertThrows(EmptyResultDataAccessException.class, () -> profileService.deleteById(fakeId));
    }

    private List<ProfileDTO> createTestProfiles() {
        List<ProfileDTO> profiles = Arrays.asList(
                new ProfileDTO("1001", "name1", 1),
                new ProfileDTO("1001", "name2", 2),
                new ProfileDTO("1001", "name3", 3)
        );
        profiles = profiles.stream()
                .map(profileService::create)
                .map(ProfileDTO::new)
                .collect(Collectors.toList());

        return profiles;
    }

}
