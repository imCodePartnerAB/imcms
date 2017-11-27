package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.persistence.entity.RoleJPA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void save() throws Exception {
        final RoleJPA role = new RoleJPA();
        role.setName("test_name");

        roleRepository.save(role);

        assertNotNull(role.getId());
    }

    @Test
    public void saveCollection() throws Exception {
        final int howMuch = 4;
        final List<RoleJPA> forSave = new ArrayList<>();

        for (int i = 0; i < howMuch; i++) {
            final RoleJPA role = new RoleJPA();
            role.setName("test_name" + i);
            forSave.add(role);
        }

        final List<RoleJPA> saved = roleRepository.save(forSave);

        assertNotNull(saved);
        assertEquals(saved.size(), forSave.size());
    }

    @Test
    public void findOne() throws Exception {
        final RoleJPA role = new RoleJPA();
        role.setName("test_name");
        roleRepository.save(role);

        assertNotNull(roleRepository.findOne(role.getId()));
    }

    @Test
    public void findAll() throws Exception {
        final int prev = roleRepository.findAll().size();
        final int howMuch = 4;

        for (int i = 0; i < howMuch; i++) {
            final RoleJPA role = new RoleJPA();
            role.setName("test_name" + i);
            roleRepository.save(role);
        }

        final List<RoleJPA> all = roleRepository.findAll();

        assertEquals(all.size(), howMuch + prev);
    }

}
