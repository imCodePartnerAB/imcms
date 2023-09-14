package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.persistence.entity.*;
import imcode.server.ImcmsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public void saveImageHistory_When_UsedSameVersion_Expected_Saved() {
        assertTrue(imageHistoryRepository.findAll().isEmpty());

        imageDataInitializer.generateImageHistory(IMAGE_INDEX, swedish, version, null, userService.getUser(1));
        imageDataInitializer.generateImageHistory(IMAGE_INDEX, swedish, version, null, userService.getUser(1));
        imageDataInitializer.generateImageHistory(IMAGE_INDEX + 1, english, version, null, userService.getUser(1));

        assertEquals(3, imageHistoryRepository.findAll().size());
    }

    @Test
    public void saveImageHistory_When_UsedLoopEntry_Expected_Saved() {
        assertTrue(imageHistoryRepository.findAll().isEmpty());

        final LoopEntryRefJPA loopEntryRefJPA = new LoopEntryRefJPA();
        loopEntryRefJPA.setLoopIndex(1);
        loopEntryRefJPA.setLoopEntryIndex(1);

        imageDataInitializer.generateImageHistory(IMAGE_INDEX, swedish, version, loopEntryRefJPA, userService.getUser(1));
        imageDataInitializer.generateImageHistory(IMAGE_INDEX + 1, english, version, null, userService.getUser(1));

        assertEquals(2, imageHistoryRepository.findAll().size());
    }

    @Test
    public void findImageHistoryInLoop_When_OneSpecifiedExists_Expect_OneImageHistoryReturned() {
        final LoopEntryRefJPA loopEntryRefJPA = new LoopEntryRefJPA();
        loopEntryRefJPA.setLoopIndex(1);
        loopEntryRefJPA.setLoopEntryIndex(1);


        final ImageHistoryJPA savedImageHistoryJPA = imageDataInitializer.generateImageHistory(
                IMAGE_INDEX, swedish, version, loopEntryRefJPA, userService.getUser(1)
        );

        final List<ImageHistoryJPA> expected = Collections.singletonList(savedImageHistoryJPA);

        final List<ImageHistoryJPA> actual = imageHistoryRepository.findImageHistoryInLoop(
                DOC_ID, savedImageHistoryJPA.getLanguage(), loopEntryRefJPA, 1
        );

        assertEquals(expected, actual);
    }

    @Test
    public void findImageHistoryNotInLoop_When_MultipleDocsAndHistoriesExist_Expect_FoundForSpecifiedDoc() {

        final ImageHistoryJPA savedImageHistoryJPAen = imageDataInitializer.generateImageHistory(
                IMAGE_INDEX, english, version, null, userService.getUser(1)
        );

        final ImageHistoryJPA savedImageHistoryJPAsv = imageDataInitializer.generateImageHistory(
                IMAGE_INDEX, swedish, version, null, userService.getUser(1)
        );

        final List<ImageHistoryJPA> expectedEn = Collections.singletonList(savedImageHistoryJPAen);
        final List<ImageHistoryJPA> expectedSv = Collections.singletonList(savedImageHistoryJPAsv);

        final List<ImageHistoryJPA> imageHistory_en = imageHistoryRepository.findImageHistoryNotInLoop(
                DOC_ID, english, IMAGE_INDEX
        );

        final List<ImageHistoryJPA> imageHistory_sv = imageHistoryRepository.findImageHistoryNotInLoop(
                DOC_ID, swedish, IMAGE_INDEX
        );

        assertEquals(expectedEn, imageHistory_en);
        assertEquals(expectedSv, imageHistory_sv);
    }

    @Test
    public void clearHistoryIfLimitExceeded_When_LoopIsNotNull_Expect_OutdatedHistoryDeleted() {
        assertTrue(imageHistoryRepository.findAll().isEmpty());

        final int historySize = 5;
        final int limit = 2;

        final LoopEntryRefJPA loopEntryRefJPA = new LoopEntryRefJPA();
        loopEntryRefJPA.setLoopIndex(1);
        loopEntryRefJPA.setLoopEntryIndex(1);

        final User user = userService.getUser(1);

        final List<ImageHistoryJPA> imageHistoryList = new ArrayList<>();
        for (int i=0; i < historySize; i++) {
            imageHistoryList.add(imageDataInitializer.generateImageHistory(IMAGE_INDEX, swedish, version,
                    loopEntryRefJPA, user));
        }

        assertEquals(historySize, imageHistoryRepository.findAll().size());

        imageHistoryRepository.clearHistoryIfLimitExceeded(version.getDocId(), IMAGE_INDEX, swedish.getId(),
                loopEntryRefJPA.getLoopIndex(), loopEntryRefJPA.getLoopEntryIndex(), limit);

        final List<ImageHistoryJPA> imageHistoryListAfterClear = imageHistoryRepository.findAll();

        assertEquals(limit, imageHistoryListAfterClear.size());
        assertTrue(imageHistoryList.stream().limit(historySize - limit).noneMatch(imageHistoryListAfterClear::contains));
        assertTrue(imageHistoryList.stream().skip(Math. abs(limit - historySize)).allMatch(imageHistoryListAfterClear::contains));
    }

    @Test
    public void clearHistoryIfLimitExceeded_When_LoopIsNull_Expect_OutdatedHistoryDeleted() {
        assertTrue(imageHistoryRepository.findAll().isEmpty());

        final int historySize = 5;
        final int limit = 2;

        final User user = userService.getUser(1);

        final List<ImageHistoryJPA> imageHistoryList = new ArrayList<>();
        for (int i = 0; i < historySize; i++) {
            imageHistoryList.add(imageDataInitializer.generateImageHistory(IMAGE_INDEX, swedish, version, null, user));
        }

        assertEquals(historySize, imageHistoryRepository.findAll().size());

        imageHistoryRepository.clearHistoryIfLimitExceeded(version.getDocId(), IMAGE_INDEX, swedish.getId(),
                null, null, limit);

        final List<ImageHistoryJPA> imageHistoryListAfterClear = imageHistoryRepository.findAll();

        assertEquals(limit, imageHistoryListAfterClear.size());
        assertTrue(imageHistoryList.stream().limit(historySize - limit).noneMatch(imageHistoryListAfterClear::contains));
        assertTrue(imageHistoryList.stream().skip(Math. abs(limit - historySize)).allMatch(imageHistoryListAfterClear::contains));
    }
}