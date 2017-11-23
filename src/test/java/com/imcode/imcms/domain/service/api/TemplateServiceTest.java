package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.TemplateDTO;
import imcode.server.document.TemplateMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateDataInitializer dataInitializer;

    private List<TemplateDTO> templatesExpected;

    @Before
    public void setUp() throws Exception {
        dataInitializer.cleanRepositories();
        templatesExpected = dataInitializer.createData(5);
    }

    @After
    public void tearDown() throws Exception {
        dataInitializer.cleanRepositories();
    }

    @Test
    public void getAllTest() {
        assertEquals(templatesExpected, templateService.getAll());
    }

    @Test
    public void getByName() throws IOException {
        final String templateName = "testttt123";
        final File templateFile = new File(TemplateMapper.getTemplateDirectory(), templateName + ".jsp");
        templateFile.createNewFile();

        try {
            final TemplateDTO templateDTO = dataInitializer.createData(templateName);
            final Optional<TemplateDTO> templateOptional = templateService.getTemplate(templateName);
            assertTrue(templateOptional.isPresent());
            final TemplateDTO templateResult = templateOptional.get();
            assertEquals(templateDTO, templateResult);

        } finally {
            assertTrue(templateFile.delete());
        }
    }

    @Test
    public void save() throws Exception {
        final String templateName = "testttt123";
        final File templateFile = new File(TemplateMapper.getTemplateDirectory(), templateName + ".jsp");

        try {
            assertTrue(templateFile.createNewFile());

            TemplateDTO templateDTO = new TemplateDTO(null, templateName, false);
            templateDTO = templateService.save(templateDTO);
            assertNotNull(templateDTO);

            final Optional<TemplateDTO> templateOptional = templateService.getTemplate(templateName);
            assertTrue(templateOptional.isPresent());
            final TemplateDTO templateResult = templateOptional.get();
            assertEquals(templateDTO.getName(), templateResult.getName());
            assertEquals(templateDTO.isHidden(), templateResult.isHidden());

        } finally {
            assertTrue(templateFile.delete());
        }
    }

    @Test
    public void save_When_NoTemplate_Expect_Null() throws Exception {
        final String dummyName = "test_" + System.currentTimeMillis();
        final TemplateDTO templateDTO = new TemplateDTO(null, dummyName, false);
        assertNull(templateService.save(templateDTO));
    }

}
