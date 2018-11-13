package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.model.Profile;
import com.imcode.imcms.persistence.entity.ProfileJPA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class ProfileRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    public void findAll_When_ProfilesExist_Expected_CorrectSize() {
        profileRepository.deleteAll();
        assertTrue(profileRepository.findAll().isEmpty());

        final List<ProfileJPA> profiles = createTestProfiles();

        assertEquals(profileRepository.findAll().size(), profiles.size());
    }

    @Test
    public void findAll_When_ProfilesExist_Expected_CorrectEntities() {
        assertTrue(profileRepository.findAll().isEmpty());
        final List<ProfileJPA> profiles = createTestProfiles();

        assertEquals(profileRepository.findAll(), profiles);
    }

    @Test
    public void findAll_When_ProfilesNotExist_Expected_EmptyResult() {
        final List<ProfileJPA> profiles = createTestProfiles();

        assertFalse(profileRepository.findAll().isEmpty());

        profileRepository.deleteAll();

        assertTrue(profileRepository.findAll().isEmpty());
    }

    @Test
    public void createProfile_When_ProfileNotExist_Expected_CreateNewEntity() {
        ProfileJPA profile = new ProfileJPA(null, "Test", "DocTest");
        Profile newProfile = profileRepository.save(profile);
        assertEquals(profile, newProfile);
    }

    @Test
    public void getById_When_ProfileExist_Expected_CorrectEntity() {
        final List<ProfileJPA> profiles = createTestProfiles();
        int idFirstElement = profiles.get(0).getId();
        Profile profile = profileRepository.findOne(idFirstElement);

        assertNotNull(profile);
        assertEquals(profiles.get(0), profile);
    }

    @Test
    public void getById_When_ProfileNotExist_Expected_ResultNull() {
        int fakeId = -1;
        Profile profile = profileRepository.findOne(fakeId);

        assertNull(profile);
    }

    @Test
    public void update_When_ProfileExist_Expected_NewUpdateEntity() {
        final List<ProfileJPA> profiles = createTestProfiles();

        ProfileJPA testProfileJPA = profiles.get(0);
        testProfileJPA.setName("1002");

        assertEquals(testProfileJPA, profileRepository.save(testProfileJPA));
    }

    @Test
    public void deleteById_When_ProfilesNotExist_Expected_CorrectException() {
        int fakeId = -1;
        assertThrows(EmptyResultDataAccessException.class, () -> profileRepository.delete(fakeId));
    }

    @Test
    public void deleteById_When_ProfilesExist_Expected_Null() {
        assertTrue(profileRepository.findAll().isEmpty());
        List<ProfileJPA> profiles = createTestProfiles();

        int id = profiles.get(0).getId();
        profileRepository.delete(id);

        assertNull(profileRepository.findOne(id));
        assertEquals(2, profileRepository.findAll().size());
    }

    private List<ProfileJPA> createTestProfiles() {
        List<ProfileJPA> profiles = Arrays.asList(
                new ProfileJPA(null, "name1", "1001"),
                new ProfileJPA(null, "name2", "alias"),
                new ProfileJPA(null, "name3", "alias2")
        );
        profileRepository.save(profiles);
        return profiles;
    }
}
