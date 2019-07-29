package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.model.TemplateGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TemplateGroupServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private TemplateDataInitializer dataInitializer;

    @Autowired
    private TemplateGroupService templateGroupService;

    @BeforeEach
    public void setUp() {
        dataInitializer.cleanRepositories();
    }

    @Test
    public void getAll_When_templateGroupsWithTemplates_Expect_theyAllPersisted() {
        int i = 1;
        final List<TemplateGroup> expected = Arrays.asList(dataInitializer.createData("test " + i++, i++, false),
                dataInitializer.createData("test " + i++, i++, false),
                dataInitializer.createData("test " + i++, i++, false),
                dataInitializer.createData("test " + i++, i++, false),
                dataInitializer.createData("test " + i++, i, false)
        );

        assertTrue(templateGroupService.getAll().containsAll(expected));
    }

    @Test
    public void save() {
        final TemplateGroup test = dataInitializer.createData("test", 5, true);

        templateGroupService.save(test);

        final TemplateGroup persisted = templateGroupService.get("test");
        persisted.setId(null);
        assertEquals(test, persisted);
    }

    @Test
    public void edit_WhenTemplateGroupExist_Expected_EditEntity() {
        final TemplateGroup test = dataInitializer.createData("test", 5, true);
        final TemplateGroup saved = templateGroupService.save(test);
        final String anotherName = "Another";
        saved.setName(anotherName);
        templateGroupService.edit(saved);

        assertNotEquals(test.getName(), saved.getName());
    }

    @Test
    public void edit_WhenTemplateGroupNameEmpty_Expected_CorrectException() {
        final TemplateGroup test = dataInitializer.createData("test", 5, true);
        final TemplateGroup saved = templateGroupService.save(test);
        saved.setName("");


        assertThrows(IllegalArgumentException.class, () -> templateGroupService.edit(saved));
    }


    @Test
    public void getByName_When_TemplateGroupNameExist_Expected_CorrectTemplateGroup() {
        final String name = "TEST";
        final TemplateGroup test = dataInitializer.createData(name, 5, false);
        final TemplateGroup persistedByName = templateGroupService.get(name);

        assertNotNull(persistedByName);
        assertEquals(test, persistedByName);
    }

    @Test
    public void getById_When_TemplateGroupIdExist_Expected_CorrectTemplateGroup() {
        final TemplateGroup expectedTemplateGroup = dataInitializer.createData("TEST", 5, false);
        final TemplateGroup actualTemplateGroup = templateGroupService.get(expectedTemplateGroup.getId());

        assertNotNull(actualTemplateGroup);
        assertEquals(expectedTemplateGroup, actualTemplateGroup);
    }
}