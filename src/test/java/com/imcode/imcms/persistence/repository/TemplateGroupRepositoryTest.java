package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.model.TemplateGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
public class TemplateGroupRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Autowired
    private TemplateGroupRepository templateGroupRepository;

    @BeforeEach
    public void setUp() {
        templateDataInitializer.cleanRepositories();
    }

    @Test
    public void findByName_When_containsTemplates_Expect_theSameId() {
        final String name = "Top menu group";
        final TemplateGroup topMenuGroup = templateDataInitializer.createData(name, 5, false);
        final TemplateGroup actualGroup = templateGroupRepository.findByName(name);

        assertEquals(topMenuGroup.getId(), actualGroup.getId());
        assertEquals(name, actualGroup.getName());
    }

}