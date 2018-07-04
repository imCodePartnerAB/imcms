package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.TransactionalWebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.persistence.entity.PhoneJPA;
import com.imcode.imcms.persistence.entity.PhoneTypeJPA;
import com.imcode.imcms.persistence.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

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
        assertTrue(phoneRepository.findAll().isEmpty());
        user = userDataInitializer.createData("test-login");
    }

    @Test
    public void testSave() {
        final PhoneTypeJPA phoneType1 = phoneTypeRepository.save(new PhoneTypeJPA(13, "test-type1"));
        final PhoneTypeJPA phoneType2 = phoneTypeRepository.save(new PhoneTypeJPA(14, "test-type2"));

        final PhoneJPA phone1 = phoneRepository.save(new PhoneJPA("852412541", user, phoneType1));
        final PhoneJPA phone2 = phoneRepository.save(new PhoneJPA("852412542", user, phoneType2));

        final List<PhoneJPA> all = phoneRepository.findAll();

        assertFalse(all.isEmpty());
        assertTrue(Arrays.asList(phone1, phone2).containsAll(all));
    }
}
