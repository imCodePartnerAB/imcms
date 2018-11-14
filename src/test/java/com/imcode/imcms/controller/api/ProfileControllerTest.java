package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.ProfileDTO;
import com.imcode.imcms.domain.service.ProfileService;
import com.imcode.imcms.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ProfileControllerTest extends AbstractControllerTest {

    @Autowired
    private ProfileService profileService;

    @Override
    protected String controllerPath() {
        return "/profiles";
    }

    @Test
    public void getAll_When_ProfilesExist_Excpected_OkAndCorrectEntites() throws Exception {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(profiles.toArray()));
    }

    @Test
    public void getAll_When_ProfilesNotExist_Excpected_OkAndEmptyResult() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void createEntity_When_ProfileNotExist_Expected_OkAndCreatedEntity() throws Exception {
        assertTrue(profileService.getAll().isEmpty());

        Profile profileDTO = new ProfileDTO("1002", "name1", null);

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(profileDTO);

        performRequestBuilderExpectedOk(requestBuilder);

        List<Profile> savedProfiles = profileService.getAll();

        assertEquals(1, savedProfiles.size());
        profileDTO.setId(savedProfiles.get(0).getId());
        assertEquals(profileDTO, savedProfiles.get(0));
    }

    @Test
    public void getById_When_ProfileExist_Expected_OkAndCorrectEntity() throws Exception {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();
        int id = profiles.get(0).getId();

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + id);
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(profiles.get(0)));
    }

    @Test
    public void getById_When_ProfileNotExist_Expected_CorrectException() throws Exception {
        int fakeId = -1;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + fakeId);
        performRequestBuilderExpectException(EmptyResultDataAccessException.class, requestBuilder);
    }

    @Test
    public void deleteById_When_ProfileExist_Expected_OkAndDeletedEntity() throws Exception {
        assertTrue(profileService.getAll().isEmpty());
        List<ProfileDTO> profiles = createTestProfiles();
        int id = profiles.get(0).getId();

        final MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders.delete(controllerPath() + "/" + id);

        performRequestBuilderExpectedOk(requestBuilder1);

        assertEquals(profiles.size() - 1, profileService.getAll().size());
    }

    @Test
    public void deleteById_When_ProfileNotExist_Expected_CorrectException() throws Exception {
        int fakeId = -1;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + fakeId);
        performRequestBuilderExpectException(EmptyResultDataAccessException.class, requestBuilder);
    }

    @Test
    public void update_When_ProfileExist_Expected_OkAndUpdatedEntity() throws Exception {
        List<ProfileDTO> profiles = createTestProfiles();

        ProfileDTO profileDTO = profiles.get(0);
        profileDTO.setName("anotherName");
        profileDTO.setDocumentName("anotherDocName");

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(profileDTO);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(profileDTO));
    }

    private List<ProfileDTO> createTestProfiles() {
        List<ProfileDTO> profiles = Arrays.asList(
                new ProfileDTO("1002", "name1", 1),
                new ProfileDTO("alias", "name2", 2),
                new ProfileDTO("alias2", "name3", 3)
        );

        profiles = profiles.stream()
                .map(profileService::create)
                .map(ProfileDTO::new)
                .collect(Collectors.toList());
        return profiles;
    }
}
