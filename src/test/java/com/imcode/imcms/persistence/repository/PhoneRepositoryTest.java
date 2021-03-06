package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.persistence.entity.PhoneJPA;
import com.imcode.imcms.persistence.entity.PhoneTypeJPA;
import com.imcode.imcms.persistence.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class PhoneRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private PhoneTypeRepository phoneTypeRepository;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @BeforeEach
    public void setUp() {
        phoneRepository.deleteAll();
        phoneTypeRepository.deleteAll();
        assertTrue(phoneRepository.findAll().isEmpty());
        assertTrue(phoneTypeRepository.findAll().isEmpty());
    }

    @Test
    public void testSave() {
        final User user = userDataInitializer.createData("test-login");

        final PhoneTypeJPA phoneType1 = phoneTypeRepository.save(new PhoneTypeJPA(13, "test-type1"));
        final PhoneTypeJPA phoneType2 = phoneTypeRepository.save(new PhoneTypeJPA(14, "test-type2"));

        final PhoneJPA phone1 = phoneRepository.save(new PhoneJPA("852412541", user, phoneType1));
        final PhoneJPA phone2 = phoneRepository.save(new PhoneJPA("852412542", user, phoneType2));

        final List<PhoneJPA> all = phoneRepository.findAll();

        assertFalse(all.isEmpty());
        assertTrue(Arrays.asList(phone1, phone2).containsAll(all));
    }

    @Test
    public void deleteByUserName_Expect_Deleted() {
        final User user1 = userDataInitializer.createData("test-login-1");
        final User user2 = userDataInitializer.createData("test-login-2");

        final PhoneTypeJPA phoneType1 = phoneTypeRepository.save(new PhoneTypeJPA(13, "test-type-1"));
        final PhoneTypeJPA phoneType2 = phoneTypeRepository.save(new PhoneTypeJPA(14, "test-type-2"));

        final PhoneJPA phone11 = phoneRepository.save(new PhoneJPA("852412541", user1, phoneType1));
        final PhoneJPA phone12 = phoneRepository.save(new PhoneJPA("852412542", user1, phoneType2));

        final PhoneJPA phone21 = phoneRepository.save(new PhoneJPA("852412541", user2, phoneType1));
        final PhoneJPA phone22 = phoneRepository.save(new PhoneJPA("852412542", user2, phoneType2));

        final List<PhoneJPA> all1 = phoneRepository.findAll();

        assertFalse(all1.isEmpty());
        assertTrue(Arrays.asList(phone11, phone12, phone21, phone22).containsAll(all1));

        // deleting one user's phones

        phoneRepository.deleteByUserId(user1.getId());

        final List<PhoneJPA> all2 = phoneRepository.findAll();

        assertFalse(all2.isEmpty());
        assertTrue(Arrays.asList(phone21, phone22).containsAll(all2));

        // deleting another user's phones

        phoneRepository.deleteByUserId(user2.getId());

        final List<PhoneJPA> all3 = phoneRepository.findAll();

        assertTrue(all3.isEmpty());
    }

    @Test
    public void findByUserId() {
        final User user1 = userDataInitializer.createData("test-login-1");
        final User user2 = userDataInitializer.createData("test-login-2");

        final PhoneTypeJPA phoneType1 = phoneTypeRepository.save(new PhoneTypeJPA(13, "test-type-1"));
        final PhoneTypeJPA phoneType2 = phoneTypeRepository.save(new PhoneTypeJPA(14, "test-type-2"));

        final PhoneJPA phone11 = phoneRepository.save(new PhoneJPA("852412541", user1, phoneType1));
        final PhoneJPA phone12 = phoneRepository.save(new PhoneJPA("852412542", user1, phoneType2));

        final PhoneJPA phone21 = phoneRepository.save(new PhoneJPA("852412541", user2, phoneType1));
        final PhoneJPA phone22 = phoneRepository.save(new PhoneJPA("852412542", user2, phoneType2));

        final List<PhoneJPA> all1 = phoneRepository.findAll();

        assertFalse(all1.isEmpty());
        assertTrue(Arrays.asList(phone11, phone12, phone21, phone22).containsAll(all1));

        final List<PhoneJPA> phones1 = phoneRepository.findByUserId(user1.getId());

        assertFalse(phones1.isEmpty());
        assertTrue(Arrays.asList(phone11, phone12).containsAll(phones1));

        final List<PhoneJPA> phones2 = phoneRepository.findByUserId(user2.getId());

        assertFalse(phones2.isEmpty());
        assertTrue(Arrays.asList(phone21, phone22).containsAll(phones2));
    }
}
