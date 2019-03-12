package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.ImcmsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ImageHistoryJPARepositoryTest extends WebAppSpringTestConfig {

    private static final int DOC_ID = 1001;
    private static final int VERSION_INDEX = 0;
    private static final int IMAGE_INDEX = 1;

    @Autowired
    private ImageHistoryRepository imageHistoryRepository;
    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;
    @Autowired
    private ImageDataInitializer imageDataInitializer;


    private Version version;
    private LanguageJPA english;
    private LanguageJPA swedish;

    @BeforeEach
    public void setUp() {
        imageHistoryRepository.deleteAll();
        imageDataInitializer.cleanRepositories();

        version = versionDataInitializer.createData(VERSION_INDEX, DOC_ID);
        english = languageRepository.findByCode(ImcmsConstants.ENG_CODE);
        swedish = languageRepository.findByCode(ImcmsConstants.SWE_CODE);
    }

    @Test
    public void saveImageHistory_When_UsedSAmeVersion_Expected_Saved() {
        assertTrue(imageHistoryRepository.findAll().isEmpty());

        imageDataInitializer.generateImageHistory(IMAGE_INDEX, swedish, version, null, userService.getUser(1));
        imageDataInitializer.generateImageHistory(IMAGE_INDEX, swedish, version, null, userService.getUser(1));
        imageDataInitializer.generateImageHistory(IMAGE_INDEX + 1, english, version, null, userService.getUser(1));

        assertEquals(3, imageHistoryRepository.findAll().size());
    }

}