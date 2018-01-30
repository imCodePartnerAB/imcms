package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.TextHistoryService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class DefaultTextHistoryServiceTest {

    @Autowired
    private TextHistoryService textHistoryService;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private TextHistoryRepository textHistoryRepository;

    private Text text;

    @BeforeClass
    public static void setUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user);
    }

    @Before
    public void setUp() {
        final Language en = languageDataInitializer.createData().get(0);
        final int index = 1;

        final LoopEntryRefDTO loopEntryRef = new LoopEntryRefDTO();
        loopEntryRef.setLoopEntryIndex(1);
        loopEntryRef.setLoopIndex(1);

        final TextDTO textDTO = new TextDTO(index, null, en.getCode(), loopEntryRef);
        textDTO.setType(Text.Type.PLAIN_TEXT);
        textDTO.setText("Long text");

        this.text = textDTO;
    }

    @Test
    public void saveTextHistory_Expect_Saved() {
        final int expected = textHistoryRepository.findAll().size() + 1;

        textHistoryService.save(this.text);

        final int actual = textHistoryRepository.findAll().size();

        assertEquals(expected, actual);
    }
}