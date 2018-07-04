package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.TransactionalWebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.persistence.entity.PhoneJPA;
import com.imcode.imcms.persistence.entity.PhoneTypeJPA;
import com.imcode.imcms.persistence.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneRepositoryTest extends TransactionalWebAppSpringTestConfig {

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private PhoneTypeRepository phoneTypeRepository;

    @Autowired
    private UserDataInitializer userDataInitializer;

    private User user;

    @Before
    public void setUp() throws Exception {
        phoneRepository.deleteAll();
        user = userDataInitializer.createData("test-login");
    }

    @Test
    public void testSave() {

        assertTrue(phoneRepository.findAll().isEmpty());

        final PhoneTypeJPA phoneType = phoneTypeRepository.save(new PhoneTypeJPA(13, "test-type"));
        final PhoneJPA phone = phoneRepository.save(new PhoneJPA("85241254", user, phoneType));

        final List<PhoneJPA> all = phoneRepository.findAll();

        assertFalse(all.isEmpty());
        assertEquals(phone, all.get(0));
    }
}
