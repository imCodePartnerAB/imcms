package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageHistoryDTO;
import com.imcode.imcms.domain.service.ImageHistoryService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class ImageHistoryServiceTest extends WebAppSpringTestConfig {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_IMAGE_INDEX = 1;
    private static final int VERSION_INDEX = 0;


    @Autowired
    private ImageHistoryService imageHistoryService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private LanguageDataInitializer languageDataInitializer;
    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private Function<ImageJPA, ImageDTO> imageJPAToImageDTO;

    @BeforeAll
    public static void setUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
    }

    @BeforeEach
    public void setUp() {
        imageDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();
        languageDataInitializer.cleanRepositories();
        languageDataInitializer.createData();
    }

    @Test
    public void saveImageHistory_WhenUsedInLoop_Expect_Saved() {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        imageHistoryService.save(image);
        imageHistoryService.save(image);

        final List<ImageHistoryDTO> actual = imageHistoryService.getAll(imageDTO);

        assertEquals(2, actual.size());
    }

    @Test
    public void saveImageHistory_WhenNotUsedInLoop_Expect_Saved() {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, null);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        imageHistoryService.save(image);

        final List<ImageHistoryDTO> actual = imageHistoryService.getAll(imageDTO);

        assertEquals(1, actual.size());
    }

    @Test
    public void saveImageHistory_WhenUsedDTOObject_Expect_Saved() {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, null);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);
        final Version version = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);

        imageHistoryService.save(imageDTO, languageRepository.findByCode(ImcmsConstants.ENG_CODE), version);

        final List<ImageHistoryDTO> actual = imageHistoryService.getAll(imageDTO);

        assertEquals(1, actual.size());
    }

    @Test
    public void saveImageHistory_When_HistoryRecordsLimitExceeded_Expect_SavedAndOutdatedRecordDeleted() {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        final int contentHistoryRecordsSize = Imcms.getServices().getConfig().getContentHistoryRecordsSize();
        for(int i=0; i<contentHistoryRecordsSize; i++){
            imageHistoryService.save(image);
        }

        final List<ImageHistoryDTO> imageHistoryDTOsBeforeLimitExceeded = imageHistoryService.getAll(imageDTO);
        assertEquals(contentHistoryRecordsSize, imageHistoryDTOsBeforeLimitExceeded.size());

        imageHistoryService.save(image);

        final List<ImageHistoryDTO> imageHistoryDTOsAfterLimitExceeded = imageHistoryService.getAll(imageDTO);
        assertEquals(contentHistoryRecordsSize, imageHistoryDTOsAfterLimitExceeded.size());

        ImageHistoryDTO outdatedRecord = imageHistoryDTOsBeforeLimitExceeded.get(imageHistoryDTOsBeforeLimitExceeded.size() - 1);
        assertFalse(imageHistoryDTOsAfterLimitExceeded.contains(outdatedRecord));
    }

    @Test
    public void saveImageHistory_When_HistoryRecordsLimitExceeded_And_LoopIsNull_Expect_SavedAndOutdatedRecordDeleted() {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, null);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        final int contentHistoryRecordsSize = Imcms.getServices().getConfig().getContentHistoryRecordsSize();
        for(int i=0; i<contentHistoryRecordsSize; i++){
            imageHistoryService.save(image);
        }

        final List<ImageHistoryDTO> imageHistoryDTOsBeforeLimitExceeded = imageHistoryService.getAll(imageDTO);
        assertEquals(contentHistoryRecordsSize, imageHistoryDTOsBeforeLimitExceeded.size());

        imageHistoryService.save(image);

        final List<ImageHistoryDTO> imageHistoryDTOsAfterLimitExceeded = imageHistoryService.getAll(imageDTO);
        assertEquals(contentHistoryRecordsSize, imageHistoryDTOsAfterLimitExceeded.size());

        ImageHistoryDTO outdatedRecord = imageHistoryDTOsBeforeLimitExceeded.get(imageHistoryDTOsBeforeLimitExceeded.size() - 1);
        assertFalse(imageHistoryDTOsAfterLimitExceeded.contains(outdatedRecord));
    }

    @Test
    public void saveImageHistory_When_UsedDTOObject_And_HistoryRecordsLimitExceeded_Expect_SavedAndOutdatedRecordDeleted() {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);
        final Version version = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);

        long contentHistoryRecordsSize = Imcms.getServices().getConfig().getContentHistoryRecordsSize();
        for(int i=0; i<contentHistoryRecordsSize; i++){
            imageHistoryService.save(imageDTO, languageRepository.findByCode(ImcmsConstants.ENG_CODE), version);
        }

        final List<ImageHistoryDTO> imageHistoryDTOsBeforeLimitExceeded = imageHistoryService.getAll(imageDTO);
        assertEquals(contentHistoryRecordsSize, imageHistoryDTOsBeforeLimitExceeded.size());

        imageHistoryService.save(imageDTO, languageRepository.findByCode(ImcmsConstants.ENG_CODE), version);

        final List<ImageHistoryDTO> imageHistoryDTOsAfterLimitExceeded = imageHistoryService.getAll(imageDTO);
        assertEquals(contentHistoryRecordsSize, imageHistoryDTOsAfterLimitExceeded.size());

        ImageHistoryDTO outdatedRecord = imageHistoryDTOsBeforeLimitExceeded.get(imageHistoryDTOsBeforeLimitExceeded.size() - 1);
        assertFalse(imageHistoryDTOsAfterLimitExceeded.contains(outdatedRecord));
    }

    @Test
    public void saveImageHistory_When_UsedDTOObject_And_HistoryRecordsLimitExceeded_And_LoopIsNull_Expect_SavedAndOutdatedRecordDeleted() {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, null);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);
        final Version version = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);

        long contentHistoryRecordsSize = Imcms.getServices().getConfig().getContentHistoryRecordsSize();
        for(int i=0; i<contentHistoryRecordsSize; i++){
            imageHistoryService.save(imageDTO, languageRepository.findByCode(ImcmsConstants.ENG_CODE), version);
        }

        final List<ImageHistoryDTO> imageHistoryDTOsBeforeLimitExceeded = imageHistoryService.getAll(imageDTO);
        assertEquals(contentHistoryRecordsSize, imageHistoryDTOsBeforeLimitExceeded.size());

        imageHistoryService.save(imageDTO, languageRepository.findByCode(ImcmsConstants.ENG_CODE), version);

        final List<ImageHistoryDTO> imageHistoryDTOsAfterLimitExceeded = imageHistoryService.getAll(imageDTO);
        assertEquals(contentHistoryRecordsSize, imageHistoryDTOsAfterLimitExceeded.size());

        ImageHistoryDTO outdatedRecord = imageHistoryDTOsBeforeLimitExceeded.get(imageHistoryDTOsBeforeLimitExceeded.size() - 1);
        assertFalse(imageHistoryDTOsAfterLimitExceeded.contains(outdatedRecord));
    }

    @Test
    public void findAll_When_UsingLoopImage_Expect_CorrectEntities() {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA generatedImage = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        generatedImage.setLanguage(languageRepository.findByCode(ImcmsConstants.SWE_CODE));
        imageHistoryService.save(generatedImage);
        imageHistoryService.save(generatedImage);
        imageHistoryService.save(generatedImage);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(generatedImage);

        final List<ImageHistoryDTO> actual = imageHistoryService.getAll(imageDTO);

        assertEquals(3, actual.size());
        actual.forEach(imageHistory -> {
            assertEquals(generatedImage.getIndex(), imageHistory.getIndex());
            assertEquals(generatedImage.getLanguage().getCode(), imageHistory.getLangCode());
            assertEquals(generatedImage.getLoopEntryRef(), new LoopEntryRefJPA(imageHistory.getLoopEntryRef()));
        });
    }

    @Test
    public void findAll_When_UsingNotLoopImage_Expect_CorrectEntities() {
        final ImageJPA generatedImage = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, null);
        generatedImage.setLanguage(languageRepository.findByCode(ImcmsConstants.ENG_CODE));
        imageHistoryService.save(generatedImage);
        imageHistoryService.save(generatedImage);
        imageHistoryService.save(generatedImage);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(generatedImage);

        final List<ImageHistoryDTO> actual = imageHistoryService.getAll(imageDTO);

        assertEquals(3, actual.size());
        actual.forEach(imageHistory -> {
            assertEquals(generatedImage.getIndex(), imageHistory.getIndex());
            assertEquals(generatedImage.getLanguage().getCode(), imageHistory.getLangCode());
            assertNull(imageHistory.getLoopEntryRef());
        });
    }

}