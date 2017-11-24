package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.TemplateDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TemplateServiceTest {

    @Value("WEB-INF/templates/text")
    private File templateDirectory;

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

    @Test
    public void getAllTest() {
        assertEquals(templatesExpected, templateService.getAll());
    }

    @Test
    public void getByName() throws IOException {
        final String templateName = "testttt123";
        final File templateFile = new File(templateDirectory, templateName + ".jsp");
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
        final File templateFile = new File(templateDirectory, templateName + ".jsp");

        try {
            assertTrue(templateFile.createNewFile());

            TemplateDTO templateDTO = new TemplateDTO(null, templateName, false);
            final Optional<TemplateDTO> oTemplate = templateService.save(templateDTO);
            assertTrue(oTemplate.isPresent());

            templateDTO = oTemplate.get();
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
        assertFalse(templateService.save(templateDTO).isPresent());
    }

}
