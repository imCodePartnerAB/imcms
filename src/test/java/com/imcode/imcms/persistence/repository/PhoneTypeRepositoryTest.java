package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.persistence.entity.PhoneTypeJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
class PhoneTypeRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private PhoneTypeRepository phoneTypeRepository;

    @BeforeEach
    public void setUp() {
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
