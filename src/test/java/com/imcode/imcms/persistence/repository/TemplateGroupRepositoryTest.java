package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.persistence.entity.TemplateGroupJpa;
import org.junit.Before;
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
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TemplateGroupRepositoryTest {

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Autowired
    private TemplateGroupRepository templateGroupRepository;

    @Before
    public void setUp() throws Exception {
        templateDataInitializer.cleanRepositories();
    }

    @Test
    public void findByName_When_containsTemplates_Expect_theSameId() throws Exception {
        final String name = "Top menu group";
        final TemplateGroupJpa topMenuGroup = templateDataInitializer.createData(name, 5),
                actualGroup = templateGroupRepository.findByName(name);

        assertEquals(topMenuGroup.getId(), actualGroup.getId());
        assertEquals(name, actualGroup.getName());
    }

}