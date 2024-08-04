package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.*;
import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.util.Value;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static imcode.server.ImcmsConstants.ENG_CODE;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CommonContentServiceTest extends WebAppSpringTestConfig {

    private static final int DOC_ID = 1001;
    private static final int WORKING_VERSION_INDEX = 0;
    private static final int LATEST_VERSION_INDEX = 1;

    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

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

    final TriFunction<String, String, Integer, String> generateAlias = (alias, langCode, versionNo) -> alias + "_" + langCode + "_" + versionNo;

    @BeforeEach
    public void setUp() {
        tearDown();

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
    }

    @AfterEach
    public void tearDown() {
        commonContentDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
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
    public void getOrCreateCommonContentMultiple_When_CommonContentsExist_Expect_MapDocIdAndCommonContentDTOs() {
        final Map<Integer, List<CommonContent>> createdCommonContents =
                documentDataInitializer.createDocumentsData(3, true, true).stream()
                        .collect(Collectors.toMap(DocumentDTO::getId, DocumentDTO::getCommonContents));

        final Set<Integer> docIds = createdCommonContents.keySet();
        final Map<Integer, List<CommonContent>> receivedCommonContents =
                commonContentService.getOrCreateCommonContents(docIds);

        assertEquals(createdCommonContents, receivedCommonContents);
    }

    @Test
    public void getOrCreateCommonContentMultiple_When_OneDocumentHasPublishedVersion_Expect_MapDocIdAndLatestVersionCommonContents() {
        final List<DocumentDTO> documentDTOs = documentDataInitializer.createDocumentsData(3, true, true);

        DocumentDTO publishedDocumentDTO = documentDTOs.get(0);
        versionDataInitializer.createData(LATEST_VERSION_INDEX, publishedDocumentDTO.getId());
        final List<CommonContentJPA> publishedCommonContents = publishedDocumentDTO.getCommonContents().stream()
                .map(commonContent -> {
                    final Language language = commonContent.getLanguage();
                    String alias = generateAlias.apply("alias", language.getCode(), LATEST_VERSION_INDEX);
                    String headline = "headline_" + language.getCode() + "_" + LATEST_VERSION_INDEX;
                    String menuText = "menuText_" + language.getCode() + "_" + LATEST_VERSION_INDEX;

                    return new CommonContentJPA(
                            commonContent.getDocId(), alias, new LanguageJPA(language), headline, menuText, true, LATEST_VERSION_INDEX);
                })
                .toList();
        final List<CommonContentJPA> savedPublishedCommonContents = commonContentRepository.saveAll(publishedCommonContents);

        final Map<Integer, List<CommonContent>> expectedCommonContents = documentDTOs.stream()
                .collect(Collectors.toMap(DocumentDTO::getId, DocumentDTO::getCommonContents));
        expectedCommonContents.put(publishedDocumentDTO.getId(), savedPublishedCommonContents
                .stream().map(commonContentJPA -> (CommonContent) new CommonContentDTO(commonContentJPA)).toList());

        final Set<Integer> docIds = expectedCommonContents.keySet();
        final Map<Integer, List<CommonContent>> receivedCommonContents =
                commonContentService.getOrCreateCommonContents(docIds);

        assertEquals(expectedCommonContents, receivedCommonContents);
    }

    @Test
    public void getOrCreateCommonContentMultiple_When_OneDocumentDoesNotHaveCommonContents_Expect_CreatedMissingCommonContents() {
        final DocumentDTO document = documentDataInitializer.createData();

        final DocumentDTO documentWithoutCommonContent = documentDataInitializer.createData();
        commonContentRepository.deleteByDocId(documentWithoutCommonContent.getId());

        final List<LanguageDTO> availableLangs = languageDataInitializer.createData();
        final List<Integer> docIds = List.of(document.getId(), documentWithoutCommonContent.getId());

        final Map<Integer, List<CommonContent>> receivedCommonContents =
                commonContentService.getOrCreateCommonContents(docIds);

        assertEquals(docIds.size(), receivedCommonContents.size());
        boolean hasCommonContentsForAllAvailableLangs = receivedCommonContents.entrySet().stream()
                .allMatch(commonContent -> commonContent.getValue().size() == availableLangs.size());
        assertTrue(hasCommonContentsForAllAvailableLangs);
    }

    @Test
    public void getOrCreateCommonContentMultiple_When_OneDocumentDoesNotExist_Expect_MapWithoutNonexistentDoc() {
        final int numberOfExistingDocument = 3;

        final Map<Integer, List<CommonContent>> createdCommonContents =
                documentDataInitializer.createDocumentsData(numberOfExistingDocument, true, true).stream()
                        .collect(Collectors.toMap(DocumentDTO::getId, DocumentDTO::getCommonContents));
        final int nonexistentDocId = 1000;

        final Set<Integer> docIds = new HashSet<>(createdCommonContents.keySet());
        docIds.add(nonexistentDocId);
        final Map<Integer, List<CommonContent>> receivedCommonContents =
                commonContentService.getOrCreateCommonContents(docIds);

        assertEquals(numberOfExistingDocument, receivedCommonContents.size());
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
    public void saveCommonContent_When_CommonContentsHaveDuplicateAlias_Expect_SavedWithEmptyAliases() {
        versionDataInitializer.createData(WORKING_VERSION_INDEX, DOC_ID);

        final String duplicateAlias = "duplicateAlias";

        final List<CommonContent> commonContentList = languageDataInitializer.createData()
                .stream()
                .map(languageDTO -> Value.with(new CommonContentDTO(), contentDTO -> {
                    contentDTO.setVersionNo(WORKING_VERSION_INDEX);
                    contentDTO.setDocId(DOC_ID);
                    contentDTO.setLanguage(languageDTO);
                    contentDTO.setAlias(duplicateAlias);
                }))
                .collect(Collectors.toList());

        commonContentService.save(DOC_ID, commonContentList);

        for (CommonContent commonContent : commonContentList) {
            final CommonContent savedCommonContent = commonContentService.getOrCreate(
                    DOC_ID, WORKING_VERSION_INDEX, commonContent.getLanguage()
            );
            assertTrue(savedCommonContent.getAlias().isEmpty());
        }
    }

    @Test
    public void saveCommonContent_When_CommonContentWithSameAliasAlreadyExists_Expect_SavedWithEmptyAlias() {
        final String duplicateAlias = "duplicateAlias";
        final LanguageDTO languageDTO = languageDataInitializer.createData().get(0);

        final Integer newDocId = documentDataInitializer.createData().getId();
        final CommonContent commonContentNewDoc = commonContentService.getOrCreate(newDocId, WORKING_VERSION_INDEX, languageDTO);
        commonContentNewDoc.setAlias(duplicateAlias);
        commonContentService.save(newDocId, List.of(commonContentNewDoc));

        final CommonContent savedCommonContentNewDoc = commonContentService.getOrCreate(newDocId, WORKING_VERSION_INDEX, languageDTO);
        assertEquals(duplicateAlias, savedCommonContentNewDoc.getAlias());

        versionDataInitializer.createData(WORKING_VERSION_INDEX, DOC_ID);
        final CommonContentDTO commonContentWithSameAlias = Value.with(new CommonContentDTO(), commonContentDTO -> {
            commonContentDTO.setVersionNo(WORKING_VERSION_INDEX);
            commonContentDTO.setDocId(DOC_ID);
            commonContentDTO.setLanguage(languageDTO);
            commonContentDTO.setAlias(duplicateAlias);
        });
        commonContentService.save(DOC_ID, List.of(commonContentWithSameAlias));

        final CommonContent savedContentWithSameAlias = commonContentService.getOrCreate(DOC_ID, WORKING_VERSION_INDEX, languageDTO);
        assertTrue(savedContentWithSameAlias.getAlias().isEmpty());
    }

    @Test
    public void setAsWorking_Expected_CopyCommonDataFromSpecificVersionToWorkingVersion() {
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
            content.setAlias(generateAlias.apply(aliasText, languageDTO.getCode(), WORKING_VERSION_INDEX));
            content.setMenuText(menuText);
            content.setHeadline(headline);
            content.setDocumentMetadataList(documentMetadataInitializer.createDTO(languageDTO));
            content.setDocId(DOC_ID);
            content.setLanguage(languageDTO);

            CommonContentJPA contentVersion1 = new CommonContentJPA(content);
            contentVersion1.setVersionNo(version1);
            contentVersion1.setAlias(generateAlias.apply(aliasText, languageDTO.getCode(), version1));
            contentVersion1.setMenuText(menuText + version1);
            contentVersion1.setHeadline(headline + version1);

            CommonContentJPA contentVersion2 = new CommonContentJPA(content);
            contentVersion2.setVersionNo(version2);
            contentVersion2.setAlias(generateAlias.apply(aliasText, languageDTO.getCode(), version2));
            contentVersion2.setMenuText(menuText + version2);
            contentVersion2.setHeadline(headline + version2);

            commonContentRepository.saveAll(List.of(content, contentVersion1, contentVersion2));
        });

        final List<CommonContentJPA> commonContentDTOWorkingVersion = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, WORKING_VERSION_INDEX);
        final List<CommonContentJPA> commonContentDTOVersion1 = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, version1);
        final List<CommonContentJPA> commonContentDTOVersion2 = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, version2);

        commonContentRepository.flush();
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
            content.setAlias(generateAlias.apply(aliasText, languageDTO.getCode(), WORKING_VERSION_INDEX));
            content.setMenuText(menuText);
            content.setHeadline(headline);
            content.setDocumentMetadataList(documentMetadataInitializer.createDTO(languageDTO));
            content.setDocId(DOC_ID);
            content.setLanguage(languageDTO);

            CommonContentJPA contentVersion2 = new CommonContentJPA(content);
            contentVersion2.setVersionNo(version2);
            contentVersion2.setAlias(generateAlias.apply(aliasText, languageDTO.getCode(), version2));
            contentVersion2.setMenuText(menuText + version2);
            contentVersion2.setHeadline(headline + version2);

            commonContentRepository.saveAll(List.of(content, contentVersion2));
        });

        final List<CommonContentJPA> commonContentDTOWorkingVersion = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, WORKING_VERSION_INDEX);
        assertFalse(commonContentDTOWorkingVersion.isEmpty());

        commonContentRepository.flush();
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
                })
                .sorted(Comparator.comparing(o -> o.getLanguage().getCode()))
                .toList();

        List<CommonContentDTO> bDTO = b.stream()
                .map(contentJPA -> {
                    CommonContentDTO content = new CommonContentDTO(contentJPA);
                    content.setId(null);
                    content.setVersionNo(null);
                    return content;
                })
                .sorted(Comparator.comparing(o -> o.getLanguage().getCode()))
                .toList();

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
            content.setAlias(generateAlias.apply("alias", languageDTO.getCode(), WORKING_VERSION_INDEX));
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

    @Test
    public void getByVersionAndLanguage_When_PassWorkingVersion_Expect_WorkingCommonContent() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);

        final Version versionWorking = versionDataInitializer.createData(WORKING_VERSION_INDEX, DOC_ID);
        final CommonContentDTO expectedCC = ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode()))
                .findAny().map(CommonContentDTO::new).get();

        assertEquals(expectedCC, commonContentService.getByVersionAndLanguage(versionWorking, expectedCC.getLanguage()).get());
    }

    @Test
    public void getByVersionAndLanguage_When_PassLatestVersion_Expect_LatestCommonContent() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);

        final Version version1 = versionDataInitializer.createData(1, DOC_ID);
        final CommonContentDTO expectedCC = ccVersion1.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode()))
                .findAny().map(CommonContentDTO::new).get();

        assertEquals(expectedCC, commonContentService.getByVersionAndLanguage(version1, expectedCC.getLanguage()).get());
    }

    @Test
    public void getByAlias_Expect_WorkingAndLatestCommonContent() {
        final Set<CommonContentDTO> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());

        final Version workingVersion = versionRepository.findByDocIdAndNo(DOC_ID, WORKING_VERSION_INDEX);
        final Version version1 = versionDataInitializer.createData(1, DOC_ID);
        final Version version2 = versionDataInitializer.createData(2, DOC_ID);

        commonContentService.createVersionedContent(workingVersion, version1);
        commonContentService.createVersionedContent(workingVersion, version2);

        final Set<CommonContentDTO> ccVersion2 = commonContentRepository.findByVersion(version2).stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());

        final String aliasEng = ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();

        final Set<CommonContentDTO> expectedEngCC = new HashSet<>();
        expectedEngCC.add(ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get());
        expectedEngCC.add(ccVersion2.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get());

        Set<CommonContent> resultCC = new HashSet<>(commonContentService.getByAlias(aliasEng));
        assertEquals(expectedEngCC, resultCC);
    }

    @Test
    public void getByAlias_When_PassRandomAlias_Expect_EmptyResult() {
        final Set<CommonContentDTO> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());

        final Version workingVersion = versionRepository.findByDocIdAndNo(DOC_ID, WORKING_VERSION_INDEX);
        final Version version1 = versionDataInitializer.createData(1, DOC_ID);
        final Version version2 = versionDataInitializer.createData(2, DOC_ID);

        commonContentService.createVersionedContent(workingVersion, version1);
        commonContentService.createVersionedContent(workingVersion, version2);

        String randomAlias = "random-alias";

        List<CommonContent> resultCC = commonContentService.getByAlias(randomAlias);
        assertTrue(resultCC.isEmpty());
    }

    @Test
    public void getPublicByAlias_When_PassPublicAlias_Expect_PublicCommonContent() {
        final Set<CommonContentDTO> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());

        final Version workingVersion = versionRepository.findByDocIdAndNo(DOC_ID, WORKING_VERSION_INDEX);
        final Version version1 = versionDataInitializer.createData(1, DOC_ID);
        final Version version2 = versionDataInitializer.createData(2, DOC_ID);

        commonContentService.createVersionedContent(workingVersion, version1);
        commonContentService.createVersionedContent(workingVersion, version2);

        final Set<CommonContentDTO> ccVersion2 = commonContentRepository.findByVersion(version2).stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());

        final String aliasEng = ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();

        CommonContentDTO expectedEngCC = ccVersion2.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get();

        CommonContent resultCC = commonContentService.getPublicByAlias(aliasEng).get();
        assertEquals(expectedEngCC, resultCC);
    }

    @Test
    public void getPublicByAlias_When_ThereIsNoPublicVersion_Expect_EmptyResult() {
        final Set<CommonContentDTO> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX, true, true)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toSet());

        final String aliasEng = ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();

        Optional<CommonContent> resultCC = commonContentService.getPublicByAlias(aliasEng);
        assertTrue(resultCC.isEmpty());
    }

    @Test
    public void existsByAlias_When_PassWorkingAlias_Expect_True() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);
        final List<CommonContent> ccVersion2 = commonContentDataInitializer.createData(DOC_ID, 2,
                true, true);

        final String aliasEngWorkingVersion = ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        assertTrue(commonContentService.existsByAlias(aliasEngWorkingVersion));
    }

    @Test
    public void existsByAlias_When_PassLatestAlias_Expect_True() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);
        final List<CommonContent> ccVersion2 = commonContentDataInitializer.createData(DOC_ID, 2,
                true, true);

        final String aliasEngLatestVersion = ccVersion2.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        assertTrue(commonContentService.existsByAlias(aliasEngLatestVersion));
    }

    @Test
    public void existsByAlias_When_PassNotWorkingLatestAlias_Expect_False() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);
        final List<CommonContent> ccVersion2 = commonContentDataInitializer.createData(DOC_ID, 2,
                true, true);

        final String aliasEngNotLatestVersion = ccVersion1.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        assertFalse(commonContentService.existsByAlias(aliasEngNotLatestVersion));
    }

    @Test
    public void existsPublicByAlias_When_PassLatestAlias_Expect_True() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);
        final List<CommonContent> ccVersion2 = commonContentDataInitializer.createData(DOC_ID, 2,
                true, true);

        final String aliasEngLatestVersion = ccVersion2.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        assertTrue(commonContentService.existsPublicByAlias(aliasEngLatestVersion));
    }

    @Test
    public void existsPublicByAlias_When_PassWorkingAlias_Expect_False() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);
        final List<CommonContent> ccVersion2 = commonContentDataInitializer.createData(DOC_ID, 2,
                true, true);

        final String aliasEngWorkingVersion = ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        assertFalse(commonContentService.existsPublicByAlias(aliasEngWorkingVersion));
    }

    @Test
    public void getDocIdByPublicAlias_Expect_DocIdLatestVersion() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);
        final List<CommonContent> ccVersion2 = commonContentDataInitializer.createData(DOC_ID, 2,
                true, true);

        final String aliasEngWorkingVersion = ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        final String aliasEngNotLatestVersion = ccVersion1.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        final String aliasEngLatestVersion = ccVersion2.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();

        assertTrue(commonContentService.getDocIdByPublicAlias(aliasEngWorkingVersion).isEmpty());
        assertTrue(commonContentService.getDocIdByPublicAlias(aliasEngNotLatestVersion).isEmpty());
        assertEquals(DOC_ID, commonContentService.getDocIdByPublicAlias(aliasEngLatestVersion).get());
    }

    @Test
    public void getAllAliases_Expect_ListOfWorkingAndLatestAliases() {
        final List<CommonContent> ccWorking = commonContentDataInitializer.createData(DOC_ID, WORKING_VERSION_INDEX,
                true, true);
        final List<CommonContent> ccVersion1 = commonContentDataInitializer.createData(DOC_ID, 1,
                true, true);
        final List<CommonContent> ccVersion2 = commonContentDataInitializer.createData(DOC_ID, 2,
                true, true);

        final String aliasEngWorkingVersion = ccWorking.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        final String aliasEngNotLatestVersion = ccVersion1.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();
        final String aliasEngLatestVersion = ccVersion2.stream().filter(cc -> ENG_CODE.equals(cc.getLanguage().getCode())).findAny().get().getAlias();

        final List<String> allAliases = commonContentService.getAllAliases();

        assertTrue(allAliases.contains(aliasEngWorkingVersion));
        assertFalse(allAliases.contains(aliasEngNotLatestVersion));
        assertTrue(allAliases.contains(aliasEngLatestVersion));
    }

}
