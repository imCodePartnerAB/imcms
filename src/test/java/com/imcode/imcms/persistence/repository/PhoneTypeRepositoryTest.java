package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.TransactionalWebAppSpringTestConfig;
import com.imcode.imcms.persistence.entity.PhoneTypeJPA;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PhoneTypeRepositoryTest extends TransactionalWebAppSpringTestConfig {

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
