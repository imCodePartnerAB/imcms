package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.dto.TextHistoryDTO;
import com.imcode.imcms.domain.service.TextHistoryService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class TextHistoryServiceTest extends WebAppSpringTestConfig {

    private final static int DOC_ID = 1001;

    @Autowired
    private TextHistoryService textHistoryService;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private TextHistoryRepository textHistoryRepository;

    private Language language;
    private Integer index;
    private LoopEntryRef loopEntryRef;

    @BeforeAll
    public static void setUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
    }

    @BeforeEach
    public void setUp() {
        textHistoryRepository.deleteAll();
        this.language = languageDataInitializer.createData().get(0);
        this.index = 1;

        this.loopEntryRef = new LoopEntryRefDTO();
        loopEntryRef.setLoopEntryIndex(1);
        loopEntryRef.setLoopIndex(1);
    }

    @Test
    public void saveTextHistory_Expect_Saved() {
        final int expected = textHistoryRepository.findAll().size() + 1;
        final Text text = textList(1).get(0);

        textHistoryService.save(text);

        final int actual = textHistoryRepository.findAll().size();

        assertEquals(expected, actual);
    }

    @Test
    public void saveTextHistory_When_HistoryRecordsLimitExceeded_Expect_SavedAndOutdatedRecordDeleted(){
        assertTrue(textHistoryRepository.findAll().isEmpty());

        final int contentHistoryRecordsSize = Imcms.getServices().getConfig().getContentHistoryRecordsSize();

        final List<TextDTO> textDTOs = textList(contentHistoryRecordsSize + 1);
        for(int i=0; i<contentHistoryRecordsSize; i++){
            textHistoryService.save(textDTOs.get(i));
        }

        final List<TextHistoryDTO> textHistoryDTOsBeforeLimitExceeded = textHistoryService.getAll(textDTOs.get(0));
        assertEquals(contentHistoryRecordsSize, textHistoryDTOsBeforeLimitExceeded.size());

        textHistoryService.save(textDTOs.get(contentHistoryRecordsSize));

        final List<TextHistoryDTO> textHistoryDTOsAfterLimitExceeded = textHistoryService.getAll(textDTOs.get(0));
        assertEquals(contentHistoryRecordsSize, textHistoryDTOsAfterLimitExceeded.size());

        TextHistoryDTO outdatedRecord = textHistoryDTOsBeforeLimitExceeded.get(textHistoryDTOsBeforeLimitExceeded.size() - 1);
        assertFalse(textHistoryDTOsAfterLimitExceeded.contains(outdatedRecord));
    }

    @Test
    public void saveTextHistory_When_HistoryRecordsLimitExceeded_And_LoopIsNull_Expect_SavedAndOutdatedRecordDeleted(){
        assertTrue(textHistoryRepository.findAll().isEmpty());

        final int contentHistoryRecordsSize = Imcms.getServices().getConfig().getContentHistoryRecordsSize();

        final List<TextDTO> textDTOs = textList(contentHistoryRecordsSize + 1);
        textDTOs.forEach(textDTO -> textDTO.setLoopEntryRef(null));
        for(int i=0; i<contentHistoryRecordsSize; i++){
            textHistoryService.save(textDTOs.get(i));
        }

        final List<TextHistoryDTO> textHistoryDTOsBeforeLimitExceeded = textHistoryService.getAll(textDTOs.get(0));
        assertEquals(contentHistoryRecordsSize, textHistoryDTOsBeforeLimitExceeded.size());

        textHistoryService.save(textDTOs.get(contentHistoryRecordsSize));

        final List<TextHistoryDTO> textHistoryDTOsAfterLimitExceeded = textHistoryService.getAll(textDTOs.get(0));
        assertEquals(contentHistoryRecordsSize, textHistoryDTOsAfterLimitExceeded.size());

        TextHistoryDTO outdatedRecord = textHistoryDTOsBeforeLimitExceeded.get(textHistoryDTOsBeforeLimitExceeded.size() - 1);
        assertFalse(textHistoryDTOsAfterLimitExceeded.contains(outdatedRecord));
    }

    @Test
    public void findAllByLanguageAndLoopEntryRefAndNo_When_ThreeSpecifiedTextHistoriesExists_Expect_Returned() {
        final int textHistoryListSize = 3;
        final List<TextDTO> texts = textList(textHistoryListSize);

        // create another text history
        this.index++;
        texts.addAll(textList(1));
        this.index--;

        texts.forEach(textHistoryService::save);

        final List<TextHistoryDTO> actual = textHistoryService
                .getAll(texts.get(new Random().nextInt(textHistoryListSize)));

        assertEquals(textHistoryListSize, actual.size());

        actual.forEach(textHistory -> {
            assertEquals(textHistory.getIndex(), this.index);
            assertEquals(textHistory.getLangCode(), this.language.getCode());
            assertEquals(textHistory.getLoopEntryRef(), this.loopEntryRef);
        });
    }

    private List<TextDTO> textList(int number) {
        return IntStream.range(0, number)
                .mapToObj(i -> {
                    final TextDTO textDTO = new TextDTO(this.index, DOC_ID, this.language.getCode(), this.loopEntryRef);
                    textDTO.setType(Text.Type.TEXT);
                    textDTO.setText("Long text" + i);

                    return textDTO;
                })
                .collect(Collectors.toList());
    }
}