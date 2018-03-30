package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.persistence.entity.ProfileJPA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ProfileRepositoryTest {

    @Autowired
    ProfileRepository profileRepository;

    @Test
    public void findAll() {
        profileRepository.deleteAll();

        assertTrue(profileRepository.findAll().isEmpty());

        final List<ProfileJPA> profiles = Arrays.asList(
                new ProfileJPA(null, "name1", "1001"),
                new ProfileJPA(null, "name2", "alias"),
                new ProfileJPA(null, "name3", "alias2")
        );

        profileRepository.save(profiles);

        assertEquals(profileRepository.findAll().size(), profiles.size());
    }
}
