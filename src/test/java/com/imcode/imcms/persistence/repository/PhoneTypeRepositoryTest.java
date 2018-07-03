package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.persistence.entity.PhoneTypeJPA;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class PhoneTypeRepositoryTest {

    @Autowired
    private PhoneTypeRepository phoneTypeRepository;

    @Before
    public void setUp() throws Exception {
        phoneTypeRepository.deleteAll();
        assertTrue(phoneTypeRepository.findAll().isEmpty());
    }

    @Test
    public void testSave() {
        final PhoneTypeJPA phoneType = phoneTypeRepository.save(
                new PhoneTypeJPA(1, "test-name")
        );

        final PhoneTypeJPA saved = phoneTypeRepository.findOne(phoneType.getId());

        assertEquals(phoneType, saved);
    }
}
