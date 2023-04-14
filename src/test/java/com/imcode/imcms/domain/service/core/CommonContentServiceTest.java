package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentMetadataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.util.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CommonContentServiceTest extends WebAppSpringTestConfig {

    private static final int DOC_ID = 1001;
    private static final int WORKING_VERSION_INDEX = 0;
    private static final int LATEST_VERSION_INDEX = 1;

    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

	@Autowired
	private DocumentMetadataInitializer documentMetadataInitializer;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private CommonContentRepository commonContentRepository;

    @BeforeEach
    public void setUp() {
        tearDown();
    }

    @AfterEach
    public void tearDown() {
        commonContentDataInitializer.cleanRepositories();
    }

    @Test
    public void getOrCreateCommonContent_When_Exist_Expect_CorrectDTO() {
        final List<CommonContent> commonContentDTOS = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toList());

        for (LanguageDTO languageDTO : languageDataInitializer.createData()) {
            final CommonContent commonContentDTO = commonContentService.getOrCreate(DOC_ID, WORKING_VERSION_INDEX, languageDTO);
            assertTrue(commonContentDTOS.contains(commonContentDTO));
        }
    }

    @Test
    public void getOrCreateCommonContent_When_NotExist_Expect_CreatedAndCorrectDTO() {
        final int newVersion = 100;
        versionDataInitializer.createData(newVersion, DOC_ID);
        commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,true, true);
        for (LanguageDTO languageDTO : languageDataInitializer.createData()) {
            assertNotNull(commonContentService.getOrCreate(DOC_ID, newVersion, languageDTO));
        }
    }

    @Test
    public void saveCommonContent_When_ExistBefore_Expect_Saved() {
        final List<CommonContent> contents = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toList());

        for (CommonContent content : contents) {
            content.setHeadline("test_content_headline");
        }

        commonContentService.save(DOC_ID, contents);

        final List<CommonContent> commonContents = new ArrayList<>();

        for (LanguageDTO languageDTO : languageDataInitializer.createData()) {
            commonContents.add(commonContentService.getOrCreate(DOC_ID, WORKING_VERSION_INDEX, languageDTO));
        }

        assertTrue(contents.containsAll(commonContents));
    }

    @Test
    public void saveCommonContent_When_NotExistBefore_Expect_Saved() {
        versionDataInitializer.createData(WORKING_VERSION_INDEX, DOC_ID);

        final List<CommonContent> contents = languageDataInitializer.createData()
                .stream()
                .map(languageDTO -> Value.with(new CommonContentDTO(), contentDTO -> {
                    contentDTO.setVersionNo(WORKING_VERSION_INDEX);
                    contentDTO.setEnabled(true);
                    contentDTO.setMenuText("menu_text_test");
                    contentDTO.setHeadline("test_headline");
	                contentDTO.setDocumentMetadataList(documentMetadataInitializer.createDTO(languageDTO));
                    contentDTO.setDocId(DOC_ID);
                    contentDTO.setLanguage(languageDTO);
                }))
                .collect(Collectors.toList());

        commonContentService.save(DOC_ID, contents);

        for (CommonContent commonContent : contents) {
            final Language language = commonContent.getLanguage();
            final CommonContent savedContent = commonContentService.getOrCreate(DOC_ID, WORKING_VERSION_INDEX, language);
            commonContent.setId(savedContent.getId());

            assertEquals(savedContent, commonContent);
        }
    }

    @Test
    public void setAsWorking_Expected_CopyCommonDataFromSpecificVersionToWorkingVersion(){
        final int version1 = LATEST_VERSION_INDEX;
        final int version2 = 2;

        versionDataInitializer.createData(WORKING_VERSION_INDEX, DOC_ID);
        versionDataInitializer.createData(version1, DOC_ID);
        versionDataInitializer.createData(version2, DOC_ID);

        final String aliasText = "aliasText";
        final String menuText = "menuText";
        final String headline = "headlineText";

        final List<LanguageDTO> data = languageDataInitializer.createData();

        data.forEach(languageDTO -> {
            CommonContentJPA content = new CommonContentJPA();
            content.setVersionNo(WORKING_VERSION_INDEX);
            content.setEnabled(true);
            content.setAlias(aliasText);
            content.setMenuText(menuText);
            content.setHeadline(headline);
            content.setDocumentMetadataList(documentMetadataInitializer.createDTO(languageDTO));
            content.setDocId(DOC_ID);
            content.setLanguage(languageDTO);

            CommonContentJPA contentVersion1 = new CommonContentJPA(content);
            contentVersion1.setVersionNo(version1);
            contentVersion1.setAlias(aliasText + version1);
            contentVersion1.setMenuText(menuText + version1);
            contentVersion1.setHeadline(headline + version1);

            CommonContentJPA contentVersion2 = new CommonContentJPA(content);
            contentVersion2.setVersionNo(version2);
            contentVersion2.setAlias(aliasText + version2);
            contentVersion2.setMenuText(menuText + version2);
            contentVersion2.setHeadline(headline + version2);

            commonContentRepository.saveAll(List.of(content, contentVersion1, contentVersion2));
        });

        final List<CommonContentJPA> commonContentDTOWorkingVersion = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, WORKING_VERSION_INDEX);
        final List<CommonContentJPA> commonContentDTOVersion1 = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, version1);
        final List<CommonContentJPA> commonContentDTOVersion2 = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, version2);

        commonContentService.setAsWorkingVersion(versionRepository.findByDocIdAndNo(DOC_ID, version1));

        final List<CommonContentJPA> commonContentDTOWorkingVersionAfterReset = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, WORKING_VERSION_INDEX);
        final List<CommonContentJPA> commonContentDTOVersion1AfterReset = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, version1);
        final List<CommonContentJPA> commonContentDTOVersion2AfterReset = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, version2);

        assertFalse(equalsIgnoreIdAndVersion(commonContentDTOWorkingVersion, commonContentDTOWorkingVersionAfterReset));
        assertTrue(equalsIgnoreIdAndVersion(commonContentDTOVersion1, commonContentDTOVersion1AfterReset));
        assertTrue(equalsIgnoreIdAndVersion(commonContentDTOVersion2, commonContentDTOVersion2AfterReset));
        assertTrue(equalsIgnoreIdAndVersion(commonContentDTOVersion1, commonContentDTOWorkingVersionAfterReset));
    }

    @Test
    public void setAsWorking_When_noCommonDataWithSpecificVersion_Expected_WorkingVersionHasNoCommonData(){
        final int version1 = LATEST_VERSION_INDEX;
        final int version2 = 2;

        versionDataInitializer.createData(WORKING_VERSION_INDEX, DOC_ID);
        versionDataInitializer.createData(version1, DOC_ID);
        versionDataInitializer.createData(version2, DOC_ID);

        final String aliasText = "aliasText";
        final String menuText = "menuText";
        final String headline = "headlineText";

        final List<LanguageDTO> data = languageDataInitializer.createData();

        data.forEach(languageDTO -> {
            CommonContentJPA content = new CommonContentJPA();
            content.setVersionNo(WORKING_VERSION_INDEX);
            content.setEnabled(true);
            content.setAlias(aliasText);
            content.setMenuText(menuText);
            content.setHeadline(headline);
            content.setDocumentMetadataList(documentMetadataInitializer.createDTO(languageDTO));
            content.setDocId(DOC_ID);
            content.setLanguage(languageDTO);

            CommonContentJPA contentVersion2 = new CommonContentJPA(content);
            contentVersion2.setVersionNo(version2);
            contentVersion2.setAlias(aliasText + version2);
            contentVersion2.setMenuText(menuText + version2);
            contentVersion2.setHeadline(headline + version2);

            commonContentRepository.saveAll(List.of(content, contentVersion2));
        });

        final List<CommonContentJPA> commonContentDTOWorkingVersion = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, WORKING_VERSION_INDEX);
        assertFalse(commonContentDTOWorkingVersion.isEmpty());

        commonContentService.setAsWorkingVersion(versionRepository.findByDocIdAndNo(DOC_ID, version1));

        final List<CommonContentJPA> commonContentDTOWorkingVersionAfterReset = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, WORKING_VERSION_INDEX);
        assertTrue(commonContentDTOWorkingVersionAfterReset.isEmpty());
    }

    private boolean equalsIgnoreIdAndVersion(List<CommonContentJPA> a, List<CommonContentJPA> b) {
        List<CommonContentDTO> aDTO = a.stream()
                .map(contentJPA -> {
                    CommonContentDTO content = new CommonContentDTO(contentJPA);
                    content.setId(null);
                    content.setVersionNo(null);
                    return content;
                }).collect(Collectors.toList());

        List<CommonContentDTO> bDTO = b.stream()
                .map(contentJPA -> {
                    CommonContentDTO content = new CommonContentDTO(contentJPA);
                    content.setId(null);
                    content.setVersionNo(null);
                    return content;
                }).collect(Collectors.toList());

        return aDTO.equals(bDTO);
    }

    @Test
    public void deleteByDocId() {
        final Version version = new Version();
        version.setDocId(DOC_ID);
        version.setNo(WORKING_VERSION_INDEX);

        commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true);
        assertFalse(commonContentRepository.findByVersion(version).isEmpty());

        commonContentService.deleteByDocId(DOC_ID);
        assertTrue(commonContentRepository.findByVersion(version).isEmpty());
    }

    @Test
    public void deleteByDocId_When_CommonContentHasMetadata_Expect_DeleteCascadeMetadata(){
        final Version workingVersion = versionDataInitializer.createData(WORKING_VERSION_INDEX, DOC_ID);

        final List<LanguageDTO> data = languageDataInitializer.createData();

        data.forEach(languageDTO -> {
            CommonContentJPA content = new CommonContentJPA();
            content.setVersionNo(WORKING_VERSION_INDEX);
            content.setEnabled(true);
            content.setAlias("alias" + languageDTO.getCode());
            content.setMenuText("menuText" + languageDTO.getCode());
            content.setHeadline("headline" + languageDTO.getCode());
            content.setDocumentMetadataList(documentMetadataInitializer.createDTO(languageDTO));
            content.setDocId(DOC_ID);
            content.setLanguage(languageDTO);

            commonContentRepository.save(content);
        });

        assertFalse(commonContentRepository.findByVersion(workingVersion).isEmpty());

        commonContentService.deleteByDocId(DOC_ID);
        assertTrue(commonContentRepository.findByVersion(workingVersion).isEmpty());
    }

    @Test
    public void getByVersion() {
        final Set<CommonContentDTO> expected = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());

        final Version workingVersion = versionRepository.findByDocIdAndNo(DOC_ID, WORKING_VERSION_INDEX);

        final Set<CommonContent> actual = commonContentService.getByVersion(workingVersion);

        assertEquals(expected.size(), actual.size());

        assertEquals(expected, actual);
    }

    @Test
    public void createVersionedContent() {

        final Set<CommonContentDTO> expected = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true)
                .stream()
                .map(CommonContentDTO::new)
                .peek(commonContentDTO -> {
                    commonContentDTO.setId(null);
                    commonContentDTO.setVersionNo(LATEST_VERSION_INDEX);
                })
                .collect(Collectors.toSet());

        final Version workingVersion = versionRepository.findByDocIdAndNo(DOC_ID, WORKING_VERSION_INDEX),
                latestVersion = versionDataInitializer.createData(LATEST_VERSION_INDEX, DOC_ID);

        commonContentService.createVersionedContent(workingVersion, latestVersion);

        final Set<CommonContent> actual = commonContentService.getByVersion(latestVersion)
                .stream()
                .peek(commonContentDTO -> commonContentDTO.setId(null))
                .collect(Collectors.toSet());

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }
}
